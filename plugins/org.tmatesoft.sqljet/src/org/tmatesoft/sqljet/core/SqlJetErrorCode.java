/**
 * SqlJetErrorCode.java
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
package org.tmatesoft.sqljet.core;

/**
 * SqlJet error codes.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public enum SqlJetErrorCode {

    /** SQL error or missing database */
    ERROR,
    
    /** Internal logic error in SQLite */
    INTERNAL,
    
    /** Internal logic error in SQLite */
    PERM,
    
    /** Callback routine requested an abort */    
    ABORT,
    
    /** The database file is locked */
    BUSY,
    
    /** A table in the database is locked */
    LOCKED,
    
    /** A malloc() failed */
    NOMEM,
    
    /** Attempt to write a readonly database */
    READONLY,
    
    /** Operation terminated by sqlite3_interrupt()*/
    INTERRUPT,
    
    /** Some kind of disk I/O error occurred */
    IOERR,
    
    /** The database disk image is malformed */
    CORRUPT,
    
    /** NOT USED. Table or record not found */
    NOTFOUND,
    
    /** Insertion failed because database is full */
    FULL,
    
    /** Unable to open the database file */
    CANTOPEN,
    
    /** NOT USED. Database lock protocol error */
    PROTOCOL,
    
    /** Database is empty */
    EMPTY,
    
    /** The database schema changed */
    SCHEMA,
    
    /** String or BLOB exceeds size limit */
    TOOBIG,
    
    /** Abort due to constraint violation */
    CONSTRAINT,
    
    /** Data type mismatch */
    MISMATCH,
    
    /** Library used incorrectly */
    MISUSE,
    
    /** Uses OS features not supported on host */
    NOLFS,
    
    /** Authorization denied */
    AUTH,
    
    /** Auxiliary database format error */
    FORMAT,
    
    /** 2nd parameter to sqlite3_bind out of range */
    RANGE,
    
    /** File opened that is not a database file */
    NOTADB,
    
    DONE,
    
    /** Bad parameter value in function call wich impossible to execute */
    BAD_PARAMETER

}
