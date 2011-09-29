/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.theme.ThemePlugin;

public class ThemeingDamagerRepairer extends DefaultDamagerRepairer
{

	private TextAttribute lastAttribute;
	private String scope = StringUtil.EMPTY;
	private IRegion fLastLine;
	private int fCountForLine;
	private TypedPosition fLastPosition;

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
			fLastPosition = null;
			fCountForLine = 0;
			int offset = region.getOffset();
			scope = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(fDocument, offset);
			if (scope == null)
			{
				scope = StringUtil.EMPTY;
			}
			wipeExistingScopes(region);
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
			fLastPosition = null;
			fCountForLine = 0;
		}
	}

	/**
	 * Given a partition region, iterate through all our scope positions and wipe any in that region.
	 * 
	 * @param region
	 * @throws BadPositionCategoryException
	 */
	private void wipeExistingScopes(ITypedRegion region)
	{
		int offset = region.getOffset();
		int end = offset + region.getLength();

		try
		{
			synchronized (getLockObject(fDocument))
			{
				fDocument.addPositionCategory(ICommonConstants.SCOPE_CATEGORY);
				int index = fDocument.computeIndexInCategory(ICommonConstants.SCOPE_CATEGORY, offset);
				int endIndex = fDocument.computeIndexInCategory(ICommonConstants.SCOPE_CATEGORY, end);
				if (endIndex == index)
				{
					// there should be nothing to wipe!
					return;
				}
				// Only loop over positions[index] to positions[endIndex - 1]!
				int start;
				int stop;
				Position[] positions;
				if (fDocument instanceof AbstractDocument)
				{
					AbstractDocument abDoc = (AbstractDocument) fDocument;
					positions = abDoc.getPositions(ICommonConstants.SCOPE_CATEGORY, offset, region.getLength(), false,
							false);
					start = 0;
					stop = positions.length;
				}
				else
				{
					positions = fDocument.getPositions(ICommonConstants.SCOPE_CATEGORY);
					start = index;
					stop = endIndex;
				}
				for (int i = start; i < stop; i++)
				{
					fDocument.removePosition(ICommonConstants.SCOPE_CATEGORY, positions[i]);
				}
			}
		}
		catch (BadPositionCategoryException e)
		{
			// should never happen because we are explicitly adding the category before asking for positions inside it
			IdeLog.logError(CommonEditorPlugin.getDefault(), e.getMessage());
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e.getMessage());
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
			storeScope(last);
			if (last.length() == 0)
			{
				last = scope;
			}
			else if (scope.length() > 0)
			{
				if (scope.endsWith(last))
				{
					last = scope;
				}
				else
				{
					last = scope + " " + last; //$NON-NLS-1$
				}
			}
			IToken converted = ThemePlugin.getDefault().getThemeManager().getToken(last);
			lastAttribute = super.getTokenTextAttribute(converted);
			return lastAttribute;
		}
		else if (token.isWhitespace())
		{
			// WHat the hell was I smoking here? Just return the previous scope? Empty string?
			return lastAttribute;
		}
		lastAttribute = super.getTokenTextAttribute(token);
		return lastAttribute;
	}

	private void storeScope(String tokenLevelScope)
	{
		// TODO Redcar stores the fragment pieces individually as overlapping positions which seems like it'd be much
		// less objects/memory. Can we hack that here by looking at previous scope and diffing? We'd also have to keep
		// expanding the parent scope positions...

		// empty scope. Don't store a position for it, but do set last position to be null so we don't end up expanding
		// it.
		if (tokenLevelScope == null || tokenLevelScope.length() == 0)
		{
			// Wipe last position because we have an empty scope in between...
			fLastPosition = null;
			return;
		}
		try
		{
			int offset = fScanner.getTokenOffset();
			int length = fScanner.getTokenLength();

			// Continuing same scope as last position, expand to merge them
			if (fLastPosition != null && fLastPosition.getType().equals(tokenLevelScope))
			{
				fLastPosition.setLength((offset + length) - fLastPosition.getOffset());
			}
			else
			{
				TypedPosition newPosition = new TypedPosition(offset, length, tokenLevelScope);
				fLastPosition = newPosition;
				synchronized (getLockObject(fDocument))
				{
					fDocument.addPosition(ICommonConstants.SCOPE_CATEGORY, newPosition);
				}
			}
		}
		catch (BadLocationException e1)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e1);
		}
		catch (BadPositionCategoryException e1)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e1);
		}
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
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		super.addRange(presentation, offset, length, attr);
	}
}
