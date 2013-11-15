package com.aptana.explorer.internal.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISources;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.explorer.internal.ui.CaseInsensitiveProjectComparator;
import com.aptana.explorer.internal.ui.SingleProjectView;

public class SwitchProjectMenuItem extends CompoundContributionItem implements IWorkbenchContribution
{

	private IServiceLocator serviceLocator;

	@Override
	protected IContributionItem[] getContributionItems()
	{
		List<IContributionItem> contributions = new ArrayList<IContributionItem>();

		IEvaluationService evalService = (IEvaluationService) serviceLocator.getService(IEvaluationService.class);
		IEvaluationContext context = evalService.getCurrentState();
		Object part = context.getVariable(ISources.ACTIVE_PART_NAME);
		if (part instanceof SingleProjectView)
		{
			final SingleProjectView view = (SingleProjectView) part;
			IProject viewProject = view.getActiveProject();
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			List<IProject> sortedProjects = CollectionsUtil.newList(projects);
			Collections.sort(sortedProjects, new CaseInsensitiveProjectComparator());

			for (final IProject project : sortedProjects)
			{
				if (!project.isAccessible() || project.equals(viewProject))
				{
					continue;
				}
				contributions.add(new ContributionItem()
				{
					public void fill(Menu menu, int index)
					{
						MenuItem item = new MenuItem(menu, SWT.NONE);
						item.setText(project.getName());
						item.addSelectionListener(new SelectionAdapter()
						{
							@Override
							public void widgetSelected(SelectionEvent e)
							{
								view.setActiveProject(project);
							}
						});
					}
				});
			}
		}

		return contributions.toArray(new IContributionItem[contributions.size()]);
	}

	public void initialize(IServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}

}
