package com.aptana.radrails.explorer.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.UIJob;

import com.aptana.radrails.explorer.ExplorerPlugin;

/**
 * Customized CommonNavigator that adds a project combo and focuses the view on a single project.
 * 
 * @author cwilliams
 */
public class SingleProjectView extends CommonNavigator
{
	/**
	 * Property we assign to a project to make it the active one that this view is filtered to.
	 */
	private static final String ACTIVE_PROJECT = "activeProject"; //$NON-NLS-1$

	private Combo projectCombo;
	protected IProject selectedProject;

	private ResourceListener fResourceListener;

	@Override
	public void createPartControl(Composite aParent)
	{
		// Create our own parent
		Composite customComposite = new Composite(aParent, SWT.NONE);
		customComposite.setLayout(new FormLayout());

		Composite bottom = doCreatePartControl(customComposite);
		createNavigator(customComposite, bottom);

		fResourceListener = new ResourceListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener, IResourceChangeEvent.POST_CHANGE);
		detectSelectedProject();
	}

	protected Composite doCreatePartControl(Composite customComposite)
	{
		createProjectCombo(customComposite);
		return projectCombo;
	}

	private void createNavigator(Composite myComposite, Composite top)
	{
		Composite viewer = new Composite(myComposite, SWT.NONE);
		viewer.setLayout(new FillLayout());
		FormData data2 = new FormData();
		data2.top = new FormAttachment(top);
		data2.bottom = new FormAttachment(100, 0);
		data2.right = new FormAttachment(100, 0);
		data2.left = new FormAttachment(0, 0);
		viewer.setLayoutData(data2);
		super.createPartControl(viewer);
	}

	private IProject[] createProjectCombo(Composite parent)
	{
		projectCombo = new Combo(parent, SWT.DROP_DOWN | SWT.MULTI | SWT.READ_ONLY);
		FormData projectData = new FormData();
		projectData.left = new FormAttachment(0, 5);
		projectData.top = new FormAttachment(0, 5);
		projectData.right = new FormAttachment(100, -5);
		projectCombo.setLayoutData(projectData);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : projects)
		{
			projectCombo.add(iProject.getName());
		}
		projectCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setActiveProject(projectCombo.getText());
			}
		});
		return projects;
	}

	@Override
	public void dispose()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
		super.dispose();
	}

	/**
	 * Returns the name for the given element. Used as the name for the current frame.
	 */
	String getFrameName(Object element)
	{
		if (element instanceof IResource)
		{
			return ((IResource) element).getName();
		}
		String text = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(element);
		if (text == null)
		{
			return "";//$NON-NLS-1$
		}
		return text;
	}

	protected void reloadProjects()
	{
		Job job = new UIJob("Reload Projects") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// FIXME What if the active project was deleted or renamed?
				projectCombo.removeAll();
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject iProject : projects)
				{
					projectCombo.add(iProject.getName());
				}
				if (selectedProject == null)
				{
					if (projects.length > 0)
					{
						setActiveProject(projects[0].getName());
					}
				}
				else
				{
					projectCombo.setText(selectedProject.getName());
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	private void detectSelectedProject()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects == null)
			return;
		for (IProject iProject : projects)
		{
			try
			{
				String value = iProject.getPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID,
						ACTIVE_PROJECT));
				if (value != null && value.equals(Boolean.TRUE.toString()))
				{
					projectCombo.setText(iProject.getName());
					setActiveProject(iProject.getName());
					return;
				}
			}
			catch (CoreException e)
			{
				ExplorerPlugin.logError(e);
			}
		}
	}

	protected void setActiveProject(String projectName)
	{
		IProject newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (newSelectedProject == null || (selectedProject != null && newSelectedProject.equals(selectedProject)))
			return;
		try
		{
			if (selectedProject != null)
			{
				selectedProject
						.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT), null);
			}
			selectedProject = newSelectedProject;
			selectedProject.setPersistentProperty(new QualifiedName(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT),
					Boolean.TRUE.toString());
			projectChanged(newSelectedProject);
			refreshViewer();
		}
		catch (CoreException e)
		{
			ExplorerPlugin.logError(e);
		}
	}

	protected void projectChanged(IProject newSelectedProject)
	{
		// Override in child classes
	}

	protected void refreshViewer()
	{
		if (getCommonViewer() == null)
			return;
		getCommonViewer().refresh();
	}

	private class ResourceListener implements IResourceChangeListener
	{

		public void resourceChanged(IResourceChangeEvent event)
		{
			IResourceDelta delta = event.getDelta();
			if (delta == null)
				return;
			try
			{
				delta.accept(new IResourceDeltaVisitor()
				{

					public boolean visit(IResourceDelta delta) throws CoreException
					{
						IResource resource = delta.getResource();
						if (resource.getType() == IResource.FILE || resource.getType() == IResource.FOLDER)
							return false;
						if (resource.getType() == IResource.ROOT)
							return true;
						if (resource.getType() == IResource.PROJECT)
						{
							// a project was added, removed, or changed!
							reloadProjects();
						}
						return false;
					}
				});
			}
			catch (CoreException e)
			{
				ExplorerPlugin.logError(e);
			}
		}
	}

}
