/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.internal.QuickFixProcessorsRegistry;
import com.aptana.editor.common.internal.scripting.ContentTypeTranslation;
import com.aptana.editor.common.internal.scripting.DocumentScopeManager;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.spelling.SpellingPreferences;
import com.aptana.index.core.IndexPlugin;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;
import com.aptana.usage.UsagePlugin;
import com.aptana.usage.preferences.IPreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonEditorPlugin extends AbstractUIPlugin
{

	public static final String SNIPPET = "/icons/snippet.png"; //$NON-NLS-1$
	public static final String COMMAND = "/icons/command.png"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.common"; //$NON-NLS-1$

	private static final String TEMPLATES = PLUGIN_ID + ".templates"; //$NON-NLS-1$

	private static final String OUTLINE_VIEW_ID = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.aptana.editor.common.commands.toggleOutline"; //$NON-NLS-1$
	private static final String COMMAND_STATE = "org.eclipse.ui.commands.toggleState"; //$NON-NLS-1$

	private static final String UID = CorePlugin.getDefault() != null ? Platform.getPreferencesService().getString(
			UsagePlugin.PLUGIN_ID, IPreferenceConstants.P_IDE_ID, null, null) : null;

	// The shared instance
	private static CommonEditorPlugin plugin;

	private Map<ContextTypeRegistry, ContributionTemplateStore> fTemplateStoreMap;
	private FilenameDifferentiator differentiator;

	private final IPartListener fPartListener = new IPartListener()
	{

		public void partActivated(IWorkbenchPart part)
		{
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
		}

		public void partClosed(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				IEditorPart editorPart = (IEditorPart) part;
				String id = editorPart.getEditorSite().getId();
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("instance", part.toString()); //$NON-NLS-1$
				if (UID != null)
				{
					payload.put("uid", UID); //$NON-NLS-1$
				}
				StudioAnalytics.getInstance()
						.sendEvent(new FeatureEvent("editor.closed" + getLastSegment(id), payload)); //$NON-NLS-1$
			}
		}

		public void partDeactivated(IWorkbenchPart part)
		{
		}

		public void partOpened(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				IEditorPart editorPart = (IEditorPart) part;
				String id = editorPart.getEditorSite().getId();
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("instance", part.toString()); //$NON-NLS-1$
				if (UID != null)
				{
					payload.put("uid", UID); //$NON-NLS-1$
				}
				StudioAnalytics.getInstance()
						.sendEvent(new FeatureEvent("editor.opened" + getLastSegment(id), payload)); //$NON-NLS-1$
			}
		}

		private String getLastSegment(String id)
		{
			if (id == null)
			{
				return StringUtil.EMPTY;
			}
			int index = id.lastIndexOf("."); //$NON-NLS-1$
			return (index < 0) ? "." + id : id.substring(index); //$NON-NLS-1$
		}
	};

	private final IPerspectiveListener fPerspectiveListener = new IPerspectiveListener()
	{

		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
		{
			setCommandState(findView(page, OUTLINE_VIEW_ID) != null);
		}

		public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId)
		{
			if (changeId.equals(IWorkbenchPage.CHANGE_VIEW_HIDE))
			{
				if (findView(page, OUTLINE_VIEW_ID) == null)
				{
					setCommandState(false);
				}
			}
			else if (changeId.equals(IWorkbenchPage.CHANGE_VIEW_SHOW))
			{
				if (findView(page, OUTLINE_VIEW_ID) != null)
				{
					setCommandState(true);
				}
			}
		}

		private void setCommandState(boolean state)
		{
			ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
			Command command = service.getCommand(COMMAND_ID);
			State commandState = command.getState(COMMAND_STATE);
			if (((Boolean) commandState.getValue()) != state)
			{
				commandState.setValue(state);
				service.refreshElements(COMMAND_ID, null);
			}
		}

		protected IViewReference findView(IWorkbenchPage page, String viewId)
		{
			for (IViewReference ref : page.getViewReferences())
			{
				if (viewId.equals(ref.getId()))
				{
					return ref;
				}
			}
			return null;
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
			window.removePerspectiveListener(fPerspectiveListener);
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
			window.addPerspectiveListener(fPerspectiveListener);
		}
	};

	private DocumentScopeManager fDocumentScopeManager;
	private IPreferenceChangeListener fThemeChangeListener;
	private SpellingPreferences spellingPreferences;
	private IQuickFixProcessorsRegistry quickFixRegistry;

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

		// Update occurrence colors
		listenForThemeChanges();

		// Activate indexing
		// FIXME Why can't we just have the indexing plugin load lazily on-demand?
		IndexPlugin.getDefault();

		differentiator = new FilenameDifferentiator();
		differentiator.schedule();
		// FIXME initialize spelling preferences lazily
		spellingPreferences = new SpellingPreferences();

		new UIJob("adding part listener") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				addPartListener();
				return Status.OK_STATUS;
			}
		}.schedule();

	}

	/**
	 * Hook up a listener for theme changes, and change the PHP occurrence colors!
	 */
	private void listenForThemeChanges()
	{
		Job job = new UIJob("Set occurrence colors to theme") //$NON-NLS-1$
		{
			private void setOccurrenceColors()
			{
				IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode("org.eclipse.ui.editors"); //$NON-NLS-1$
				Theme theme = ThemePlugin.getDefault().getThemeManager().getCurrentTheme();

				prefs.put("OccurrenceIndicationColor", StringConverter.asString(theme.getSearchResultColor())); //$NON-NLS-1$

				try
				{
					prefs.flush();
				}
				catch (BackingStoreException e)
				{
					// ignore
				}
			}

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				fThemeChangeListener = new IPreferenceChangeListener()
				{
					public void preferenceChange(PreferenceChangeEvent event)
					{
						if (event.getKey().equals(IThemeManager.THEME_CHANGED))
						{
							setOccurrenceColors();
						}
					}
				};

				setOccurrenceColors();

				EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID)
						.addPreferenceChangeListener(fThemeChangeListener);

				return Status.OK_STATUS;
			}
		};

		EclipseUtil.setSystemForJob(job);
		job.schedule(2000);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			if (fThemeChangeListener != null)
			{
				EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID)
						.removePreferenceChangeListener(fThemeChangeListener);

				fThemeChangeListener = null;
			}

			differentiator.dispose();

			removePartListener();

			if (fDocumentScopeManager != null)
			{
				fDocumentScopeManager.dispose();
			}
			if (spellingPreferences != null)
			{
				spellingPreferences.dispose();
				spellingPreferences = null;
			}
		}
		finally
		{
			fDocumentScopeManager = null;
			differentiator = null;
			quickFixRegistry = null;
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

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		reg.put(SNIPPET, imageDescriptorFromPlugin(PLUGIN_ID, SNIPPET));
		reg.put(COMMAND, imageDescriptorFromPlugin(PLUGIN_ID, COMMAND));
	}

	public Image getImageFromImageRegistry(String imageID)
	{
		return getImageRegistry().get(imageID);
	}

	/**
	 * @return the spellingPreferences
	 */
	public SpellingPreferences getSpellingPreferences()
	{
		return spellingPreferences;
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
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			}
		}
		return store;
	}

	public synchronized IDocumentScopeManager getDocumentScopeManager()
	{
		if (fDocumentScopeManager == null)
		{
			fDocumentScopeManager = new DocumentScopeManager();
		}
		return fDocumentScopeManager;
	}

	public IContentTypeTranslator getContentTypeTranslator()
	{
		return ContentTypeTranslation.getDefault();
	}

	private void addPartListener()
	{
		try
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null)
			{
				IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
				IPartService partService;
				for (IWorkbenchWindow window : windows)
				{
					partService = window.getPartService();
					if (partService != null)
					{
						partService.addPartListener(fPartListener);
					}
					window.addPerspectiveListener(fPerspectiveListener);
				}

				// Listen on any future windows
				PlatformUI.getWorkbench().addWindowListener(fWindowListener);
			}
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
	}

	private void removePartListener()
	{
		IWorkbench workbench = null;
		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (Exception e)
		{
			// ignore, may be running headless, like in tests
		}
		if (workbench != null)
		{
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			IPartService partService;
			for (IWorkbenchWindow window : windows)
			{
				partService = window.getPartService();
				if (partService != null)
				{
					partService.removePartListener(fPartListener);
				}
				window.removePerspectiveListener(fPerspectiveListener);
			}
			PlatformUI.getWorkbench().removeWindowListener(fWindowListener);
		}
	}

	public synchronized IQuickFixProcessorsRegistry getQuickFixProcessorRegistry()
	{
		if (quickFixRegistry == null)
		{
			quickFixRegistry = new QuickFixProcessorsRegistry();
		}
		return quickFixRegistry;
	}
}