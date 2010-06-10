/**
 * SqlJetPagerSafetyLevel.java
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
 * Valuest to adjust the robustness of the database to damage due to OS crashes or power
 * failures by changing the number of syncs()s when writing the rollback
 * journal.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public enum SqlJetSafetyLevel {

    /**
     * sqlite3OsSync() is never called. This is the default for temporary and
     * transient files.
     */
    OFF,

    /**
     * The journal is synced once before writes begin on the database. This is
     * normally adequate protection, but it is theoretically possible, though
     * very unlikely, that an inopertune power failure could leave the journal
     * in a state which would cause damage to the database when it is rolled
     * back.
     */
    NORMAL,

    /**
     * The journal is synced twice before writes begin on the database (with
     * some additional information - the nRec field of the journal header -
     * being written in between the two syncs). If we assume that writing a
     * single disk sector is atomic, then this mode provides assurance that the
     * journal will not be corrupted to the point of causing damage to the
     * database during rollback.
     */
    FULL

}
