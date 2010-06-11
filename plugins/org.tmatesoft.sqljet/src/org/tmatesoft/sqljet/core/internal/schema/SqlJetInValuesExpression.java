/**
 * SqlJetInValuesExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetInValuesExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetInValuesExpression extends SqlJetExpression implements ISqlJetInValuesExpression {

    private final ISqlJetExpression expression;
    private final boolean not;
    private final List<ISqlJetExpression> values;

    public SqlJetInValuesExpression(CommonTree ast) throws SqlJetException {
        assert "in_values".equalsIgnoreCase(ast.getText());
        int idx = 0;
        CommonTree child = (CommonTree) ast.getChild(idx++);
        if ("not".equalsIgnoreCase(child.getText())) {
            not = true;
            child = (CommonTree) ast.getChild(idx++);
        } else {
            not = false;
        }
        assert "in".equalsIgnoreCase(child.getText());
        List<ISqlJetExpression> values = new ArrayList<ISqlJetExpression>();
        for (int exprIdx = 0; exprIdx < child.getChildCount(); exprIdx++) {
            values.add(create((CommonTree) child.getChild(exprIdx)));
        }
        this.values = Collections.unmodifiableList(values);
        expression = create((CommonTree) ast.getChild(idx));
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    public boolean isNot() {
        return not;
    }

    public List<ISqlJetExpression> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getExpression());
        if (isNot()) {
            buffer.append(" NOT");
        }
        buffer.append(" IN (");
        for (int idx = 0; idx < getValues().size(); idx++) {
            if (idx > 0) {
                buffer.append(", ");
            }
            buffer.append(getValues().get(idx));
        }
        buffer.append(')');
        return buffer.toString();
    }
}
