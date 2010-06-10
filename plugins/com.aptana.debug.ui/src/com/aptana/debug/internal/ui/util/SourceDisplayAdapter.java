/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.internal.ui.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.progress.UIJob;

import com.aptana.debug.core.model.IJSScriptElement;
import com.aptana.debug.core.model.ISourceLink;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
public class SourceDisplayAdapter implements InvocationHandler {

	private static final String DEFAULT_SOURCE_LOCATOR_ID = "com.aptana.debug.core.sourceLookupDirector"; //$NON-NLS-1$
	private static final String DEFAULT_SOURCE_PATH_COMPUTER_ID = "com.aptana.debug.core.sourcePathComputer"; //$NON-NLS-1$

	private static Class clazz;
	
	static {
		try {
			clazz = Class.forName("org.eclipse.debug.ui.sourcelookup.ISourceDisplay"); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			try {
				clazz = Class.forName("org.eclipse.debug.internal.ui.contexts.provisional.ISourceDisplayAdapter"); //$NON-NLS-1$
			} catch (ClassNotFoundException e1) {
			}
		}
	}

	private static Object fInstance;
	
	private SourceLookupJob fSourceLookupJob = new SourceLookupJob();
	private SourceDisplayJob fSourceDisplayJob = new SourceDisplayJob();
	private ISourceLocator fDefaultSourceLocator;

	private SourceDisplayAdapter() {
	}
	
