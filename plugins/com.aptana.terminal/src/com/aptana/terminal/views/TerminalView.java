package com.aptana.terminal.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.TerminalBrowser;
import com.aptana.terminal.Utils;
import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.server.HttpServer;

public class TerminalView extends ViewPart
{
	public static final String ID = "com.aptana.terminal.views.TerminalView"; //$NON-NLS-1$

	private TerminalBrowser browser;
	private Action openEditor;

	@Override
	public void setPartName(String partName)
	{
		super.setPartName(partName);
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
		this.browser = new TerminalBrowser(this);
		this.browser.createControl(parent);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.browser.getControl(), ID);

		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		if (this.browser != null)
		{
			this.browser.dispose();
			this.browser = null;
		}

		super.dispose();
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
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$

		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				TerminalView.this.fillContextMenu(manager);
			}
		});

		Control browserControl = browser.getControl();
		Menu menu = menuMgr.createContextMenu(browserControl);
		browserControl.setMenu(menu);
		// getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * init
	 */
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);

		HttpServer server = HttpServer.getInstance();

		if (server == null)
		{
			System.out.println(Messages.TerminalView_Could_Not_Start_Server);
		}
	}

	/**
	 * makeActions
	 */
	private void makeActions()
	{
		ImageDescriptor icon = Activator.getImageDescriptor("/icons/terminal.png"); //$NON-NLS-1$

		openEditor = new Action()
		{
			public void run()
			{
				Utils.openEditor(TerminalEditor.ID, true);
			}
		};
		openEditor.setText(Messages.TerminalView_Open_Terminal_Editor);
		openEditor.setToolTipText(Messages.TerminalView_Create_Terminal_Editor_Tooltip);
		openEditor.setImageDescriptor(icon);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		browser.setFocus();
	}

	public String getId()
	{
		return browser.getId();
	}
}