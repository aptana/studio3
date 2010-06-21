/**
 * SqlJetExpression.java
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
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetBinaryExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetMatchExpression;
import org.tmatesoft.sqljet.core.schema.ISqlJetUnaryExpression;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public abstract class SqlJetExpression implements ISqlJetExpression {

    public static ISqlJetExpression create(CommonTree ast) throws SqlJetException {
        String op = ast.getText().toLowerCase();
        if ("bind".equals(op) || "bind_name".equals(op)) {
            return new SqlJetBindParameter(ast);
        } else if ("column_expression".equals(op)) {
            return new SqlJetColumnExpression(ast);
        } else if ("null".equals(op)) {
            return new SqlJetNullLiteral(ast);
        } else if ("blob_literal".equals(op)) {
            return new SqlJetBlobLiteral(ast);
        } else if ("float_literal".equals(op)) {
            return new SqlJetFloatLiteral(ast);
        } else if ("function_literal".equals(op)) {
            return new SqlJetFunctionLiteral(ast);
        } else if ("integer_literal".equals(op)) {
            return new SqlJetIntegerLiteral(ast);
        } else if ("string_literal".equals(op)) {
            return new SqlJetStringLiteral(ast);
        } else if ("function_expression".equals(op)) {
            return new SqlJetFunctionExpression(ast);
        } else if ("isnull".equals(op) || "notnull".equals(op)) {
            return new SqlJetIsNullExpression(ast);
        } else if ("case".equals(op)) {
            return new SqlJetCaseExpression(ast);
        } else if ("raise".equals(op)) {
            return new SqlJetRaiseExpression(ast);
        } else if (ISqlJetMatchExpression.Operation.decode(ast.getText()) != null) {
            return new SqlJetMatchExpression(ast);
        } else if ("in_values".equals(op)) {
            return new SqlJetInValuesExpression(ast);
        } else if ("in_table".equals(op)) {
            return new SqlJetInTableExpression(ast);
        } else if ("between".equals(op)) {
            return new SqlJetBetweenExpression(ast);
        } else if (ISqlJetBinaryExpression.Operation.decode(ast.getText()) != null && ast.getChildCount() == 2) {
            return new SqlJetBinaryExpression(ast);
        } else if (ISqlJetUnaryExpression.Operation.decode(ast.getText()) != null && ast.getChildCount() == 1) {
            return new SqlJetUnaryExpression(ast);
        } else if ("collate".equals(op)) {
            return new SqlJetCollateExpression(ast);
        }
        throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid expression");
    }
    
    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.schema.ISqlJetExpression#getValue()
     */
    public Object getValue() {
        return toString();
    }
}
