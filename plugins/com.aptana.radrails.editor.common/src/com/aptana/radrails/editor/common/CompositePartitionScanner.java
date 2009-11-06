/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.radrails.editor.common;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Max Stepanov
 *
 */
public final class CompositePartitionScanner extends RuleBasedPartitionScanner {

	public final static String START_SWITCH_TAG = "__cl_start_switch_tag";
	public final static String END_SWITCH_TAG = "__cl_end_switch_tag";

	public final static String[] SWITCHING_CONTENT_TYPES = new String[] {
			START_SWITCH_TAG,
			END_SWITCH_TAG
		};
		
	private ISubPartitionScanner defaultPartitionScanner;
	private ISubPartitionScanner primaryPartitionScanner;
	
	private ISubPartitionScanner currentPartitionScanner;
	
	private IPredicateRule[] switchRules;
		
	private IExtendedPartitioner partitioner;
	
	private boolean hasSwitch = false;
		

	/**
	 * 
	 */
	public CompositePartitionScanner(ISubPartitionScanner defaultPartitionScanner, ISubPartitionScanner primaryPartitionScanner,
			IPartitionerSwitchStrategy partitionerSwitchStrategy) {
		this.defaultPartitionScanner = defaultPartitionScanner;
		this.primaryPartitionScanner = primaryPartitionScanner;
		defaultPartitionScanner.initCharacterScanner(this, partitionerSwitchStrategy.getDefaultSwitchStrategy());
		primaryPartitionScanner.initCharacterScanner(this, partitionerSwitchStrategy.getPrimarySwitchStrategy());
				
		String[][] pairs = partitionerSwitchStrategy.getSwitchTagPairs();
		switchRules = new IPredicateRule[pairs.length*2];
		for (int i = 0; i < pairs.length; ++i) {
			switchRules[2*i] = new SingleTagRule(pairs[i][0], new Token(START_SWITCH_TAG));
			switchRules[2*i+1] = new SingleTagRule(pairs[i][1], new Token(END_SWITCH_TAG));
		}
				
		currentPartitionScanner = defaultPartitionScanner;
	}

	/**
	 * @param partitioner the partitioner to set
	 */
	/* package */ void setPartitioner(IExtendedPartitioner partitioner) {
		this.partitioner = partitioner;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedPartitionScanner#setPredicateRules(org.eclipse.jface.text.rules.IPredicateRule[])
	 */
	@Override
	public void setPredicateRules(IPredicateRule[] rules) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedPartitionScanner#setPartialRange(org.eclipse.jface.text.IDocument, int, int, java.lang.String, int)
	 */
	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		defaultTokenState = null;
		currentPartitionScanner = defaultPartitionScanner;
		currentPartitionScanner.setLastToken(new Token(contentType));
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) && partitioner != null) {
			TypedPosition partition = partitioner.findClosestPosition(offset);
			if (partition != null) {
				if (partition.overlapsWith(offset, length)) {
					partition = partitioner.findClosestPosition(offset-1);
				}
			}
			if (partition != null) {
				String type = partition.getType();
				if (primaryPartitionScanner.hasContentType(type)) {
					currentPartitionScanner = primaryPartitionScanner;
				} else if (START_SWITCH_TAG.equals(type)) {
					hasSwitch = true;
				}
				currentPartitionScanner.setLastToken(new Token(type));
			}
		}
		super.setPartialRange(document, offset, length, contentType, partitionOffset);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenOffset()
	 */
	@Override
	public int getTokenOffset() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			return defaultTokenState.offset;
		}
		return super.getTokenOffset();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenLength()
	 */
	@Override
	public int getTokenLength() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			return defaultTokenState.length;
		}
		return super.getTokenLength();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getColumn()
	 */
	@Override
	public int getColumn() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			return defaultTokenState.column;
		}
		return super.getColumn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedPartitionScanner#nextToken()
	 */
	@Override
	public IToken nextToken() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			IToken token = defaultTokenState.token;
			defaultTokenState = null;
			System.out.println("> "+token.getData());
			return token;
		}
		if (fContentType == null || hasSwitch) {
			//don't try to resume
			return baseNextToken();
		}

		// inside a partition
		fColumn = UNDEFINED;
		boolean resume = (fPartitionOffset > -1 && fPartitionOffset < fOffset);
		fTokenOffset = resume ? fPartitionOffset : fOffset;
		
		IToken token;

		boolean doResetRules = false;
		do {
			for (IPredicateRule rule : currentPartitionScanner.getRules()) {
				token = rule.getSuccessToken();
				if (fContentType.equals(token.getData())) {
					token = rule.evaluate(currentPartitionScanner.getCharacterScanner(), resume);
					if (!token.isUndefined()) {
						fContentType = null;
						currentPartitionScanner.setLastToken(token);
						return token;
					}
					if (doResetRules = currentPartitionScanner.doResetRules()) {
						break;
					}
					if (hasSwitchingSequence()) {
						return currentPartitionScanner.getDefaultToken();
					}
				}
			}
		} while (doResetRules);

		// haven't found any rule for this type of partition
		fContentType = null;
		if (resume) {
			fOffset = fPartitionOffset;
		}
		return baseNextToken();
	}
	
	private IToken baseNextToken() {
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;

		if (hasSwitch) {
			hasSwitch = false;
			boolean toPrimary = (currentPartitionScanner == defaultPartitionScanner);
			for (int i = 0; i < switchRules.length; ++i) {
				IToken token = (switchRules[i].evaluate(this));
				if (!token.isUndefined()) {
					currentPartitionScanner = toPrimary ? primaryPartitionScanner : defaultPartitionScanner;
					return token;
				}
			}
		} else {
			boolean doResetRules = false;
			do {
				for (IPredicateRule rule : currentPartitionScanner.getRules()) {
					IToken token = rule.evaluate(currentPartitionScanner.getCharacterScanner());
					if (!token.isUndefined()) {
						currentPartitionScanner.setLastToken(token);
						return token;
					}
					if (doResetRules = currentPartitionScanner.doResetRules()) {
						break;
					}
					if (hasSwitchingSequence()) {
						return currentPartitionScanner.getDefaultToken();
					}
				}
			} while (doResetRules);
		}

		if (read() == EOF) {
			return Token.EOF;
		}
		return currentPartitionScanner.getDefaultToken();
	}
	
	private boolean hasSwitchingSequence() {
		if (currentPartitionScanner.foundSequence()) {
			hasSwitch = true;
			return true;
		}
		return false;
	}

}
