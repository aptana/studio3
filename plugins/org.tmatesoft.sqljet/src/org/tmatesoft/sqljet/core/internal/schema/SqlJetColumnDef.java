/**
 * SqlJetColumnDef.java
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
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnConstraint;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTypeDef;
import org.tmatesoft.sqljet.core.schema.SqlJetTypeAffinity;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetColumnDef implements ISqlJetColumnDef {

    private final String name;
    private final ISqlJetTypeDef type;
    private final List<ISqlJetColumnConstraint> constraints;

    public SqlJetColumnDef(CommonTree ast) throws SqlJetException {
        name = ast.getText();
        CommonTree constraintsNode = (CommonTree) ast.getChild(0);
        assert "constraints".equalsIgnoreCase(constraintsNode.getText());
        List<ISqlJetColumnConstraint> constraints = new ArrayList<ISqlJetColumnConstraint>();
        for (int i = 0; i < constraintsNode.getChildCount(); i++) {
            CommonTree constraintRootNode = (CommonTree) constraintsNode.getChild(i);
            assert "column_constraint".equalsIgnoreCase(constraintRootNode.getText());
            CommonTree constraintNode = (CommonTree) constraintRootNode.getChild(0);
            String constraintType = constraintNode.getText();
            String constraintName = constraintRootNode.getChildCount() > 1 ? constraintRootNode.getChild(1).getText()
                    : null;
            if ("primary".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnPrimaryKey(this, constraintName, constraintNode));
            } else if ("not_null".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnNotNull(this, constraintName, constraintNode));
            } else if ("unique".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnUnique(this, constraintName, constraintNode));
            } else if ("check".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnCheck(this, constraintName, constraintNode));
            } else if ("default".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnDefault(this, constraintName, constraintNode));
            } else if ("collate".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnCollate(this, constraintName, constraintNode));
            } else if ("references".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnForeignKey(constraintName, constraintNode));
            } else if ("is_null".equalsIgnoreCase(constraintType)) {
                constraints.add(new SqlJetColumnNull(this, constraintName, constraintNode));
            } else {
                assert false;
            }
        }
        this.constraints = Collections.unmodifiableList(constraints);
        if (ast.getChildCount() > 1) {
            CommonTree typeNode = (CommonTree) ast.getChild(1);
            assert "type".equalsIgnoreCase(typeNode.getText());
            type = new SqlJetTypeDef(typeNode);
        } else {
            type = null;
        }
    }

    public String getName() {
        return name;
    }

    public ISqlJetTypeDef getType() {
        return type;
    }

    public SqlJetTypeAffinity getTypeAffinity() {
        ISqlJetTypeDef type = getType();
        if (type == null) {
            return SqlJetTypeAffinity.decode(null);
        }
        List<String> typeNames = type.getNames();
        if (typeNames.size() == 1) {
            return SqlJetTypeAffinity.decode(typeNames.get(0)); // common case
        }
        String types = "";
        for (String typeName : getType().getNames()) {
            types += typeName + ' ';
        }
        return SqlJetTypeAffinity.decode(types);
    }

    public boolean hasExactlyIntegerType() {
        if (getTypeAffinity() != SqlJetTypeAffinity.INTEGER) {
            return false;
        }
        final ISqlJetTypeDef type = getType();
        if (type == null || type.getNames() == null || type.getNames().size() == 0) {
            return false;
        }
        return "INTEGER".equals(type.getNames().get(0).toUpperCase());
    }

    public List<ISqlJetColumnConstraint> getConstraints() {
        return constraints;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getName());
        if (getType() != null) {
            buffer.append(' ');
            buffer.append(getType());
        }
        for (int i = 0; i < getConstraints().size(); i++) {
            buffer.append(' ');
            buffer.append(getConstraints().get(i));
        }
        return buffer.toString();
    }
}
