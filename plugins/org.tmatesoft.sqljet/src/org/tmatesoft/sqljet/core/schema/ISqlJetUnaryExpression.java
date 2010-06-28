/**
 * ISqlJetUnaryExpression.java
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
package org.tmatesoft.sqljet.core.schema;

/**
 * Unary expression.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public interface ISqlJetUnaryExpression extends ISqlJetExpression {

    /**
     * Unary expression's operations.
     * 
     * @author TMate Software Ltd.
     * @author Dmitry Stadnik (dtrace@seznam.cz)
     */
    public enum Operation {
        PLUS, MINUS, NEGATE, NOT;

        public static Operation decode(String s) {
            if ("+".equalsIgnoreCase(s)) {
                return PLUS;
            } else if ("-".equalsIgnoreCase(s)) {
                return MINUS;
            } else if ("~".equalsIgnoreCase(s)) {
                return NEGATE;
            } else if ("not".equalsIgnoreCase(s)) {
                return NOT;
            }
            return null;
        }

        public String toString() {
            switch (this) {
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case NEGATE:
                return "~";
            case NOT:
                return "NOT";
            }
            return "";
        }
    }

    public Operation getOperation();

    public ISqlJetExpression getExpression();
}
