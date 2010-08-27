/**
 * SqlJetInTableExpression.java
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
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetInTableExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetInTableExpression extends SqlJetExpression implements ISqlJetInTableExpression {

    private final ISqlJetExpression expression;
    private final boolean not;
    private final String tableName, databaseName;

    public SqlJetInTableExpression(CommonTree ast) throws SqlJetException {
        assert "in_table".equalsIgnoreCase(ast.getText());
        int idx = 0;
        CommonTree child = (CommonTree) ast.getChild(idx++);
        if ("not".equalsIgnoreCase(child.getText())) {
            not = true;
            child = (CommonTree) ast.getChild(idx++);
        } else {
            not = false;
        }
        assert "in".equalsIgnoreCase(child.getText());
        CommonTree tableNode = (CommonTree) child.getChild(0);
        tableName = tableNode.getText();
        if (tableNode.getChildCount() > 0) {
            databaseName = tableNode.getChild(0).getText();
        } else {
            databaseName = null;
        }
        expression = create((CommonTree) ast.getChild(idx));
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    public boolean isNot() {
        return not;
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
        buffer.append(getExpression());
        if (isNot()) {
            buffer.append(" NOT");
        }
        buffer.append(" IN ");
        if (getDatabaseName() != null) {
            buffer.append(getDatabaseName());
            buffer.append('.');
        }
        buffer.append(getTableName());
        return buffer.toString();
    }
}
