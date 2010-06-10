/**
 * ISqlJetMatchExpression.java
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
 * "MATCH" expression.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public interface ISqlJetMatchExpression extends ISqlJetExpression {

    /**
     * "MATCH" expression's operation.
     * 
     * @author TMate Software Ltd.
     * @author Dmitry Stadnik (dtrace@seznam.cz)
     */
    public enum Operation {
        LIKE, GLOB, REGEXP, MATCH;

        public static Operation decode(String s) {
            if ("like".equalsIgnoreCase(s)) {
                return LIKE;
            } else if ("glob".equalsIgnoreCase(s)) {
                return GLOB;
            } else if ("regexp".equalsIgnoreCase(s)) {
                return REGEXP;
            } else if ("match".equalsIgnoreCase(s)) {
                return MATCH;
            }
            return null;
        }

        public String toString() {
            switch (this) {
            case LIKE:
                return "LIKE";
            case GLOB:
                return "GLOB";
            case REGEXP:
                return "REGEXP";
            case MATCH:
                return "MATCH";
            }
            return "";
        }
    }

    public ISqlJetExpression getExpression();

    public Operation getOperation();

    public boolean isNot();

    public ISqlJetExpression getMatchExpression();

    public ISqlJetExpression getEscapeExpression();
}
