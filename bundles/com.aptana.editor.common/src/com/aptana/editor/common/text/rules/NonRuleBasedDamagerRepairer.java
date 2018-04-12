/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.swt.custom.StyleRange;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

/**
 * Applies a single scope/text attribute to the entire region. Useful for partitions that have no sub-scopes.
 * 
 * @author Max Stepanov
 * @author cwilliams
 */
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

				int end = event.getOffset()
						+ ((event.getText() == null) ? event.getLength() : event.getText().length());

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
		wipeExistingScopes(region);
		synchronized (getLockObject(fDocument))
		{
			try
			{
				fDocument.addPositionCategory(ICommonConstants.SCOPE_CATEGORY);
				fDocument.addPosition(
						ICommonConstants.SCOPE_CATEGORY,
						new TypedPosition(region.getOffset(), region.getLength(), (String) fDefaultTextAttribute
								.getData()));
			}
			catch (Exception e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			}
		}

		addRange(presentation, region.getOffset(), region.getLength(), getTextAttribute(region));
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
					String scope = getDocumentScopeManager().getScopeAtOffset(fDocument, offset);
					if (last.length() == 0)
					{
						last = scope;
					}
					else if (!scope.endsWith(last))
					{
						scope += ' ' + last;
					}
					fFullScope = scope;
				}
				catch (BadLocationException e)
				{
					IdeLog.logError(CommonEditorPlugin.getDefault(), e);
				}
			}
			IToken token = getThemeManager().getToken(fFullScope);
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

	protected IDocumentScopeManager getDocumentScopeManager()
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager();
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}
}