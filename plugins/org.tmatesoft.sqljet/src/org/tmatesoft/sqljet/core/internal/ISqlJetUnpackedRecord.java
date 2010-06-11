/**
 * IUnpackedRecord.java
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

import org.tmatesoft.sqljet.core.SqlJetException;

/**
 * An instance of the following structure holds information about a single index
 * record that has already been parsed out into individual values.
 * 
 * A record is an object that contains one or more fields of data. Records are
 * used to store the content of a table row and to store the key of an index. A
 * blob encoding of a record is created by the OP_MakeRecord opcode of the VDBE
 * and is disassembled by the OP_Column opcode.
 * 
 * This structure holds a record that has already been disassembled into its
 * constituent fields.
 * 
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetUnpackedRecord {

    /**
     * This function compares the two table rows or index records specified by
     * {nKey1, pKey1} and pPKey2. It returns a negative, zero or positive
     * integer if key1 is less than, equal to or greater than key2. The {nKey1,
     * pKey1} key must be a blob created by th OP_MakeRecord opcode of the VDBE.
     * The pPKey2 key must be a parsed key such as obtained from
     * sqlite3VdbeParseRecord.
     * 
     * Key1 and Key2 do not have to contain the same number of fields. The key
     * with fewer fields is usually compares less than the longer key. However
     * if the UNPACKED_INCRKEY flags in pPKey2 is set and the common prefixes
     * are equal, then key1 is less than key2. Or if the UNPACKED_MATCH_PREFIX
     * flag is set and the prefixes are equal, then the keys are considered to
     * be equal and the parts beyond the common prefix are ignored.
     * 
     * If the UNPACKED_IGNORE_ROWID flag is set, then the last byte of the
     * header of pKey1 is ignored. It is assumed that pKey1 is an index key, and
     * thus ends with a rowid value. The last byte of the header will therefore
     * be the serial type of the rowid: one of 1, 2, 3, 4, 5, 6, 8, or 9 - the
     * integer serial types. The serial type of the final rowid will always be a
     * single byte. By ignoring this last byte of the header, we force the
     * comparison to ignore the rowid at the end of key1.
     * 
     * @param i
     * @param cellKey
     * @return
     * @throws SqlJetException 
     */
    int recordCompare(int i, ISqlJetMemoryPointer cellKey) throws SqlJetException;

}
