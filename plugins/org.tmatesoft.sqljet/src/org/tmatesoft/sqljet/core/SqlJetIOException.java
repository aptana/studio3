/**
 * SqlJetIOException.java
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
 * Extended exception for {@link SqlJetErrorCode#IOERR}
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public class SqlJetIOException extends SqlJetException {

    private static final long serialVersionUID = -7059309339596959681L;
    
    private SqlJetIOErrorCode ioErrorCode;

    /**
     * @return the ioErrorCode
     */
    public SqlJetIOErrorCode getIoErrorCode() {
        return ioErrorCode;
    }
    
    /**
     * Create extended exception for IOERR.
     * 
     * @param ioErrorCode error code.
     */
    public SqlJetIOException(final SqlJetIOErrorCode ioErrorCode) {
        super(SqlJetErrorCode.IOERR);
        this.ioErrorCode = ioErrorCode;
    }
    
    public SqlJetIOException(final SqlJetIOErrorCode ioErrorCode, 
            final String message) 
    {
        super(SqlJetErrorCode.IOERR,message);
        this.ioErrorCode = ioErrorCode;
    }

    public SqlJetIOException(final SqlJetIOErrorCode ioErrorCode,
            final Throwable cause ) {
        super(SqlJetErrorCode.IOERR,cause);
        this.ioErrorCode = ioErrorCode;
    }

    public SqlJetIOException(final SqlJetIOErrorCode ioErrorCode, 
            final String message, final Throwable cause  ) 
    {
        super(SqlJetErrorCode.IOERR,message, cause);
        this.ioErrorCode = ioErrorCode;
    }
    
}
