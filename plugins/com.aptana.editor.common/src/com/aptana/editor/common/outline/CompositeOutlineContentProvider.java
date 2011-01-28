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

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.aptana.parsing.ast.ILanguageNode;
import com.aptana.parsing.ast.ParseRootNode;

public class CompositeOutlineContentProvider extends CommonOutlineContentProvider
{

	private Map<String, ITreeContentProvider> fProvidersByLanguage;

	public CompositeOutlineContentProvider()
	{
		fProvidersByLanguage = new HashMap<String, ITreeContentProvider>();
	}

	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ILanguageNode)
		{
			String language = ((ILanguageNode) parentElement).getLanguage();
			ITreeContentProvider provider = fProvidersByLanguage.get(language);
			if (provider != null)
			{
				return provider.getChildren(parentElement);
			}
		}
		return getDefaultChildren(parentElement);
	}

	protected void addSubLanguage(String language, ITreeContentProvider provider)
	{
		fProvidersByLanguage.put(language, provider);
	}

	protected Object[] getDefaultChildren(Object parent)
	{
		Object[] children = super.getChildren(parent);
		if (children.length == 1 && children[0] instanceof ParseRootNode)
		{
			// skips the root node
			return getChildren(children[0]);
		}
		return children;
	}
}
