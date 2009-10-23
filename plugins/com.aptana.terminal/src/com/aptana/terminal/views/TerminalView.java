package com.aptana.terminal.views;

import java.util.UUID;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.ViewPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.Utils;
import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.server.HttpServer;

public class TerminalView extends ViewPart
{
	private static final String SHELL_KEY_BINDING_SCHEME = "com.aptana.terminal.scheme";
	private static final String TERMINAL_URL = "http://127.0.0.1:8181/webterm/";
	
	public static final String ID = "com.aptana.terminal.views.TerminalView";
	
	private Browser browser;
	private String id;
	private Action openEditor;

	/**
	 * The constructor.
	 */
	public TerminalView()
	{
	}

	/**
	 * contributeToActionBars
	 */
	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
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
				if (part == TerminalView.this)
				{
					//System.out.println("Activating shell scheme");
					
					try
					{
						BindingService bindingService = (BindingService) getViewSite().getService(IBindingService.class);
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
				if (part == TerminalView.this)
				{
					//System.out.println("Shell brought to top");
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part)
			{
				if (part == TerminalView.this)
				{
					HttpServer.getInstance().removeProcess(TerminalView.this.id);
				}
			}

			@Override
			public void partDeactivated(IWorkbenchPart part)
			{
				if (part == TerminalView.this)
				{
					//System.out.println("Deactivating shell scheme");
					
					try
					{
						BindingService bindingService = (BindingService) getViewSite().getService(IBindingService.class);
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
				if (part == TerminalView.this)
				{
					//System.out.println("Shell opened");
				}
			}
		});
		
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	/**
	 * fillContextMenu
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(openEditor);
		
		// Other plug-ins can contribute there actions here
		
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * fillLocalPullDown
	 * 
	 * @param manager
	 */
	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(openEditor);
	}

	/**
	 * fillLocalToolBar
	 * 
	 * @param manager
	 */
	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(openEditor);
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				TerminalView.this.fillContextMenu(manager);
			}
		});
		
		Menu menu = menuMgr.createContextMenu(browser);
		browser.setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
	}

	
	/**
	 * init
	 */
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		HttpServer server = HttpServer.getInstance();
		
		if (server != null)
		{
			System.out.println("Server started");
		}
		else
		{
			System.out.println("Server could not start");
		}
	}

	/**
	 * makeActions
	 */
	private void makeActions()
	{
		ImageDescriptor icon = Activator.getImageDescriptor("/icons/terminal.png");
		
		openEditor = new Action()
		{
			public void run()
			{
				Utils.openEditor(TerminalEditor.ID, true);
			}
		};
		openEditor.setText("Open Terminal Editor");
		openEditor.setToolTipText("Create a Terminal Editor");
		openEditor.setImageDescriptor(icon);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		browser.setFocus();
	}

	/**
	 * showMessage
	 * 
	 * @param message
	 */
	private void showMessage(String message)
	{
		MessageDialog.openInformation(browser.getShell(), "Terminal View", message);
	}
}