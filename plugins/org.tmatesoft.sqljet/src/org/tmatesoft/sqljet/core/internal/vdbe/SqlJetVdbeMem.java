/**
 * SqlJetMem.java
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

import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.getUnsignedByte;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.memcpy;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.memmove;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.memset;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.mutex_held;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.pointer;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.putUnsignedByte;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.strlen30;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtreeCursor;
import org.tmatesoft.sqljet.core.internal.ISqlJetCallback;
import org.tmatesoft.sqljet.core.internal.ISqlJetCollSeq;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.ISqlJetFuncDef;
import org.tmatesoft.sqljet.core.internal.ISqlJetLimits;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetRowSet;
import org.tmatesoft.sqljet.core.internal.ISqlJetVdbeMem;
import org.tmatesoft.sqljet.core.internal.SqlJetCloneable;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.schema.SqlJetTypeAffinity;

/**
 * Internally, the vdbe manipulates nearly all SQL values as Mem structures.
 * Each Mem struct may cache multiple representations (string, integer etc.) of
 * the same value. A value (and therefore Mem structure) has the following
 * properties:
 * 
 * Each value has a manifest type. The manifest type of the value stored in a
 * Mem struct is returned by the MemType(Mem*) macro. The type is one of
 * SQLITE_NULL, SQLITE_INTEGER, SQLITE_REAL, SQLITE_TEXT or SQLITE_BLOB.
 * 
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetVdbeMem extends SqlJetCloneable implements ISqlJetVdbeMem {

    // union {

    /** Integer value. */
    long i;

    /** Used when bit MEM_Zero is set in flags */
    int nZero;

    /** Used only when flags==MEM_Agg */
    ISqlJetFuncDef pDef;

    /** Used only when flags==MEM_RowSet */
    ISqlJetRowSet pRowSet;

    // } u;

    /** Real value */
    double r;

    /** The associated database connection */
    ISqlJetDbHandle db;

    /** String or BLOB value */
    ISqlJetMemoryPointer z;

    /** Number of characters in string value, excluding '\0' */
    int n;

    /** Some combination of MEM_Null, MEM_Str, MEM_Dyn, etc. */
    EnumSet<SqlJetVdbeMemFlags> flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Null);

    /** One of SQLITE_NULL, SQLITE_TEXT, SQLITE_INTEGER, etc */
    SqlJetValueType type = SqlJetValueType.NULL;

    /** SQLITE_UTF8, SQLITE_UTF16BE, SQLITE_UTF16LE */
    SqlJetEncoding enc;

    /** If not null, call this function to delete Mem.z */
    ISqlJetCallback xDel;

    /** Dynamic buffer allocated by sqlite3_malloc() */
    ISqlJetMemoryPointer zMalloc;

    public SqlJetVdbeMem() {
        this.db = null;
    }

    public SqlJetVdbeMem(ISqlJetDbHandle db) {
        this.db = db;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.vdbe.ISqlJetVdbeMem#release()
     */
    public void release() {
        // releaseExternal();
        // sqlite3DbFree(p->db, p->zMalloc);
        z = null;
        zMalloc = null;
        xDel = null;
    }

    /**
     * Compare the values contained by the two memory cells, returning negative,
     * zero or positive if pMem1 is less than, equal to, or greater than pMem2.
     * Sorting order is NULL's first, followed by numbers (integers and reals)
     * sorted numerically, followed by text ordered by the collating sequence
     * pColl and finally blob's ordered by memcmp().Two NULL values are
     * considered equal by this function.
     */
    public static int compare(SqlJetVdbeMem pMem1, SqlJetVdbeMem pMem2, ISqlJetCollSeq pColl) throws SqlJetException {

        int rc;

        /*
         * Interchange pMem1 and pMem2 if the collating sequence specifies* DESC
         * order.
         */
        EnumSet<SqlJetVdbeMemFlags> f1 = pMem1.flags;
        EnumSet<SqlJetVdbeMemFlags> f2 = pMem2.flags;
        EnumSet<SqlJetVdbeMemFlags> combined_flags = EnumSet.copyOf(f1);
        combined_flags.addAll(f2);
        assert (!combined_flags.contains(SqlJetVdbeMemFlags.RowSet));

        /*
         * If one value is NULL, it is less than the other. If both values* are
         * NULL, return 0.
         */
        if (combined_flags.contains(SqlJetVdbeMemFlags.Null)) {
            return (f2.contains(SqlJetVdbeMemFlags.Null) ? 1 : 0) - (f1.contains(SqlJetVdbeMemFlags.Null) ? 1 : 0);
        }

        /*
         * If one value is a number and the other is not, the number is less.*
         * If both are numbers, compare as reals if one is a real, or as
         * integers* if both values are integers.
         */
        if (combined_flags.contains(SqlJetVdbeMemFlags.Int) || combined_flags.contains(SqlJetVdbeMemFlags.Real)) {
            if (!(f1.contains(SqlJetVdbeMemFlags.Int) || f1.contains(SqlJetVdbeMemFlags.Real))) {
                return 1;
            }
            if (!(f2.contains(SqlJetVdbeMemFlags.Int) || f2.contains(SqlJetVdbeMemFlags.Real))) {
                return -1;
            }
            if (!f1.contains(SqlJetVdbeMemFlags.Int) && !f2.contains(SqlJetVdbeMemFlags.Int)) {
                double r1, r2;
                if (!f1.contains(SqlJetVdbeMemFlags.Real)) {
                    r1 = (double) pMem1.i;
                } else {
                    r1 = pMem1.r;
                }
                if (!f2.contains(SqlJetVdbeMemFlags.Real)) {
                    r2 = (double) pMem2.i;
                } else {
                    r2 = pMem2.r;
                }
                if (r1 < r2)
                    return -1;
                if (r1 > r2)
                    return 1;
                return 0;
            } else {
                assert (f1.contains(SqlJetVdbeMemFlags.Int));
                assert (f2.contains(SqlJetVdbeMemFlags.Int));
                if (pMem1.i < pMem2.i)
                    return -1;
                if (pMem1.i > pMem2.i)
                    return 1;
                return 0;
            }
        }

        /*
         * If one value is a string and the other is a blob, the string is less.
         * * If both are strings, compare using the collating functions.
         */
        if (combined_flags.contains(SqlJetVdbeMemFlags.Str)) {
            if (!f1.contains(SqlJetVdbeMemFlags.Str)) {
                return 1;
            }
            if (!f2.contains(SqlJetVdbeMemFlags.Str)) {
                return -1;
            }

            assert (pMem1.enc == pMem2.enc);
            assert (pMem1.enc == SqlJetEncoding.UTF8 || pMem1.enc == SqlJetEncoding.UTF16LE || pMem1.enc == SqlJetEncoding.UTF16BE);

            /*
             * The collation sequence must be defined at this point, even if*
             * the user deletes the collation sequence after the vdbe program is
             * * compiled (this was not always the case).
             */
            if (pColl != null) {
                if (pMem1.enc == pColl.getEnc()) {
                    /*
                     * The strings are already in the correct encoding. Call the
                     * * comparison function directly
                     */
                    return pColl.cmp(pColl.getUserData(), pMem1.n, pMem1.z, pMem2.n, pMem2.z);
                } else {

                    ISqlJetMemoryPointer v1, v2;
                    int n1, n2;

                    SqlJetVdbeMem c1 = (SqlJetVdbeMem) pMem1.shallowCopy(SqlJetVdbeMemFlags.Ephem);
                    SqlJetVdbeMem c2 = (SqlJetVdbeMem) pMem2.shallowCopy(SqlJetVdbeMemFlags.Ephem);
                    v1 = c1.valueText(pColl.getEnc());
                    n1 = v1 == null ? 0 : c1.n;
                    v2 = c2.valueText(pColl.getEnc());
                    n2 = v2 == null ? 0 : c2.n;
                    c1.release();
                    c2.release();
                    return pColl.cmp(pColl.getUserData(), n1, v1, n2, v2);

                }
            }
            /*
             * If a NULL pointer was passed as the collate function, fall
             * through to the blob case and use memcmp().
             */
        }

        /* Both values must be blobs. Compare using memcmp(). */
        rc = SqlJetUtility.memcmp(pMem1.z, pMem2.z, (pMem1.n > pMem2.n) ? pMem2.n : pMem1.n);
        if (rc == 0) {
            rc = pMem1.n - pMem2.n;
        }
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetVdbeMem#shallowCopy(org.tmatesoft.sqljet
     * .core.internal.vdbe.SqlJetVdbeMemFlags)
     */
    public ISqlJetVdbeMem shallowCopy(SqlJetVdbeMemFlags srcType) throws SqlJetException {
        final SqlJetVdbeMem pFrom = this;
        assert (!pFrom.flags.contains(SqlJetVdbeMemFlags.RowSet));
        final SqlJetVdbeMem pTo = memcpy(pFrom);
        if (pFrom.flags.contains(SqlJetVdbeMemFlags.Dyn) || pFrom.z == pFrom.zMalloc) {
            pTo.flags.removeAll(SqlJetUtility.of(SqlJetVdbeMemFlags.Dyn, SqlJetVdbeMemFlags.Static,
                    SqlJetVdbeMemFlags.Ephem));
            assert (srcType == SqlJetVdbeMemFlags.Ephem || srcType == SqlJetVdbeMemFlags.Static);
            pTo.flags.add(srcType);
        }
        return pTo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#copy()
     */
    public ISqlJetVdbeMem copy() throws SqlJetException {
        final SqlJetVdbeMem pFrom = this;
        assert (!pFrom.flags.contains(SqlJetVdbeMemFlags.RowSet));
        final SqlJetVdbeMem pTo = SqlJetUtility.memcpy(pFrom);
        pTo.flags.remove(SqlJetVdbeMemFlags.Dyn);
        if (pTo.flags.contains(SqlJetVdbeMemFlags.Str) || pTo.flags.contains(SqlJetVdbeMemFlags.Blob)) {
            if (!pFrom.flags.contains(SqlJetVdbeMemFlags.Static)) {
                pTo.flags.add(SqlJetVdbeMemFlags.Ephem);
                pTo.makeWriteable();
            }
        }
        return pTo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetVdbeMem#move(org.tmatesoft.sqljet.core
     * .ISqlJetVdbeMem, org.tmatesoft.sqljet.core.ISqlJetVdbeMem)
     */
    public ISqlJetVdbeMem move() throws SqlJetException {
        assert (db == null || mutex_held(db.getMutex()));
        SqlJetVdbeMem pTo = SqlJetUtility.memcpy(this);
        this.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Null);
        this.xDel = null;
        this.zMalloc = null;
        return pTo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.vdbe.ISqlJetVdbeMem#valueText(org.
     * tmatesoft.sqljet.core.SqlJetEncoding)
     */
    public ISqlJetMemoryPointer valueText(SqlJetEncoding enc) throws SqlJetException {
        // if( !pVal ) return 0;

        final SqlJetVdbeMem pVal = this;

        assert (pVal.db == null || mutex_held(pVal.db.getMutex()));
        // assert( (enc&3)==(enc&~SQLITE_UTF16_ALIGNED) );
        assert (!pVal.flags.contains(SqlJetVdbeMemFlags.RowSet));

        if (pVal.flags.contains(SqlJetVdbeMemFlags.Null)) {
            return null;
        }
        // assert( (MEM_Blob>>3) == MEM_Str );
        // pVal.flags |= (pVal.flags & MEM_Blob)>>3;
        if (pVal.flags.contains(SqlJetVdbeMemFlags.Blob)) {
            pVal.flags.add(SqlJetVdbeMemFlags.Str);
        }
        pVal.expandBlob();
        if (pVal.flags.contains(SqlJetVdbeMemFlags.Str)) {
            pVal.changeEncoding(enc);
            /*
             * if( (enc & SQLITE_UTF16_ALIGNED)!=0 &&
             * 1==(1&SQLITE_PTR_TO_INT(pVal->z)) ){ assert( (pVal->flags &
             * (MEM_Ephem|MEM_Static))!=0 ); if(
             * sqlite3VdbeMemMakeWriteable(pVal)!=SQLITE_OK ){ return 0; } }
             */
            pVal.makeWriteable();
            pVal.nulTerminate();
        } else {
            assert (!pVal.flags.contains(SqlJetVdbeMemFlags.Blob));
            pVal.stringify(enc);
            // assert( 0==(1&SQLITE_PTR_TO_INT(pVal->z)) );
        }
        /*
         * assert(pVal->enc==(enc & ~SQLITE_UTF16_ALIGNED) || pVal->db==0 ||
         * pVal->db->mallocFailed ); if( pVal->enc==(enc &
         * ~SQLITE_UTF16_ALIGNED) ){ return pVal->z; }else{ return 0; }
         */
        return pVal.z;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetVdbeMem#stringify(org.tmatesoft.sqljet
     * .core.SqlJetEncoding)
     */
    public void stringify(SqlJetEncoding enc) throws SqlJetException {

        final SqlJetVdbeMem pMem = this;

        final Set<SqlJetVdbeMemFlags> fg = pMem.flags;
        final int nByte = 32;

        assert (pMem.db == null || mutex_held(pMem.db.getMutex()));
        assert (!fg.contains(SqlJetVdbeMemFlags.Zero));
        assert (!(fg.contains(SqlJetVdbeMemFlags.Str) || fg.contains(SqlJetVdbeMemFlags.Blob)));
        assert (fg.contains(SqlJetVdbeMemFlags.Int) || fg.contains(SqlJetVdbeMemFlags.Real));
        assert (!pMem.flags.contains(SqlJetVdbeMemFlags.RowSet));

        pMem.grow(nByte, false);

        /*
         * For a Real or Integer, use sqlite3_mprintf() to produce the UTF-8*
         * string representation of the value. Then, if the required encoding*
         * is UTF-16le or UTF-16be do a translation.** FIX ME: It would be
         * better if sqlite3_snprintf() could do UTF-16.
         */
        if (fg.contains(SqlJetVdbeMemFlags.Int)) {
            // sqlite3_snprintf(nByte, pMem->z, "%lld", pMem->u.i);
            pMem.z.putBytes(Long.toString(pMem.i).getBytes());
        } else {
            assert (fg.contains(SqlJetVdbeMemFlags.Real));
            // sqlite3_snprintf(nByte, pMem->z, "%!.15g", pMem->r);
            pMem.z.putBytes(Double.toString(pMem.r).getBytes());
        }
        pMem.n = strlen30(pMem.z);
        pMem.enc = SqlJetEncoding.UTF8;
        pMem.flags.add(SqlJetVdbeMemFlags.Str);
        pMem.flags.add(SqlJetVdbeMemFlags.Term);
        pMem.changeEncoding(enc);
        type = SqlJetValueType.TEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.vdbe.ISqlJetVdbeMem#grow(int,
     * boolean)
     */
    public void grow(int n, boolean preserve) {

        final SqlJetVdbeMem pMem = this;

        assert (1 >= ((pMem.zMalloc != null && pMem.zMalloc == pMem.z) ? 1 : 0)
                + ((pMem.flags.contains(SqlJetVdbeMemFlags.Dyn) && pMem.xDel != null) ? 1 : 0)
                + (pMem.flags.contains(SqlJetVdbeMemFlags.Ephem) ? 1 : 0)
                + (pMem.flags.contains(SqlJetVdbeMemFlags.Static) ? 1 : 0));
        assert (!pMem.flags.contains(SqlJetVdbeMemFlags.RowSet));

        if (n < 32)
            n = 32;
        /*
         * if( sqlite3DbMallocSize(pMem->db, pMem->zMalloc)<n ){ if( preserve &&
         * pMem->z==pMem->zMalloc ){ pMem->z = pMem->zMalloc =
         * sqlite3DbReallocOrFree(pMem->db, pMem->z, n); preserve = 0; }else{
         * sqlite3DbFree(pMem->db, pMem->zMalloc); pMem->zMalloc =
         * sqlite3DbMallocRaw(pMem->db, n); } }
         */

        pMem.zMalloc = SqlJetUtility.allocatePtr(n);

        if (preserve && pMem.z != null) {
            memcpy(pMem.zMalloc, pMem.z, pMem.n);
        }
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Dyn) && pMem.xDel != null) {
            pMem.xDel.call(pMem.z);
        }
        pMem.z = pMem.zMalloc;
        if (pMem.z == null) { // WTF? /sergey/
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Null);
        } else {
            pMem.flags.remove(SqlJetVdbeMemFlags.Ephem);
            pMem.flags.remove(SqlJetVdbeMemFlags.Static);
        }
        pMem.xDel = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#nulTerminate()
     */
    public void nulTerminate() {
        final SqlJetVdbeMem pMem = this;
        assert (pMem.db == null || mutex_held(pMem.db.getMutex()));
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Term) || !pMem.flags.contains(SqlJetVdbeMemFlags.Str)) {
            return; /* Nothing to do */
        }
        pMem.grow(pMem.n + 2, true);
        pMem.z.putByte(pMem.n, (byte) 0);
        pMem.z.putByte(pMem.n + 1, (byte) 0);
        pMem.flags.add(SqlJetVdbeMemFlags.Term);
        pMem.z.limit(pMem.n);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetVdbeMem#changeEncoding(org.tmatesoft
     * .sqljet.core.SqlJetEncoding)
     */
    public void changeEncoding(SqlJetEncoding desiredEnc) throws SqlJetException {
        final SqlJetVdbeMem pMem = this;
        assert (!pMem.flags.contains(SqlJetVdbeMemFlags.RowSet));
        assert (desiredEnc == SqlJetEncoding.UTF8 || desiredEnc == SqlJetEncoding.UTF16LE || desiredEnc == SqlJetEncoding.UTF16BE);
        if (!pMem.flags.contains(SqlJetVdbeMemFlags.Str) || pMem.enc == desiredEnc) {
            return;
        }
        assert (pMem.db == null || mutex_held(pMem.db.getMutex()));

        /*
         * MemTranslate() may return SQLITE_OK or SQLITE_NOMEM. If NOMEM is
         * returned, then the encoding of the value may not have changed.
         */
        pMem.translate(desiredEnc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetVdbeMem#translate(org.tmatesoft.sqljet
     * .core.SqlJetEncoding)
     */
    public void translate(SqlJetEncoding desiredEnc) throws SqlJetException {

        final SqlJetVdbeMem pMem = this;

        int len; /* Maximum length of output string in bytes */

        ISqlJetMemoryPointer zOut; /* Output buffer */
        int zIn; /* Input iterator */
        int zTerm; /* End of input */
        // int z; /* Output iterator */

        // long c;

        assert (pMem.db == null || mutex_held(pMem.db.getMutex()));
        assert (pMem.flags.contains(SqlJetVdbeMemFlags.Str));
        assert (pMem.enc != desiredEnc);
        assert (pMem.enc != null);
        assert (pMem.n >= 0);

        /*
         * If the translation is between UTF-16 little and big endian, then* all
         * that is required is to swap the byte order. This case is handled*
         * differently from the others.
         */
        if (pMem.enc != SqlJetEncoding.UTF8 && desiredEnc != SqlJetEncoding.UTF8) {
            short temp;
            pMem.makeWriteable();
            zIn = 0;
            zTerm = pMem.n & ~1;
            while (zIn < zTerm) {
                temp = (short) SqlJetUtility.getUnsignedByte(pMem.z, zIn);
                SqlJetUtility.putUnsignedByte(pMem.z, zIn, SqlJetUtility.getUnsignedByte(pMem.z, zIn + 1));
                zIn++;
                SqlJetUtility.putUnsignedByte(pMem.z, zIn++, temp);
            }
            pMem.enc = desiredEnc;
            return;
        }

        /* Set len to the maximum number of bytes required in the output buffer. */
        if (desiredEnc == SqlJetEncoding.UTF8) {
            /*
             * When converting from UTF-16, the maximum growth results from*
             * translating a 2-byte character to a 4-byte UTF-8 character.* A
             * single byte is required for the output string* nul-terminator.
             */
            pMem.n &= ~1;
            len = pMem.n * 2 + 1;
        } else {
            /*
             * When converting from UTF-8 to UTF-16 the maximum growth is caused
             * * when a 1-byte UTF-8 character is translated into a 2-byte
             * UTF-16* character. Two bytes are required in the output buffer
             * for the* nul-terminator.
             */
            len = pMem.n * 2 + 2;
        }

        /*
         * Set zIn to point at the start of the input buffer and zTerm to point
         * 1* byte past the end.** Variable zOut is set to point at the output
         * buffer, space obtained* from sqlite3_malloc().
         */
        zOut = SqlJetUtility.translate(pMem.z, pMem.enc, desiredEnc);
        pMem.n = zOut.remaining();

        assert ((pMem.n + (desiredEnc == SqlJetEncoding.UTF8 ? 1 : 2)) <= len);

        pMem.release();
        pMem.flags.removeAll(SqlJetUtility.of(SqlJetVdbeMemFlags.Static, SqlJetVdbeMemFlags.Dyn,
                SqlJetVdbeMemFlags.Ephem));
        pMem.enc = desiredEnc;
        pMem.flags.addAll(SqlJetUtility.of(SqlJetVdbeMemFlags.Term, SqlJetVdbeMemFlags.Dyn));
        pMem.z = zOut;
        pMem.zMalloc = pMem.z;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#expandBlob()
     */
    public void expandBlob() {
        final SqlJetVdbeMem pMem = this;
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Zero)) {
            int nByte;
            assert (pMem.flags.contains(SqlJetVdbeMemFlags.Blob));
            assert (!pMem.flags.contains(SqlJetVdbeMemFlags.RowSet));
            assert (pMem.db == null || mutex_held(pMem.db.getMutex()));

            /*
             * Set nByte to the number of bytes required to store the expanded
             * blob.
             */
            nByte = pMem.n + pMem.nZero;
            if (nByte <= 0) {
                nByte = 1;
            }
            pMem.grow(nByte, true);
            memset(pMem.z, pMem.n, (byte) 0, pMem.nZero);
            pMem.n += pMem.nZero;
            pMem.flags.removeAll(SqlJetUtility.of(SqlJetVdbeMemFlags.Zero, SqlJetVdbeMemFlags.Term));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.vdbe.ISqlJetVdbeMem#fromBtree(org.
     * tmatesoft.sqljet.core.ISqlJetBtreeCursor, int, int, boolean)
     */
    public void fromBtree(ISqlJetBtreeCursor pCur, int offset, int amt, boolean key) throws SqlJetException {

        assert (mutex_held(pCur.getCursorDb().getMutex()));

        SqlJetVdbeMem pMem = this;

        /* Data from the btree layer */
        ISqlJetMemoryPointer zData;
        /* Number of bytes available on the local btree page */
        int[] available = { 0 };

        if (key) {
            zData = pCur.keyFetch(available);
        } else {
            zData = pCur.dataFetch(available);
        }
        assert (zData != null);

        if (offset + amt <= available[0]) {
            pMem.release();
            pMem.z = pointer(zData, offset);
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Blob, SqlJetVdbeMemFlags.Ephem);
        } else {
            pMem.grow(amt + 2, false);
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Blob, SqlJetVdbeMemFlags.Dyn, SqlJetVdbeMemFlags.Term);
            pMem.enc = null;
            pMem.type = SqlJetValueType.BLOB;
            try {
                if (key) {
                    pCur.key(offset, amt, pMem.z);
                } else {
                    pCur.data(offset, amt, pMem.z);
                }
            } catch (SqlJetException e) {
                pMem.release();
                throw e;
            } finally {
                if (pMem.z != null) {
                    SqlJetUtility.putUnsignedByte(pMem.z, amt, (byte) 0);
                    SqlJetUtility.putUnsignedByte(pMem.z, amt + 1, (byte) 0);
                }
            }
        }
        pMem.n = amt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.vdbe.ISqlJetVdbeMem#makeWriteable()
     */
    public void makeWriteable() {
        SqlJetVdbeMem pMem = this;
        assert (pMem.db == null || mutex_held(pMem.db.getMutex()));
        assert (!pMem.flags.contains(SqlJetVdbeMemFlags.RowSet));
        pMem.expandBlob();
        if ((pMem.flags.contains(SqlJetVdbeMemFlags.Str) || pMem.flags.contains(SqlJetVdbeMemFlags.Blob))
                && pMem.z != pMem.zMalloc) {
            pMem.grow(pMem.n + 2, true);
            putUnsignedByte(pMem.z, pMem.n, (byte) 0);
            putUnsignedByte(pMem.z, pMem.n + 1, (byte) 0);
            pMem.flags.add(SqlJetVdbeMemFlags.Term);
            pMem.z.limit(pMem.n);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#intValue()
     */
    public long intValue() {
        assert (db == null || mutex_held(db.getMutex()));
        if (flags.contains(SqlJetVdbeMemFlags.Int)) {
            return i;
        } else if (flags.contains(SqlJetVdbeMemFlags.Real)) {
            return (long) r;
        } else if (flags.contains(SqlJetVdbeMemFlags.Str) || flags.contains(SqlJetVdbeMemFlags.Blob)) {
            long value = 0;
            /*
             * pMem->flags |= MEM_Str; if( sqlite3VdbeChangeEncoding(pMem,
             * SQLITE_UTF8) || sqlite3VdbeMemNulTerminate(pMem) ){ return 0; }
             * assert( pMem->z ); sqlite3Atoi64(pMem->z, &value);
             */
            flags.add(SqlJetVdbeMemFlags.Str);
            try {
                changeEncoding(SqlJetEncoding.UTF8);
                nulTerminate();
            } catch (SqlJetException e) {
                return 0;
            }
            value = SqlJetUtility.atoi64(SqlJetUtility.toString(this.z));
            return value;
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#setNull()
     */
    public void setNull() {
        if (flags.contains(SqlJetVdbeMemFlags.RowSet)) {
            // sqlite3RowSetClear(pMem->u.pRowSet);
        }
        flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Null);
        type = SqlJetValueType.NULL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#setStr(java.nio.ByteBuffer,
     * org.tmatesoft.sqljet.core.SqlJetEncoding)
     */
    public void setStr(ISqlJetMemoryPointer z, SqlJetEncoding enc) throws SqlJetException {

        assert (db == null || mutex_held(db.getMutex()));
        assert (!flags.contains(SqlJetVdbeMemFlags.RowSet));

        /* If z is a NULL pointer, set pMem to contain an SQL NULL. */
        if (z == null) {
            setNull();
            return;
        }

        int nByte = z.remaining(); /* New value for pMem->n */
        /* Maximum allowed string or blob size */        
        int iLimit = ISqlJetLimits.SQLJET_MAX_LENGTH; 
        /* New value for pMem->flags */        
        flags = SqlJetUtility.noneOf(SqlJetVdbeMemFlags.class); 

        flags.add(enc == null ? SqlJetVdbeMemFlags.Blob : SqlJetVdbeMemFlags.Str);

        if (nByte > iLimit) {
            throw new SqlJetException(SqlJetErrorCode.TOOBIG);
        }

        this.z = z;
        this.n = nByte;
        this.enc = (enc == null ? SqlJetEncoding.UTF8 : enc);
        this.type = (enc == null ? SqlJetValueType.BLOB : SqlJetValueType.TEXT);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#setInt64(long)
     */
    public void setInt64(long val) {
        release();
        i = val;
        flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Int);
        type = SqlJetValueType.INTEGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#realValue()
     */
    public double realValue() {
        final SqlJetVdbeMem pMem = this;
        assert (pMem.db == null || pMem.db.getMutex().held());
        if (pMem.flags.contains(SqlJetVdbeMemFlags.Real)) {
            return pMem.r;
        } else if (pMem.flags.contains(SqlJetVdbeMemFlags.Int)) {
            return (double) pMem.i;
        } else if (pMem.flags.contains(SqlJetVdbeMemFlags.Str) || pMem.flags.contains(SqlJetVdbeMemFlags.Blob)) {
            double val = 0.0;
            pMem.flags.add(SqlJetVdbeMemFlags.Str);
            try {
                pMem.changeEncoding(SqlJetEncoding.UTF8);
                pMem.nulTerminate();
            } catch (SqlJetException e) {
                return 0.0;
            }
            assert (pMem.z != null);
            val = SqlJetUtility.atof(pMem.z);
            return val;
        } else {
            return 0.0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#integerAffinity()
     */
    public void integerAffinity() {
        final SqlJetVdbeMem pMem = this;
        assert (pMem.flags.contains(SqlJetVdbeMemFlags.Real));
        assert (!pMem.flags.contains(SqlJetVdbeMemFlags.RowSet));
        assert (pMem.db == null || pMem.db.getMutex().held());
        pMem.i = (long) pMem.r;
        if (pMem.r == (double) pMem.i) {
            pMem.flags.add(SqlJetVdbeMemFlags.Int);
            type = SqlJetValueType.INTEGER;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#integerify()
     */
    public void integerify() {
        final SqlJetVdbeMem pMem = this;
        assert (pMem.db == null || pMem.db.getMutex().held());
        assert (!pMem.flags.contains(SqlJetVdbeMemFlags.RowSet));
        pMem.i = pMem.intValue();
        pMem.setTypeFlag(SqlJetVdbeMemFlags.Int);
        type = SqlJetValueType.INTEGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#realify()
     */
    public void realify() {
        final SqlJetVdbeMem pMem = this;
        assert (pMem.db == null || pMem.db.getMutex().held());
        pMem.r = pMem.realValue();
        pMem.setTypeFlag(SqlJetVdbeMemFlags.Real);
        type = SqlJetValueType.FLOAT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#numerify()
     */
    public void numerify() {
        final SqlJetVdbeMem pMem = this;
        double r1, r2;
        long i;
        assert (!(pMem.flags.contains(SqlJetVdbeMemFlags.Int) || pMem.flags.contains(SqlJetVdbeMemFlags.Real) || pMem.flags
                .contains(SqlJetVdbeMemFlags.Null)));
        assert (pMem.flags.contains(SqlJetVdbeMemFlags.Str) || pMem.flags.contains(SqlJetVdbeMemFlags.Blob));
        assert (pMem.db == null || pMem.db.getMutex().held());
        r1 = pMem.realValue();
        i = (long) r1;
        r2 = (double) i;
        if (r1 == r2) {
            pMem.integerify();
        } else {
            pMem.r = r1;
            pMem.setTypeFlag(SqlJetVdbeMemFlags.Real);
            type = SqlJetValueType.FLOAT;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetVdbeMem#setTypeFlag(org.tmatesoft.sqljet
     * .core.internal.vdbe.SqlJetVdbeMemFlags)
     */
    public void setTypeFlag(SqlJetVdbeMemFlags f) {
        final Iterator<SqlJetVdbeMemFlags> iterator = flags.iterator();
        while (iterator.hasNext()) {
            final SqlJetVdbeMemFlags flag = iterator.next();
            if (flag.ordinal() < SqlJetVdbeMemFlags.TypeMask.ordinal() || flag == SqlJetVdbeMemFlags.Zero)
                iterator.remove();
        }
        flags.add(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#setZeroBlob(int)
     */
    public void setZeroBlob(int n) {
        final SqlJetVdbeMem pMem = this;
        pMem.release();
        pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Blob, SqlJetVdbeMemFlags.Zero);
        pMem.type = SqlJetValueType.BLOB;
        pMem.n = 0;
        if (n < 0)
            n = 0;
        pMem.nZero = n;
        pMem.enc = SqlJetEncoding.UTF8;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#setDouble(double)
     */
    public void setDouble(double val) {
        final SqlJetVdbeMem pMem = this;
        if (Double.isNaN(val)) {
            pMem.setNull();
        } else {
            pMem.release();
            pMem.r = val;
            pMem.flags = SqlJetUtility.of(SqlJetVdbeMemFlags.Real);
            pMem.type = SqlJetValueType.FLOAT;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#setRowSet()
     */
    public void setRowSet() {
        final SqlJetVdbeMem pMem = this;
        final ISqlJetDbHandle db = pMem.db;
        assert (db != null);
        if (pMem.flags.contains(SqlJetVdbeMemFlags.RowSet)) {
            pMem.pRowSet.clear();
        } else {
            pMem.release();
            pMem.pRowSet = new SqlJetRowSet(db);
            pMem.flags.add(SqlJetVdbeMemFlags.RowSet);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#isTooBig()
     */
    public boolean isTooBig() {
        final SqlJetVdbeMem p = this;
        assert (p.db != null);
        if (p.flags.contains(SqlJetVdbeMemFlags.Str) || p.flags.contains(SqlJetVdbeMemFlags.Blob)) {
            int n = p.n;
            if (p.flags.contains(SqlJetVdbeMemFlags.Zero)) {
                n += p.nZero;
            }
            return n > ISqlJetLimits.SQLJET_MAX_LENGTH;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#sanity()
     */
    public void sanity() {
        final SqlJetVdbeMem pMem = this;
        final Set<SqlJetVdbeMemFlags> flags = pMem.flags;

        assert (flags != null && flags.size() > 0); /* Must define some type */
        if (flags.contains(SqlJetVdbeMemFlags.Str) || flags.contains(SqlJetVdbeMemFlags.Blob)) {
            int x = (flags.contains(SqlJetVdbeMemFlags.Static) ? 1 : 0)
                    + (flags.contains(SqlJetVdbeMemFlags.Dyn) ? 1 : 0)
                    + (flags.contains(SqlJetVdbeMemFlags.Ephem) ? 1 : 0);
            /* Strings must define a string subtype */
            /* Only one string subtype can be defined */
            assert (x == 1);
            assert (pMem.z != null); /* Strings must have a value */
            /* No destructor unless there is MEM_Dyn */
            assert (pMem.xDel == null || flags.contains(SqlJetVdbeMemFlags.Dyn));

            if (flags.contains(SqlJetVdbeMemFlags.Str)) {
                assert (pMem.enc == SqlJetEncoding.UTF8 || pMem.enc == SqlJetEncoding.UTF16BE || pMem.enc == SqlJetEncoding.UTF16LE);
                /*
                 * If the string is UTF-8 encoded and nul terminated, then
                 * pMem->n* must be the length of the string. (Later:) If the
                 * database file* has been corrupted, null characters might have
                 * been inserted* into the middle of the string. In that case,
                 * the sqlite3Strlen30()* might be less.
                 */
                if (pMem.enc == SqlJetEncoding.UTF8 && flags.contains(SqlJetVdbeMemFlags.Term)) {
                    assert (SqlJetUtility.strlen30(pMem.z) <= pMem.n);
                    assert (SqlJetUtility.getUnsignedByte(pMem.z, pMem.n) == 0);
                }
            }
        } else {
            /* Cannot define a string subtype for non-string objects */
            assert (!(pMem.flags.contains(SqlJetVdbeMemFlags.Static) || pMem.flags.contains(SqlJetVdbeMemFlags.Dyn) || pMem.flags
                    .contains(SqlJetVdbeMemFlags.Ephem)));
            assert (pMem.xDel == null);
        }
        /* MEM_Null excludes all other types */
        assert (!(pMem.flags.contains(SqlJetVdbeMemFlags.Static) || pMem.flags.contains(SqlJetVdbeMemFlags.Dyn) || pMem.flags
                .contains(SqlJetVdbeMemFlags.Ephem)) || !pMem.flags.contains(SqlJetVdbeMemFlags.Null));
        /* If the MEM is both real and integer, the values are equal */
        assert (pMem.flags.contains(SqlJetVdbeMemFlags.Int) && pMem.flags.contains(SqlJetVdbeMemFlags.Real) && pMem.r == pMem.i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetVdbeMem#valueBytes(org.tmatesoft.sqljet
     * .core.SqlJetEncoding)
     */
    public int valueBytes(SqlJetEncoding enc) throws SqlJetException {
        SqlJetVdbeMem p = this;
        if (p.flags.contains(SqlJetVdbeMemFlags.Blob) || p.valueText(enc) != null) {
            if (p.flags.contains(SqlJetVdbeMemFlags.Zero)) {
                return p.n + p.nZero;
            } else {
                return p.n;
            }
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#handleBom()
     */
    public void handleBom() {

        final SqlJetVdbeMem pMem = this;

        SqlJetEncoding bom = null;

        if (pMem.n < 0 || pMem.n > 1) {
            short b1 = (short) getUnsignedByte(pMem.z, 0);
            short b2 = (short) getUnsignedByte(pMem.z, 1);
            if (b1 == 0xFE && b2 == 0xFF) {
                bom = SqlJetEncoding.UTF16BE;
            }
            if (b1 == 0xFF && b2 == 0xFE) {
                bom = SqlJetEncoding.UTF16LE;
            }
        }

        if (null != bom) {
            pMem.makeWriteable();
            pMem.n -= 2;
            memmove(pMem.z, 0, pMem.z, 2, pMem.n);
            putUnsignedByte(pMem.z, pMem.n, (byte) 0);
            putUnsignedByte(pMem.z, pMem.n + 1, (byte) 0);
            pMem.flags.add(SqlJetVdbeMemFlags.Term);
            pMem.enc = bom;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetVdbeMem#getFlags()
     */
    public Set<SqlJetVdbeMemFlags> getFlags() {
        return flags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetVdbeMem#isNull()
     */
    public boolean isNull() {
        if (null == flags)
            return true;
        return flags.contains(SqlJetVdbeMemFlags.Null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetVdbeMem#getType()
     */
    public SqlJetValueType getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetVdbeMem#valueBlob()
     */
    public ISqlJetMemoryPointer valueBlob() throws SqlJetException {
        if (flags.contains(SqlJetVdbeMemFlags.Str) || flags.contains(SqlJetVdbeMemFlags.Blob)) {
            expandBlob();
            flags.remove(SqlJetVdbeMemFlags.Str);
            flags.add(SqlJetVdbeMemFlags.Blob);
            z.limit(n);
            return z;
        } else {
            return valueText(SqlJetEncoding.UTF8);
        }
    }

    /**
     * Processing is determine by the affinity parameter:
     * 
     * <table>
     * <tr>
     * <td>
     * <ul>
     * <li>AFF_INTEGER:</li>
     * <li>AFF_REAL:</li>
     * <li>AFF_NUMERIC:</li>
     * </ul>
     * </td>
     * <td></td>
     * </tr>
     * <tr>
     * <td></td>
     * <td>
     * Try to convert value to an integer representation or a floating-point
     * representation if an integer representation is not possible. Note that
     * the integer representation is always preferred, even if the affinity is
     * REAL, because an integer representation is more space efficient on disk.</td>
     * </tr>
     * <tr>
     * <td>
     * <ul>
     * <li>AFF_TEXT:</li>
     * </ul>
     * </td>
     * <td></td>
     * </tr>
     * <tr>
     * <td></td>
     * <td>Convert value to a text representation.</td>
     * </tr>
     * 
     * <tr>
     * <td>
     * <ul>
     * <li>AFF_NONE:</li>
     * </ul>
     * </td>
     * <td></td>
     * </tr>
     * <tr>
     * <td></td>
     * <td>No-op. value is unchanged.</td>
     * </tr>
     * </table>
     * 
     * @param affinity
     *            The affinity to be applied
     * @param enc
     *            Use this text encoding
     * 
     * @throws SqlJetException
     */
    public void applyAffinity(SqlJetTypeAffinity affinity, SqlJetEncoding enc) throws SqlJetException {
        if (affinity == SqlJetTypeAffinity.TEXT) {
            /*
             * Only attempt the conversion to TEXT if there is an integer or
             * real representation (blob and NULL do not get converted) but no
             * string representation.
             */
            if (!flags.contains(SqlJetVdbeMemFlags.Str)
                    && (flags.contains(SqlJetVdbeMemFlags.Real) || flags.contains(SqlJetVdbeMemFlags.Int))) {
                stringify(enc);
            }
            flags.remove(SqlJetVdbeMemFlags.Real);
            flags.remove(SqlJetVdbeMemFlags.Int);
        } else if (affinity != SqlJetTypeAffinity.NONE) {
            assert (affinity == SqlJetTypeAffinity.INTEGER || affinity == SqlJetTypeAffinity.REAL || affinity == SqlJetTypeAffinity.NUMERIC);
            applyNumericAffinity();
            if (flags.contains(SqlJetVdbeMemFlags.Real)) {
                applyIntegerAffinity();
            }
        }
    }

    /**
     * Try to convert a value into a numeric representation if we can do so
     * without loss of information. In other words, if the string looks like a
     * number, convert it into a number. If it does not look like a number,
     * leave it alone.
     * 
     * @throws SqlJetException
     */
    public void applyNumericAffinity() throws SqlJetException {
        if (!flags.contains(SqlJetVdbeMemFlags.Real) && !flags.contains(SqlJetVdbeMemFlags.Int)) {
            boolean[] realnum = { false };
            nulTerminate();
            if (flags.contains(SqlJetVdbeMemFlags.Str)
                    && SqlJetUtility.isNumber(SqlJetUtility.toString(z, enc), realnum)) {
                Long value;
                changeEncoding(SqlJetEncoding.UTF8);
                if (!realnum[0] && (value = SqlJetUtility.atoi64(SqlJetUtility.toString(z))) != null) {
                    i = value;
                    setTypeFlag(SqlJetVdbeMemFlags.Int);
                    type = SqlJetValueType.INTEGER;
                } else {
                    realify();
                }
            }
        } else if (type != SqlJetValueType.INTEGER && type != SqlJetValueType.FLOAT) {
            if (flags.contains(SqlJetVdbeMemFlags.Int)) {
                type = SqlJetValueType.INTEGER;
            } else if (flags.contains(SqlJetVdbeMemFlags.Real)) {
                type = SqlJetValueType.FLOAT;
            }
        }
    }

    /**
     * The MEM structure is already a MEM_Real. Try to also make it a MEM_Int if
     * we can.
     */
    void applyIntegerAffinity() {
        assert (flags.contains(SqlJetVdbeMemFlags.Real));
        assert (!flags.contains(SqlJetVdbeMemFlags.RowSet));
        assert (db == null || SqlJetUtility.mutex_held(db.getMutex()));
        final Long l = SqlJetUtility.doubleToInt64(r);
        if (l != null) {
            i = l;
            flags.add(SqlJetVdbeMemFlags.Int);
            type = SqlJetValueType.INTEGER;
        }
    }
}