	public static synchronized Object getInstance() {
		if (fInstance == null) {
			/* This reflection code is to fix compatibility between Eclipse 3.2 and 3.3 API */
			fInstance = Proxy.newProxyInstance(SourceDisplayAdapter.class.getClassLoader(),
						new Class[] { clazz }, new SourceDisplayAdapter());
		}
		return fInstance;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		method = this.getClass().getMethod(method.getName(), method.getParameterTypes());
		return method.invoke(this, args);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.sourcelookup.ISourceDisplay#displaySource(java.lang.Object, org.eclipse.ui.IWorkbenchPage, boolean)
	 */
	public void displaySource(Object element, IWorkbenchPage page, boolean forceSourceLookup) {
		fSourceLookupJob.setLookupInfo(element,
				element instanceof IDebugElement ? getSourceLocator((IDebugElement) element) : null,
				page);
		fSourceLookupJob.schedule();
	}

	private ISourceLocator getSourceLocator(IDebugElement debugElement)
	{
		ISourceLocator sourceLocator = null;
		if (debugElement.getDebugTarget() == null) {
			if (debugElement instanceof ISourceLink) {
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
	
	private ISourceLocator getDefaultSourceLocator() {
		if (fDefaultSourceLocator == null) {
			try {
				fDefaultSourceLocator = DebugPlugin.getDefault().getLaunchManager().newSourceLocator(DEFAULT_SOURCE_LOCATOR_ID);
				if (fDefaultSourceLocator instanceof ISourceLookupDirector) {
					ISourceLookupDirector sourceLookupDirector = (ISourceLookupDirector) fDefaultSourceLocator;
					sourceLookupDirector.initializeParticipants();
					sourceLookupDirector.setSourceContainers(new ISourceContainer[]{
							new DefaultSourceContainer() {

								/* (non-Javadoc)
								 * @see org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer#createSourceContainers()
								 */
								@Override
								protected ISourceContainer[] createSourceContainers() throws CoreException {
									ISourcePathComputer sourcePathComputer = null;
									ISourceLookupDirector director = getDirector();
									if (director != null) {
										sourcePathComputer = director.getSourcePathComputer();
									}
									if (sourcePathComputer != null) {
										return sourcePathComputer.computeSourceContainers(null, null);
									}
									
									return new ISourceContainer[0];
								}
								
							}
						});
					ISourcePathComputer sourcePathComputer = DebugPlugin.getDefault().getLaunchManager().getSourcePathComputer(DEFAULT_SOURCE_PATH_COMPUTER_ID);
					sourceLookupDirector.setSourcePathComputer(sourcePathComputer);
				}
			} catch (CoreException e) {
				DebugUiPlugin.log(e);
			}
		}
		return fDefaultSourceLocator;
	}

	private class SourceLookupJob extends Job {
		
		private Object fTarget;
		private ISourceLocator fLocator;
		private IWorkbenchPage fPage;

		/**
		 * Constructs a new source lookup job.
		 */
		public SourceLookupJob() {
			super("Debug Source Lookup");  //$NON-NLS-1$
			setPriority(Job.INTERACTIVE);
			setSystem(true);	
		}
		
		public void setLookupInfo(Object target, ISourceLocator locator, IWorkbenchPage page) {
			fTarget = target;
			fLocator = locator;
			fPage = page;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			if (!monitor.isCanceled()) {
				Object lookupObject = fTarget;
				ISourceLocator lookupLocator = fLocator;
				
				if (lookupObject != null && lookupLocator != null && (!(lookupObject instanceof IStackFrame) || !((IStackFrame)lookupObject).isTerminated())) {
					ISourceLookupResult result = null;
					result = DebugUITools.lookupSource(lookupObject, lookupLocator);
					if (!monitor.isCanceled() && fPage != null && (!(lookupObject instanceof IStackFrame) || !((IStackFrame)lookupObject).isTerminated())) {
						fSourceDisplayJob.setDisplayInfo(result, fPage);
						fSourceDisplayJob.schedule();
					}
				}
				setLookupInfo(null, null, null);
			}
			return Status.OK_STATUS;
		}
		
	}

	private class SourceDisplayJob extends UIJob {
		
		private ISourceLookupResult fResult;
		private IWorkbenchPage fPage;

		public SourceDisplayJob() {
			super("Debug Source Display");  //$NON-NLS-1$
			setSystem(true);
			setPriority(Job.INTERACTIVE);
		}
		
		/**
		 * Constructs a new source display job
		 */
		public synchronized void setDisplayInfo(ISourceLookupResult result, IWorkbenchPage page) {
			fResult = result;
			fPage = page;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus runInUIThread(IProgressMonitor monitor) {
			ISourceLookupResult result = null;
			IWorkbenchPage page = null;
			synchronized (this) {
				result = fResult;
				page = fPage;
				setDisplayInfo(null, null);
			}
			if (!monitor.isCanceled() && result != null && page != null) {
				if (result.getEditorInput() == null) {
					MessageDialog.openError(page.getWorkbenchWindow().getShell(),
							Messages.getString("SourceDisplayAdapter.Error_Opening_Source"), //$NON-NLS-1$
							MessageFormat.format(Messages.getString("SourceDisplayAdapter.Source_Not_Located"), //$NON-NLS-1$
									DebugUiPlugin.getDefault().getModelPresentation().getText(result.getArtifact())));
					return Status.CANCEL_STATUS;
				}
				// Workaround for http://support.aptana.com/asap/browse/STU-3818
				if (result.getArtifact() instanceof ISourceLink) {
					boolean oldReuseValue = DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IDebugUIConstants.PREF_REUSE_EDITOR);
					DebugUIPlugin.getDefault().getPreferenceStore().setValue(IDebugUIConstants.PREF_REUSE_EDITOR, false);
					DebugUITools.displaySource(result, page);
					DebugUIPlugin.getDefault().getPreferenceStore().setValue(IDebugUIConstants.PREF_REUSE_EDITOR, oldReuseValue);
				} else {
					DebugUITools.displaySource(result, page);
				}
				if (result.getArtifact() instanceof IJSScriptElement) {
					int lineNumber = ((IJSScriptElement)result.getArtifact()).getBaseLine();
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
	
	public static class Factory implements IAdapterFactory {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (adapterType == clazz) {
				return SourceDisplayAdapter.getInstance();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { clazz };
		}
		
	}

}
