package com.aptana.scripting.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.IPageBookViewPage;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.ColorManager;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.editor.common.theme.Theme;
import com.aptana.scripting.ScriptLogListener;
import com.aptana.scripting.ScriptLogger;

public class ScriptingConsole implements IStartup, IConsolePageParticipant
{
	private static final String TEXTFONT_PROPERTY = "org.eclipse.jface.textfont"; //$NON-NLS-1$
	private static final String CONSOLE_TRACE = "console.trace"; //$NON-NLS-1$
	private static final String CONSOLE_WARNING = "console.warning"; //$NON-NLS-1$
	private static final String CONSOLE_INFO = "console.info"; //$NON-NLS-1$
	private static final String CONSOLE_ERROR = "console.error"; //$NON-NLS-1$
	private static final String CONSOLE_OUTPUT = "console.output"; //$NON-NLS-1$
	private static final String CONSOLE_ICON_PATH = "icons/console.png"; //$NON-NLS-1$

	private static ScriptingConsole INSTANCE;
	private static MessageConsole console;
	private static MessageConsoleStream outputConsoleStream;
	private static MessageConsoleStream errorConsoleStream;
	private static MessageConsoleStream infoConsoleStream;
	private static MessageConsoleStream warningConsoleStream;
	private static MessageConsoleStream traceConsoleStream;

	private IPropertyChangeListener _fontChangeListener;
	private IPreferenceChangeListener _themeChangeListener;

	static
	{
		if (console == null)
		{
			console = new MessageConsole(Messages.EarlyStartup_SCRIPTING_CONSOLE_NAME,
					ScriptingUIPlugin.getImageDescriptor(CONSOLE_ICON_PATH));

			// create message streams
			outputConsoleStream = console.newMessageStream();
			errorConsoleStream = console.newMessageStream();
			infoConsoleStream = console.newMessageStream();
			warningConsoleStream = console.newMessageStream();
			traceConsoleStream = console.newMessageStream();

			// bring console into view when errors occur
			errorConsoleStream.setActivateOnWrite(true);

			// register our console with Eclipse
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		}
	}

	/**
	 * Return the singleton instance of ScriptConsole. Should be called on UI thread.
	 * 
	 * @return
	 */
	public static ScriptingConsole getDefault()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ScriptingConsole();

