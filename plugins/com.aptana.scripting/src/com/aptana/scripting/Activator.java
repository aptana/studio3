package com.aptana.scripting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.osgi.framework.BundleContext;

import com.aptana.scripting.keybindings.internal.KeybindingsManager;
import com.aptana.scripting.model.BundleChangeListener;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.RunType;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin
{
	public static final String PLUGIN_ID = "com.aptana.scripting"; //$NON-NLS-1$
	private static Activator plugin;

	/**
	 * Context id set by workbench part to indicate they are scripting aware.
	 */
	public static final String CONTEXT_ID = "com.aptana.scripting.context"; //$NON-NLS-1$

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
	 * This returns the default run type to be used by ScriptingEngine and CommandElement.
	 * 
	 * @return
	 */
	public static RunType getDefaultRunType()
	{
		return RunType.CURRENT_THREAD;
	}

	/**
	 * logError
	 * 
	 * @param msg
	 * @param e
	 */
	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	/**
	 * logInfo
	 * 
	 * @param string
	 */
	public static void logInfo(String string)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, string));
	}

	/**
	 * logWarning
	 * 
	 * @param msg
	 */
	public static void logWarning(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, msg));
	}

	/**
	 * trace
	 * 
	 * @param string
	 */
	public static void trace(String string)
	{
		getDefault().getLog().log(new Status(IStatus.OK, PLUGIN_ID, string));
	}

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
		BundleManager.getInstance().addBundleChangeListener(new BundleChangeListener()
		{

			@Override
			public void deleted(BundleElement bundle)
			{
				// nothing
			}

			@Override
			public void becameVisible(BundleEntry entry)
			{
				// Activate the file type associations
				List<String> fileTypes = getFileTypes(entry);
				for (String fileType : fileTypes)
				{
					IContentType type = Platform.getContentTypeManager().findContentTypeFor(
							fileType.replaceAll("\\*", "star")); //$NON-NLS-1$ //$NON-NLS-2$
					// TODO Make this much more intelligent! If we're associating a scope that is more specific than an
					// existing scope that is associated with a non-generic content type, we should associate with that
					// parent content type!
					// i.e. 'source.ruby.rspec' => '*.spec' should get associated to same content type that
					// 'source.ruby' did (the ruby content type).
					if (type == null)
					{
						type = Platform.getContentTypeManager().getContentType(BundleElement.GENERIC_CONTENT_TYPE_ID);

						try
						{
							int assocType = IContentType.FILE_NAME_SPEC;

							if (fileType.contains("*") && fileType.indexOf('.') != -1) //$NON-NLS-1$
							{
								assocType = IContentType.FILE_EXTENSION_SPEC;
								fileType = fileType.substring(fileType.indexOf('.') + 1);
							}

							type.addFileSpec(fileType, assocType);
						}
						catch (CoreException e)
						{
							Activator.logError(e.getMessage(), e);
						}
					}
				}
			}

			@Override
			public void becameHidden(BundleEntry entry)
			{
				// remove the file type associations
				List<String> fileTypes = getFileTypes(entry);
				for (String fileType : fileTypes)
				{
					IContentType type = Platform.getContentTypeManager().getContentType(
							BundleElement.GENERIC_CONTENT_TYPE_ID);
					try
					{
						int assocType = IContentType.FILE_NAME_SPEC;
						if (fileType.contains("*") && fileType.indexOf('.') != -1) //$NON-NLS-1$
						{
							assocType = IContentType.FILE_EXTENSION_SPEC;
							fileType = fileType.substring(fileType.indexOf('.') + 1);
						}
						type.removeFileSpec(fileType, assocType);
					}
					catch (CoreException e)
					{
						Activator.logError(e.getMessage(), e);
					}
				}
			}

			private List<String> getFileTypes(BundleEntry entry)
			{
				// TODO How do I properly grab the filetypes we need to associate?
				return new ArrayList<String>();
			}

			@Override
			public void added(BundleElement bundle)
			{
				// nothing
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			KeybindingsManager.uninstall();
			// Clean up the generic content type in bundle
			IContentType type = Platform.getContentTypeManager().getContentType(BundleElement.GENERIC_CONTENT_TYPE_ID);
			int[] specTypes = new int[] { IContentType.FILE_EXTENSION_SPEC, IContentType.FILE_NAME_SPEC };
			for (int specType : specTypes)
			{
				String[] specs = type.getFileSpecs(specType);
				for (String spec : specs)
				{
					type.removeFileSpec(spec, specType);
				}
			}
		}
		catch (Exception e)
		{
			// ignore
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}
}
