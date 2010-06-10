/**
 * SqlJetFile.java
 * Copyright (C) 2008 TMate Software Ltd
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For information on how to redistribute this software under
 * the terms of a license other than GNU General Public License
 * contact TMate Software at support@sqljet.com
 */
package org.tmatesoft.sqljet.core.internal.fs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetIOErrorCode;
import org.tmatesoft.sqljet.core.SqlJetIOException;
import org.tmatesoft.sqljet.core.SqlJetLogDefinitions;
import org.tmatesoft.sqljet.core.internal.ISqlJetFile;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.SqlJetDeviceCharacteristics;
import org.tmatesoft.sqljet.core.internal.SqlJetFileOpenPermission;
import org.tmatesoft.sqljet.core.internal.SqlJetFileType;
import org.tmatesoft.sqljet.core.internal.SqlJetLockType;
import org.tmatesoft.sqljet.core.internal.SqlJetSyncFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.fs.util.SqlJetFileUtil;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetFile implements ISqlJetFile {

    private static final boolean SQLJET_LOG_FILES = SqlJetUtility.getBoolSysProp(SqlJetLogDefinitions.SQLJET_LOG_FILES,
            false);

    private static final boolean SQLJET_LOG_FILES_PERFORMANCE = SqlJetUtility.getBoolSysProp(
            SqlJetLogDefinitions.SQLJET_LOG_FILES_PERFORMANCE, false);

    private static Logger filesLogger = Logger.getLogger(SqlJetLogDefinitions.SQLJET_LOG_FILES);

    private static void OSTRACE(String format, Object... args) {
        if (SQLJET_LOG_FILES) {
            SqlJetUtility.log(filesLogger, format, args);
        }
    }

    public static final int SQLJET_DEFAULT_SECTOR_SIZE = 512;

    private long timer_start = 0;
    private long timer_elapsed = 0;

    /**
     * @return
     */
    private long TIMER_ELAPSED() {
        return timer_elapsed;
    }

    /**
     * 
     */
    private void TIMER_END() {
        if (SQLJET_LOG_FILES_PERFORMANCE)
            timer_elapsed = System.nanoTime() - timer_start;
    }

    /**
     * 
     */
    private void TIMER_START() {
        if (SQLJET_LOG_FILES_PERFORMANCE)
            timer_start = System.nanoTime();
    }

    private static String pid = ManagementFactory.getRuntimeMXBean().getName();

    /**
     * @return
     */
    private String getpid() {
        return pid;
    }

    /**
     * @param lockType2
     * @return
     */
    private String locktypeName(SqlJetLockType lockType) {
        return lockType != null ? lockType.name() : null;
    }

    /**
     * An instance of the following structure is allocated for each open inode
     * on each thread with a different process ID. A single inode can have
     * multiple file descriptors, so each unixFile structure contains a pointer
     * to an instance of this object and this object keeps a count of the number
     * of unixFile pointing to it.
     */
    private static class LockInfo {
        private SqlJetLockType lockType = SqlJetLockType.NONE;
        /** Number of SHARED locks held */
        private int sharedLockCount = 0;
        /** Number of pointers to this structure */
        private int numRef = 1;
    };

    /**
     * An instance of the following structure is allocated for each open inode.
     * This structure keeps track of the number of locks on that inode. If a
     * close is attempted against an inode that is holding locks, the close is
     * deferred until all locks clear by adding the file descriptor to be closed
     * to the pending list.
     */
    private static class OpenFile {
        /** Number of pointers to this structure */
        private int numRef = 1;
        /** Number of outstanding locks */
        private int numLock = 0;
        private Map<Thread, LockInfo> lockInfoMap = new ConcurrentHashMap<Thread, LockInfo>();
        /** Malloced space holding fd's awaiting a close() */
        private List<RandomAccessFile> pending = new ArrayList<RandomAccessFile>();
    };

    private final static Map<String, OpenFile> openFiles = new HashMap<String, OpenFile>();

    private SqlJetFileType fileType;
    private Set<SqlJetFileOpenPermission> permissions;
    private RandomAccessFile file;
    private File filePath;
    private String filePathResolved;
    private boolean noLock;

    private SqlJetLockType lockType = SqlJetLockType.NONE;
    private Map<SqlJetLockType, FileLock> locks = new ConcurrentHashMap<SqlJetLockType, FileLock>();

    private OpenFile openCount = null;
    private LockInfo lockInfo = null;

    private SqlJetFileLockManager fileLockManager;

    /**
     * @param fileSystem
     * @param file
     * @param filePath
     * @param permissions
     * @param type
     * @param noLock
     */

    SqlJetFile(final SqlJetFileSystem fileSystem, final RandomAccessFile file, final File filePath,
            final SqlJetFileType fileType, final Set<SqlJetFileOpenPermission> permissions, final boolean noLock) {
        this.file = file;
        this.filePath = filePath;
        this.filePathResolved = filePath.getAbsolutePath();
        this.fileType = fileType;
        this.permissions = EnumSet.copyOf(permissions);
        this.noLock = noLock;

        this.fileLockManager = new SqlJetFileLockManager(this.filePathResolved, file.getChannel());

        findLockInfo();

        OSTRACE("OPEN    %s\n", this.filePath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#getFileType()
     */
    public SqlJetFileType getFileType() {
        return fileType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#getPermissions()
     */
    public synchronized Set<SqlJetFileOpenPermission> getPermissions() {
        // return clone to avoid manipulations with file's permissions
        HashSet<SqlJetFileOpenPermission> permissionsCopy = new HashSet<SqlJetFileOpenPermission>();
        for (SqlJetFileOpenPermission permission : permissions) {
            permissionsCopy.add(permission);
        }
        return permissionsCopy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#close()
     */
    public synchronized void close() throws SqlJetException {
        if (null == file)
            return;

        synchronized (openFiles) {

            unlock(SqlJetLockType.NONE);

            /*
             * If there are outstanding locks, do not actually close the file
             * just yet because that would clear those locks. Instead, add the
             * file descriptor to pOpen->aPending. It will be automatically
             * closed when the last lock is cleared.
             */
            if (!noLock && null != openCount && null != openCount.lockInfoMap && openCount.lockInfoMap.size() > 0) {
                for (LockInfo l : openCount.lockInfoMap.values()) {
                    if (l.sharedLockCount > 0) {
                        openCount.pending.add(file);
                        return;
                    }
                }
            }
            /*
             * if (!noLock && null != openCount && null != lockInfo &&
             * lockInfo.sharedLockCount > 0) { openCount.pending.add(file);
             * return; }
             */

            releaseLockInfo();

            try {
                file.close();
            } catch (IOException e) {
                throw new SqlJetException(SqlJetErrorCode.IOERR, e);
            } finally {
                file = null;
            }

        }

        if (filePath != null && permissions.contains(SqlJetFileOpenPermission.DELETEONCLOSE)) {
            if (!SqlJetFileUtil.deleteFile(filePath)) {
                throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_DELETE, String.format("Can't delete file '%s'",
                        filePath.getPath()));
            }
        }

        OSTRACE("CLOSE   %s\n", this.filePath);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#read(byte[], int, long)
     */
    public synchronized int read(ISqlJetMemoryPointer buffer, int amount, long offset) throws SqlJetIOException {
        assert (amount > 0);
        assert (offset >= 0);
        assert (buffer != null);
        assert (buffer.remaining() >= amount);
        assert (file != null);
        try {
            TIMER_START();
            final int read = buffer.readFromFile(file, offset, amount);
            TIMER_END();
            OSTRACE("READ %s %5d %7d %d\n", this.filePath, read, offset, TIMER_ELAPSED());
            return read < 0 ? 0 : read;
        } catch (IOException e) {
            throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_READ, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#write(byte[], int, long)
     */
    public synchronized void write(ISqlJetMemoryPointer buffer, int amount, long offset) throws SqlJetIOException {
        assert (amount > 0);
        assert (offset >= 0);
        assert (buffer != null);
        assert (buffer.remaining() >= amount);
        assert (file != null);
        try {
            TIMER_START();
            final int write = buffer.writeToFile(file, offset, amount);
            TIMER_END();
            OSTRACE("WRITE %s %5d %7d %d\n", this.filePath, write, offset, TIMER_ELAPSED());
        } catch (IOException e) {
            throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_WRITE, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#truncate(long)
     */
    public synchronized void truncate(long size) throws SqlJetIOException {
        assert (size >= 0);
        assert (file != null);
        try {
            file.setLength(size);
        } catch (IOException e) {
            throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_TRUNCATE, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#sync(boolean, boolean)
     */
    public synchronized void sync(Set<SqlJetSyncFlags> syncFlags) throws SqlJetIOException {
        assert (file != null);
        try {
            OSTRACE("SYNC    %s\n", this.filePath);
            boolean syncMetaData = syncFlags != null && syncFlags.contains(SqlJetSyncFlags.NORMAL);
            file.getChannel().force(syncMetaData);
        } catch (IOException e) {
            throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_FSYNC, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#fileSize()
     */
    public synchronized long fileSize() throws SqlJetException {
        assert (file != null);
        try {
            return file.getChannel().size();
        } catch (IOException e) {
            throw new SqlJetException(SqlJetErrorCode.IOERR, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#lockType()
     */
    public synchronized SqlJetLockType getLockType() {
        return lockType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetFile#lock(org.tmatesoft.sqljet.core.
     * SqlJetLockType)
     */

    public synchronized boolean lock(final SqlJetLockType lockType) throws SqlJetIOException {
        assert (lockType != null);
        assert (file != null);

        /*
         * The following describes the implementation of the various locks and
         * lock transitions in terms of the POSIX advisory shared and exclusive
         * lock primitives (called read-locks and write-locks below, to avoid
         * confusion with SQLite lock names). The algorithms are complicated
         * slightly in order to be compatible with windows systems
         * simultaneously accessing the same database file, in case that is ever
         * required.
         * 
         * Symbols defined in os.h indentify the 'pending byte' and the
         * 'reserved byte', each single bytes at well known offsets, and the
         * 'shared byte range', a range of 510 bytes at a well known offset.
         * 
         * To obtain a SHARED lock, a read-lock is obtained on the 'pending
         * byte'. If this is successful, a random byte from the 'shared byte
         * range' is read-locked and the lock on the 'pending byte' released.
         * 
         * A process may only obtain a RESERVED lock after it has a SHARED lock.
         * A RESERVED lock is implemented by grabbing a write-lock on the
         * 'reserved byte'.
         * 
         * A process may only obtain a PENDING lock after it has obtained a
         * SHARED lock. A PENDING lock is implemented by obtaining a write-lock
         * on the 'pending byte'. This ensures that no new SHARED locks can be
         * obtained, but existing SHARED locks are allowed to persist. A process
         * does not have to obtain a RESERVED lock on the way to a PENDING lock.
         * This property is used by the algorithm for rolling back a journal
         * file after a crash.
         * 
         * An EXCLUSIVE lock, obtained after a PENDING lock is held, is
         * implemented by obtaining a write-lock on the entire 'shared byte
         * range'. Since all other locks require a read-lock on one of the bytes
         * within this range, this ensures that no other locks are held on the
         * database.
         * 
         * The reason a single byte cannot be used instead of the 'shared byte
         * range' is that some versions of windows do not support read-locks. By
         * locking a random byte from a range, concurrent SHARED locks may exist
         * even if the locking primitive used is always a write-lock.
         */

        if (noLock)
            return false;

        OSTRACE("LOCK    %s %s was %s(%s,%d) pid=%s\n", this.filePath, locktypeName(lockType),
                locktypeName(this.lockType), locktypeName(lockInfo.lockType), lockInfo.sharedLockCount, getpid());

        /*
         * If there is already a lock of this type or more restrictive on the
         * file then do nothing.
         */
        if (this.lockType.compareTo(lockType) > 0) {
            OSTRACE("LOCK    %s %s ok (already held)\n", this.filePath, locktypeName(lockType));
            return false;
        }

        /* Make sure the locking sequence is correct */
        assert (lockType != SqlJetLockType.PENDING);
        assert (this.lockType != SqlJetLockType.NONE || lockType == SqlJetLockType.SHARED);
        assert (lockType != SqlJetLockType.RESERVED || this.lockType == SqlJetLockType.SHARED);

        assert (lockInfo != null);
        try {
            synchronized (openFiles) {

                /*
                 * If some thread using this PID has a lock via a different
                 * unixFile handle that precludes the requested lock, return
                 * BUSY.
                 */
                if (this.lockType != lockInfo.lockType
                        && (SqlJetLockType.PENDING.compareTo(lockInfo.lockType) <= 0 || SqlJetLockType.SHARED
                                .compareTo(lockType) < 0)) {
                    return false;
                }

                /*
                 * If a SHARED lock is requested, and some thread using this PID
                 * already has a SHARED or RESERVED lock, then increment
                 * reference counts and return SQLITE_OK.
                 */
                if (lockType == SqlJetLockType.SHARED && lockInfo.sharedLockCount > 0
                        && (lockInfo.lockType == SqlJetLockType.SHARED || lockInfo.lockType == SqlJetLockType.RESERVED)) {
                    this.lockType = SqlJetLockType.SHARED;
                    lockInfo.sharedLockCount++;
                    openCount.numLock++;
                    return true;
                }

                /*
                 * A PENDING lock is needed before acquiring a SHARED lock and
                 * before acquiring an EXCLUSIVE lock. For the SHARED lock, the
                 * PENDING will be released.
                 */

                if (lockType == SqlJetLockType.SHARED
                        || (lockType == SqlJetLockType.EXCLUSIVE && this.lockType.compareTo(SqlJetLockType.PENDING) < 0)) {

                    if (lockType != SqlJetLockType.SHARED) {
                        final FileLock sharedLock = locks.get(SqlJetLockType.SHARED);
                        if (null != sharedLock) {
                            sharedLock.release();
                            locks.remove(SqlJetLockType.SHARED);
                        }
                    }

                    if (!locks.containsKey(SqlJetLockType.PENDING)) {
                        final FileLock pendingLock = fileLockManager.tryLock(PENDING_BYTE, 1,
                                lockType == SqlJetLockType.SHARED);
                        if (null == pendingLock)
                            return false;
                        locks.put(SqlJetLockType.PENDING, pendingLock);
                    }
                }

                /*
                 * If control gets to this point, then actually go ahead and
                 * make operating system calls for the specified lock.
                 */
                if (lockType == SqlJetLockType.SHARED) {

                    /* Now get the read-lock */
                    final FileLock sharedLock = fileLockManager.tryLock(SHARED_FIRST, SHARED_SIZE, true);
                    locks.put(SqlJetLockType.SHARED, sharedLock);

                    /* Drop the temporary PENDING lock */
                    final FileLock pendingLock = locks.get(SqlJetLockType.PENDING);
                    if (null != pendingLock) {
                        pendingLock.release();
                        locks.remove(SqlJetLockType.PENDING);
                    }

                    if (null == sharedLock)
                        return false;

                    this.lockType = SqlJetLockType.SHARED;
                    openCount.numLock++;
                    lockInfo.sharedLockCount = 1;

                } else if (lockType == SqlJetLockType.EXCLUSIVE && lockInfo.sharedLockCount > 1) {
                    /*
                     * We are trying for an exclusive lock but another thread in
                     * this same process is still holding a shared lock.
                     */
                    return false;

                } else {
                    /*
                     * The request was for a RESERVED or EXCLUSIVE lock. It is
                     * assumed that there is a SHARED or greater lock on the
                     * file already.
                     */
                    assert (SqlJetLockType.NONE != this.lockType);

                    switch (lockType) {
                    case RESERVED:
                        final FileLock reservedLock = fileLockManager.tryLock(RESERVED_BYTE, 1, false);
                        if (null == reservedLock)
                            return false;
                        locks.put(SqlJetLockType.RESERVED, reservedLock);
                        break;
                    case EXCLUSIVE:
                        final FileLock exclusiveLock = fileLockManager.tryLock(SHARED_FIRST, SHARED_SIZE, false);
                        if (null == exclusiveLock) {
                            this.lockType = SqlJetLockType.PENDING;
                            lockInfo.lockType = SqlJetLockType.PENDING;
                            return false;
                        }
                        locks.put(SqlJetLockType.EXCLUSIVE, exclusiveLock);
                        break;
                    default:
                        assert (false);
                    }

                }

                this.lockType = lockType;
                lockInfo.lockType = lockType;
                return true;

            }

        } catch (IOException e) {
            throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_LOCK, e);
        } finally {
            OSTRACE("LOCK    %s %s %s\n", this.filePath, locktypeName(lockType), this.lockType == lockType ? "ok"
                    : "failed");

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetFile#unlock(org.tmatesoft.sqljet.core
     * .SqlJetLockType)
     */
    public synchronized boolean unlock(final SqlJetLockType lockType) throws SqlJetIOException {
        assert (lockType != null);
        assert (file != null);

        /*
         * Lower the locking level on file descriptor pFile to locktype.
         * locktype must be either NONE or SHARED.
         * 
         * If the locking level of the file descriptor is already at or below
         * the requested locking level, this routine is a no-op.
         */

        if (noLock)
            return false;

        OSTRACE("UNLOCK  %s %s was %s(%s,%s) pid=%s\n", this.filePath, locktypeName(lockType),
                locktypeName(this.lockType), locktypeName(lockInfo.lockType), lockInfo.sharedLockCount, getpid());

        assert (SqlJetLockType.SHARED.compareTo(lockType) >= 0);
        if (this.lockType.compareTo(lockType) <= 0)
            return true;

        synchronized (openFiles) {

            assert (lockInfo != null);
            assert (lockInfo.sharedLockCount > 0);

            try {

                if (SqlJetLockType.SHARED.compareTo(this.lockType) < 0) {

                    if (SqlJetLockType.SHARED == lockType) {

                        final FileLock exclusiveLock = locks.get(SqlJetLockType.EXCLUSIVE);
                        if (null != exclusiveLock) {
                            if (exclusiveLock.isValid())
                                exclusiveLock.release();
                            locks.remove(SqlJetLockType.EXCLUSIVE);
                        }

                        if (null == locks.get(SqlJetLockType.SHARED)) {
                            final FileLock sharedLock = fileLockManager.lock(SHARED_FIRST, SHARED_SIZE, true);
                            if (null == sharedLock)
                                return false;
                            locks.put(SqlJetLockType.SHARED, sharedLock);
                        }

                    }

                    final FileLock reservedLock = locks.get(SqlJetLockType.RESERVED);
                    if (null != reservedLock) {
                        if (reservedLock.isValid())
                            reservedLock.release();
                        locks.remove(SqlJetLockType.RESERVED);
                    }

                    final FileLock pendingLock = locks.get(SqlJetLockType.PENDING);
                    if (null != pendingLock) {
                        if (pendingLock.isValid())
                            pendingLock.release();
                        locks.remove(SqlJetLockType.PENDING);
                    }

                    lockInfo.lockType = SqlJetLockType.SHARED;

                }
                if (lockType == SqlJetLockType.NONE) {
                    /*
                     * Decrement the shared lock counter. Release the lock using
                     * an OS call only when all threads in this same process
                     * have released the lock.
                     */
                    lockInfo.sharedLockCount--;
                    if (lockInfo.sharedLockCount == 0) {
                        lockInfo.sharedLockCount = 1;
                        for (final FileLock l : locks.values()) {
                            if (l.isValid())
                                l.release();
                        }
                        locks.clear();
                        lockInfo.sharedLockCount = 0;
                        this.lockType = SqlJetLockType.NONE;
                    }

                    /*
                     * Decrement the count of locks against this same file. When
                     * the count reaches zero, close any other file descriptors
                     * whose close was deferred because of outstanding locks.
                     */
                    openCount.numLock--;
                    assert (openCount.numLock >= 0);
                    if (openCount.numLock == 0 && null != openCount.pending && openCount.pending.size() > 0) {
                        for (final RandomAccessFile f : openCount.pending) {
                            f.close();
                        }
                        openCount.pending.clear();
                    }

                }
                this.lockType = lockType;

            } catch (IOException e) {
                throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_LOCK, e);
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#checkReservedLock()
     */
    public synchronized boolean checkReservedLock() {

        boolean reserved = false;
        try {
            if (noLock)
                return false;

            if (null == file)
                return false;

            if (null == lockInfo)
                return false;

            synchronized (openFiles) {

                /* Check if a thread in this process holds such a lock */
                if (SqlJetLockType.SHARED.compareTo(lockInfo.lockType) < 0)
                    return true;

                /* Otherwise see if some other process holds it. */
                try {

                    final FileLock reservedLock = fileLockManager.tryLock(RESERVED_BYTE, 1, false);

                    if (null == reservedLock) {
                        reserved = true;
                        return true;
                    }

                    reservedLock.release();

                } catch (IOException e) {
                }

            }

            return false;

        } finally {
            OSTRACE("TEST WR-LOCK %s %b\n", this.filePath, reserved);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#sectorSize()
     */
    public int sectorSize() {
        return SQLJET_DEFAULT_SECTOR_SIZE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#deviceCharacteristics()
     */
    final static Set<SqlJetDeviceCharacteristics> noDeviceCharacteristircs = SqlJetUtility
            .noneOf(SqlJetDeviceCharacteristics.class);

    public Set<SqlJetDeviceCharacteristics> deviceCharacteristics() {
        return noDeviceCharacteristircs;
    }

    private synchronized void findLockInfo() {
        synchronized (openFiles) {
            if (null == openCount) {
                final OpenFile fileOpenCount = openFiles.get(filePathResolved);
                if (null != fileOpenCount) {
                    openCount = fileOpenCount;
                    openCount.numRef++;
                } else {
                    openCount = new OpenFile();
                    openFiles.put(filePathResolved, openCount);
                }
            }
            final LockInfo fileLockInfo = openCount.lockInfoMap.get(Thread.currentThread());
            if (null != fileLockInfo) {
                lockInfo = fileLockInfo;
                lockInfo.numRef++;
            } else {
                lockInfo = new LockInfo();
                openCount.lockInfoMap.put(Thread.currentThread(), lockInfo);
            }
        }
    }

    /**
     * 
     */
    private void releaseLockInfo() {
        synchronized (openFiles) {
            if (null != lockInfo) {
                lockInfo.numRef--;
                if (0 == lockInfo.numRef) {
                    if (null != openCount) {
                        openCount.lockInfoMap.remove(Thread.currentThread());
                    }
                    this.lockInfo = null;
                }
            }
            if (null != openCount) {
                openCount.numRef--;
                if (0 == openCount.numRef) {
                    openFiles.remove(filePathResolved);
                    this.openCount = null;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#isMemJournal()
     */
    public boolean isMemJournal() {
        return false;
    }

}
