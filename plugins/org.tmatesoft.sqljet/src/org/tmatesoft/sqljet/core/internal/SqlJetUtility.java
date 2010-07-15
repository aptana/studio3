/**
 * SqlJetUtility.java
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.tmatesoft.sqljet.core.ISqlJetMutex;
import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetError;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetLogDefinitions;
import org.tmatesoft.sqljet.core.internal.memory.SqlJetByteBuffer;
import org.tmatesoft.sqljet.core.internal.memory.SqlJetMemoryManager;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public final class SqlJetUtility {

    public static final String SQLJET_PACKAGENAME = "org.tmatesoft.sqljet";

    private static final boolean SQLJET_LOG_STACKTRACE = getBoolSysProp(SqlJetLogDefinitions.SQLJET_LOG_STACKTRACE,
            false);

    private static final boolean SQLJET_LOG_SIGNED = getBoolSysProp(SqlJetLogDefinitions.SQLJET_LOG_SIGNED, false);

    private static final Logger signedLogger = Logger.getLogger(SqlJetLogDefinitions.SQLJET_LOG_SIGNED);

    public static final ISqlJetMemoryManager memoryManager = new SqlJetMemoryManager();

    /**
     * @param logger
     * @param format
     * @param args
     */
    public static void log(Logger logger, String format, Object... args) {
        StringBuilder s = new StringBuilder();
        s.append(String.format(format, args)).append('\n');
        if (SQLJET_LOG_STACKTRACE) {
            logStackTrace(s);
        }
        logger.info(s.toString());
    }

    /**
     * @param s
     */
    private static void logStackTrace(StringBuilder s) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            final String l = stackTraceElement.toString();
            if (l.startsWith(SQLJET_PACKAGENAME))
                s.append('\t').append(l).append('\n');
        }
    }

    private static void logSigned(long v) {
        if (SQLJET_LOG_SIGNED && v < 0) {
            log(signedLogger, "signed %d", v);
        }
    }

    public static final ISqlJetMemoryPointer allocatePtr(int size) {
        return memoryManager.allocatePtr(size);
    }

    public static final ISqlJetMemoryPointer allocatePtr(int size, SqlJetMemoryBufferType bufferType) {
        return memoryManager.allocatePtr(size, bufferType);
    }

    /**
     * 
     * @param buf
     * @return
     */
    public static final ISqlJetMemoryPointer pointer(ISqlJetMemoryPointer p) {
        return p.getBuffer().getPointer(p.getPointer());
    }

    /**
     * Implements address arithmetic on byte buffer.
     * 
     * @param p
     * @param pos
     * @return
     */
    public static final ISqlJetMemoryPointer pointer(ISqlJetMemoryPointer p, int pos) {
        return p.getBuffer().getPointer(p.getAbsolute(pos));
    }

    public static final void movePtr(ISqlJetMemoryPointer p, int pos) {
        p.movePointer(pos);
    }

    /**
     * @param bs
     * @return
     */
    public static final ISqlJetMemoryPointer wrapPtr(byte[] bs) {
        final ISqlJetMemoryPointer p = allocatePtr(bs.length);
        p.putBytes(bs);
        return p;
    }

    public static String getSysProp(final String propName, final String defValue) throws SqlJetError {
        if (null == propName)
            throw new SqlJetError("Undefined property name");
        try {
            return System.getProperty(propName, defValue);
        } catch (Throwable t) {
            throw new SqlJetError("Error while get int value for property " + propName, t);
        }
    }

    public static int getIntSysProp(final String propName, final int defValue) throws SqlJetError {
        if (null == propName)
            throw new SqlJetError("Undefined property name");
        try {
            return Integer.valueOf(System.getProperty(propName, Integer.toString(defValue)));
        } catch (Throwable t) {
            throw new SqlJetError("Error while get int value for property " + propName, t);
        }
    }

    /**
     * @param string
     * @param b
     * @return
     */
    public static boolean getBoolSysProp(String propName, boolean defValue) {
        if (null == propName)
            throw new SqlJetError("Undefined property name");
        try {
            return Boolean.valueOf(System.getProperty(propName, Boolean.toString(defValue)));
        } catch (Throwable t) {
            throw new SqlJetError("Error while get int value for property " + propName, t);
        }
    }

    /**
     * @param <T>
     * @param propName
     * @param defValue
     * @return
     */
    public static <T extends Enum<T>> T getEnumSysProp(String propName, T defValue) {
        if (null == propName)
            throw new SqlJetError("Undefined property name");
        if (null == defValue)
            throw new SqlJetError("Undefined default value");
        try {
            return Enum.valueOf(defValue.getDeclaringClass(), System.getProperty(propName, defValue.toString()));
        } catch (Throwable t) {
            throw new SqlJetError("Error while get int value for property " + propName, t);
        }
    }

    /**
     * Read a two-byte big-endian integer values.
     */
    public static final int get2byte(ISqlJetMemoryPointer x) {
        return get2byte(x, 0);
    }

    /**
     * Read a two-byte big-endian integer values.
     */
    public static final int get2byte(ISqlJetMemoryPointer x, int off) {
        return x.getShortUnsigned(off);
    }

    /**
     * Write a two-byte big-endian integer values.
     */
    public static final void put2byte(ISqlJetMemoryPointer p, int v) {
        put2byte(p, 0, v);
    }

    /**
     * Write a two-byte big-endian integer values.
     */
    public static final void put2byte(ISqlJetMemoryPointer p, int off, int v) {
        p.putShortUnsigned(off, v);
    }

    /**
     * Write a four-byte big-endian integer value.
     */
    public static final ISqlJetMemoryPointer put4byte(int v) {
        logSigned(v);
        final ISqlJetMemoryPointer b = allocatePtr(4);
        b.putInt(0, v);
        return b;
    }

    /**
     * Read a four-byte big-endian integer value.
     */
    public static final int get4byte(ISqlJetMemoryPointer p) {
        return get4byte(p, 0);
    }

    /**
     * Read a four-byte big-endian integer value.
     */
    public static final int get4byte(ISqlJetMemoryPointer p, int pos) {
        int v = p.getInt(pos);
        logSigned(v);
        return v;
    }

    /**
     * Write a four-byte big-endian integer value.
     */
    public static final void put4byte(ISqlJetMemoryPointer p, int pos, long v) {
        if (null == p || (p.remaining() - pos) < 4)
            throw new SqlJetError("Wrong destination");
        logSigned(v);
        p.putIntUnsigned(pos, v);
    }

    /**
     * Write a four-byte big-endian integer value.
     */
    public static final void put4byte(ISqlJetMemoryPointer p, long v) {
        put4byte(p, 0, v);
    }

    /**
     * @param dest
     * @param src
     * @param length
     */
    public static final void memcpy(byte[] dest, byte[] src, int length) {
        System.arraycopy(src, 0, dest, 0, length);
    }

    public static final void memcpy(byte[] dest, int dstPos, byte[] src, int srcPos, int length) {
        System.arraycopy(src, srcPos, dest, dstPos, length);
    }

    public static final void memcpy(ISqlJetMemoryPointer dest, ISqlJetMemoryPointer src, int length) {
        dest.copyFrom(src, length);
    }

    public static final void memcpy(ISqlJetMemoryPointer dest, int dstPos, ISqlJetMemoryPointer src, int srcPos,
            int length) {
        dest.copyFrom(dstPos, src, srcPos, length);
    }

    public static final void memcpy(SqlJetCloneable[] dest, SqlJetCloneable[] src, int length) throws SqlJetException {
        memcpy(src, 0, dest, 0, length);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends SqlJetCloneable> T memcpy(T src) throws SqlJetException {
        try {
            return (T) src.clone();
        } catch (CloneNotSupportedException e) {
            throw new SqlJetException(SqlJetErrorCode.INTERNAL, e);
        }
    }

    /**
     * @param src
     * @param dstPos
     * @param dest
     * @param srcPos
     * @param length
     * 
     * @throws SqlJetException
     */
    private static final void memcpy(SqlJetCloneable[] src, int srcPos, SqlJetCloneable[] dest, int dstPos, int length)
            throws SqlJetException {
        for (int x = srcPos, y = dstPos; x < src.length && y < dest.length; x++, y++) {
            final SqlJetCloneable o = src[x];
            if (null == o)
                continue;
            try {
                dest[y] = (SqlJetCloneable) o.clone();
            } catch (CloneNotSupportedException e) {
                throw new SqlJetException(SqlJetErrorCode.INTERNAL, e);
            }
        }
    }

    /**
     * @param data
     * @param from
     * @param value
     * @param count
     */
    public static final void memset(ISqlJetMemoryPointer data, int from, byte value, int count) {
        data.fill(from, count, value);
    }

    /**
     * @param data
     * @param value
     * @param count
     */
    public static final void memset(ISqlJetMemoryPointer data, byte value, int count) {
        memset(data, 0, value, count);
    }

    /**
     * @param data
     * @param value
     */
    public static final void memset(ISqlJetMemoryPointer data, byte value) {
        memset(data, value, data.remaining());
    }

    /**
     * @param s
     * @param from
     * @return
     */
    public static int strlen(byte[] s, int from) {
        int p = from;
        /* Loop over the data in s. */
        while (p < s.length && s[p] != 0)
            p++;
        return (p - from);
    }

    public static int strlen(ISqlJetMemoryPointer s, int from) {
        int p = from;
        /* Loop over the data in s. */
        while (p < s.remaining() && getUnsignedByte(s, p) != 0)
            p++;
        return (p - from);
    }

    /**
     * Check to see if the i-th bit is set. Return true or false. If p is NULL
     * (if the bitmap has not been created) or if i is out of range, then return
     * false.
     * 
     * @param bitSet
     * @param index
     * @return
     */
    public static boolean bitSetTest(BitSet bitSet, int index) {
        if (bitSet == null)
            return false;
        if (index < 0)
            return false;
        return bitSet.get(index);
    }

    /**
     * @param magic
     * @param journalMagic
     * @param i
     * @return
     */
    public static final int memcmp(byte[] a1, byte[] a2, int count) {
        for (int i = 0; i < count; i++) {
            final Byte b1 = Byte.valueOf(a1[i]);
            final Byte b2 = Byte.valueOf(a2[i]);
            final int c = b1.compareTo(b2);
            if (0 != c)
                return c;
        }
        return 0;
    }

    public static final int memcmp(byte[] a1, int from1, byte[] a2, int from2, int count) {
        for (int i = 0; i < count; i++) {
            final Byte b1 = Byte.valueOf(a1[from1 + i]);
            final Byte b2 = Byte.valueOf(a2[from2 + i]);
            final int c = b1.compareTo(b2);
            if (0 != c)
                return c;
        }
        return 0;
    }

    /**
     * @param z
     * @param z2
     * @param count
     * @return
     */
    public static final int memcmp(ISqlJetMemoryPointer a1, ISqlJetMemoryPointer a2, int count) {
        for (int i = 0; i < count; i++) {
            final int b1 = SqlJetUtility.getUnsignedByte(a1, i);
            final int b2 = SqlJetUtility.getUnsignedByte(a2, i);
            final int c = b1 - b2;
            if (0 != c)
                return c;
        }
        return 0;
    }

    /**
     * @param z
     * @param z2
     * @param count
     * @return
     */
    public static final int memcmp(ISqlJetMemoryPointer a1, int a1offs, ISqlJetMemoryPointer a2, int a2offs, int count) {
        for (int i = 0; i < count; i++) {
            final int b1 = SqlJetUtility.getUnsignedByte(a1, a1offs + i);
            final int b2 = SqlJetUtility.getUnsignedByte(a2, a2offs + i);
            final int c = b1- b2;
            if (0 != c)
                return c;
        }
        return 0;
    }

    /**
     * @param b
     * @return
     */
    public static byte[] addZeroByteEnd(byte[] b) {
        if (null == b)
            throw new SqlJetError("Undefined byte array");
        byte[] r = new byte[b.length + 1];
        memcpy(r, b, b.length);
        r[b.length] = 0;
        return r;
    }

    /**
     * @param sqliteFileHeader
     * @return
     */
    public static byte[] getBytes(String string) {
        if (null == string)
            throw new SqlJetError("Undefined string");
        try {
            return string.getBytes("UTF8");
        } catch (Throwable t) {
            throw new SqlJetError("Error while get bytes for string \"" + string + "\"", t);
        }
    }

    /**
     * The variable-length integer encoding is as follows:
     * 
     * KEY: A = 0xxxxxxx 7 bits of data and one flag bit B = 1xxxxxxx 7 bits of
     * data and one flag bit C = xxxxxxxx 8 bits of data
     * 
     * 7 bits - A 14 bits - BA 21 bits - BBA 28 bits - BBBA 35 bits - BBBBA 42
     * bits - BBBBBA 49 bits - BBBBBBA 56 bits - BBBBBBBA 64 bits - BBBBBBBBC
     */

    /**
     * Write a 64-bit variable-length integer to memory starting at p[0]. The
     * length of data write will be between 1 and 9 bytes. The number of bytes
     * written is returned.
     * 
     * A variable-length integer consists of the lower 7 bits of each byte for
     * all bytes that have the 8th bit set and one byte with the 8th bit clear.
     * Except, if we get to the 9th byte, it stores the full 8 bits and is the
     * last byte.
     */
    public static int putVarint(ISqlJetMemoryPointer p, long v) {
        int i, j, n;
        if ((v & (((long) 0xff000000) << 32)) != 0) {
            SqlJetUtility.putUnsignedByte(p, 8, (byte) v);
            v >>= 8;
            for (i = 7; i >= 0; i--) {
                SqlJetUtility.putUnsignedByte(p, i, (byte) ((v & 0x7f) | 0x80));
                v >>= 7;
            }
            return 9;
        }
        n = 0;
        byte[] buf = new byte[10];
        do {
            buf[n++] = (byte) ((v & 0x7f) | 0x80);
            v >>= 7;
        } while (v != 0);
        buf[0] &= 0x7f;
        assert (n <= 9);
        for (i = 0, j = n - 1; j >= 0; j--, i++) {
            SqlJetUtility.putUnsignedByte(p, i, buf[j]);
        }
        return n;
    }

    /**
     * This routine is a faster version of sqlite3PutVarint() that only works
     * for 32-bit positive integers and which is optimized for the common case
     * of small integers. A MACRO version, putVarint32, is provided which
     * inlines the single-byte case. All code should use the MACRO version as
     * this function assumes the single-byte case has already been handled.
     */
    public static int putVarint32(ISqlJetMemoryPointer p, int v) {
        if (v < 0x80) {
            SqlJetUtility.putUnsignedByte(p, 0, (byte) v);
            return 1;
        }

        if ((v & ~0x7f) == 0) {
            SqlJetUtility.putUnsignedByte(p, 0, (byte) v);
            return 1;
        }
        if ((v & ~0x3fff) == 0) {
            SqlJetUtility.putUnsignedByte(p, 0, (byte) ((v >> 7) | 0x80));
            SqlJetUtility.putUnsignedByte(p, 1, (byte) (v & 0x7f));
            return 2;
        }
        return putVarint(p, v);
    }

    /**
     * Read a 64-bit variable-length integer from memory starting at p[0].
     * Return the number of bytes read. The value is stored in *v.
     */
    public static byte getVarint(ISqlJetMemoryPointer p, long[] v) {
        return getVarint(p, 0, v);
    }

    public static byte getVarint(ISqlJetMemoryPointer p, int offset, long[] v) {
        long l = 0;
        for (byte i = 0; i < 8; i++) {
            final int b = SqlJetUtility.getUnsignedByte(p, i + offset);
            l = (l << 7) | (b & 0x7f);
            if ((b & 0x80) == 0) {
                v[0] = l;
                return ++i;
            }
        }
        final int b = SqlJetUtility.getUnsignedByte(p, 8 + offset);
        l = (l << 8) | b;
        v[0] = l;
        return 9;
    }

    /**
     * Read a 32-bit variable-length integer from memory starting at p[0].
     * Return the number of bytes read. The value is stored in *v. A MACRO
     * version, getVarint32, is provided which inlines the single-byte case. All
     * code should use the MACRO version as this function assumes the
     * single-byte case has already been handled.
     * 
     * @throws SqlJetExceptionRemove
     */
    public static byte getVarint32(ISqlJetMemoryPointer p, int[] v) {
        return getVarint32(p, 0, v);
    }

    public static byte getVarint32(ISqlJetMemoryPointer p, int offset, int[] v) {

        int x = SqlJetUtility.getUnsignedByte(p, 0 + offset);
        if (x < 0x80) {
            v[0] = x;
            return 1;
        }

        int a, b;
        int i = 0;

        a = SqlJetUtility.getUnsignedByte(p, i + offset);
        /* a: p0 (unmasked) */
        if ((a & 0x80) == 0) {
            v[0] = a;
            return 1;
        }

        i++;
        b = SqlJetUtility.getUnsignedByte(p, i + offset);
        /* b: p1 (unmasked) */
        if ((b & 0x80) == 0) {
            a &= 0x7f;
            a = a << 7;
            v[0] = a | b;
            return 2;
        }

        i++;
        a = a << 14;
        a |= SqlJetUtility.getUnsignedByte(p, i + offset);
        /* a: p0<<14 | p2 (unmasked) */
        if ((a & 0x80) == 0) {
            a &= (0x7f << 14) | (0x7f);
            b &= 0x7f;
            b = b << 7;
            v[0] = a | b;
            return 3;
        }

        i++;
        b = b << 14;
        b |= SqlJetUtility.getUnsignedByte(p, i + offset);
        /* b: p1<<14 | p3 (unmasked) */
        if ((b & 0x80) == 0) {
            b &= (0x7f << 14) | (0x7f);
            a &= (0x7f << 14) | (0x7f);
            a = a << 7;
            v[0] = a | b;
            return 4;
        }

        i++;
        a = a << 14;
        a |= SqlJetUtility.getUnsignedByte(p, i + offset);
        /* a: p0<<28 | p2<<14 | p4 (unmasked) */
        if ((a & 0x80) == 0) {
            a &= (0x7f << 28) | (0x7f << 14) | (0x7f);
            b &= (0x7f << 28) | (0x7f << 14) | (0x7f);
            b = b << 7;
            v[0] = a | b;
            return 5;
        }

        /*
         * We can only reach this point when reading a corrupt database file. In
         * that case we are not in any hurry. Use the (relatively slow)
         * general-purpose sqlite3GetVarint() routine to extract the value.
         */
        {
            long[] v64 = new long[1];
            byte n;

            i -= 4;
            n = getVarint(p, offset, v64);
            assert (n > 5 && n <= 9);
            v[0] = (int) v64[0];
            return n;
        }
    }

    /**
     * Return the number of bytes that will be needed to store the given 64-bit
     * integer.
     */
    public static int sqlite3VarintLen(long v) {
        int i = 0;
        do {
            i++;
            v >>= 7;
        } while (v != 0 && i < 9);
        return i;
    }

    /**
     * @param mutex
     * @return
     */
    public static final boolean mutex_held(ISqlJetMutex mutex) {
        return mutex == null || mutex.held();
    }

    /**
     * Compute a string length that is limited to what can be stored in lower 30
     * bits of a 32-bit signed integer.
     * 
     * @param z
     * @return
     */
    public static int strlen30(ISqlJetMemoryPointer z) {
        int i = 0;
        final int l = z.getPointer();
        for (; i < l && SqlJetUtility.getUnsignedByte(z, i) != 0; i++)
            ;
        return 0x3fffffff & (int) (i);
    }

    /**
     * Get unsigned byte from byte buffer
     * 
     * @param byteBuffer
     * @param index
     * @return
     */
    public static final int getUnsignedByte(ISqlJetMemoryPointer byteBuffer, int index) {
        return byteBuffer.getByteUnsigned(index);
    }

    /**
     * Put unsigned byte to byte buffer
     * 
     * @param byteBuffer
     * @param index
     * @param value
     * @return
     */
    public static final ISqlJetMemoryPointer putUnsignedByte(ISqlJetMemoryPointer byteBuffer, int index, int value) {
        byteBuffer.putByteUnsigned(index, value);
        return byteBuffer;
    }

    /**
     * Convert byte buffer to string.
     * 
     * @param buf
     * @return
     */
    public static String toString(ISqlJetMemoryPointer buf) {
        synchronized (buf) {
            byte[] bytes = new byte[buf.remaining()];
            buf.getBytes(bytes);
            return new String(bytes);
        }
    }

    /**
     * Convert byte buffer to string.
     * 
     * @param buf
     * @return
     * @throws SqlJetException
     */
    public static String toString(ISqlJetMemoryPointer buf, SqlJetEncoding enc) throws SqlJetException {
        if (buf == null)
            return null;
        if (enc == null)
            return null;
        synchronized (buf) {
            byte[] bytes = new byte[buf.remaining()];
            buf.getBytes(bytes);
            try {
                return new String(bytes, enc.getCharsetName());
            } catch (UnsupportedEncodingException e) {
                throw new SqlJetException(SqlJetErrorCode.MISUSE, "Unknown charset " + enc.name());
            }
        }
    }

    /**
     * Get {@link ByteBuffer} from {@link String}.
     * 
     * @param s
     * @param enc
     * @return
     * @throws SqlJetException
     */
    public static ISqlJetMemoryPointer fromString(String s, SqlJetEncoding enc) throws SqlJetException {
        try {
            return wrapPtr(s.getBytes(enc.getCharsetName()));
        } catch (UnsupportedEncodingException e) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Unknown charset " + enc.name());
        }
    }

    /**
     * Translate {@link ByteBuffer} from one charset to other charset.
     * 
     * @param buf
     * @param from
     * @param to
     * @return
     * @throws SqlJetException
     */
    public static ISqlJetMemoryPointer translate(ISqlJetMemoryPointer buf, SqlJetEncoding from, SqlJetEncoding to)
            throws SqlJetException {
        return fromString(toString(buf, from), to);
    }

    /**
     * @param s
     * @return
     */
    public static final String trim(String s) {
        return null != s ? s.trim() : null;
    }

    /**
     * Return the number of bytes that will be needed to store the given 64-bit
     * integer.
     */
    public static int varintLen(long v) {
        int i = 0;
        do {
            i++;
            v >>= 7;
        } while (v != 0 && i < 9);
        return i;
    }

    public static final short toUnsigned(byte value) {
        return (short) (value & (short) 0xff);
    }

    public static final byte fromUnsigned(short value) {
        return (byte) (value & 0xff);
    }

    public static final int toUnsigned(short value) {
        return (int) (value & (int) 0xffff);
    }

    public static final short fromUnsigned(int value) {
        return (short) (value & 0xffff);
    }

    public static long toUnsigned(int value) {
        return (long) (value & (long) 0xffffffffL);
    }

    public static final int fromUnsigned(long value) {
        return (int) (value & 0xffffffffL);
    }

    /**
     * Read a four-byte big-endian integer value.
     */
    public static final long get4byteUnsigned(byte[] p) {
        return get4byteUnsigned(SqlJetUtility.wrapPtr(p));
    }

    /**
     * Read a four-byte big-endian integer value.
     */
    public static final long get4byteUnsigned(byte[] p, int pos) {
        return get4byteUnsigned(SqlJetUtility.wrapPtr(p));
    }

    /**
     * Write a four-byte big-endian integer value.
     */
    public static final ISqlJetMemoryPointer put4byteUnsigned(long v) {
        logSigned(v);
        final ISqlJetMemoryPointer b = SqlJetUtility.allocatePtr(4);
        b.putIntUnsigned(v);
        return b;
    }

    /**
     * Write a four-byte big-endian integer value.
     */
    public static final void put4byteUnsigned(byte[] p, int pos, long v) {
        put4byteUnsigned(SqlJetUtility.wrapPtr(p), pos, v);
    }

    /**
     * Read a four-byte big-endian integer value.
     */
    public static final long get4byteUnsigned(ISqlJetMemoryPointer p) {
        return p.getIntUnsigned();
    }

    /**
     * Read a four-byte big-endian integer value.
     */
    public static final long get4byteUnsigned(ISqlJetMemoryPointer p, int pos) {
        return p.getIntUnsigned(pos);
    }

    /**
     * Write a four-byte big-endian integer value.
     */
    public static final void put4byteUnsigned(ISqlJetMemoryPointer p, int pos, long v) {
        p.putIntUnsigned(pos, v);
    }

    /**
     * Write a four-byte big-endian integer value.
     */
    public static final void put4byteUnsigned(ISqlJetMemoryPointer p, long v) {
        p.putIntUnsigned(v);
    }

    /**
     * @param z
     * @param slice
     * @param n
     */
    public static final void memmove(ISqlJetMemoryPointer dst, ISqlJetMemoryPointer src, int n) {
        memmove(dst, 0, src, 0, n);
    }

    /**
     * @param z
     * @param slice
     * @param n
     */
    public static final void memmove(ISqlJetMemoryPointer dst, int dstOffs, ISqlJetMemoryPointer src, int srcOffs, int n) {
        byte[] b = new byte[n];
        src.getBytes(srcOffs, b, n);
        dst.putBytes(dstOffs, b, n);
    }

    /**
     * @param z
     * @return
     */
    public static final double atof(ISqlJetMemoryPointer z) {
        final String s = toString(z);
        return Double.valueOf(s).doubleValue();
    }

    /**
     * @param str
     * @return
     */
    public static final Long atoi64(String str) {
        return Long.valueOf(str);
    }

    /**
     * Returns absolute value of argument
     * 
     * @param i
     * @return
     */
    public static final long absolute(long i) {
        long u;
        u = i < 0 ? -i : i;
        if (u == Integer.MIN_VALUE || u == Long.MIN_VALUE)
            u = u - 1;
        return u;
    }

    /**
     * @param key
     * @param dataRowId
     * @return
     */
    public static final Object[] addArrays(Object[] array1, Object[] array2) {
        Object[] a = new Object[array1.length + array2.length];
        System.arraycopy(array1, 0, a, 0, array1.length);
        System.arraycopy(array2, 0, a, array1.length, array2.length);
        return a;
    }

    public static final Object[] insertArray(Object[] intoArray, Object[] insertArray, int pos) {
        Object[] a = new Object[intoArray.length + insertArray.length];
        System.arraycopy(intoArray, 0, a, 0, pos);
        System.arraycopy(insertArray, 0, a, pos, insertArray.length);
        System.arraycopy(intoArray, pos, a, insertArray.length + pos, intoArray.length - pos);
        return a;
    }

    public static final <E extends Enum<E>> EnumSet<E> of(E e1, E... e) {
        return EnumSet.of(e1, e);
    }

    public static final <E extends Enum<E>> EnumSet<E> of(E e) {
        return EnumSet.of(e);
    }

    public static final <E extends Enum<E>> EnumSet<E> of(E e1, E e2) {
        return EnumSet.of(e1, e2);
    }

    public static final <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3) {
        return EnumSet.of(e1, e2, e3);
    }

    public static final <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        return EnumSet.noneOf(elementType);
    }

    /**
     * @param key
     * @return
     */
    public static final Object[] adjustNumberTypes(Object[] key) {
        if (null == key)
            return null;
        for (int i = 0; i < key.length; i++) {
            key[i] = adjustNumberType(key[i]);
        }
        return key;
    }

    public static final Object adjustNumberType(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Number) {
            if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
                return Long.valueOf(((Number) value).longValue());
            } else if (value instanceof Float) {
                // TODO may be better solution exists?
                return Double.parseDouble(Float.toString((Float) value));
            }
        }
        return value;
    }

    /**
     * @param value
     * @return
     * @throws SqlJetException
     */
    public static ISqlJetMemoryPointer streamToBuffer(InputStream stream) throws SqlJetException {
        if (stream == null)
            return null;
        try {
            byte[] b = new byte[stream.available()];
            stream.read(b);
            stream.reset();
            return wrapPtr(b);
        } catch (IOException e) {
            throw new SqlJetException(SqlJetErrorCode.IOERR, e);
        }

    }

    /**
     * @param buffer
     * @return
     */
    public static byte[] readByteBuffer(ISqlJetMemoryPointer buffer) {
        if (buffer == null)
            return null;
        byte[] array = new byte[buffer.remaining()];
        buffer.getBytes(array);
        return array;
    }

    /**
     * @param firstKey
     * @return
     */
    public static Object[] copyArray(Object[] array) {
        if (null == array)
            return null;
        final Object[] copy = new Object[array.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        return copy;
    }

    /**
     * @param value
     * @return
     */
    public static ISqlJetMemoryPointer fromByteBuffer(ByteBuffer b) {
        return new SqlJetByteBuffer(b).getPointer(0);
    }
    
    /**
     * Return TRUE if z is a pure numeric string. Return FALSE and leave realnum
     * unchanged if the string contains any character which is not part of a
     * number.
     * 
     * If the string is pure numeric, set realnum to TRUE if the string contains
     * the '.' character or an "E+000" style exponentiation suffix. Otherwise
     * set realnum to FALSE. Note that just becaue realnum is false does not
     * mean that the number can be successfully converted into an integer - it
     * might be too big.
     * 
     * An empty string is considered non-numeric.
     * 
     * @param s
     * @param realnum
     * @return
     */
    public static boolean isNumber(String s, boolean[] realnum) {
        if (s == null)
            return false;
        if (!NUMBER_PATTER.matcher(s).matches())
            return false;
        if (realnum != null && realnum.length > 0) {
            realnum[0] = REAL_PATTERN.matcher(s).matches();
        }
        return true;
    }

    static private final Pattern NUMBER_PATTER = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    static private final Pattern REAL_PATTERN = Pattern.compile("[\\.eE]+");

    /**
     * @param r
     * @return
     */
    public static Long doubleToInt64(Double r) {
        if (r == null)
            return null;
        if (r == Double.NaN)
            return null;
        if (r == Double.POSITIVE_INFINITY)
            return null;
        if (r == Double.NEGATIVE_INFINITY)
            return null;
        final double rdbl = r.doubleValue();
        final double rint = Math.rint(rdbl);
        if (rdbl != rint)
            return null;
        return Long.valueOf(r.longValue());
    }
    

}
