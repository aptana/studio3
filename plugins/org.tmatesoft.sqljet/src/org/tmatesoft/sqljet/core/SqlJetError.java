/**
 * SqlJetError.java
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
 * SQLJet's runtime error.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetError extends Error {

    /**
     * 
     */
    private static final long serialVersionUID = -6895964570583712664L;

    /**
     * Create error with message.
     * 
     * @param message
     *            message string
     */
    public SqlJetError(String message) {
        super(message);
    }

    /**
     * Create error caused by some reason.
     * 
     * @param cause
     *            the reason of error.
     */
    public SqlJetError(Throwable cause) {
        super(cause);
    }

    /**
     * Create error with message and caused by reason.
     * 
     * @param message
     *            the message string.
     * @param cause
     *            the reason of error.
     */
    public SqlJetError(String message, Throwable cause) {
        super(message, cause);
    }

}
