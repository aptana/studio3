/**
 * ISqlJetVdbeMem.java
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

import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetVdbeMemFlags;
import org.tmatesoft.sqljet.core.schema.SqlJetTypeAffinity;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetVdbeMem {

    /**
     * Release any memory held by the Mem. This may leave the Mem in an
     * inconsistent state, for example with (Mem.z==0) and
     * (Mem.type==SQLITE_TEXT).
     * 
     */
    void release();

    /**
     * If pMem is an object with a valid string representation, this routine
     * ensures the internal encoding for the string representation is
     * 'desiredEnc', one of SQLITE_UTF8, SQLITE_UTF16LE or SQLITE_UTF16BE.
     * 
     * If pMem is not a string object, or the encoding of the string
     * representation is already stored using the requested encoding, then this
     * routine is a no-op.
     * 
     * SQLITE_OK is returned if the conversion is successful (or not required).
     * SQLITE_NOMEM may be returned if a malloc() fails during conversion
     * between formats.
     * 
     * @param enc
     * @throws SqlJetException
     */
    void changeEncoding(SqlJetEncoding desiredEnc) throws SqlJetException;

    /**
     * This routine transforms the internal text encoding used by pMem to
     * desiredEnc. It is an error if the string is already of the desired
     * encoding, or if *pMem does not contain a string value.
     * 
     * @param desiredEnc
     * @throws SqlJetException
     */
    void translate(SqlJetEncoding desiredEnc) throws SqlJetException;

    /**
     * This routine checks for a byte-order mark at the beginning of the UTF-16
     * string stored in *pMem. If one is present, it is removed and the encoding
     * of the Mem adjusted. This routine does not do any byte-swapping, it just
     * sets Mem.enc appropriately.
     * 
     * The allocation (static, dynamic etc.) and encoding of the Mem may be
     * changed by this function.
     */
    void handleBom();

    /**
     * If the given Mem* has a zero-filled tail, turn it into an ordinary blob
     * stored in dynamically allocated space.
     * 
     */
    void expandBlob();

    /**
     * This function is only available internally, it is not part of the
     * external API. It works in a similar way to sqlite3_value_text(), except
     * the data returned is in the encoding specified by the second parameter,
     * which must be one of SQLITE_UTF16BE, SQLITE_UTF16LE or SQLITE_UTF8.
     * 
     * (2006-02-16:) The enc value can be or-ed with SQLITE_UTF16_ALIGNED. If
     * that is the case, then the result must be aligned on an even byte
     * boundary.
     * 
     * @param enc
     * @return
     * @throws SqlJetException
     */
    ISqlJetMemoryPointer valueText(SqlJetEncoding enc) throws SqlJetException;

    /**
     * Make sure pMem->z points to a writable allocation of at least n bytes.
     * 
     * If the memory cell currently contains string or blob data and the third
     * argument passed to this function is true, the current content of the cell
     * is preserved. Otherwise, it may be discarded.
     * 
     * This function sets the MEM_Dyn flag and clears any xDel callback. It also
     * clears MEM_Ephem and MEM_Static. If the preserve flag is not set, Mem.n
     * is zeroed.
     * 
     * @param n
     * @param preserve
     */
    void grow(int n, boolean preserve);

    /**
     * Move data out of a btree key or data field and into a Mem structure. The
     * data or key is taken from the entry that pCur is currently pointing to.
     * offset and amt determine what portion of the data or key to retrieve. key
     * is true to get the key or false to get data. The result is written into
     * the pMem element.
     * 
     * The pMem structure is assumed to be uninitialized. Any prior content is
     * overwritten without being freed.
     * 
     * If this routine fails for any reason (malloc returns NULL or unable to
     * read from the disk) then the pMem is left in an inconsistent state.
     * 
     * @param pCur
     * @param offset
     *            Offset from the start of data to return bytes from.
     * @param amt
     *            Number of bytes to return.
     * @param key
     *            If true, retrieve from the btree key, not data.
     * @return
     * @throws SqlJetException
     */
    void fromBtree(ISqlJetBtreeCursor pCur, int offset, int amt, boolean key) throws SqlJetException;

    /**
     * Make the given Mem object MEM_Dyn. In other words, make it so that any
     * TEXT or BLOB content is stored in memory obtained from malloc(). In this
     * way, we know that the memory is safe to be overwritten or altered.
     */
    void makeWriteable();

    /**
     * Return some kind of integer value which is the best we can do at
     * representing the value that *pMem describes as an integer. If pMem is an
     * integer, then the value is exact. If pMem is a floating-point then the
     * value returned is the integer part. If pMem is a string or blob, then we
     * make an attempt to convert it into a integer and return that. If pMem is
     * NULL, return 0.
     * 
     * If pMem is a string, its encoding might be changed.
     */
    long intValue();

    /**
     * Delete any previous value and set the value stored in *pMem to NULL.
     */
    void setNull();

    /**
     * Change the value of a Mem to be a string or a BLOB.
     * 
     * The memory management strategy depends on the value of the xDel
     * parameter. If the value passed is SQLITE_TRANSIENT, then the string is
     * copied into a (possibly existing) buffer managed by the Mem structure.
     * Otherwise, any existing buffer is freed and the pointer copied.
     * 
     * @throws SqlJetException
     */
    void setStr(ISqlJetMemoryPointer z, SqlJetEncoding enc) throws SqlJetException;

    /**
     * Delete any previous value and set the value stored in *pMem to val,
     * manifest type INTEGER.
     */
    void setInt64(long val);

    /**
     * Make sure the given Mem is nul terminated.
     * 
     */
    void nulTerminate();

    /**
     * Add MEM_Str to the set of representations for the given Mem. Numbers are
     * converted using sqlite3_snprintf(). Converting a BLOB to a string is a
     * no-op.
     * 
     * Existing representations MEM_Int and MEM_Real are *not* invalidated.
     * 
     * A MEM_Null value will never be passed to this function. This function is
     * used for converting values to text for returning to the user (i.e. via
     * sqlite3_value_text()), or for ensuring that values to be used as btree
     * keys are strings. In the former case a NULL pointer is returned the user
     * and the later is an internal programming error.
     * 
     * @param enc
     * @throws SqlJetException
     */
    void stringify(SqlJetEncoding enc) throws SqlJetException;

    /**
     * Return the best representation of pMem that we can get into a double. If
     * pMem is already a double or an integer, return its value. If it is a
     * string or blob, try to convert it to a double. If it is a NULL, return
     * 0.0.
     */
    double realValue();

    /**
     * The MEM structure is already a MEM_Real. Try to also make it a MEM_Int if
     * we can.
     */
    void integerAffinity();

    /**
     * Convert pMem to type integer. Invalidate any prior representations.
     */
    void integerify();

    /**
     * Convert pMem so that it is of type MEM_Real. Invalidate any prior
     * representations.
     */
    void realify();

    /**
     * Convert pMem so that it has types MEM_Real or MEM_Int or both. Invalidate
     * any prior representations.
     */
    void numerify();

    /**
     * Delete any previous value and set the value to be a BLOB of length n
     * containing all zeros.
     */
    void setZeroBlob(int n);

    /**
     * Delete any previous value and set the value stored in *pMem to val,
     * manifest type REAL.
     */
    void setDouble(double val);

    /**
     * Delete any previous value and set the value of pMem to be an empty
     * boolean index.
     */
    void setRowSet();

    /**
     * Return true if the Mem object contains a TEXT or BLOB that is too large -
     * whose size exceeds SQLITE_MAX_LENGTH.
     */
    boolean isTooBig();

    /**
     ** Make an shallow copy. The pFrom->z field is not duplicated. If pFrom->z
     * is used, then pTo->z points to the same thing as pFrom->z and flags gets
     * srcType (either MEM_Ephem or MEM_Static).
     * 
     * @param srcType
     * 
     * @throws SqlJetException
     */
    ISqlJetVdbeMem shallowCopy(SqlJetVdbeMemFlags srcType) throws SqlJetException;

    /**
     * Make a full copy of pFrom into pTo. Prior contents of pTo are freed
     * before the copy is made.
     * 
     * @throws SqlJetException
     */
    ISqlJetVdbeMem copy() throws SqlJetException;

    /**
     * Transfer the contents of pFrom to pTo. Any existing value in pTo is
     * freed. If pFrom contains ephemeral data, a copy is made.
     * 
     * pFrom contains an SQL NULL when this routine returns.
     * 
     * @throws SqlJetException
     */
    ISqlJetVdbeMem move() throws SqlJetException;

    /**
     * Perform various checks on the memory cell pMem. An assert() will fail if
     * pMem is internally inconsistent.
     */
    void sanity();

    /**
     * Return the number of bytes in the sqlite3_value object assuming that it
     * uses the encoding "enc"
     * 
     * @throws SqlJetException
     */
    int valueBytes(SqlJetEncoding enc) throws SqlJetException;

    /**
     * Clear any existing type flags from a Mem and replace them with f
     * 
     * @param real
     */
    void setTypeFlag(SqlJetVdbeMemFlags f);

    Set<SqlJetVdbeMemFlags> getFlags();

    /**
     * @return
     */
    boolean isNull();

    /**
     * @return
     */
    SqlJetValueType getType();

    /**
     * Converts the object V into a BLOB and then returns a pointer to the
     * converted value.
     * 
     * @return
     * 
     * @throws SqlJetException 
     */
    ISqlJetMemoryPointer valueBlob() throws SqlJetException;

    /**
     * @param affinity
     * @param enc
     * @throws SqlJetException
     */
    void applyAffinity(SqlJetTypeAffinity affinity, SqlJetEncoding enc) throws SqlJetException;
    
}