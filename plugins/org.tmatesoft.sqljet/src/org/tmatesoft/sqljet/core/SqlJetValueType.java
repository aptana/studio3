/**
 * SqlJetValueType.java
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
 * Fundamental Datatypes.
 * 
 * Every value in SQLJet has one of five fundamental datatypes:
 * 
 * <ul>
 * <li>64-bit signed integer
 * <li>64-bit IEEE floating point number
 * <li>String
 * <li>BLOB
 * <li>NULL
 * </ul>
 * 
 * These constants are codes for each of those types.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public enum SqlJetValueType {

    /**
     * 64-bit signed integer
     */
    INTEGER, // 1

    /**
     * 64-bit IEEE floating point number
     */
    FLOAT, // 2

    /**
     * String value
     */
    TEXT, // 3

    /**
     * Blob value
     */
    BLOB, // 4

    /**
     * Null value
     */
    NULL
    // 5

}
