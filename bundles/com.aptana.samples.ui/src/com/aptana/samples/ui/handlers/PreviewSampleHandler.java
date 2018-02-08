/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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

import com.aptana.samples.handlers.ISamplePreviewHandler;
import com.aptana.samples.model.SamplesReference;

public class PreviewSampleHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection)
		{
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();

			if (firstElement instanceof SamplesReference)
			{
				SamplesReference sample = (SamplesReference) firstElement;
				ISamplePreviewHandler handler = sample.getPreviewHandler();
				if (handler != null)
				{
					handler.previewRequested(sample);
				}
			}
		}
		return null;
	}
}
