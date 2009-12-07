package com.aptana.editor.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.editor.common.theme.ColorManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonEditorPlugin extends AbstractUIPlugin
{

	public static final String PENCIL_ICON = "icons/pencil.png"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.common"; //$NON-NLS-1$

    private static final String TEMPLATES = PLUGIN_ID + ".templates"; //$NON-NLS-1$

	// The shared instance
	private static CommonEditorPlugin plugin;

	private ColorManager _colorManager;

	private Map<String, Image> images = new HashMap<String, Image>();
    private Map<ContextTypeRegistry, ContributionTemplateStore> fTemplateStoreMap;

	/**
	 * The constructor
	 */
	public CommonEditorPlugin()
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
		if (_colorManager != null)
			_colorManager.dispose();
		_colorManager = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CommonEditorPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * getColorManager
	 * 
	 * @return
	 */
	public ColorManager getColorManager()
	{
		if (this._colorManager == null)
		{
			this._colorManager = new ColorManager();
		}

		return this._colorManager;
	}

	public static void logError(Exception e)
	{
		if (e instanceof CoreException)
			logError((CoreException) e);
		else
			getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	public static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	public static void trace(String string)
	{
		if (getDefault() != null && getDefault().isDebugging())
			getDefault().getLog().log(new Status(IStatus.OK, PLUGIN_ID, string));
	}

	public static void logError(String string, Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, string, e));
	}
	
	public static void logWarning(String message)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, null));
	}

	@Override
	protected ImageRegistry createImageRegistry()
	{
		ImageRegistry reg = super.createImageRegistry();
		reg.put(PENCIL_ICON, imageDescriptorFromPlugin(PLUGIN_ID, PENCIL_ICON));
		return reg;
	}

    public Image getImage(String path) {
        Image image = images.get(path);
        if (image == null) {
            ImageDescriptor id = getImageDescriptor(path);
            if (id == null) {
                return null;
            }

            image = id.createImage();
            images.put(path, image);
        }
        return image;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public ContributionTemplateStore getTemplateStore(ContextTypeRegistry contextTypeRegistry) {
        if (fTemplateStoreMap == null) {
            fTemplateStoreMap = new HashMap<ContextTypeRegistry, ContributionTemplateStore>();
        }
        ContributionTemplateStore store = fTemplateStoreMap.get(contextTypeRegistry);
        if (store == null) {
            store = new ContributionTemplateStore(contextTypeRegistry, getPreferenceStore(), TEMPLATES);
            try {
                store.load();
                fTemplateStoreMap.put(contextTypeRegistry, store);
            } catch (IOException e) {
                logError(e.getMessage(), e);
            }
        }
        return store;
    }
}
