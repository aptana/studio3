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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPersistableElement#getFactoryId()
	 */
	public String getFactoryId() {
		return TerminalElementFactory.ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		IMemento child = memento.createChild(PROP_TITLE);
		child.putTextData(title != null ? title : ""); //$NON-NLS-1$
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
	 * @param title the title to set
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
	 * @param workingDirectory the workingDirectory to set
	 */
	public void setWorkingDirectory(IPath workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
}
