/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.js.debug.core.model.IJSVariable;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.js.debug.ui.internal.IJSDebugUIConstants;

/**
 * @author Max Stepanov
 */
public class ShowConstantsActionDelegate extends ViewerFilter implements IViewActionDelegate, IActionDelegate2
{
	private IViewPart fView;
	private IAction fAction;

	/*
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
		fView = view;
		StructuredViewer viewer = getStructuredViewer();
		ViewerFilter[] filters = viewer.getFilters();
		ViewerFilter filter = null;
		for (ViewerFilter f : filters)
		{
			if (this.equals(f))
			{
				filter = f;
				break;
			}
		}
		if (filter == null)
		{
			viewer.addFilter(this);
		}
		viewer.refresh();
		fAction.setChecked(getPreferenceValue(view));
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public void dispose()
	{
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action)
	{
		fAction = action;
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction,
	 * org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event)
	{
		run(action);
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		IEclipsePreferences preferences = getPreferences();
		String key = fView.getSite().getId() + "." + getPreferenceKey(); //$NON-NLS-1$
		preferences.putBoolean(key, action.isChecked());
		try
		{
			preferences.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		getStructuredViewer().refresh();
	}

	/*
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	/*
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 * java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof IJSVariable)
		{
			try
			{
				if (!getValue())
				{
					return !((IJSVariable) element).isConst();
				}
			}
			catch (DebugException e)
			{
				IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
			}
		}
		return true;
	}

	protected IEclipsePreferences getPreferences()
	{
		return InstanceScope.INSTANCE.getNode(JSDebugUIPlugin.PLUGIN_ID);
	}

	/*
	 * getPreferenceValue
	 * @param part
	 * @return boolean
	 */
	protected boolean getPreferenceValue(IViewPart part)
	{
		String baseKey = getPreferenceKey();
		String viewKey = part.getSite().getId();
		String compositeKey = viewKey + "." + baseKey; //$NON-NLS-1$
		IEclipsePreferences preferences = getPreferences();
		return preferences.getBoolean(compositeKey, preferences.getBoolean(baseKey, false));
	}

	/*
	 * getStructuredViewer
	 * @return StructuredViewer
	 */
	protected StructuredViewer getStructuredViewer()
	{
		IDebugView view = (IDebugView) fView.getAdapter(IDebugView.class);
		if (view != null)
		{
			Viewer viewer = view.getViewer();
			if (viewer instanceof StructuredViewer)
			{
				return (StructuredViewer) viewer;
			}
		}
		return null;
	}

	/*
	 * Returns whether this action is seleted/checked.
	 * @return whether this action is seleted/checked
	 */
	protected boolean getValue()
	{
		return fAction.isChecked();
	}

	/*
	 * getPreferenceKey
	 * @return String
	 */
	protected String getPreferenceKey()
	{
		return IJSDebugUIConstants.PREF_SHOW_CONSTANTS;
	}
}
