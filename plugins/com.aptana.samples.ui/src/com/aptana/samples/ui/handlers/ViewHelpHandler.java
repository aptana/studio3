/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.handlers;

import java.io.File;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

import com.aptana.core.logging.IdeLog;
import com.aptana.samples.model.SampleEntry;
import com.aptana.samples.model.SampleEntryUtil;
import com.aptana.samples.model.SamplesReference;
import com.aptana.samples.ui.SamplesUIPlugin;
import com.aptana.samples.ui.views.Messages;
import com.aptana.ui.util.UIUtils;

@SuppressWarnings("restriction")
public class ViewHelpHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection)
		{
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();

			SamplesReference samplesRef = null;
			if (firstElement instanceof SamplesReference)
			{
				samplesRef = (SamplesReference) firstElement;
			}
			else if (firstElement instanceof SampleEntry)
			{
				samplesRef = SampleEntryUtil.getParentSamplesRef((SampleEntry) firstElement);
			}
			if (samplesRef != null)
			{
				try
				{
					String infoFile = samplesRef.getInfoFile();
					if (infoFile != null)
					{
						URL url = (new File(infoFile)).toURI().toURL();
						WebBrowserEditorInput input = new WebBrowserEditorInput(url);
						IWorkbenchPage page = UIUtils.getActivePage();
						if (page != null)
						{
							page.openEditor(input, WebBrowserEditor.WEB_BROWSER_EDITOR_ID);
						}
					}

				}
				catch (Exception e)
				{
					IdeLog.logError(SamplesUIPlugin.getDefault(), Messages.SamplesView_ERR_UnableToOpenHelp, e);
				}
			}
		}
		return null;
	}
}
