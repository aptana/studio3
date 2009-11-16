package com.aptana.editor.scripting.startup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.scripting.actions.ExecuteLineInsertingResultAction;
import com.aptana.editor.scripting.actions.ExpandSnippetAction;
import com.aptana.editor.scripting.actions.FilterThroughCommandAction;

public class EditorScriptingStartup implements IStartup {
	private static IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
			if (part instanceof IEditorPart) {
				addAction((IEditorPart)part);
			}
		}	
	};
	
	private static IPageListener pageListener = new IPageListener() {
		
		public void pageOpened(IWorkbenchPage page) {
			addPartListener(page);
		}
		
		public void pageClosed(IWorkbenchPage page) {
			page.removePartListener(partListener);
		}
		
		public void pageActivated(IWorkbenchPage page) {
			
		}
	};

	private static IWindowListener windowListener = new IWindowListener() {
		
		public void windowOpened(IWorkbenchWindow window) {
			addWindowListeners(window);
		}
		
		public void windowDeactivated(IWorkbenchWindow window) {
			
		}
		
		public void windowClosed(IWorkbenchWindow window) {
			window.removePageListener(pageListener);
			IWorkbenchPage[] workbenchPages = window.getPages();
			for (IWorkbenchPage workbenchPage : workbenchPages) {
				workbenchPage.removePartListener(partListener);
			}
		}
		
		public void windowActivated(IWorkbenchWindow window) {
			
		}
	};

	public void earlyStartup() {
		Job addEditorMonitorJobs = new WorkbenchJob("Monitor TextEditors.") {  //$NON-NLS-1$
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench != null) {
					IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
					for (IWorkbenchWindow workbenchWindow : workbenchWindows) {
						addWindowListeners(workbenchWindow);
					}
					workbench.addWindowListener(windowListener);
				}
				return Status.OK_STATUS;
			}

		};
		addEditorMonitorJobs.setSystem(true);
		addEditorMonitorJobs.schedule();
	}
	
	private static void addWindowListeners(IWorkbenchWindow workbenchWindow) {
		IWorkbenchPage[] workbenchPages = workbenchWindow.getPages();
		for (IWorkbenchPage workbenchPage : workbenchPages) {
			addPartListener(workbenchPage);
		}
		workbenchWindow.addPageListener(pageListener);
	}
	
	private static void addPartListener(IWorkbenchPage workbenchPage) {
		IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
		for (IEditorReference editorReference : editorReferences) {
			IEditorPart editor = editorReference.getEditor(false);
			if (editor != null) {
				addAction(editor);
			}
		}
		workbenchPage.addPartListener(partListener);		
	}

	private static void addAction(IEditorPart editorPart) {
		if (editorPart instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editorPart;
			if (textEditor.isEditable()) {
				textEditor.setAction(ExpandSnippetAction.COMMAND_ID, ExpandSnippetAction.create(textEditor));
				textEditor.setAction(ExecuteLineInsertingResultAction.COMMAND_ID, ExecuteLineInsertingResultAction.create(textEditor));
				textEditor.setAction(FilterThroughCommandAction.COMMAND_ID, FilterThroughCommandAction.create(textEditor));
			}
		}
	}
}
