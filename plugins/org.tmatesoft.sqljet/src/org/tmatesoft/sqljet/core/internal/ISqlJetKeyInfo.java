/**
 * ISqlJetKeyInfo.java
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

import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetUnpackedRecord;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public interface ISqlJetKeyInfo {

    /**
     * Given the nKey-byte encoding of a record in pKey[], parse the
     * record into a UnpackedRecord structure.  Return a pointer to
     * that structure.
     *
     * The calling function might provide szSpace bytes of memory
     * space at pSpace.  This space can be used to hold the returned
     * VDbeParsedRecord structure if it is large enough.  If it is
     * not big enough, space is obtained from sqlite3_malloc().
     *
     * The returned structure should be closed by a call to
     * sqlite3VdbeDeleteUnpackedRecord().
     * 
     * @param nKey Size of the binary record
     * @param pKey The binary record
     * @return
     */
    SqlJetUnpackedRecord recordUnpack(int nKey, ISqlJetMemoryPointer pKey);

}
