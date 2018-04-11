/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public interface ICommonContentAssistProcessor
{
	/**
	 * computeCompletionProposals
	 * 
	 * @param viewer
	 * @param offset
	 * @param activationChar
	 * @param autoActivated
	 * @return
	 */
	ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar,
			boolean autoActivated);

	/**
	 * Is the current position a valid place to show content assist?
	 * 
	 * @param c
	 *            Character
	 * @param keyCode
	 *            Key code
	 * @param document
	 *            The current document
	 * @param offset
	 *            Offset into the document
	 * @return
	 */
	boolean isValidAutoActivationLocation(char c, int keyCode, IDocument document, int offset);

	/**
	 * Is the current position a valid place to show content assist?
	 * 
	 * @param c
	 *            Character
	 * @param keyCode
	 *            Key code
	 */
	boolean isValidIdentifier(char c, int keyCode);

	/**
	 * Is the current position a valid place to show content assist?
	 * 
	 * @param c
	 *            Character
	 * @param keyCode
	 *            Key code
	 */
	boolean isValidActivationCharacter(char c, int keyCode);

	/**
	 * Disposes of any unused resources
	 */
	void dispose();

	/**
	 * Return a list of all user agent ids that are active in the project associated whit this processor
	 * 
	 * @return Returns an array of user agent ids. These ids can be used with the UserAgentManager to retrieve UserAgent
	 *         instances and icons
	 */
	String[] getActiveUserAgentIds();
}
