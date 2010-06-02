/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;

/**
 * @author Max Stepanov
 *
 */
public class SubPartitionScanner implements ISubPartitionScanner {

	private static final IToken DEFAULT_TOKEN = new Token(IDocument.DEFAULT_CONTENT_TYPE);
	
	private List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
	private IToken defaultToken;
	private Set<String> contentTypes = new HashSet<String>();
	private SequenceCharacterScanner characterScanner;

	/**
	 * 
	 */
	public SubPartitionScanner(IPredicateRule[] rules, String[] contentTypes, IToken defaultToken) {
		this.rules.addAll(Arrays.asList(rules));
		this.contentTypes.addAll(Arrays.asList(contentTypes));
		this.defaultToken = defaultToken != null ? defaultToken : DEFAULT_TOKEN;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#initCharacterScanner(org.eclipse.jface.text.rules.ICharacterScanner, com.aptana.editor.common.IPartitionScannerSwitchStrategy)
	 */
	public void initCharacterScanner(ICharacterScanner baseCharacterScanner, IPartitionScannerSwitchStrategy switchStrategy) {
		this.characterScanner = new SequenceCharacterScanner(baseCharacterScanner, switchStrategy);
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#getRules()
	 */
	public Collection<IPredicateRule> getRules() {
		return rules;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#getDefaultToken()
	 */
	public IToken getDefaultToken() {
		return defaultToken;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#getCharacterScanner()
	 */
	public ICharacterScanner getCharacterScanner() {
		return characterScanner;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#foundSequence()
	 */
	public boolean foundSequence() {
		return characterScanner.foundSequence();
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#doResetRules()
	 */
	public boolean doResetRules() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#hasContentType(java.lang.String)
	 */
	public boolean hasContentType(String contentType) {
		return contentTypes.contains(contentType);
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#setLastToken(org.eclipse.jface.text.rules.IToken)
	 */
	public void setLastToken(IToken token) {
	}
}
