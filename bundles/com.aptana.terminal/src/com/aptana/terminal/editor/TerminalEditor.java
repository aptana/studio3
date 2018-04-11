/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeInFinally
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.terminal.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;

import com.aptana.ui.keybinding.KeyBindingHelper;
import com.aptana.terminal.TerminalPlugin;
import com.aptana.terminal.Utils;
import com.aptana.terminal.internal.IProcessListener;
import com.aptana.terminal.preferences.IPreferenceConstants;
import com.aptana.terminal.widget.TerminalComposite;

public class TerminalEditor extends EditorPart implements ISaveablePart2, IProcessListener {
	public static final String ID = "com.aptana.terminal.TerminalEditor"; //$NON-NLS-1$

	private TerminalComposite terminalComposite;

	private TerminalActionCopy fActionEditCopy;
	private TerminalActionCut fActionEditCut;
	private TerminalActionPaste fActionEditPaste;
	private TerminalActionClearAll fActionEditClearAll;
	private TerminalActionSelectAll fActionEditSelectAll;

	private boolean checkCanClose = false;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		terminalComposite = new TerminalComposite(parent, SWT.NONE);
		terminalComposite.setTerminalListener(new ITerminalListener() {
			public void setTerminalTitle(final String title) {
				Utils.runInDisplayThread(new Runnable() {
					public void run() {
						setPartName(title);
					}
				});
			}

			public void setState(TerminalState state) {
			}
		});
		terminalComposite.setProcessListener(this);
		IEditorInput input = getEditorInput();
		if (input instanceof TerminalEditorInput) {
			TerminalEditorInput terminalEditorInput = (TerminalEditorInput) input;
			String title = terminalEditorInput.getTitle();
			if (title != null && title.length() > 0) {
				setPartName(title);
			}
			terminalComposite.setWorkingDirectory(terminalEditorInput.getWorkingDirectory());
		}

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(terminalComposite.getTerminalControl(), ID);

		makeActions();
		hookContextMenu();
		contributeToActionBars();
		saveInputState();

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
					Event event = new Event();
					event.character = e.character;
					event.keyCode = e.keyCode;
					event.stateMask = e.stateMask;
					event.doit = e.doit;
					event.display = e.display;
					event.widget = e.widget;
					event.time = e.time;
					event.data = e.data;
					KeyBindingHelper.handleEvent(event);
				}
			}
		});

		// Add drag and drop support for file paths
		DropTarget dt = new DropTarget(terminalComposite.getRootControl(), DND.DROP_DEFAULT | DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				FileTransfer ft = FileTransfer.getInstance();
				if (ft.isSupportedType(event.currentDataType)) {
					String[] fileList = (String[]) event.data;
					if (fileList != null && fileList.length > 0) {
						StringBuilder builder = new StringBuilder();
						for (String file : fileList) {
							builder.append(file).append(' ');
						}
						terminalComposite.sendInput(builder.toString());
					}
				}
			}
		});
		terminalComposite.connect();
	}

	/**
	 * @param text
	 * @see com.aptana.terminal.widget.TerminalComposite#sendInput(java.lang.String)
	 */
	public void sendInput(String text) {
		terminalComposite.sendInput(text);
	}

	private void saveInputState() {
		IEditorInput input = getEditorInput();
		if (input instanceof TerminalEditorInput) {
			TerminalEditorInput terminalEditorInput = (TerminalEditorInput) input;
			terminalEditorInput.setTitle(getPartName());
			terminalEditorInput.setWorkingDirectory(terminalComposite.getWorkingDirectory());
		}
	}

	protected void close() {
		if (terminalComposite != null && !terminalComposite.isDisposed()) {
			terminalComposite.getTerminalControl().getDisplay().asyncExec(new Runnable() {
				public void run() {
					getSite().getPage().closeEditor((IEditorPart) getSite().getPart(), false);
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.internal.IProcessListener#processCompleted()
	 */
	public void processCompleted() {
		IPreferenceStore prefs = TerminalPlugin.getDefault().getPreferenceStore();
		boolean closeViewOnExit = prefs.getBoolean(IPreferenceConstants.CLOSE_VIEW_ON_EXIT);
		if (closeViewOnExit) {
			close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		try {
			return checkCanClose;
		} finally {
			checkCanClose = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveOnCloseNeeded()
	 */
	@Override
	public boolean isSaveOnCloseNeeded() {
		checkCanClose = true;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart2#promptToSaveOnClose()
	 */
	public int promptToSaveOnClose() {
		return terminalComposite.canCloseTerminal() ? ISaveablePart2.YES : ISaveablePart2.CANCEL;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setContentDescription(java.lang.String)
	 */
	@Override
	protected void setContentDescription(String description) {
		super.setContentDescription(description);
		checkCanClose = false; // reset state set by testEditor
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/**
	 * fillContextMenu
	 * 
	 * @param menuMgr
	 */
	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(fActionEditCopy);
		menuMgr.add(fActionEditPaste);
		menuMgr.add(new Separator());
		menuMgr.add(fActionEditClearAll);
		menuMgr.add(fActionEditSelectAll);
		menuMgr.add(new Separator());

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * contributeToActionBars
	 */
	private void contributeToActionBars() {
		IActionBars actionBars = getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), fActionEditCopy);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), fActionEditPaste);
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), fActionEditSelectAll);

	}

	private void updateActions() {
		boolean aboutToShow = true;
		fActionEditCut.updateAction(aboutToShow);
		fActionEditCopy.updateAction(aboutToShow);
		fActionEditPaste.updateAction(aboutToShow);
		fActionEditSelectAll.updateAction(aboutToShow);
		fActionEditClearAll.updateAction(aboutToShow);
	}

	/**
	 * makeActions
	 */
	private void makeActions() {
		fActionEditCopy = new TerminalActionCopy(terminalComposite.getTerminalViewControl());
		fActionEditCut = new TerminalActionCut(terminalComposite.getTerminalViewControl());
		fActionEditPaste = new TerminalActionPaste(terminalComposite.getTerminalViewControl());
		fActionEditClearAll = new TerminalActionClearAll(terminalComposite.getTerminalViewControl()) {
			@Override
			public void run() {
				super.run();
				updateActions();
			}
		};
		fActionEditSelectAll = new TerminalActionSelectAll(terminalComposite.getTerminalViewControl()) {
			@Override
			public void run() {
				super.run();
				updateActions();
			}
		};
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(Messages.TerminalEditor_Part_Name);
		setTitleToolTip(Messages.TerminalEditor_Title_Tool_Tip);
		setTitleImage(TerminalPlugin.getImage("icons/terminal.png")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		terminalComposite.setFocus();
	}

	public IPath getWorkingDirectory() {
		return terminalComposite.getWorkingDirectory();
	}
}
