/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;

/**
 * An interface for handling multiple languages via switching partitioning rules.
 *  
 * @author Max Stepanov
 *
 */
public interface ISubPartitionScanner {

	/**
	 * Returns current active set of rules
	 * Note: we use array instead of Collection here to reduce memory allocation for iterators in sensitive places.
	 * @return
	 */
	public IPredicateRule[] getRules();
	
	/**
	 * Returns current active default token.
	 * @return
	 */
	public IToken getDefaultToken();
	
	/**
	 * Initializes internal set of character scanners to support partitioner switching basing on the provided strategy.
	 * @param baseCharacterScanner
	 * @param switchStrategy
	 */
	public void initCharacterScanner(ICharacterScanner baseCharacterScanner, IPartitionScannerSwitchStrategy switchStrategy);
	
	/**
	 * Returns current active character scanner
	 * @return
	 */
	public ICharacterScanner getCharacterScanner();
	
	/**
	 * Returns true if the switching sequence has been found.
	 * Attention: the flag is reset upon reading it.
	 * @return
	 */
	public boolean foundSequence();
	
	/**
	 * Returns true if the swithing sequence had been found and the parent partition scanner should reset its rules and restart the token scanning.
	 * @return
	 */
	public boolean doResetRules();
	
	/**
	 * Returns true if the provided content type is handled by the partition scanner.
	 * @param contentType
	 * @return
	 */
	public boolean hasContentType(String contentType);
	
	/**
	 * Notify partition scanner about the last found token.
	 * @param token
	 */
	public void setLastToken(IToken token);
	
	/**
	 * Retrieve the last found token for the current active partitioner.
	 * @return
	 */
	public IToken getLastToken();
	
	/**
	 * Returns token (if any) to resume after switching partitions
	 * @return
	 */
	public IToken getResumeToken();

}