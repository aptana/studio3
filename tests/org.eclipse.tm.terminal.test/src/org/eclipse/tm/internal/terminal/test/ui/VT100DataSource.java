/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Martin Oberhuber (Wind River) - [204796] Terminal should allow setting the encoding to use
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.test.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.eclipse.tm.internal.terminal.control.impl.ITerminalControlForText;
import org.eclipse.tm.internal.terminal.emulator.VT100Emulator;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.tm.terminal.model.ITerminalTextData;

/**
 * Reads the file in an infinite loop.
 * Makes lines containing 'x' bold.
 *
 */
final class VT100DataSource implements IDataSource {
	VT100Emulator fEmulator;
	volatile int fAvailable;
	volatile int fRead;
	private final String fFile;

	VT100DataSource(String file) {
		fFile=file;
	}
	class InfiniteFileInputStream extends InputStream {
		public InfiniteFileInputStream() {
			try {
				fInputStream=new FileInputStream(fFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public int available() throws IOException {
			return fAvailable;
		}
		private InputStream fInputStream;
		public int read() throws IOException {
			throw new IOException();
		}
		public int read(byte[] b, int off, int len) throws IOException {
			while(fAvailable==0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			len=fAvailable;
			int n=fInputStream.read(b, off, len);
			if(n<=0) {
				fInputStream.close();
				fInputStream=new FileInputStream(fFile);
				n=fInputStream.read(b, off, len);
			}
			fAvailable-=n;
			return n;
		}

	}
	void init(ITerminalTextData terminal) {
		final Reader reader;
		try {
			reader = new InputStreamReader(new InfiniteFileInputStream(), "ISO-8859-1"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		fEmulator=new VT100Emulator(terminal,new ITerminalControlForText() {

			public void disconnectTerminal() {
				// TODO Auto-generated method stub

			}

			public OutputStream getOutputStream() {
				return new ByteArrayOutputStream();
			}

			public TerminalState getState() {
				return TerminalState.CONNECTED;
			}

			public ITerminalConnector getTerminalConnector() {
				return null;
			}

			public void setState(TerminalState state) {
			}

			public void setTerminalTitle(String title) {
			}

			public void setApplicationKeypad(boolean mode)
			{
				// TODO Auto-generated method stub
				
			}
		}, reader);
	}
	public int step(ITerminalTextData terminal) {
		synchronized(terminal) {
			if(fEmulator==null) {
				init(terminal);
//				fEmulator.setDimensions(48, 132);
				fEmulator.setDimensions(24, 80);
				fEmulator.setCrAfterNewLine(true);

			}
			fAvailable=80;
			fEmulator.processText();
		}
		return 80;
	}
}
