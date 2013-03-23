/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.views;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.ui.views.IActionProvider;
import com.aptana.index.core.ui.views.IndexView;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.inferencing.JSTypeUtil;
import com.aptana.js.core.model.ClassElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;

/**
 * JSIndexViewActionProvider
 */
public class JSIndexViewActionProvider implements IActionProvider
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.ui.views.IActionProvider#getActions(java.lang.Object)
	 */
	public IAction[] getActions(final IndexView view, Object object)
	{
		if (object instanceof PropertyElement)
		{
			final List<String> typeNames = ((PropertyElement) object).getTypeNames();

			if (!CollectionsUtil.isEmpty(typeNames))
			{
				return new IAction[] { createAction(view, typeNames) };
			}
		}

		return null;
	}

	/**
	 * @param view
	 * @param typeNames
	 * @return
	 */
	protected IAction createAction(final IndexView view, final List<String> typeNames)
	{
		IAction action = new Action()
		{
			@Override
			public void run()
			{
				TreeViewer treeViewer = view.getTreeViewer();

				if (treeViewer != null)
				{
					Object input = treeViewer.getInput();

					if (input instanceof IProject)
					{
						IProject project = (IProject) input;

						JSIndexQueryHelper queryHelper = new JSIndexQueryHelper(project);
						Collection<TypeElement> types = queryHelper.getTypes(typeNames.get(0), true);
						List<ClassElement> classes = JSTypeUtil.typesToClasses(types);

						if (!CollectionsUtil.isEmpty(classes))
						{
							ClassElement c = classes.get(0);

							treeViewer.setSelection(new StructuredSelection(c), true);
						}
					}
				}
			}
		};

		action.setText(Messages.JSIndexViewActionProvider_JumpToType);

		return action;
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}
}
