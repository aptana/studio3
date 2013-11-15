package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class FixedMultiLineRule extends MultiLineRule
{
	private boolean endBreaksOnEOF;

	public FixedMultiLineRule(String startSequence, String endSequence, IToken token, char escape, boolean breaksOnEOF)
	{
		super(startSequence, endSequence, token, escape, false);
		this.endBreaksOnEOF = breaksOnEOF;
	}

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner)
	{
		try
		{
			fBreaksOnEOF = endBreaksOnEOF;
			return super.endSequenceDetected(scanner);
		}
		finally
		{
			fBreaksOnEOF = false;
		}
	}
}
