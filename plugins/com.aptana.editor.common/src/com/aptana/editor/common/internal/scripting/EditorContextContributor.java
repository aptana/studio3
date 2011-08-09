/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.scripting;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;

public class EditorContextContributor implements ContextContributor
{
	private static final String EDITOR_PROPERTY_NAME = "editor"; //$NON-NLS-1$
	private static final String EDITOR_RUBY_CLASS = "Editor"; //$NON-NLS-1$

	private IEditorPart _editor;

	/**
	 * EditorContextContributor
	 */
	public EditorContextContributor()
	{
	}

	/**
	 * getDisplay
	 * 
	 * @return
	 */
	private Display getDisplay()
	{
		Display result = Display.getCurrent();

		if (result == null)
		{
			result = Display.getDefault();
		}

		return result;
	}

	/**
	 * onUIThread
	 * 
	 * @return
	 */
	private boolean onUIThread()
	{
		return this.getDisplay() != null;
	}

	/**
	 * getActiveEditor
	 * 
	 * @return
	 */
	private IEditorPart getActiveEditor()
	{
		UIJob job = new UIJob("Get active editor") //$NON-NLS-1$
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				try
				{
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

					if (window != null)
					{
						IWorkbenchPage page = window.getActivePage();

						if (page != null)
						{
							_editor = page.getActiveEditor();
						}
					}
				}
				catch (IllegalStateException e)
				{
				}

				return Status.OK_STATUS;
			}
		};

		if (this.onUIThread())
		{
			job.runInUIThread(new NullProgressMonitor());
		}
		else
		{
			// run the job
			try
			{
				job.schedule();
				job.join();
			}
			catch (InterruptedException e)
			{
				// fail silently
			}
		}

		// grab the result and lose the editor reference
		IEditorPart result = this._editor;
		this._editor = null;

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement,
	 * com.aptana.scripting.model.CommandContext)
	 */
	public void modifyContext(CommandElement command, CommandContext context)
	{
		IEditorPart editor = this.getActiveEditor();

		if (editor != null && command != null)
		{
			Ruby runtime = command.getRuntime();

			if (runtime != null)
			{
				IRubyObject rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RUBLE_MODULE,
						EDITOR_RUBY_CLASS, JavaEmbedUtils.javaToRuby(runtime, editor));

				context.put(EDITOR_PROPERTY_NAME, rubyInstance);
			}
			else
			{
				context.put(EDITOR_PROPERTY_NAME, null);
			}
		}
	}
}
