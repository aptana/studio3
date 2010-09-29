package com.aptana.index.core.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;

import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexProjectJob;

public class RebuildHandler extends AbstractHandler
{
	private List<IProject> _projects;

	/**
	 * RebuildHandler
	 */
	public RebuildHandler()
	{
		this._projects = new ArrayList<IProject>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IndexManager manager = IndexManager.getInstance();

		for (IProject p : this._projects)
		{
			// remove project index
			manager.removeIndex(p.getLocationURI());

			// and then re-build it
			new IndexProjectJob(p).schedule();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		return this._projects.isEmpty() == false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#setEnabled(java.lang.Object)
	 */
	@Override
	public void setEnabled(Object evaluationContext)
	{
		// clear cached selection
		this._projects.clear();

		if (evaluationContext instanceof EvaluationContext)
		{
			EvaluationContext context = (EvaluationContext) evaluationContext;
			Object value = context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);

			if (value instanceof ISelection)
			{
				ISelection selection = (ISelection) value;

				if (selection instanceof IStructuredSelection && selection.isEmpty() == false)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;

					for (Object object : structuredSelection.toArray())
					{
						if (object instanceof IProject)
						{
							this._projects.add((IProject) object);
						}
					}
				}
			}
		}
	}
}
