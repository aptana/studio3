package com.aptana.radrails.explorer.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;
import org.osgi.service.prefs.BackingStoreException;

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

	private ViewerFilter activeProjectFilter;

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
		activeProjectFilter = new ViewerFilter()
		{

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{
				if (selectedProject == null)
					return false;
				IResource resource = null;
				if (element instanceof IResource)
				{
					resource = (IResource) element;
				}
				if (resource == null)
				{
					if (element instanceof IAdaptable)
					{
						IAdaptable adapt = (IAdaptable) element;
						resource = (IResource) adapt.getAdapter(IResource.class);
					}
				}

				if (resource == null)
					return false;

				IProject project = resource.getProject();
				return selectedProject.equals(project);
			}
		};
		getCommonViewer().addFilter(activeProjectFilter);
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
		getCommonViewer().removeFilter(activeProjectFilter);
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

	private void detectSelectedProject()
	{
		String value = Platform.getPreferencesService().getString(ExplorerPlugin.PLUGIN_ID, ACTIVE_PROJECT, null, null);
		IProject project = null;
		if (value != null)
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(value);
		}
		if (project == null)
		{
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			if (projects == null || projects.length == 0)
				return;
			project = projects[0];
		}
		if (project != null)
		{
			projectCombo.setText(project.getName());
			setActiveProject(project.getName());
			return;
		}
	}

	protected void setActiveProject(String projectName)
	{
		IProject newSelectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (selectedProject != null && selectedProject.equals(newSelectedProject))
			return;

		if (selectedProject != null)
		{
			unsetActiveProject();
		}
		selectedProject = newSelectedProject;
		if (newSelectedProject != null)
		{
			setActiveProject();
		}
		projectChanged(newSelectedProject);
		refreshViewer();

	}

	private void setActiveProject()
	{
		try
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID);
			prefs.put(ACTIVE_PROJECT, selectedProject.getName());
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ExplorerPlugin.logError(e.getMessage(), e);
		}
	}

	private void unsetActiveProject()
	{
		try
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(ExplorerPlugin.PLUGIN_ID);
			prefs.remove(ACTIVE_PROJECT);
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ExplorerPlugin.logError(e.getMessage(), e);
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
							if (delta.getKind() == IResourceDelta.ADDED)
							{
								// Add to the combo and then switch to it!
								final String projectName = resource.getName();
								Display.getDefault().asyncExec(new Runnable()
								{

									public void run()
									{
										projectCombo.add(projectName);
										projectCombo.setText(projectName);
										setActiveProject(projectName);
									}
								});
							}
							else if (delta.getKind() == IResourceDelta.REMOVED)
							{
								// Remove from combo and if it was the active project, switch away from it!
								final String projectName = resource.getName();
								Display.getDefault().asyncExec(new Runnable()
								{

									public void run()
									{
										projectCombo.remove(projectName);
										if (selectedProject != null && selectedProject.getName().equals(projectName))
										{
											IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
													.getProjects();
											String newActiveProject = ""; //$NON-NLS-1$
											if (projects.length > 0)
											{
												newActiveProject = projects[0].getName();
											}
											projectCombo.setText(newActiveProject);
											setActiveProject(newActiveProject);
										}
									}
								});
							}
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
