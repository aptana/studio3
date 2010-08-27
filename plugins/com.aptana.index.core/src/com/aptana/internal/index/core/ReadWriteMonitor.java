/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.internal.index.core;

import com.aptana.index.core.IReadWriteMonitor;

/**
 * Monitor ensuring no more than one writer working concurrently. Multiple readers are allowed to perform
 * simultaneously.
 */
public class ReadWriteMonitor implements IReadWriteMonitor
{

	/**
	 * <0 : writing (cannot go beyond -1, i.e one concurrent writer) =0 : idle >0 : reading (number of concurrent
	 * readers)
	 */
	private int status = 0;

	/* (non-Javadoc)
	 * @see com.aptana.internal.index.core.IReadWriteMonitor#enterRead()
	 */
	public synchronized void enterRead()
	{
		while (status < 0)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// ignore
			}
		}
		status++;
	}

	/* (non-Javadoc)
	 * @see com.aptana.internal.index.core.IReadWriteMonitor#enterWrite()
	 */
	public synchronized void enterWrite()
	{
		while (status != 0)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// ignore
			}
		}
		status--;
	}

	/* (non-Javadoc)
	 * @see com.aptana.internal.index.core.IReadWriteMonitor#exitRead()
	 */
	public synchronized void exitRead()
	{

		if (--status == 0)
			notifyAll();
	}

	/* (non-Javadoc)
	 * @see com.aptana.internal.index.core.IReadWriteMonitor#exitWrite()
	 */
	public synchronized void exitWrite()
	{

		if (++status == 0)
			notifyAll();
	}

	/* (non-Javadoc)
	 * @see com.aptana.internal.index.core.IReadWriteMonitor#exitReadEnterWrite()
	 */
	public synchronized boolean exitReadEnterWrite()
	{
		if (status != 1)
			return false; // only continue if this is the only reader

		status = -1;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aptana.internal.index.core.IReadWriteMonitor#exitWriteEnterRead()
	 */
	public synchronized void exitWriteEnterRead()
	{
		this.exitWrite();
		this.enterRead();
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		if (status == 0)
		{
			buffer.append("Monitor idle "); //$NON-NLS-1$
		}
		else if (status < 0)
		{
			buffer.append("Monitor writing "); //$NON-NLS-1$
		}
		else if (status > 0)
		{
			buffer.append("Monitor reading "); //$NON-NLS-1$
		}
		buffer.append("(status = "); //$NON-NLS-1$
		buffer.append(this.status);
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}
}
