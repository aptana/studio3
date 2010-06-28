/**
 * SqlJetDeviceCharacteristics.java
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
 * Device Characteristics
 *
 * The deviceCapabilities method of the {@link ISqlJetFile}
 * object returns an set of the thesevalues expressing 
 * I/O characteristics of the mass storage
 * device that holds the file that the {@link ISqlJetFile}
 * refers to.
 *
 * The IOCAP_ATOMIC property means that all writes of
 * any size are atomic.  The IOCAP_ATOMICnnn values
 * mean that writes of blocks that are nnn bytes in size and
 * are aligned to an address which is an integer multiple of
 * nnn are atomic.  The IOCAP_SAFE_APPEND value means
 * that when data is appended to a file, the data is appended
 * first then the size of the file is extended, never the other
 * way around.  The IOCAP_SEQUENTIAL property means that
 * information is written to disk in the same order as calls
 * to write().
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public enum SqlJetDeviceCharacteristics {

    IOCAP_ATOMIC512(512),
    IOCAP_ATOMIC1K(1024),
    IOCAP_ATOMIC2K(2*1024),
    IOCAP_ATOMIC4K(4*1024),
    IOCAP_ATOMIC8K(8*1024),
    IOCAP_ATOMIC16K(16*1024),
    IOCAP_ATOMIC32K(32*1024),
    IOCAP_ATOMIC64K(64*1024),
    IOCAP_SAFE_APPEND,
    IOCAP_SEQUENTIAL;

    private int ioCapAtomicSize = 0;
    
    /**
     * @return the ioCapAtomicSize
     */
    public int getIoCapAtomicSize() {
        return ioCapAtomicSize;
    }
    
    /**
     * Set ioCapAtomicSize
     */
    private SqlJetDeviceCharacteristics(final int ioCapAtomicSize) {
        this.ioCapAtomicSize = ioCapAtomicSize;
    }

    /**
     * 
     */
    private SqlJetDeviceCharacteristics() {
    }
}
