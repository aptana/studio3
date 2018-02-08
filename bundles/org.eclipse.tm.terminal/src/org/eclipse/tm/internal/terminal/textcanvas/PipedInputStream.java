/*******************************************************************************
 * Copyright (c) 1996, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Douglas Lea (Addison Wesley) - [cq:1552] BoundedBufferWithStateTracking adapted to BoundedByteBuffer 
 * Martin Oberhuber (Wind River) - the waitForAvailable method
 * Martin Oberhuber (Wind River) - [208166] Avoid unnecessary arraycopy in BoundedByteBuffer
 *******************************************************************************/

package org.eclipse.tm.internal.terminal.textcanvas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The main purpose of this class is to start a runnable in the
 * display thread when data is available and to pretend no data 
 * is available after a given amount of time the runnable is running.
 *
 */
public class PipedInputStream extends InputStream {
	/**
	 * The output stream used by the terminal backend to write to the terminal
	 */
	protected final OutputStream fOutputStream;
	/**
	 * A blocking byte queue.
	 */
	private final BoundedByteBuffer fQueue;
	
	/**
	 * A byte bounded buffer used to synchronize the input and the output stream.
	 * <p>
	 * Adapted from BoundedBufferWithStateTracking 
	 * http://gee.cs.oswego.edu/dl/cpj/allcode.java
	 * http://gee.cs.oswego.edu/dl/cpj/
	 * <p>
	 * BoundedBufferWithStateTracking is part of the examples for the book
	 * Concurrent Programming in Java: Design Principles and Patterns by
	 * Doug Lea (ISBN 0-201-31009-0). Second edition published by 
	 * Addison-Wesley, November 1999. The code is 
	 * Copyright(c) Douglas Lea 1996, 1999 and released to the public domain
	 * and may be used for any purposes whatsoever. 
	 * <p>
	 * For some reasons a solution based on
	 * PipedOutputStream/PipedIntputStream
	 * does work *very* slowly:
	 * 		http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4404700
	 * <p>
	 * 
	 */
	private class BoundedByteBuffer {
		protected final byte[] fBuffer; // the elements
		protected int fPutPos = 0; // circular indices
		protected int fTakePos = 0;
		protected int fUsedSlots = 0; // the count
		private boolean fClosed;
		public BoundedByteBuffer(int capacity) throws IllegalArgumentException {
			// make sure we don't deadlock on too small capacity
			if (capacity <= 0)
				throw new IllegalArgumentException();
			fBuffer = new byte[capacity];
		}
		/**
		 * @return the bytes available for {@link #read()}
		 * Must be called with a lock on this!
		 */
		public int available() {
			return fUsedSlots;
		}
		/**
		 * Writes a single byte to the buffer. Blocks if the buffer is full.
		 * @param b byte to write to the buffer
		 * @throws InterruptedException when the thread is interrupted while waiting
		 *     for the buffer to become ready
		 * Must be called with a lock on this!
		 */
		public void write(byte b) throws InterruptedException {
			while (fUsedSlots == fBuffer.length)
				// wait until not full
				wait();

			fBuffer[fPutPos] = b;
			fPutPos = (fPutPos + 1) % fBuffer.length; // cyclically increment

			if (fUsedSlots++ == 0) // signal if was empty
				notifyAll();
		}
		public int getFreeSlots() {
			return fBuffer.length - fUsedSlots;
		}
		public void write(byte[] b, int off, int len) throws InterruptedException {
			assert len<=getFreeSlots();
			while (fUsedSlots == fBuffer.length)
				// wait until not full
				wait();
			int n = Math.min(len, fBuffer.length - fPutPos);
			System.arraycopy(b, off, fBuffer, fPutPos, n);
			if (fPutPos + len > fBuffer.length)
				System.arraycopy(b, off + n, fBuffer, 0, len - n);
			fPutPos = (fPutPos + len) % fBuffer.length; // cyclically increment
			boolean wasEmpty = fUsedSlots == 0;
			fUsedSlots += len;
			if (wasEmpty) // signal if was empty
				notifyAll();
		}
		/**
		 * Read a single byte. Blocks until a byte is available.
		 * @return a byte from the buffer
		 * @throws InterruptedException when the thread is interrupted while waiting
		 *     for the buffer to become ready
		 * Must be called with a lock on this!
		 */
		public int read() throws InterruptedException {
			while (fUsedSlots == 0) {
				if(fClosed)
					return -1;
				// wait until not empty
				wait();
			}
			byte b = fBuffer[fTakePos];
			fTakePos = (fTakePos + 1) % fBuffer.length;

			if (fUsedSlots-- == fBuffer.length) // signal if was full
				notifyAll();
			return b;
		}
		public int read(byte[] cbuf, int off, int len) throws InterruptedException {
			assert len<=available();
			while (fUsedSlots == 0) {
				if(fClosed)
					return 0;
				// wait until not empty
				wait();
			}
			int n = Math.min(len, fBuffer.length - fTakePos);
			System.arraycopy(fBuffer, fTakePos, cbuf, off, n);
			if (fTakePos + len > n)
				System.arraycopy(fBuffer, 0, cbuf, off + n, len - n);
			fTakePos = (fTakePos + len) % fBuffer.length;
			boolean wasFull = fUsedSlots == fBuffer.length;
			fUsedSlots -= len;
			if(wasFull)
				notifyAll();
				
			return len;
		}
		public void close() {
			fClosed=true;
			notifyAll();
		}
		public boolean isClosed() {
			return fClosed;
		}
	}

