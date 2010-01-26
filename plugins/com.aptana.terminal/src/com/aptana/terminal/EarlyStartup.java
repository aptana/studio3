package com.aptana.terminal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.preferences.IPreferenceConstants;

public class EarlyStartup implements IStartup
{
	private static final String RAILS_PERSPECTIVE_ID = "org.radrails.rails.ui.PerspectiveRails";
	private IPerspectiveListener4 _perspectiveListener;

	/**
	 * earlyStartup
	 */
	public void earlyStartup()
	{
		this._perspectiveListener = new PerspectiveAdapter()
		{
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
			{
				if (RAILS_PERSPECTIVE_ID.equals(perspective.getId()) && "resetComplete".equals(changeId))
				{
					openTerminalEditor();
				}
			}
		};

		WorkbenchJob job = new WorkbenchJob("Terminal Perspective Listener")
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
				boolean firstRun = prefs.getBoolean(IPreferenceConstants.FIRST_RUN);

				if (firstRun)
				{
					openTerminalEditor();

					// set firstRun to false
					prefs.setValue(IPreferenceConstants.FIRST_RUN, false);
				}

				try
				{
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

					workbenchWindow.addPerspectiveListener(_perspectiveListener);
				}
				catch (IllegalStateException e)
				{
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}
		};
		
		job.schedule();
	}

	/**
	 * openTerminalEditor
	 */
	private void openTerminalEditor()
	{
		Utils.openEditor(TerminalEditor.ID, true);
	}
}
