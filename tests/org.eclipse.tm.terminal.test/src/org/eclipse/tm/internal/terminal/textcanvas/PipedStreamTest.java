/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.textcanvas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


class PipedStreamTest {
	static class ReadThread extends Thread implements Runnable {

		InputStream pi = null;

		OutputStream po = null;

		ReadThread(String process, InputStream pi, OutputStream po) {
			this.pi = pi;
			this.po = po;
			setDaemon(true);
		}

		public void run() {
			byte[] buffer = new byte[2048];
			int bytes_read;
			try {
				for (;;) {
					bytes_read = pi.read(buffer);
					if (bytes_read == -1) {
						po.close();
						pi.close();
						return;
					}
					po.write(buffer, 0, bytes_read);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	static class FakeInputStream extends InputStream {
		int N;
		FakeInputStream(int n) {
			N=n;
		}
		public int read(byte[] b, int off, int len) throws IOException {
			if(N==0)
				return -1;
			int n=Math.min(len,N);
			for (int i = off; i < off+n; i++) {
				b[i]='x';
			}
			N-=n;
			return n;
		}
		public int read() throws IOException {
			throw new UnsupportedOperationException();
		}
		/* 
		 * available has to be implemented!
		 */
		public int available() throws IOException {
			return N;
		}
	}
	static class FakeOutputStream extends OutputStream {
		long N;
		public void write(int b) throws IOException {
			throw new UnsupportedOperationException();
		}
		public void write(byte[] b, int off, int len) throws IOException {
			N+=len;
		}
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		while(true) {
			runSunTest();
			runMyTest();
		}
	}
	private static void runSunTest() throws IOException, InterruptedException {
		java.io.PipedInputStream in = new java.io.PipedInputStream();
		OutputStream out = new java.io.PipedOutputStream(in);
		runPipe("Sun ",in, out,10);
	}
	private static void runMyTest() throws IOException, InterruptedException {
		PipedInputStream in=new PipedInputStream(4*1024);
		OutputStream out=in.getOutputStream();
		runPipe("My  ",in, out,99);
	}
	public static void runPipe(String what,InputStream writeIn, OutputStream readOut,int N) throws InterruptedException {
		FakeInputStream in=new FakeInputStream(N*1000*1000);
		FakeOutputStream out=new FakeOutputStream();
		ReadThread rt = new ReadThread("reader", in , readOut);
		ReadThread wt = new ReadThread("writer", writeIn, out);
		long t0=System.currentTimeMillis();
		rt.start();
		wt.start();
		wt.join();
		long t=System.currentTimeMillis();
		long n=out.N;
		System.out.println(what+n + " byte in " +(t-t0)+" ms -> "+(1000*n)/((t-t0+1)*1024)+" kb/sec");
	}
}
