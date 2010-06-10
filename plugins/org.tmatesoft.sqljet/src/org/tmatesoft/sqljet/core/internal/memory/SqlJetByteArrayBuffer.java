/**
 * SqlJetByteArrayBuffer.java
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
public class SqlJetByteArrayBuffer implements ISqlJetMemoryBuffer {

    private byte[] buffer;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #allocate(int)
     */
    public void allocate(final int size) {
        assert (size >= 0);

        buffer = new byte[size];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #free()
     */
    public void free() {
        assert (buffer != null);

        buffer = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #isAllocated()
     */
    public boolean isAllocated() {
        return buffer != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getSize()
     */
    public int getSize() {
        assert (buffer != null);

        return buffer.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getPointer(int)
     */
    public ISqlJetMemoryPointer getPointer(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length);

        return new SqlJetMemoryPointer(this, pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getByte(int)
     */
    public byte getByte(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.length);

        return buffer[pointer];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getInt(int)
     */
    public int getInt(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.INT_SIZE);

        return SqlJetBytesUtility.getInt(buffer, pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getLong(int)
     */
    public long getLong(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.LONG_SIZE);

        return SqlJetBytesUtility.getLong(buffer, pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getShort(int)
     */
    public short getShort(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.SHORT_SIZE);

        return SqlJetBytesUtility.getShort(buffer, pointer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getUnsignedByte(int)
     */
    public int getByteUnsigned(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.length);

        return SqlJetBytesUtility.toUnsignedByte(getByte(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getUnsignedInt(int)
     */
    public long getIntUnsigned(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.INT_SIZE);

        return SqlJetBytesUtility.toUnsignedInt(getInt(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #getUnsignedShort(int)
     */
    public int getShortUnsigned(final int pointer) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.SHORT_SIZE);

        return SqlJetBytesUtility.toUnsignedShort(getShort(pointer));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #setByte(int, byte)
     */
    public void putByte(final int pointer, final byte value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.length);

        buffer[pointer] = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #setInt(int, int)
     */
    public void putInt(final int pointer, final int value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.INT_SIZE);

        SqlJetBytesUtility.putInt(buffer, pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #setLong(int, long)
     */
    public void putLong(final int pointer, final long value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.LONG_SIZE);

        SqlJetBytesUtility.putLong(buffer, pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #setShort(int, short)
     */
    public void putShort(final int pointer, final short value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.SHORT_SIZE);

        SqlJetBytesUtility.putShort(buffer, pointer, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #setUnsignedByte(int, int)
     */
    public void putByteUnsigned(final int pointer, final int value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.length);

        putByte(pointer, (byte) SqlJetBytesUtility.toUnsignedByte(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #setUnsignedInt(int, long)
     */
    public void putIntUnsigned(final int pointer, final long value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.INT_SIZE);

        putInt(pointer, (int) SqlJetBytesUtility.toUnsignedInt(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #setUnsignedShort(int, int)
     */
    public void putShortUnsigned(final int pointer, final int value) {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer <= buffer.length - ISqlJetMemoryManager.SHORT_SIZE);

        putShort(pointer, (short) SqlJetBytesUtility.toUnsignedShort(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #read(int, java.io.RandomAccessFile, long, int)
     */
    public int readFromFile(final int pointer, final RandomAccessFile file, final long position, final int count)
            throws IOException {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.length);
        assert (file != null);
        assert (position >= 0);
        assert (count > 0);

        file.seek(position);
        return file.read(buffer, pointer, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.ISqlJetMemoryBuffer
     * #write(int, java.io.RandomAccessFile, long, int)
     */
    public int writeToFile(final int pointer, final RandomAccessFile file, final long position, final int count)
            throws IOException {
        assert (buffer != null);
        assert (pointer >= 0);
        assert (pointer < buffer.length);
        assert (file != null);
        assert (position >= 0);
        assert (count > 0);

        file.seek(position);
        file.write(buffer, pointer, count);
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#asArray()
     */
    public byte[] asArray() {
        return buffer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#copyFrom(int,
     * org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer, int, int)
     */
    public void copyFrom(int dstPos, ISqlJetMemoryBuffer src, int srcPos, int count) {
        if (src instanceof SqlJetByteArrayBuffer) {
            final SqlJetByteArrayBuffer srcBuf = (SqlJetByteArrayBuffer) src;
            System.arraycopy(srcBuf.buffer, srcPos, buffer, dstPos, count);
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
        Arrays.fill(buffer, from, from + count, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#getBytes(int,
     * byte[], int, int)
     */
    public void getBytes(int pointer, byte[] bytes, int to, int count) {
        System.arraycopy(buffer, pointer, bytes, to, count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetMemoryBuffer#putBytes(int,
     * byte[], int, int)
     */
    public void putBytes(int pointer, byte[] bytes, int from, int count) {
        System.arraycopy(bytes, from, buffer, pointer, count);
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
        final int count = thisCount > bufferCount ? bufferCount : thisCount;
        if (buffer instanceof SqlJetByteArrayBuffer) {
            final SqlJetByteArrayBuffer b = (SqlJetByteArrayBuffer) buffer;
            final int cmp = SqlJetUtility.memcmp(this.buffer, pointer, b.buffer, bufferPointer, count);
            if (cmp != 0) {
                return cmp;
            }
        } else {
            final byte[] b = new byte[thisCount];
            buffer.getBytes(bufferPointer, b, 0, thisCount);
            final int cmp = SqlJetUtility.memcmp(this.buffer, pointer, b, bufferPointer, count);
            if (cmp != 0) {
                return cmp;
            }
        }
        if (thisCount != bufferCount) {
            if (thisCount > bufferCount) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }
}
