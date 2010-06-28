/**
 * SqlJetForeignKeyUpdateAction.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetForeignKeyUpdateAction;
import org.tmatesoft.sqljet.core.schema.SqlJetForeignKeyEvent;
import org.tmatesoft.sqljet.core.schema.SqlJetForeignKeyUpdate;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetForeignKeyUpdateAction implements ISqlJetForeignKeyUpdateAction {

    private final SqlJetForeignKeyEvent event;
    private final SqlJetForeignKeyUpdate action;

    public SqlJetForeignKeyUpdateAction(CommonTree ast) {
        assert "on".equalsIgnoreCase(ast.getText());
        assert ast.getChildCount() == 2;
        event = SqlJetForeignKeyEvent.decode(ast.getChild(0).getText());
        action = SqlJetForeignKeyUpdate.decode(ast.getChild(1).getText());
    }

    public SqlJetForeignKeyEvent getEvent() {
        return event;
    }

    public SqlJetForeignKeyUpdate getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ON " + getEvent() + ' ' + getAction();
    }
}
