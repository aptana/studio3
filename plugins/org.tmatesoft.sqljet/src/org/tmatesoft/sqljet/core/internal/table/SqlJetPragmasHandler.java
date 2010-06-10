/**
 * SqlJetPragmasHandler.java
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
package org.tmatesoft.sqljet.core.internal.table;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.lang.SqlLexer;
import org.tmatesoft.sqljet.core.internal.lang.SqlParser;
import org.tmatesoft.sqljet.core.table.ISqlJetOptions;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetPragmasHandler {

    private final ISqlJetOptions options;

    public SqlJetPragmasHandler(ISqlJetOptions options) {
        this.options = options;
    }

    private ISqlJetOptions getOptions() {
        return options;
    }

    /**
     * Executes pragma statement. If statement queries pragma value then it will
     * be returned.
     */
    public Object pragma(String sql) throws SqlJetException {
        return pragma(parsePragma(sql));
    }

    public Object pragma(CommonTree ast) throws SqlJetException {
        assert "pragma".equalsIgnoreCase(ast.getText());
        String name = ast.getChild(0).getText();
        // String database = "main";
        // if (ast.getChild(0).getChildCount() > 0) {
        // database = ast.getChild(0).getChild(0).getText();
        // }
        // TODO: use the specified database where appropriate
        if (ast.getChildCount() > 1) {
            // set or execute
            Object value = readPragmaValue(ast.getChild(1));
            if ("auto_vacuum".equals(name)) {
                int mode = readAutovacuumMode(value);
                getOptions().setAutovacuum(mode == 1);
                getOptions().setIncrementalVacuum(mode == 2);
            } else if ("cache_size".equals(name)) {
                if (value instanceof Number) {
                    getOptions().setCacheSize(((Number) value).intValue());
                } else {
                    throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid cache_size value: " + value);
                }
            } else if ("encoding".equals(name)) {
                if (value instanceof String) {
                    SqlJetEncoding enc = SqlJetEncoding.decode((String) value);
                    if (enc != null) {
                        getOptions().setEncoding(enc);
                    } else {
                        throw new SqlJetException(SqlJetErrorCode.ERROR, "Unknown encoding: " + value);
                    }
                } else {
                    throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid encoding value: " + value);
                }
            } else if ("legacy_file_format".equals(name)) {
                getOptions().setLegacyFileFormat(toBooleanValue(value));
            } else if ("schema_version".equals(name)) {
                if (value instanceof Number) {
                    int version = ((Number) value).intValue();
                    getOptions().setSchemaVersion(version);
                } else {
                    throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid schema_version value: " + value);
                }
            } else if ("user_version".equals(name)) {
                if (value instanceof Number) {
                    int version = ((Number) value).intValue();
                    getOptions().setUserVersion(version);
                } else {
                    throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid user_version value: " + value);
                }
            }
            return null;
        } else {
            // get value
            if ("auto_vacuum".equals(name)) {
                int mode = 0;
                if (getOptions().isAutovacuum()) {
                    mode = 1;
                }
                if (getOptions().isIncrementalVacuum()) {
                    mode = 2;
                }
                return Integer.valueOf(mode);
            } else if ("cache_size".equals(name)) {
                return Integer.valueOf(getOptions().getCacheSize());
            } else if ("encoding".equals(name)) {
                return getOptions().getEncoding();
            } else if ("legacy_file_format".equals(name)) {
                return getOptions().isLegacyFileFormat();
            } else if ("schema_version".equals(name)) {
                return Integer.valueOf(getOptions().getSchemaVersion());
            } else if ("user_version".equals(name)) {
                return Integer.valueOf(getOptions().getUserVersion());
            }
            return null;
        }
    }

    private int readAutovacuumMode(Object value) throws SqlJetException {
        int mode = -1;
        if (value instanceof String) {
            String s = ((String) value).toLowerCase();
            if ("none".equals(s)) {
                mode = 0;
            } else if ("full".equals(s)) {
                mode = 1;
            } else if ("incremental".equals(s)) {
                mode = 2;
            }
        } else if (value instanceof Number) {
            int i = ((Number) value).intValue();
            if (i == 0 || i == 1 || i == 2) {
                mode = i;
            }
        }
        if (mode < 0) {
            throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid auto_vacuum value: " + value);
        }
        return mode;
    }

    private CommonTree parsePragma(String sql) throws SqlJetException {
        try {
            CharStream chars = new ANTLRStringStream(sql);
            SqlLexer lexer = new SqlLexer(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SqlParser parser = new SqlParser(tokens);
            return (CommonTree) parser.pragma_stmt().getTree();
        } catch (RecognitionException re) {
            throw new SqlJetException(SqlJetErrorCode.ERROR, "Invalid sql statement: " + sql);
        }
    }

    private Object readPragmaValue(Tree node) {
        String type = node.getText().toLowerCase();
        String value = node.getChild(0).getText();
        if ("float_literal".equals(type)) {
            return Double.valueOf(value);
        } else if ("id_literal".equals(type)) {
            return value;
        } else if ("string_literal".equals(type)) {
            return value.substring(1, value.length() - 1);
        }
        throw new IllegalStateException();
    }

    protected boolean toBooleanValue(Object value) throws SqlJetException {
        if (value instanceof Number) {
            int i = ((Number) value).intValue();
            if (i == 0) {
                return false;
            } else if (i == 1) {
                return true;
            }
        } else if (value instanceof String) {
            String s = ((String) value).toLowerCase();
            if ("yes".equals(s) || "true".equals(s) || "on".equals(s)) {
                return true;
            } else if ("no".equals(s) || "false".equals(s) || "off".equals(s)) {
                return false;
            }
        }
        throw new SqlJetException(SqlJetErrorCode.ERROR, "Boolean value is expected.");
    }

    protected Object toResult(boolean value) {
        return value ? Integer.valueOf(1) : Integer.valueOf(0);
    }
}
