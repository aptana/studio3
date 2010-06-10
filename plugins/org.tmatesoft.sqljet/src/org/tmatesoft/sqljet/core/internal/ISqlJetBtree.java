/**
 * ISqlJetBtree.java
 * Copyright (C) 2009-2010 TMate Software Ltd
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
package org.tmatesoft.sqljet.core.internal;

import java.io.File;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.internal.schema.SqlJetSchema;

/**
 * A Btree handle
 * 
 * A database connection contains a pointer to an instance of this object for
 * every database file that it has open. This structure is opaque to the
 * database connection. The database connection cannot see the internals of this
 * structure and only deals with pointers to this structure.
 * 
 * For some database files, the same underlying database cache might be shared
 * between multiple connections. In that case, each contection has it own
 * pointer to this object. But each instance of this object points to the same
 * BtShared object. The database cache and the schema associated with the
 * database file are all contained within the BtShared object.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetBtree {

    SqlJetAutoVacuumMode SQLJET_DEFAULT_AUTOVACUUM = SqlJetUtility.getEnumSysProp("SQLJET_DEFAULT_AUTOVACUUM",
            SqlJetAutoVacuumMode.NONE);

    String SQLITE_FILE_HEADER = "SQLite format 3";

    /*
     * * The header string that appears at the beginning of every* SQLite
     * database.
     */
    ISqlJetMemoryPointer zMagicHeader = SqlJetUtility.wrapPtr(SqlJetUtility.addZeroByteEnd(SqlJetUtility
            .getBytes(SQLITE_FILE_HEADER)));

    /**
     * Open a database file.
     * 
     * zFilename is the name of the database file. If zFilename is NULL a new
     * database with a random name is created. This randomly named database file
     * will be deleted when sqlite3BtreeClose() is called. If zFilename is
     * ":memory:" then an in-memory database is created that is automatically
     * destroyed when it is closed.
     * 
     * 
     * @param filename
     *            Name of database file to open
     * @param db
     *            Associated database connection
     * @param flags
     *            Flags
     * @param fsFlags
     *            Flags passed through to VFS open
     * @return
     */
    void open(File filename, ISqlJetDbHandle db, Set<SqlJetBtreeFlags> flags, final SqlJetFileType type,
            final Set<SqlJetFileOpenPermission> permissions) throws SqlJetException;

    /**
     * Close an open database and invalidate all cursors.
     * 
     * @throws SqlJetException
     */
    void close() throws SqlJetException;

    /**
     * Change the limit on the number of pages allowed in the cache.
     * 
     * The maximum number of cache pages is set to the absolute value of mxPage.
     * If mxPage is negative, the pager will operate asynchronously - it will
     * not stop to do fsync()s to insure data is written to the disk surface
     * before continuing. Transactions still work if synchronous is off, and the
     * database cannot be corrupted if this program crashes. But if the
     * operating system crashes or there is an abrupt power failure when
     * synchronous is off, the database could be left in an inconsistent and
     * unrecoverable state. Synchronous is on by default so database corruption
     * is not normally a worry.
     * 
     * @param mxPage
     */
    void setCacheSize(int mxPage);

    /**
     * Change the way data is synced to disk in order to increase or decrease
     * how well the database resists damage due to OS crashes and power
     * failures. Level 1 is the same as asynchronous (no syncs() occur and there
     * is a high probability of damage) Level 2 is the default. There is a very
     * low but non-zero probability of damage. Level 3 reduces the probability
     * of damage to near zero but with a write performance reduction.
     * 
     * @param level
     * @throws SqlJetException
     */
    void setSafetyLevel(SqlJetSafetyLevel level);

    /**
     * Return TRUE if the given btree is set to safety level 1. In other words,
     * return TRUE if no sync() occurs on the disk files.
     * 
     * @return
     */
    boolean isSyncDisabled();

    /**
     * Change the default pages size and the number of reserved bytes per page.
     * 
     * The page size must be a power of 2 between 512 and 65536. If the page
     * size supplied does not meet this constraint then the page size is not
     * changed.
     * 
     * Page sizes are constrained to be a power of two so that the region of the
     * database file used for locking (beginning at PENDING_BYTE, the first byte
     * past the 1GB boundary, 0x40000000) needs to occur at the beginning of a
     * page.
     * 
     * If parameter nReserve is less than zero, then the number of reserved
     * bytes per page is left unchanged.
     * 
     * @param pageSize
     * @param nReserve
     * @throws SqlJetException
     */
    void setPageSize(int pageSize, int nReserve) throws SqlJetException;

    /**
     * Return the currently defined page size
     * 
     * @return
     */
    int getPageSize();

    /**
     * Set the maximum page count for a database if mxPage is positive. No
     * changes are made if mxPage is 0 or negative. Regardless of the value of
     * mxPage, return the maximum page count.
     * 
     * @param mxPage
     * @throws SqlJetException
     */
    void setMaxPageCount(int mxPage) throws SqlJetException;

    int getReserve();

    /**
     * Change the 'auto-vacuum' property of the database. If the 'autoVacuum'
     * parameter is non-zero, then auto-vacuum mode is enabled. If zero, it is
     * disabled. The default value for the auto-vacuum property is determined by
     * the SQLITE_DEFAULT_AUTOVACUUM macro.
     * 
     * @param autoVacuum
     * @throws SqlJetException
     */
    void setAutoVacuum(SqlJetAutoVacuumMode autoVacuum) throws SqlJetException;

    /**
     * Return the value of the 'auto-vacuum' property. If auto-vacuum is enabled
     * 1 is returned. Otherwise 0.
     * 
     * @return
     * @throws SqlJetException
     */
    SqlJetAutoVacuumMode getAutoVacuum();

    /**
     * Attempt to start a new transaction. A write-transaction is started if the
     * second argument is nonzero, otherwise a read- transaction. If the second
     * argument is 2 or more and exclusive transaction is started, meaning that
     * no other process is allowed to access the database. A preexisting
     * transaction may not be upgraded to exclusive by calling this routine a
     * second time - the exclusivity flag only works for a new transaction.
     * 
     * A write-transaction must be started before attempting any changes to the
     * database. None of the following routines will work unless a transaction
     * is started first:
     * 
     * createTable() createIndex() clearTable() dropTable() insert() delete()
     * updateMeta()
     * 
     * If an initial attempt to acquire the lock fails because of lock
     * contention and the database was previously unlocked, then invoke the busy
     * handler if there is one. But if there was previously a read-lock, do not
     * invoke the busy handler - just return BUSY. BUSY is returned when there
     * is already a read-lock in order to avoid a deadlock.
     * 
     * Suppose there are two processes A and B. A has a read lock and B has a
     * reserved lock. B tries to promote to exclusive but is blocked because of
     * A's read lock. A tries to promote to reserved but is blocked by B. One or
     * the other of the two processes must give way or there can be no progress.
     * By returning BUSY and not invoking the busy callback when A already has a
     * read lock, we encourage A to give up and let B proceed.
     * 
     * @param mode
     * @throws SqlJetException
     */
    void beginTrans(SqlJetTransactionMode mode) throws SqlJetException;

    /**
     * This routine does the first phase of a two-phase commit. This routine
     * causes a rollback journal to be created (if it does not already exist)
     * and populated with enough information so that if a power loss occurs the
     * database can be restored to its original state by playing back the
     * journal. Then the contents of the journal are flushed out to the disk.
     * After the journal is safely on oxide, the changes to the database are
     * written into the database file and flushed to oxide. At the end of this
     * call, the rollback journal still exists on the disk and we are still
     * holding all locks, so the transaction has not committed. See
     * sqlite3BtreeCommit() for the second phase of the commit process.
     * 
     * This call is a no-op if no write-transaction is currently active on pBt.
     * 
     * Otherwise, sync the database file for the btree pBt. zMaster points to
     * the name of a master journal file that should be written into the
     * individual journal file, or is NULL, indicating no master journal file
     * (single database transaction).
     * 
     * When this is called, the master journal should already have been created,
     * populated with this journal pointer and synced to disk.
     * 
     * Once this is routine has returned, the only thing required to commit the
     * write-transaction for this database file is to delete the journal.
     * 
     * @param master
     * @throws SqlJetException
     */
    void commitPhaseOne(String master) throws SqlJetException;

    /**
     * Commit the transaction currently in progress.
     * 
     * This routine implements the second phase of a 2-phase commit. The
     * sqlite3BtreeSync() routine does the first phase and should be invoked
     * prior to calling this routine. The sqlite3BtreeSync() routine did all the
     * work of writing information out to disk and flushing the contents so that
     * they are written onto the disk platter. All this routine has to do is
     * delete or truncate the rollback journal (which causes the transaction to
     * commit) and drop locks.
     * 
     * This will release the write lock on the database file. If there are no
     * active cursors, it also releases the read lock.
     * 
     * @throws SqlJetException
     */
    void commitPhaseTwo() throws SqlJetException;

    /**
     * Do both phases of a commit.
     * 
     * @throws SqlJetException
     */
    void commit() throws SqlJetException;

    /**
     * Rollback the transaction in progress. All cursors will be invalided by
     * this operation. Any attempt to use a cursor that was open at the
     * beginning of this operation will result in an error.
     * 
     * This will release the write lock on the database file. If there are no
     * active cursors, it also releases the read lock.
     * 
     * @throws SqlJetException
     */
    void rollback() throws SqlJetException;

    /**
     * Start a statement subtransaction. The subtransaction can can be rolled
     * back independently of the main transaction. You must start a transaction
     * before starting a subtransaction. The subtransaction is ended
     * automatically if the main transaction commits or rolls back.
     * 
     * Only one subtransaction may be active at a time. It is an error to try to
     * start a new subtransaction if another subtransaction is already active.
     * 
     * Statement subtransactions are used around individual SQL statements that
     * are contained within a BEGIN...COMMIT block. If a constraint error occurs
     * within the statement, the effect of that one statement can be rolled back
     * without having to rollback the entire transaction.
     * 
     * @throws SqlJetException
     */
    void beginStmt() throws SqlJetException;

    /**
     * Commit the statment subtransaction currently in progress. If no
     * subtransaction is active, this is a no-op.
     * 
     * @throws SqlJetException
     */
    void commitStmt() throws SqlJetException;

    /**
     * Rollback the active statement subtransaction. If no subtransaction is
     * active this routine is a no-op.
     * 
     * All cursors will be invalidated by this operation. Any attempt to use a
     * cursor that was open at the beginning of this operation will result in an
     * error.
     * 
     * @throws SqlJetException
     */
    void rollbackStmt() throws SqlJetException;

    /**
     * Create a new BTree table. Returns the page number for the root page of
     * the new table.
     * 
     * The type of type is determined by the flags parameter. Only the following
     * values of flags are currently in use. Other values for flags might not
     * work:
     * 
     * INTKEY|LEAFDATA Used for SQL tables with rowid keys ZERODATA Used for SQL
     * indices
     * 
     * @param flags
     * @return the page number for the root page of the new table
     * @throws SqlJetException
     */
    int createTable(Set<SqlJetBtreeTableCreateFlags> flags) throws SqlJetException;

    /**
     * Return true if a transaction is active.
     * 
     * @return
     */
    boolean isInTrans();

    /**
     * Return true if a statement transaction is active.
     * 
     * @return
     */
    boolean isInStmt();

    /**
     * Return true if a read (or write) transaction is active.
     * 
     * @return
     */
    boolean isInReadTrans();

    /**
     * This function returns a pointer to a blob of memory associated with a
     * single shared-btree. The memory is used by client code for its own
     * purposes (for example, to store a high-level schema associated with the
     * shared-btree).
     * 
     * @return
     */
    SqlJetSchema getSchema();

    /**
     * @param schema
     */
    void setSchema(SqlJetSchema schema);

    /**
     * Return true if another user of the same shared btree as the argument
     * handle holds an exclusive lock on the sqlite_master table.
     * 
     * @return
     * @throws SqlJetException
     */
    boolean isSchemaLocked();

    /**
     * Obtain a lock on the table whose root page is iTab. The lock is a write
     * lock if isWritelock is true or a read lock if it is false.
     * 
     * @param iTab
     * @param isWriteLock
     * @throws SqlJetException
     */
    void lockTable(int table, boolean isWriteLock);

    /**
     * The second argument to this function, op, is always SAVEPOINT_ROLLBACK or
     * SAVEPOINT_RELEASE. This function either releases or rolls back the
     * savepoint identified by parameter iSavepoint, depending on the value of
     * op.
     * 
     * Normally, iSavepoint is greater than or equal to zero. However, if op is
     * SAVEPOINT_ROLLBACK, then iSavepoint may also be -1. In this case the
     * contents of the entire transaction are rolled back. This is different
     * from a normal transaction rollback, as no locks are released and the
     * transaction remains open.
     * 
     * @param op
     * @param iSavepoint
     * @throws SqlJetException
     */
    void savepoint(SqlJetSavepointOperation op, int savepoint) throws SqlJetException;

    /**
     * Return the full pathname of the underlying database file.
     * 
     * The pager filename is invariant as long as the pager is open so it is
     * safe to access without the BtShared mutex.
     * 
     * @return
     */
    File getFilename();

    /**
     * Return the pathname of the directory that contains the database file.
     * 
     * The pager directory name is invariant as long as the pager is open so it
     * is safe to access without the BtShared mutex.
     * 
     * @return
     */
    File getDirname();

    /**
     * Return the pathname of the journal file for this database. The return
     * value of this routine is the same regardless of whether the journal file
     * has been created or not.
     * 
     * The pager journal filename is invariant as long as the pager is open so
     * it is safe to access without the BtShared mutex.
     * 
     * @return
     */
    File getJournalname();

    /**
     * Copy the complete content of from. A transaction must be active for both
     * files.
     * 
     * The size of file may be reduced by this operation. If anything goes
     * wrong, the transaction is rolled back.
     * 
     * If successful, commitPhaseOne() may be called before returning. The
     * caller should finish committing the transaction by calling commit().
     * 
     * @param from
     * @throws SqlJetException
     */
    void copyFile(ISqlJetBtree from) throws SqlJetException;

    /**
     * A write-transaction must be opened before calling this function. It
     * performs a single unit of work towards an incremental vacuum.
     * 
     * If the incremental vacuum is finished after this function has run, DONE
     * is thrown. If it is not finished, but no error occured, none isn't
     * thrown. Otherwise an SQLite error code.
     * 
     * @throws SqlJetException
     */
    void incrVacuum() throws SqlJetException;

    /**
     * Erase all information in a table and add the root of the table to the
     * freelist. Except, the root of the principle table (the one on page 1) is
     * never added to the freelist.
     * 
     * This routine will fail with LOCKED if there are any open cursors on the
     * table.
     * 
     * If AUTOVACUUM is enabled and the page at table is not the last root page
     * in the database file, then the last root page in the database file is
     * moved into the slot formerly occupied by table and that last slot
     * formerly occupied by the last root page is added to the freelist instead
     * of iTable. In this say, all root pages are kept at the beginning of the
     * database file, which is necessary for AUTOVACUUM to work right. Returned
     * is the page number that used to be the last root page in the file before
     * the move. If no page gets moved, returned is 0. The last root page is
     * recorded in meta[3] and the value of meta[3] is updated by this
     * procedure.
     * 
     * @param table
     * @return
     * @throws SqlJetException
     */
    int dropTable(int table) throws SqlJetException;

    /**
     * Delete all information from a single table in the database. Table is the
     * page number of the root of the table. After this routine returns, the
     * root page is empty, but still exists.
     * 
     * This routine will fail with LOCKED if there are any open read cursors on
     * the table. Open write cursors are moved to the root of the table.
     * 
     * If nChange is not NULL, then table table must be an intkey table. The
     * integer value pointed to by nChange[0] is incremented by the number of
     * entries in the table.
     * 
     * @param table
     * @return
     * @throws SqlJetException
     */
    void clearTable(int table, int[] nChange) throws SqlJetException;

    /**
     * Read the meta-information out of a database file. Meta[0] is the number
     * of free pages currently in the database. Meta[1] through meta[15] are
     * available for use by higher layers. Meta[0] is read-only, the others are
     * read/write.
     * 
     * The schema layer numbers meta values differently. At the schema layer
     * (and the SetCookie and ReadCookie opcodes) the number of free pages is
     * not visible. So Cookie[0] is the same as Meta[1].
     * 
     * @param idx
     * @return
     * @throws SqlJetException
     */
    int getMeta(int idx) throws SqlJetException;

    /**
     * Write meta-information back into the database. Meta[0] is read-only and
     * may not be written.
     * 
     * @param idx
     * @param value
     * @throws SqlJetException
     */
    void updateMeta(int idx, int value) throws SqlJetException;

    /**
     * This routine sets the state to CURSOR_FAULT and the error code to errCode
     * for every cursor on BtShared that pBtree references.
     * 
     * Every cursor is tripped, including cursors that belong to other database
     * connections that happen to be sharing the cache with pBtree.
     * 
     * This routine gets called when a rollback occurs. All cursors using the
     * same cache must be tripped to prevent them from trying to use the btree
     * after the rollback. The rollback may have deleted tables or moved root
     * pages, so it is not sufficient to save the state of the cursor. The
     * cursor must be invalidated.
     * 
     * @param errCode
     * @throws SqlJetException
     */
    void tripAllCursors(SqlJetErrorCode errCode) throws SqlJetException;

    /**
     * This routine does a complete check of the given BTree file. aRoot[] is an
     * array of pages numbers were each page number is the root page of a table.
     * nRoot is the number of entries in aRoot.
     * 
     * Write the number of error seen in nErr[0]. Except for some memory
     * allocation errors, an error message held in memory obtained from malloc
     * is returned if nErr[0] is non-zero. If nErr[0]==0 then NULL is returned.
     * If a memory allocation error occurs, NULL is returned.
     * 
     * @return
     */
    String integrityCheck(int[] aRoot, int nRoot, int mxErr, int[] nErr) throws SqlJetException;

    /**
     * Return the pager associated with a BTree. This routine is used for
     * testing and debugging only.
     * 
     * @return
     * @throws SqlJetException
     */
    ISqlJetPager getPager() throws SqlJetException;

    /**
     * Create a new cursor for the BTree whose root is on the page iTable. The
     * act of acquiring a cursor gets a read lock on the database file.
     * 
     * If wrFlag==0, then the cursor can only be used for reading. If wrFlag==1,
     * then the cursor can be used for reading or for writing if other
     * conditions for writing are also met. These are the conditions that must
     * be met in order for writing to be allowed:
     * 
     * 1: The cursor must have been opened with wrFlag==1
     * 
     * 2: Other database connections that share the same pager cache but which
     * are not in the READ_UNCOMMITTED state may not have cursors open with
     * wrFlag==0 on the same table. Otherwise the changes made by this write
     * cursor would be visible to the read cursors in the other database
     * connection.
     * 
     * 3: The database must be writable (not on read-only media)
     * 
     * 4: There must be an active transaction.
     * 
     * No checking is done to make sure that page iTable really is the root page
     * of a b-tree. If it is not, then the cursor acquired will not work
     * correctly.
     * 
     * It is assumed that the sqlite3BtreeCursorSize() bytes of memory pointed
     * to by pCur have been zeroed by the caller.
     * 
     * @param table
     *            Index of root page
     * @param wrFlag
     *            true for writing. false for read-only
     * @param keyInfo
     *            First argument to compare function
     * @return
     * @throws SqlJetException
     */
    ISqlJetBtreeCursor getCursor(int table, boolean wrFlag, ISqlJetKeyInfo keyInfo) throws SqlJetException;

    /**
     * Enter a mutex on the given BTree object.
     * 
     * If the object is not sharable, then no mutex is ever required and this
     * routine is a no-op. The underlying mutex is non-recursive. But we keep a
     * reference count in Btree.wantToLock so the behavior of this interface is
     * recursive.
     * 
     * To avoid deadlocks, multiple Btrees are locked in the same order by all
     * database connections. The p->pNext is a list of other Btrees belonging to
     * the same database connection as the p Btree which need to be locked after
     * p. If we cannot get a lock on p, then first unlock all of the others on
     * p->pNext, then wait for the lock to become available on p, then relock
     * all of the subsequent Btrees that desire a lock.
     */
    void enter();

    /**
     * Exit the recursive mutex on a Btree.
     */
    void leave();

    /**
     * @return
     */
    int getCacheSize();

    void closeAllCursors() throws SqlJetException;

    /**
     * @return
     */
    ISqlJetDbHandle getDb();
}
