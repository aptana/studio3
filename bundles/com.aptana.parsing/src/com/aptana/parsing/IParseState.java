/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.parsing.lexer.IRange;

public interface IParseState
{
	/**
	 * This will clean up the source passed in via {@link #setEditState(String, int)} for RAM/GC purposes. The
	 * implementation should retain a hashcode of the source or any other artifacts to be able to compare later via
	 * {@link #requiresReparse(IParseState)}
	 */
	public void clearEditState();


	/**
	 * getSource
	 * 
	 * @return
	 */
	public String getSource();

	/**
	 * getStartingOffset
	 * 
	 * @return
	 */
	public int getStartingOffset();

	/**
	 * Returns the list of ranges in the source to skip.
	 * 
	 * @return the list of ranges in an array
	 */
	public IRange[] getSkippedRanges();

	/**
	 * Returns parsing progress monitor primarily for cancellation checks.
	 * 
	 * @return
	 */
	public IProgressMonitor getProgressMonitor();

	/**
	 * Set parsing progress monitor
	 * 
	 * @param monitor
	 */
	public void setProgressMonitor(IProgressMonitor monitor);

	/**
	 * @return an immutable object to be used as the key for this parse state (i.e.: object with hashCode/equals).
	 */
	public IParseStateCacheKey getCacheKey(String contentTypeId);



}
