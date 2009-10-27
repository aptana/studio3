package com.aptana.terminal;

import java.util.UUID;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.terminal.server.HttpServer;

public class TerminalBrowser
{
	private static final String SHELL_KEY_BINDING_SCHEME = "com.aptana.terminal.scheme"; //$NON-NLS-1$
	private static final String TERMINAL_URL = "http://127.0.0.1:8181/webterm/"; //$NON-NLS-1$
	
	private Browser _browser;
	private WorkbenchPart _owningPart;
	private String _id;
	private IServiceLocator _serviceLocator;
	
	/**
	 * TerminalBrowser
	 * 
	 * @param owningPart
	 */
	public TerminalBrowser(WorkbenchPart owningPart)
	{
		this._owningPart = owningPart;
//		this._serviceLocator = serviceLocator;
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
		
		HttpServer.getInstance().createProcess(this._id);
		
		this.setUrl(TERMINAL_URL + "?id=" + this._id); //$NON-NLS-1$
		
		IPartService service = (IPartService) this._owningPart.getSite().getService(IPartService.class);
		service.addPartListener(new IPartListener() {
			Scheme oldScheme = null;
		
			@Override
			public void partActivated(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					//System.out.println("Activating shell scheme");
					
					try
					{
						BindingService bindingService = (BindingService) _serviceLocator.getService(IBindingService.class);
						oldScheme = bindingService.getBindingManager().getActiveScheme();
						
						Scheme scheme = bindingService.getScheme(SHELL_KEY_BINDING_SCHEME);
						bindingService.getBindingManager().setActiveScheme(scheme);
					}
					catch (NotDefinedException e)
					{
						e.printStackTrace();
					}
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					//System.out.println("Shell brought to top");
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					HttpServer.getInstance().removeProcess(TerminalBrowser.this._id);
				}
			}

			@Override
			public void partDeactivated(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					//System.out.println("Deactivating shell scheme");
					
					try
					{
						BindingService bindingService = (BindingService) _serviceLocator.getService(IBindingService.class);
						Scheme scheme = bindingService.getScheme(SHELL_KEY_BINDING_SCHEME);
						
						if (oldScheme != null) {
							bindingService.getBindingManager().setActiveScheme(oldScheme);
						}
					}
					catch (NotDefinedException e)
					{
						e.printStackTrace();
					}
				}
			}

			@Override
			public void partOpened(IWorkbenchPart part)
			{
				if (part == TerminalBrowser.this._owningPart)
				{
					//System.out.println("Shell opened");
				}
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
