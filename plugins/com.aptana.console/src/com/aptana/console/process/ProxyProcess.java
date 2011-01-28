/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.console.process;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Max Stepanov
 *
 */
public class ProxyProcess extends Process {

	private Process process;
	private ESCSequnceFilterInputStream errorStream;
	private ESCSequnceFilterInputStream inputStream;
	
	/**
	 * 
	 */
	public ProxyProcess(Process process) {
		this.process = process;
	}

	/* (non-Javadoc)
	 * @see java.lang.Process#destroy()
	 */
	@Override
	public void destroy() {
		process.destroy();
	}

	/* (non-Javadoc)
	 * @see java.lang.Process#exitValue()
	 */
	@Override
	public int exitValue() {
		return process.exitValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Process#getErrorStream()
	 */
	@Override
	public InputStream getErrorStream() {
		if (errorStream == null) {
			errorStream = new ESCSequnceFilterInputStream(process.getErrorStream());
		}
		return errorStream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Process#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		if (inputStream == null) {
			inputStream = new ESCSequnceFilterInputStream(process.getInputStream());
		}
		return inputStream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Process#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return process.getOutputStream();
	}

	/* (non-Javadoc)
	 * @see java.lang.Process#waitFor()
	 */
	@Override
	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}

}
