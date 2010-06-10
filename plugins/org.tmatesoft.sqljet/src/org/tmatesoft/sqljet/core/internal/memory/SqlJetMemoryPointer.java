/**
 * SqlJetMemoryPointer.java
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

import java.io.IOException;
import java.io.RandomAccessFile;

import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public final class SqlJetMemoryPointer implements ISqlJetMemoryPointer {

    private ISqlJetMemoryBuffer buffer;
    private int pointer;
    private int limit;

    /**
     * 
     */
    public SqlJetMemoryPointer(ISqlJetMemoryBuffer buffer, int pointer) {
        assert (buffer != null);
        assert (buffer.isAllocated());
        assert (pointer >= 0);
        assert (pointer <= buffer.getSize());

        this.buffer = buffer;
        this.pointer = pointer;
        this.limit = buffer.getSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getBuffer()
     */
    final public ISqlJetMemoryBuffer getBuffer() {
        return buffer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getPointer()
     */
    final public int getPointer() {
        return pointer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setPointer(int)
     */
    final public void setPointer(int pointer) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        this.pointer = pointer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #movePointer(int)
     */
    final public void movePointer(int count) {
        assert (buffer != null);
        assert (buffer.isAllocated());
        assert (pointer + count >= 0);
        assert (pointer + count <= buffer.getSize());

        pointer += count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getByte()
     */
    final public byte getByte() {
        assert (buffer != null);
        assert (buffer.isAllocated());

        return buffer.getByte(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getInt()
     */
    final public int getInt() {
        assert (buffer != null);
        assert (buffer.isAllocated());

        return buffer.getInt(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getLong()
     */
    final public long getLong() {
        assert (buffer != null);
        assert (buffer.isAllocated());

        return buffer.getLong(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getShort()
     */
    final public short getShort() {
        assert (buffer != null);
        assert (buffer.isAllocated());

        return buffer.getShort(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getUnsignedByte()
     */
    final public int getByteUnsigned() {
        assert (buffer != null);
        assert (buffer.isAllocated());

        return buffer.getByteUnsigned(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getUnsignedInt()
     */
    final public long getIntUnsigned() {
        assert (buffer != null);
        assert (buffer.isAllocated());

        return buffer.getIntUnsigned(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #getUnsignedShort()
     */
    final public int getShortUnsigned() {
        assert (buffer != null);
        assert (buffer.isAllocated());

        return buffer.getShortUnsigned(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setByte(byte)
     */
    final public void putByte(byte value) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        buffer.putByte(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setInt(int)
     */
    final public void putInt(int value) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        buffer.putInt(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setLong(long)
     */
    final public void putLong(long value) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        buffer.putLong(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setShort(short)
     */
    final public void putShort(short value) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        buffer.putShort(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setUnsignedByte(int)
     */
    final public void putByteUnsigned(int value) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        buffer.putByteUnsigned(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setUnsignedInt(long)
     */
    final public void putIntUnsigned(long value) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        buffer.putIntUnsigned(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #setUnsignedShort(int)
     */
    final public void putShortUnsigned(int value) {
        assert (buffer != null);
        assert (buffer.isAllocated());

        buffer.putShortUnsigned(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #read(java.io.RandomAccessFile, long, int)
     */
    final public int readFromFile(RandomAccessFile file, long position, int count) throws IOException {
        assert (buffer != null);
        assert (buffer.isAllocated());
        assert (file != null);
        assert (position >= 0);
        assert (count > 0);
        assert (pointer + count <= buffer.getSize());

        return buffer.readFromFile(pointer, file, position, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryPointer
     * #write(java.io.RandomAccessFile, long, int)
     */
    final public int writeToFile(RandomAccessFile file, long position, int count) throws IOException {
        assert (buffer != null);
        assert (file != null);
        assert (position >= 0);
        assert (count > 0);
        assert (pointer + count <= buffer.getSize());

        return buffer.writeToFile(pointer, file, position, count);
    }

    /**
     * @param pointer
     * @return
     */
    final public int getAbsolute(int pointer) {
        return this.pointer + pointer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getByte(int)
     */
    final public byte getByte(int pointer) {
        return buffer.getByte(getAbsolute(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getByteUnsigned
     * (int)
     */
    final public int getByteUnsigned(int pointer) {
        return buffer.getByteUnsigned(getAbsolute(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getInt(int)
     */
    final public int getInt(int pointer) {
        return buffer.getInt(getAbsolute(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getIntUnsigned
     * (int)
     */
    final public long getIntUnsigned(int pointer) {
        return buffer.getIntUnsigned(getAbsolute(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getLong(int)
     */
    final public long getLong(int pointer) {
        return buffer.getLong(getAbsolute(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getShort(int)
     */
    final public short getShort(int pointer) {
        return buffer.getShort(getAbsolute(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getShortUnsigned
     * (int)
     */
    final public int getShortUnsigned(int pointer) {
        return buffer.getShortUnsigned(getAbsolute(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#putByte(int,
     * byte)
     */
    final public void putByte(int pointer, byte value) {
        buffer.putByte(getAbsolute(pointer), value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#putByteUnsigned
     * (int, int)
     */
    final public void putByteUnsigned(int pointer, int value) {
        buffer.putByteUnsigned(getAbsolute(pointer), value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#putInt(int,
     * int)
     */
    final public void putInt(int pointer, int value) {
        buffer.putInt(getAbsolute(pointer), value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#putIntUnsigned
     * (int, long)
     */
    final public void putIntUnsigned(int pointer, long value) {
        buffer.putIntUnsigned(getAbsolute(pointer), value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#putLong(int,
     * long)
     */
    final public void putLong(int pointer, long value) {
        buffer.putLong(getAbsolute(pointer), value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#putShort(int,
     * short)
     */
    final public void putShort(int pointer, short value) {
        buffer.putShort(getAbsolute(pointer), value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#putShortUnsigned
     * (int, int)
     */
    final public void putShortUnsigned(int pointer, int value) {
        buffer.putShortUnsigned(getAbsolute(pointer), value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#readFromFile(int,
     * java.io.RandomAccessFile, long, int)
     */
    final public int readFromFile(int pointer, RandomAccessFile file, long position, int count) throws IOException {
        return buffer.readFromFile(getAbsolute(pointer), file, position, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#writeToFile(int,
     * java.io.RandomAccessFile, long, int)
     */
    final public int writeToFile(int pointer, RandomAccessFile file, long position, int count) throws IOException {
        return buffer.writeToFile(getAbsolute(pointer), file, position, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#remaining()
     */
    final public int remaining() {
        return limit - pointer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#copyFrom(int,
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer, int, int)
     */
    final public void copyFrom(int dstPos, ISqlJetMemoryPointer src, int srcPos, int length) {
        buffer.copyFrom(getAbsolute(dstPos), src.getBuffer(), src.getAbsolute(srcPos), length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#copyFrom(org.
     * tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer, int, int)
     */
    final public void copyFrom(ISqlJetMemoryPointer src, int srcPos, int length) {
        buffer.copyFrom(pointer, src.getBuffer(), src.getAbsolute(srcPos), length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#copyFrom(org.
     * tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer, int)
     */
    final public void copyFrom(ISqlJetMemoryPointer src, int length) {
        buffer.copyFrom(pointer, src.getBuffer(), src.getPointer(), length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#fill(int,
     * byte)
     */
    final public void fill(int count, byte value) {
        buffer.fill(pointer, count, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#fill(int,
     * int, byte)
     */
    final public void fill(int from, int count, byte value) {
        buffer.fill(getAbsolute(from), count, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(byte[])
     */
    final public void getBytes(byte[] bytes) {
        buffer.getBytes(pointer, bytes, 0, bytes.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(int,
     * byte[])
     */
    final public void getBytes(int pointer, byte[] bytes) {
        buffer.getBytes(getAbsolute(pointer), bytes, 0, bytes.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(int,
     * byte[], int)
     */
    final public void getBytes(int pointer, byte[] bytes, int count) {
        buffer.getBytes(getAbsolute(pointer), bytes, 0, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(int,
     * byte[], int, int)
     */
    final public void getBytes(int pointer, byte[] bytes, int to, int count) {
        buffer.getBytes(getAbsolute(pointer), bytes, to, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(byte[])
     */
    final public void putBytes(byte[] bytes) {
        buffer.putBytes(pointer, bytes, 0, bytes.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(int,
     * byte[])
     */
    final public void putBytes(int pointer, byte[] bytes) {
        buffer.putBytes(getAbsolute(pointer), bytes, 0, bytes.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(int,
     * byte[], int)
     */
    final public void putBytes(int pointer, byte[] bytes, int count) {
        buffer.putBytes(getAbsolute(pointer), bytes, 0, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#getBytes(int,
     * byte[], int, int)
     */
    final public void putBytes(int pointer, byte[] bytes, int to, int count) {
        buffer.putBytes(getAbsolute(pointer), bytes, to, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#compareTo(org
     * .tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer)
     */
    final public int compareTo(ISqlJetMemoryPointer ptr) {
        return buffer.compareTo(pointer, ptr.getBuffer(), ptr.getPointer());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer#limit(int)
     */
    final public void limit(int n) {
        this.limit = n;
    }
}
