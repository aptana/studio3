/**
 * SqlJetUnpackedRecord.java
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

import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetUnpackedRecord;
import org.tmatesoft.sqljet.core.internal.SqlJetUnpackedRecordFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetUnpackedRecord implements ISqlJetUnpackedRecord {

    /* Collation and sort-order information */
    SqlJetKeyInfo pKeyInfo;

    /* Number of entries in apMem[] */
    int nField;

    /* Boolean settings. UNPACKED_... below */
    Set<SqlJetUnpackedRecordFlags> flags;

    /* Values */
    SqlJetVdbeMem[] aMem;

    /**
     * 
     */
    public static void delete(SqlJetUnpackedRecord p) {

        if (p != null) {

            if (p.flags.contains(SqlJetUnpackedRecordFlags.NEED_DESTROY)) {
                for (int i = 0; i < p.nField; i++) {
                    final SqlJetVdbeMem pMem = p.aMem[i];
                    if (pMem.zMalloc != null) {
                        pMem.release();
                    }
                }
            }

            /*
             * if( p.flags.contains(SqlJetUnpackedRecordFlags.NEED_FREE) ){
             * p.pKeyInfo.db.free(p); }
             */

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetUnpackedRecord#recordCompare(int,
     * java.nio.ByteBuffer)
     */
    public int recordCompare(int nKey1, ISqlJetMemoryPointer pKey1) throws SqlJetException {

        final SqlJetUnpackedRecord pPKey2 = this;

        int d1; /* Offset into aKey[] of next data element */
        int idx1; /* Offset into aKey[] of next header element */
        int[] szHdr1 = new int[1]; /* Number of bytes in header */
        int i = 0;
        int nField;
        int rc = 0;
        SqlJetKeyInfo pKeyInfo;
        SqlJetVdbeMem mem1 = new SqlJetVdbeMem();

        pKeyInfo = pPKey2.pKeyInfo;
        mem1.enc = pKeyInfo.enc;
        mem1.db = pKeyInfo.db;
        mem1.flags = SqlJetUtility.noneOf(SqlJetVdbeMemFlags.class);
        mem1.zMalloc = null;

        idx1 = SqlJetUtility.getVarint32(pKey1, szHdr1);
        d1 = szHdr1[0];
        if (pPKey2.flags.contains(SqlJetUnpackedRecordFlags.IGNORE_ROWID)) {
            szHdr1[0]--;
        }
        nField = pKeyInfo.nField;
        while (idx1 < szHdr1[0] && i < pPKey2.nField) {
            int[] serial_type1 = new int[1];

            /* Read the serial types for the next element in each key. */
            idx1 += SqlJetUtility.getVarint32(pKey1, idx1, serial_type1);
            if (d1 >= nKey1 && SqlJetVdbeSerialType.serialTypeLen(serial_type1[0]) > 0)
                break;

            /*
             * Extract the values to be compared.
             */
            d1 += SqlJetVdbeSerialType.serialGet(pKey1, d1, serial_type1[0], mem1);

            /*
             * Do the comparison
             */
            rc = SqlJetVdbeMem.compare(mem1, pPKey2.aMem[i], i < nField ? pKeyInfo.aColl[i] : null);
            if (rc != 0) {
                break;
            }
            i++;
        }
        if (mem1.zMalloc != null)
            mem1.release();

        if (rc == 0) {
            /*
             * rc==0 here means that one of the keys ran out of fields and* all
             * the fields up to that point were equal. If the UNPACKED_INCRKEY*
             * flag is set, then break the tie by treating key2 as larger.* If
             * the UPACKED_PREFIX_MATCH flag is set, then keys with common
             * prefixes* are considered to be equal. Otherwise, the longer key
             * is the* larger. As it happens, the pPKey2 will always be the
             * longer* if there is a difference.
             */
            if (pPKey2.flags.contains(SqlJetUnpackedRecordFlags.INCRKEY)) {
                rc = -1;
            } else if (pPKey2.flags.contains(SqlJetUnpackedRecordFlags.PREFIX_MATCH)) {
                /* Leave rc==0 */
            } else if (idx1 < szHdr1[0]) {
                rc = 1;
            }
        } else if (pKeyInfo.aSortOrder != null && i < pKeyInfo.nField && pKeyInfo.aSortOrder[i]) {
            rc = -rc;
        }

        return rc;
    }

    /**
     * @return the flags
     */
    public Set<SqlJetUnpackedRecordFlags> getFlags() {
        return flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(Set<SqlJetUnpackedRecordFlags> flags) {
        this.flags = flags;
    }
    
}
