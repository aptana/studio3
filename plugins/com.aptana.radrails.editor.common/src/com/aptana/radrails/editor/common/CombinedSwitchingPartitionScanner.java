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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
public final class CombinedSwitchingPartitionScanner extends RuleBasedPartitionScanner {

	public final static String START_SWITCH_TAG = "__cl_start_switch_tag";
	public final static String END_SWITCH_TAG = "__cl_end_switch_tag";

	public final static String[] SWITCHING_CONTENT_TYPES = new String[] {
			START_SWITCH_TAG,
			END_SWITCH_TAG
		};
		
	private IPredicateRule[] defaultRules;
	private IPredicateRule[] primaryRules;
	private IPredicateRule[] switchRules;
	
	private Set<String> defaultContentTypes = new HashSet<String>();
	private Set<String> primaryContentTypes = new HashSet<String>();
	
	private IPartitionerSwitchStrategy partitionerSwitchStrategy;
	private IExtendedPartitioner partitioner;
	
	private boolean hasSwitch = false;
	private SequenceCharacterScanner startSequenceCharacterScanner;
	private SequenceCharacterScanner endSequenceCharacterScanner;

	private SequenceCharacterScanner sequenceCharacterScanner;
		

	/**
	 * 
	 */
	public CombinedSwitchingPartitionScanner(IPredicateRule[] defaultRules, IPredicateRule[] primaryRules,
			String[] defaultContentTypes, String[] primaryContentTypes,
			IPartitionerSwitchStrategy partitionerSwitchStrategy) {
		this.defaultRules = defaultRules;
		this.primaryRules = primaryRules;
		this.defaultContentTypes.addAll(Arrays.asList(defaultContentTypes));
		this.primaryContentTypes.addAll(Arrays.asList(primaryContentTypes));
		this.partitionerSwitchStrategy = partitionerSwitchStrategy;
				
		String[][] pairs = partitionerSwitchStrategy.getSwitchTagPairs();
		char[][] startSequences = new char[pairs.length][];
		char[][] endSequences = new char[pairs.length][];
		switchRules = new IPredicateRule[pairs.length*2];
		for (int i = 0; i < pairs.length; ++i) {
			startSequences[i] = pairs[i][0].toCharArray();
			endSequences[i] = pairs[i][1].toCharArray();
			switchRules[2*i] = new SingleTagRule(pairs[i][0], new Token(START_SWITCH_TAG));
			switchRules[2*i+1] = new SingleTagRule(pairs[i][1], new Token(END_SWITCH_TAG));
		}
		startSequenceCharacterScanner = new SequenceCharacterScanner(this, startSequences);
		endSequenceCharacterScanner = new SequenceCharacterScanner(this, endSequences);
		
		fRules = defaultRules;
		sequenceCharacterScanner = startSequenceCharacterScanner;
		setDefaultReturnToken(new Token(IDocument.DEFAULT_CONTENT_TYPE));
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
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) && partitioner != null) {
			TypedPosition partition = partitioner.findClosestPosition(offset);
			if (partition != null) {
				String type = partition.getType();
				if (primaryContentTypes.contains(type) || START_SWITCH_TAG.equals(type)) {
					fRules = primaryRules;
					sequenceCharacterScanner = endSequenceCharacterScanner;
				} else if (defaultContentTypes.contains(type) || END_SWITCH_TAG.equals(type)) {
					fRules = defaultRules;
					sequenceCharacterScanner = startSequenceCharacterScanner;
				}
			}
		}
		super.setPartialRange(document, offset, length, contentType, partitionOffset);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedPartitionScanner#nextToken()
	 */
	@Override
	public IToken nextToken() {
		if (fContentType == null || hasSwitch) {
			//don't try to resume
			return baseNextToken();
		}

		// inside a partition
		fColumn = UNDEFINED;
		boolean resume = (fPartitionOffset > -1 && fPartitionOffset < fOffset);
		fTokenOffset = resume ? fPartitionOffset : fOffset;
		
		IPredicateRule rule;
		IToken token;

		for (int i = 0; i < fRules.length; ++i) {
			rule = (IPredicateRule) fRules[i];
			token = rule.getSuccessToken();
			if (fContentType.equals(token.getData())) {
				token = rule.evaluate(sequenceCharacterScanner, resume);
				if (!token.isUndefined()) {
					fContentType = null;
					return token;
				}
				if (hasSwitchingSequence()) {
					return fDefaultReturnToken;
				}
			}
		}

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
			boolean toPrimary = (fRules == defaultRules);
			for (int i = 0; i < switchRules.length; ++i) {
				IToken token = (switchRules[i].evaluate(this));
				if (!token.isUndefined()) {
					fRules = toPrimary ? primaryRules : defaultRules;
					sequenceCharacterScanner = toPrimary ? endSequenceCharacterScanner : startSequenceCharacterScanner;
					return token;
				}
			}
		} else {
			for (int i = 0; i < fRules.length; ++i) {
				IToken token = (fRules[i].evaluate(sequenceCharacterScanner));
				if (!token.isUndefined()) {
					return token;
				}
				if (hasSwitchingSequence()) {
					return fDefaultReturnToken;
				}
			}
		}

		if (read() == EOF) {
			return Token.EOF;
		}
		return fDefaultReturnToken;
	}
	
	private boolean hasSwitchingSequence() {
		if (sequenceCharacterScanner.foundSequence()) {
			hasSwitch = true;
			return true;
		}
		return false;
	}

}
