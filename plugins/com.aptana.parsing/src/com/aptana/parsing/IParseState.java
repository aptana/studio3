/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.IRange;

public interface IParseState
{
	/**
	 * clearEditState
	 */
	public void clearEditState();

	/**
	 * getInsertedText
	 * 
	 * @return
	 */
	public char[] getInsertedText();

	/**
	 * getParseResult
	 * 
	 * @return
	 */
	public IParseRootNode getParseResult();

	/**
	 * getRemovedLength
	 * 
	 * @return
	 */
	public int getRemovedLength();

	/**
	 * getSource
	 * 
	 * @return
	 */
	public char[] getSource();

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
	 * Returns language-specific properties related to the parser.
	 * 
	 * @return the properties in a map
	 */
	public Map<String, Object> getProperties();

	/**
	 * setEditState
	 * 
	 * @param source
	 * @param insertedText
	 * @param startingOffset
	 * @param removedLength
	 */
	public void setEditState(String source, String insertedText, int startingOffset, int removedLength);

	/**
	 * setParseResult
	 * 
	 * @param result
	 */
	public void setParseResult(IParseRootNode result);

	/**
	 * Returns a list of the errors found in the document.
	 * 
	 * @return an list of IParseError
	 */
	public List<IParseError> getErrors();

	/**
	 * Adds error to the list of errors
	 * 
	 * @param error
	 */
	public void addError(IParseError error);

	/**
	 * Clears the list of errors
	 */
	public void clearErrors();
	
	/**
	 * Returns parsing progress monitor primarily for cancellation checks.
	 * 
	 * @return
	 */
	public IProgressMonitor getProgressMonitor();

	/**
	 * Set parsing progress monitor
	 * @param monitor
	 */
	public void setProgressMonitor(IProgressMonitor monitor);
}
