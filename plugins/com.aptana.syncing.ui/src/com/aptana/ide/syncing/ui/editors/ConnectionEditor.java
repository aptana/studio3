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
package com.aptana.ide.syncing.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.views.FTPManagerComposite;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionEditor extends EditorPart implements FTPManagerComposite.Listener {

    /**
     * ID of the editor
     */
    public static final String ID = "com.aptana.ide.syncing.ui.editors.ConnectionEditor"; //$NON-NLS-1$

    private ConnectionEditorInput fInput;
    private FTPManagerComposite fConnectionComposite;

    public ConnectionEditor() {
    }

    public void setSelectedSite(ISiteConnection site) {
        fConnectionComposite.setSelectedSite(site);
    }

    @Override
    public void dispose() {
        fConnectionComposite.dispose();
        super.dispose();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        if (input instanceof ConnectionEditorInput) {
            setInput(input);
            fInput = (ConnectionEditorInput) input;
            setPartName(fInput.getName());
        } else {
            throw new PartInitException("Incorrect editor input for ConnectionEditor"); //$NON-NLS-1$
        }
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        fConnectionComposite = new FTPManagerComposite(parent);
        fConnectionComposite.setSelectedSite(fInput.getConnection());
        fConnectionComposite.addListener(this);
    }

    @Override
    public void setFocus() {
        fConnectionComposite.getControl().setFocus();
    }

    public void siteConnectionChanged(ISiteConnection site) {
        fInput.setConnection(site);
        setPartName(fInput.getName());
        setTitleToolTip(fInput.getToolTipText());
    }
}
