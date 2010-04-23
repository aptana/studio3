package com.aptana.editor.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.editor.common.internal.scripting.ContentTypeTranslation;
import com.aptana.editor.common.internal.scripting.DocumentScopeManager;
import com.aptana.editor.common.internal.theme.ThemeManager;
import com.aptana.editor.common.internal.theme.fontloader.EditorFontOverride;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.theme.ColorManager;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.usage.EventLogger;
import com.aptana.index.core.IndexActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonEditorPlugin extends AbstractUIPlugin
{

	public static final String PENCIL_ICON = "icons/pencil.png"; //$NON-NLS-1$
	public static final String SNIPPET = "/icons/snippet.png"; //$NON-NLS-1$
	public static final String COMMAND = "/icons/command.png"; //$NON-NLS-1$
	public static final String IBEAM_BLACK = "/icons/ibeam-black.gif"; //$NON-NLS-1$
	public static final String IBEAM_WHITE = "/icons/ibeam-white.gif"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.common"; //$NON-NLS-1$

	private static final String TEMPLATES = PLUGIN_ID + ".templates"; //$NON-NLS-1$

	// The shared instance
	private static CommonEditorPlugin plugin;

	private ColorManager fColorManager;
	private Map<ContextTypeRegistry, ContributionTemplateStore> fTemplateStoreMap;
	private InvasiveThemeHijacker themeHijacker;
	private FilenameDifferentiator differentiator;

	private final IPartListener fPartListener = new IPartListener()
	{

		@Override
		public void partActivated(IWorkbenchPart part)
		{
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		@Override
		public void partClosed(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				IEditorPart editorPart = (IEditorPart) part;
				EventLogger.getInstance().logEvent("editor.closed", editorPart.getEditorSite().getId()); //$NON-NLS-1$
			}
		}

		@Override
		public void partDeactivated(IWorkbenchPart part)
		{
		}

		@Override
		public void partOpened(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				IEditorPart editorPart = (IEditorPart) part;
				EventLogger.getInstance().logEvent("editor.opened", editorPart.getEditorSite().getId()); //$NON-NLS-1$
			}
		}
	};

	private final IWindowListener fWindowListener = new IWindowListener()
	{

		public void windowActivated(IWorkbenchWindow window)
		{
		}

		public void windowClosed(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.removePartListener(fPartListener);
			}
		}

		public void windowDeactivated(IWorkbenchWindow window)
		{
		}

		public void windowOpened(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.addPartListener(fPartListener);
			}
		}
	};

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

		// Activate indexing
		IndexActivator.getDefault();
		
		new EditorFontOverride().schedule();
		themeHijacker = new InvasiveThemeHijacker();
		themeHijacker.schedule();
		
		differentiator = new FilenameDifferentiator();
		differentiator.schedule();

		addPartListener();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			if (fColorManager != null)
				fColorManager.dispose();

			IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
			prefs.removePreferenceChangeListener(themeHijacker);
			differentiator.dispose();

			removePartListener();
		}
		finally
		{
			themeHijacker = null;
			fColorManager = null;
			differentiator = null;
			plugin = null;
			super.stop(context);
		}
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
		if (this.fColorManager == null)
		{
			this.fColorManager = new ColorManager();
		}

		return this.fColorManager;
	}

	public IThemeManager getThemeManager()
	{
		return ThemeManager.instance();
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
	
	public static void logInfo(String message)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message, null));
	}

	@Override
	protected ImageRegistry createImageRegistry()
	{
		ImageRegistry reg = super.createImageRegistry();
		reg.put(PENCIL_ICON, imageDescriptorFromPlugin(PLUGIN_ID, PENCIL_ICON));
		return reg;
	}

	public Image getImage(String path)
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

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		reg.put(PENCIL_ICON, imageDescriptorFromPlugin(PLUGIN_ID, PENCIL_ICON));
		reg.put(SNIPPET, imageDescriptorFromPlugin(PLUGIN_ID, SNIPPET));
		reg.put(COMMAND, imageDescriptorFromPlugin(PLUGIN_ID, COMMAND));
		reg.put(IBEAM_BLACK, imageDescriptorFromPlugin(PLUGIN_ID, IBEAM_BLACK));
		reg.put(IBEAM_WHITE, imageDescriptorFromPlugin(PLUGIN_ID, IBEAM_WHITE));
	}

	public Image getImageFromImageRegistry(String imageID)
	{
		return getImageRegistry().get(imageID);
	}

	public ContributionTemplateStore getTemplateStore(ContextTypeRegistry contextTypeRegistry)
	{
		if (fTemplateStoreMap == null)
		{
			fTemplateStoreMap = new HashMap<ContextTypeRegistry, ContributionTemplateStore>();
		}
		ContributionTemplateStore store = fTemplateStoreMap.get(contextTypeRegistry);
		if (store == null)
		{
			store = new ContributionTemplateStore(contextTypeRegistry, getPreferenceStore(), TEMPLATES);
			try
			{
				store.load();
				fTemplateStoreMap.put(contextTypeRegistry, store);
			}
			catch (IOException e)
			{
				logError(e.getMessage(), e);
			}
		}
		return store;
	}

	public IDocumentScopeManager getDocumentScopeManager()
	{
		return DocumentScopeManager.getInstance();
	}

	public IContentTypeTranslator getContentTypeTranslator()
	{
		return ContentTypeTranslation.getDefault();
	}

	private void addPartListener()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		IPartService partService;
		for (IWorkbenchWindow window : windows)
		{
			partService = window.getPartService();
			if (partService != null)
			{
				partService.addPartListener(fPartListener);
			}
		}

		// Listen on any future windows
		PlatformUI.getWorkbench().addWindowListener(fWindowListener);
	}

	private void removePartListener()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		IPartService partService;
		for (IWorkbenchWindow window : windows)
		{
			partService = window.getPartService();
			if (partService != null)
			{
				partService.removePartListener(fPartListener);
			}
		}
		PlatformUI.getWorkbench().removeWindowListener(fWindowListener);
	}
}
