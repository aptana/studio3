package com.aptana.editor.common;

import org.eclipse.core.runtime.IAdapterFactory;

import com.aptana.scripting.keybindings.ICommandElementsProvider;

@SuppressWarnings("rawtypes")
public class CommandElementsProviderAdapterFactory implements IAdapterFactory
{
	private static final Class[] ADAPTERS = new Class[] {ICommandElementsProvider.class};

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType == ICommandElementsProvider.class)
		{
			if (adaptableObject instanceof AbstractThemeableEditor)
			{
				return ((AbstractThemeableEditor) adaptableObject).getCommandElementsProvider();
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList()
	{
		return ADAPTERS;
	}

}
