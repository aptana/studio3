/**
 * SqlJetRowNumCursor.java
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

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public abstract class SqlJetRowNumCursor extends SqlJetCursor {

    private long rowsCount;
    private long currentRowNum;
    private long currentRowId;
    private boolean internalMove;

    private long limit;

    /**
     * @param table
     * @param db
     * @throws SqlJetException 
     */
    SqlJetRowNumCursor(ISqlJetBtreeTable table, SqlJetDb db) throws SqlJetException {
        super(table, db);

        rowsCount = -1;
        currentRowNum = -1;
        currentRowId = -1;

        limit = 0;
    }

    /**
     * @param limit
     *            the limit to set
     * @throws SqlJetException
     */
    public void setLimit(long limit) throws SqlJetException {
        if (limit >= 0) {
            this.limit = limit;
            rowsCount = -1;
            first();
        }
    }

    /**
     * @return the limit
     */
    public long getLimit() {
        return limit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getRowCount()
     */
    public long getRowCount() throws SqlJetException {

        if (rowsCount < 0) {
            computeRows(false);
        }

        return rowsCount;
    }

    /**
     * @throws SqlJetException
     */
    private void computeRows(boolean current) throws SqlJetException {

        try {

            internalMove = true;

            currentRowId = getRowIdSafe();
            rowsCount = 0;
            currentRowNum = -1;

            for (first(); !eof(); next()) {
                rowsCount++;
                if (currentRowId == getRowIdSafe()) {
                    currentRowNum = rowsCount;
                    if (current) {
                        break;
                    }
                }
            }

            if (currentRowNum < 0) {
                currentRowNum = rowsCount;
            }

            if (currentRowId > 0) {
                goTo(currentRowId);
            }

        } finally {
            internalMove = false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getCurrentRow()
     */
    public long getRowIndex() throws SqlJetException {

        if (currentRowNum < 0 || (rowsCount < 0 && eof()) || currentRowId != getRowIdSafe()) {
            computeRows(true);
        }

        return currentRowNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#goToRow(long)
     */
    public boolean goToRow(long rowNum) throws SqlJetException {

        try {

            internalMove = true;

            if (rowsCount > 0 && rowNum > rowsCount) {
                return false;
            }

            if (currentRowNum < 0 || (eof() && rowsCount < 0) || currentRowId != getRowIdSafe()) {

                currentRowId = getRowIdSafe();
                currentRowNum = 0;
                for (first(); !eof(); next()) {
                    currentRowNum++;
                    if (currentRowNum == rowNum) {
                        currentRowId = getRowIdSafe();
                        return true;
                    }
                }

                if (rowsCount < 0) {
                    rowsCount = currentRowNum;
                }

                currentRowNum = -1;

                if (currentRowId > 0) {
                    goTo(currentRowId);
                }

            } else {

                if (rowNum == currentRowNum) {

                    return true;

                }

                final long rn = currentRowNum;

                while (!eof()) {
                    if (rowNum > currentRowNum) {
                        currentRowNum++;
                    } else {
                        currentRowNum--;
                    }
                    if (currentRowNum == rowNum) {
                        currentRowId = getRowIdSafe();
                        return true;
                    }
                    if (rowNum > currentRowNum) {
                        if (!next()) {
                            break;
                        }
                    } else {
                        if (!previous()) {
                            break;
                        }
                    }
                }

                if (rowsCount < 0) {
                    rowsCount = currentRowNum;
                }

                currentRowNum = rn;

                if (currentRowId > 0) {
                    goTo(currentRowId);
                }

            }

            return false;

        } finally {
            internalMove = false;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#delete()
     */
    public void delete() throws SqlJetException {
        rowsCount--;
        currentRowNum--;
        currentRowId = getRowIdSafe();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#first()
     */
    @Override
    public boolean first() throws SqlJetException {
        return firstRowNum(super.first());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#next()
     */
    @Override
    public boolean next() throws SqlJetException {
        return nextRowNum(super.next());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#previous()
     */
    @Override
    public boolean previous() throws SqlJetException {
        return previousRowNum(super.previous());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#last()
     */
    @Override
    public boolean last() throws SqlJetException {
        if (limit > 0 && goToRow(limit)) {
            return true;
        } else {
            return lastRowNum(super.last());
        }
    }

    protected boolean firstRowNum(boolean first) throws SqlJetException {
        if (internalMove)
            return first;
        if (first) {
            currentRowNum = 1;
            currentRowId = getRowIdSafe();
        } else {
            currentRowNum = 0;
            currentRowId = -1;
        }
        return first;
    }

    protected boolean nextRowNum(boolean next) throws SqlJetException {
        if (internalMove)
            return next;
        if (next) {
            currentRowNum++;
            currentRowId = getRowIdSafe();
        }
        return next;
    }

    protected boolean previousRowNum(boolean previous) throws SqlJetException {
        if (internalMove)
            return previous;
        if (previous) {
            currentRowNum--;
            currentRowId = getRowIdSafe();
        }
        return previous;
    }

    protected boolean lastRowNum(boolean last) throws SqlJetException {
        if (internalMove)
            return last;
        currentRowNum = -1;
        currentRowId = -1;
        return last;
    }

    private long getRowIdSafe() throws SqlJetException {
        return super.eof() ? 0 : getRowId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#eof()
     */
    @Override
    public boolean eof() throws SqlJetException {
        if (limit > 0) {
            return currentRowNum >= limit;
        } else {
            return super.eof();
        }
    }

}
