/**
 * ISqlJet.java
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

import java.util.List;
import java.util.Set;

import org.tmatesoft.sqljet.core.ISqlJetMutex;
import org.tmatesoft.sqljet.core.table.ISqlJetBusyHandler;
import org.tmatesoft.sqljet.core.table.ISqlJetOptions;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetDbHandle {

    /**
     * Name of the master database table. The master database table is a special
     * table that holds the names and attributes of all user tables and indices.
     */
    String MASTER_NAME = "sqlite_master";
    String TEMP_MASTER_NAME = "sqlite_temp_master";

    /**
     * The root-page of the master database table.
     */
    int MASTER_ROOT = 1;

    /**
     * Get config
     * 
     * @return
     */
    ISqlJetConfig getConfig();

    /**
     * Set config
     * 
     * @param config
     */
    void setConfig(ISqlJetConfig config);

    /**
     * @return
     */
    ISqlJetFileSystem getFileSystem();

    /**
     * @return
     */
    Set<SqlJetDbFlags> getFlags();

    List<ISqlJetBackend> getBackends();

    ISqlJetMutex getMutex();

    /**
     * @return
     */
    ISqlJetBusyHandler getBusyHandler();

    /**
     * @return
     */
    int getSavepointNum();

    ISqlJetOptions getOptions();

    void setOptions(ISqlJetOptions options);

    /**
     * @param busyHandler
     */
    void setBusyHandler(ISqlJetBusyHandler busyHandler);
}
