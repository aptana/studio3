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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.swt.custom.StyleRange;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.theme.ThemePlugin;

public class NonRuleBasedDamagerRepairer implements IPresentationDamager, IPresentationRepairer
{

	/** The document this object works on */
	protected IDocument fDocument;
	/** The default text attribute if non is returned as data by the current token */
	protected IToken fDefaultTextAttribute;
	private String fFullScope;

	/**
	 * Constructor for NonRuleBasedDamagerRepairer.
	 */
	public NonRuleBasedDamagerRepairer(IToken defaultTextAttribute)
	{
		Assert.isNotNull(defaultTextAttribute);

		fDefaultTextAttribute = defaultTextAttribute;
	}

	/**
	 * @see IPresentationRepairer#setDocument(IDocument)
	 */
	public void setDocument(IDocument document)
	{
		fDocument = document;
	}

	/**
	 * Returns the end offset of the line that contains the specified offset or if the offset is inside a line
	 * delimiter, the end offset of the next line.
	 * 
	 * @param offset
	 *            the offset whose line end offset must be computed
	 * @return the line end offset for the given offset
	 * @exception BadLocationException
	 *                if offset is invalid in the current document
	 */
	protected int endOfLineOf(int offset) throws BadLocationException
	{

		IRegion info = fDocument.getLineInformationOfOffset(offset);
		if (offset <= info.getOffset() + info.getLength())
		{
			return info.getOffset() + info.getLength();
		}

		int line = fDocument.getLineOfOffset(offset);
		try
		{
			info = fDocument.getLineInformation(line + 1);
			return info.getOffset() + info.getLength();
		}
		catch (BadLocationException x)
		{
			return fDocument.getLength();
		}
	}

	/**
	 * @see IPresentationDamager#getDamageRegion(ITypedRegion, DocumentEvent, boolean)
	 */
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged)
	{
		if (!documentPartitioningChanged)
		{
			try
			{

				IRegion info = fDocument.getLineInformationOfOffset(event.getOffset());
				int start = Math.max(partition.getOffset(), info.getOffset());

				int end = event.getOffset() + (event.getText() == null ? event.getLength() : event.getText().length());

				if (info.getOffset() <= end && end <= info.getOffset() + info.getLength())
				{
					// optimize the case of the same line
					end = info.getOffset() + info.getLength();
				}
				else
				{
					end = endOfLineOf(end);
				}
				end = Math.min(partition.getOffset() + partition.getLength(), end);
				return new Region(start, end - start);

			}
			catch (BadLocationException x)
			{
			}
		}

		return partition;
	}

	/**
	 * @see IPresentationRepairer#createPresentation(TextPresentation, ITypedRegion)
	 */
	public void createPresentation(TextPresentation presentation, ITypedRegion region)
	{
		addRange(presentation, region.getOffset(), region.getLength(), getTextAttribute(region));
	}

	protected TextAttribute getTextAttribute(ITypedRegion region)
	{
		Object data = fDefaultTextAttribute.getData();
		if (data instanceof String)
		{
			// Cache the full scope so we can just re-use it. It shouldn't ever change... Previous caching of text
			// attribute ended up breaking when theme changed
			if (fFullScope == null)
			{
				try
				{
					String last = (String) data;
					int offset = region.getOffset();
					String scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
							.getScopeAtOffset(fDocument, offset);
					if (last.length() == 0)
					{
						last = scope;
					}
					else if (!scope.endsWith(last))
					{
						scope += " " + last; //$NON-NLS-1$
					}
					fFullScope = scope;
				}
				catch (BadLocationException e)
				{
					CommonEditorPlugin.logError(e);
				}
			}
			IToken token = ThemePlugin.getDefault().getThemeManager().getToken(fFullScope);
			data = token.getData();
		}
		if (data instanceof TextAttribute)
		{
			return (TextAttribute) data;
		}
		return null;
	}

	/**
	 * Adds style information to the given text presentation.
	 * 
	 * @param presentation
	 *            the text presentation to be extended
	 * @param offset
	 *            the offset of the range to be styled
	 * @param length
	 *            the length of the range to be styled
	 * @param attr
	 *            the attribute describing the style of the range to be styled
	 */
	protected void addRange(TextPresentation presentation, int offset, int length, TextAttribute attr)
	{
		if (attr != null)
		{
			presentation.addStyleRange(new StyleRange(offset, length, attr.getForeground(), attr.getBackground(), attr
					.getStyle()));
		}
	}
}