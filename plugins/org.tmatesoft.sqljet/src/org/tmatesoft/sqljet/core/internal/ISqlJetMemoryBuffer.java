/**
 * ISqlJetMemory.java
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

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Represents SQLJet's operations with memory.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetMemoryBuffer {

    /**
     * Allocate chunk of memory. If memory already has allocated then fries
     * current chunk and allocates new.
     * 
     * @param size
     *            size of chunk in bytes
     */
    void allocate(int size);

    /**
     * Fries chunk of memory.
     */
    void free();

    /**
     * Returns true if memory is allocated.
     * 
     * @return
     */
    boolean isAllocated();

    /**
     * Returns pointer in memory chunk at passed offset.
     * 
     * @param pointer
     * 
     * @return
     */
    ISqlJetMemoryPointer getPointer(int pointer);

    /**
     * Size of allocated memory chunk.
     * 
     * @return
     */
    int getSize();

    /**
     * Read byte at pointer.
     * 
     * @param pointer
     * @return
     */
    byte getByte(int pointer);

    /**
     * Write byte at pointer.
     * 
     * @param pointer
     * @param value
     */
    void putByte(int pointer, byte value);

    /**
     * Read short at pointer.
     * 
     * @param pointer
     * @return
     */
    short getShort(int pointer);

    /**
     * Write short at pointer.
     * 
     * @param pointer
     * @param value
     */
    void putShort(int pointer, short value);

    /**
     * Read int at pointer.
     * 
     * @param pointer
     * @return
     */
    int getInt(int pointer);

    /**
     * Write int at pointer.
     * 
     * @param pointer
     * @param value
     */
    void putInt(int pointer, int value);

    /**
     * Read long at pointer.
     * 
     * @param pointer
     * @return
     */
    long getLong(int pointer);

    /**
     * Write long at pointer.
     * 
     * @param pointer
     * @param value
     */
    void putLong(int pointer, long value);

    /**
     * Read unsigned byte at pointer.
     * 
     * @param pointer
     * @return
     */
    int getByteUnsigned(int pointer);

    /**
     * Write unsigned byte at pointer.
     * 
     * @param pointer
     * @param value
     */
    void putByteUnsigned(int pointer, int value);

    /**
     * Read unsigned short at pointer.
     * 
     * @param pointer
     * @return
     */
    int getShortUnsigned(int pointer);

    /**
     * Write unsigned short at pointer.
     * 
     * @param pointer
     * @param value
     */
    void putShortUnsigned(int pointer, int value);

    /**
     * Read unsigned int at pointer.
     * 
     * @param pointer
     * @return
     */
    long getIntUnsigned(int pointer);

    /**
     * Write unsigned int at pointer.
     * 
     * @param pointer
     * @param value
     */
    void putIntUnsigned(int pointer, long value);

    /**
     * Read from file into memory chunk at pointer. Method isn't synchronized on
     * file.
     * 
     * @param pointer
     * @param file
     * @param position
     * @param count
     * @return
     * @throws IOException
     */
    int readFromFile(int pointer, RandomAccessFile file, long position, int count) throws IOException;

    /**
     * Write from memory chunk at pointer to file. Method isn't synchronized on
     * file.
     * 
     * @param pointer
     * @param file
     * @param position
     * @param count
     * @return
     * @throws IOException
     */
    int writeToFile(int pointer, RandomAccessFile file, long position, int count) throws IOException;

    /**
     * @param dstPos
     * @param src
     * @param srcPos
     * @param count
     */
    void copyFrom(int dstPos, ISqlJetMemoryBuffer src, int srcPos, int count);

    /**
     * @param from
     * @param count
     * @param value
     */
    void fill(int from, int count, byte value);

    /**
     * @param absolute
     * @param bytes
     * @param to
     * @param count
     */
    void getBytes(int pointer, byte[] bytes, int to, int count);

    /**
     * @param absolute
     * @param bytes
     * @param to
     * @param count
     */
    void putBytes(int pointer, byte[] bytes, int from, int count);

    /**
     * @return
     */
    byte[] asArray();

    /**
     * @param pointer
     * @param buffer
     * @param pointer2
     * @return
     */
    int compareTo(int pointer, ISqlJetMemoryBuffer buffer, int bufferPointer);

}
