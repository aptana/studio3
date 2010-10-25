/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.views;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionClearAll;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionCopy;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionCut;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionPaste;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionSelectAll;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.internal.keys.WorkbenchKeyboard.KeyDownFilter;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.ViewPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.Utils;
import com.aptana.terminal.editor.TerminalEditor;
import com.aptana.terminal.internal.IProcessListener;
import com.aptana.terminal.preferences.IPreferenceConstants;
import com.aptana.terminal.widget.TerminalComposite;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class TerminalView extends ViewPart implements ISaveablePart2, ITerminalListener, IProcessListener {

	public static final String ID = "com.aptana.terminal.views.terminal"; //$NON-NLS-1$

	private static final String PROP_TITLE = "title"; //$NON-NLS-1$
	private static final String PROP_WORKING_DIRECTORY = "workingDirectory"; //$NON-NLS-1$

	private TerminalComposite terminalComposite;
	private IMemento savedState = null;
	
	private Action fOpenEditorAction;
	private Action fOpenViewAction;
	private TerminalActionCopy fActionEditCopy;
	private TerminalActionCut fActionEditCut;
	private TerminalActionPaste fActionEditPaste;
	private TerminalActionClearAll fActionEditClearAll;
	private TerminalActionSelectAll fActionEditSelectAll;
	
	private boolean checkCanClose = false;

	/**
	 * @param id
	 *            The secondary id of the view. Used to uniquely identify and address a specific instance of this view.
	 * @param title
	 *            the title used in the UI tab for the instance of the view.
	 * @param workingDirectory
	 *            The directory in which to set the view initially.
	 * @return
	 */
	public static TerminalView openView(String secondaryId, String title, IPath workingDirectory) {
		TerminalView view = null;
		secondaryId  = secondaryId != null ? secondaryId : Long.toHexString(System.currentTimeMillis());
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			view = (TerminalView) page.showView(TerminalView.ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
			view.initialize(title, workingDirectory);
		} catch (PartInitException e) {
			Activator.logError("Terminal view creation failed.", e); //$NON-NLS-1$
		}
		return view;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		savedState = memento;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		terminalComposite = new TerminalComposite(parent, SWT.NONE);
		terminalComposite.setTerminalListener(this);
		terminalComposite.setProcessListener(this);
		if (getViewSite().getSecondaryId() == null || savedState != null) {
			if (savedState != null) {
				loadState(savedState);
			}
			terminalComposite.connect();
		}
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		
		terminalComposite.getTerminalControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				updateActions();
			}
		});
		terminalComposite.getTerminalControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.doit) {
					IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
					Event event = new Event();
					event.character = e.character;
					event.keyCode = e.keyCode;
					event.stateMask = e.stateMask;
					event.doit = e.doit;
					event.display = e.display;
					event.widget = e.widget;
					event.time = e.time;
					event.data = e.data;
					KeyDownFilter keyDownFilter = ((BindingService) bindingService).getKeyboard().getKeyDownFilter();
					boolean enabled = keyDownFilter.isEnabled();
					Control focusControl = e.display.getFocusControl();
					try {
						keyDownFilter.setEnabled(true);
						keyDownFilter.handleEvent(event);
					} finally {
						if (focusControl == e.display.getFocusControl()) {
							keyDownFilter.setEnabled(enabled);
						}
					}
				}
			}
		});
		
		// Add drag and drop support for file paths		
		DropTarget dt = new DropTarget(terminalComposite.getRootControl(), DND.DROP_DEFAULT | DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter()
		{
			public void drop(DropTargetEvent event)
			{
				FileTransfer ft = FileTransfer.getInstance();
				if (ft.isSupportedType(event.currentDataType))
				{
					String fileList[] = (String[]) event.data;
					if (fileList != null && fileList.length > 0)
					{
						StringBuilder builder = new StringBuilder();
						for (String file : fileList)
						{
							builder.append(file).append(" "); //$NON-NLS-1$
						}				
						terminalComposite.sendInput(builder.toString());
					}
				}
			}
		});
	}
	
	protected void close() {
		if (terminalComposite != null && !terminalComposite.isDisposed()) {
			terminalComposite.getTerminalControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					getSite().getPage().hideView((IViewPart) getSite().getPart());
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.terminal.internal.IProcessListener#processCompleted()
	 */
	public void processCompleted() {
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		boolean closeViewOnExit = prefs.getBoolean(IPreferenceConstants.CLOSE_VIEW_ON_EXIT);
		if (closeViewOnExit) {
			close();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart2#promptToSaveOnClose()
	 */
	public int promptToSaveOnClose() {
		return terminalComposite.canCloseTerminal() ? ISaveablePart2.YES : ISaveablePart2.CANCEL;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {
		try {
			return checkCanClose;
		} finally {
			checkCanClose = false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		checkCanClose = true;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		IMemento child = memento.createChild(PROP_TITLE);
		child.putTextData(getPartName());
		child = memento.createChild(PROP_WORKING_DIRECTORY);
		IPath workingDirectory = terminalComposite.getWorkingDirectory();
		if (workingDirectory != null) {
			child.putTextData(workingDirectory.toOSString());
		}
	}

	private void loadState(IMemento memento) {
		IMemento child = memento.getChild(PROP_TITLE);
		if (child != null) {
			setPartName(child.getTextData());
		}
		child = memento.getChild(PROP_WORKING_DIRECTORY);
		if (child != null) {
			String value = child.getTextData();
			if (value != null) {
				terminalComposite.setWorkingDirectory(Path.fromOSString(value));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		terminalComposite.setFocus();
	}
	
	public void setState(TerminalState state) {
	}

	public void setTerminalTitle(final String title) {
		Utils.runInDisplayThread(new Runnable() {
			public void run() {
				setPartName(title);
			}
		});
	}
	
	protected void initialize(String title, IPath workingDirectory) {
		if (terminalComposite.isConnected()) {
			return;
		}
		setPartName(title);
		terminalComposite.setWorkingDirectory(workingDirectory);
		terminalComposite.connect();
	}
		
	/**
	 * @param text
	 * @see com.aptana.terminal.widget.TerminalComposite#sendInput(java.lang.String)
	 */
	public void sendInput(String text) {
		terminalComposite.sendInput(text);
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Control control = terminalComposite.getTerminalControl();
		Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);
	}
	
	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(fActionEditCopy);
		menuMgr.add(fActionEditPaste);
		menuMgr.add(new Separator());
		menuMgr.add(fActionEditClearAll);
		menuMgr.add(fActionEditSelectAll);
		menuMgr.add(new Separator());
		menuMgr.add(fOpenViewAction);
		menuMgr.add(fOpenEditorAction);

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * contributeToActionBars
	 */
	private void contributeToActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();
		fillLocalPullDown(actionBars.getMenuManager());
		fillLocalToolBar(actionBars.getToolBarManager());
		
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), fActionEditCopy);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), fActionEditPaste);
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), fActionEditSelectAll);

	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(fOpenViewAction);
		manager.add(fOpenEditorAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fOpenViewAction);
		manager.add(fOpenEditorAction);
		manager.add(new Separator());
		manager.add(fActionEditClearAll);
	}
	
	private void updateActions() {
		fActionEditCut.updateAction(true);
		fActionEditCopy.updateAction(true);
		fActionEditPaste.updateAction(true);
		fActionEditSelectAll.updateAction(true);
		fActionEditClearAll.updateAction(true);
	}

	/**
	 * makeActions
	 */
	private void makeActions() {
		fActionEditCopy = new TerminalActionCopy(terminalComposite.getTerminalViewControl());
		fActionEditCut = new TerminalActionCut(terminalComposite.getTerminalViewControl());
		fActionEditPaste = new TerminalActionPaste(terminalComposite.getTerminalViewControl());
		fActionEditClearAll = new TerminalActionClearAll(terminalComposite.getTerminalViewControl());
		fActionEditSelectAll = new TerminalActionSelectAll(terminalComposite.getTerminalViewControl());

		// open view action
		fOpenViewAction = new Action(Messages.TerminalView_Open_Terminal_View, Activator.getImageDescriptor("/icons/terminal.png")) { //$NON-NLS-1$
			@Override
			public void run() {
				openView(null, getPartName(), getWorkingDirectory());
			}
		};
		fOpenViewAction.setToolTipText(Messages.TerminalView_Create_Terminal_View_Tooltip);

		// open editor action
		fOpenEditorAction = new Action(Messages.TerminalView_Open_Terminal_Editor, Activator.getImageDescriptor("/icons/terminal.png")) { //$NON-NLS-1$
			@Override
			public void run() {
				Utils.openTerminalEditor(TerminalEditor.ID, true);
			}
		};
		fOpenEditorAction.setToolTipText(Messages.TerminalView_Create_Terminal_Editor_Tooltip);
	}
	
	public IPath getWorkingDirectory()
	{
		return terminalComposite.getWorkingDirectory();
	}

}
