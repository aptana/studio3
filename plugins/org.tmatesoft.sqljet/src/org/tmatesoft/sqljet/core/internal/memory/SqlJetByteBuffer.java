/**
 * SqlJetByteBuffer.java
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
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryManager;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetByteBuffer implements ISqlJetMemoryBuffer {

    protected ByteBuffer buffer;

    public SqlJetByteBuffer() {

    }

    /**
     * @param b
     */
    public SqlJetByteBuffer(ByteBuffer b) {
        buffer = b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#allocate
     * (int)
     */
    public void allocate(int size) {
        assert (size >= 0);

        buffer = ByteBuffer.allocate(size);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#free()
     */
    public void free() {
        assert (buffer != null);

        buffer = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#isAllocated
     * ()
     */
    public boolean isAllocated() {
        return buffer != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getSize()
     */
    public int getSize() {
        assert (buffer != null);

        return buffer.capacity();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getPointer
     * (int)
     */
    public ISqlJetMemoryPointer getPointer(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity());

        return new SqlJetMemoryPointer(this, pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getByte(int)
     */
    public byte getByte(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.capacity());

        return buffer.get(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getByteUnsigned
     * (int)
     */
    public int getByteUnsigned(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.capacity());

        return SqlJetBytesUtility.toUnsignedByte(getByte(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getInt(int)
     */
    public int getInt(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.INT_SIZE);

        return buffer.getInt(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getIntUnsigned
     * (int)
     */
    public long getIntUnsigned(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.INT_SIZE);

        return SqlJetBytesUtility.toUnsignedInt(getInt(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getLong(int)
     */
    public long getLong(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.LONG_SIZE);

        return buffer.getLong(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getShort
     * (int)
     */
    public short getShort(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.SHORT_SIZE);

        return buffer.getShort(pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#getShortUnsigned
     * (int)
     */
    public int getShortUnsigned(int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.SHORT_SIZE);

        return SqlJetBytesUtility.toUnsignedShort(getShort(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#setByte(int,
     * byte)
     */
    public void putByte(int pointer, byte value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.capacity());

        buffer.put(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#setByteUnsigned
     * (int, int)
     */
    public void putByteUnsigned(int pointer, int value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.capacity());

        putByte(pointer, (byte) SqlJetBytesUtility.toUnsignedByte(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#setInt(int,
     * int)
     */
    public void putInt(int pointer, int value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.INT_SIZE);

        buffer.putInt(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#setIntUnsigned
     * (int, long)
     */
    public void putIntUnsigned(int pointer, long value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.INT_SIZE);

        putInt(pointer, (int) SqlJetBytesUtility.toUnsignedInt(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#setLong(int,
     * long)
     */
    public void putLong(int pointer, long value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.LONG_SIZE);

        buffer.putLong(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#setShort
     * (int, short)
     */
    public void putShort(int pointer, short value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.SHORT_SIZE);

        buffer.putShort(pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#setShortUnsigned
     * (int, int)
     */
    public void putShortUnsigned(int pointer, int value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.capacity() - ISqlJetMemoryManager.SHORT_SIZE);

        putShort(pointer, (short) SqlJetBytesUtility.toUnsignedShort(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#readFromFile
     * (int, java.io.RandomAccessFile, long, int)
     */
    public int readFromFile(int pointer, RandomAccessFile file, long position, int count) throws IOException {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.capacity());
        assert (file != null);
        assert (position >= 0);
        assert (count > 0);

        buffer.limit(pointer + count).position(pointer);
        try {
            return file.getChannel().read(buffer, position);
        } finally {
            buffer.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.memory.ISqlJetMemoryBuffer#writeToFile
     * (int, java.io.RandomAccessFile, long, int)
     */
    public int writeToFile(int pointer, RandomAccessFile file, long position, int count) throws IOException {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.capacity());
        assert (file != null);
        assert (position >= 0);
        assert (count > 0);

        buffer.limit(pointer + count).position(pointer);
        try {
            return file.getChannel().write(buffer, position);
        } finally {
            buffer.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#asArray()
     */
    public byte[] asArray() {
        return buffer.array();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#copyFrom(int,
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer, int, int)
     */
    public void copyFrom(int dstPos, ISqlJetMemoryBuffer src, int srcPos, int count) {
        if (src instanceof SqlJetByteBuffer&& !(src instanceof SqlJetDirectByteBuffer)) {
            final SqlJetByteBuffer srcBuf = (SqlJetByteBuffer) src;
            System.arraycopy(srcBuf.buffer.array(), srcPos, buffer.array(), dstPos, count);
        } else {
            final byte[] b = new byte[count];
            src.getBytes(srcPos, b, 0, count);
            putBytes(dstPos, b, 0, count);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#fill(int,
     * int, byte)
     */
    public void fill(int from, int count, byte value) {
        Arrays.fill(buffer.array(), from, from + count, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#getBytes(int,
     * byte[], int, int)
     */
    public void getBytes(int pointer, byte[] bytes, int to, int count) {
        System.arraycopy(buffer.array(), pointer, bytes, to, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#putBytes(int,
     * byte[], int, int)
     */
    public void putBytes(int pointer, byte[] bytes, int from, int count) {
        System.arraycopy(bytes, from, buffer.array(), pointer, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#compareTo(int,
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer, int)
     */
    public int compareTo(int pointer, ISqlJetMemoryBuffer buffer, int bufferPointer) {
        final int thisCount = getSize() - pointer;
        final int bufferCount = buffer.getSize() - bufferPointer;
        if (thisCount != bufferCount) {
            if (thisCount > bufferCount) {
                return 1;
            } else {
                return -1;
            }
        }
        if (buffer instanceof SqlJetByteBuffer && !(buffer instanceof SqlJetDirectByteBuffer)) {
            final SqlJetByteBuffer b = (SqlJetByteBuffer) buffer;
            return SqlJetUtility.memcmp(this.buffer.array(), pointer, b.buffer.array(), bufferPointer, thisCount);
        } else {
            final byte[] b = new byte[thisCount];
            buffer.getBytes(bufferPointer, b, 0, thisCount);
            return SqlJetUtility.memcmp(this.buffer.array(), pointer, b, bufferPointer, thisCount);
        }
    }

}
