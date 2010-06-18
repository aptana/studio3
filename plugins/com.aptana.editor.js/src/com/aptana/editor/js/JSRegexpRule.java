package com.aptana.editor.js;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class JSRegexpRule extends MultiLineRule
{

	public JSRegexpRule(IToken successToken)
	{
		super("/", "/", successToken, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner)
	{
		boolean result = super.endSequenceDetected(scanner);
		if (!result)
			return false;
		// Gobble up i, g or m if they're next
		while (true)
		{
			switch (scanner.read())
			{
				case 'i':
				case 'g':
				case 'm':
					// gobble it up!
					continue;
				case ICharacterScanner.EOF:
					return true;
				default:
					scanner.unread();
					return true;
			}
		}
	}
}
