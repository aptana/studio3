/**
 * SqlJetColumnExpression.java
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

import org.antlr.runtime.tree.CommonTree;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetColumnExpression extends SqlJetExpression implements ISqlJetColumnExpression {

    private final String columnName, tableName, databaseName;

    public SqlJetColumnExpression(CommonTree ast) {
        assert "column_expression".equalsIgnoreCase(ast.getText());
        CommonTree columnNode = (CommonTree) ast.getChild(0);
        columnName = columnNode.getText();
        if (columnNode.getChildCount() > 0) {
            CommonTree tableNode = (CommonTree) columnNode.getChild(0);
            tableName = tableNode.getText();
            databaseName = (tableNode.getChildCount() > 0) ? tableNode.getChild(0).getText() : null;
        } else {
            tableName = null;
            databaseName = null;
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (getDatabaseName() != null) {
            buffer.append(getDatabaseName());
            buffer.append('.');
        }
        if (getTableName() != null) {
            buffer.append(getTableName());
            buffer.append('.');
        }
        buffer.append(getColumnName());
        return buffer.toString();
    }
}
