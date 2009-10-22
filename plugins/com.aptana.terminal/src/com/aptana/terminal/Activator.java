package com.aptana.terminal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.terminal.editor.TerminalEditorInput;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.terminal"; //$NON-NLS-1$

	private static Activator plugin;
	private static Map<String, Image> images = new HashMap<String, Image>();

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns the image on the specified path.
	 * 
	 * @param path
	 *            the path to the image
	 * @return Image the image object
	 */
	public static Image getImage(String path)
	{
		if (images.containsKey(path) == false)
		{
			ImageDescriptor id = getImageDescriptor(path);

			if (id != null)
			{
				Image i = id.createImage();

				images.put(path, i);
			}
		}

		return images.get(path);
	}

	// TODO: Use com.aptana.ide.core.ui.CoreUIUtils.openEditor once this has been merged to master
	// See more comments in the method below
	
	/**
	 * Opens a specific editor.
	 * 
	 * @param editorId
	 *            the editor ID
	 * @param activate
	 *            true if the editor should be activated, false otherwise
	 * @return
	 */
	public static IEditorPart openEditor(String editorId, boolean activate)
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			
			try
			{
				// TODO: changed MATCH pattern from MATCH_ID to MATCH_INPUT, so we'll probably need our own version
				// of this method
				return page.openEditor(new TerminalEditorInput(), editorId, activate, IWorkbenchPage.MATCH_INPUT);
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static String encodeString(String text)
	{
		StringBuffer buffer = new StringBuffer();
		
		for (char c : text.toCharArray())
		{
			if (0 <= c && c < 32 || 128 <= c)
			{
				buffer.append("\\x").append(Integer.toString(c, 16));
			}
			else
			{
				buffer.append(c);
			}
		}
		
		return buffer.toString();
	}
}
