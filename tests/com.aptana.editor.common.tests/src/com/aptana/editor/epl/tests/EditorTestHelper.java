/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.editor.epl.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IWidgetTokenKeeper;
import org.eclipse.jface.text.IWidgetTokenOwner;
import org.eclipse.jface.text.reconciler.AbstractReconciler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.text.reconciler.CommonReconciler;

/**
 * @since 3.1
 */

@SuppressWarnings("deprecation")
public class EditorTestHelper
{

	public static final String TEXT_EDITOR_ID = "org.eclipse.ui.DefaultTextEditor";
	public static final String RESOURCE_PERSPECTIVE_ID = "org.eclipse.ui.resourcePerspective";
	public static final String WEB_PERSPECTIVE_ID = "com.aptana.ui.WebPerspective";
	public static final String OUTLINE_VIEW_ID = "org.eclipse.ui.views.ContentOutline";
	public static final String NAVIGATOR_VIEW_ID = "org.eclipse.ui.views.ResourceNavigator";
	public static final String INTRO_VIEW_ID = "org.eclipse.ui.internal.introview";

	public static IEditorPart openInEditor(IFile file, boolean runEventLoop) throws PartInitException
	{
		IEditorPart part = IDE.openEditor(getActivePage(), file);
		if (runEventLoop)
			runEventQueue(part);
		return part;
	}

	public static IEditorPart openInEditor(IFile file, String editorId, boolean runEventLoop) throws PartInitException
	{
		IEditorPart part = IDE.openEditor(getActivePage(), file, editorId);
		if (runEventLoop)
		{
			runEventQueue(part);
		}
		return part;
	}

	public static IDocument getDocument(ITextEditor editor)
	{
		IDocumentProvider provider = editor.getDocumentProvider();
		IEditorInput input = editor.getEditorInput();
		return provider.getDocument(input);
	}

	public static void revertEditor(ITextEditor editor, boolean runEventQueue)
	{
		editor.doRevertToSaved();
		if (runEventQueue)
			runEventQueue(editor);
	}

	public static void closeEditor(IEditorPart editor)
	{
		if (Display.getCurrent() != null)
		{
			IWorkbenchPartSite site;
			IWorkbenchPage page;
			if (editor != null && (site = editor.getSite()) != null && (page = site.getPage()) != null)
			{
				page.closeEditor(editor, false);
				runEventQueue();
			}
		}
		else
		{
			((AbstractTextEditor) editor).close(false);
		}
	}

