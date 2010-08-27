/**
 * SqlJetColumnCheck.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnCheck;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetColumnCheck extends SqlJetColumnConstraint implements ISqlJetColumnCheck {

    private final ISqlJetExpression expression;

    public SqlJetColumnCheck(SqlJetColumnDef column, String name, CommonTree ast) throws SqlJetException {
        super(column, name);
        assert "check".equalsIgnoreCase(ast.getText());
        expression = SqlJetExpression.create((CommonTree) ast.getChild(0));
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        if (buffer.length() > 0) {
            buffer.append(' ');
        }
        buffer.append("CHECK (");
        buffer.append(getExpression());
        buffer.append(")");
        return buffer.toString();
    }
}
