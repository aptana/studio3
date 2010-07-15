/**
 * SqlJetPageFlags.java
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

import org.tmatesoft.sqljet.core.internal.pager.SqlJetPage;

/**
 * Bit values for {@link SqlJetPage#flags}
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public enum SqlJetPageFlags {

    /** Page has changed */
    DIRTY,  
    
    /** Fsync the rollback journal before
     * writing this page to the database */
    NEED_SYNC,  
    
    /** Content is unread */
    NEED_READ,  
    
    /** A hint that reuse is unlikely */
    REUSE_UNLIKELY,  
    
    /** Do not write content to disk */
    DONT_WRITE  
    
}
