package com.aptana.scripting;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.aptana.scripting.model.BundleManager;

/**
 * ResourceDeltaVisitor
 */
public class ResourceDeltaVisitor implements IResourceDeltaVisitor
{
	private static final Pattern BUNDLE_PATTERN = Pattern.compile("/.+?/bundles/.+?/bundle\\.rb$"); //$NON-NLS-1$
	private static final Pattern FILE_PATTERN = Pattern.compile("/.+?/bundles/.+?/(?:commands|snippets)/[^/]+\\.rb$"); //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		String fullPath = delta.getFullPath().toString().toLowerCase();
		boolean visitChildren = true;
		
		if (BUNDLE_PATTERN.matcher(fullPath).matches())
		{
			visitChildren = this.processBundle(delta);
		}
		else if (FILE_PATTERN.matcher(fullPath).matches())
		{
			visitChildren = this.processFile(delta);
		}

		return visitChildren;
	}

	/**
	 * processBundle
	 * 
	 * @param delta
	 */
	private boolean processBundle(IResourceDelta delta)
	{
		IFile file = (IFile) delta.getResource();
		boolean visitChildren = true;

		if (file != null && file.getLocation() != null)
		{
			IFolder bundleFolder = (IFolder) file.getParent();
			String bundleFolderPath = bundleFolder.getLocation().toPortableString();

			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					BundleManager.getInstance().processBundle(bundleFolder, false);
					break;

				case IResourceDelta.REMOVED:
					BundleManager.getInstance().removeBundle(bundleFolderPath);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						String oldPath = delta.getMovedFromPath().toPortableString();
						
						BundleManager.getInstance().moveBundle(oldPath, bundleFolderPath);
					}
					if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
					{
						String newPath = delta.getMovedToPath().toString();
						
						BundleManager.getInstance().moveBundle(bundleFolderPath, newPath);
					}
					if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
					{
						BundleManager.getInstance().removeBundle(bundleFolderPath);
						BundleManager.getInstance().processBundle(bundleFolder, false);
					}
					if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
					{
						BundleManager.getInstance().removeBundle(bundleFolderPath);
						BundleManager.getInstance().processBundle(bundleFolder, false);
					}
					break;
			}
		}

		return visitChildren;
	}

	/**
	 * processFile
	 * 
	 * @param delta
	 */
	private boolean processFile(IResourceDelta delta)
	{
		IFile file = (IFile) delta.getResource();
		boolean visitChildren = true;

		if (file != null && file.getLocation() != null)
		{
			BundleManager manager = BundleManager.getInstance();
			
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					System.out.println("Added");
					BundleManager.getInstance().processSnippetOrCommand(file);
					break;

				case IResourceDelta.REMOVED:
					System.out.println("Removed");
					BundleManager.getInstance().removeSnippetOrCommand(file);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						IPath movedFromPath = delta.getMovedFromPath();
						IResource movedFrom = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(movedFromPath);
						
						if (movedFrom != null && movedFrom instanceof IFile)
						{
							manager.removeSnippetOrCommand((IFile) movedFrom);
							manager.processSnippetOrCommand(file);
						}
					}
					else if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
					{
						IPath movedToPath = delta.getMovedToPath();
						IResource movedTo = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(movedToPath);
						
						if (movedTo != null && movedTo instanceof IFile)
						{
							manager.removeSnippetOrCommand(file);
							manager.processSnippetOrCommand((IFile) movedTo);
						}
					}
					else if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
					{
						manager.removeSnippetOrCommand(file);
						manager.processSnippetOrCommand(file);
					}
					else if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
					{
						manager.removeSnippetOrCommand(file);
						manager.processSnippetOrCommand(file);
					}
					break;
			}
		}
		

		return visitChildren;
	}
}