	/**
	 * An output stream that calls {@link PipedInputStream#textAvailable} 
	 * every time data is written to the stream. The data is written to
	 * {@link PipedInputStream#fQueue}.
	 * 
	 */
	class PipedOutputStream extends OutputStream {
		public void write(byte[] b, int off, int len) throws IOException {
			try {
				synchronized (fQueue) {
					if(fQueue.isClosed())
						throw new IOException("Stream is closed!"); //$NON-NLS-1$
					int written=0;
					while(written<len) {
						if(fQueue.getFreeSlots()==0) {
							// if no slots available, write one byte and block
							// until free slots are available
							fQueue.write(b[off + written]);
							written++;
						} else {
							// if slots are available, write as much as 
							// we can in one junk
							int n=Math.min(fQueue.getFreeSlots(), len-written);
							fQueue.write(b, off + written, n);
							written+=n;
						}
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		public void write(int b) throws IOException {
			try {
				synchronized(fQueue) {
					if(fQueue.isClosed())
						throw new IOException("Stream is closed!"); //$NON-NLS-1$
					fQueue.write((byte)b);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		public void close() throws IOException {
			synchronized(fQueue) {
				fQueue.close();
			}
		}
	}
	/**
	 * @param bufferSize the size of the buffer of the output stream
	 */
	public PipedInputStream(int bufferSize) {
		fOutputStream =new PipedOutputStream();
		fQueue=new BoundedByteBuffer(bufferSize);
	}
	/**
	 * @return the output stream used by the backend to write to the terminal.
	 */
	public OutputStream getOutputStream() {
		return fOutputStream;
	}
	/**
	 * Waits until data is available for reading.
	 * @param millis see {@link Object#wait(long)}
	 * @throws InterruptedException when the thread is interrupted while waiting
	 *     for the buffer to become ready
	 */
	public void waitForAvailable(long millis) throws InterruptedException {
		synchronized(fQueue) {
			if(fQueue.available()==0) 
				fQueue.wait(millis);
		}
	} 
	/**
	 * Must be called in the Display Thread!
	 * @return true if a character is available for the terminal to show.
	 */
	public int available() {
		synchronized(fQueue) {
			return fQueue.available();
		}
	}
	/**
	 * @return the next available byte. Check with {@link #available}
	 * if characters are available.
	 */
	public int read() throws IOException  {
		try {
			synchronized (fQueue) {
				return fQueue.read();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return -1;
		}
	}
    /**
     * Closing a <tt>PipedInputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     * <p>
     */
	public void close() throws IOException {
	}

	public int read(byte[] cbuf, int off, int len) throws IOException {
		int n=0;
		if(len==0)
			return 0;
		// read as much as we can using a single synchronized statement
		try {
			synchronized (fQueue) {		
				// if nothing available, block and read one byte
				if (fQueue.available() == 0) {
					// block now until at least one byte is available
					int c = fQueue.read();
					// are we at the end of stream
					if (c == -1)
						return -1;
					cbuf[off] = (byte) c;
					n++;
				}
				// is there more data available?
				if (n < len && fQueue.available() > 0) {
					// read at most available()
					int nn = Math.min(fQueue.available(), len - n);
					// are we at the end of the stream?
					if (nn == 0 && fQueue.isClosed()) {
						// if no byte was read, return -1 to indicate end of stream
						// else return the bytes we read up to now
						if (n == 0)
							n = -1;
						return n;
					}
					fQueue.read(cbuf, off + n, nn);
					n += nn;
				}
				
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return n;
	}
}
