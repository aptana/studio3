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
package com.aptana.portal.ui.dispatch.configurationProcessors.installer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.portal.ui.dispatch.configurationProcessors.Messages;

/**
 * A generic implementation for an installation dialog. Through this dialog, the user can input arbitrary data that is
 * needed for the specific installer.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class InstallerOptionsDialog extends TitleAreaDialog
{
	public static final String INSTALL_DIR_ATTR = "install_dir"; //$NON-NLS-1$
	protected Map<String, Object> attributes;
	private Text path;
	private String installerName;

	public InstallerOptionsDialog(Shell parentShell, String installerName)
	{
		super(Display.getDefault().getActiveShell());
		this.installerName = installerName;
		setBlockOnOpen(true);
		setHelpAvailable(false);
		attributes = new HashMap<String, Object>();
		setAttributes();
	}

	/**
	 * Returns an unmodifiable Map of the attributes this install dialog is holding.
	 * 
	 * @return
	 */
	public Map<String, Object> getAttributes()
	{
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * Set attributes that can later be used when creating the dialog area.
	 * 
	 * @param attributeName
	 * @param value
	 */
	protected abstract void setAttributes();

	/**
	 * Configure the shell to display a title.
	 */
	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.InstallProcessor_installerShellTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		// Create a inner composite so we can control the margins
		Composite inner = new Composite(composite, SWT.NONE);
		inner.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginLeft = 4;
		layout.marginRight = 4;
		layout.marginTop = 4;
		layout.marginBottom = 4;
		inner.setLayout(layout);

		// TODO - Split this to a method.
		Group group = new Group(inner, SWT.NONE);
		group.setText(Messages.InstallProcessor_installerGroupTitle);
		group.setLayout(new GridLayout());
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		group.setLayoutData(layoutData);

		createInstallerGroupControls(group);
		createExtendedControls(inner);
		setTitle(NLS.bind(Messages.InstallProcessor_installerTitle, installerName));
		return composite;
	}

	/**
	 * Creates the components inside the 'Installer' group. <br>
	 * The default creation is only for the installation path. This can be overwritten, or extended, by a subclass.
	 * 
	 * @param group
	 * @return A composite.
	 */
	protected Composite createInstallerGroupControls(Composite group)
	{
		Label l = new Label(group, SWT.WRAP);
		l.setText(NLS.bind(Messages.InstallProcessor_installerMessage, installerName));
		Composite installLocation = new Composite(group, SWT.NONE);
		installLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		installLocation.setLayout(new GridLayout(2, false));
		path = new Text(installLocation, SWT.SINGLE | SWT.BORDER);
		path.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		path.setText(attributes.get(INSTALL_DIR_ATTR).toString());
		path.addKeyListener(new KeyListener()
		{
			public void keyReleased(org.eclipse.swt.events.KeyEvent e)
			{
				attributes.put(INSTALL_DIR_ATTR, path.getText());
			}

			public void keyPressed(org.eclipse.swt.events.KeyEvent e)
			{
				attributes.put(INSTALL_DIR_ATTR, path.getText());
			}
		});
		Button browse = new Button(installLocation, SWT.PUSH);
		browse.setText(Messages.InstallProcessor_browse);
		browse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dirDialog = new DirectoryDialog(getParentShell());
				String dir = dirDialog.open();
				if (dir != null)
				{
					path.setText(dir);
					attributes.put(INSTALL_DIR_ATTR, dir);
				}
			}
		});
		return group;
	}

	/**
	 * Create extended controls that will appear under the 'Installer' group.<br>
	 * The default implementation is empty, and can be sub-classed.
	 * 
	 * @param parent
	 * @return A composite.
	 */
	protected Composite createExtendedControls(Composite parent)
	{
		// Does nothing special here
		return parent;
	}

	/**
	 * Capitalize the word by upper-casing the first letter.
	 * 
	 * @param word
	 * @return A capitalized word.
	 */
	protected static String capitalize(String word)
	{
		if (word != null && word.length() > 0)
		{
			return Character.toUpperCase(word.charAt(0)) + word.substring(1);
		}
		return word;
	}
}
