package com.aptana.git.ui.internal.actions;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

public class ShowInHistoryHandler extends AbstractHandler
{

	private boolean enabled;

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		if (evaluationContext instanceof EvaluationContext)
		{
			enabled = (getResource((EvaluationContext) evaluationContext) != null);
		}
		else
		{
			enabled = false;
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null)
		{
			return null;
		}
		Object context = event.getApplicationContext();
		if (context instanceof EvaluationContext)
		{
			IResource resource = getResource((EvaluationContext) context);
			if (resource != null)
			{
				TeamUI.getHistoryView().showHistoryFor(resource);
			}
		}
		return null;
	}

	private IResource getResource(EvaluationContext evContext)
	{
		// TODO Ensure that the resource is under a git repo!
		Object input = evContext.getVariable("showInInput"); //$NON-NLS-1$
		if (input instanceof IFileEditorInput)
		{
			IFileEditorInput fei = (IFileEditorInput) input;
			return fei.getFile();
		}
		else if (input instanceof IURIEditorInput)
		{
			// IURIEditorInput uriInput = (IURIEditorInput) input;
			// open(uriInput.getURI());
		}
		else
		{
			@SuppressWarnings("unchecked")
			List<Object> selectedFiles = (List<Object>) evContext.getDefaultVariable();
			for (Object selected : selectedFiles)
			{
				if (selected instanceof IResource)
				{
					return (IResource) selected;
				}
				else if (selected instanceof IAdaptable)
				{
					return (IResource) ((IAdaptable) selected).getAdapter(IResource.class);
				}

			}
		}
		return null;
	}

}
