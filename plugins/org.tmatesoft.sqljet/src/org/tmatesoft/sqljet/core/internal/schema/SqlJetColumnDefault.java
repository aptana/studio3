/**
 * SqlJetColumnDefault.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDefault;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetLiteralValue;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetColumnDefault extends SqlJetColumnConstraint implements ISqlJetColumnDefault {

    private final ISqlJetExpression expression;

    public SqlJetColumnDefault(SqlJetColumnDef column, String name, CommonTree ast) throws SqlJetException {
        super(column, name);
        assert "default".equalsIgnoreCase(ast.getText());
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
        buffer.append("DEFAULT ");
        ISqlJetExpression expression = getExpression();
        if (expression instanceof ISqlJetLiteralValue) {
            buffer.append(expression);
        } else {
            buffer.append("(");
            buffer.append(expression);
            buffer.append(")");
        }
        return buffer.toString();
    }
}
