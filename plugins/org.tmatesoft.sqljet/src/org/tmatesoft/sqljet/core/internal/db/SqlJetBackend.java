/**
 * SqlJetBackend.java
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
package org.tmatesoft.sqljet.core.internal.db;

import org.tmatesoft.sqljet.core.internal.ISqlJetBackend;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.SqlJetSafetyLevel;
import org.tmatesoft.sqljet.core.internal.SqlJetTransactionState;
import org.tmatesoft.sqljet.core.schema.ISqlJetSchema;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public class SqlJetBackend implements ISqlJetBackend {
    
    private ISqlJetBtree btree;
    private String name;
    private SqlJetSafetyLevel safetyLevel;
    private ISqlJetSchema schema;
    private SqlJetTransactionState transactionState;

    /**
     * 
     */
    public SqlJetBackend(String name, ISqlJetBtree btree, ISqlJetSchema schema) {
        this.name = name;
        this.btree = btree;
        this.schema = schema;
    }
    
    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.ISqlJetBackend#getBtree()
     */
    public ISqlJetBtree getBtree() {
        return btree;
    }

    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.ISqlJetBackend#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.ISqlJetBackend#getSafetyLevel()
     */
    public SqlJetSafetyLevel getSafetyLevel() {
        return safetyLevel;
    }

    /**
     * @param safetyLevel the safetyLevel to set
     */
    public void setSafetyLevel(SqlJetSafetyLevel safetyLevel) {
        this.safetyLevel = safetyLevel;
    }
    
    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.ISqlJetBackend#getSchema()
     */
    public ISqlJetSchema getSchema() {
        return schema;
    }

    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.ISqlJetBackend#getTransactionState()
     */
    public SqlJetTransactionState getTransactionState() {
        return transactionState;
    }

    /**
     * @param transactionState the transactionState to set
     */
    public void setTransactionState(SqlJetTransactionState transactionState) {
        this.transactionState = transactionState;
    }
    
}
