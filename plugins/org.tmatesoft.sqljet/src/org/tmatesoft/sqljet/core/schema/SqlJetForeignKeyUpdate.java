/**
 * SqlJetForeignKeyUpdate.java
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
 * Foreign key's update.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public enum SqlJetForeignKeyUpdate {
    SET_NULL, SET_DEFAULT, CASCADE, RESTRICT;

    public static SqlJetForeignKeyUpdate decode(String s) {
        if ("null".equalsIgnoreCase(s)) {
            return SET_NULL;
        } else if ("default".equalsIgnoreCase(s)) {
            return SET_DEFAULT;
        } else if ("cascade".equalsIgnoreCase(s)) {
            return CASCADE;
        } else if ("restrict".equalsIgnoreCase(s)) {
            return RESTRICT;
        }
        return null;
    }

    public String toString() {
        switch (this) {
        case SET_NULL:
            return "SET NULL";
        case SET_DEFAULT:
            return "SET DEFAULT";
        case CASCADE:
            return "CASCADE";
        case RESTRICT:
            return "RESTRICT";
        }
        return "";
    }
}
