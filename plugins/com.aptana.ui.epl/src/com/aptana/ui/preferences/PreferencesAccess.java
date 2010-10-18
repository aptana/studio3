/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences;

import org.osgi.service.prefs.BackingStoreException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;

import org.eclipse.ui.preferences.IWorkingCopyManager;

/**
 *
 */
public class PreferencesAccess
{

	public static PreferencesAccess getOriginalPreferences()
	{
		return new PreferencesAccess();
	}

	public static PreferencesAccess getWorkingCopyPreferences(IWorkingCopyManager workingCopyManager)
	{
		return new WorkingCopyPreferencesAccess(workingCopyManager);
	}

	private PreferencesAccess()
	{
		// can only extends in this file
	}

	public IScopeContext getDefaultScope()
	{
		return new DefaultScope();
	}

	public IScopeContext getInstanceScope()
	{
		return new InstanceScope();
	}

	public IScopeContext getProjectScope(IProject project)
	{
		return new ProjectScope(project);
	}

	/**
	 * Applies the changes
	 * 
	 * @throws BackingStoreException
	 *             thrown when the changes could not be applied
	 */
	public void applyChanges() throws BackingStoreException
	{
	}

	private static class WorkingCopyPreferencesAccess extends PreferencesAccess
	{

		private final IWorkingCopyManager fWorkingCopyManager;

		private WorkingCopyPreferencesAccess(IWorkingCopyManager workingCopyManager)
		{
			fWorkingCopyManager = workingCopyManager;
		}

		private final IScopeContext getWorkingCopyScopeContext(IScopeContext original)
		{
			return new WorkingCopyScopeContext(fWorkingCopyManager, original);
		}

		public IScopeContext getDefaultScope()
		{
			return getWorkingCopyScopeContext(super.getDefaultScope());
		}

		public IScopeContext getInstanceScope()
		{
			return getWorkingCopyScopeContext(super.getInstanceScope());
		}

		public IScopeContext getProjectScope(IProject project)
		{
			return getWorkingCopyScopeContext(super.getProjectScope(project));
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.preferences.PreferencesAccess#applyChanges()
		 */
		public void applyChanges() throws BackingStoreException
		{
			fWorkingCopyManager.applyChanges();
		}
	}

	private static class WorkingCopyScopeContext implements IScopeContext
	{

		private final IWorkingCopyManager fWorkingCopyManager;
		private final IScopeContext fOriginal;

		public WorkingCopyScopeContext(IWorkingCopyManager workingCopyManager, IScopeContext original)
		{
			fWorkingCopyManager = workingCopyManager;
			fOriginal = original;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.preferences.IScopeContext#getName()
		 */
		public String getName()
		{
			return fOriginal.getName();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.preferences.IScopeContext#getNode(java.lang.String)
		 */
		public IEclipsePreferences getNode(String qualifier)
		{
			return fWorkingCopyManager.getWorkingCopy(fOriginal.getNode(qualifier));
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.preferences.IScopeContext#getLocation()
		 */
		public IPath getLocation()
		{
			return fOriginal.getLocation();
		}
	}

}
