/**
 * SqlJetForeignKeyEvent.java
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
package org.tmatesoft.sqljet.core.schema;

/**
 * Foreign key's event.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public enum SqlJetForeignKeyEvent {
    DELETE, UPDATE, INSERT;

    public static SqlJetForeignKeyEvent decode(String s) {
        if ("delete".equalsIgnoreCase(s)) {
            return DELETE;
        } else if ("update".equalsIgnoreCase(s)) {
            return UPDATE;
        } else if ("insert".equalsIgnoreCase(s)) {
            return INSERT;
        }
        return null;
    }

    public String toString() {
        switch (this) {
        case DELETE:
            return "DELETE";
        case UPDATE:
            return "UPDATE";
        case INSERT:
            return "INSERT";
        }
        return "";
    }
}
