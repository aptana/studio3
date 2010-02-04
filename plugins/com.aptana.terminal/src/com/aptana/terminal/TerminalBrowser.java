package com.aptana.terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
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
	private static List<String> startDirectories = new ArrayList<String>(2);

	private Browser _browser;
	private WorkbenchPart _owningPart;
	private String _id;

	/**
	 * grabStartDirectory
	 * 
	 * @return
	 */
	private static String grabStartingDirectory()
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

	/**
	 * setStartingDirectory
	 * 
	 * @param startingDirectory
	 */
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
		// create browser control
		this._browser = new Browser(parent, SWT.NONE);
		
//		// create progress listener so we can set focus after a page has loaded
//		this._browser.addProgressListener(new ProgressListener()
//		{
//			public void changed(ProgressEvent event)
//			{
//			}
//
//			public void completed(ProgressEvent event)
//			{
//				_owningPart.setFocus();
//			}
//		});
		
		// create focus listener so we can enable/disable key bindings
		final IBindingService bindingService = (IBindingService) _owningPart.getSite().getService(IBindingService.class);
		
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
		return grabStartingDirectory();
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
