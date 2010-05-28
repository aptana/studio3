/**
 * SqlJetConfig.java
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

import org.tmatesoft.sqljet.core.internal.ISqlJetConfig;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetConfig implements ISqlJetConfig {

    private static final String SQLJET_SYNCHRONIZED_THREADING = "SQLJET_SYNCHRONIZED_THREADING";
    private boolean synchronizedThreading = SqlJetUtility.getBoolSysProp(SQLJET_SYNCHRONIZED_THREADING, true);

    private static final String SQLJET_SHARED_CACHE = "SQLJET_SHARED_CACHE";
    private boolean sharedCacheEnabled = SqlJetUtility.getBoolSysProp(SQLJET_SHARED_CACHE, false);

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetConfig#isSharedCacheEnabled()
     */
    public boolean isSharedCacheEnabled() {
        return sharedCacheEnabled;
    }

    /**
     * @return the synchronizedThreading
     */
    public boolean isSynchronizedThreading() {
        return synchronizedThreading;
    }

}
