package com.aptana.editor.css;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.editor.css.parsing.CSSScanner;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.css"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private CSSCodeScanner _codeScanner;
	private CSSParser fParser;
	private CSSScanner fScanner;

	private static Map<String, Image> fImages = new HashMap<String, Image>();

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

	public CSSParser getParser()
	{
	    if (fParser == null)
	    {
	        fParser = new CSSParser();
	    }
	    return fParser;
	}

    public CSSScanner getTokenScanner()
    {
        if (fScanner == null)
        {
            fScanner = new CSSScanner();
        }
        return fScanner;
    }

    public static Image getImage(String path)
    {
        Image image = fImages.get(path);
        if (image == null)
        {
            ImageDescriptor id = getImageDescriptor(path);
            if (id == null)
            {
                return null;
            }

            image = id.createImage();
            fImages.put(path, image);
        }
        return image;
    }

    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
