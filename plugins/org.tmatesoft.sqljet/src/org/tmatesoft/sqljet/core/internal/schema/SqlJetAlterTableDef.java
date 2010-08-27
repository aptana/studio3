/**
 * SqlJetAlterTableDef.java
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

import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.tree.CommonTree;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDef;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetAlterTableDef {

    private static final String INVALID_ALTER_TABLE_STATEMENT = "Invalid ALTER TABLE statement";

    private final String tableName;
    private final String newTableName;
    private final ISqlJetColumnDef newColumnDef;
    
    private ParserRuleReturnScope parsedSql;

    /**
     * @param ast
     * @throws SqlJetException
     */
    public SqlJetAlterTableDef(ParserRuleReturnScope parsedSql) throws SqlJetException {
        this.parsedSql = parsedSql;
        final CommonTree ast = (CommonTree)parsedSql.getTree();
        final int childCount = ast.getChildCount();
        if (childCount < 5) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, INVALID_ALTER_TABLE_STATEMENT);
        }
        final CommonTree alterNode = (CommonTree) ast.getChild(0);
        final CommonTree tableNode = (CommonTree) ast.getChild(1);
        if (!"alter".equalsIgnoreCase(alterNode.getText()) || !"table".equalsIgnoreCase(tableNode.getText())) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, INVALID_ALTER_TABLE_STATEMENT);
        }
        final CommonTree tableNameNode = (CommonTree) ast.getChild(2);
        tableName = tableNameNode.getText();
        final CommonTree actionNode = (CommonTree) ast.getChild(3);
        final String action = actionNode.getText();
        final CommonTree child = (CommonTree) ast.getChild(4);
        if ("add".equalsIgnoreCase(action)) {
            newTableName = null;
            final CommonTree newColumnNode;
            if ("column".equalsIgnoreCase(child.getText())) {
                if (childCount != 6) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE, INVALID_ALTER_TABLE_STATEMENT);
                }
                newColumnNode = (CommonTree) ast.getChild(5);
            } else {
                if (childCount != 5) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE, INVALID_ALTER_TABLE_STATEMENT);
                }
                newColumnNode = child;
            }
            newColumnDef = new SqlJetColumnDef(newColumnNode);
        } else if ("rename".equalsIgnoreCase(action)) {
            newColumnDef = null;
            assert ("to".equalsIgnoreCase(child.getText()));
            if (childCount < 6) {
                throw new SqlJetException(SqlJetErrorCode.MISUSE, INVALID_ALTER_TABLE_STATEMENT);
            }
            final CommonTree newTableNode = (CommonTree) ast.getChild(5);
            newTableName = newTableNode.getText();
        } else {
            newTableName = null;
            newColumnDef = null;
            throw new SqlJetException(SqlJetErrorCode.MISUSE, INVALID_ALTER_TABLE_STATEMENT);
        }
    }

    /**
     * @return
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return
     */
    public String getNewTableName() {
        return newTableName;
    }

    /**
     * @return
     */
    public ISqlJetColumnDef getNewColumnDef() {
        return newColumnDef;
    }

    /**
     * @return the parsedSql
     */
    public ParserRuleReturnScope getParsedSql() {
        return parsedSql;
    }
    
}
