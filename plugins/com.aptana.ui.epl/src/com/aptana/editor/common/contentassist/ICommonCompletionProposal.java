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
}
