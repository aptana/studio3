/**
 * ISqlJetMemoryManager.java
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
 * Default implementation of SQLJet's memory manager. It allows allocate memory
 * chunk {@link ISqlJetMemoryBuffer}.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetMemoryManager {

    int BYTE_SIZE = 1;
    int SHORT_SIZE = 2;
    int INT_SIZE = 4;
    int LONG_SIZE = 8;

    /**
     * Get buffers implementation type which is used by default.
     * 
     * @return
     */
    SqlJetMemoryBufferType getDefaultBufferType();

    /**
     * Get buffers implementation which is used by default.
     * 
     * @param bufferType
     */
    void setDefaultBufferType(SqlJetMemoryBufferType bufferType);

    /**
     * Allocates memory chunk {@link ISqlJetMemoryBuffer} using default buffer
     * type.
     * 
     * @param size
     *            size of buffer in bytes
     * @return allocated buffer
     */
    ISqlJetMemoryBuffer allocate(int size);

    /**
     * @param size
     * @param bufferType
     * @return
     */
    ISqlJetMemoryBuffer allocate(int size, SqlJetMemoryBufferType bufferType);

    /**
     * Allocates memory chunk {@link ISqlJetMemoryBuffer} using default buffer
     * type.
     * 
     * @param size
     *            size of buffer in bytes
     * @return allocated buffer
     */
    ISqlJetMemoryPointer allocatePtr(int size);

    /**
     * @param size
     * @param bufferType
     * @return
     */
    ISqlJetMemoryPointer allocatePtr(int size, SqlJetMemoryBufferType bufferType);
    
    /**
     * Fries memory.
     * 
     * @param buffer
     *            buff
     */
    void free(ISqlJetMemoryBuffer buffer);

}
