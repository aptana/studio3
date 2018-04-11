/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import java.text.MessageFormat;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IDebugScopes;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.IPartitionerSwitchStrategy;

/**
 * @author Max Stepanov
 */
public final class CompositePartitionScanner extends RuleBasedPartitionScanner {

	public final static String START_SWITCH_TAG = "__common_start_switch_tag"; //$NON-NLS-1$
	public final static String END_SWITCH_TAG = "__common_end_switch_tag"; //$NON-NLS-1$

	public final static String[] SWITCHING_CONTENT_TYPES = new String[] { START_SWITCH_TAG, END_SWITCH_TAG };

	private final boolean traceEnabled = IdeLog.isTraceEnabled(CommonEditorPlugin.getDefault(), IDebugScopes.PARTITIONER);

	private ISubPartitionScanner defaultPartitionScanner;
	private ISubPartitionScanner primaryPartitionScanner;

	private ISubPartitionScanner currentPartitionScanner;

	private IPredicateRule[][] switchRules;

	private IExtendedPartitioner partitioner;

	private boolean hasSwitch;
	private boolean hasResume;

	private DefaultTokenState defaultTokenState;

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
		switchRules = new IPredicateRule[pairs.length][];
		for (int i = 0; i < pairs.length; ++i) {
			switchRules[i] = new IPredicateRule[] { new SingleTagRule(pairs[i][0], new Token(START_SWITCH_TAG)), new SingleTagRule(pairs[i][1], new Token(END_SWITCH_TAG)) };
		}

