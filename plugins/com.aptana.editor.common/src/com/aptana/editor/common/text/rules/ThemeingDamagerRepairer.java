package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.theme.ThemePlugin;

public class ThemeingDamagerRepairer extends DefaultDamagerRepairer
{

	private TextAttribute lastAttribute;

	public ThemeingDamagerRepairer(ITokenScanner scanner)
	{
		super(scanner);
	}

	@Override
	protected TextAttribute getTokenTextAttribute(IToken token)
	{
		Object data = token.getData();
		if (data instanceof String)
		{
			try
			{
				String last = (String) data;
				int offset = fScanner.getTokenOffset();
				String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
						.getScopeAtOffset(fDocument, offset);
				scope += " " + last; //$NON-NLS-1$
				IToken converted = ThemePlugin.getDefault().getThemeManager().getToken(scope);
				lastAttribute = super.getTokenTextAttribute(converted);
				return lastAttribute;
			}
			catch (BadLocationException e)
			{
				CommonEditorPlugin.logError(e);
			}
		}
		else if (token.isWhitespace())
		{
			try
			{
				int offset = fScanner.getTokenOffset();
				String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
						.getScopeAtOffset(fDocument, offset);
				IToken converted = ThemePlugin.getDefault().getThemeManager().getToken(scope);
				lastAttribute = super.getTokenTextAttribute(converted);
				return lastAttribute;
			}
			catch (BadLocationException e)
			{
				CommonEditorPlugin.logError(e);
			}
		}
		lastAttribute = super.getTokenTextAttribute(token);
		return lastAttribute;
	}
}
