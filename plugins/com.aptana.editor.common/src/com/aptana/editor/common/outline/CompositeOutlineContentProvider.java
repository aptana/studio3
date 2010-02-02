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
