package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

public interface ICommonCompletionProposal extends ICompletionProposal
{
	String getFileLocation();
	
	Image[] getUserAgentImages();
	
	boolean isDefaultSelection();
}
