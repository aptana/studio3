/**
 * SqlJetBtreeFlags.java
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

import java.util.Set;

/**
 * The flags parameter to sqlite3BtreeOpen can be the bitwise or of the
 * following values.
 * 
 * NOTE: These values must match the corresponding PAGER_ values in pager.h.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public enum SqlJetBtreeFlags {

    /** Do not use journal. No argument */
    OMIT_JOURNAL(SqlJetPagerFlags.OMIT_JOURNAL),

    /** Omit readlocks on readonly files */
    NO_READLOCK(SqlJetPagerFlags.NO_READLOCK),

    /** In-memory DB. No argument */
    MEMORY,

    /** Open the database in read-only mode */
    READONLY,

    /** Open for both reading and writing */
    READWRITE,

    /** Create the database if it does not exist */
    CREATE;

    private SqlJetPagerFlags pagerFlag;

    /**
     * 
     */
    private SqlJetBtreeFlags() {
    }
    
    /**
     * 
     */
    private SqlJetBtreeFlags(SqlJetPagerFlags pagerFlag) {
        this.pagerFlag = pagerFlag;
    }
    
    /**
     * @return the pagerFlag
     */
    public SqlJetPagerFlags getPagerFlag() {
        return pagerFlag;
    }
    
    public static Set<SqlJetPagerFlags> toPagerFlags(final Set<SqlJetBtreeFlags> btreeFlags){
        if(null==btreeFlags) return null;
        final Set<SqlJetPagerFlags> Set = SqlJetUtility.noneOf(SqlJetPagerFlags.class);
        for(SqlJetBtreeFlags flag:btreeFlags) {
            final SqlJetPagerFlags f = flag.getPagerFlag();
            if(null!=f) Set.add(f);
        }
        return Set;
    }
    
}
