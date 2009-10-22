package com.aptana.terminal.editor;

import java.util.UUID;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.EditorPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.server.HttpServer;

public class TerminalEditor extends EditorPart
{
	public static final String ID = "com.aptana.terminal.TerminalEditor";
	
	private static final String SHELL_KEY_BINDING_SCHEME = "com.aptana.terminal.scheme";
	private static final String TERMINAL_URL = "http://127.0.0.1:8181/webterm/";
	
	private Browser browser;
	private String id;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		try
		{
			if (this.browser != null)
			{
				this.browser.dispose();
				this.browser = null;
			}
		}
		catch (Exception e)
		{
		}
		
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		this.setSite(site);
		this.setInput(input);
		this.setPartName("Terminal");
		this.setTitleToolTip("Aptana Terminal");
		this.setTitleImage(Activator.getImage("icons/terminal.png"));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		this.id = UUID.randomUUID().toString();
		
		HttpServer.getInstance().createProcess(this.id);
		browser = new Browser(parent, SWT.NONE);
		browser.setUrl(TERMINAL_URL + "?id=" + this.id);
		
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(browser, ID);
			
		IPartService service = (IPartService) getSite().getService(IPartService.class);
		service.addPartListener(new IPartListener() {
			Scheme oldScheme = null;
		
			@Override
			public void partActivated(IWorkbenchPart part)
			{
				if (part == TerminalEditor.this)
				{
					try
					{
						IEditorSite editorSite = TerminalEditor.this.getEditorSite();
						BindingService bindingService = (BindingService) editorSite.getService(IBindingService.class);
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
				if (part == TerminalEditor.this)
				{
					//System.out.println("Shell brought to top");
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part)
			{
				if (part == TerminalEditor.this)
				{
					HttpServer.getInstance().removeProcess(TerminalEditor.this.id);
				}
			}

			@Override
			public void partDeactivated(IWorkbenchPart part)
			{
				if (part == TerminalEditor.this)
				{
					try
					{
						IEditorSite editorSite = TerminalEditor.this.getEditorSite();
						BindingService bindingService = (BindingService) editorSite.getService(IBindingService.class);
						Scheme scheme = bindingService.getScheme(SHELL_KEY_BINDING_SCHEME);
						
						bindingService.getBindingManager().setActiveScheme(oldScheme);
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
				if (part == TerminalEditor.this)
				{
					//System.out.println("Shell opened");
				}
			}
		});
	}

	@Override
	public void setFocus()
	{
		browser.setFocus();
	}
}
