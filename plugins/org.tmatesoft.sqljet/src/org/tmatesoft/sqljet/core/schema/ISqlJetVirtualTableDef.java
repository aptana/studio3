/**
 * ISqlJetVirtualTableDef.java
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
package org.tmatesoft.sqljet.core.schema;

import java.util.List;

/**
 * Virtual table schema definition.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetVirtualTableDef {

    /**
     * @return the name
     */
    String getTableName();

    /**
     * @return the databaseName
     */
    String getDatabaseName();

    /**
     * @return the moduleName
     */
    String getModuleName();

    /**
     * @return the moduleColumns
     */
    List<ISqlJetColumnDef> getModuleColumns();

    /**
     * @return the page
     */
    int getPage();

    /**
     * @param page
     *            the page to set
     */
    void setPage(int page);

    /**
     * @return the rowId
     */
    long getRowId();

    /**
     * @param rowId
     *            the rowId to set
     */
    void setRowId(long rowId);

    /**
     * @return SQL representation of this virtual table schema definition.
     */
    String toSQL();

}