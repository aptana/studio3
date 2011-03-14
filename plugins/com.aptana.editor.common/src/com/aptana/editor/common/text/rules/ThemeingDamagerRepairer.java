/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.theme.ThemePlugin;

public class ThemeingDamagerRepairer extends DefaultDamagerRepairer
{

	private TextAttribute lastAttribute;
	private String scope = StringUtil.EMPTY;
	private IRegion fLastLine;
	private int fCountForLine;

	public ThemeingDamagerRepairer(ITokenScanner scanner)
	{
		super(scanner);
	}

	@Override
	public void createPresentation(TextPresentation presentation, ITypedRegion region)
	{
		try
		{
			fLastLine = null;
			fCountForLine = 0;
			int offset = region.getOffset();
			scope = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(fDocument, offset);
			if (scope == null)
			{
				scope = StringUtil.EMPTY;
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		finally
		{
			synchronized (getLockObject(fDocument))
			{
				super.createPresentation(presentation, region);
			}
			scope = StringUtil.EMPTY;
			fLastLine = null;
			fCountForLine = 0;
		}
	}

	private static Object getLockObject(Object object)
	{
		if (object instanceof ISynchronizable)
		{
			Object lock = ((ISynchronizable) object).getLockObject();
			if (lock != null)
			{
				return lock;
			}
		}
		return object;
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
			else if (scope.length() > 0 && !scope.endsWith(last))
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

	@Override
	protected void addRange(TextPresentation presentation, int offset, int length, TextAttribute attr)
	{
		try
		{
			// first time, grab line info
			if (fLastLine == null)
			{
				fLastLine = fDocument.getLineInformationOfOffset(offset);
				fCountForLine = 1;
			}
			else
			{
				// is this still on the same line?
				if (offset > (fLastLine.getOffset() + fLastLine.getLength()))
				{
					// it's a new line, reset counter, update line region
					fCountForLine = 0;
					fLastLine = fDocument.getLineInformationOfOffset(offset);
				}
				else
				{
					// same line, update counter
					fCountForLine++;
					if (fCountForLine > 200)
					{
						// only record 200 styles per line!
						return;
					}
				}
			}
		}
		catch (BadLocationException e)
		{
			CommonEditorPlugin.logError(e);
		}
		super.addRange(presentation, offset, length, attr);
	}
}
