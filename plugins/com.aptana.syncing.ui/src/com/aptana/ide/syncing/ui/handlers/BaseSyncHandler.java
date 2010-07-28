package com.aptana.ide.syncing.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;

import com.aptana.ide.syncing.core.SiteConnectionUtils;

public abstract class BaseSyncHandler extends AbstractHandler
{

	private IResource selectedResource;

	@Override
	public boolean isEnabled()
	{
		return selectedResource != null && SiteConnectionUtils.findSitesForSource(selectedResource).length > 0;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		selectedResource = null;
		if (evaluationContext instanceof EvaluationContext)
		{
			Object value = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (value instanceof ISelection)
			{
				ISelection selections = (ISelection) value;
				if (!selections.isEmpty() && selections instanceof IStructuredSelection)
				{
					Object selection = ((IStructuredSelection) selections).getFirstElement();
					IResource resource = null;
					if (selection instanceof IResource)
					{
						resource = (IResource) selection;
					}
					else if (selection instanceof IAdaptable)
					{
						resource = (IResource) ((IAdaptable) selection).getAdapter(IResource.class);
					}
					if (resource != null)
					{
						selectedResource = resource;
					}
				}
			}
		}
	}

}
