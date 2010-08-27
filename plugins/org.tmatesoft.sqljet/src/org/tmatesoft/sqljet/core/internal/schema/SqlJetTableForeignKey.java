/**
 * SqlJetTableForeignKey.java
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
import org.tmatesoft.sqljet.core.schema.ISqlJetForeignKey;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableForeignKey;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetTableForeignKey extends SqlJetTableConstraint implements ISqlJetTableForeignKey {

    private final List<String> columnNames;
    private final ISqlJetForeignKey foreignKey;

    public SqlJetTableForeignKey(String name, CommonTree ast) {
        super(name);
        assert "foreign".equalsIgnoreCase(ast.getText());
        CommonTree columnsNode = (CommonTree) ast.getChild(1);
        assert "columns".equalsIgnoreCase(columnsNode.getText()) || "references".equalsIgnoreCase(columnsNode.getText());
        List<String> columnNames = new ArrayList<String>();
        for (int i = 0; i < columnsNode.getChildCount(); i++) {
            columnNames.add(columnsNode.getChild(i).getText());
        }
        this.columnNames = Collections.unmodifiableList(columnNames);
        foreignKey = new SqlJetForeignKey((CommonTree) ast.getChild(1));
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public ISqlJetForeignKey getForeignKey() {
        return foreignKey;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        if (buffer.length() > 0) {
            buffer.append(' ');
        }
        buffer.append("FOREIGN KEY (");
        for (int i = 0; i < getColumnNames().size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(getColumnNames().get(i));
        }
        buffer.append(") ");
        buffer.append(foreignKey);
        return buffer.toString();
    }
}
