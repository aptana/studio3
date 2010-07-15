/**
 * SqlJetVdbeMemFlags.java
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
package org.tmatesoft.sqljet.core.internal.vdbe;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public enum SqlJetVdbeMemFlags {

    /** Value is NULL */
    Null, // 0x0001

    /** Value is a string */
    Str, // 0x0002

    /** Value is an integer */
    Int, // 0x0004

    /** Value is a real number */
    Real, // 0x0008

    /** Value is a BLOB */
    Blob, // 0x0010

    /** Value is a RowSet object */
    RowSet, // 0x0020

    /** Mask of type bits */
    TypeMask, // 0x00ff

    /*
     * Whenever Mem contains a valid string or blob representation, one of* the
     * following flags must be set to determine the memory management* policy
     * for Mem.z. The MEM_Term flag tells us whether or not the* string is \000
     * or \u0000 terminated
     */

    /** String rep is nul terminated */
    Term, // 0x0200

    /** Need to call sqliteFree() on Mem.z */
    Dyn, // 0x0400

    /** Mem.z points to a static string */
    Static, // 0x0800

    /** Mem.z points to an ephemeral string */
    Ephem, // 0x1000

    /** Mem.z points to an agg function context */
    Agg, // 0x2000

    /** Mem.i contains count of 0s appended to blob */
    Zero
    // 0x4000

}
