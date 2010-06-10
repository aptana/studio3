/**
 * SqlJetIOErrorCode.java
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
package org.tmatesoft.sqljet.core;

/**
 * Extended error codes for {@link SqlJetErrorCode#IOERR}
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public enum SqlJetIOErrorCode {

    IOERR_READ,
    IOERR_SHORT_READ,
    IOERR_WRITE,
    IOERR_FSYNC,
    IOERR_DIR_FSYNC,
    IOERR_TRUNCATE,
    IOERR_FSTAT,
    IOERR_UNLOCK,
    IOERR_RDLOCK,
    IOERR_DELETE,
    IOERR_BLOCKED,
    IOERR_NOMEM,
    IOERR_ACCESS,
    IOERR_CHECKRESERVEDLOCK,
    IOERR_LOCK
    
}
