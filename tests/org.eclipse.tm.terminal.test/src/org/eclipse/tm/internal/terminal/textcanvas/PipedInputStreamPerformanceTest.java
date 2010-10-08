/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.textcanvas;

import java.io.OutputStream;

public class PipedInputStreamPerformanceTest {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		runPerformanceTest();
		runPerformanceTest();
	}

	private static void runPerformanceTest() throws InterruptedException {
		PipedInputStream in=new PipedInputStream(1024);
		OutputStream out=in.getOutputStream();
		PipedStreamTest.runPipe("",in, out,100);
	}

}
