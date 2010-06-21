/**
 * SqlJetDbHandle.java
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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tmatesoft.sqljet.core.ISqlJetMutex;
import org.tmatesoft.sqljet.core.internal.ISqlJetBackend;
import org.tmatesoft.sqljet.core.internal.ISqlJetConfig;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.ISqlJetFileSystem;
import org.tmatesoft.sqljet.core.internal.SqlJetDbFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.fs.SqlJetFileSystemsManager;
import org.tmatesoft.sqljet.core.internal.mutex.SqlJetEmptyMutex;
import org.tmatesoft.sqljet.core.internal.mutex.SqlJetMutex;
import org.tmatesoft.sqljet.core.table.ISqlJetBusyHandler;
import org.tmatesoft.sqljet.core.table.ISqlJetOptions;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetDbHandle implements ISqlJetDbHandle {

    private Set<SqlJetDbFlags> flags = SqlJetUtility.noneOf(SqlJetDbFlags.class);
    private ISqlJetConfig config = new SqlJetConfig();
    private ISqlJetFileSystem fileSystem = SqlJetFileSystemsManager.getManager().find(null);
    private ISqlJetMutex mutex = new SqlJetEmptyMutex();
    private List<ISqlJetBackend> backends = new LinkedList<ISqlJetBackend>();
    private ISqlJetOptions options;
    private ISqlJetBusyHandler busyHandler;

    public SqlJetDbHandle() {
        if (config.isSynchronizedThreading()) {
            mutex = new SqlJetMutex();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetDb#getBackends()
     */
    public List<ISqlJetBackend> getBackends() {
        return backends;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetDb#getBusyHaldler()
     */
    public ISqlJetBusyHandler getBusyHandler() {
        return busyHandler;
    }

    /**
     * @param busyHandler
     *            the busyHandler to set
     */
    public void setBusyHandler(ISqlJetBusyHandler busyHandler) {
        this.busyHandler = busyHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetDb#getConfig()
     */
    public ISqlJetConfig getConfig() {
        return config;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetDb#getFileSystem()
     */
    public ISqlJetFileSystem getFileSystem() {
        return fileSystem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetDb#getFlags()
     */
    public Set<SqlJetDbFlags> getFlags() {
        // TODO Auto-generated method stub
        return flags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetDb#getMutex()
     */
    public ISqlJetMutex getMutex() {
        return mutex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetDb#getSavepointNum()
     */
    public int getSavepointNum() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetDb#setConfig(org.tmatesoft.sqljet.core
     * .ISqlJetConfig)
     */
    public void setConfig(ISqlJetConfig config) {
        // TODO Auto-generated method stub

    }

    public ISqlJetOptions getOptions() {
        return options;
    }

    public void setOptions(ISqlJetOptions options) {
        this.options = options;
    }
}
