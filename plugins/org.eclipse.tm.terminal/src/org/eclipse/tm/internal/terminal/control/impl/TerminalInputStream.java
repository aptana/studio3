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
 *******************************************************************************/

package org.eclipse.tm.internal.terminal.control.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

/**
 * The main purpose of this class is to start a runnable in the
 * display thread when data is available and to pretend no data
 * is available after a given amount of time the runnable is running.
 *
 */
public class TerminalInputStream extends InputStream {
	/**
	 * The maximum time in milliseconds the {@link #fNotifyChange} runs until
	 * {@link #ready()} returns false.
	 */
	private final int fUITimeout;
	/**
	 * The output stream used by the terminal backend to write to the terminal
	 */
	protected final OutputStream fOutputStream;
	/**
	 * This runnable is called every time some characters are available from...
	 */
	private final Runnable fNotifyChange;
	/**
	 * A shared timer for all terminals. This times is used to limit the
	 * time used in the display thread....
	 */
	static Timer fgTimer=new Timer(false);
	/**
	 * A blocking byte queue.
	 */
	private final BoundedByteBuffer fQueue;

	/**
	 * The maximum amount of data read and written in one shot.
	 * The timer cannot interrupt reading this amount of data.
	 * {@link #available()} and {@link #read(byte[], int, int)}
	 * This is used as optimization, because reading single characters
	 * can be very inefficient, because each call is synchronized.
	 */
	// block size must be smaller than the Queue capacity!
	final int BLOCK_SIZE=64;


	/**
	 * The runnable that is scheduled in the display tread. Takes care of the
	 * timeout management. It calls the {@link #fNotifyChange}
	 */
	// synchronized with fQueue!
	private Runnable fRunnable;

	/**
	 * Used as flag to indicate that the current runnable
	 * has used enough time in the display thread.
	 * This variable is set by a timer thread after the
	 * Runnable starts to run in the Display thread after
	 * {@link #fUITimeout}.
	 */
	// synchronized with fQueue!
	private boolean fEnoughDisplayTime;

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
	class BoundedByteBuffer {
		protected final byte[] fBuffer; // the elements
		protected int fPutPos = 0; // circular indices
		protected int fTakePos = 0;
		protected int fUsedSlots = 0; // the count
		public BoundedByteBuffer(int capacity) throws IllegalArgumentException {
			// make sure we don't deadlock on too small capacity
			if(capacity<BLOCK_SIZE)
				capacity=2*BLOCK_SIZE;
			if (capacity <= 0)
				throw new IllegalArgumentException();
			fBuffer = new byte[capacity];
		}
		/**
		 * @return the bytes available for {@link #read()}
		 */
		public synchronized int size() {
			return fUsedSlots;
		}
		/**
		 * Writes a single byte to the buffer. Blocks if the buffer is full.
		 *
		 * @param b the byte to write
		 * @throws InterruptedException when the Thread is interrupted while
		 *             waiting for the buffer to become available because it was
		 *             full
		 */
		public synchronized void write(byte b) throws InterruptedException {
			while (fUsedSlots == fBuffer.length)
				// wait until not full
				wait();

			fBuffer[fPutPos] = b;
			fPutPos = (fPutPos + 1) % fBuffer.length; // cyclically increment

			if (fUsedSlots++ == 0) // signal if was empty
				notifyAll();
		}
		/**
		 * Read a single byte. Blocks until a byte is available.
		 *
		 * @return a byte from the buffer
		 * @throws InterruptedException when the Thread is interrupted while
		 *             waiting for the buffer to be filled with a readable byte
		 */
		public synchronized byte read() throws InterruptedException {
			while (fUsedSlots == 0)
				// wait until not empty
				wait();
			byte b = fBuffer[fTakePos];
			fTakePos = (fTakePos + 1) % fBuffer.length;

			if (fUsedSlots-- == fBuffer.length) // signal if was full
				notifyAll();
			return b;
		}
	}

