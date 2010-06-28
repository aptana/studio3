/**
 * SqlJetFileSystemsManager.java
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetFileSystem;
import org.tmatesoft.sqljet.core.internal.ISqlJetFileSystemsManager;

/**
 * Singleton implementation of {@link ISqlJetFileSystemsManager}.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetFileSystemsManager implements ISqlJetFileSystemsManager {

    private Object lock = new Object();
    private ISqlJetFileSystem defaultFileSystem = null;
    private Map<String, ISqlJetFileSystem> fileSystems = new ConcurrentHashMap<String, ISqlJetFileSystem>();

    private static SqlJetFileSystemsManager manager = new SqlJetFileSystemsManager(); 
    
    /**
     * Protected constructor 
     */
    protected SqlJetFileSystemsManager() {
        try {
            register(new SqlJetFileSystem(), true);
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Singleton accessor.
     * 
     * @return the manager
     */
    public static SqlJetFileSystemsManager getManager() {
        return manager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetFileSystemsManager#find(java.lang.String
     * )
     */
    public ISqlJetFileSystem find(final String name) {
        if (null != name)
            return fileSystems.get(name);
        else
            synchronized (lock) {
                return defaultFileSystem;
            }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetFileSystemsManager#register(org.tmatesoft
     * .sqljet.core.ISqlJetFileSystem, boolean)
     */
    public void register(final ISqlJetFileSystem fs, final boolean isDefault) throws SqlJetException {
        checkFS(fs);
        fileSystems.put(fs.getName(), fs);
        if (isDefault || null == defaultFileSystem)
            synchronized (lock) {
                defaultFileSystem = fs;
            }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetFileSystemsManager#unregister(org.tmatesoft
     * .sqljet.core.ISqlJetFileSystem)
     */
    public void unregister(final ISqlJetFileSystem fs) throws SqlJetException {
        checkFS(fs);
        fileSystems.remove(fs.getName());
        if (fs == defaultFileSystem) {
            synchronized (lock) {
                defaultFileSystem = null;
                if (fileSystems.size() > 0) {
                    defaultFileSystem = fileSystems.values().iterator().next();
                } else
                    defaultFileSystem = null;
            }
        }
    }

    /**
     * Check FS parameter
     * 
     * @param fs
     * @throws SqlJetExceptionRemove
     */
    private void checkFS(final ISqlJetFileSystem fs) throws SqlJetException {
        if(null==fs) 
            throw new SqlJetException(SqlJetErrorCode.BAD_PARAMETER,
                "Prameter 'fs' must be not null");
        if(null==fs.getName()) 
            throw new SqlJetException(SqlJetErrorCode.BAD_PARAMETER,
                "fs.getName() must return not null value");
    }
    
}
