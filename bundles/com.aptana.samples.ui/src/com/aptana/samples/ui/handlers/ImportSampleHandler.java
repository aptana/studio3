/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.core.util.StringUtil;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.model.SamplesReference;
import com.aptana.samples.ui.project.SampleProjectCreator;

public class ImportSampleHandler extends AbstractHandler
{
	public static final String COMMAND_ID = "com.aptana.samples.ui.commands.import"; //$NON-NLS-1$

	private static final String PARAMETER_ID = "id"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		String id = event.getParameter(PARAMETER_ID);
		if (StringUtil.isEmpty(id))
		{
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if (selection instanceof IStructuredSelection)
			{
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				if (firstElement instanceof SamplesReference)
				{
					return SampleProjectCreator.createSampleProject((SamplesReference) firstElement);
				}
			}
		}
		else
		{
			IProjectSample samplesRef = SamplesPlugin.getDefault().getSamplesManager().getSample(id);
			if (samplesRef != null)
			{
				return SampleProjectCreator.createSampleProject(samplesRef);
			}
		}
		return null;
	}
}