			INSTANCE.addListeners();
		}

		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#activated()
	 */
	public void activated()
	{
	}

	/**
	 * addListeners
	 */
	private void addListeners()
	{
		this.listenForFontChanges();
		this.listenForThemeChanges();
		this.listenForLoggingEvents();
	}

	/**
	 * applyTheme
	 */
	private void applyTheme()
	{
		IWorkbench workbench = null;

		try
		{
			workbench = PlatformUI.getWorkbench();
		}
		catch (IllegalStateException e)
		{
			CommonEditorPlugin.logError(e);
		}

		if (workbench != null)
		{
			final Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				public void run()
				{
					// set colors
					CommonEditorPlugin plugin = CommonEditorPlugin.getDefault();
					ColorManager colorManager = plugin.getColorManager();
					Theme theme = plugin.getThemeManager().getCurrentTheme();

					// set background color
					// NOTE: we have to force the background color to change; otherwise, even
					// with a forced redraw, the background will not be drawn
					console.setBackground(null);
					console.setBackground(colorManager.getColor(theme.getBackground()));

					// set font
					console.setFont(JFaceResources.getTextFont());

					// set stream colors
					// For CONSOLE_OUTPUT stream we should use the foreground color of the theme
					applyTheme(CONSOLE_OUTPUT, outputConsoleStream, colorManager.getColor(theme.getForeground()));
					applyTheme(CONSOLE_ERROR, errorConsoleStream, display.getSystemColor(SWT.COLOR_DARK_RED));
					applyTheme(CONSOLE_INFO, infoConsoleStream, display.getSystemColor(SWT.COLOR_DARK_BLUE));
					applyTheme(CONSOLE_WARNING, warningConsoleStream, display.getSystemColor(SWT.COLOR_DARK_YELLOW));
					applyTheme(CONSOLE_TRACE, traceConsoleStream, display.getSystemColor(SWT.COLOR_DARK_GREEN));

					refresh();
				}
			});
		}
	}

	/**
	 * applyTheme
	 * 
	 * @param name
	 * @param stream
	 * @param defaultColor
	 * @return
	 */
	private void applyTheme(String name, MessageConsoleStream stream, Color defaultColor)
	{
		Theme theme = CommonEditorPlugin.getDefault().getThemeManager().getCurrentTheme();
		Color color = defaultColor;
		int style = SWT.NONE;

		// grab theme values, if they exist
		if (theme.hasEntry(name))
		{
			TextAttribute attr = theme.getTextAttribute(name);

			color = theme.getForeground(name);
			style = attr.getStyle();
		}

		// apply new values
		stream.setColor(color);
		stream.setFontStyle(style);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#deactivated()
	 */
	public void deactivated()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * earlyStartup
	 */
	public void earlyStartup()
	{
		// force scripting console to be registered with the console manager
		getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	/**
	 * getErrorConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getErrorConsoleStream()
	{
		return errorConsoleStream;
	}

	/**
	 * getInfoConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getInfoConsoleStream()
	{
		return infoConsoleStream;
	}

	/**
	 * getOutputConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getOutputConsoleStream()
	{
		return outputConsoleStream;
	}

	/**
	 * getTraceConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getTraceConsoleStream()
	{
		return traceConsoleStream;
	}

	/**
	 * getWarningConsoleStream
	 * 
	 * @return
	 */
	MessageConsoleStream getWarningConsoleStream()
	{
		return warningConsoleStream;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#init(org.eclipse.ui.part.IPageBookViewPage,
	 * org.eclipse.ui.console.IConsole)
	 */
	public void init(IPageBookViewPage page, IConsole console)
	{
		String consoleName = console.getName();

		if (consoleName.equals(Messages.EarlyStartup_SCRIPTING_CONSOLE_NAME))
		{
			// apply theme
			this.applyTheme();
		}
	}

	/**
	 * listenForFontChanges
	 */
	private void listenForFontChanges()
	{
		// This is for the unit tests, need to not try and get font registry when not on UI thread
		if (Display.getCurrent() != null && JFaceResources.getFontRegistry() != null)
		{
			this._fontChangeListener = new IPropertyChangeListener()
			{
				@Override
				public void propertyChange(PropertyChangeEvent event)
				{
					if (event.getProperty().equals(TEXTFONT_PROPERTY))
					{
						applyTheme();
					}
				}
			};
			JFaceResources.getFontRegistry().addListener(this._fontChangeListener);
		}
	}

	/**
	 * listenForLoggingEvents
	 */
	private void listenForLoggingEvents()
	{
		// create our scripting log listener and register it
		ScriptLogger.getInstance().addLogListener(new ScriptLogListener()
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

			private String formatMessage(String message)
			{
				return "[" + this.getDateTimeStamp() + "] " + message; //$NON-NLS-1$ //$NON-NLS-2$
			}

			private String getDateTimeStamp()
			{
				return format.format(new Date());
			}

			public void logError(String error)
			{
				getErrorConsoleStream().println(this.formatMessage(error));
			}

			public void logInfo(String info)
			{
				getInfoConsoleStream().println(this.formatMessage(info));
			}

			public void logWarning(String warning)
			{
				getWarningConsoleStream().println(this.formatMessage(warning));
			}

			public void print(String message)
			{
				getOutputConsoleStream().println(message);
			}

			public void printError(String message)
			{
				getErrorConsoleStream().println(message);
			}

			public void trace(String message)
			{
				getTraceConsoleStream().println(this.formatMessage(message));
			}
		});
	}

	/**
	 * listenForThemeChanges
	 */
	private void listenForThemeChanges()
	{
		this._themeChangeListener = new IPreferenceChangeListener()
		{
			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
			}
		};

		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID)
				.addPreferenceChangeListener(this._themeChangeListener);
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		// refresh the display
		ConsolePlugin.getDefault().getConsoleManager().refresh(console);
	}
}
