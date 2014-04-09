/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;

import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 */
public class ResumableSingleLineRule extends SingleLineRule implements IResumableRule
{

	private boolean fResume;

	/**
	 * @param startSequence
	 * @param endSequence
	 * @param token
	 * @param escapeCharacter
	 * @param breaksOnEOF
	 */
	public ResumableSingleLineRule(String startSequence, String endSequence, IToken token, char escapeCharacter,
			boolean breaksOnEOF)
	{
		super(startSequence, endSequence, token, escapeCharacter, breaksOnEOF);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.PatternRule#doEvaluate(org.eclipse.jface.text.rules.ICharacterScanner, boolean)
	 */
	@Override
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume)
	{
		try
		{
			fResume = resume;
			return super.doEvaluate(scanner, resume);
		}
		finally
		{
			fResume = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner)
	{
		CollectingCharacterScanner collectingCharacterScanner = new CollectingCharacterScanner(scanner,
				fResume ? "" : String.valueOf(fStartSequence)); //$NON-NLS-1$
		scanner = (fResume && fToken instanceof ExtendedToken) ? new PrefixedCharacterScanner(
				((ExtendedToken) fToken).getContentSubstring(fStartSequence.length), collectingCharacterScanner)
				: collectingCharacterScanner;
		if (doDetectEndSequence(scanner))
		{
			if (fToken instanceof ExtendedToken)
			{
				ExtendedToken extendedToken = (ExtendedToken) fToken;
				String prefix = fResume ? extendedToken.getContents() : ""; //$NON-NLS-1$
				extendedToken.setContents(prefix.concat(collectingCharacterScanner.getContents()));
			}
			return true;
		}
		return false;
	}

	private boolean doDetectEndSequence(ICharacterScanner scanner)
	{
		return super.endSequenceDetected(scanner);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.IResumableRule#resetRule()
	 */
	public void resetRule()
	{
		if (fToken instanceof ExtendedToken)
		{
			((ExtendedToken) fToken).setContents(StringUtil.EMPTY);
		}
	}

}
