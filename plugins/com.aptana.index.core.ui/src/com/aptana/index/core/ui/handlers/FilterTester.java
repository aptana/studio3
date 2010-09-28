package com.aptana.index.core.ui.handlers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;

import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.index.core.ui.IndexFilterManager;

public class FilterTester extends PropertyTester
{
	private static final String IS_FILTERED = "isFiltered"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		boolean result = false;

		if (receiver instanceof IResource)
		{
			IResource resource = (IResource) receiver;

			if (IS_FILTERED.equals(property))
			{
				IFileStore fileStore = EFSUtils.getFileStore(resource);
				boolean expectedResult = toBoolean(expectedValue);

				result = (IndexFilterManager.getInstance().isFilteredItem(fileStore) == expectedResult);
			}
		}

		return result;
	}

	/**
	 * toBoolean
	 * 
	 * @param value
	 * @return
	 */
	private static boolean toBoolean(Object value)
	{
		if (value instanceof Boolean)
		{
			return ((Boolean) value).booleanValue();
		}
		if (value instanceof String)
		{
			return Boolean.parseBoolean((String) value);
		}
		return false;
	}
}
