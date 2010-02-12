package com.aptana.commandline.launcher;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * Process command line arguments.
 *
 * @author schitale
 *
 */
public class CommandlineArgumentsHandler
{
	/**
	 * Process the command line arguments. Currently treats every argument as a file name
	 * and opens it in a editor.
	 *
	 * @param arguments
	 */
	public static void processCommandLineArgs(final String[] arguments)
	{
		WorkbenchJob workbenchJob = new WorkbenchJob("Processing command line args.") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				for (String argument : arguments)
				{
					File file = new File(argument);
					// Process an existing file.
					if (file.exists() && file.isFile())
					{
						Path path = new Path(argument);
						IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
						IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
						IEditorDescriptor editorDescriptor = null;
						if (fileForLocation == null)
						{
							IContentType contentType = Platform.getContentTypeManager().findContentTypeFor(argument);
							editorDescriptor = editorRegistry.getDefaultEditor(argument, contentType);
						}
						else
						{
							editorDescriptor = editorRegistry.getDefaultEditor(argument);
						}
						String editorId = (editorDescriptor == null ?  "com.aptana.editor.text" : editorDescriptor.getId()); //$NON-NLS-1$
						try
						{
							IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file
									.toURI(), editorId, true);
						}
						catch (PartInitException e)
						{
							CommandlineLauncherPlugin.logError(e);
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		workbenchJob.setSystem(true);
		workbenchJob.setPriority(WorkbenchJob.INTERACTIVE);
		workbenchJob.schedule();

	}

}
