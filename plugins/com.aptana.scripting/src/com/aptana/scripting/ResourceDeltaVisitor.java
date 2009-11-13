package com.aptana.scripting;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
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
	private static final Pattern USER_BUNDLE_PATTERN;
	private static final Pattern USER_FILE_PATTERN;

	static
	{
		String userBundlesRoot = BundleManager.getInstance().getUserBundlePath().toLowerCase();
		
		// TODO: make this work on win32
		USER_BUNDLE_PATTERN = Pattern.compile(userBundlesRoot + "/.+?/bundle\\.rb$"); //$NON-NLS-1$
		USER_FILE_PATTERN = Pattern.compile(userBundlesRoot + "/.+?/(?:commands|snippets)/[^/]+\\.rb$"); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		String fullProjectPath = delta.getFullPath().toString().toLowerCase();
		String fullPath = delta.getResource().getLocation().toPortableString().toLowerCase();
		boolean visitChildren = true;
		
		if (BUNDLE_PATTERN.matcher(fullProjectPath).matches() || USER_BUNDLE_PATTERN.matcher(fullPath).matches())
		{
			visitChildren = this.processBundle(delta);
		}
		else if (FILE_PATTERN.matcher(fullProjectPath).matches() || USER_FILE_PATTERN.matcher(fullPath).matches())
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
		IResource file = delta.getResource();
		boolean visitChildren = true;

		if (file != null && file.getLocation() != null)
		{
			IResource bundleFolder = file.getParent();
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
		IResource file = delta.getResource();
		boolean visitChildren = true;

		if (file != null && file.getLocation() != null)
		{
			BundleManager manager = BundleManager.getInstance();
			
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					BundleManager.getInstance().processSnippetOrCommand(file);
					break;

				case IResourceDelta.REMOVED:
					BundleManager.getInstance().removeSnippetOrCommand(file);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						IPath movedFromPath = delta.getMovedFromPath();
						IResource movedFrom = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(movedFromPath);
						
						if (movedFrom != null && movedFrom instanceof IFile)
						{
							manager.removeSnippetOrCommand(movedFrom);
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
							manager.processSnippetOrCommand(movedTo);
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
