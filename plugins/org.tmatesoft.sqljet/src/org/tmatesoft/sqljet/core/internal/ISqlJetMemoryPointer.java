/**
 * ISqlJetPointer.java
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
 * Pointer in SqlJet's memory buffer.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetMemoryPointer {

    /**
     * Get buffer which contains pointer.
     * 
     * @return
     */
    ISqlJetMemoryBuffer getBuffer();

    /**
     * Get pointer address (offset in buffer).
     * 
     * @return
     */
    int getPointer();

    /**
     * Set pointer address (offset in buffer).
     * 
     * @param pointer
     */
    void setPointer(int pointer);

    /**
     * Move pointer. Add some count to pointer address. Count may be negative.
     * 
     * @param count
     *            count which added to address. May be negative.
     */
    void movePointer(int count);

    /**
     * Read byte at current address.
     * 
     * @return
     */
    byte getByte();

    /**
     * Write byte at current address.
     * 
     * @param value
     */
    void putByte(byte value);

    /**
     * Read short at current address.
     * 
     * @return
     */
    short getShort();

    /**
     * Write short at current address.
     * 
     * @param value
     */
    void putShort(short value);

    /**
     * Read int at current address.
     * 
     * @return
     */
    int getInt();

    /**
     * Write int at current address.
     * 
     * @param value
     */
    void putInt(int value);

    /**
     * Read long at current address.
     * 
     * @return
     */
    long getLong();

    /**
     * Write long at current address.
     * 
     * @param value
     */
    void putLong(long value);

    /**
     * Read unsigned byte at current address.
     * 
     * @return
     */
    int getByteUnsigned();

    /**
     * Write unsigned byte at current address.
     * 
     * @param value
     */
    void putByteUnsigned(int value);

    /**
     * Read unsigned short at current address.
     * 
     * @return
     */
    int getShortUnsigned();

    /**
     * Write unsigned short at current address.
     * 
     * @param value
     */
    void putShortUnsigned(int value);

    /**
     * Read unsigned int at current address.
     * 
     * @return
     */
    long getIntUnsigned();

    /**
     * Write unsigned int at current address.
     * 
     * @param value
     */
    void putIntUnsigned(long value);

    /**
     * Read from file at current address.
     * 
     * @param file
     * @param position
     * @param count
     * @return
     * @throws IOException
     */
    int readFromFile(RandomAccessFile file, long position, int count) throws IOException;

    /**
     * Write to file at current address.
     * 
     * @param file
     * @param position
     * @param count
     * @return
     * @throws IOException
     */
    int writeToFile(RandomAccessFile file, long position, int count) throws IOException;

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
     * @return
     */
    int remaining();

    void copyFrom(int dstPos, ISqlJetMemoryPointer src, int srcPos, int length);

    void copyFrom(ISqlJetMemoryPointer src, int srcPos, int length);

    void copyFrom(ISqlJetMemoryPointer src, int length);

    /**
     * @param pointer
     * @return
     */
    int getAbsolute(int pointer);

    /**
     * @param from
     * @param count
     * @param value
     */
    void fill(int from, int count, byte value);
    /**
     * @param from
     * @param count
     * @param value
     */
    void fill(int count, byte value);

    /**
     * @param bytes
     */
    void getBytes(byte[] bytes);

    /**
     * @param bytes
     */
    void getBytes(int pointer, byte[] bytes);

    /**
     * @param bytes
     */
    void getBytes(int pointer, byte[] bytes, int count);
    
    /**
     * @param bytes
     */
    void getBytes(int pointer, byte[] bytes, int to, int count);

    /**
     * @param bytes
     */
    void putBytes(byte[] bytes);

    /**
     * @param bytes
     */
    void putBytes(int pointer, byte[] bytes);

    /**
     * @param bytes
     */
    void putBytes(int pointer, byte[] bytes, int count);
    
    /**
     * @param bytes
     */
    void putBytes(int pointer, byte[] bytes, int to, int count);

    /**
     * @param raw2
     * @return
     */
    int compareTo(ISqlJetMemoryPointer ptr);

    /**
     * @param n
     */
    void limit(int n);
    
}
