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
package org.eclipse.tm.internal.terminal.speedtest;


import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;

public class SpeedTestSettings {
	String fInputFile="";
	String fBufferSize="";
	String fThrottle;
	String getInputFile() {
		return fInputFile;
	}
	public String getBufferSizeString() {
		return getBufferSize()+"";
	}
	public void setBufferSizeString(String bufferSize) {
		fBufferSize = bufferSize;
	}
	public int getBufferSize() {
		try {
			return Integer.parseInt(fBufferSize);
		} catch(RuntimeException e) {
			return 1024;
		}
	}
	void setInputFile(String testFile) {
		fInputFile = testFile;
	}
	public void load(ISettingsStore store) {
		fInputFile=store.get("inputFile");
		fBufferSize=store.get("bufferSize");
		fThrottle=store.get("throttle");
	}
	public void save(ISettingsStore store) {
		store.put("inputFile", fInputFile);
		store.put("bufferSize", fBufferSize);
		store.put("throttle", fThrottle);
	}
	public String getThrottleString() {
		return fThrottle;
	}
	public int getThrottle() {
		try {
			return Integer.parseInt(fThrottle);
		} catch(RuntimeException e) {
			return 0;
		}
	}
	public void setThrottleString(String throttle) {
		fThrottle = throttle;
	}
}
