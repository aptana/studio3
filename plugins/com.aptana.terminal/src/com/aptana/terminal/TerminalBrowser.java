package com.aptana.terminal;

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

import com.aptana.terminal.server.TerminalServer;

public class TerminalBrowser
{
	private static final String TERMINAL_URL = "http://{0}:{1}/webterm/"; //$NON-NLS-1$

	private Browser _browser;
	private WorkbenchPart _owningPart;
	private String _id;
	private String _startingDirectory;

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
	 * createControl
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		// create browser control
		this._browser = new Browser(parent, SWT.NONE);
		
		// create focus listener so we can enable/disable key bindings
		final IBindingService bindingService = (IBindingService) this._owningPart.getSite().getService(IBindingService.class);
		
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
