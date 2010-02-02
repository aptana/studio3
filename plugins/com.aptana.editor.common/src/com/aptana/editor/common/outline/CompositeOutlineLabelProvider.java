package com.aptana.editor.common.outline;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.parsing.ast.ILanguageNode;

public class CompositeOutlineLabelProvider extends LabelProvider
{

	private Map<String, ILabelProvider> fProvidersByLanguage;

	public CompositeOutlineLabelProvider()
	{
		fProvidersByLanguage = new HashMap<String, ILabelProvider>();
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof ILanguageNode)
		{
			String language = ((ILanguageNode) element).getLanguage();
			ILabelProvider provider = fProvidersByLanguage.get(language);
			if (provider != null)
			{
				return provider.getImage(element);
			}
		}
		return getDefaultImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof ILanguageNode)
		{
			String language = ((ILanguageNode) element).getLanguage();
			ILabelProvider provider = fProvidersByLanguage.get(language);
			if (provider != null)
			{
				return provider.getText(element);
			}
		}
		return getDefaultText(element);
	}

	protected void addSubLanguage(String language, ILabelProvider provider)
	{
		fProvidersByLanguage.put(language, provider);
	}

	protected Image getDefaultImage(Object element)
	{
		return super.getImage(element);
	}

	protected String getDefaultText(Object element)
	{
		return super.getText(element);
	}
}
