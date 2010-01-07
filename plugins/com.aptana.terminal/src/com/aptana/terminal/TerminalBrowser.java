package com.aptana.terminal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.terminal.server.HttpServer;

@SuppressWarnings("restriction")
public class TerminalBrowser
{
	private static final String SHELL_KEY_BINDING_SCHEME = "com.aptana.terminal.scheme"; //$NON-NLS-1$
	private static final String TERMINAL_URL = "http://{0}:{1}/webterm/"; //$NON-NLS-1$

	private Browser _browser;
	private WorkbenchPart _owningPart;
	private String _id;
	private IServiceLocator _serviceLocator;
	private IPreferenceChangeListener _themeChangeListener;

	private static List<String> startDirectories = new ArrayList<String>(2);

	public static void setStartingDirectory(String startingDirectory)
	{
		synchronized (startDirectories)
		{
			startDirectories.add(startingDirectory);
		}
	}

	private static String grabStartDirectory()
	{
		synchronized (startDirectories)
		{
			if (!startDirectories.isEmpty())
				return startDirectories.remove(0);
		}
		return null;
	}

	/**
	 * TerminalBrowser
	 * 
	 * @param owningPart
	 */
	public TerminalBrowser(WorkbenchPart owningPart)
	{
		this._owningPart = owningPart;
		this._serviceLocator = owningPart.getSite();
		this._id = UUID.randomUUID().toString();
	}

	/**
	 * createControl
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		this._browser = new Browser(parent, SWT.NONE);

		HttpServer.getInstance().createProcess(this._id, this.getStartingDirectory());
		String url = NLS.bind(TERMINAL_URL, new Object[] { HttpServer.getInstance().getHost(),
				HttpServer.getInstance().getPort() })
				+ "?id=" + this._id; //$NON-NLS-1$
		this.setUrl(url);

		IPartService service = (IPartService) this._owningPart.getSite().getService(IPartService.class);
		service.addPartListener(new IPartListener()
		{
			Scheme oldScheme = null;

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
			 */
			public void partActivated(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					// System.out.println("Activating shell scheme");

					try
					{
						BindingService bindingService = (BindingService) _serviceLocator
								.getService(IBindingService.class);
						Scheme currentScheme = bindingService.getActiveScheme();
						Scheme scheme = bindingService.getScheme(SHELL_KEY_BINDING_SCHEME);

						// NOTE: During debugging I saw two activation events in a row with no
						// interleaved deactivate. That could cause oldScheme to be overwritten
						// with our shell scheme, so we now only save the old scheme if we don't
						// have one defined already.
						if (oldScheme == null)
						{
							oldScheme = currentScheme;
						}
						// FIXME This getBidningManager method doesn't exist in 3.4!
						bindingService.getBindingManager().setActiveScheme(scheme);
					}
					catch (NotDefinedException e)
					{
						String message = MessageFormat.format(
								Messages.TerminalBrowser_Key_Binding_Scheme_Does_Not_Exist,
								new Object[] { SHELL_KEY_BINDING_SCHEME });

						Activator.logError(message, e);
					}
				}
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
			 */
			public void partBroughtToTop(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					// System.out.println("Shell brought to top");
				}
			}

			public void partClosed(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					HttpServer.getInstance().removeProcess(TerminalBrowser.this._id);
				}
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
			 */
			public void partDeactivated(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					// System.out.println("Deactivating shell scheme");

					try
					{
						if (oldScheme != null)
						{
							BindingService bindingService = (BindingService) _serviceLocator
									.getService(IBindingService.class);

							// re-activate original key binding scheme
							bindingService.getBindingManager().setActiveScheme(oldScheme);

							// erase reference to scheme
							oldScheme = null;
						}
					}
					catch (NotDefinedException e)
					{
						Activator.logError(Messages.TerminalBrowser_Unable_To_Restore_Key_Binding, e);
					}
				}
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
			 */
			public void partOpened(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					// System.out.println("Shell opened");
				}
			}
		});
		// Force refresh of CSS when theme changes
		_themeChangeListener = new IPreferenceChangeListener()
		{

			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (_browser == null)
					return;
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					Display display = Display.getCurrent();
					
					if (display != null)
					{
						display.syncExec(new Runnable()
						{
	
							@Override
							public void run()
							{
								final String reloadCSSScript = "s = document.getElementById('ss');\n" //$NON-NLS-1$
										+ "var h=s.href.replace(/(&|\\?)forceReload=d /,'');\n" //$NON-NLS-1$
										+ "s.href=h+(h.indexOf('?')>=0?'&':'?')+'forceReload='+(new Date().valueOf());"; //$NON-NLS-1$
								_browser.execute(reloadCSSScript);
							}
						});
					}
				}
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(_themeChangeListener);
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		try
		{
			if (this._themeChangeListener != null)
			{
				new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(
						_themeChangeListener);
				_themeChangeListener = null;
			}
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
	 * getStartingDirectory
	 * 
	 * @return
	 */
	private String getStartingDirectory()
	{
		return grabStartDirectory();
	}

	/**
	 * setFocus
	 */
	public void setFocus()
	{
		if (this._browser != null)
		{
			this._browser.setFocus();
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

	public String getId()
	{
		return _id;
	}
}
