/**
 * SqlJetCollateExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetCollateExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetCollateExpression extends SqlJetExpression implements ISqlJetCollateExpression {

    private final ISqlJetExpression expression;
    private final String collationName;

    public SqlJetCollateExpression(CommonTree ast) throws SqlJetException {
        assert "collate".equalsIgnoreCase(ast.getText());
        expression = create((CommonTree) ast.getChild(0));
        collationName = ast.getChild(1).getText();
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    public String getCollationName() {
        return collationName;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getExpression());
        buffer.append(" COLLATE ");
        buffer.append(getCollationName());
        return buffer.toString();
    }
}
