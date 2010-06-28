/**
 * SqlJetColumnCollate.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnCollate;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetColumnCollate extends SqlJetColumnConstraint implements ISqlJetColumnCollate {

    private String collation;

    public SqlJetColumnCollate(SqlJetColumnDef column, String name, CommonTree ast) {
        super(column, name);
        assert "collate".equalsIgnoreCase(ast.getText());
        assert ast.getChildCount() == 1;
        CommonTree child = (CommonTree) ast.getChild(0);
        collation = child.getText();
    }

    public String getCollation() {
        return collation;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        if (buffer.length() > 0) {
            buffer.append(' ');
        }
        buffer.append("COLLATE ");
        buffer.append(collation);
        return buffer.toString();
    }
}
