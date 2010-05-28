/**
 * ISqlJetRecord.java
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
package org.tmatesoft.sqljet.core.internal.table;

import java.util.List;

import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtreeCursor;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetVdbeMem;

/**
 * Parses current record in {@link ISqlJetBtreeCursor} and allow acces to
 * fields.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetBtreeRecord {

    /**
     * @return the fieldsCount
     */
    int getFieldsCount();

    /**
     * @return
     */
    List<ISqlJetVdbeMem> getFields();

    /**
     * @return
     */
    ISqlJetMemoryPointer getRawRecord();

    /**
     * @param field
     * @return
     * @throws SqlJetException
     */
    String getStringField(int field, SqlJetEncoding enc) throws SqlJetException;

    /**
     * @param field
     * @return
     */
    long getIntField(int field);

    /**
     * @param field
     * @return
     */
    double getRealField(int field);

}