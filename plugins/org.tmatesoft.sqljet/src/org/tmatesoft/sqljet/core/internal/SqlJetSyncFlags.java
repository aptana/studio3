/**
 * SqlJetSyncFlags.java
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

/**
 * Synchronization Type Flags
 * 
 * When SQLite invokes the xSync() method of an [sqlite3_io_methods] object it
 * uses a combination of these integer values as the second argument.
 * 
 * When the SQLITE_SYNC_DATAONLY flag is used, it means that the sync operation
 * only needs to flush data to mass storage. Inode information need not be
 * flushed. The SQLITE_SYNC_NORMAL flag means to use normal fsync() semantics.
 * The SQLITE_SYNC_FULL flag means to use Mac OS-X style fullsync instead of
 * fsync().
 * 
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public enum SqlJetSyncFlags {
    
    NORMAL,
    
    FULL,
    
    DATAONLY
    
}
