/**
 * ISqlJetRaiseExpression.java
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
 * "RAISE" expression.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public interface ISqlJetRaiseExpression extends ISqlJetExpression {

    /**
     * "RAISE" expression's action.
     * 
     * @author TMate Software Ltd.
     * @author Dmitry Stadnik (dtrace@seznam.cz)
     */
    public enum Action {
        IGNORE, ROLLBACK, ABORT, FAIL;

        public static Action decode(String s) {
            if ("ignore".equalsIgnoreCase(s)) {
                return IGNORE;
            } else if ("rollback".equalsIgnoreCase(s)) {
                return ROLLBACK;
            } else if ("abort".equalsIgnoreCase(s)) {
                return ABORT;
            } else if ("fail".equalsIgnoreCase(s)) {
                return FAIL;
            }
            return null;
        }

        public String toString() {
            switch (this) {
            case IGNORE:
                return "IGNORE";
            case ROLLBACK:
                return "ROLLBACK";
            case ABORT:
                return "ABORT";
            case FAIL:
                return "FAIL";
            }
            return "";
        }
    }

    public Action getAction();

    public String getErrorMessage();
}
