/**
 * SqlJetIndexDef.java
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
 */
package org.tmatesoft.sqljet.core.internal.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexedColumn;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetIndexDef extends SqlJetBaseIndexDef {

    private final String databaseName;
    private final boolean unique;
    private final boolean ifNotExists;
    private final List<ISqlJetIndexedColumn> columns;

    SqlJetIndexDef(final String name, final String tableName, final int page, final String databaseName,
            final boolean unique, final boolean ifNotExists, final List<ISqlJetIndexedColumn> columns) {
        super(name, tableName, page);
        this.databaseName = databaseName;
        this.unique = unique;
        this.ifNotExists = ifNotExists;
        this.columns = Collections.unmodifiableList(columns);
    }

    public SqlJetIndexDef(CommonTree ast, int page) {
        super(null, null, page);

        CommonTree optionsNode = (CommonTree) ast.getChild(0);
        unique = hasOption(optionsNode, "unique");
        ifNotExists = hasOption(optionsNode, "exists");

        CommonTree nameNode = (CommonTree) ast.getChild(1);
        setName(nameNode.getText());
        databaseName = nameNode.getChildCount() > 0 ? nameNode.getChild(0).getText() : null;

        CommonTree tableNameNode = (CommonTree) ast.getChild(2);
        setTableName(tableNameNode.getText());

        List<ISqlJetIndexedColumn> columns = new ArrayList<ISqlJetIndexedColumn>();
        CommonTree defNode = (CommonTree) ast.getChild(3);
        for (int i = 0; i < defNode.getChildCount(); i++) {
            columns.add(new SqlJetIndexedColumn((CommonTree) defNode.getChild(i)));
        }
        this.columns = Collections.unmodifiableList(columns);
    }

    private boolean hasOption(CommonTree optionsNode, String name) {
        for (int i = 0; i < optionsNode.getChildCount(); i++) {
            CommonTree optionNode = (CommonTree) optionsNode.getChild(i);
            if (name.equalsIgnoreCase(optionNode.getText())) {
                return true;
            }
        }
        return false;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isKeepExisting() {
        return ifNotExists;
    }

    public List<ISqlJetIndexedColumn> getColumns() {
        return columns;
    }

    public ISqlJetIndexedColumn getColumn(String name) {
        for (ISqlJetIndexedColumn column : getColumns()) {
            if (column.getName().equalsIgnoreCase(name)) {
                return column;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getPage());
        buffer.append("/");
        buffer.append(getRowId());
        buffer.append(": ");
        buffer.append(toSQL());
        return buffer.toString();
    }

    public String toSQL() {
        return toSQL(true);
    }

    public String toSQL(boolean schemaStrict) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE ");
        if (isUnique()) {
            buffer.append("UNIQUE ");
        }
        buffer.append("INDEX ");
        if (!schemaStrict) {
            if (isKeepExisting()) {
                buffer.append("IF NOT EXISTS ");
            }
            if (getDatabaseName() != null) {
                buffer.append(getDatabaseName());
                buffer.append('.');
            }
        }
        buffer.append(getName());
        buffer.append(" ON ");
        buffer.append(getTableName());
        buffer.append(" (");
        List<ISqlJetIndexedColumn> columns = getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(columns.get(i).toString());
        }
        buffer.append(')');
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.schema.SqlJetBaseIndexDef#isImplicit()
     */
    @Override
    public boolean isImplicit() {
        return false;
    }

}
