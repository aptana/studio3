/**
 * SqlJetConflictAction.java
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
 * "ON CONFLICT" action.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public enum SqlJetConflictAction {

    /**
     * When a constraint violation occurs, an immediate ROLLBACK occurs, thus
     * ending the current transaction, and the command aborts with a return code
     * of SQLITE_CONSTRAINT. If no transaction is active (other than the implied
     * transaction that is created on every command) then this algorithm works
     * the same as ABORT.
     */
    ROLLBACK,

    /**
     * When a constraint violation occurs, the command backs out any prior
     * changes it might have made and aborts with a return code of
     * SQLITE_CONSTRAINT. But no ROLLBACK is executed so changes from prior
     * commands within the same transaction are preserved. This is the default
     * behavior.
     */
    ABORT,

    /**
     *When a constraint violation occurs, the command aborts with a return code
     * SQLITE_CONSTRAINT. But any changes to the database that the command made
     * prior to encountering the constraint violation are preserved and are not
     * backed out. For example, if an UPDATE statement encountered a constraint
     * violation on the 100th row that it attempts to update, then the first 99
     * row changes are preserved but changes to rows 100 and beyond never occur.
     */
    FAIL,

    /**
     * When a constraint violation occurs, the one row that contains the
     * constraint violation is not inserted or changed. But the command
     * continues executing normally. Other rows before and after the row that
     * contained the constraint violation continue to be inserted or updated
     * normally. No error is returned.
     */
    IGNORE,

    /**
     * When a UNIQUE constraint violation occurs, the pre-existing rows that are
     * causing the constraint violation are removed prior to inserting or
     * updating the current row. Thus the insert or update always occurs. The
     * command continues executing normally. No error is returned. If a NOT NULL
     * constraint violation occurs, the NULL value is replaced by the default
     * value for that column. If the column has no default value, then the ABORT
     * algorithm is used. If a CHECK constraint violation occurs then the IGNORE
     * algorithm is used.
     * 
     * When this conflict resolution strategy deletes rows in order to satisfy a
     * constraint, it does not invoke delete triggers on those rows. Nor is the
     * update hook invoked. The exceptional behaviors defined in this paragraph
     * might change in a future release.
     */
    REPLACE;

    public static SqlJetConflictAction decode(String s) {
        if ("rollback".equalsIgnoreCase(s)) {
            return ROLLBACK;
        } else if ("abort".equalsIgnoreCase(s)) {
            return ABORT;
        } else if ("fail".equalsIgnoreCase(s)) {
            return FAIL;
        } else if ("ignore".equalsIgnoreCase(s)) {
            return IGNORE;
        } else if ("replace".equalsIgnoreCase(s)) {
            return REPLACE;
        }
        return null;
    }

    public String toString() {
        switch (this) {
        case ROLLBACK:
            return "ROLLBACK";
        case ABORT:
            return "ABORT";
        case FAIL:
            return "FAIL";
        case IGNORE:
            return "IGNORE";
        case REPLACE:
            return "REPLACE";
        }
        return "";
    }
}
