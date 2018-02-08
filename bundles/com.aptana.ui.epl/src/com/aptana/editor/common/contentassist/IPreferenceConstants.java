/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;


public interface IPreferenceConstants
{

	/**
	 * The types of user agents to display in code assist
	 */
	String USER_AGENT_PREFERENCE = "selectUserAgents"; //$NON-NLS-1$

	/**
	 * The preference key used to determine if filtering should occur based on user agent type. There are three values:
	 * none, some, all. "none" indicates that no filtering should occur. "some" indicates that at least one user agent
	 * must be associated with the proposal for it to be included in the proposal list. "all" indicates that all user
	 * agents associated with the proposal must exist for it to be added to the proposal list
	 */
	public static final String CONTENT_ASSIST_USER_AGENT_FILTER_TYPE = "CONTENT_ASSIST_USER_AGENT_FILTER_TYPE"; //$NON-NLS-1$

	/**
	 * The characters we use to pop up content assist
	 */
	public static final String CONTENT_ASSIST_ACTIVATION_CHARACTERS = "CONTENT_ASSIST_ACTIVATION_CHARACTERS"; //$NON-NLS-1$

	/**
	 * The characters we use to pop up content assist (in proposals)
	 */
	public static final String COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS = "completionProposalActivationCharacters"; //$NON-NLS-1$

	/**
	 * The characters we use to pop up content assist (in context situations)
	 */
	public static final String CONTEXT_INFORMATION_ACTIVATION_CHARACTERS = "contextInformationActivationCharacters"; //$NON-NLS-1$

	/**
	 * The characters we use to trigger the insertion of proposals
	 */
	public static final String PROPOSAL_TRIGGER_CHARACTERS = "proposalTriggerCharacters"; //$NON-NLS-1$

}
