/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.outline;

import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.ast.ILanguageNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class CompositeOutlineContentProvider extends CommonOutlineContentProvider
{

	private Map<String, CommonOutlineContentProvider> fProvidersByLanguage;

	public CompositeOutlineContentProvider()
	{
		fProvidersByLanguage = new HashMap<String, CommonOutlineContentProvider>();
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ILanguageNode)
		{
			String language = ((ILanguageNode) parentElement).getLanguage();
			CommonOutlineContentProvider provider = getContentProviderForLanguage(language);
			if (provider != null)
			{
				return provider.getChildren(parentElement);
			}
		}
		return getDefaultChildren(parentElement);
	}

	@Override
	public CommonOutlineItem getOutlineItem(IParseNode node)
	{
		if (node instanceof ILanguageNode)
		{
			String language = ((ILanguageNode) node).getLanguage();
			CommonOutlineContentProvider provider = getContentProviderForLanguage(language);
			if (provider != null)
			{
				return provider.getOutlineItem(node);
			}
		}
		return super.getOutlineItem(node);
	}

	protected void addSubLanguage(String language, CommonOutlineContentProvider provider)
	{
		fProvidersByLanguage.put(language, provider);
	}

	private Object[] getDefaultChildren(Object parent)
	{
		Object[] children = super.getChildren(parent);
		if (children.length == 1 && children[0] instanceof ParseRootNode)
		{
			// skips the root node
			return getChildren(children[0]);
		}
		return children;
	}

	private CommonOutlineContentProvider getContentProviderForLanguage(String language)
	{
		return fProvidersByLanguage.get(language);
	}
}
