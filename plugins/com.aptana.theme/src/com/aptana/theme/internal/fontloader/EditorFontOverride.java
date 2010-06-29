package com.aptana.theme.internal.fontloader;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.themes.IThemeManager;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.theme.ThemePlugin;

/**
 * Copies the included font to the plugin state location. Then sets the theme to a custom one we've added which sets the
 * text editor and block selection fonts to Inconsolata-14. Once we've forced our theme, store a pref boolean so we
 * don't re-force the theme again.
 * 
 * @author cwilliams
 */
public class EditorFontOverride extends UIJob
{

	private static final String BLOCK_SELECTION_FONT_ID = "org.eclipse.ui.workbench.texteditor.blockSelectionModeFont";//$NON-NLS-1$

	/**
	 * Pref key used to determine if we've already forced the editor font to Inconsolata.
	 */
	protected static final String FORCED_EDITOR_FONT = "FORCED_EDITOR_FONT"; //$NON-NLS-1$

	/**
	 * ID of the custom Eclipse theme we use to override the fonts.
	 */
	private static final String CUSTOM_THEME_ID = "com.aptana.theme"; //$NON-NLS-1$

	/**
	 * Face name of custom font we use.
	 */
	private static final String CUSTOM_FONT_FACE_NAME = "Inconsolata"; //$NON-NLS-1$

	public EditorFontOverride()
	{
		super(MessageFormat.format("Default editor font to {0}", CUSTOM_FONT_FACE_NAME)); //$NON-NLS-1$
		setSystem(true);
		setPriority(Job.SHORT);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		// Add a listener to track the changes to JFaceResources.TEXT_FONT
		final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		themeManager.addPropertyChangeListener(new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				// Is our theme in effect
				if (themeManager.getCurrentTheme().getId().equals(CUSTOM_THEME_ID))
				{
					String property = event.getProperty();
					if (JFaceResources.TEXT_FONT.equals(property))
					{
						FontData[] newTextFontData = (FontData[]) event.getNewValue();
						FontData[] blockSelectionModeFontData = themeManager.getCurrentTheme().getFontRegistry()
								.getFontData(BLOCK_SELECTION_FONT_ID);
						if (blockSelectionModeFontData.length > 0 && newTextFontData.length > 0
								&& !blockSelectionModeFontData[0].equals(newTextFontData[0]))
						{
							themeManager.getCurrentTheme().getFontRegistry()
									.put(BLOCK_SELECTION_FONT_ID, newTextFontData);
							// Set the font data and save it to the workbench preferences.
							// We have to save it directly on 'org.eclipse.ui.workbench', otherwise, it does not
							// preserve the state on the next Studio run.
							IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.ui.workbench");//$NON-NLS-1$
							prefs.put(CUSTOM_THEME_ID + '.' + BLOCK_SELECTION_FONT_ID, newTextFontData[0].toString());
							prefs.put(CUSTOM_THEME_ID + '.' + JFaceResources.TEXT_FONT, newTextFontData[0].toString());
							try
							{
								prefs.flush();
							}
							catch (BackingStoreException e)
							{
								// ignore this
							}
						}
					}
				}
			}
		});
		IEclipsePreferences prefs = new InstanceScope().getNode(ThemePlugin.PLUGIN_ID);
		boolean alreadyForcedFont = prefs.getBoolean(FORCED_EDITOR_FONT, false);

		if (alreadyForcedFont)
			return Status.OK_STATUS;

		FontData[] fontList = PlatformUI.getWorkbench().getDisplay().getFontList(CUSTOM_FONT_FACE_NAME, true);
		if (fontList.length > 0)
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
				ThemePlugin.logError(e);
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean shouldRun()
	{
		return isStandalone();
	}

	@Override
	public boolean shouldSchedule()
	{
		return EclipseUtil.isStandalone();
	}

	private boolean isStandalone()
	{
		return EclipseUtil.isStandalone();
	}
}
