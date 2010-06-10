/**
 * SqlJetTransactionMode.java
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
package org.tmatesoft.sqljet.core.internal;

/**
 * Transaction mode.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 * @deprecated use {@link org.tmatesoft.sqljet.core.SqlJetTransactionMode} class.
 * 
 */
public enum SqlJetTransactionMode {

    /**
     * Read-only transaction.
     */
    READ_ONLY,

    /**
     * Write transaction.
     */
    WRITE,

    /**
     * Exclusive transaction.
     */
    EXCLUSIVE;

    /**
     * 
     * @return non-deprecated equivalent of this enum value.
     */
    public org.tmatesoft.sqljet.core.SqlJetTransactionMode mode() {
        switch (this) {
        case WRITE:
            return org.tmatesoft.sqljet.core.SqlJetTransactionMode.WRITE;
        case READ_ONLY:
            return org.tmatesoft.sqljet.core.SqlJetTransactionMode.READ_ONLY;
        case EXCLUSIVE:
        default:
            return org.tmatesoft.sqljet.core.SqlJetTransactionMode.EXCLUSIVE;
        }

    }
}
