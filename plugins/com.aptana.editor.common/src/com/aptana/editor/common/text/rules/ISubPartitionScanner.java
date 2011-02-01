/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import java.util.Collection;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;

public interface ISubPartitionScanner {

	public Collection<IPredicateRule> getRules();
	public IToken getDefaultToken();
	
	public void initCharacterScanner(ICharacterScanner baseCharacterScanner, IPartitionScannerSwitchStrategy switchStrategy);
	public ICharacterScanner getCharacterScanner();
	public boolean foundSequence();
	public boolean doResetRules();
	
	public boolean hasContentType(String contentType);
	
	public void setLastToken(IToken token);
	
}