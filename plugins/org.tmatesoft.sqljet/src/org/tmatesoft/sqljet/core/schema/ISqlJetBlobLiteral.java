/**
 * ISqlJetBlobLiteral.java
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
 * Blob literal.
 * 
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public interface ISqlJetBlobLiteral extends ISqlJetLiteralValue {

    /**
     * <p>
     * Blob value as bytes array.
     * </p>
     * 
     * <p>
     * IMPORTANT: Do not modify the returned array!
     * </p>
     * 
     * @return blob value
     */
    public byte[] getValue();
}
