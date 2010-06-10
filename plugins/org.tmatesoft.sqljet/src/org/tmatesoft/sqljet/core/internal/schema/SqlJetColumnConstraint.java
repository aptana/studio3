/**
 * SqlJetColumnConstraint.java
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

import org.tmatesoft.sqljet.core.schema.ISqlJetColumnConstraint;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public abstract class SqlJetColumnConstraint implements ISqlJetColumnConstraint {

    private final SqlJetColumnDef column;
    private final String name;

    public SqlJetColumnConstraint(SqlJetColumnDef column, String name) {
        this.column = column;
        this.name = name;
    }

    public SqlJetColumnDef getColumn() {
        return column;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (getName() != null) {
            buffer.append("CONSTRAINT ");
            buffer.append(getName());
        }
        return buffer.toString();
    }
}
