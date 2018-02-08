/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.IFileStore;

import com.aptana.index.core.filter.IndexFilterManager;

public class ExcludeHandler extends BaseHandler
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
			manager.addFilterItem(fileStore);
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
		return IndexFilterManager.getInstance().isFilteredItem(fileStore) == false;
	}
}
