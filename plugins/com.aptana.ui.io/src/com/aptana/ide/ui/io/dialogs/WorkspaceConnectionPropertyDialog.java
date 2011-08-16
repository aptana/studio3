/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.dialogs;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;

import com.aptana.core.CoreStrings;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ui.IPropertyDialog;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class WorkspaceConnectionPropertyDialog extends TitleAreaDialog implements IPropertyDialog {

	private static final String DEFAULT_NAME = Messages.WorkspaceConnectionPropertyDialog_NewShortcut;
	
	private WorkspaceConnectionPoint workspaceConnectionPoint;
	private boolean isNew = false;

	private Text nameText;
	private Text workspacePathText;
	private Button browseButton;

	private Image titleImage;

	private ModifyListener modifyListener;

	/**
	 * @param parentShell
	 */
	public WorkspaceConnectionPropertyDialog(Shell parentShell) {
		super(parentShell);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertyDialog#setPropertySource(java.lang.Object)
	 */
	public void setPropertySource(Object element) {
		workspaceConnectionPoint = null;
		if (element instanceof WorkspaceConnectionPoint) {
			workspaceConnectionPoint = (WorkspaceConnectionPoint) element;
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource() {
		return workspaceConnectionPoint;
	}

	private String getConnectionPointType() {
		return WorkspaceConnectionPoint.TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		titleImage = IOUIPlugin.getImageDescriptor("/icons/full/wizban/workspace.png").createImage(); //$NON-NLS-1$
		dialogArea.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (titleImage != null) {
					setTitleImage(null);
					titleImage.dispose();
					titleImage = null;
				}
			}
		});
		
		setTitleImage(titleImage);
		if (workspaceConnectionPoint != null) {
			setTitle(Messages.WorkspaceConnectionPropertyDialog_EditTitle);
			getShell().setText(Messages.WorkspaceConnectionPropertyDialog_EditText);
		} else {
			setTitle(Messages.WorkspaceConnectionPropertyDialog_CreateTitle);
			getShell().setText(Messages.WorkspaceConnectionPropertyDialog_CreateText);
		}
		
		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory.swtDefaults()
				.margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING))
				.numColumns(3).create());
		
		/* row 1 */
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.WorkspaceConnectionPropertyDialog_ShortcutName));
		
		nameText = new Text(container, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.fillDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.span(2, 1).grab(true, false).create());
		
		/* row 2 */
		label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.WorkspaceConnectionPropertyDialog_WorkspacePath));

		workspacePathText = new Text(container, SWT.SINGLE | SWT.BORDER);
		workspacePathText.setLayoutData(GridDataFactory.swtDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());
		workspacePathText.setEditable(false);
		
		browseButton = new Button(container, SWT.PUSH);
		browseButton.setText('&' + StringUtil.ellipsify(CoreStrings.BROWSE));
		browseButton.setLayoutData(GridDataFactory.fillDefaults().hint(
				Math.max(
					new PixelConverter(browseButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
					browseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x
				), SWT.DEFAULT).create());
		
		/* -- */
		addListeners();

		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWorkspace();
			}
		});
		
		if (workspaceConnectionPoint == null) {
			try {
				workspaceConnectionPoint = (WorkspaceConnectionPoint) CoreIOPlugin.getConnectionPointManager().createConnectionPoint(getConnectionPointType());
				workspaceConnectionPoint.setName(DEFAULT_NAME);
				isNew = true;
			} catch (CoreException e) {
				IdeLog.logError(IOUIPlugin.getDefault(), Messages.WorkspaceConnectionPropertyDialog_FailedToCreate, e);
				close(); // $codepro.audit.disable closeInFinally
			}
		}
		loadPropertiesFrom(workspaceConnectionPoint);

		return dialogArea;
	}

	protected void addListeners() {
		if (modifyListener == null) {
			modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validate();
				}
			};
		}
		nameText.addModifyListener(modifyListener);
		workspacePathText.addModifyListener(modifyListener);
	}
	
	protected void removeListeners() {
		if (modifyListener != null) {
			nameText.removeModifyListener(modifyListener);
			workspacePathText.removeModifyListener(modifyListener);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (!isValid()) {
			return;
		}
		if (savePropertiesTo(workspaceConnectionPoint)) {
			/* TODO: notify */
			workspaceConnectionPoint.hashCode();
		}
		if (isNew) {
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(workspaceConnectionPoint);
		}
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		try {
			return super.createContents(parent);
		} finally {
			validate();
		}
	}

	protected void loadPropertiesFrom(WorkspaceConnectionPoint connectionPoint) {
		removeListeners();
		try {
			nameText.setText(valueOrEmpty(connectionPoint.getName()));
			IContainer resource = connectionPoint.getResource();
			workspacePathText.setText((resource != null) ? resource.getFullPath().toPortableString() : ""); //$NON-NLS-1$
		} finally {
			addListeners();
		}
	}

	protected boolean savePropertiesTo(WorkspaceConnectionPoint connectionPoint) {
		boolean updated = false;
		String name = nameText.getText();
		if (!name.equals(connectionPoint.getName())) {
			connectionPoint.setName(name);
			updated = true;
		}
		IPath path = Path.fromPortableString(workspacePathText.getText());
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (resource instanceof IContainer) {
			if (!resource.equals(connectionPoint.getResource())) {
				connectionPoint.setResource((IContainer) resource);
				updated = true;
			}
		}
		return updated;
	}

	private void browseWorkspace() {
		FileFolderSelectionDialog dlg = new FileFolderSelectionDialog(getShell(), false, IResource.FOLDER);
		IPath path = Path.fromPortableString(workspacePathText.getText());
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		if (resource instanceof IContainer) {
			dlg.setInitialSelection(EFSUtils.getFileStore(resource));
		}
		dlg.setInput(EFSUtils.getFileStore(ResourcesPlugin.getWorkspace().getRoot()));
		if (dlg.open() == Window.OK) {
			IFileStore fileStore = (IFileStore) dlg.getFirstResult();
			if (fileStore != null) {
				resource = (IResource) fileStore.getAdapter(IResource.class);
				if (resource instanceof IContainer) {
					workspacePathText.setText(resource.getFullPath().toPortableString());
					if (DEFAULT_NAME.equals(nameText.getText())) {
						nameText.setText(resource.getName());
					}
				}
			}
		}
	}
	
	public void validate() {
		boolean valid = isValid();
		getButton(OK).setEnabled(valid);
	}
	
	public boolean isValid() {
		String message = null;
		if (nameText.getText().length() == 0) {
			message = Messages.WorkspaceConnectionPropertyDialog_SpecifyShortcutName;
		} else {
			IPath path = Path.fromPortableString(workspacePathText.getText());
			IContainer container = null;
			if (path.segmentCount() == 1) {
				container = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));			
			} else if (path.segmentCount() > 1) {
				container = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
			} else {
				message = Messages.WorkspaceConnectionPropertyDialog_SpecifyLocation;
			}
			if (message == null && (container == null || !container.exists())) {
				message = Messages.WorkspaceConnectionPropertyDialog_ResourceNotExist;
			}
		}
		if (message != null) {
			setErrorMessage(message);
		} else {
			setErrorMessage(null);
			setMessage(null);
			return true;
		}
		return false;
	}

	protected static String valueOrEmpty(String value) {
		if (value != null) {
			return value;
		}
		return ""; //$NON-NLS-1$
	}
}
