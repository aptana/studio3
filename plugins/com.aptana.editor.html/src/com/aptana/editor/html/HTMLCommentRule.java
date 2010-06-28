package com.aptana.editor.html;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

import com.aptana.editor.common.text.rules.SequenceCharacterScanner;

public class HTMLCommentRule extends MultiLineRule
{

	public HTMLCommentRule(IToken token)
	{
		super("<!--", "-->", token, (char) 0, true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		if (scanner instanceof SequenceCharacterScanner)
		{
			// when checking for comment, do not search for potential sequence
			SequenceCharacterScanner seqScanner = (SequenceCharacterScanner) scanner;
			seqScanner.setSequenceIgnored(true);
			IToken token = super.evaluate(scanner, resume);
			seqScanner.setSequenceIgnored(false);
			return token;
		}
		return super.evaluate(scanner, resume);
	}
}
