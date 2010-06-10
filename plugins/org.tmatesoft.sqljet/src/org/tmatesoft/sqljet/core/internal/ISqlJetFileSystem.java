/**
 * IVirtualFileSystem.java
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
package org.tmatesoft.sqljet.core.internal;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetIOException;

/**
 * 
 * File System Interface.
 *
 * An instance of the {@link ISqlJetFileSystem} object defines the interface between
 * the SqlJet core and the underlying file system. 
 * 
 *
 * The randomness(), sleep(), and currentTime() interfaces
 * are not strictly a part of the filesystem, but they are
 * included in the {@link ISqlJetFileSystem} structure for completeness.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetFileSystem {

    /**
     * The getName() returns the name of the FS module. The name must
     * be unique across all FS modules.
     * 
     * @return
     */
    String getName();

    /**
     * The flags argument to open() includes all set in
     * the flags argument to ISqlJet.open(). Flags includes at least
     *  {@link SqlJetFileOpenPermission#READWRITE} and 
     *  {@link SqlJetFileOpenPermission#CREATE}. 
     * 
     * If open() opens a file read-only then it sets flags in 
     * {@link ISqlJetFile#getPermissions()} to
     * include {@link SqlJetFileOpenPermission#READONLY}.  
     * Other permissions may be set.
     *
     * SqlJet will also add one of the following flags to the open()
     * call, depending on the object being opened:
     *
     * <ul>
     * <li>  {@link SqlJetFileOpenPermission#MAIN_DB}
     * <li>  {@link SqlJetFileOpenPermission#MAIN_JOURNAL}
     * <li>  {@link SqlJetFileOpenPermission#TEMP_DB}
     * <li>  {@link SqlJetFileOpenPermission#TEMP_JOURNAL}
     * <li>  {@link SqlJetFileOpenPermission#TRANSIENT_DB}
     * <li>  {@link SqlJetFileOpenPermission#SUBJOURNAL}
     * <li>  {@link SqlJetFileOpenPermission#MASTER_JOURNAL}
     * </ul>
     *
     * The file I/O implementation can use the object type flags to
     * change the way it deals with files.  For example, an application
     * that does not care about crash recovery or rollback might make
     * the open of a journal file a no-op.  Writes to this journal would
     * also be no-ops, and any attempt to read the journal would throws
     * {@link SqlJetIOException}.  Or the implementation might recognize that a database
     * file will be doing page-aligned sector reads and writes in a random
     * order and set up its I/O subsystem accordingly.
     *
     * SqlJet might also add one of the following flags to the open() method:
     *
     * <ul>
     * <li> {@link SqlJetFileOpenPermission#DELETEONCLOSE}
     * <li> {@link SqlJetFileOpenPermission#EXCLUSIVE}
     * </ul>
     *
     * The {@link SqlJetFileOpenPermission#DELETEONCLOSE} flag means the file should be
     * deleted when it is closed.  The {@link SqlJetFileOpenPermission#DELETEONCLOSE}
     * will be set for TEMP  databases, journals and for subjournals.
     *
     * The {@link SqlJetFileOpenPermission#EXCLUSIVE} flag means the file should be opened
     * for exclusive access.  This flag is set for all files except
     * for the main database file.
     * 
     *
     * @param path      {@link File} or NULL. If NULL then open()
     *              must invite its own temporary name for the file. Whenever the 
     *              filename parameter is NULL it will also be the case that the
     *              flags parameter will include {@link SqlJetFileOpenPermission#DELETEONCLOSE}.
     *              
     * @param permissions   Exactly one of the {@link SqlJetFileOpenPermission#READWRITE} and 
     *   {@link SqlJetFileOpenPermission#READONLY} flags must be set, and 
     *   if {@link SqlJetFileOpenPermission#CREATE} is set, 
     *   then {@link SqlJetFileOpenPermission#READWRITE} must also be set, and
     *   if {@link SqlJetFileOpenPermission#EXCLUSIVE} is set, 
     *   then {@link SqlJetFileOpenPermission#CREATE} must also be set.
     *   if {@link SqlJetFileOpenPermission#DELETEONCLOSE} is set, 
     *   then {@link SqlJetFileOpenPermission#CREATE} must also be set.
     *   
     * @return  Opened file.
     * 
     * @throws SqlJetException
     *          If it is impossible to open file.
     *          
     */
    ISqlJetFile open(final File path, final SqlJetFileType type,
            final Set<SqlJetFileOpenPermission> permissions) throws SqlJetException;

    /** 
    ** Open a memory journal file.
    */
    ISqlJetFile memJournalOpen();

    /**
     * Delete the file. If the sync argument is true, sync()
     * the directory after deleting the file.
     * 
     * @param path
     * @param sync
     * @return
     */
    boolean delete(final File path, final boolean sync) throws SqlJetException;


    /**
     * The flags argument to access() may be {@link SqlJetFileAccesPermission#EXISTS}
     * to test for the existence of a file, or {@link SqlJetFileAccesPermission#READWRITE} to
     * test whether a file is readable and writable, or {@link SqlJetFileAccesPermission#READ}
     * to test whether a file is at least readable. The file can be a directory.
     * 
     * Test the existance of or access permissions of file. The
     * test performed depends on the value of flags:
     *
     *     {@link SqlJetFileAccesPermission#EXISTS}: Return true if the file exists
     *     {@link SqlJetFileAccesPermission#READWRITE}: Return true if the file is read and writable.
     *     {@link SqlJetFileAccesPermission#READ}: Return true if the file is readable.
     *
     * Otherwise return false.
     * 
     * 
     * @param path
     * @param permission
     * @return
     * @throws SqlJetException
     */
    boolean access(final File path, final SqlJetFileAccesPermission permission) throws SqlJetException;

    /**
     * The randomness() function returns numBytes bytes of good-quality randomness.
     * 
     * @param numBytes
     * @return
     */
    byte[] randomness(final int numBytes);

    /**
     * The sleep() method causes the calling thread to sleep for at
     * least the number of microseconds given.
     * 
     * @param microseconds
     * @return
     */
    long sleep(final long microseconds);

    /**
     *  The currentTime() method returns a Julian Day Number for the current date and time.
     * 
     * @return
     */
    long currentTime();

    File getTempFile() throws IOException;
    
    String getFullPath(File filename) throws SqlJetException;
    
}
