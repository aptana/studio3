package com.aptana.editor.scripting.startup;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewerExtension;
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
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.scripting.actions.ExecuteLineInsertingResultAction;
import com.aptana.editor.scripting.actions.ExpandSnippetVerifyKeyListener;
import com.aptana.editor.scripting.actions.FilterThroughCommandAction;
import com.aptana.radrails.editor.common.AbstractThemeableEditor;

public class EditorScriptingStartup implements IStartup {

	public void earlyStartup() {
		Job addEditorMonitorJobs = new WorkbenchJob("Monitor TextEditors.") { //$NON-NLS-1$
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench != null) {
					IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
					for (IWorkbenchWindow workbenchWindow : workbenchWindows) {
						processWindow(workbenchWindow);
					}
					workbench.addWindowListener(windowListener);
				}
				return Status.OK_STATUS;
			}

		};
		addEditorMonitorJobs.setSystem(true);
		addEditorMonitorJobs.schedule();
	}
	
	private static IWindowListener windowListener = new IWindowListener() {
		
		public void windowOpened(IWorkbenchWindow window) {
			// process newly opened window
			processWindow(window);
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
	
	private static IPageListener pageListener = new IPageListener() {
		
		public void pageOpened(IWorkbenchPage page) {
			processPage(page);
		}
		
		public void pageClosed(IWorkbenchPage page) {
			page.removePartListener(partListener);
		}
		
		public void pageActivated(IWorkbenchPage page) {
			
		}
	};
	
	private static IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			if (part instanceof MultiPageEditorPart) {
				((MultiPageEditorPart) part).removePageChangedListener(pageChangedListener);
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart editorPart) {
			if (editorPart instanceof IEditorPart) {
				processPart((IEditorPart)editorPart);
			}
		}	
	};
	
	private static IPageChangedListener pageChangedListener = new IPageChangedListener() {
		public void pageChanged(PageChangedEvent event) {
			Object selectedPage = event.getSelectedPage();
			if (selectedPage instanceof ITextEditor) {
				processITextEditor((ITextEditor)selectedPage);
			}
		}
	};
	
	private static void processWindow(IWorkbenchWindow workbenchWindow) {
		// process existing pages
		IWorkbenchPage[] workbenchPages = workbenchWindow.getPages();
		for (IWorkbenchPage workbenchPage : workbenchPages) {
			processPage(workbenchPage);
		}
		// process future pages
		workbenchWindow.addPageListener(pageListener);
	}
	
	private static void processPage(IWorkbenchPage workbenchPage) {
		IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
		for (IEditorReference editorReference : editorReferences) {
			processPart(editorReference.getEditor(false));
		}
		workbenchPage.addPartListener(partListener);		
	}
	
	private static void processPart(IEditorPart editorPart) {
		if (editorPart instanceof ITextEditor) {
			processITextEditor((ITextEditor)editorPart);
		} else if (editorPart instanceof MultiPageEditorPart) {
			processMultiPageEditorPart((MultiPageEditorPart) editorPart);
		}
	}
	
	private static void processITextEditor(ITextEditor textEditor) {
		addActions(textEditor);
	}
	
	private static void processMultiPageEditorPart(MultiPageEditorPart multiPageEditorPart) {
		Object selectedPage = multiPageEditorPart.getSelectedPage();
		if (selectedPage instanceof ITextEditor) {
			addActions((ITextEditor)selectedPage);
		}
		multiPageEditorPart.addPageChangedListener(pageChangedListener);
	}

	private static void addActions(IEditorPart editorPart) {
		if (editorPart instanceof ITextEditor) {
			final ITextEditor textEditor = (ITextEditor) editorPart;
			if (textEditor instanceof AbstractThemeableEditor && textEditor.isEditable()) {
				textEditor.setAction(ExecuteLineInsertingResultAction.COMMAND_ID, ExecuteLineInsertingResultAction.create(textEditor));
				textEditor.setAction(FilterThroughCommandAction.COMMAND_ID, FilterThroughCommandAction.create(textEditor));
				Object adapter = textEditor.getAdapter(ITextOperationTarget.class);
				if (adapter instanceof ITextViewerExtension) {
					ITextViewerExtension textViewerExtension = (ITextViewerExtension) adapter;
					textViewerExtension.prependVerifyKeyListener(new ExpandSnippetVerifyKeyListener(textEditor));
				}
			}
		}
	}
}
