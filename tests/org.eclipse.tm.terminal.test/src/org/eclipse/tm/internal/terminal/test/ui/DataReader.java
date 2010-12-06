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
package org.eclipse.tm.internal.terminal.test.ui;

import org.eclipse.tm.terminal.model.ITerminalTextData;

class DataReader implements Runnable {
	final Thread fThread;
	final IDataSource fDataSource;
	final ITerminalTextData fTerminal;
	volatile boolean fStart;
	volatile boolean fStop;
	volatile int fThrottleTime;
	final IStatus fStatus;
	final String fName;
	DataReader(String name, ITerminalTextData terminal, IDataSource dataSource, IStatus status) {
		fStatus=status;
		fName=name;
		fTerminal=terminal;
		fDataSource=dataSource;
		fThread=new Thread(this,name);
		fThread.setDaemon(true);
		fThread.start();
	}
	public void run() {
		long t0=System.currentTimeMillis()-1;
		long c=0;
		int lines=0;
		while(!Thread.interrupted()) {
			while(!fStart || fStop) {
				sleep(1);
			}
			if(fThrottleTime>0)
				sleep(fThrottleTime);
			// synchronize because we have to be sure the size does not change while
			// we add lines
			int len=fDataSource.step(fTerminal);
			// keep the synchronized block short!
			c+=len;
			lines++;
			if((fThrottleTime>0 || (lines%100==0))&&(System.currentTimeMillis()-t0)>1000) {
				long t=System.currentTimeMillis()-t0;
				final String s=(c*1000)/(t*1024)+"kb/s " + (lines*1000)/t+"lines/sec "+(c*1000*8)/t+" bits/s ";
				fStatus.setStatus(s);
				lines=0;
				t0=System.currentTimeMillis();
				c=0;
			}
		}
	}
	public int getThrottleTime() {
		return fThrottleTime;
	}
	public void setThrottleTime(int throttleTime) {
		fThrottleTime = throttleTime;
	}
	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	public boolean isStart() {
		return fStart;
	}
	public void setStart(boolean start) {
		fStart = start;
	}
	public String getName() {
		return fName;
	}
	public boolean isStop() {
		return fStop;
	}
	public void setStop(boolean stop) {
		fStop = stop;
	}
}