/**
 * SqlJetColumnNull.java
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
 *
 * For information on how to redistribute this software under
 * the terms of a license other than GNU General Public License
 * contact TMate Software at support@sqljet.com
 */
package org.tmatesoft.sqljet.core.internal.schema;

import org.antlr.runtime.tree.CommonTree;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public class SqlJetColumnNull extends SqlJetColumnConstraint {

    public SqlJetColumnNull(SqlJetColumnDef column, String name, CommonTree ast) {
        super(column, name);
        assert "is_null".equalsIgnoreCase(ast.getText());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        if (buffer.length() > 0) {
            buffer.append(' ');
        }
        buffer.append("NULL");
        return buffer.toString();
    }

}
