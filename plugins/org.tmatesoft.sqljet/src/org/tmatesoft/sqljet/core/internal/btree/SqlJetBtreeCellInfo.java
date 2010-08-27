package org.tmatesoft.sqljet.core.internal.btree;

import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;

/**
 * An instance of the following structure is used to hold information
 * about a cell.  The parseCellPtr() function fills in this structure
 * based on information extract from the raw disk page.
 */
class SqlJetBtreeCellInfo{
    ISqlJetMemoryPointer pCell;     /* Pointer to the start of cell content */
    long nKey;      /* The key for INTKEY tables, or number of bytes in key */
    int nData;     /* Number of bytes of data */
    int nPayload;  /* Total amount of payload */
    int nHeader;   /* Size of the cell content header in bytes */
    int nLocal;    /* Amount of payload held locally */
    int iOverflow; /* Offset to overflow page number.  Zero if no overflow */
    int nSize;     /* Size of the cell content on the main b-tree page */
}