/**
 * SqlJetLogDefinitions.java
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
package org.tmatesoft.sqljet.core;

/**
 * <p>
 * Definitions of properties names for SQLJet logging.
 * </p>
 * 
 * <p>
 * All these properties are boolean type and have "false" value by default.
 * </p>
 * 
 * <p>
 * Using as: <br/>
 * <br/>
 * 
 * <code>
 * -DSQLJET_LOG_BTREE=true -DSQLJET_LOG_STACKTRACE=true.
 * </code>
 * </p>
 * 
 * 
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetLogDefinitions {

    /**
     * Activates logging of files operations.
     */
    public static final String SQLJET_LOG_FILES = "SQLJET_LOG_FILES";

    /**
     * Activates logging of files operations performance.
     */
    public static final String SQLJET_LOG_FILES_PERFORMANCE = "SQLJET_LOG_FILES_PERFORMANCE";

    /**
     * Activates logging of pager operations.
     */
    public static final String SQLJET_LOG_PAGER = "SQLJET_LOG_PAGER";

    /**
     * Activates logging of b-tree operations.
     */
    public static final String SQLJET_LOG_BTREE = "SQLJET_LOG_BTREE";

    /**
     * Activates logging of signed values in operations with unsigned types.
     */
    public static final String SQLJET_LOG_SIGNED = "SQLJET_LOG_SIGNED";

    /**
     * Activates logging of stack trace at each logging invocation.
     */
    public static final String SQLJET_LOG_STACKTRACE = "SQLJET_LOG_STACKTRACE";

}
