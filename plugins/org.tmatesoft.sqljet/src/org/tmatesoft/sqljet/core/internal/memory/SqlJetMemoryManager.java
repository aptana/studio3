/**
 * SqlJetMemoryManager.java
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
package org.tmatesoft.sqljet.core.internal.memory;

import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryManager;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.SqlJetMemoryBufferType;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetMemoryManager implements ISqlJetMemoryManager {

    private SqlJetMemoryBufferType defaultBufferType = SqlJetUtility.getEnumSysProp(
            "SqlJetMemoryManager.defaultBufferType", SqlJetMemoryBufferType.ARRAY);

    /*
     * (non-Javadoc)
     * 
     * @seeorg.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryManager#
     * getDefaultBufferType()
     */
    public SqlJetMemoryBufferType getDefaultBufferType() {
        return defaultBufferType;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryManager#
     * setDefaultBufferType
     * (org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryManager
     * .BufferType)
     */
    public void setDefaultBufferType(final SqlJetMemoryBufferType bufferType) {
        if (bufferType != null) {
            defaultBufferType = bufferType;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryManager#allocatePtr(int)
     */
    public ISqlJetMemoryPointer allocatePtr(int size) {
        return allocate(size).getPointer(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryManager#allocatePtr(int,
     * org.tmatesoft.sqljet.core.internal.SqlJetMemoryBufferType)
     */
    public ISqlJetMemoryPointer allocatePtr(int size, SqlJetMemoryBufferType bufferType) {
        return allocate(size, bufferType).getPointer(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryManager
     * #allocate(int)
     */
    public ISqlJetMemoryBuffer allocate(final int size) {
        return allocate(size, defaultBufferType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryManager#allocate
     * (int,
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryManager.BufferType)
     */
    public ISqlJetMemoryBuffer allocate(int size, SqlJetMemoryBufferType bufferType) {
        if (size >= 0) {
            final ISqlJetMemoryBuffer buffer;
            switch (bufferType) {
            case ARRAY:
                buffer = new SqlJetByteArrayBuffer();
                break;
            case BUFFER:
                buffer = new SqlJetByteBuffer();
                break;
            case DIRECT:
                buffer = new SqlJetDirectByteBuffer();
                break;
            default:
                buffer = new SqlJetByteArrayBuffer();
            }
            buffer.allocate(size);
            return buffer;
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryManager
     * #free
     * (org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer)
     */
    public void free(final ISqlJetMemoryBuffer buffer) {
        if (buffer != null) {
            buffer.free();
        }
    }

}
