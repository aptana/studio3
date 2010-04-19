package com.aptana.terminal.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.control.ITerminalViewControl;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionClearAll;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionCopy;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionCut;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionPaste;
import org.eclipse.tm.internal.terminal.control.actions.TerminalActionSelectAll;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.internal.keys.WorkbenchKeyboard.KeyDownFilter;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.EditorPart;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.terminal.Activator;
import com.aptana.terminal.Closeable;
import com.aptana.terminal.connector.LocalTerminalConnector;
import com.aptana.terminal.internal.IProcessListener;
import com.aptana.terminal.internal.emulator.VT100TerminalControl;
import com.aptana.terminal.preferences.IPreferenceConstants;

@SuppressWarnings("restriction")
public class TerminalEditor extends EditorPart implements Closeable, ITerminalListener, IProcessListener, IPreferenceChangeListener {
	public static final String ID = "com.aptana.terminal.TerminalEditor"; //$NON-NLS-1$

	private ITerminalViewControl fCtlTerminal;

	private TerminalActionCopy fActionEditCopy;
	private TerminalActionCut fActionEditCut;
	private TerminalActionPaste fActionEditPaste;
	private TerminalActionClearAll fActionEditClearAll;
	private TerminalActionSelectAll fActionEditSelectAll;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {		
		fCtlTerminal = new VT100TerminalControl(this, parent, getTerminalConnectors());
		fCtlTerminal.setConnector(fCtlTerminal.getConnectors()[0]);
		IEditorInput input = getEditorInput();
		if (input instanceof TerminalEditorInput) {
			TerminalEditorInput terminalEditorInput = (TerminalEditorInput) input;
			String title = terminalEditorInput.getTitle();
			if (title != null && title.length() > 0) {
				setPartName(title);
			}
			setWorkingDirectory(terminalEditorInput.getWorkingDirectory());
		}
		fCtlTerminal.connectTerminal();
		hookProcessListener();

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(fCtlTerminal.getControl(), ID);
		
		makeActions();
		hookContextMenu();
		saveInputState();
		
		fCtlTerminal.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				updateActions();
			}
		});
		fCtlTerminal.getControl().addKeyListener(new KeyAdapter() {
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
	}

	private ITerminalConnector[] getTerminalConnectors() {
		return new ITerminalConnector[] { TerminalConnectorExtension.makeTerminalConnector(LocalTerminalConnector.ID) };
	}
	
	private void saveInputState() {
		IEditorInput input = getEditorInput();
		if (input instanceof TerminalEditorInput) {
			TerminalEditorInput terminalEditorInput = (TerminalEditorInput) input;
			terminalEditorInput.setTitle(getPartName());
			terminalEditorInput.setWorkingDirectory(getWorkingDirectory());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.Closeable#close()
	 */
	public void close() {
		if (fCtlTerminal != null && !fCtlTerminal.isDisposed()) {
			fCtlTerminal.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					getSite().getPage().closeEditor((IEditorPart) getSite().getPart(), false);
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.terminal.internal.IProcessListener#processCompleted()
	 */
	@Override
	public void processCompleted() {
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		boolean closeViewOnExit = prefs.getBoolean(IPreferenceConstants.CLOSE_VIEW_ON_EXIT);
		if (closeViewOnExit) {
			close();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.control.ITerminalListener#setState(org.eclipse.tm.internal.terminal.provisional.api.TerminalState)
	 */
	@Override
	public void setState(TerminalState state) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.control.ITerminalListener#setTerminalTitle(java.lang.String)
	 */
	@Override
	public void setTerminalTitle(String title) {
		setPartName(title);
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

	/**
	 * fillContextMenu
	 * 
	 * @param menuMgr
	 */
	private void fillContextMenu(IMenuManager menuMgr)
	{
		menuMgr.add(fActionEditCopy);
		menuMgr.add(fActionEditPaste);
		menuMgr.add(new Separator());
		menuMgr.add(fActionEditClearAll);
		menuMgr.add(fActionEditSelectAll);
		menuMgr.add(new Separator());

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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
		fActionEditCopy = new TerminalActionCopy(fCtlTerminal);
		fActionEditCut = new TerminalActionCut(fCtlTerminal);
		fActionEditPaste = new TerminalActionPaste(fCtlTerminal);
		fActionEditClearAll = new TerminalActionClearAll(fCtlTerminal);
		fActionEditSelectAll = new TerminalActionSelectAll(fCtlTerminal);
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Control control = fCtlTerminal.getControl();
		Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException{
		setSite(site);
		setInput(input);
		setPartName(Messages.TerminalEditor_Part_Name);
		setTitleToolTip(Messages.TerminalEditor_Title_Tool_Tip);
		setTitleImage(Activator.getImage("icons/terminal.png")); //$NON-NLS-1$
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(this);
		fCtlTerminal.disposeTerminal();
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
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
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		fCtlTerminal.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
	 */
	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (IThemeManager.THEME_CHANGED.equals(event.getKey())) {
			if (fCtlTerminal != null && !fCtlTerminal.isDisposed()) {
				fCtlTerminal.getControl().redraw();
			}
		}
	}

	protected void setWorkingDirectory(IPath workingDirectory) {
		if (workingDirectory != null && fCtlTerminal != null) {
			LocalTerminalConnector localTerminalConnector = (LocalTerminalConnector) fCtlTerminal.getTerminalConnector().getAdapter(LocalTerminalConnector.class);
			if (localTerminalConnector != null) {
				localTerminalConnector.setWorkingDirectory(workingDirectory);
			}		
		}
	}
	
	protected IPath getWorkingDirectory() {
		if (fCtlTerminal != null) {
			LocalTerminalConnector localTerminalConnector = (LocalTerminalConnector) fCtlTerminal.getTerminalConnector().getAdapter(LocalTerminalConnector.class);
			if (localTerminalConnector != null) {
				return localTerminalConnector.getWorkingDirectory();
			}
		}
		return null;
	}

	protected void hookProcessListener() {
		LocalTerminalConnector localTerminalConnector = (LocalTerminalConnector) fCtlTerminal.getTerminalConnector().getAdapter(LocalTerminalConnector.class);
		if (localTerminalConnector != null) {
			localTerminalConnector.addProcessListener(this);
		}
	}

}
