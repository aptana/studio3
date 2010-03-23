package com.aptana.terminal.views;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.TerminalBrowser;
import com.aptana.terminal.Utils;
import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.server.ProcessWrapper;
import com.aptana.terminal.server.TerminalServer;

public class TerminalView extends ViewPart
{
	public static final String ID = "com.aptana.terminal.views.TerminalView"; //$NON-NLS-1$
	private static List<String> startDirectories = new ArrayList<String>(2);

	/**
	 * pullStartDirectory
	 * 
	 * @return
	 */
	private static String pullStartingDirectory()
	{
		String result = null;

		synchronized (startDirectories)
		{
			if (startDirectories.isEmpty() == false)
			{
				result = startDirectories.remove(0);
			}
		}

		return result;
	}

	/**
	 * pushStartingDirectory
	 * 
	 * @param startingDirectory
	 */
	public static void pushStartingDirectory(String startingDirectory)
	{
		synchronized (startDirectories)
		{
			startDirectories.add(startingDirectory);
		}
	}

	/**
	 * @param id
	 *            The secondary id of the view. Used to uniquely identify and address a specific instance of this view.
	 * @param title
	 *            the title used in the UI tab for the instance of the view.
	 * @param workingDirectory
	 *            The directory in which to set the view initially.
	 * @return
	 */
	public static TerminalView open(String id, String title, String workingDirectory)
	{
		TerminalView term = null;

		try
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			if (workingDirectory != null)
			{
				pushStartingDirectory(workingDirectory);
			}
			// FIXME What if view with same ids is already open? We need to close and re-open, or issue cd explcitiy or our starting dir stack is messed up
			term = (TerminalView) page.showView(TerminalView.ID, id, org.eclipse.ui.IWorkbenchPage.VIEW_ACTIVATE);
		}
		catch (IllegalStateException e)
		{
			Activator.logError(e.getMessage(), e);
		}
		catch (PartInitException e)
		{
			Activator.logError(e.getMessage(), e);
		}

		if (term != null)
		{
			term.setPartName(title);
			// For some reason the stack for working directories isn't working as expected always, 
			// so we force a cd to the working dir before returning the reference to terminal
			ProcessWrapper wrapper = TerminalServer.getInstance().getProcess(term.getId());
			if (workingDirectory != null)
			{
				// FIXME We may need to tweak the working dir path based on cmd/cygwin/mingw
				wrapper.sendText(MessageFormat.format("cd \"{0}\"\n", workingDirectory)); //$NON-NLS-1$
			}
		}

		return term;
	}

	private TerminalBrowser browser;
	private Action openEditor;

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
		this.browser = new TerminalBrowser(this, pullStartingDirectory());
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
		// add menus
		manager.add(this.openEditor);
		manager.add(new Separator());
		
		// add browser's menus
		this.browser.fillContextMenu(manager);

		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * fillLocalPullDown
	 * 
	 * @param manager
	 */
	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(this.openEditor);
	}

	/**
	 * fillLocalToolBar
	 * 
	 * @param manager
	 */
	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(this.openEditor);
	}

	/**
	 * getId
	 * 
	 * @return
	 */
	public String getId()
	{
		return browser.getId();
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
				fillContextMenu(manager);
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

		TerminalServer server = TerminalServer.getInstance();

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
		// open editor action
		this.openEditor = new Action()
		{
			public void run()
			{
				Utils.openEditor(TerminalEditor.ID, true);
			}
		};
		this.openEditor.setText(Messages.TerminalView_Open_Terminal_Editor);
		this.openEditor.setToolTipText(Messages.TerminalView_Create_Terminal_Editor_Tooltip);
		this.openEditor.setImageDescriptor(Activator.getImageDescriptor("/icons/terminal.png")); //$NON-NLS-1$
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		this.browser.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
	 */
	@Override
	public void setPartName(String partName)
	{
		super.setPartName(partName);
	}
}