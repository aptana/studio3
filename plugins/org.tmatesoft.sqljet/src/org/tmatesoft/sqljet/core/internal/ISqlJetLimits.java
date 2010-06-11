/**
 * SqlJetLimits.java
 * Copyright (C) 2008 TMate Software Ltd
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

/**
 * This file defines various limits of what SqlJet can process.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public interface ISqlJetLimits {

    /**
    ** The maximum length of a TEXT or BLOB in bytes.   This also
    ** limits the size of a row in a table or index.
    **
    ** The hard limit is the ability of a 32-bit signed integer
    ** to count the size: 2^31-1 or 2147483647.
    */
    int SQLJET_MAX_LENGTH = 1000000000;

    /**
    ** This is the maximum number of
    **
    **    * Columns in a table
    **    * Columns in an index
    **    * Columns in a view
    **    * Terms in the SET clause of an UPDATE statement
    **    * Terms in the result set of a SELECT statement
    **    * Terms in the GROUP BY or ORDER BY clauses of a SELECT statement.
    **    * Terms in the VALUES clause of an INSERT statement
    **
    ** The hard upper limit here is 32676.  Most database people will
    ** tell you that in a well-normalized database, you usually should
    ** not have more than a dozen or so columns in any table.  And if
    ** that is the case, there is no point in having more than a few
    ** dozen values in any of the other situations described above.
    */
    int SQLJET_MAX_COLUMN = 2000;

    /**
    ** The maximum length of a single SQL statement in bytes.
    **
    ** It used to be the case that setting this value to zero would
    ** turn the limit off.  That is no longer true.  It is not possible
    ** to turn this limit off.
    */
    int SQLJET_MAX_SQL_LENGTH = 1000000000;

    /**
    ** The maximum depth of an expression tree. This is limited to 
    ** some extent by SqlJet_MAX_SQL_LENGTH. But sometime you might 
    ** want to place more severe limits on the complexity of an 
    ** expression.
    **
    ** A value of 0 used to mean that the limit was not enforced.
    ** But that is no longer true.  The limit is now strictly enforced
    ** at all times.
    */
    int SQLJET_MAX_EXPR_DEPTH = 1000;

    /**
    ** The maximum number of terms in a compound SELECT statement.
    ** The code generator for compound SELECT statements does one
    ** level of recursion for each term.  A stack overflow can result
    ** if the number of terms is too large.  In practice, most SQL
    ** never has more than 3 or 4 terms.  Use a value of 0 to disable
    ** any limit on the number of terms in a compount SELECT.
    */
    int SQLJET_MAX_COMPOUND_SELECT = 500;

    /**
    ** The maximum number of opcodes in a VDBE program.
    ** Not currently enforced.
    */
    int SQLJET_MAX_VDBE_OP = 25000;

    /**
    ** The maximum number of arguments to an SQL function.
    */
    int SQLJET_MAX_FUNCTION_ARG = 100;

    /**
    ** The maximum number of in-memory pages to use for the main database
    ** table and for temporary tables.  The SQLJET_DEFAULT_CACHE_SIZE
    */
    int SQLJET_DEFAULT_CACHE_SIZE = 2000;

    int SQLJET_DEFAULT_TEMP_CACHE_SIZE = 500;

    /**
    ** The maximum number of attached databases.  This must be between 0
    ** and 30.  The upper bound on 30 is because a 32-bit integer bitmap
    ** is used internally to track attached databases.
    */
    int SQLJET_MAX_ATTACHED = 10;


    /**
    ** The maximum value of a ?nnn wildcard that the parser will accept.
    */
    int SQLJET_MAX_VARIABLE_NUMBER = 999;


    /** Maximum page size.  The upper bound on this value is 32768.  This a limit
    ** imposed by the necessity of storing the value in a 2-byte unsigned integer
    ** and the fact that the page size must be a power of 2.
    */
    int SQLJET_MAX_PAGE_SIZE = 32768;

    int SQLJET_MIN_PAGE_SIZE = 512;
    

    /**
    ** The default size of a database page.
    */
    int SQLJET_DEFAULT_PAGE_SIZE = 1024;

    /**
    ** Ordinarily, if no value is explicitly provided, SqlJet creates databases
    ** with page size SQLJET_DEFAULT_PAGE_SIZE. However, based on certain
    ** device characteristics (sector-size and atomic write() support),
    ** SqlJet may choose a larger value. This constant is the maximum value
    ** SqlJet will choose on its own.
    */
    int SQLJET_MAX_DEFAULT_PAGE_SIZE = 8192;


    /**
    ** Maximum number of pages in one database file.
    **
    ** This is really just the default value for the max_page_count pragma.
    ** This value can be lowered (or raised) at run-time using that the
    ** max_page_count macro.
    */
    int SQLJET_MAX_PAGE_COUNT = 1073741823;

    /**
    ** Maximum length (in bytes) of the pattern in a LIKE or GLOB
    ** operator.
    */
    int SQLJET_MAX_LIKE_PATTERN_LENGTH = 50000;

    int SQLJET_MIN_FILE_FORMAT = 1;
    int SQLJET_MAX_FILE_FORMAT = 4;
    
}
