/**
 * SqlJetConsole.java
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
package org.tmatesoft.sqljet.core.internal.lang;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.schema.SqlJetBlobLiteral;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetConsole implements SqlJetExecCallback {

    private final String fileName;
    private boolean firstRow;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            println("Exactly one database file name should be specified.");
            return;
        }
        SqlJetConnection conn = SqlJetConnection.open(args[0]);
        println("SQLJet version 1.0");
        println("Enter \".help\" for instructions");
        try {
            new SqlJetConsole(args[0]).repl(conn);
        } finally {
            conn.close();
        }
    }

    private SqlJetConsole(String fileName) {
        this.fileName = fileName;
    }

    private void repl(SqlJetConnection conn) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        StringBuffer buffer = new StringBuffer();
        String line;
        print("sqljet> ");
        while ((line = in.readLine()) != null) {
            buffer.append(line);
            String cmd = buffer.toString().trim().toLowerCase();
            if (".help".equals(cmd)) {
                printHelp();
            } else if (".databases".equals(cmd)) {
                printDatabases();
            } else if (".schema".equals(cmd)) {
                printSchema(conn);
            } else if (".exit".equals(cmd) || ".quit".equals(cmd)) {
                System.exit(0);
            } else {
                if (line.trim().endsWith(";")) {
                    firstRow = true;
                    try {
                        conn.exec(buffer.toString(), this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    print("   ...> ");
                    continue;
                }
            }
            buffer.setLength(0);
            print("sqljet> ");
        }
    }

    public void processRow(SqlJetPreparedStatement stmt) throws SqlJetException {
        StringBuffer buffer = new StringBuffer();
        if (firstRow) {
            for (int i = 0; i < 80; i++) {
                buffer.append('-');
            }
            println(buffer.toString());
            buffer.setLength(0);
            firstRow = false;
        }
        for (int i = 0; i < stmt.getColumnsCount(); i++) {
            if (i > 0) {
                buffer.append("|");
            }
            switch (stmt.getColumnType(i)) {
            case INTEGER:
                buffer.append(stmt.getInteger(i));
                break;
            case FLOAT:
                buffer.append(stmt.getFloat(i));
                break;
            case TEXT:
                buffer.append(String.valueOf(stmt.getText(i)));
                break;
            case BLOB:
                buffer.append(asBlob(stmt.getBlobAsArray(i)));
                break;
            case NULL:
                buffer.append("NULL");
                break;
            }
        }
        println(buffer.toString());
    }

    private String asBlob(byte[] data) {
        if (data == null) {
            return "NULL";
        }
        return SqlJetBlobLiteral.asBlob(data);
    }

    private void printHelp() {
        println(".databases             List names and files of attached databases");
        println(".exit                  Exit this program");
        println(".help                  Show this message");
        println(".quit                  Exit this program");
        println(".schema ?TABLE?        Show the CREATE statements");
    }

    private void printDatabases() {
        println(fileName);
    }

    private void printSchema(SqlJetConnection conn) throws SqlJetException {
        println(conn.getSchema("main").toString());
    }

    private static void print(String s) {
        System.out.print(s);
    }

    private static void println(String s) {
        System.out.println(s);
    }
}
