/**
 * SqlJetSerialType.java
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
package org.tmatesoft.sqljet.core.internal.vdbe;

import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetVdbeSerialType {

    private static final byte aSize[] = { 0, 1, 2, 3, 4, 6, 8, 8, 0, 0, 0, 0 };

    /**
     ** Return the length of the data corresponding to the supplied serial-type.
     */
    public static int serialTypeLen(int serial_type) {
        if (serial_type >= 12) {
            return (serial_type - 12) / 2;
        } else {
            return aSize[serial_type];
        }
    }

    /**
     * Deserialize the data blob pointed to by buf as serial type serial_type
     * and store the result in pMem. Return the number of bytes read.
     * 
     * @param buf
     *            Buffer to deserialize from
     * @param serial_type
     *            Serial type to deserialize
     * @param pMem
     *            Memory cell to write value into
     * @return
     */
    public static int serialGet(ISqlJetMemoryPointer buf, int serial_type, SqlJetVdbeMem pMem) {
        return serialGet(buf, 0, serial_type, pMem);
    }

    public static int serialGet(ISqlJetMemoryPointer buf, int offset, int serial_type, SqlJetVdbeMem pMem) {
        switch (serial_type) {
        case 10: /* Reserved for future use */
        case 11: /* Reserved for future use */
        case 0: { /* NULL */
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Null);
            pMem.type = SqlJetValueType.NULL;
            break;
        }
        case 1: { /* 1-byte signed integer */
            pMem.i = buf.getByte(offset);
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
            pMem.type = SqlJetValueType.INTEGER;
            return 1;
        }
        case 2: { /* 2-byte signed integer */
            pMem.i = SqlJetUtility
                    .fromUnsigned((int) ((SqlJetUtility.getUnsignedByte(buf, offset) << 8) | SqlJetUtility
                            .getUnsignedByte(buf, offset + 1)));
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
            pMem.type = SqlJetValueType.INTEGER;
            return 2;
        }
        case 3: { /* 3-byte signed integer */
            pMem.i = (buf.getByte(offset) << 16) | (SqlJetUtility.getUnsignedByte(buf, offset + 1) << 8)
                    | SqlJetUtility.getUnsignedByte(buf, offset + 2);
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
            pMem.type = SqlJetValueType.INTEGER;
            return 3;
        }
        case 4: { /* 4-byte signed integer */
            pMem.i = SqlJetUtility.fromUnsigned((long) ((SqlJetUtility.getUnsignedByte(buf, offset) << 24)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 1) << 16)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 2) << 8) | SqlJetUtility.getUnsignedByte(buf,
                    offset + 3)));
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
            pMem.type = SqlJetValueType.INTEGER;
            return 4;
        }
        case 5: { /* 6-byte signed integer */
            long x = (SqlJetUtility.getUnsignedByte(buf, offset) << 8) | SqlJetUtility.getUnsignedByte(buf, offset + 1);
            int y = (SqlJetUtility.getUnsignedByte(buf, offset + 2) << 24)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 3) << 16)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 4) << 8)
                    | SqlJetUtility.getUnsignedByte(buf, offset + 5);
            x = ((long) (short) x << 32) | SqlJetUtility.toUnsigned(y);
            pMem.i = x;
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
            pMem.type = SqlJetValueType.INTEGER;
            return 6;
        }
        case 6: /* 8-byte signed integer */
        case 7: { /* IEEE floating point */
            long x;
            int y;
            x = (SqlJetUtility.getUnsignedByte(buf, offset) << 24)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 1) << 16)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 2) << 8)
                    | SqlJetUtility.getUnsignedByte(buf, offset + 3);
            y = (SqlJetUtility.getUnsignedByte(buf, offset + 4) << 24)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 5) << 16)
                    | (SqlJetUtility.getUnsignedByte(buf, offset + 6) << 8)
                    | SqlJetUtility.getUnsignedByte(buf, offset + 7);
            x = ((long) (int) x << 32) | SqlJetUtility.toUnsigned(y);
            if (serial_type == 6) {
                pMem.i = x;
                pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
                pMem.type = SqlJetValueType.INTEGER;
            } else {
                // assert( sizeof(x)==8 && sizeof(pMem->r)==8 );
                // swapMixedEndianFloat(x);
                // memcpy(&pMem->r, &x, sizeof(x));
                // pMem.r = ByteBuffer.allocate(8).putLong(x).getDouble();
                pMem.r = Double.longBitsToDouble(x);
                pMem.flags = SqlJetUtility.of(pMem.r == Double.NaN ? SqlJetVdbeMemFlags.Null : SqlJetVdbeMemFlags.Real);
                pMem.type = pMem.r == Double.NaN ? SqlJetValueType.NULL : SqlJetValueType.FLOAT;
            }
            return 8;
        }
        case 8: /* Integer 0 */
        case 9: { /* Integer 1 */
            pMem.i = serial_type - 8;
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
            pMem.type = SqlJetValueType.INTEGER;
            return 0;
        }
        default: {
            int len = (serial_type - 12) / 2;
            pMem.z = SqlJetUtility.pointer(buf, offset);
            pMem.z.limit(len);
            pMem.n = len;
            pMem.xDel = null;
            if ((serial_type & 0x01) != 0) {
                pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Str, SqlJetVdbeMemFlags.Ephem);
                pMem.type = SqlJetValueType.TEXT;
            } else {
                pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Blob, SqlJetVdbeMemFlags.Ephem);
                pMem.type = SqlJetValueType.BLOB;
            }
            return len;
        }
        }
        return 0;
    }

    /*
     * * Return the serial-type for the value stored in pMem.
     */
    public static int serialType(SqlJetVdbeMem pMem, int file_format) {
        int n;
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Null)) {
            return 0;
        }
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Int)) {
            /* Figure out whether to use 1, 2, 4, 6 or 8 bytes. */
            long MAX_6BYTE = ((((long) 0x00008000) << 32) - 1);
            long i = pMem.i;
            long u;
            if (file_format >= 4 && (i & 1) == i) {
                return 8 + (int) i;
            }

            u = SqlJetUtility.absolute(i);

            if (u <= 127)
                return 1;
            if (u <= 32767)
                return 2;
            if (u <= 8388607)
                return 3;
            if (u <= 2147483647)
                return 4;
            if (u <= MAX_6BYTE)
                return 5;
            return 6;
        }
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Real)) {
            return 7;
        }
        n = pMem.n;
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Zero)) {
            n += pMem.nZero;
        }
        assert (n >= 0);
        return ((n * 2) + 12 + (pMem.flags.contains(SqlJetVdbeMemFlags.Str) ? 1 : 0));
    }

    /**
     * Write the serialized data blob for the value stored in pMem into buf. It
     * is assumed that the caller has allocated sufficient space. Return the
     * number of bytes written.
     * 
     * nBuf is the amount of space left in buf[]. nBuf must always be large
     * enough to hold the entire field. Except, if the field is a blob with a
     * zero-filled tail, then buf[] might be just the right size to hold
     * everything except for the zero-filled tail. If buf[] is only big enough
     * to hold the non-zero prefix, then only write that prefix into buf[]. But
     * if buf[] is large enough to hold both the prefix and the tail then write
     * the prefix and set the tail to all zeros.
     * 
     * Return the number of bytes actually written into buf[]. The number of
     * bytes in the zero-filled tail is included in the return value only if
     * those bytes were zeroed in buf[].
     */
    public static int serialPut(ISqlJetMemoryPointer buf, int nBuf, SqlJetVdbeMem pMem, int file_format) {
        int serial_type = serialType(pMem, file_format);
        int len;

        /* Integer and Real */
        if (serial_type <= 7 && serial_type > 0) {
            long v;
            int i;
            if (serial_type == 7) {
                v = Double.doubleToLongBits(pMem.r);
            } else {
                v = pMem.i;
            }
            len = i = serialTypeLen(serial_type);
            assert (len <= nBuf);
            while (i-- > 0) {
                buf.putByteUnsigned(i, (int) v);
                v >>>= 8;
            }
            return len;
        }

        /* String or blob */
        if (serial_type >= 12) {
            assert (pMem.n + (pMem.flags.contains(SqlJetVdbeMemFlags.Zero) ? pMem.nZero : 0) == serialTypeLen(serial_type));
            assert (pMem.n <= nBuf);
            len = pMem.n;
            SqlJetUtility.memcpy(buf, pMem.z, len);
            if (pMem.flags.contains(SqlJetVdbeMemFlags.Zero)) {
                len += pMem.nZero;
                if (len > nBuf) {
                    len = nBuf;
                }
                SqlJetUtility.memset(buf, pMem.n, (byte) 0, len - pMem.n);
            }
            return len;
        }

        /* NULL or constants 0 or 1 */
        return 0;
    }

}
