/**
 * SqlJetUnaryExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetUnaryExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetUnaryExpression extends SqlJetExpression implements ISqlJetUnaryExpression {

    private final Operation operation;
    private final ISqlJetExpression expression;

    public SqlJetUnaryExpression(CommonTree ast) throws SqlJetException {
        operation = Operation.decode(ast.getText());
        expression = create((CommonTree) ast.getChild(0));
    }

    public Operation getOperation() {
        return operation;
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return getOperation().toString() + getExpression();
    }
}
