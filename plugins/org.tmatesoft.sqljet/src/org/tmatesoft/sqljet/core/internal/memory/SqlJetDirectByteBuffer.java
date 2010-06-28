/**
 * SqlJetDirectByteBuffer.java
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
package org.tmatesoft.sqljet.core.internal.memory;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetDirectByteBuffer extends SqlJetByteBuffer {

    public SqlJetDirectByteBuffer() {
    }

    public SqlJetDirectByteBuffer(ByteBuffer buffer) {
        super(buffer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.sandbox.internal.memory.SqlJetByteBuffer#allocate
     * (int)
     */
    @Override
    public void allocate(int size) {
        assert (size > 0);

        buffer = ByteBuffer.allocateDirect(size);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.memory.SqlJetByteBuffer#asArray()
     */
    @Override
    public byte[] asArray() {
        final byte[] b = new byte[buffer.remaining()];
        getBytes(0, b, 0, b.length);
        return b;
    }
    
    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.internal.memory.SqlJetByteBuffer#fill(int, int, byte)
     */
    @Override
    public void fill(int from, int count, byte value) {
        final byte[] b = new byte[count];
        Arrays.fill(b, value);
        putBytes(from,b,0,count);

    }
    
    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.internal.memory.SqlJetByteBuffer#getBytes(int, byte[], int, int)
     */
    @Override
    public void getBytes(int pointer, byte[] bytes, int to, int count) {
        final int position = buffer.position();
        try{
            buffer.position(pointer);
            buffer.get(bytes, to, count);
        } finally {buffer.position(position);}
    }
    
    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.internal.memory.SqlJetByteBuffer#putBytes(int, byte[], int, int)
     */
    @Override
    public void putBytes(int pointer, byte[] bytes, int from, int count) {
        final int position = buffer.position();
        try{
            buffer.position(pointer);
            buffer.put(bytes, from, count);
        } finally {buffer.position(position);}
    }

}
