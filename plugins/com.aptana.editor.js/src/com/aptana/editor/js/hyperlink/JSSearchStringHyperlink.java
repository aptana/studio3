/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.hyperlink;

import java.util.List;

import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IRegion;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSIdentifierNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * JSHyperlink
 */
public class JSSearchStringHyperlink extends JSAbstractHyperlink
{
	private String searchString;

	/**
	 * JSHyperlink
	 * 
	 * @param hyperlinkRegion
	 * @param typeLabel
	 * @param hyperlinkText
	 * @param targetFilePath
	 * @param searchString
	 */
	public JSSearchStringHyperlink(IRegion hyperlinkRegion, String typeLabel, String hyperlinkText,
			String targetFilePath, String searchString)
	{
		super(hyperlinkRegion, typeLabel, hyperlinkText, targetFilePath);

		this.searchString = searchString;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((searchString == null) ? 0 : searchString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		JSSearchStringHyperlink other = (JSSearchStringHyperlink) obj;
		if (searchString == null)
		{
			if (other.searchString != null)
			{
				return false;
			}
		}
		else if (!searchString.equals(other.searchString))
		{
			return false;
		}
		return true;
	}

	/**
	 * getSearchString
	 * 
	 * @return
	 */
	public String getSearchString()
	{
		return searchString;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#open()
	 */
	public void open()
	{
		AbstractThemeableEditor editor = getEditor();

		if (editor != null)
		{
			// grab AST, making sure the file has been parsed
			IParseNode ast = editor.getAST();

			// assume no target identifier
			JSIdentifierNode targetIdentifier = null;

			// try to locate the target identifier in the AST
			if (ast instanceof JSParseRootNode)
			{
				// collect all identifiers that match the search string
				JSIdentifierCollector collector = new JSIdentifierCollector(searchString);
				((JSParseRootNode) ast).accept(collector);
				List<JSIdentifierNode> identifiers = collector.getIdentifiers();

				// try to refine the selection based on link type
				if (INVOCATION_TYPE.equals(getTypeLabel()))
				{
					for (JSIdentifierNode identifier : identifiers)
					{
						if (identifier.getParent().getNodeType() == IJSNodeTypes.FUNCTION)
						{
							targetIdentifier = identifier;
							break;
						}
					}
				}

				// assume first item in list, as a fallback. This is only slightly better than performing a plain-text
				// search
				if (targetIdentifier == null && !CollectionsUtil.isEmpty(identifiers))
				{
					targetIdentifier = identifiers.get(0);
				}
			}

			if (targetIdentifier != null)
			{
				// show the identifier we ended up with
				editor.selectAndReveal(targetIdentifier.getStart(), targetIdentifier.getLength());
			}
			else
			{
				IFindReplaceTarget target = (IFindReplaceTarget) editor.getAdapter(IFindReplaceTarget.class);

				if (target != null && target.canPerformFind())
				{
					// do a standard search (forward from 0 offset, case sensitive, whole word)
					target.findAndSelect(0, searchString, true, true, true);
				}
				else
				{
					// as a last resort, show the editor. Not sure this will ever happen, but here just in case
					editor.selectAndReveal(0, 0);
				}
			}
		}
	}
}