	/**
	 * An output stream that calls {@link TerminalInputStream#textAvailable}
	 * every time data is written to the stream. The data is written to
	 * {@link TerminalInputStream#fQueue}.
	 *
	 */
	class TerminalOutputStream extends OutputStream {
		public void write(byte[] b, int off, int len) throws IOException {
			try {
				// optimization to avoid many synchronized
				// sections: put the data in junks into the
				// queue.
				int noff=off;
				int end=off+len;
				while(noff<end) {
					int n=noff+BLOCK_SIZE;
					if(n>end)
						n=end;
					// now block the queue for the time we need to
					// add some characters
					synchronized(fQueue) {
						for(int i=noff;i<n;i++) {
							fQueue.write(b[i]);
						}
						bytesAreAvailable();
					}
					noff+=BLOCK_SIZE;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		public void write(int b) throws IOException {
			try {
				// a kind of optimization, because
				// both calls use the fQueue lock...
				synchronized(fQueue) {
					fQueue.write((byte)b);
					bytesAreAvailable();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	/**
	 * @param bufferSize the size of the buffer of the output stream
	 * @param uiTimeout the maximum time the notifyChange runnable runs. It will be
	 * rescheduled after uiTimeout if input data is still available.
	 * @param notifyChange a Runnable that is posted to the Display thread
	 * via {@link Display#asyncExec}. The runnable is posted several times!
	 */
	public TerminalInputStream(int bufferSize,int uiTimeout,Runnable notifyChange) {
		//if(true) {notifyChange=new Runnable() {public void run() {byte buff[]=new byte[1024];while((available())>0)try {read(buff);} catch (IOException e) {break;}}};}
		fOutputStream =new TerminalOutputStream();
		fNotifyChange=notifyChange;
		fQueue=new BoundedByteBuffer(bufferSize);
		fUITimeout=uiTimeout;
	}
	/**
	 * Posts the runnable {@link #fNotifyChange} to the display Thread,
	 * unless the runnable is already scheduled.
	 * It will make {@link #ready} return false after
	 * {@link #fUITimeout} milli seconds.
	 */
	void bytesAreAvailable() {
		// synchronize on the Queue to reduce the locks
		synchronized(fQueue) {
			if(fRunnable==null) {
				fRunnable=new Runnable(){
					public void run() {
						// protect the access to fRunnable
						synchronized(fQueue){
							fRunnable=null;
						}
						// end the reading after some time
						startTimer(fUITimeout);
						// and start the real runnable
						fNotifyChange.run();
					}

				};
				// TODO: make sure we don't create a display if the display is disposed...
				Display.getDefault().asyncExec(fRunnable);
			}
		}

	}
	/**
	 * Starts a timer that sets {@link #fEnoughDisplayTime} to
	 * true after milliSec.
	 * @param milliSec The time after which fEnoughDisplayTime is set to true.
	 */
	void startTimer(int milliSec) {
		synchronized(fQueue) {
			fEnoughDisplayTime=false;
		}
		fgTimer.schedule(new TimerTask(){
			public void run() {
				synchronized(fQueue) {
					fEnoughDisplayTime=true;
					// there is some data available
					if(fQueue.size()>0) {
						// schedule a new runnable to do the work
						bytesAreAvailable();
					}
				}
			}}, milliSec);
	}
	/**
	 * @return the output stream used by the backend to write to the terminal.
	 */
	public OutputStream getOutputStream() {
		return fOutputStream;
	}
	/**
	 * Must be called in the Display Thread!
	 * @return true if a character is available for the terminal to show.
	 */
	public int available() {
		int available;
		synchronized(fQueue) {
			if(fEnoughDisplayTime)
				return 0;
			available=fQueue.size();
		}
		// Limit the available amount of data.
		// else our trick of limiting the time spend
		// reading might not work.
		if(available>BLOCK_SIZE)
			available=BLOCK_SIZE;
		return available;
	}
	/**
	 * @return the next available byte. Check with {@link #available}
	 * if characters are available.
	 */
	public int read() throws IOException  {
		try {
			return fQueue.read();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return -1;
		}
	}
    /**
     * Closing a <tt>ByteArrayInputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     * <p>
     */
	public void close() throws IOException {
	}

	public int read(byte[] cbuf, int off, int len) throws IOException {
		int n=0;
		// read as much as we can using a single synchronized statement
		synchronized (fQueue) {
			try {
				// The assumption is that the caller has used available to
				// check if bytes are available! That's why we don't check
				// for fEnoughDisplayTime!
				// Make sure that not more than BLOCK_SIZE is read in one call
				while(fQueue.size()>0 && n<len && n<BLOCK_SIZE) {
					cbuf[off+n]=fQueue.read();
					n++;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return n;
	}
}
