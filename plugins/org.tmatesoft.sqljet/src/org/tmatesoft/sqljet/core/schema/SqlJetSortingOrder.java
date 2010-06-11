/**
 * SqlJetSortingOrder.java
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
 * Sorting order.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public enum SqlJetSortingOrder {
    ASC, DESC;

    public static SqlJetSortingOrder decode(String s) {
        if ("asc".equalsIgnoreCase(s)) {
            return ASC;
        } else if ("desc".equalsIgnoreCase(s)) {
            return DESC;
        }
        return null;
    }

    public String toString() {
        switch (this) {
        case ASC:
            return "ASC";
        case DESC:
            return "DESC";
        }
        return "";
    }
}
