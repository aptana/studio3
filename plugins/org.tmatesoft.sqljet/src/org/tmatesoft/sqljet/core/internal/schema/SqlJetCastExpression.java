/**
 * SqlJetCastExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetCastExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetTypeDef;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetCastExpression extends SqlJetExpression implements ISqlJetCastExpression {

    private final ISqlJetExpression expression;
    private final ISqlJetTypeDef type;

    public SqlJetCastExpression(CommonTree ast) throws SqlJetException {
        assert "cast".equalsIgnoreCase(ast.getText());
        expression = create((CommonTree) ast.getChild(0));
        type = new SqlJetTypeDef((CommonTree) ast.getChild(1));
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    public ISqlJetTypeDef getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CAST (");
        buffer.append(getExpression());
        buffer.append(" AS ");
        buffer.append(getType());
        buffer.append(')');
        return buffer.toString();
    }
}
