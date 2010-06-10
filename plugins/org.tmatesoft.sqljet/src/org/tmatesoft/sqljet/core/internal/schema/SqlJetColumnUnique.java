/**
 * SqlJetColumnUnique.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnUnique;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetColumnUnique extends SqlJetColumnIndexConstraint implements ISqlJetColumnUnique {

    private SqlJetConflictAction conflictAction;

    public SqlJetColumnUnique(SqlJetColumnDef column, String name, CommonTree ast) {
        super(column, name);
        assert "unique".equalsIgnoreCase(ast.getText());
        for (int i = 0; i < ast.getChildCount(); i++) {
            CommonTree child = (CommonTree) ast.getChild(i);
            if ("conflict".equalsIgnoreCase(child.getText())) {
                assert child.getChildCount() == 1;
                child = (CommonTree) child.getChild(0);
                conflictAction = SqlJetConflictAction.decode(child.getText());
            } else {
                assert false;
            }
        }
    }

    public SqlJetConflictAction getConflictAction() {
        return conflictAction;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        if (buffer.length() > 0) {
            buffer.append(' ');
        }
        buffer.append("UNIQUE");
        if (conflictAction != null) {
            buffer.append(" ON CONFLICT ");
            buffer.append(conflictAction);
        }
        return buffer.toString();
    }
}
