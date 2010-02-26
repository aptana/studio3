package com.aptana.terminal;

import java.util.UUID;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.WorkbenchPart;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.terminal.server.TerminalServer;

public class TerminalBrowser
{
	private static final String TERMINAL_URL = "http://{0}:{1}/webterm/"; //$NON-NLS-1$
	private static final String TEXTFONT_PROPERTY = "org.eclipse.jface.textfont"; //$NON-NLS-1$

	private Browser _browser;
	private WorkbenchPart _owningPart;
	private String _id;
	private String _startingDirectory;
	private IPreferenceChangeListener _themeChangeListener;
	private IPropertyChangeListener _fontChangeListener;

	/**
	 * TerminalBrowser
	 * 
	 * @param owningPart
	 */
	public TerminalBrowser(WorkbenchPart owningPart)
	{
		this(owningPart, null);
	}

	/**
	 * TerminalBrowser
	 * 
	 * @param owningPart
	 * @param startingDirectory
	 */
	public TerminalBrowser(WorkbenchPart owningPart, String startingDirectory)
	{
		this._owningPart = owningPart;
		this._startingDirectory = startingDirectory;
		this._id = UUID.randomUUID().toString();
	}

	/**
	 * applyTheme
	 */
	private void applyTheme()
	{
		try
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				@Override
				public void run()
				{
					_browser.execute("onThemeChange()"); //$NON-NLS-1$
				}
			});
		}
		catch (IllegalStateException e)
		{
			// do nothing
		}
	}

	/**
	 * createControl
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		// create browser control
		this._browser = new Browser(parent, SWT.NONE);

		// create focus listener so we can enable/disable key bindings
		final IBindingService bindingService = (IBindingService) this._owningPart.getSite().getService(
				IBindingService.class);

		this._browser.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				bindingService.setKeyFilterEnabled(false);
			}

			public void focusLost(FocusEvent e)
			{
				bindingService.setKeyFilterEnabled(true);
			}
		});

		// Force refresh of CSS when theme changes
		this._themeChangeListener = new IPreferenceChangeListener()
		{
			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (_browser != null && event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(_themeChangeListener);

		// refresh CSS when theme's font changes
		this._fontChangeListener = new IPropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				if (_browser != null && event.getProperty().equals(TEXTFONT_PROPERTY))
				{
					applyTheme();
				}
			}
		};
		JFaceResources.getFontRegistry().addListener(this._fontChangeListener);

		// create our supporting process for access to the system's shell
		TerminalServer.getInstance().createProcess(this._id, this.getStartingDirectory());

		// load the terminal
		String url = NLS.bind(TERMINAL_URL, new Object[] { TerminalServer.getInstance().getHost(),
				TerminalServer.getInstance().getPort() })
				+ "?id=" + this._id; //$NON-NLS-1$
		this.setUrl(url);
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		if (this._themeChangeListener != null)
		{
			new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(
					_themeChangeListener);
			this._themeChangeListener = null;
		}

		if (this._fontChangeListener != null)
		{
			JFaceResources.getFontRegistry().removeListener(this._fontChangeListener);
			this._fontChangeListener = null;
		}

		try
		{
			if (this._browser != null)
			{
				this._browser.dispose();
				this._browser = null;
			}
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * getControl
	 * 
	 * @return
	 */
	public Control getControl()
	{
		return this._browser;
	}

	/**
	 * getId
	 * 
	 * @return
	 */
	public String getId()
	{
		return _id;
	}

	/**
	 * getStartingDirectory
	 * 
	 * @return
	 */
	private String getStartingDirectory()
	{
		return this._startingDirectory;
	}

	/**
	 * setFocus
	 */
	public void setFocus()
	{
		if (this._browser != null)
		{
			this._browser.setFocus();
			this._browser.execute("window.focus();");
		}
	}

	/**
	 * setUrl
	 * 
	 * @param string
	 */
	private void setUrl(String string)
	{
		if (this._browser != null)
		{
			this._browser.setUrl(string);
		}
	}
}
