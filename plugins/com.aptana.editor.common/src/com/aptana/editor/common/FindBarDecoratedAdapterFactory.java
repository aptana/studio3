package com.aptana.editor.common;

import org.eclipse.core.runtime.IAdapterFactory;

import com.aptana.editor.findbar.api.IFindBarDecorated;

@SuppressWarnings("unchecked")
public class FindBarDecoratedAdapterFactory implements IAdapterFactory
{

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType == IFindBarDecorated.class)
		{
			if (adaptableObject instanceof AbstractThemeableEditor)
			{
				return ((AbstractThemeableEditor) adaptableObject).getFindBarDecorated();
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList()
	{
		return  new Class[] {IFindBarDecorated.class };
	}

}