		currentPartitionScanner = defaultPartitionScanner;
		setDefaultReturnToken(new Token(IDocument.DEFAULT_CONTENT_TYPE));
	}

	/**
	 * @param partitioner
	 *            the partitioner to set
	 */
	public/* package */void setPartitioner(IExtendedPartitioner partitioner) {
		this.partitioner = partitioner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.rules.RuleBasedPartitionScanner#setPredicateRules
	 * (org.eclipse.jface.text.rules.IPredicateRule[])
	 */
	@Override
	public void setPredicateRules(IPredicateRule[] rules) {
		throw new UnsupportedOperationException("unsupported method"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.rules.RuleBasedPartitionScanner#setPartialRange
	 * (org.eclipse.jface.text.IDocument,
	 * int, int, java.lang.String, int)
	 */
	@Override
	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset) {
		defaultTokenState = null;
		hasResume = false;
		resetRules(defaultPartitionScanner.getRules());
		resetRules(primaryPartitionScanner.getRules());
		currentPartitionScanner = defaultPartitionScanner;
		currentPartitionScanner.setLastToken(new Token(contentType));
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType) && partitioner != null) {
			TypedPosition partition = partitioner.findClosestPosition(offset);
			if (partition != null) {
				if (partition.overlapsWith(offset, length)) {
					partition = partitioner.findClosestPosition(offset - 1);
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
		} else if (primaryPartitionScanner.hasContentType(contentType)) {
			currentPartitionScanner = primaryPartitionScanner;
		}
		super.setPartialRange(document, offset, length, contentType, partitionOffset);
	}

	private static void resetRules(IPredicateRule[] rules) {
		for (IPredicateRule rule : rules) {
			if (rule instanceof IResumableRule) {
				((IResumableRule) rule).resetRule();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenOffset()
	 */
	@Override
	public int getTokenOffset() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			return defaultTokenState.offset;
		}
		return super.getTokenOffset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getTokenLength()
	 */
	@Override
	public int getTokenLength() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			return defaultTokenState.length;
		}
		return super.getTokenLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#getColumn()
	 */
	@Override
	public int getColumn() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			return defaultTokenState.column;
		}
		return super.getColumn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.RuleBasedPartitionScanner#nextToken()
	 */
	@Override
	public IToken nextToken() {
		if (defaultTokenState != null && defaultTokenState.hasToken()) {
			IToken token = defaultTokenState.token;
			defaultTokenState = null;
			if (traceEnabled) {
				trace(MessageFormat.format("> {0} {1}:{2}", token.getData(), getTokenOffset(), getTokenLength())); //$NON-NLS-1$
			}
			return token;
		}
		if (fContentType == null || hasSwitch) {
			// don't try to resume
			return baseNextToken();
		}
		IToken token = doResumeContentType();
		if (token != null) {
			return token;
		}

		return baseNextToken();
	}

	private IToken doResumeContentType() {
		if (fContentType == null) {
			return null;
		}
		// inside a partition
		fColumn = UNDEFINED;
		boolean resume = (fPartitionOffset > -1 && fPartitionOffset < fOffset);
		fTokenOffset = resume ? fPartitionOffset : fOffset;
		if (hasResume) {
			resume = true;
			hasResume = false;
		}

		IToken token;
		boolean doResetRules;
		do {
			doResetRules = false;
			for (IPredicateRule rule : currentPartitionScanner.getRules()) {
				token = rule.getSuccessToken();
				if (fContentType.equals(token.getData())) {
					token = rule.evaluate(currentPartitionScanner.getCharacterScanner(), resume);
					if (!token.isUndefined() && fOffset != fTokenOffset) {
						fContentType = null;
						currentPartitionScanner.setLastToken(token);
						currentPartitionScanner.doResetRules();
						return returnToken(token);
					}
					if (doResetRules = currentPartitionScanner.doResetRules()) {
						break;
					}
					if (hasSwitchingSequence()) {
						fContentType = null;
						return getDefaultToken();
					}
				}
			}
		} while (doResetRules);

		// haven't found any rule for this type of partition
		fContentType = null;
		if (resume && fPartitionOffset >= 0) {
			fOffset = fPartitionOffset;
			fPartitionOffset = -1;
		}
		return null;
	}

	private IToken baseNextToken() {
		fPartitionOffset = -1;
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;

		if (hasSwitch) {
			hasSwitch = false;
			boolean toPrimary = (currentPartitionScanner == defaultPartitionScanner);
			for (int i = 0; i < switchRules.length; ++i) {
				IToken token = (switchRules[i][toPrimary ? 0 : 1].evaluate(this));
				if (!token.isUndefined()) {
					currentPartitionScanner = toPrimary ? primaryPartitionScanner : defaultPartitionScanner;
					IToken lastToken = currentPartitionScanner.getLastToken();
					if (lastToken != null && lastToken.getData() instanceof String) {
						fContentType = (String) lastToken.getData();
						hasResume = true;
					}
					return returnToken(token);
				}
			}
		} else {
			boolean doResetRules;
			do {
				doResetRules = false;
				for (IPredicateRule rule : currentPartitionScanner.getRules()) {
					IToken token = rule.evaluate(currentPartitionScanner.getCharacterScanner());
					if (!token.isUndefined()) {
						currentPartitionScanner.setLastToken(token);
						currentPartitionScanner.doResetRules();
						return returnToken(token);
					}
					if (doResetRules = currentPartitionScanner.doResetRules()) {
						IToken resumeToken = currentPartitionScanner.getResumeToken();
						if (resumeToken != null && resumeToken.getData() instanceof String) {
							fContentType = (String) resumeToken.getData();
							hasResume = true;
							token = doResumeContentType();
							if (token != null) {
								return token;
							}
						}
						break;
					}
					if (hasSwitchingSequence()) {
						return getDefaultToken();
					}
				}
			} while (doResetRules);
		}

		if (read() == EOF) {
			return returnToken(Token.EOF);
		}
		currentPartitionScanner.setLastToken(null);
		return getDefaultToken();
	}

	private IToken getDefaultToken() {
		if (defaultTokenState == null) {
			defaultTokenState = new DefaultTokenState(currentPartitionScanner.getDefaultToken());
		}
		return fDefaultReturnToken;
	}

	private IToken returnToken(IToken token) {
		if (defaultTokenState != null) {
			if (defaultTokenState.saveToken(token)) {
				token = defaultTokenState.defaultToken;
			} else {
				defaultTokenState = null;
			}
		}
		if (traceEnabled) {
			trace(MessageFormat.format("> {0} {1}:{2}", token.getData(), getTokenOffset(), getTokenLength())); //$NON-NLS-1$
		}
		return token;
	}

	private void trace(String string) {
		IdeLog.logTrace(CommonEditorPlugin.getDefault(), string);
	}

	private boolean hasSwitchingSequence() {
		if (currentPartitionScanner.foundSequence()) {
			hasSwitch = true;
			return true;
		}
		return false;
	}

	private class DefaultTokenState {
		private int offset;
		private int length;
		private int column;
		private IToken defaultToken;
		private IToken token;

		public DefaultTokenState(IToken defaultToken) {
			this.defaultToken = defaultToken;
			this.offset = fTokenOffset;
			this.column = getColumn();
		}

		public boolean saveToken(IToken token) {
			length = fTokenOffset - offset;
			if (length == 0) {
				return false;
			}
			this.token = token;
			return true;
		}

		public boolean hasToken() {
			return token != null;
		}
	}
}
