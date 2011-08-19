/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class TerminalEditorInput implements IEditorInput, IPersistableElement {

	private static final String PROP_TITLE = "title"; //$NON-NLS-1$
	private static final String PROP_WORKING_DIRECTORY = "workingDirectory"; //$NON-NLS-1$

	private String title;
	private IPath workingDirectory;

	public TerminalEditorInput() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPersistableElement#getFactoryId()
	 */
	public String getFactoryId() {
		return TerminalElementFactory.ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		IMemento child = memento.createChild(PROP_TITLE);
		child.putTextData((title != null) ? title : ""); //$NON-NLS-1$
		child = memento.createChild(PROP_WORKING_DIRECTORY);
		if (workingDirectory != null) {
			child.putTextData(workingDirectory.toOSString());
		}
	}

	protected void loadState(IMemento memento) {
		IMemento child = memento.getChild(PROP_TITLE);
		if (child != null) {
			setTitle(child.getTextData());
		}
		child = memento.getChild(PROP_WORKING_DIRECTORY);
		if (child != null) {
			String value = child.getTextData();
			if (value != null) {
				setWorkingDirectory(Path.fromOSString(value));
			}
		}
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the workingDirectory
	 */
	public IPath getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * @param workingDirectory
	 *            the workingDirectory to set
	 */
	public void setWorkingDirectory(IPath workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
}
