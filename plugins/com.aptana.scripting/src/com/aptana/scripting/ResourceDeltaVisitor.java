package com.aptana.scripting;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import com.aptana.scripting.model.Bundle;
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
		
		System.out.println("Visiting " + delta.getFullPath());

		if (BUNDLE_PATTERN.matcher(fullPath).matches())
		{
			System.out.println("process bundle");
			
			visitChildren = this.processBundle(delta);
			
			BundleManager.getInstance().showBundles();
		}
		else if (FILE_PATTERN.matcher(fullPath).matches())
		{
			System.out.println("process command or snippet");
			
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
					System.out.println("add bundle " + bundleFolderPath);
					BundleManager.getInstance().processBundle(bundleFolder, false);
					break;

				case IResourceDelta.REMOVED:
					System.out.println("remove bundle " + bundleFolderPath);
					BundleManager.getInstance().removeBundle(bundleFolderPath);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						String oldPath = delta.getMovedFromPath().toPortableString();
						
						BundleManager.getInstance().moveBundle(oldPath, bundleFolderPath);
						
						System.out.println("Moved from " + oldPath + " to " + bundleFolderPath);
					}
					if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
					{
						String newPath = delta.getMovedToPath().toString();
						
						BundleManager.getInstance().moveBundle(bundleFolderPath, newPath);
						
						System.out.println("Moved from " + bundleFolderPath + " to " + newPath);
					}
					if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
					{
						System.out.println("Replacing " + bundleFolderPath);
						BundleManager.getInstance().removeBundle(bundleFolderPath);
						BundleManager.getInstance().processBundle(bundleFolder, false);
					}
					if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
					{
						System.out.println("Updating content " + bundleFolderPath);
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
			IFolder bundleFolder = (IFolder) file.getParent().getParent();
			String bundleFolderPath = bundleFolder.getLocation().toPortableString();
			String fullPath = file.getLocation().toPortableString();
			Bundle bundle;

			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					BundleManager.getInstance().processFile(file);
					break;

				case IResourceDelta.REMOVED:
					// process removed script
					System.out.println("removed " + fullPath);
					break;

				case IResourceDelta.CHANGED:
					if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
					{
						// remove script
						// process new script
						System.out.println("moved from " + fullPath);
					}
					if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
					{
						// remove script
						// process new script
						System.out.println("moved to " + fullPath);
					}
					if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
					{
						// process new script
						System.out.println("replaced " + fullPath);
					}
					if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
					{
						// process new script
						System.out.println("new content " + fullPath);
					}
					break;
			}
		}

		return visitChildren;
	}
}
