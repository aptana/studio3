/**
 * SqlJetTablePrimaryKey.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetTablePrimaryKey;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetTablePrimaryKey extends SqlJetTableIndexConstraint implements ISqlJetTablePrimaryKey {

    public SqlJetTablePrimaryKey(String name, CommonTree ast) {
        super(name, ast);
        assert "primary".equalsIgnoreCase(ast.getText());
    }

    @Override
    protected String getConstraintName() {
        return "PRIMARY KEY";
    }
}
