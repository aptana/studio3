package com.aptana.editor.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Copies the included font to the plugin state location. Then sets the theme to a custom one we've added which sets the
 * text editor and block selection fonts to Inconsolata-14. Once we've forced our theme, store a pref boolean so we
 * don't re-force the theme again.
 * 
 * @author cwilliams
 */
class EditorFontOverride extends UIJob
{

	/**
	 * Filename of the font we're using.
	 */
	private static final String FONT_FILE = "Inconsolata.otf"; //$NON-NLS-1$

	/**
	 * Pref key used to determine if we've already forced the editor font to Inconsolata.
	 */
	protected static final String FORCED_EDITOR_FONT = "FORCED_EDITOR_FONT"; //$NON-NLS-1$

	/**
	 * ID of the custom Eclipse theme we use to override the fonts.
	 */
	private static final String CUSTOM_THEME_ID = "com.aptana.editor.common.theme"; //$NON-NLS-1$

	public EditorFontOverride()
	{
		super("Default editor font to Inconsolata"); //$NON-NLS-1$
		setSystem(true);
		setPriority(Job.SHORT);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		IPath targetPath = CommonEditorPlugin.getDefault().getStateLocation().append(FONT_FILE);
		if (!targetPath.toFile().exists())
		{
			copyFontToStateLocation(targetPath);
		}

		IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		boolean alreadyForcedFont = prefs.getBoolean(FORCED_EDITOR_FONT, false);

		if (alreadyForcedFont)
			return Status.OK_STATUS;

		boolean isFontLoaded = Display.getCurrent().loadFont(targetPath.toOSString());
		if (isFontLoaded)
		{
			PlatformUI.getWorkbench().getThemeManager().setCurrentTheme(CUSTOM_THEME_ID);
			// Store a pref key to remember that we've already force and overridden the font!
			prefs.putBoolean(FORCED_EDITOR_FONT, true);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				CommonEditorPlugin.logError(e);
			}
		}
		return Status.OK_STATUS;
	}

	private void copyFontToStateLocation(IPath targetPath)
	{
		// Copy font out of the JARred plugin and stick it in the plugin state location.
		InputStream stream = null;
		FileOutputStream out = null;
		try
		{
			stream = FileLocator.openStream(CommonEditorPlugin.getDefault().getBundle(), new Path(FONT_FILE), false);
			out = new FileOutputStream(targetPath.toFile());
			int b = -1;
			while ((b = stream.read()) != -1)
			{
				out.write(b);
			}
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
		finally
		{
			try
			{
				if (out != null)
					out.close();
			}
			catch (IOException e)
			{
				// ignore
			}
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

}
