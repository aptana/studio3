package com.aptana.terminal.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.aptana.terminal.Activator;
import com.aptana.terminal.TerminalBrowser;
import com.aptana.terminal.server.TerminalServer;

public class TerminalEditor extends EditorPart implements IPersistableEditor
{
	public static final String ID = "com.aptana.terminal.TerminalEditor"; //$NON-NLS-1$

	private TerminalBrowser _browser;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		// NOTE: This forces the terminal server to startup before we try to
//		// open the terminal editor. Apparently, on Windows, the editor will
//		// open the URL before the server has started resulting in a "page not found"
		TerminalServer.getInstance();
		
		this._browser = new TerminalBrowser(this);
		this._browser.createControl(parent);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this._browser.getControl(), ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		if (this._browser != null)
		{
			this._browser.dispose();
			this._browser = null;
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
	 * @see org.eclipse.ui.IPersistableEditor#restoreState(org.eclipse.ui.IMemento)
	 */
	public void restoreState(IMemento memento)
	{
		// System.out.println("TerminalEditor: Restore State");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento)
	{
		// System.out.println("TerminalEditor: Save State");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		_browser.setFocus();
	}
}
