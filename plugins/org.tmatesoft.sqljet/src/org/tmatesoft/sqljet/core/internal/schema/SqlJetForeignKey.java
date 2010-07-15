/**
 * SqlJetForeignKey.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetForeignKey;
import org.tmatesoft.sqljet.core.schema.ISqlJetForeignKeyAction;
import org.tmatesoft.sqljet.core.schema.ISqlJetForeignKeyDeferrable;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetForeignKey implements ISqlJetForeignKey {

    private final String foreignTableName;
    private final List<String> columnNames;
    private final List<ISqlJetForeignKeyAction> actions;
    private final ISqlJetForeignKeyDeferrable deferrable;

    public SqlJetForeignKey(CommonTree ast) {
        assert "references".equalsIgnoreCase(ast.getText());
        foreignTableName = ast.getChild(0).getText();
        CommonTree columnsNode = (CommonTree) ast.getChild(1);
        assert "columns".equalsIgnoreCase(columnsNode.getText());
        List<String> columnNames = new ArrayList<String>();
        for (int i = 0; i < columnsNode.getChildCount(); i++) {
            columnNames.add(columnsNode.getChild(i).getText());
        }
        this.columnNames = Collections.unmodifiableList(columnNames);
        List<ISqlJetForeignKeyAction> actions = new ArrayList<ISqlJetForeignKeyAction>();
        ISqlJetForeignKeyDeferrable deferrable = null;
        for (int i = 2; i < ast.getChildCount(); i++) {
            CommonTree child = (CommonTree) ast.getChild(i);
            if ("deferrable".equalsIgnoreCase(child.getText())) {
                assert deferrable == null;
                deferrable = new SqlJetForeignKeyDeferrable(child);
            } else if ("on".equalsIgnoreCase(child.getText())) {
                actions.add(new SqlJetForeignKeyUpdateAction(child));
            } else if ("match".equalsIgnoreCase(child.getText())) {
                actions.add(new SqlJetForeignKeyMatchAction(child));
            } else {
                assert false;
            }
        }
        this.actions = Collections.unmodifiableList(actions);
        this.deferrable = deferrable;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<ISqlJetForeignKeyAction> getActions() {
        return actions;
    }

    public ISqlJetForeignKeyDeferrable getDeferrable() {
        return deferrable;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("REFERENCES ");
        buffer.append(getForeignTableName());
        buffer.append(" (");
        for (int i = 0; i < getColumnNames().size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(getColumnNames().get(i));
        }
        buffer.append(")");
        for (int i = 0; i < getActions().size(); i++) {
            buffer.append(' ');
            buffer.append(getActions().get(i));
        }
        if (getDeferrable() != null) {
            buffer.append(' ');
            buffer.append(getDeferrable());
        }
        return buffer.toString();
    }
}
