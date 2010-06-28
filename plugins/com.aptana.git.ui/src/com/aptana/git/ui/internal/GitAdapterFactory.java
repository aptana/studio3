package com.aptana.git.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.team.ui.history.IHistoryPageSource;

import com.aptana.git.ui.internal.history.GitHistoryPageSource;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GitAdapterFactory implements IAdapterFactory
{

	private Object historyPageSource = new GitHistoryPageSource();

	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType.isAssignableFrom(IHistoryPageSource.class))
		{
			return historyPageSource;
		}
		return null;
	}

	public Class[] getAdapterList()
	{
		return new Class[] { IHistoryPageSource.class };
	}

}
