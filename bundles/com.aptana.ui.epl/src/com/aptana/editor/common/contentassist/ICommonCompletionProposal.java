/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;

public interface ICommonCompletionProposal extends ICompletionProposal, Comparable<ICompletionProposal>
{
	/**
	 * The proposal is an exact match
	 */
	public static int RELEVANCE_EXACT = 100;

	/**
	 * The proposal is highly relevant
	 */
	public static int RELEVANCE_HIGH = 90;

	/**
	 * The proposal is somewhat relevant
	 */
	public static int RELEVANCE_MEDIUM = 50;

	/**
	 * The proposal is slightly relevant
	 */
	public static int RELEVANCE_LOW = 10;

	/**
	 * getFileLocation
	 * 
	 * @return
	 */
	String getFileLocation();

	/**
	 * getUserAgentImages
	 * 
	 * @return
	 */
	Image[] getUserAgentImages();

	/**
	 * Value from 0-100 indicating the value of this particular proposal. Default is 0.
	 * 
	 * @return
	 */
	int getRelevance();

	/**
	 * Value from 0-100 indicating the relevance of this particular proposal. Default is 0.
	 * 
	 * @param relevance
	 */
	void setRelevance(int relevance);

	/**
	 * isDefaultSelection
	 * 
	 * @return
	 * @deprecated Use getRelevance instead
	 */
	boolean isDefaultSelection();

	/**
	 * isSuggestedSelection
	 * 
	 * @deprecated Use getRelevance instead
	 * @return
	 */
	boolean isSuggestedSelection();

	/**
	 * Set this proposal as the default selection.
	 * 
	 * @param isDefault
	 * @deprecated Use setRelevance instead
	 */
	void setIsDefaultSelection(boolean isDefault);

	/**
	 * Set this proposal as the suggested selection.
	 * 
	 * @param isSuggested
	 * @deprecated Use setRelevance instead
	 */
	void setIsSuggestedSelection(boolean isSuggested);

	/**
	 * Does the current offset support the usage of trigger characters?
	 * 
	 * @param document
	 * @param offset
	 * @param keyEvent
	 *            The trigger character
	 * @return
	 */
	boolean validateTrigger(IDocument document, int offset, KeyEvent keyEvent);
}
