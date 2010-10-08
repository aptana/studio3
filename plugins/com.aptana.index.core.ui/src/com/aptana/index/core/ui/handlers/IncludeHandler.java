package com.aptana.index.core.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.IFileStore;

import com.aptana.index.core.ui.IndexFilterManager;

public class IncludeHandler extends BaseHandler
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IndexFilterManager manager = IndexFilterManager.getInstance();

		for (IFileStore fileStore : this.getFileStores())
		{
			manager.removeFilterItem(fileStore);
		}

		manager.commitFilteredItems();

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.ui.handlers.BaseHandler#isValid(org.eclipse.core.filesystem.IFileStore)
	 */
	@Override
	protected boolean isValid(IFileStore fileStore)
	{
		return IndexFilterManager.getInstance().isFilteredItem(fileStore);
	}
}
