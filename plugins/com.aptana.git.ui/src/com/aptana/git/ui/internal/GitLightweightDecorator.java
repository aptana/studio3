package com.aptana.git.ui.internal;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.ui.ISharedImages;
import org.eclipse.team.ui.TeamImages;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.ui.GitUIPlugin;

public class GitLightweightDecorator extends LabelProvider implements ILightweightLabelDecorator,
		IGitRepositoryListener
{
	private static final String DIRTY_PREFIX = "> ";
	private static final String DECORATOR_ID = "com.aptana.git.ui.internal.GitLightweightDecorator"; //$NON-NLS-1$

	/**
	 * Define a cached image descriptor which only creates the image data once
	 */
	private static class CachedImageDescriptor extends ImageDescriptor
	{
		ImageDescriptor descriptor;

		ImageData data;

		public CachedImageDescriptor(ImageDescriptor descriptor)
		{
			this.descriptor = descriptor;
		}

		public ImageData getImageData()
		{
			if (data == null)
			{
				data = descriptor.getImageData();
			}
			return data;
		}
	}

	private static ImageDescriptor trackedImage;
	private static ImageDescriptor untrackedImage;
	private static ImageDescriptor stagedAddedImage;
	private static ImageDescriptor stagedRemovedImage;

	static
	{
		trackedImage = new CachedImageDescriptor(TeamImages.getImageDescriptor(ISharedImages.IMG_CHECKEDIN_OVR));
		untrackedImage = new CachedImageDescriptor(ImageDescriptor.createFromURL(GitUIPlugin.getDefault().getBundle()
				.getEntry("icons/ovr/untracked.gif"))); //$NON-NLS-1$
		stagedAddedImage = new CachedImageDescriptor(ImageDescriptor.createFromURL(GitUIPlugin.getDefault().getBundle()
				.getEntry("icons/ovr/staged_added.gif"))); //$NON-NLS-1$
		stagedRemovedImage = new CachedImageDescriptor(ImageDescriptor.createFromURL(GitUIPlugin.getDefault()
				.getBundle().getEntry("icons/ovr/staged_removed.gif"))); //$NON-NLS-1$
	}

	private static Color greenFG;
	private static Color greenBG;
	private static Color redFG;
	private static Color redBG;

	public GitLightweightDecorator()
	{
		GitRepository.addListener(this);
	}

	public void decorate(Object element, IDecoration decoration)
	{
		final IResource resource = getResource(element);
		if (resource == null)
			return;

		// Don't decorate if the workbench is not running
		if (!PlatformUI.isWorkbenchRunning())
			return;

		// Don't decorate if UI plugin is not running
		GitUIPlugin activator = GitUIPlugin.getDefault();
		if (activator == null)
			return;

		// Don't decorate the workspace root
		if (resource.getType() == IResource.ROOT)
			return;

		// Don't decorate non-existing resources
		if (!resource.exists() && !resource.isPhantom())
			return;

		switch (resource.getType())
		{
			case IResource.PROJECT:
				decorateProject(decoration, resource);
				// fall through intentionally!
			case IResource.FOLDER:
				decorateFolder(decoration, resource);
				break;
			case IResource.FILE:
				decorateFile(decoration, resource);
				break;
		}
	}

	private void decorateFolder(IDecoration decoration, IResource resource)
	{
		GitRepository repo = getRepo(resource);
		if (repo == null)
			return;

		List<ChangedFile> changedFiles = repo.index().changedFiles();
		if (changedFiles == null || changedFiles.isEmpty())
		{
			return;
		}
		String workingDirectory = repo.workingDirectory();
		for (ChangedFile changedFile : changedFiles)
		{
			String fullPath = workingDirectory + File.separator + changedFile.getPath();
			if (fullPath.startsWith(resource.getLocationURI().getPath()))
			{
				decoration.addPrefix(DIRTY_PREFIX);
				return;
			}
		}
	}

	private void decorateFile(IDecoration decoration, final IResource resource)
	{
		IFile file = (IFile) resource;
		GitRepository repo = getRepo(resource);
		if (repo == null)
			return;

		ChangedFile changed = repo.getChangedFileForResource(file);
		if (changed == null)
		{
			decoration.addOverlay(trackedImage);
			return;
		}

		ImageDescriptor overlay = trackedImage;
		if (changed.hasStagedChanges())
		{
			decoration.setForegroundColor(greenFG());
			decoration.setBackgroundColor(greenBG());
			if (changed.getStatus() == ChangedFile.Status.DELETED)
			{
				overlay = stagedRemovedImage;
			}
			else if (changed.getStatus() == ChangedFile.Status.NEW)
			{
				overlay = stagedAddedImage;
			}
		}
		else if (changed.hasUnstagedChanges())
		{
			decoration.setForegroundColor(redFG());
			decoration.setBackgroundColor(redBG());
			if (changed.getStatus() == ChangedFile.Status.NEW)
			{
				overlay = untrackedImage;
			}
		}
		decoration.addPrefix(DIRTY_PREFIX);
		decoration.addOverlay(overlay);
	}

	private void decorateProject(IDecoration decoration, final IResource resource)
	{
		GitRepository repo = getRepo(resource);
		if (repo == null)
			return;

		StringBuilder builder = new StringBuilder();
		builder.append(" ["); //$NON-NLS-1$
		String branch = repo.currentBranch();
		builder.append(branch);
		String[] commits = repo.commitsBehind(branch);
		if (commits != null && commits.length > 0)
		{
			builder.append("-").append(commits.length); //$NON-NLS-1$
		}
		else
		{
			commits = repo.commitsAhead(branch);
			if (commits != null && commits.length > 0)
				builder.append("+").append(commits.length); //$NON-NLS-1$
		}
		builder.append("]"); //$NON-NLS-1$
		decoration.addSuffix(builder.toString());
	}

	private Color greenFG()
	{
		if (greenFG == null)
		{
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			greenFG = new Color(display, 60, 168, 60);
		}
		return greenFG;
	}

	private Color greenBG()
	{
		if (greenBG == null)
		{
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			greenBG = new Color(display, 221, 255, 221);
		}
		return greenBG;
	}

	private Color redFG()
	{
		if (redFG == null)
		{
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			redFG = new Color(display, 154, 11, 11);
		}
		return redFG;
	}

	private Color redBG()
	{
		if (redBG == null)
		{
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			redBG = new Color(display, 255, 238, 238);
		}
		return redBG;
	}

	@Override
	public void dispose()
	{
		if (greenFG != null)
			greenFG.dispose();
		if (greenBG != null)
			greenBG.dispose();
		if (redFG != null)
			redFG.dispose();
		if (redBG != null)
			redBG.dispose();
		GitRepository.removeListener(this);
		super.dispose();
	}

	private static IResource getResource(Object element)
	{

		IResource resource = null;
		if (element instanceof IResource)
		{
			resource = (IResource) element;
		}
		else if (element instanceof IAdaptable)
		{
			final IAdaptable adaptable = (IAdaptable) element;
			resource = (IResource) adaptable.getAdapter(IResource.class);
		}
		return resource;
	}

	protected GitRepository getRepo(IResource resource)
	{
		if (resource == null)
			return null;
		IProject project = resource.getProject();
		if (project == null)
			return null;
		return GitRepository.getAttached(project);
	}

	/**
	 * Post the label event to the UI thread
	 * 
	 * @param event
	 *            The event to post
	 */
	private void postLabelEvent(final LabelProviderChangedEvent event)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				fireLabelProviderChanged(event);
			}
		});
	}

	public void indexChanged(IndexChangedEvent e)
	{
		// FIXME We need to walk the project and pass all files into the event, or we need to get the diff of updated
		// files from the event and force refreshes of just those! just grabbing the "changedFiles" after the index
		// change isn't sufficient!
		postLabelEvent(new LabelProviderChangedEvent(this));
	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		// FIXME Grab the repo and only refresh the project's attached to it (and their children)
		postLabelEvent(new LabelProviderChangedEvent(this));
	}

	/**
	 * Perform a blanket refresh of all decorations
	 */
	public static void refresh()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				GitUIPlugin.getDefault().getWorkbench().getDecoratorManager().update(DECORATOR_ID);
			}
		});
	}
}
