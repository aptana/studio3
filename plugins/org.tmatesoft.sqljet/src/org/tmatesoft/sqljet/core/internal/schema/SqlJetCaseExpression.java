/**
 * SqlJetCaseExpression.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetCaseExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetCaseExpression extends SqlJetExpression implements ISqlJetCaseExpression {

    private final ISqlJetExpression expression, defaultValue;
    private final List<ISqlJetExpression> conditions, values;

    public SqlJetCaseExpression(CommonTree ast) throws SqlJetException {
        assert "case".equalsIgnoreCase(ast.getText());
        int idx = 0;
        CommonTree child = (CommonTree) ast.getChild(idx++);
        if ("when".equalsIgnoreCase(child.getText())) {
            expression = null;
        } else {
            expression = create(child);
            child = (CommonTree) ast.getChild(idx++);
        }
        List<ISqlJetExpression> conditions = new ArrayList<ISqlJetExpression>();
        List<ISqlJetExpression> values = new ArrayList<ISqlJetExpression>();
        while (idx < ast.getChildCount()) {
            if ("when".equalsIgnoreCase(child.getText())) {
                ISqlJetExpression condition = create((CommonTree) child.getChild(0));
                ISqlJetExpression value = create((CommonTree) child.getChild(1));
                conditions.add(condition);
                values.add(value);
                child = (CommonTree) ast.getChild(idx++);
            } else {
                break;
            }
        }
        this.conditions = Collections.unmodifiableList(conditions);
        this.values = Collections.unmodifiableList(values);
        if (idx < child.getChildCount()) {
            defaultValue = create(child);
        } else {
            defaultValue = null;
        }
    }

    public ISqlJetExpression getExpression() {
        return expression;
    }

    public List<ISqlJetExpression> getConditions() {
        return conditions;
    }

    public List<ISqlJetExpression> getValues() {
        return values;
    }

    public ISqlJetExpression getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CASE ");
        if (getExpression() != null) {
            buffer.append(getExpression());
            buffer.append(' ');
        }
        for (int idx = 0; idx < getConditions().size(); idx++) {
            buffer.append("WHEN ");
            buffer.append(getConditions().get(idx));
            buffer.append(' ');
            buffer.append(getValues().get(idx));
            buffer.append(' ');
        }
        if (getDefaultValue() != null) {
            buffer.append("ELSE ");
            buffer.append(getDefaultValue());
            buffer.append(' ');
        }
        buffer.append("END");
        return buffer.toString();
    }
}
