/**
 * SqlJetDbFlags.java
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
 * Possible values for the sqlite.flags and or Db.flags fields.
 *
 * On sqlite.flags, the SQLITE_InTrans value means that we have
 * executed a BEGIN.  On Db.flags, SQLITE_InTrans means a statement
 * transaction is active on that particular database file.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public enum SqlJetDbFlags {
    
    /* True to trace VDBE execution */    
    VdbeTrace,  
    
    /* True if in a transaction */
    InTrans,  
    
    /* Uncommitted Hash table changes */
    InternChanges,  
    
    /* Show full column names on SELECT */
    FullColNames,  
    
    /* Show short columns names */
    ShortColNames,  
    
    /* Count rows changed by INSERT, */
    /*   DELETE, or UPDATE and return */
    /*   the count using a callback. */    
    CountRows,  
    
    /* Invoke the callback once if the */
    /*   result set is empty */
    NullCallback, 
    
    /* Debug print SQL as it executes */
    SqlTrace,  
    
    /* Debug listings of VDBE programs */
    VdbeListing, 
    
    /* OK to update SQLITE_MASTER */
    WriteSchema,  
    
    /* Readlocks are omitted when 
     ** accessing read-only databases */
    NoReadlock,  
    
    /* Do not enforce check constraints */
    IgnoreChecks,  
    
    /* For shared-cache mode */
    ReadUncommitted,
    
    /* Create new databases in format 1 */
    LegacyFileFmt,  
    
    /* Use full fsync on the backend */
    FullFSync,  
    
    /* Enable load_extension */
    LoadExtension,  

    /* Ignore schema errors */
    RecoveryMode,
    
    /* Cache sharing is enabled */
    SharedCache,  
    
    /* There exists a virtual table */
    Vtab,  
    
    /* In the process of committing */
    CommitBusy

}
