/**
 * SqlJetFileLock.java
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
package org.tmatesoft.sqljet.core.internal.fs;

import java.io.IOException;
import java.nio.channels.FileLock;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetFileLock extends FileLock {

    final private SqlJetFileLockManager manager;
    final private FileLock fileLock;

    private long locksCount;

    /**
     * @param channel
     * @param position
     * @param size
     * @param shared
     */
    public SqlJetFileLock(SqlJetFileLockManager manager, FileLock fileLock) {
        super(fileLock.channel(), fileLock.position(), fileLock.size(), fileLock.isShared());
        this.manager = manager;
        this.fileLock = fileLock;
        this.locksCount = 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.nio.channels.FileLock#isValid()
     */
    @Override
    public boolean isValid() {
        return fileLock.isValid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.nio.channels.FileLock#release()
     */
    @Override
    public void release() throws IOException {
        locksCount--;
        if (locksCount == 0) {
            fileLock.release();
            manager.deleteLock(this);
        }
    }

    public void addLock() {
        locksCount++;
    }
}
