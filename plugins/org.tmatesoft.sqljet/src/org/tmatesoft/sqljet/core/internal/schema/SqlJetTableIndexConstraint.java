/**
 * SqlJetTableIndexConstraint.java
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
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public abstract class SqlJetTableIndexConstraint extends SqlJetTableConstraint {

    private List<String> columns;
    private SqlJetConflictAction conflictAction;
    private String indexName;

    public SqlJetTableIndexConstraint(String name, CommonTree ast) {
        super(name);
        CommonTree columnsNode = (CommonTree) ast.getChild(0);
        assert "columns".equalsIgnoreCase(columnsNode.getText());
        List<String> columns = new ArrayList<String>();
        for (int i = 0; i < columnsNode.getChildCount(); i++) {
            CommonTree child = (CommonTree) columnsNode.getChild(i);
            columns.add(child.getText());
        }
        this.columns = Collections.unmodifiableList(columns);
        if (ast.getChildCount() > 1) {
            CommonTree child = (CommonTree) ast.getChild(1);
            assert "conflict".equalsIgnoreCase(child.getText());
            assert child.getChildCount() == 1;
            child = (CommonTree) child.getChild(0);
            conflictAction = SqlJetConflictAction.decode(child.getText());
        }
    }

    public List<String> getColumns() {
        return columns;
    }

    public SqlJetConflictAction getConflictAction() {
        return conflictAction;
    }

    protected abstract String getConstraintName();

    public String getIndexName() {
        return indexName;
    }

    void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        if (buffer.length() > 0) {
            buffer.append(' ');
        }
        buffer.append(getConstraintName());
        buffer.append(" (");
        for (int i = 0; i < getColumns().size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(getColumns().get(i));
        }
        buffer.append(')');
        if (conflictAction != null) {
            buffer.append(" ON CONFLICT ");
            buffer.append(conflictAction);
        }
        return buffer.toString();
    }
}
