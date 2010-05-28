/**
 * SqlJetSchema.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.CommonTree;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.SqlJetBtreeTableCreateFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.lang.SqlLexer;
import org.tmatesoft.sqljet.core.internal.lang.SqlParser;
import org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeDataTable;
import org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable;
import org.tmatesoft.sqljet.core.internal.table.SqlJetBtreeDataTable;
import org.tmatesoft.sqljet.core.internal.table.SqlJetBtreeIndexTable;
import org.tmatesoft.sqljet.core.internal.table.SqlJetBtreeSchemaTable;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnConstraint;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDefault;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnNotNull;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnPrimaryKey;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnUnique;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexedColumn;
import org.tmatesoft.sqljet.core.schema.ISqlJetSchema;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableConstraint;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTablePrimaryKey;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableUnique;
import org.tmatesoft.sqljet.core.schema.ISqlJetVirtualTableDef;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetSchema implements ISqlJetSchema {

    private static String AUTOINDEX = "sqlite_autoindex_%s_%d";

    private static final String CANT_DELETE_IMPLICIT_INDEX = "Can't delete implicit index \"%s\"";

    private static final String CREATE_TABLE_SQLITE_SEQUENCE = "CREATE TABLE sqlite_sequence(name,seq)";

    private static final String SQLITE_SEQUENCE = "SQLITE_SEQUENCE";

    private static final Set<SqlJetBtreeTableCreateFlags> BTREE_CREATE_TABLE_FLAGS = SqlJetUtility.of(
            SqlJetBtreeTableCreateFlags.INTKEY, SqlJetBtreeTableCreateFlags.LEAFDATA);

    private static final Set<SqlJetBtreeTableCreateFlags> BTREE_CREATE_INDEX_FLAGS = SqlJetUtility
            .of(SqlJetBtreeTableCreateFlags.ZERODATA);

    private static final String TABLE_TYPE = "table";
    private static final String INDEX_TYPE = "index";

    private final ISqlJetDbHandle db;
    private final ISqlJetBtree btree;

    private final Map<String, ISqlJetTableDef> tableDefs = new TreeMap<String, ISqlJetTableDef>(
            String.CASE_INSENSITIVE_ORDER);
    private final Map<String, ISqlJetIndexDef> indexDefs = new TreeMap<String, ISqlJetIndexDef>(
            String.CASE_INSENSITIVE_ORDER);
    private Map<String, ISqlJetVirtualTableDef> virtualTableDefs = new TreeMap<String, ISqlJetVirtualTableDef>(
            String.CASE_INSENSITIVE_ORDER);

    public SqlJetSchema(ISqlJetDbHandle db, ISqlJetBtree btree) throws SqlJetException {
        this.db = db;
        this.btree = btree;
        init();
    }

    private ISqlJetBtreeSchemaTable openSchemaTable(boolean write) throws SqlJetException {
        return new SqlJetBtreeSchemaTable(btree, write);
    }

    private void init() throws SqlJetException {
        if (db.getOptions().getSchemaVersion() == 0)
            return;
        final ISqlJetBtreeSchemaTable table = openSchemaTable(false);
        try {
            table.lock();
            try {
                readShema(table);
            } finally {
                table.unlock();
            }
        } finally {
            table.close();
        }
    }

    public ISqlJetDbHandle getDb() {
        return db;
    }

    public ISqlJetBtree getBtree() {
        return btree;
    }

    public Set<String> getTableNames() throws SqlJetException {
        db.getMutex().enter();
        try {
            final Set<String> s = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            s.addAll(tableDefs.keySet());
            return s;
        } finally {
            db.getMutex().leave();
        }
    }

    public ISqlJetTableDef getTable(String name) throws SqlJetException {
        db.getMutex().enter();
        try {
            return tableDefs.get(name);
        } finally {
            db.getMutex().leave();
        }
    }

    public Set<String> getIndexNames() throws SqlJetException {
        db.getMutex().enter();
        try {
            final Set<String> s = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            s.addAll(indexDefs.keySet());
            return s;
        } finally {
            db.getMutex().leave();
        }
    }

    public ISqlJetIndexDef getIndex(String name) throws SqlJetException {
        db.getMutex().enter();
        try {
            return indexDefs.get(name);
        } finally {
            db.getMutex().leave();
        }
    }

    public Set<ISqlJetIndexDef> getIndexes(String tableName) throws SqlJetException {
        db.getMutex().enter();
        try {
            Set<ISqlJetIndexDef> result = new HashSet<ISqlJetIndexDef>();
            for (ISqlJetIndexDef index : indexDefs.values()) {
                if (index.getTableName().equals(tableName)) {
                    result.add(index);
                }
            }
            return Collections.unmodifiableSet(result);
        } finally {
            db.getMutex().leave();
        }
    }

    public Set<String> getVirtualTableNames() throws SqlJetException {
        db.getMutex().enter();
        try {
            final Set<String> s = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            s.addAll(virtualTableDefs.keySet());
            return s;
        } finally {
            db.getMutex().leave();
        }
    }

    public ISqlJetVirtualTableDef getVirtualTable(String name) throws SqlJetException {
        db.getMutex().enter();
        try {
            return virtualTableDefs.get(name);
        } finally {
            db.getMutex().leave();
        }
    }

    private void readShema(ISqlJetBtreeSchemaTable table) throws SqlJetException {
        for (table.first(); !table.eof(); table.next()) {
            final String type = table.getTypeField();
            if (null == type) {
                continue;
            }
            final String name = table.getNameField();
            if (null == name) {
                continue;
            }
            final int page = table.getPageField();

            if (TABLE_TYPE.equals(type)) {
                String sql = table.getSqlField();
                // System.err.println(sql);
                final CommonTree ast = (CommonTree) parseTable(sql).getTree();
                if (!isCreateVirtualTable(ast)) {
                    final SqlJetTableDef tableDef = new SqlJetTableDef(ast, page);
                    if (!name.equals(tableDef.getName())) {
                        throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                    }
                    tableDef.setRowId(table.getRowId());
                    tableDefs.put(name, tableDef);
                } else {
                    final SqlJetVirtualTableDef virtualTableDef = new SqlJetVirtualTableDef(ast, page);
                    if (!name.equals(virtualTableDef.getTableName())) {
                        throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                    }
                    virtualTableDef.setRowId(table.getRowId());
                    virtualTableDefs.put(name, virtualTableDef);
                }
            } else if (INDEX_TYPE.equals(type)) {
                final String tableName = table.getTableField();
                if (null == type) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                final String sql = table.getSqlField();
                if (null != sql) {
                    // System.err.println(sql);
                    final CommonTree ast = (CommonTree) parseIndex(sql).getTree();
                    final SqlJetIndexDef indexDef = new SqlJetIndexDef(ast, page);
                    if (!name.equals(indexDef.getName())) {
                        throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                    }
                    if (!tableName.equals(indexDef.getTableName())) {
                        throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                    }
                    indexDef.setRowId(table.getRowId());
                    indexDefs.put(name, indexDef);
                } else {
                    SqlJetBaseIndexDef indexDef = new SqlJetBaseIndexDef(name, tableName, page);
                    indexDef.setRowId(table.getRowId());
                    indexDefs.put(name, indexDef);
                }
            }
        }
    }

    /**
     * @param ast
     * @return
     */
    private boolean isCreateVirtualTable(CommonTree ast) {
        final CommonTree optionsNode = (CommonTree) ast.getChild(0);
        for (int i = 0; i < optionsNode.getChildCount(); i++) {
            CommonTree optionNode = (CommonTree) optionsNode.getChild(i);
            if ("virtual".equalsIgnoreCase(optionNode.getText())) {
                return true;
            }
        }
        return false;
    }

    private RuleReturnScope parseTable(String sql) throws SqlJetException {
        try {
            CharStream chars = new ANTLRStringStream(sql);
            SqlLexer lexer = new SqlLexer(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SqlParser parser = new SqlParser(tokens);
            return parser.schema_create_table_stmt();
        } catch (RecognitionException re) {
            throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid sql statement: " + sql);
        }
    }

    private ParserRuleReturnScope parseIndex(String sql) throws SqlJetException {
        try {
            CharStream chars = new ANTLRStringStream(sql);
            SqlLexer lexer = new SqlLexer(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SqlParser parser = new SqlParser(tokens);
            return parser.create_index_stmt();
        } catch (RecognitionException re) {
            throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid sql statement: " + sql);
        }
    }

    @Override
    public String toString() {
        db.getMutex().enter();
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Tables:\n");
            for (ISqlJetTableDef tableDef : tableDefs.values()) {
                buffer.append(tableDef.toString());
                buffer.append('\n');
            }
            buffer.append("Indexes:\n");
            for (ISqlJetIndexDef indexDef : indexDefs.values()) {
                buffer.append(indexDef.toString());
                buffer.append('\n');
            }
            return buffer.toString();
        } finally {
            db.getMutex().leave();
        }
    }

    public ISqlJetTableDef createTable(String sql) throws SqlJetException {
        db.getMutex().enter();
        try {
            return createTableSafe(sql);
        } finally {
            db.getMutex().leave();
        }
    }

    private ISqlJetTableDef createTableSafe(String sql) throws SqlJetException {

        final RuleReturnScope parseTable = parseTable(sql);
        final CommonTree ast = (CommonTree) parseTable.getTree();

        if (isCreateVirtualTable(ast)) {
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        }

        final SqlJetTableDef tableDef = new SqlJetTableDef(ast, 0);
        if (null == tableDef.getName())
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        final String tableName = tableDef.getName();
        if ("".equals(tableName))
            throw new SqlJetException(SqlJetErrorCode.ERROR);

        if (tableDefs.containsKey(tableName)) {
            if (tableDef.isKeepExisting()) {
                return tableDefs.get(tableName);
            } else {
                throw new SqlJetException(SqlJetErrorCode.ERROR, "Table \"" + tableName + "\" exists already");
            }
        }

        final List<ISqlJetColumnDef> columns = tableDef.getColumns();
        if (null == columns || 0 == columns.size())
            throw new SqlJetException(SqlJetErrorCode.ERROR);

        final String createTableSql = getCreateTableSql(parseTable);

        final ISqlJetBtreeSchemaTable schemaTable = openSchemaTable(true);

        try {

            schemaTable.lock();

            try {

                db.getOptions().changeSchemaVersion();

                final int page = btree.createTable(BTREE_CREATE_TABLE_FLAGS);
                final long rowId = schemaTable.insertRecord(TABLE_TYPE, tableName, tableName, page, createTableSql);

                addConstraints(schemaTable, tableDef);

                tableDef.setPage(page);
                tableDef.setRowId(rowId);
                tableDefs.put(tableName, tableDef);
                return tableDef;

            } finally {
                schemaTable.unlock();
            }

        } finally {
            schemaTable.close();
        }

    }

    /**
     * @param parseTable
     * @return
     */
    private String getCreateTableSql(RuleReturnScope parseTable) {
        return String.format("CREATE TABLE %s", getCoreSQL(parseTable));
    }

    /**
     * @param parseIndex
     * @return
     */
    private String getCreateIndexSql(RuleReturnScope parseIndex) {
        return String.format("CREATE INDEX %s", getCoreSQL(parseIndex));
    }

    /**
     * @param parseIndex
     * @return
     */
    private String getCreateIndexUniqueSql(RuleReturnScope parseIndex) {
        return String.format("CREATE UNIQUE INDEX %s", getCoreSQL(parseIndex));
    }

    /**
     * @param parseTable
     * @return
     */
    private String getCreateVirtualTableSql(RuleReturnScope parseTable) {
        return String.format("CREATE VIRTUAL TABLE %s", getCoreSQL(parseTable));
    }

    private String getCoreSQL(RuleReturnScope parsedSQL) {
        final CommonTree ast = (CommonTree) parsedSQL.getTree();
        final CommonToken nameToken = (CommonToken) ((CommonTree) ast.getChild(1)).getToken();
        final CharStream inputStream = nameToken.getInputStream();
        final CommonToken stopToken = (CommonToken) parsedSQL.getStop();
        return inputStream.substring(nameToken.getStartIndex(), stopToken.getStopIndex());
    }

    /**
     * @param i
     * @return
     */
    private String generateAutoIndexName(String tableName, int i) {
        return String.format(AUTOINDEX, tableName, i);
    }

    /**
     * @param schemaTable
     * @param tableDef
     * @throws SqlJetException
     */
    private void addConstraints(ISqlJetBtreeSchemaTable schemaTable, final SqlJetTableDef tableDef)
            throws SqlJetException {

        final String tableName = tableDef.getName();
        final List<ISqlJetColumnDef> columns = tableDef.getColumns();
        int i = 0;

        for (final ISqlJetColumnDef column : columns) {
            final List<ISqlJetColumnConstraint> constraints = column.getConstraints();
            if (null == constraints)
                continue;
            for (final ISqlJetColumnConstraint constraint : constraints) {
                if (constraint instanceof ISqlJetColumnPrimaryKey) {
                    final ISqlJetColumnPrimaryKey pk = (ISqlJetColumnPrimaryKey) constraint;
                    if (!column.hasExactlyIntegerType()) {
                        if (pk.isAutoincremented()) {
                            throw new SqlJetException(SqlJetErrorCode.ERROR,
                                    "AUTOINCREMENT is allowed only for INTEGER PRIMARY KEY fields");
                        }
                        createAutoIndex(schemaTable, tableName, generateAutoIndexName(tableName, ++i));
                    } else if (pk.isAutoincremented()) {
                        checkSequenceTable();
                    }
                } else if (constraint instanceof ISqlJetColumnUnique) {
                    createAutoIndex(schemaTable, tableName, generateAutoIndexName(tableName, ++i));
                }
            }
        }

        final List<ISqlJetTableConstraint> constraints = tableDef.getConstraints();
        if (null != constraints) {
            for (final ISqlJetTableConstraint constraint : constraints) {
                if (constraint instanceof ISqlJetTablePrimaryKey) {
                    boolean b = false;
                    final ISqlJetTablePrimaryKey pk = (ISqlJetTablePrimaryKey) constraint;
                    if (pk.getColumns().size() == 1) {
                        final String n = pk.getColumns().get(0);
                        final ISqlJetColumnDef c = tableDef.getColumn(n);
                        b = c != null && c.hasExactlyIntegerType();
                    }
                    if (!b) {
                        createAutoIndex(schemaTable, tableName, generateAutoIndexName(tableName, ++i));
                    }
                } else if (constraint instanceof ISqlJetTableUnique) {
                    createAutoIndex(schemaTable, tableName, generateAutoIndexName(tableName, ++i));
                }
            }
        }
    }

    /**
     * @param schemaTable
     * @throws SqlJetException
     */
    private void checkSequenceTable() throws SqlJetException {
        if (!tableDefs.containsKey(SQLITE_SEQUENCE)) {
            createTableSafe(CREATE_TABLE_SQLITE_SEQUENCE);
        }
    }

    /**
     * @throws SqlJetException
     */
    public ISqlJetBtreeDataTable openSequenceTable() throws SqlJetException {
        if (tableDefs.containsKey(SQLITE_SEQUENCE)) {
            return new SqlJetBtreeDataTable(btree, SQLITE_SEQUENCE, true);
        } else {
            return null;
        }
    }

    /**
     * @param schemaTable
     * @param generateAutoIndexName
     * 
     * @throws SqlJetException
     */
    private void createAutoIndex(ISqlJetBtreeSchemaTable schemaTable, String tableName, String autoIndexName)
            throws SqlJetException {
        final int page = btree.createTable(BTREE_CREATE_INDEX_FLAGS);
        final SqlJetBaseIndexDef indexDef = new SqlJetBaseIndexDef(autoIndexName, tableName, page);
        indexDef.setRowId(schemaTable.insertRecord(INDEX_TYPE, autoIndexName, tableName, page, null));
        indexDefs.put(autoIndexName, indexDef);
    }

    public ISqlJetIndexDef createIndex(String sql) throws SqlJetException {
        db.getMutex().enter();
        try {
            return createIndexSafe(sql);
        } finally {
            db.getMutex().leave();
        }
    }

    private ISqlJetIndexDef createIndexSafe(String sql) throws SqlJetException {

        final ParserRuleReturnScope parseIndex = parseIndex(sql);
        final CommonTree ast = (CommonTree) parseIndex.getTree();

        final SqlJetIndexDef indexDef = new SqlJetIndexDef(ast, 0);

        if (null == indexDef.getName())
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        final String indexName = indexDef.getName();
        if ("".equals(indexName))
            throw new SqlJetException(SqlJetErrorCode.ERROR);

        if (indexDefs.containsKey(indexName)) {
            if (indexDef.isKeepExisting()) {
                return indexDefs.get(indexName);
            } else {
                throw new SqlJetException(SqlJetErrorCode.ERROR, "Index \"" + indexName + "\" exists already");
            }
        }

        if (null == indexDef.getTableName())
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        final String tableName = indexDef.getTableName();
        if ("".equals(tableName))
            throw new SqlJetException(SqlJetErrorCode.ERROR);

        final List<ISqlJetIndexedColumn> columns = indexDef.getColumns();
        if (null == columns)
            throw new SqlJetException(SqlJetErrorCode.ERROR);

        final ISqlJetTableDef tableDef = getTable(tableName);
        if (null == tableDef)
            throw new SqlJetException(SqlJetErrorCode.ERROR);

        for (final ISqlJetIndexedColumn column : columns) {
            if (null == column.getName())
                throw new SqlJetException(SqlJetErrorCode.ERROR);
            final String columnName = column.getName();
            if ("".equals(columnName))
                throw new SqlJetException(SqlJetErrorCode.ERROR);
            if (null == tableDef.getColumn(columnName))
                throw new SqlJetException(SqlJetErrorCode.ERROR, "Column \"" + columnName + "\" not found in table \""
                        + tableName + "\"");
        }

        final ISqlJetBtreeSchemaTable schemaTable = openSchemaTable(true);
        final String createIndexSQL = indexDef.isUnique() ? getCreateIndexUniqueSql(parseIndex) : getCreateIndexSql(parseIndex);

        try {

            schemaTable.lock();

            try {

                db.getOptions().changeSchemaVersion();

                final int page = btree.createTable(BTREE_CREATE_INDEX_FLAGS);

                final long rowId = schemaTable.insertRecord(INDEX_TYPE, indexName, tableName, page, createIndexSQL);

                indexDef.setPage(page);
                indexDef.setRowId(rowId);
                indexDefs.put(indexName, indexDef);

                final SqlJetBtreeIndexTable indexTable = new SqlJetBtreeIndexTable(btree, indexDef.getName(), true);
                try {
                    indexTable.reindex(this);
                } finally {
                    indexTable.close();
                }
                return indexDef;

            } finally {
                schemaTable.unlock();
            }

        } finally {
            schemaTable.close();
        }
    }

    public void dropTable(String tableName) throws SqlJetException {
        db.getMutex().enter();
        try {
            dropTableSafe(tableName);
        } finally {
            db.getMutex().leave();
        }
    }

    private void dropTableSafe(String tableName) throws SqlJetException {

        if (null == tableName || "".equals(tableName))
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Table name must be not empty");

        if (!tableDefs.containsKey(tableName))
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Table not found: " + tableName);
        final SqlJetTableDef tableDef = (SqlJetTableDef) tableDefs.get(tableName);

        dropTableIndexes(tableDef);

        final ISqlJetBtreeSchemaTable schemaTable = openSchemaTable(true);

        try {

            schemaTable.lock();

            try {

                db.getOptions().changeSchemaVersion();

                if (!schemaTable.goToRow(tableDef.getRowId()) || !TABLE_TYPE.equals(schemaTable.getTypeField()))
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                final String n = schemaTable.getNameField();
                if (null == n || !tableName.equals(n))
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                schemaTable.delete();

            } finally {
                schemaTable.unlock();
            }

        } finally {
            schemaTable.close();
        }

        final int page = tableDef.getPage();
        final int moved = btree.dropTable(page);
        if (moved != 0) {
            movePage(page, moved);
        }

        tableDefs.remove(tableName);

    }

    /**
     * @param schemaTable
     * @param tableDef
     * @throws SqlJetException
     */
    private void dropTableIndexes(SqlJetTableDef tableDef) throws SqlJetException {
        final String tableName = tableDef.getName();
        final Iterator<Map.Entry<String, ISqlJetIndexDef>> iterator = indexDefs.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, ISqlJetIndexDef> indexDefEntry = iterator.next();
            final String indexName = indexDefEntry.getKey();
            final ISqlJetIndexDef indexDef = indexDefEntry.getValue();
            if (indexDef.getTableName().equals(tableName)) {
                if (doDropIndex(indexName, true, false)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * @param schemaTable
     * @param name
     * @param generateAutoIndexName
     * @throws SqlJetException
     */
    private boolean doDropIndex(String indexName, boolean allowAutoIndex, boolean throwIfFial) throws SqlJetException {

        if (!indexDefs.containsKey(indexName)) {
            if (throwIfFial)
                throw new SqlJetException(SqlJetErrorCode.MISUSE);
            return false;
        }
        final SqlJetBaseIndexDef indexDef = (SqlJetBaseIndexDef) indexDefs.get(indexName);

        if (!allowAutoIndex && indexDef.isImplicit()) {
            if (throwIfFial)
                throw new SqlJetException(SqlJetErrorCode.MISUSE, String.format(CANT_DELETE_IMPLICIT_INDEX, indexName));
            return false;
        }

        final ISqlJetBtreeSchemaTable schemaTable = openSchemaTable(true);

        try {

            schemaTable.lock();

            try {

                if (!schemaTable.goToRow(indexDef.getRowId()) || !INDEX_TYPE.equals(schemaTable.getTypeField())) {
                    if (throwIfFial)
                        throw new SqlJetException(SqlJetErrorCode.INTERNAL);
                    return false;
                }
                final String n = schemaTable.getNameField();
                if (null == n || !indexName.equals(n)) {
                    if (throwIfFial)
                        throw new SqlJetException(SqlJetErrorCode.INTERNAL);
                    return false;
                }

                if (!allowAutoIndex && schemaTable.isNull(ISqlJetBtreeSchemaTable.SQL_FIELD)) {
                    if (throwIfFial)
                        throw new SqlJetException(SqlJetErrorCode.MISUSE, String.format(CANT_DELETE_IMPLICIT_INDEX,
                                indexName));
                    return false;
                }

                schemaTable.delete();

            } finally {
                schemaTable.unlock();
            }

        } finally {
            schemaTable.close();
        }

        final int page = indexDef.getPage();
        final int moved = btree.dropTable(page);
        if (moved != 0) {
            movePage(page, moved);
        }

        return true;

    }

    /**
     * @param page
     * @param moved
     * @throws SqlJetException
     */
    private void movePage(final int page, final int moved) throws SqlJetException {
        final ISqlJetBtreeSchemaTable schemaTable = openSchemaTable(true);
        try {
            schemaTable.lock();
            try {
                for (schemaTable.first(); !schemaTable.eof(); schemaTable.next()) {
                    final long pageField = schemaTable.getPageField();
                    if (pageField == moved) {
                        final String nameField = schemaTable.getNameField();
                        schemaTable.updateRecord(schemaTable.getRowId(), schemaTable.getTypeField(), nameField,
                                schemaTable.getTableField(), page, schemaTable.getSqlField());
                        final ISqlJetIndexDef index = getIndex(nameField);
                        if (index != null) {
                            if (index instanceof SqlJetBaseIndexDef) {
                                ((SqlJetBaseIndexDef) index).setPage(page);
                            }
                        } else {
                            final ISqlJetTableDef table = getTable(nameField);
                            if (table != null) {
                                if (table instanceof SqlJetTableDef) {
                                    ((SqlJetTableDef) table).setPage(page);
                                }
                            }
                        }
                        return;
                    }
                }
            } finally {
                schemaTable.unlock();
            }

        } finally {
            schemaTable.close();
        }
    }

    public void dropIndex(String indexName) throws SqlJetException {
        db.getMutex().enter();
        try {
            dropIndexSafe(indexName);
        } finally {
            db.getMutex().leave();
        }
    }

    private void dropIndexSafe(String indexName) throws SqlJetException {

        if (null == indexName || "".equals(indexName))
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Index name must be not empty");

        if (!indexDefs.containsKey(indexName))
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Index not found: " + indexName);

        if (doDropIndex(indexName, false, true)) {
            db.getOptions().changeSchemaVersion();
            indexDefs.remove(indexName);
        }

    }

    /**
     * @param tableName
     * @param newTableName
     * @param newColumnDef
     * @return
     * @throws SqlJetException
     */
    private ISqlJetTableDef alterTableSafe(final SqlJetAlterTableDef alterTableDef) throws SqlJetException {

        assert (null != alterTableDef);
        String tableName = alterTableDef.getTableName();
        String newTableName = alterTableDef.getNewTableName();
        ISqlJetColumnDef newColumnDef = alterTableDef.getNewColumnDef();

        if (null == tableName) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Table name isn't defined");
        }

        if (null == newTableName && null == newColumnDef) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Not defined any altering");
        }

        boolean renameTable = false;
        if (null != newTableName) {
            renameTable = true;
        } else {
            newTableName = tableName;
        }

        if (renameTable && tableDefs.containsKey(newTableName)) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, String
                    .format("Table \"%s\" already exists", newTableName));
        }

        final SqlJetTableDef tableDef = (SqlJetTableDef) tableDefs.get(tableName);
        if (null == tableDef) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, String.format("Table \"%s\" not found", tableName));
        }

        List<ISqlJetColumnDef> columns = tableDef.getColumns();
        if (null != newColumnDef) {

            final String fieldName = newColumnDef.getName();
            if (tableDef.getColumn(fieldName) != null) {
                throw new SqlJetException(SqlJetErrorCode.MISUSE, String.format(
                        "Field \"%s\" already exists in table \"%s\"", fieldName, tableName));
            }

            final List<ISqlJetColumnConstraint> constraints = newColumnDef.getConstraints();
            if (null != constraints && 0 != constraints.size()) {
                boolean notNull = false;
                boolean defaultValue = false;
                for (final ISqlJetColumnConstraint constraint : constraints) {
                    if (constraint instanceof ISqlJetColumnNotNull) {
                        notNull = true;
                    } else if (constraint instanceof ISqlJetColumnDefault) {
                        defaultValue = true;
                    } else {
                        throw new SqlJetException(SqlJetErrorCode.MISUSE, String.format("Invalid constraint: %s",
                                constraint.toString()));
                    }
                }
                if (notNull && !defaultValue) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE, "NOT NULL requires to have DEFAULT value");
                }
            }

            columns = new ArrayList<ISqlJetColumnDef>(columns);
            columns.add(newColumnDef);
        }

        final int page = tableDef.getPage();
        final long rowId = tableDef.getRowId();

        final SqlJetTableDef alterDef = new SqlJetTableDef(newTableName, null, tableDef.isTemporary(), false, columns,
                tableDef.getConstraints(), page, rowId);

        final ISqlJetBtreeSchemaTable schemaTable = openSchemaTable(true);
        try {
            schemaTable.lock();
            try {

                if (!schemaTable.goToRow(rowId)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }

                final String typeField = schemaTable.getTypeField();
                final String nameField = schemaTable.getNameField();
                final String tableField = schemaTable.getTableField();
                final int pageField = schemaTable.getPageField();

                if (null == typeField || !TABLE_TYPE.equals(typeField)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                if (null == nameField || !tableName.equals(nameField)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                if (null == tableField || !tableName.equals(tableField)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                if (0 == pageField || pageField != page) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }

                final String alteredSql = getTableAlteredSql(schemaTable.getSqlField(), alterTableDef);

                db.getOptions().changeSchemaVersion();

                schemaTable.insertRecord(TABLE_TYPE, newTableName, newTableName, page, alteredSql);

                if (renameTable && !tableName.equals(newTableName)) {
                    renameTablesIndices(schemaTable, tableName, newTableName, getAlterTableName(alterTableDef));
                }

                tableDefs.remove(tableName);
                tableDefs.put(newTableName, alterDef);

                return alterDef;

            } finally {
                schemaTable.unlock();
            }
        } finally {
            schemaTable.close();
        }

    }

    /**
     * @param alterTableDef
     * @return
     */
    private String getAlterTableName(SqlJetAlterTableDef alterTableDef) {
        final ParserRuleReturnScope parsedSql = alterTableDef.getParsedSql();
        final CommonTree ast = (CommonTree) parsedSql.getTree();
        final CommonToken stopToken = (CommonToken) parsedSql.getStop();
        final CommonToken nameToken = (CommonToken) ((CommonTree) ast.getChild(ast.getChildCount()-1)).getToken();
        final CharStream inputStream = nameToken.getInputStream();
        return inputStream.substring(nameToken.getStartIndex(), stopToken.getStopIndex());
    }

    /**
     * @param sql
     * @param alterTableDef
     * @return
     * @throws SqlJetException
     */
    private String getTableAlteredSql(String sql, SqlJetAlterTableDef alterTableDef) throws SqlJetException {

        final RuleReturnScope parsedSQL = parseTable(sql);
        final CommonTree ast = (CommonTree) parsedSQL.getTree();
        final CommonToken nameToken = (CommonToken) ((CommonTree) ast.getChild(1)).getToken();
        final CharStream inputStream = nameToken.getInputStream();
        final CommonToken stopToken = (CommonToken) parsedSQL.getStop();

        final StringBuilder b = new StringBuilder();

        if (alterTableDef.getNewTableName() != null) {
            b.append(inputStream.substring(0, nameToken.getStartIndex() - 1));
            b.append(getAlterTableName(alterTableDef));
            b.append(inputStream.substring(nameToken.getStopIndex() + 1, stopToken.getStopIndex()));
        } else if (alterTableDef.getNewColumnDef() != null) {
            b.append(inputStream.substring(0, stopToken.getStartIndex() - 1));
            b.append(",").append(getAlterTableName(alterTableDef));
            b.append(inputStream.substring(stopToken.getStartIndex(), stopToken.getStopIndex()));
        } else {
            throw new SqlJetException("Wrong ALTER TABLE statement");
        }

        return b.toString();
    }

    /**
     * @param schemaTable
     * @param newTableName
     * @param tableName
     * @param string
     * @throws SqlJetException
     */
    private void renameTablesIndices(final ISqlJetBtreeSchemaTable schemaTable, String tableName, String newTableName,
            String alterTableName) throws SqlJetException {

        final Set<ISqlJetIndexDef> indexes = getIndexes(tableName);
        if (null == indexes || 0 == indexes.size()) {
            return;
        }

        int i = 0;
        for (final ISqlJetIndexDef index : indexes) {
            if (index instanceof SqlJetBaseIndexDef) {

                final SqlJetBaseIndexDef indexDef = (SqlJetBaseIndexDef) index;
                final String indexName = indexDef.getName();
                final long rowId = indexDef.getRowId();
                final int page = indexDef.getPage();

                if (!schemaTable.goToRow(rowId)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }

                final String typeField = schemaTable.getTypeField();
                final String nameField = schemaTable.getNameField();
                final String tableField = schemaTable.getTableField();
                final int pageField = schemaTable.getPageField();

                if (null == typeField || !INDEX_TYPE.equals(typeField)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                if (null == nameField || !indexName.equals(nameField)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                if (null == tableField || !tableName.equals(tableField)) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                if (0 == pageField || pageField != page) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }

                indexDef.setTableName(newTableName);

                String newIndexName = indexName;
                String alteredIndexSql = null;

                if (index.isImplicit()) {
                    newIndexName = generateAutoIndexName(tableName, ++i);
                    indexDef.setName(newIndexName);
                    indexDefs.remove(indexName);
                    indexDefs.put(newIndexName, indexDef);
                } else {
                    alteredIndexSql = getAlteredIndexSql(schemaTable.getSqlField(), alterTableName);
                }

                schemaTable.insertRecord(INDEX_TYPE, newIndexName, newTableName, page, alteredIndexSql);

            } else {
                throw new SqlJetException(SqlJetErrorCode.INTERNAL);
            }
        }

    }

    /**
     * @param sql
     * @param alterTableName
     * @return
     * @throws SqlJetException
     */
    private String getAlteredIndexSql(String sql, String alterTableName) throws SqlJetException {
        final RuleReturnScope parsedSQL = parseIndex(sql);
        final CommonTree ast = (CommonTree) parsedSQL.getTree();
        final CommonToken nameToken = (CommonToken) ((CommonTree) ast.getChild(2)).getToken();
        final CharStream inputStream = nameToken.getInputStream();
        final CommonToken stopToken = (CommonToken) parsedSQL.getStop();
        final StringBuilder b = new StringBuilder();
        b.append(inputStream.substring(0, nameToken.getStartIndex() - 1));
        b.append(alterTableName);
        b.append(inputStream.substring(nameToken.getStopIndex() + 1, stopToken.getStopIndex()));
        return b.toString();
    }

    private ParserRuleReturnScope parseSqlStatement(String sql) throws SqlJetException {
        try {
            CharStream chars = new ANTLRStringStream(sql);
            SqlLexer lexer = new SqlLexer(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SqlParser parser = new SqlParser(tokens);
            return parser.sql_stmt_itself();
        } catch (RecognitionException re) {
            throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid sql statement: " + sql);
        }
    }

    public ISqlJetTableDef alterTable(String sql) throws SqlJetException {

        final SqlJetAlterTableDef alterTableDef = new SqlJetAlterTableDef(parseSqlStatement(sql));
        if (null == alterTableDef) {
            throw new SqlJetException(SqlJetErrorCode.INTERNAL);
        }

        db.getMutex().enter();
        try {
            return alterTableSafe(alterTableDef);
        } finally {
            db.getMutex().leave();
        }

    }

    public ISqlJetVirtualTableDef createVirtualTable(String sql, int page) throws SqlJetException {
        db.getMutex().enter();
        try {
            return createVirtualTableSafe(sql, page);
        } finally {
            db.getMutex().leave();
        }
    }

    private ISqlJetVirtualTableDef createVirtualTableSafe(String sql, int page) throws SqlJetException {

        final RuleReturnScope parseTable = parseTable(sql);
        final CommonTree ast = (CommonTree) parseTable.getTree();

        if (!isCreateVirtualTable(ast)) {
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        }

        final SqlJetVirtualTableDef tableDef = new SqlJetVirtualTableDef(ast, 0);
        if (null == tableDef.getTableName())
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        final String tableName = tableDef.getTableName();
        if ("".equals(tableName))
            throw new SqlJetException(SqlJetErrorCode.ERROR);

        if (virtualTableDefs.containsKey(tableName)) {
            throw new SqlJetException(SqlJetErrorCode.ERROR, "Virtual table \"" + tableName + "\" exists already");
        }

        final ISqlJetBtreeSchemaTable schemaTable = openSchemaTable(true);
        final String createVirtualTableSQL = getCreateVirtualTableSql(parseTable);

        try {

            schemaTable.lock();

            try {

                db.getOptions().changeSchemaVersion();

                final long rowId = schemaTable.insertRecord(TABLE_TYPE, tableName, tableName, page,
                        createVirtualTableSQL);

                tableDef.setPage(page);
                tableDef.setRowId(rowId);
                virtualTableDefs.put(tableName, tableDef);
                return tableDef;

            } finally {
                schemaTable.unlock();
            }

        } finally {
            schemaTable.close();
        }

    }

}
