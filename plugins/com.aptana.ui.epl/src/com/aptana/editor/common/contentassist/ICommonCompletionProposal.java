/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
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