	public static void closeAllEditors()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++)
		{
			IWorkbenchPage[] pages = windows[i].getPages();
			for (int j = 0; j < pages.length; j++)
			{
				IEditorReference[] editorReferences = pages[j].getEditorReferences();
				for (int k = 0; k < editorReferences.length; k++)
					closeEditor(editorReferences[k].getEditor(false));
			}
		}
	}

	/**
	 * Runs the event queue on the current display until it is empty.
	 */
	public static void runEventQueue()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null)
			runEventQueue(window.getShell());
	}

	public static void runEventQueue(IWorkbenchPart part)
	{
		runEventQueue(part.getSite().getShell());
	}

	public static void runEventQueue(Shell shell)
	{
		runEventQueue(shell.getDisplay());
	}

	public static void runEventQueue(Display display)
	{
		while (display.readAndDispatch())
		{
			// do nothing
		}
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	public static void forceFocus()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window == null)
		{
			IWorkbenchWindow[] wbWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (wbWindows.length == 0)
				return;
			window = wbWindows[0];
		}
		Shell shell = window.getShell();
		if (shell != null && !shell.isDisposed())
		{
			shell.forceActive();
			shell.forceFocus();
		}
	}

	public static IWorkbenchPage getActivePage()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window != null ? window.getActivePage() : null;
	}

	public static Display getActiveDisplay()
	{
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window != null ? window.getShell().getDisplay() : null;
	}

	public static void sleep(int intervalTime)
	{
		try
		{
			Thread.sleep(intervalTime);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean allJobsQuiet()
	{
		IJobManager jobManager = Job.getJobManager();
		Job[] jobs = jobManager.find(null);
		for (int i = 0; i < jobs.length; i++)
		{
			Job job = jobs[i];
			if (job.getName().equals("Terminal data reader"))
			{ // Skip certain jobs that are always running!
				continue;
			}
			int state = job.getState();
			if (state == Job.RUNNING || state == Job.WAITING)
			{
				Logger.global.finest(job.toString());
				return false;
			}
		}
		return true;
	}

	public static boolean isViewShown(String viewId)
	{
		return getActivePage().findViewReference(viewId) != null;
	}

	public static boolean showView(String viewId, boolean show) throws PartInitException
	{
		IWorkbenchPage activePage = getActivePage();
		IViewReference view = activePage.findViewReference(viewId);
		boolean shown = view != null;
		if (shown != show)
			if (show)
				activePage.showView(viewId);
			else
				activePage.hideView(view);
		return shown;
	}

	public static void bringToTop()
	{
		getActiveWorkbenchWindow().getShell().forceActive();
	}

	public static String showPerspective(String perspective) throws WorkbenchException
	{
		String shownPerspective = getActivePage().getPerspective().getId();
		if (!perspective.equals(shownPerspective))
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
			workbench.showPerspective(perspective, activeWindow);
		}
		return shownPerspective;
	}

	public static void closeAllPopUps(SourceViewer sourceViewer)
	{
		IWidgetTokenKeeper tokenKeeper = new IWidgetTokenKeeper()
		{
			public boolean requestWidgetToken(IWidgetTokenOwner owner)
			{
				return true;
			}
		};
		sourceViewer.requestWidgetToken(tokenKeeper, Integer.MAX_VALUE);
		sourceViewer.releaseWidgetToken(tokenKeeper);
	}

	public static IFile[] findFiles(IResource resource) throws CoreException
	{
		List<IFile> files = new ArrayList<IFile>();
		findFiles(resource, files);
		return files.toArray(new IFile[files.size()]);
	}

	private static void findFiles(IResource resource, List<IFile> files) throws CoreException
	{
		if (resource instanceof IFile)
		{
			files.add((IFile) resource);
			return;
		}
		if (resource instanceof IContainer)
		{
			IResource[] resources = ((IContainer) resource).members();
			for (int i = 0; i < resources.length; i++)
				findFiles(resources[i], files);
		}
	}

	public static SourceViewer getSourceViewer(AbstractTextEditor editor)
	{
		SourceViewer sourceViewer = (SourceViewer) new Accessor(editor, AbstractTextEditor.class).invoke(
				"getSourceViewer", new Object[0]);
		return sourceViewer;
	}

	/**
	 * Runs the event queue on the current display and lets it sleep until the timeout elapses.
	 * 
	 * @param millis
	 *            the timeout in milliseconds
	 */
	public static void runEventQueue(long millis)
	{
		runEventQueue(getActiveDisplay(), millis);
	}

	public static void runEventQueue(IWorkbenchPart part, long millis)
	{
		runEventQueue(part.getSite().getShell(), millis);
	}

	public static void runEventQueue(Shell shell, long millis)
	{
		runEventQueue(shell.getDisplay(), millis);
	}

	public static void runEventQueue(Display display, long minTime)
	{
		if (display != null)
		{
			DisplayHelper.sleep(display, minTime);
		}
		else
		{
			sleep((int) minTime);
		}
	}

	public static boolean joinReconciler(SourceViewer sourceViewer, long minTime, long maxTime, long intervalTime)
	{
		Logger.global.entering("EditorTestHelper", "joinReconciler");
		runEventQueue(minTime);

		AbstractReconciler reconciler = getReconciler(sourceViewer);
		if (reconciler == null)
		{
			return true;
		}
		final Accessor backgroundThreadAccessor = getBackgroundThreadAccessor(reconciler);
		final Accessor commonReconcilerAccessor;
		if (reconciler instanceof CommonReconciler)
		{
			commonReconcilerAccessor = new Accessor(reconciler, CommonReconciler.class);
		}
		else
		{
			commonReconcilerAccessor = null;
		}

		DisplayHelper helper = new DisplayHelper()
		{
			public boolean condition()
			{
				return !isRunning(commonReconcilerAccessor, backgroundThreadAccessor);
			}
		};
		boolean finished = helper.waitForCondition(getActiveDisplay(), maxTime > 0 ? maxTime : Long.MAX_VALUE,
				intervalTime);
		Logger.global.exiting("EditorTestHelper", "joinReconciler", new Boolean(finished));
		return finished;
	}

	public static AbstractReconciler getReconciler(SourceViewer sourceViewer)
	{
		return (AbstractReconciler) new Accessor(sourceViewer, SourceViewer.class).get("fReconciler");
	}

	private static Accessor getBackgroundThreadAccessor(AbstractReconciler reconciler)
	{
		Object backgroundThread = new Accessor(reconciler, AbstractReconciler.class).get("fThread");
		return new Accessor(backgroundThread, backgroundThread.getClass());
	}

	private static boolean isRunning(Accessor commonReconcilerAccessor, Accessor backgroundThreadAccessor)
	{
		return (commonReconcilerAccessor != null ? !isInitialProcessDone(commonReconcilerAccessor) : false)
				|| isDirty(backgroundThreadAccessor) || isActive(backgroundThreadAccessor);
	}

	private static boolean isInitialProcessDone(Accessor javaReconcilerAccessor)
	{
		return ((Boolean) javaReconcilerAccessor.get("fInitialProcessDone")).booleanValue();
	}

	private static boolean isDirty(Accessor backgroundThreadAccessor)
	{
		return ((Boolean) backgroundThreadAccessor.invoke("isDirty", new Object[0])).booleanValue();
	}

	private static boolean isActive(Accessor backgroundThreadAccessor)
	{
		return ((Boolean) backgroundThreadAccessor.invoke("isActive", new Object[0])).booleanValue();
	}

	public static void resetFolding()
	{
		// JavaPlugin.getDefault().getPreferenceStore().setToDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED);
	}

	// FIXME Fix so we can enable/disable folding?
	public static boolean enableFolding(boolean value)
	{
		return value;
		// IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
		// boolean oldValue= preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_ENABLED);
		// if (value != oldValue)
		// preferenceStore.setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED, value);
		// return oldValue;
	}

	public static void joinBackgroundActivities(AbstractTextEditor editor) throws CoreException
	{
		joinBackgroundActivities(getSourceViewer(editor));
	}

	public static void joinBackgroundActivities(SourceViewer sourceViewer) throws CoreException
	{
		joinBackgroundActivities();
		joinReconciler(sourceViewer, 500, 0, 500);
	}

	public static void joinBackgroundActivities() throws CoreException
	{
		// Join Building
		Logger.global.entering("EditorTestHelper", "joinBackgroundActivities");
		Logger.global.finer("join builder");
		boolean interrupted = true;
		while (interrupted)
		{
			try
			{
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				interrupted = false;
			}
			catch (InterruptedException e)
			{
				interrupted = true;
			}
		}
		// Join indexing
		// Logger.global.finer("join indexer");
		// new SearchEngine().searchAllTypeNames(
		// null,
		// SearchPattern.R_EXACT_MATCH,
		// "XXXXXXXXX".toCharArray(), // make sure we search a concrete name. This is faster according to Kent
		// SearchPattern.R_EXACT_MATCH,
		// IJavaSearchConstants.CLASS,
		// SearchEngine.createJavaSearchScope(new IJavaElement[0]),
		// new Requestor(),
		// IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
		// null);
		// Join jobs
		joinJobs(0, 0, 500);
		Logger.global.exiting("EditorTestHelper", "joinBackgroundActivities");
	}

	public static boolean joinJobs(long minTime, long maxTime, long intervalTime)
	{
		Logger.global.entering("EditorTestHelper", "joinJobs");
		runEventQueue(minTime);

		DisplayHelper helper = new DisplayHelper()
		{
			public boolean condition()
			{
				return allJobsQuiet();
			}
		};
		boolean quiet = helper.waitForCondition(getActiveDisplay(), maxTime > 0 ? maxTime : Long.MAX_VALUE,
				intervalTime);
		Logger.global.exiting("EditorTestHelper", "joinJobs", new Boolean(quiet));
		return quiet;
	}

}