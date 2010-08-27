package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.theme.ThemePlugin;

public class ThemeingDamagerRepairer extends DefaultDamagerRepairer
{

	private TextAttribute lastAttribute;
	private String scope;

	public ThemeingDamagerRepairer(ITokenScanner scanner)
	{
		super(scanner);
	}

	@Override
	public void createPresentation(TextPresentation presentation, ITypedRegion region)
	{
		try
		{
			int offset = region.getOffset();
			scope = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(fDocument, offset);
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		finally
		{
			super.createPresentation(presentation, region);
			scope = null;
		}
	}

	@Override
	protected TextAttribute getTokenTextAttribute(IToken token)
	{
		Object data = token.getData();
		if (data instanceof String)
		{
			String last = (String) data;
			if (last.length() == 0)
			{
				last = scope;
			}
			else if (!scope.endsWith(last))
			{
				last = scope + " " + last; //$NON-NLS-1$
			}
			IToken converted = ThemePlugin.getDefault().getThemeManager().getToken(last);
			lastAttribute = super.getTokenTextAttribute(converted);
			return lastAttribute;
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
