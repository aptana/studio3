/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

public interface ICommonCompletionProposal extends ICompletionProposal, Comparable<ICompletionProposal>
{
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
	 * Extra information relevant to the proposal in question
	 * 
	 * @return
	 */
	String getExtraInfo();
}
