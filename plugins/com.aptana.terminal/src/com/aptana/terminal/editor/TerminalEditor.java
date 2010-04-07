package com.aptana.terminal.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.tm.internal.terminal.control.ITerminalListener;
import org.eclipse.tm.internal.terminal.control.ITerminalViewControl;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.Closeable;
import com.aptana.terminal.connector.LocalTerminalConnector;
import com.aptana.terminal.internal.IProcessListener;
import com.aptana.terminal.internal.emulator.VT100TerminalControl;
import com.aptana.terminal.preferences.IPreferenceConstants;

@SuppressWarnings("restriction")
public class TerminalEditor extends EditorPart implements Closeable, ITerminalListener, IProcessListener {
	public static final String ID = "com.aptana.terminal.TerminalEditor"; //$NON-NLS-1$

	private ITerminalViewControl fCtlTerminal;
	
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
		
		hookContextMenu();
		saveInputState();
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
		setContentDescription(title);
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
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager)
	{
		// add browser's menus
		//fCtlTerminal.fillContextMenu(manager);
		
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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

		Control control = fCtlTerminal.getControl();
		Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);
		// getSite().registerContextMenu(menuMgr, viewer);
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
		this.setPartName(Messages.TerminalEditor_Part_Name);
		this.setTitleToolTip(Messages.TerminalEditor_Title_Tool_Tip);
		this.setTitleImage(Activator.getImage("icons/terminal.png")); //$NON-NLS-1$
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
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		fCtlTerminal.setFocus();
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
