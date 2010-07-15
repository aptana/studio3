/**
 * SqlJetEncoding.java
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
package org.tmatesoft.sqljet.core;

/**
 * These constant define integer codes that represent the various text encodings
 * supported by SQLite.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public enum SqlJetEncoding {

    /**
     * UTF-8 encoding.
     */
    UTF8("UTF-8"), // 1

    /**
     * UTF-16 little-endian.
     */
    UTF16LE("UTF-16le"), // 2

    /**
     * UTF-16 big-endian.
     */
    UTF16BE("UTF-16be"), // 3

    /** Use native byte order */
    UTF16("UTF-16"), // 4

    /** sqlite3_create_function only */
    ANY, // 5

    /** sqlite3_create_collation only */
    UTF16_ALIGNED; // 8

    private String charsetName = "error";

    private SqlJetEncoding() {
    }

    private SqlJetEncoding(String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * Get charset name.
     * 
     * @return the charset name
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * Get charset constant from string with charset name.
     * 
     * @param s
     *            string with charset name
     * @return decoded charset constant or null if sring doesn't contains known
     *         charser name
     */
    public static SqlJetEncoding decode(String s) {
        if (UTF8.getCharsetName().equalsIgnoreCase(s)) {
            return UTF8;
        } else if (UTF16.getCharsetName().equalsIgnoreCase(s)) {
            return UTF16;
        } else if (UTF16LE.getCharsetName().equalsIgnoreCase(s)) {
            return UTF16LE;
        } else if (UTF16BE.getCharsetName().equalsIgnoreCase(s)) {
            return UTF16BE;
        }
        return null;
    }
}
