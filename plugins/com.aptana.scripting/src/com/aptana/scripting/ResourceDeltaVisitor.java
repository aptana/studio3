package com.aptana.scripting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * ResourceDeltaVisitor
 */
public class ResourceDeltaVisitor implements IResourceDeltaVisitor
{
	private static final Pattern FILE_PATTERN = Pattern.compile("/.+?/bundles/.+?/bundle.rb"); //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		String fullPath = delta.getFullPath().toString();
		
		System.out.println("visiting " + fullPath);
		
		Matcher matcher = FILE_PATTERN.matcher(fullPath.toLowerCase());
		boolean result = true;

		if (matcher.matches())
		{
			IFile file = (IFile) delta.getResource();

			if (file != null && file.getLocation() != null)
			{
				//IFolder folder = (IFolder) file.getParent();
				fullPath = file.getLocation().toPortableString();
				System.out.println("processing " + fullPath);

				switch (delta.getKind())
				{
					case IResourceDelta.ADDED:
						// process new or changed script
						System.out.println("added");
						break;
						
					case IResourceDelta.REMOVED:
						// process removed script
						System.out.println("removed");
						break;
						
					case IResourceDelta.CHANGED:
						if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0)
						{
							// remove script
							// process new script
							System.out.println("moved from");
						}
						if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0)
						{
							// remove script
							// process new script
							System.out.println("moved to");
						}
						if ((delta.getFlags() & IResourceDelta.REPLACED) != 0)
						{
							// process new script
							System.out.println("replaced");
						}
						if ((delta.getFlags() & IResourceDelta.CONTENT) != 0)
						{
							// process new script
							System.out.println("new content");
						}
						break;
				}
			}
		}

		return result;
	}
}
