/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.js.debug.ui.internal.sourcelookup;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.debug.ui.SourceDisplayUtil;
import com.aptana.js.debug.core.model.IJSScriptElement;
import com.aptana.js.debug.core.model.ISourceLink;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class SourceDisplayAdapter implements ISourceDisplay
{

	private static final String DEFAULT_SOURCE_LOCATOR_ID = "com.aptana.js.debug.core.sourceLookupDirector"; //$NON-NLS-1$
	private static final String DEFAULT_SOURCE_PATH_COMPUTER_ID = "com.aptana.debug.core.sourcePathComputer"; //$NON-NLS-1$

	private static final ISourceContainer[] EMPTY_CONTAINERS = new ISourceContainer[0];

	private static SourceDisplayAdapter fInstance;

	private SourceLookupJob fSourceLookupJob = new SourceLookupJob();
	private SourceDisplayJob fSourceDisplayJob = new SourceDisplayJob();
	private ISourceLocator fDefaultSourceLocator;

	private SourceDisplayAdapter()
	{
	}

	public static synchronized SourceDisplayAdapter getInstance()
	{
		if (fInstance == null)
		{
			fInstance = new SourceDisplayAdapter();
		}
		return fInstance;
	}

	/*
	 * @see org.eclipse.debug.ui.sourcelookup.ISourceDisplay#displaySource(java.lang.Object,
	 * org.eclipse.ui.IWorkbenchPage, boolean)
	 */
	public void displaySource(Object element, IWorkbenchPage page, boolean forceSourceLookup)
	{
		fSourceLookupJob.setLookupInfo(element,
				element instanceof IDebugElement ? getSourceLocator((IDebugElement) element) : null, page);
		fSourceLookupJob.schedule();
	}

	private ISourceLocator getSourceLocator(IDebugElement debugElement)
	{
		ISourceLocator sourceLocator = null;
		if (debugElement.getDebugTarget() == null)
		{
			if (debugElement instanceof ISourceLink)
			{
				sourceLocator = getDefaultSourceLocator();
			}
			return sourceLocator;
		}
		ILaunch launch = debugElement.getLaunch();
		if (launch != null)
		{
			sourceLocator = launch.getSourceLocator();
		}
		return sourceLocator;
	}

	private ISourceLocator getDefaultSourceLocator()
	{
		if (fDefaultSourceLocator == null)
		{
			try
			{
				fDefaultSourceLocator = DebugPlugin.getDefault().getLaunchManager()
						.newSourceLocator(DEFAULT_SOURCE_LOCATOR_ID);
				if (fDefaultSourceLocator instanceof ISourceLookupDirector)
				{
					ISourceLookupDirector sourceLookupDirector = (ISourceLookupDirector) fDefaultSourceLocator;
					sourceLookupDirector.initializeParticipants();
					sourceLookupDirector.setSourceContainers(new ISourceContainer[] { new DefaultSourceContainer()
					{

						/*
						 * (non-Javadoc)
						 * @see org.eclipse.debug.core.sourcelookup.containers.
						 * DefaultSourceContainer#createSourceContainers()
						 */
						@Override
						protected ISourceContainer[] createSourceContainers() throws CoreException
						{
							ISourcePathComputer sourcePathComputer = null;
							ISourceLookupDirector director = getDirector();
							if (director != null)
							{
								sourcePathComputer = director.getSourcePathComputer();
							}
							if (sourcePathComputer != null)
							{
								return sourcePathComputer.computeSourceContainers(null, null);
							}

							return EMPTY_CONTAINERS;
						}

					} });
					ISourcePathComputer sourcePathComputer = DebugPlugin.getDefault().getLaunchManager()
							.getSourcePathComputer(DEFAULT_SOURCE_PATH_COMPUTER_ID);
					sourceLookupDirector.setSourcePathComputer(sourcePathComputer);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
			}
		}
		return fDefaultSourceLocator;
	}

	private class SourceLookupJob extends Job
	{

		private Object fTarget;
		private ISourceLocator fLocator;
		private IWorkbenchPage fPage;

		/**
		 * Constructs a new source lookup job.
		 */
		protected SourceLookupJob()
		{
			super("Debug Source Lookup"); //$NON-NLS-1$
			setPriority(Job.INTERACTIVE);
			EclipseUtil.setSystemForJob(this);
		}

		public void setLookupInfo(Object target, ISourceLocator locator, IWorkbenchPage page)
		{
			fTarget = target;
			fLocator = locator;
			fPage = page;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime. IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor)
		{
			if (!monitor.isCanceled())
			{
				Object lookupObject = fTarget;
				ISourceLocator lookupLocator = fLocator;

				if (lookupObject != null && lookupLocator != null
						&& (!(lookupObject instanceof IStackFrame) || !((IStackFrame) lookupObject).isTerminated()))
				{
					ISourceLookupResult result = null;
					result = DebugUITools.lookupSource(lookupObject, lookupLocator);
					if (!monitor.isCanceled() && fPage != null
							&& (!(lookupObject instanceof IStackFrame) || !((IStackFrame) lookupObject).isTerminated()))
					{
						fSourceDisplayJob.setDisplayInfo(result, fPage);
						fSourceDisplayJob.schedule();
					}
				}
				setLookupInfo(null, null, null);
			}
			return Status.OK_STATUS;
		}

	}

	private static class SourceDisplayJob extends UIJob
	{

		private ISourceLookupResult fResult;
		private IWorkbenchPage fPage;

		protected SourceDisplayJob()
		{
			super("Debug Source Display"); //$NON-NLS-1$
			EclipseUtil.setSystemForJob(this);
			setPriority(Job.INTERACTIVE);
		}

		/**
		 * Constructs a new source display job
		 */
		public synchronized void setDisplayInfo(ISourceLookupResult result, IWorkbenchPage page)
		{
			fResult = result;
			fPage = page;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime .IProgressMonitor)
		 */
		public IStatus runInUIThread(IProgressMonitor monitor)
		{
			ISourceLookupResult result = null;
			IWorkbenchPage page = null;
			synchronized (this)
			{
				result = fResult;
				page = fPage;
				setDisplayInfo(null, null);
			}
			if (!monitor.isCanceled() && result != null && page != null)
			{
				if (result.getEditorInput() == null)
				{
					MessageDialog.openError(page.getWorkbenchWindow().getShell(),
							Messages.getString("SourceDisplayAdapter.Error_Opening_Source"), //$NON-NLS-1$
							MessageFormat.format(Messages.getString("SourceDisplayAdapter.Source_Not_Located"), //$NON-NLS-1$
									JSDebugUIPlugin.getDefault().getModelPresentation().getText(result.getArtifact())));
					return Status.CANCEL_STATUS;
				}
				// Workaround for http://support.aptana.com/asap/browse/STU-3818
				if (result.getArtifact() instanceof ISourceLink)
				{
					boolean oldReuseValue = DebugUIPlugin.getDefault().getPreferenceStore()
							.getBoolean(IDebugUIConstants.PREF_REUSE_EDITOR);
					DebugUIPlugin.getDefault().getPreferenceStore()
							.setValue(IDebugUIConstants.PREF_REUSE_EDITOR, false);
					DebugUITools.displaySource(result, page);
					DebugUIPlugin.getDefault().getPreferenceStore()
							.setValue(IDebugUIConstants.PREF_REUSE_EDITOR, oldReuseValue);
				}
				else
				{
					DebugUITools.displaySource(result, page);
				}
				if (result.getArtifact() instanceof IJSScriptElement)
				{
					int lineNumber = ((IJSScriptElement) result.getArtifact()).getBaseLine();
					IEditorInput editorInput = result.getEditorInput();
					if (editorInput != null && lineNumber > 0)
					{
						IEditorPart editorPart = SourceDisplayUtil.findEditor(editorInput);
						if (editorPart != null)
						{
							SourceDisplayUtil.revealLineInEditor(editorPart, lineNumber);
						}
					}
				}
			}

			return Status.OK_STATUS;
		}

	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory
	{

		/*
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType)
		{
			if (ISourceDisplay.class.equals(adapterType))
			{
				return SourceDisplayAdapter.getInstance();
			}
			return null;
		}

		/*
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList()
		{
			return new Class[] { ISourceDisplay.class };
		}

	}

}
