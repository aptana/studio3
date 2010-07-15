/**
 * SqlJetBetweenExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetBetweenExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetBetweenExpression extends SqlJetExpression implements ISqlJetBetweenExpression {

    private final ISqlJetExpression expression, lowerBound, upperBound;
    private final boolean not;

    public SqlJetBetweenExpression(CommonTree ast) throws SqlJetException {
        assert "between".equalsIgnoreCase(ast.getText());
        int idx = 0;
        CommonTree child = (CommonTree) ast.getChild(idx++);
        if ("not".equalsIgnoreCase(child.getText())) {
            not = true;
            child = (CommonTree) ast.getChild(idx++);
        } else {
            not = false;
        }
        expression = create((CommonTree) ast.getChild(idx++));
        lowerBound = create((CommonTree) ast.getChild(idx++));
        upperBound = create((CommonTree) ast.getChild(idx++));
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    public boolean isNot() {
        return not;
    }

    public ISqlJetExpression getLowerBound() {
        return lowerBound;
    }

    public ISqlJetExpression getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getExpression());
        if (isNot()) {
            buffer.append(" NOT");
        }
        buffer.append(" BETWEEN ");
        buffer.append(getLowerBound());
        buffer.append(" AND ");
        buffer.append(getUpperBound());
        return buffer.toString();
    }
}
