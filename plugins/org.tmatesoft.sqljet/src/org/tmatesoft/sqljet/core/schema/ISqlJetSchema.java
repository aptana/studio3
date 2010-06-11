/**
 * ISqlJetSchema.java
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

import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;

/**
 * Database schema interface.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 */
public interface ISqlJetSchema {

    /**
     * Get tables names.
     * 
     * @return tables names.
     * @throws SqlJetException
     */
    Set<String> getTableNames() throws SqlJetException;

    /**
     * Get table definition by name.
     * 
     * @param name
     *            table name
     * @return table definition
     * @throws SqlJetException
     */
    ISqlJetTableDef getTable(String name) throws SqlJetException;

    /**
     * Get indices names.
     * 
     * @return indices names.
     * @throws SqlJetException
     */
    Set<String> getIndexNames() throws SqlJetException;

    /**
     * Get index definition by name.
     * 
     * @param name
     *            index name
     * @return index definition
     * @throws SqlJetException
     */
    ISqlJetIndexDef getIndex(String name) throws SqlJetException;

    /**
     * Get indices related with table.
     * 
     * @param tableName
     *            table name
     * @return indices of table
     * @throws SqlJetException
     */
    Set<ISqlJetIndexDef> getIndexes(String tableName) throws SqlJetException;

    /**
     * @return Set of virtual table names defined in this schema.
     * 
     * @throws SqlJetException
     */
    Set<String> getVirtualTableNames() throws SqlJetException;

    /**
     * @param name
     * @return definition of the virtual table <code>name</code>.
     * 
     * @throws SqlJetException
     */
    ISqlJetVirtualTableDef getVirtualTable(String name) throws SqlJetException;
}
