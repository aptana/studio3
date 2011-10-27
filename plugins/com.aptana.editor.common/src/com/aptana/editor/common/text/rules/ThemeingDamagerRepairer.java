/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IRegion;
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
import com.aptana.editor.common.IDebugScopes;
import com.aptana.theme.ThemePlugin;

public class ThemeingDamagerRepairer extends DefaultDamagerRepairer
{

	private TextAttribute lastAttribute;
	private String scope = StringUtil.EMPTY;
	private IRegion fLastLine;
	private int fCountForLine;
	private TypedPosition fLastPosition;
	private List<Position> newPositions;
	private Position[] oldPositions;

	public ThemeingDamagerRepairer(ITokenScanner scanner)
	{
		super(scanner);
	}

	@Override
	public void createPresentation(TextPresentation presentation, ITypedRegion region)
	{
		try
		{
			if (IdeLog.isInfoEnabled(CommonEditorPlugin.getDefault(), IDebugScopes.PRESENTATION))
			{
				IdeLog.logInfo(CommonEditorPlugin.getDefault(), MessageFormat.format(
						"Creating presentation for region at offset {0}, length {1} in document of length {2}", //$NON-NLS-1$
						region.getOffset(), region.getLength(), fDocument.getLength()), IDebugScopes.PRESENTATION);
			}
			fLastLine = null;
			fLastPosition = null;
			fCountForLine = 0;
			int offset = region.getOffset();
			scope = CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(fDocument, offset);
			if (scope == null)
			{
				scope = StringUtil.EMPTY;
			}
			oldPositions = getExistingScopes(region);
			newPositions = new ArrayList<Position>();
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		finally
		{
			// Do coloring and collect all the scopes
			super.createPresentation(presentation, region);
			updateScopePositions();

			oldPositions = null;
			newPositions = null;
			scope = StringUtil.EMPTY;
			fLastLine = null;
			fLastPosition = null;
			fCountForLine = 0;
		}
	}

	/**
	 * This attempts to do minimal add/remove calls for positions on IDocument, since each call synchronizes on the
	 * document, which adds up very quickly.
	 */
	private void updateScopePositions()
	{
		try
		{
			int oldIndex = 0;
			int newIndex = 0;
			int oldLength = oldPositions.length;
			int newLength = newPositions.size();
			while (newIndex < newLength && oldIndex < oldLength)
			{
				Position newPosition = newPositions.get(newIndex);
				Position oldPosition = oldPositions[oldIndex];
				if (newPosition.equals(oldPosition))
				{
					// a match, move on
					oldIndex++;
					newIndex++;
					continue;
				}
				// uh oh, no match. figure out if we need to remove old / add new
				else if (newPosition.offset + newPosition.length < oldPosition.offset)
				{
					// new Position is before old, so just add the new one
					fDocument.addPosition(ICommonConstants.SCOPE_CATEGORY, newPosition);
					newIndex++;
					continue;
				}
				if (oldPosition.offset + oldPosition.length < newPosition.offset)
				{
					// old Position is before new, so just remove the old one
					oldPosition.delete();
					fDocument.removePosition(ICommonConstants.SCOPE_CATEGORY, oldPosition);
					oldIndex++;
					continue;
				}
				else
				{
					// same scope, but offset/length has changed. Update them on position
					if (((TypedPosition) oldPosition).getType().equals(((TypedPosition) newPosition).getType()))
					{
						oldPosition.offset = newPosition.offset;
						oldPosition.length = newPosition.length;
					}
					else
					{
						// scope has changed, remove old, add new
						oldPosition.delete();
						fDocument.removePosition(ICommonConstants.SCOPE_CATEGORY, oldPosition);
						fDocument.addPosition(ICommonConstants.SCOPE_CATEGORY, newPosition);
					}
					oldIndex++;
					newIndex++;
					continue;
				}
			}
			// if we went off the end of one list, but not the other - We still need to add or remove!
			for (int i = oldIndex; i < oldLength; i++)
			{
				oldPositions[i].delete();
				fDocument.removePosition(ICommonConstants.SCOPE_CATEGORY, oldPositions[i]);
			}
			for (int i = newIndex; i < newLength; i++)
			{
				fDocument.addPosition(ICommonConstants.SCOPE_CATEGORY, newPositions.get(i));
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

	/**
	 * Grab the set of scopes stored for this document in this region.
	 * 
	 * @param region
	 * @throws BadPositionCategoryException
	 */
	private Position[] getExistingScopes(ITypedRegion region)
	{
		int offset = region.getOffset();
		int end = offset + region.getLength();

		try
		{
			fDocument.addPositionCategory(ICommonConstants.SCOPE_CATEGORY);
			int index = fDocument.computeIndexInCategory(ICommonConstants.SCOPE_CATEGORY, offset);
			int endIndex = fDocument.computeIndexInCategory(ICommonConstants.SCOPE_CATEGORY, end);
			if (endIndex == index)
			{
				// there should be nothing to wipe!
				return new Position[0];
			}
			if (fDocument instanceof AbstractDocument)
			{
				AbstractDocument abDoc = (AbstractDocument) fDocument;
				Position[] positions = abDoc.getPositions(ICommonConstants.SCOPE_CATEGORY, offset, region.getLength(),
						false, false);
				return positions;
			}
			else
			{
				Position[] positions = fDocument.getPositions(ICommonConstants.SCOPE_CATEGORY);
				int start = index;
				int stop = endIndex;

				int length = stop - start;
				Position[] sub = new Position[length];
				System.arraycopy(positions, start, sub, 0, length);
				return sub;
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
		return new Position[0];
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
					last = scope + ' ' + last;
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
			newPositions.add(newPosition);
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
