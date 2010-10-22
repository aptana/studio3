/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

public class TerminalToRemoteInjectionOutputStreamTest extends TestCase {
	final static String ENCODING="UTF-8";
	/**
	 * This class escapes strings coming on the original
	 * terminal..
	 *
	 */
	class CleverInterceptor extends TerminalToRemoteInjectionOutputStream.Interceptor {

		public void close() throws IOException {
		}
		public void write(int b) throws IOException {
			fOriginal.write('[');
			fOriginal.write(b);
			fOriginal.write(']');
		}
		public void write(byte[] b, int off, int len) throws IOException {
			fOriginal.write('[');
			fOriginal.write(b,off,len);
			fOriginal.write(']');
		}

	}
	class NullInterceptor extends TerminalToRemoteInjectionOutputStream.Interceptor {
	}
	public void testClose() throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream bs=new ByteArrayOutputStream();
		TerminalToRemoteInjectionOutputStream s= new TerminalToRemoteInjectionOutputStream(bs);
		s.write("begin:".getBytes(ENCODING));
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		OutputStream os1=s.grabOutput();
		os1.write('x');
		s.write('A');
		os1.write('y');
		s.write('B');
		os1.close();

		s.write('-');
		OutputStream os=s.grabOutput();
		// make sure the closed output does not inject anything
		try {
			os1.write('k');
			fail("...");
		} catch (Exception e) {
		}
		os.write('X');
		s.write('a');
		os.write('Y');
		// make sure the closed output does not inject anything
		try {
			os1.write('l');
			fail("...");
		} catch (Exception e) {
		}
		s.write('b');
		os.close();
		assertEquals("begin:xyAB-XYab", new String(bs.toByteArray(),ENCODING));
	}

	public void testFlush() {
	}

	public void testWriteInt() throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream bs=new ByteArrayOutputStream();
		TerminalToRemoteInjectionOutputStream s= new TerminalToRemoteInjectionOutputStream(bs);
		s.write("begin:".getBytes(ENCODING));
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		OutputStream os=s.grabOutput();
		os.write('x');
		s.write('A');
		os.write('y');
		s.write('B');
		s.close();
		assertEquals("begin:xyAB", new String(bs.toByteArray(),ENCODING));

	}

	public void testWriteByteArray() {
	}

	public void testWriteByteArrayIntInt() {
	}
	public void testGrabOutput() throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream bs=new ByteArrayOutputStream();
		TerminalToRemoteInjectionOutputStream s= new TerminalToRemoteInjectionOutputStream(bs);
		s.write("begin:".getBytes(ENCODING));
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		OutputStream os1=s.grabOutput();
		OutputStream os2;
		try {
			os2=s.grabOutput();
			fail("should fail until the foirst output is closed");
		} catch (IOException e) {
		}
		os1.close();
		os2=s.grabOutput();
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		os2.write("Test".getBytes(ENCODING));
		assertEquals("begin:Test", new String(bs.toByteArray(),ENCODING));
		s.write(" west".getBytes(ENCODING));
		assertEquals("begin:Test", new String(bs.toByteArray(),ENCODING));
		os2.write(" the".getBytes(ENCODING));
		assertEquals("begin:Test the", new String(bs.toByteArray(),ENCODING));
		os2.close();
		assertEquals("begin:Test the west", new String(bs.toByteArray(),ENCODING));
		s.write('!');
		assertEquals("begin:Test the west!", new String(bs.toByteArray(),ENCODING));

	}
	public void testGrabOutputWithCleverInterceptor() throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream bs=new ByteArrayOutputStream();
		TerminalToRemoteInjectionOutputStream s= new TerminalToRemoteInjectionOutputStream(bs);
		s.write("begin:".getBytes(ENCODING));
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		// the injector escapes the output coming from the main stream
		OutputStream os=s.grabOutput(new CleverInterceptor());
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		os.write("Test".getBytes(ENCODING));
		assertEquals("begin:Test", new String(bs.toByteArray(),ENCODING));
		s.write(" west".getBytes(ENCODING));
		assertEquals("begin:Test[ west]", new String(bs.toByteArray(),ENCODING));
		os.write(" the".getBytes(ENCODING));
		assertEquals("begin:Test[ west] the", new String(bs.toByteArray(),ENCODING));
		s.write('x');
		assertEquals("begin:Test[ west] the[x]", new String(bs.toByteArray(),ENCODING));
		os.close();
		assertEquals("begin:Test[ west] the[x]", new String(bs.toByteArray(),ENCODING));
		s.write('!');
		assertEquals("begin:Test[ west] the[x]!", new String(bs.toByteArray(),ENCODING));

	}
	public void testGrabOutputWithNullInterceptor() throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream bs=new ByteArrayOutputStream();
		TerminalToRemoteInjectionOutputStream s= new TerminalToRemoteInjectionOutputStream(bs);
		s.write("begin:".getBytes(ENCODING));
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		// bytes written to the main stream are ignored while the injector
		// is active
		OutputStream os=s.grabOutput(new NullInterceptor());
		assertEquals("begin:", new String(bs.toByteArray(),ENCODING));
		os.write("Test".getBytes(ENCODING));
		assertEquals("begin:Test", new String(bs.toByteArray(),ENCODING));
		s.write(" west".getBytes(ENCODING));
		assertEquals("begin:Test", new String(bs.toByteArray(),ENCODING));
		os.write(" the".getBytes(ENCODING));
		assertEquals("begin:Test the", new String(bs.toByteArray(),ENCODING));
		s.write('x');
		assertEquals("begin:Test the", new String(bs.toByteArray(),ENCODING));
		os.close();
		assertEquals("begin:Test the", new String(bs.toByteArray(),ENCODING));
		s.write('!');
		assertEquals("begin:Test the!", new String(bs.toByteArray(),ENCODING));

	}

}
