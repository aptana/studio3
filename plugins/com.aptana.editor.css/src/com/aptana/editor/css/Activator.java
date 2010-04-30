package com.aptana.editor.css;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexWriter;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.css"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private CSSCodeScanner _codeScanner;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		CSSIndexWriter indexer = new CSSIndexWriter();
		
		this.loadMetadata(indexer, "/metadata/css_metadata.xml");
		
		IndexManager manager = IndexManager.getInstance();
		Index index = manager.getIndex(CSSIndexConstants.METADATA);
		
		indexer.writeToIndex(index);
	}
	
	/**
	 * loadMetadata
	 * 
	 * @param indexer
	 * @param resources
	 */
	private void loadMetadata(CSSIndexWriter indexer, String ... resources)
	{
		for (String resource : resources)
		{
			URL url = FileLocator.find(this.getBundle(), new Path(resource), null);
			
			if (url != null)
			{
				InputStream stream = null;
				
				try
				{
					stream = url.openStream();
					
					indexer.loadXML(stream);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (Throwable t)
				{
					t.printStackTrace();
				}
				finally
				{
					if (stream != null)
					{
						try
						{
							stream.close();
						}
						catch (IOException e)
						{
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * getCodeScanner
	 * 
	 * @return
	 */
	public CSSCodeScanner getCodeScanner()
	{
		if (this._codeScanner == null)
		{
			this._codeScanner = new CSSCodeScanner();
		}

		return this._codeScanner;
	}

	public static Image getImage(String path)
	{
		ImageRegistry registry = plugin.getImageRegistry();
		Image image = registry.get(path);
		
		if (image == null)
		{
			ImageDescriptor id = getImageDescriptor(path);
			
			if (id == null)
			{
				return null;
			}
			
			registry.put(path, id);
			image = registry.get(path);
		}
		
		return image;
	}

	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
