package com.aptana.terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.WorkbenchPart;

import com.aptana.terminal.server.HttpServer;

public class TerminalBrowser
{
	private static final String TERMINAL_URL = "http://{0}:{1}/webterm/"; //$NON-NLS-1$

	private Browser _browser;
	private WorkbenchPart _owningPart;
	private String _id;

	private static List<String> startDirectories = new ArrayList<String>(2);

	private static String grabStartDirectory()
	{
		synchronized (startDirectories)
		{
			if (!startDirectories.isEmpty())
			{
				return startDirectories.remove(0);
			}
		}

		return null;
	}

	public static void setStartingDirectory(String startingDirectory)
	{
		synchronized (startDirectories)
		{
			startDirectories.add(startingDirectory);
		}
	}

	/**
	 * TerminalBrowser
	 * 
	 * @param owningPart
	 */
	public TerminalBrowser(WorkbenchPart owningPart)
	{
		this._owningPart = owningPart;
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

		final IBindingService bindingService = (IBindingService) _owningPart.getSite()
				.getService(IBindingService.class);
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
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
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
}
