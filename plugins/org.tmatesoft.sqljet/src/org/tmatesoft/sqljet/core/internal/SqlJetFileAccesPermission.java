/**
 * SqlJetFileAccesPermission.java
 * Copyright (C) 2008 TMate Software Ltd
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
package org.tmatesoft.sqljet.core.internal;

/**
 * Flags for the {@link ISqlJetFileSystem#access(java.io.File, SqlJetFileAccesPermission)} method.
 * They determine what kind of permissions the access() method is looking for.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public enum SqlJetFileAccesPermission {

    /**
     * Simply checks whether the file exists.
     */
    EXISTS,
    
    /**
     * Checks whether the file is both readable and writable.
     */
    READWRITE,
    
    /**
     * Checks whether the file is readable.
     */
    READONLY
    
}
