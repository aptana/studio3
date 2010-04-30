package com.aptana.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class SWTUtils
{
	private SWTUtils()
	{
	}
	
	/**
	 * Finds and caches the image from the image descriptor for this particular bundle
	 * 
	 * @param bundle
	 *            The bundle to search
	 * @param path
	 *            The path to the image
	 * @return The image, or null if not found
	 */
	public static Image getImage(Bundle bundle, String path)
	{
		if (path.charAt(0) != '/')
		{
			path = "/" + path; //$NON-NLS-1$
		}

		String computedName = bundle.getSymbolicName() + path;
		Image image = JFaceResources.getImage(computedName);
		
		if (image != null)
		{
			return image;
		}

		ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(bundle.getSymbolicName(), path);
		
		if (id != null)
		{
			JFaceResources.getImageRegistry().put(computedName, id);
			
			return JFaceResources.getImage(computedName);
		}
		else
		{
			return null;
		}
	}
}
