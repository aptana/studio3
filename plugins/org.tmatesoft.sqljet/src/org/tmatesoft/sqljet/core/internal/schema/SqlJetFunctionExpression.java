/**
 * SqlJetFunctionExpression.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetFunctionExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetFunctionExpression extends SqlJetExpression implements ISqlJetFunctionExpression {

    private final String name;
    private final boolean distinct, all;
    private final List<ISqlJetExpression> arguments;

    public SqlJetFunctionExpression(CommonTree ast) throws SqlJetException {
        assert "function_expression".equalsIgnoreCase(ast.getText());
        name = ast.getChild(0).getText();
        if ("*".equals(ast.getChild(1).getText())) {
            all = true;
            distinct = false;
            arguments = Collections.emptyList();
        } else {
            all = false;
            int idx = 1;
            CommonTree child = (CommonTree) ast.getChild(idx++);
            if ("distinct".equalsIgnoreCase(child.getText())) {
                distinct = true;
                child = (CommonTree) ast.getChild(idx++);
            } else {
                distinct = false;
            }
            List<ISqlJetExpression> arguments = new ArrayList<ISqlJetExpression>();
            while (idx < ast.getChildCount()) {
                ISqlJetExpression argument = create(child);
                arguments.add(argument);
                child = (CommonTree) ast.getChild(idx++);
            }
            this.arguments = Collections.unmodifiableList(arguments);
        }
    }

    public String getName() {
        return name;
    }

    public boolean areDistinctArguments() {
        return distinct;
    }

    public List<ISqlJetExpression> getArguments() {
        return arguments;
    }

    public boolean isAll() {
        return all;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getName());
        buffer.append(" (");
        if (isAll()) {
            buffer.append('*');
        } else {
            if (areDistinctArguments()) {
                buffer.append("DISTINCT ");
                for (int idx = 0; idx < getArguments().size(); idx++) {
                    if (idx > 0) {
                        buffer.append(", ");
                    }
                    buffer.append(getArguments().get(idx));
                }
            }
        }
        buffer.append(')');
        return buffer.toString();
    }
}
