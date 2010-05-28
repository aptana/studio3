/**
 * SqlJetIndexedColumn.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexedColumn;
import org.tmatesoft.sqljet.core.schema.SqlJetSortingOrder;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetIndexedColumn implements ISqlJetIndexedColumn {

    private final String name;
    private final String collation;
    private final SqlJetSortingOrder sortingOrder;

    public SqlJetIndexedColumn(CommonTree ast) {
        name = ast.getText();
        String collation = null;
        SqlJetSortingOrder sortingOrder = null;
        for (int i = 0; i < ast.getChildCount(); i++) {
            CommonTree child = (CommonTree) ast.getChild(i);
            if ("collate".equalsIgnoreCase(child.getText())) {
                collation = child.getChild(0).getText();
            } else if ("asc".equalsIgnoreCase(child.getText())) {
                sortingOrder = SqlJetSortingOrder.ASC;
            } else if ("desc".equalsIgnoreCase(child.getText())) {
                sortingOrder = SqlJetSortingOrder.DESC;
            } else {
                assert false;
            }
        }
        this.collation = collation;
        this.sortingOrder = sortingOrder;
    }

    public String getName() {
        return name;
    }

    public String getCollation() {
        return collation;
    }

    public SqlJetSortingOrder getSortingOrder() {
        return sortingOrder;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getName());
        if (getCollation() != null) {
            buffer.append(" COLLATE ");
            buffer.append(getCollation());
        }
        if (getSortingOrder() != null) {
            buffer.append(' ');
            buffer.append(getSortingOrder());
        }
        return buffer.toString();
    }
}
