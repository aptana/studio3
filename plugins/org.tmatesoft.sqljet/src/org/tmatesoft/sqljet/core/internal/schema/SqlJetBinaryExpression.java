/**
 * SqlJetBinaryExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetBinaryExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetBinaryExpression extends SqlJetExpression implements ISqlJetBinaryExpression {

    private final Operation operation;
    private final ISqlJetExpression leftExpression, rightExpression;

    public SqlJetBinaryExpression(CommonTree ast) throws SqlJetException {
        operation = Operation.decode(ast.getText());
        assert operation != null;
        if (operation == Operation.EQUALS || operation == Operation.NOT_EQUALS) {
            leftExpression = create((CommonTree) ast.getChild(1));
            rightExpression = create((CommonTree) ast.getChild(0));
        } else {
            leftExpression = create((CommonTree) ast.getChild(0));
            rightExpression = create((CommonTree) ast.getChild(1));
        }
    }

    public Operation getOperation() {
        return operation;
    }

    public ISqlJetExpression getLeftExpression() {
        return leftExpression;
    }

    public ISqlJetExpression getRightExpression() {
        return rightExpression;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getLeftExpression());
        buffer.append(' ');
        buffer.append(operation);
        buffer.append(' ');
        buffer.append(getRightExpression());
        return buffer.toString();
    }
}
