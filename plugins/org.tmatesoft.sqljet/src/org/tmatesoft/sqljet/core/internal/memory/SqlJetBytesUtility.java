/**
 * SqlJetUnsigned.java
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

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBytesUtility {

    public static final int BYTE_UNSIGNED_MASK = 0xFF;
    public static final long BYTE_UNSIGNED_MASK_L = 0xFFL;
    public static final int SHORT_UNSIGNED_MASK = 0xFFFF;
    public static final long INT_UNSIGNED_MASK = 0xFFFFFFFFL;

    public static int toUnsignedByte(int value) {
        return BYTE_UNSIGNED_MASK & value;
    }

    public static int toUnsignedShort(int value) {
        return SHORT_UNSIGNED_MASK & value;
    }

    public static long toUnsignedInt(long value) {
        return INT_UNSIGNED_MASK & value;
    }

    /*
     * Methods for unpacking primitive values from byte arrays starting at given
     * offsets.
     */

    static boolean getBoolean(byte[] b, int off) {
        return b[off] != 0;
    }

    static char getChar(byte[] b, int off) {
        return (char) (((b[off + 1] & BYTE_UNSIGNED_MASK)) | ((b[off] & BYTE_UNSIGNED_MASK) << 8));
    }

    static short getShort(byte[] b, int off) {
        return (short) (((b[off + 1] & BYTE_UNSIGNED_MASK)) | ((b[off] & BYTE_UNSIGNED_MASK) << 8));
    }

    static int getInt(byte[] b, int off) {
        return ((b[off + 3] & BYTE_UNSIGNED_MASK)) | ((b[off + 2] & BYTE_UNSIGNED_MASK) << 8)
                | ((b[off + 1] & BYTE_UNSIGNED_MASK) << 16) | ((b[off] & BYTE_UNSIGNED_MASK) << 24);
    }

    static float getFloat(byte[] b, int off) {
        int i = ((b[off + 3] & BYTE_UNSIGNED_MASK)) | ((b[off + 2] & BYTE_UNSIGNED_MASK) << 8)
                | ((b[off + 1] & BYTE_UNSIGNED_MASK) << 16) + ((b[off] & BYTE_UNSIGNED_MASK) << 24);
        return Float.intBitsToFloat(i);
    }

    static long getLong(byte[] b, int off) {
        return ((b[off + 7] & BYTE_UNSIGNED_MASK_L)) | ((b[off + 6] & BYTE_UNSIGNED_MASK_L) << 8)
                | ((b[off + 5] & BYTE_UNSIGNED_MASK_L) << 16) + ((b[off + 4] & BYTE_UNSIGNED_MASK_L) << 24)
                | ((b[off + 3] & BYTE_UNSIGNED_MASK_L) << 32) | ((b[off + 2] & BYTE_UNSIGNED_MASK_L) << 40)
                + ((b[off + 1] & BYTE_UNSIGNED_MASK_L) << 48) | ((b[off] & BYTE_UNSIGNED_MASK_L) << 56);
    }

    static double getDouble(byte[] b, int off) {
        long j = ((b[off + 7] & BYTE_UNSIGNED_MASK_L)) | ((b[off + 6] & BYTE_UNSIGNED_MASK_L) << 8)
                | ((b[off + 5] & BYTE_UNSIGNED_MASK_L) << 16) + ((b[off + 4] & BYTE_UNSIGNED_MASK_L) << 24)
                | ((b[off + 3] & BYTE_UNSIGNED_MASK_L) << 32) | ((b[off + 2] & BYTE_UNSIGNED_MASK_L) << 40)
                + ((b[off + 1] & BYTE_UNSIGNED_MASK_L) << 48) | ((b[off] & BYTE_UNSIGNED_MASK_L) << 56);
        return Double.longBitsToDouble(j);
    }

    /*
     * Methods for packing primitive values into byte arrays starting at given
     * offsets.
     */

    static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }

    static void putChar(byte[] b, int off, char val) {
        b[off + 1] = (byte) (val);
        b[off] = (byte) (val >>> 8);
    }

    static void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val);
        b[off] = (byte) (val >>> 8);
    }

    static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off] = (byte) (val >>> 24);
    }

    static void putFloat(byte[] b, int off, float val) {
        final int i = Float.floatToIntBits(val);
        b[off + 3] = (byte) (i);
        b[off + 2] = (byte) (i >>> 8);
        b[off + 1] = (byte) (i >>> 16);
        b[off] = (byte) (i >>> 24);
    }

    static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val);
        b[off + 6] = (byte) (val >>> 8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off] = (byte) (val >>> 56);
    }

    static void putDouble(byte[] b, int off, double val) {
        final long j = Double.doubleToLongBits(val);
        b[off + 7] = (byte) (j);
        b[off + 6] = (byte) (j >>> 8);
        b[off + 5] = (byte) (j >>> 16);
        b[off + 4] = (byte) (j >>> 24);
        b[off + 3] = (byte) (j >>> 32);
        b[off + 2] = (byte) (j >>> 40);
        b[off + 1] = (byte) (j >>> 48);
        b[off] = (byte) (j >>> 56);
    }

}
