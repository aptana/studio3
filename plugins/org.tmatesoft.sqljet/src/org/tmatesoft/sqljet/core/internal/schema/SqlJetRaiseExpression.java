/**
 * SqlJetRaiseExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetRaiseExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetRaiseExpression extends SqlJetExpression implements ISqlJetRaiseExpression {

    private final Action action;
    private final String errorMessage;

    public SqlJetRaiseExpression(CommonTree ast) {
        assert "raise".equalsIgnoreCase(ast.getText());
        action = Action.decode(ast.getChild(0).getText());
        if (ast.getChildCount() > 1) {
            errorMessage = ast.getChild(1).getText();
        } else {
            errorMessage = null;
        }
    }

    public Action getAction() {
        return action;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("RAISE (");
        buffer.append(getAction());
        if (getErrorMessage() != null) {
            buffer.append(' ');
            buffer.append(getErrorMessage());
        }
        buffer.append(')');
        return buffer.toString();
    }
}
