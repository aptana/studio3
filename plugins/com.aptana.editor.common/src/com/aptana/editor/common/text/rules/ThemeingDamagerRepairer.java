/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
