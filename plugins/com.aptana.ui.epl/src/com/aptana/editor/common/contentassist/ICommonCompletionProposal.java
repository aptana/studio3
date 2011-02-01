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

public interface ICommonCompletionProposal extends ICompletionProposal
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
	 * isDefaultSelection
	 * 
	 * @return
	 */
	boolean isDefaultSelection();
	
	/**
	 * isSuggestedSelection
	 * 
	 * @return
	 */
	boolean isSuggestedSelection();

	/**
	 * Set this proposal as the default selection.
	 * 
	 * @param isDefault
	 */
	void setIsDefaultSelection(boolean isDefault);

	/**
	 * Set this proposal as the suggested selection.
	 * 
	 * @param isSuggested
	 */
	void setIsSuggestedSelection(boolean isSuggested);
}
