/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.dialogs;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.internal.ui.actions.StatusInfo;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.internal.ide.dialogs.ResourceComparator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class HttpServerPathDialog extends StatusDialog {

	private static final Pattern SERVER_PATH_PATTERN = Pattern.compile("(/[a-zA-Z0-9_!~*'().;?:@&=+$,%#-]+)*/?"); //$NON-NLS-1$

	private String serverPath = StringUtil.EMPTY;
	private IResource resource;

	/**
	 * @param parent
	 */
	public HttpServerPathDialog(Shell parent, String title) {
		super(parent);
		setTitle(title);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		((GridLayout) container.getLayout()).numColumns = 3;

		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.HttpServerPathDialog_ServerPath);
		GridDataFactory.swtDefaults().applyTo(label);

		Text serverPathText = new Text(container, SWT.BORDER);
		serverPathText.setText(serverPath);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).hint(250, SWT.DEFAULT).applyTo(serverPathText);

		label = new Label(container, SWT.NONE);
		label.setText(Messages.HttpServerPathDialog_WorkspaceLocation);
		GridDataFactory.swtDefaults().applyTo(label);

		final Text workspacePathText = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		if (resource != null) {
			workspacePathText.setText(resource.getFullPath().toPortableString());
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(workspacePathText);

		Button browseButton = new Button(container, SWT.PUSH);
		browseButton.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		GridDataFactory.fillDefaults().applyTo(browseButton);

		serverPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				serverPath = ((Text) e.widget).getText();
				checkValues();
			}
		});

		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(),
						new WorkbenchLabelProvider(), new WorkbenchContentProvider());
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
				dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
				dialog.addFilter(new ViewerFilter() {
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						return element instanceof IContainer;
					}
				});
				dialog.setMessage(Messages.HttpServerPathDialog_SelectWorkspaceFolder);
				if (resource != null) {
					dialog.setInitialSelection(resource);
				}
				if (dialog.open() == Window.OK) {
					resource = (IResource) dialog.getFirstResult();
					workspacePathText.setText(resource.getFullPath().toPortableString());
					checkValues();
				}

			}
		});

		updateButtonsEnableState(new StatusInfo(IStatus.ERROR, StringUtil.EMPTY));

		return container;
	}

	/**
	 * Check the field values and display a message in the status if needed.
	 */
	private void checkValues() {
		StatusInfo status = new StatusInfo();

		if (serverPath.length() == 0 || !SERVER_PATH_PATTERN.matcher(serverPath).matches()) {
			status.setError(Messages.HttpServerPathDialog_Error_IncompleteServerPath);
		} else if (resource == null) {
			status.setError(Messages.HttpServerPathDialog_Error_EmptyWorkspaceLocation);
		}

		updateStatus(status);
	}

	/**
	 * @return the resource
	 */
	public IResource getWorkspaceResource() {
		return resource;
	}

	/**
	 * @return the serverPath
	 */
	public String getServerPath() {
		return serverPath;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void setWorkspaceResource(IResource resource) {
		this.resource = resource;
	}

	/**
	 * @param serverPath
	 *            the serverPath to set
	 */
	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

}
