/**
 * ISqlJetBackend.java
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

import org.tmatesoft.sqljet.core.schema.ISqlJetSchema;

/**
 * Each database file to be accessed by the system is an instance
 * of the following structure.  There are normally two of these structures
 * in the sqlite.aDb[] array.  aDb[0] is the main database file and
 * aDb[1] is the database file used to hold temporary tables.  Additional
 * databases may be attached.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public interface ISqlJetBackend {

    String getName();         /* Name of this database */
    
    ISqlJetBtree getBtree();          /* The B*Tree structure for this database file */
    
    SqlJetTransactionState getTransactionState();  /* 0: not writable.  1: Transaction.  2: Checkpoint */
    SqlJetSafetyLevel getSafetyLevel();     /* How aggressive at syncing data to disk */
    
    //void *pAux;               /* Auxiliary data.  Usually NULL */
    //void (*xFreeAux)(void*);  /* Routine to free pAux */
    
    ISqlJetSchema getSchema();     /* Pointer to database schema (possibly shared) */
    
}
