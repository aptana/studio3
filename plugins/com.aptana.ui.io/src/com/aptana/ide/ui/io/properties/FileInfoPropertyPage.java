/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.ide.ui.io.properties;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ide.ui.io.preferences.PermissionsGroup;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class FileInfoPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

	private static final int MAX_VALUE_WIDTH = 80;

	private PermissionsGroup fPermissionsGroup;

	private IFileInfo fFileInfo;

	// private EncodingFieldEditor encodingEditor;
	// private LineDelimiterEditor lineDelimiterEditor;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		// first try to adapt to IFileStore directly
		final IFileStore fileStore = Utils.getFileStore(getElement());
		if (fileStore == null)
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText(IDEWorkbenchMessages.ResourceInfoPage_noResource);
			return label;
		}
		try
		{
			if (getElement().getAdapter(File.class) != null)
			{
				fFileInfo = fileStore.fetchInfo(EFS.NONE, new NullProgressMonitor());
			}
			else
			{
				final IFileInfo[] result = new IFileInfo[1];
				ProgressMonitorDialog dlg = new ProgressMonitorDialog(parent.getShell());
				try
				{
					dlg.run(true, true, new IRunnableWithProgress()
					{
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException
						{
							try
							{
								result[0] = fileStore.fetchInfo(IExtendedFileStore.DETAILED, monitor);
							}
							catch (CoreException e)
							{
								throw new InvocationTargetException(e, e.getLocalizedMessage());
							}
							finally
							{
								monitor.done();
							}
						}
					});
				}
				catch (InvocationTargetException e)
				{
					throw (CoreException) e.getTargetException();
				}
				catch (InterruptedException e)
				{
					e.getCause();
				}
				fFileInfo = result[0];
			}
		}
		catch (CoreException e)
		{
			UIUtils.showErrorMessage(Messages.FileInfoPropertyPage_FailedToFetchInfo, e);
		}
		if (fFileInfo == null)
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText(IDEWorkbenchMessages.ResourceInfoPage_noResource);
			return label;
		}

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite basicInfo = createBasicInfoGroup(composite, fileStore, fFileInfo);
		basicInfo.setLayoutData(GridDataFactory.fillDefaults().create());

		Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite state = createStateGroup(composite, fileStore, fFileInfo);
		state.setLayoutData(GridDataFactory.fillDefaults().create());

		if (fFileInfo instanceof IExtendedFileInfo)
		{
			IExtendedFileInfo extendedInfo = (IExtendedFileInfo) fFileInfo;
			Composite owner = createOwnerGroup(composite, extendedInfo);
			owner.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(0, 10).create());

			Composite permissions = createPermissionsGroup(composite, extendedInfo);
			permissions.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(0, 5).create());

		}

		/*
		 * TODO new Label(composite, SWT.NONE); // a vertical spacer encodingEditor = new EncodingFieldEditor("",
		 * fileInfo.isDirectory() ? IDEWorkbenchMessages.ResourceInfo_fileEncodingTitle :
		 * IDEWorkbenchMessages.WorkbenchPreference_encoding, composite); encodingEditor.setPreferenceStore(null);
		 * encodingEditor.setPage(this); encodingEditor.load(); encodingEditor.setPropertyChangeListener(new
		 * IPropertyChangeListener() { public void propertyChange(PropertyChangeEvent event) { if
		 * (event.getProperty().equals(FieldEditor.IS_VALID)) { setValid(encodingEditor.isValid()); } } }); if
		 * (fileInfo.isDirectory()) { lineDelimiterEditor = new LineDelimiterEditor(composite, resource.getProject());
		 * lineDelimiterEditor.doLoad(); }
		 */

		Dialog.applyDialogFont(composite);

		return composite;
	}

	public boolean performOk()
	{
		if (fFileInfo != null && (fFileInfo instanceof IExtendedFileInfo))
		{
			long permissions = fPermissionsGroup.getPermissions();
			IExtendedFileInfo extendedInfo = (IExtendedFileInfo) fFileInfo;
			if (permissions != extendedInfo.getPermissions())
			{
				// the permissions have been modified
				extendedInfo.setPermissions(permissions);

				IFileStore fileStore = Utils.getFileStore(getElement());
				if (fileStore != null)
				{
					try
					{
						fileStore.putInfo(fFileInfo, IExtendedFileInfo.SET_PERMISSIONS, new NullProgressMonitor());
					}
					catch (CoreException e)
					{
						UIUtils.showErrorMessage(Messages.FileInfoPropertyPage_ErrorStoreInfo, e);
					}
				}
			}
		}

		return super.performOk();
	}

	protected void performDefaults()
	{
		if (fPermissionsGroup != null)
		{
			if (fFileInfo.isDirectory())
			{
				fPermissionsGroup.setPermissions(PreferenceUtils.getFolderPermissions(PermissionDirection.UPLOAD));
			}
			else
			{
				fPermissionsGroup.setPermissions(PreferenceUtils.getFilePermissions(PermissionDirection.UPLOAD));
			}
		}
		super.performDefaults();
	}

	protected IPreferenceStore doGetPreferenceStore()
	{
		return IOUIPlugin.getDefault().getPreferenceStore();
	}

	private Composite createBasicInfoGroup(Composite parent, IFileStore fileStore, IFileInfo fileInfo)
	{
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).create());

		Label label = new Label(container, SWT.NONE);
		label.setText(IDEWorkbenchMessages.ResourceInfo_path);
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP).create());

		Text pathText = new Text(container, SWT.WRAP | SWT.READ_ONLY);
		pathText.setText(fileStore.toURI().getPath());
		pathText.setBackground(pathText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		pathText.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
				.hint(convertWidthInCharsToPixels(MAX_VALUE_WIDTH), SWT.DEFAULT).create());

		label = new Label(container, SWT.LEFT);
		label.setText(IDEWorkbenchMessages.ResourceInfo_type);
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP).create());

		Text typeText = new Text(container, SWT.LEFT | SWT.READ_ONLY);
		typeText.setText(fileInfo.isDirectory() ? Messages.FileInfoPropertyPage_Folder
				: Messages.FileInfoPropertyPage_File);
		typeText.setBackground(typeText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		typeText.setLayoutData(GridDataFactory.swtDefaults().create());

		label = new Label(container, SWT.LEFT);
		label.setText(IDEWorkbenchMessages.ResourceInfo_location);
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP).create());

		Text locationText = new Text(container, SWT.WRAP | SWT.READ_ONLY);
		locationText.setText(fileStore.toString());
		locationText.setBackground(locationText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		locationText.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
				.hint(convertWidthInCharsToPixels(MAX_VALUE_WIDTH), SWT.DEFAULT).create());

		if (!fileInfo.isDirectory())
		{
			label = new Label(container, SWT.LEFT);
			label.setText(IDEWorkbenchMessages.ResourceInfo_size);
			label.setLayoutData(GridDataFactory.swtDefaults().create());

			Text sizeText = new Text(container, SWT.LEFT | SWT.READ_ONLY);
			sizeText.setText(MessageFormat.format(Messages.FileInfoPropertyPage_Bytes,
					Long.toString(fileInfo.getLength())));
			sizeText.setBackground(sizeText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			sizeText.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER)
					.hint(convertWidthInCharsToPixels(MAX_VALUE_WIDTH), SWT.DEFAULT).create());
		}

		return container;
	}

	private Composite createStateGroup(Composite parent, IFileStore fileStore, IFileInfo fileInfo)
	{
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).create());

		Label timeStampLabel = new Label(container, SWT.NONE);
		timeStampLabel.setText(IDEWorkbenchMessages.ResourceInfo_lastModified);

		Text timeStampText = new Text(container, SWT.READ_ONLY);
		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
		timeStampText.setText(format.format(new Date(fileInfo.getLastModified())));
		timeStampText.setBackground(timeStampText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		timeStampText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());

		return container;
	}

	private Composite createOwnerGroup(Composite parent, IExtendedFileInfo fileInfo)
	{
		Group container = new Group(parent, SWT.NONE);
		container.setText(Messages.FileInfoPropertyPage_OwnerAndGroup);
		container.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).create());

		Label label = new Label(container, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.FileInfoPropertyPage_Owner));
		Text text = new Text(container, SWT.READ_ONLY);
		text.setText(fileInfo.getOwner());
		text.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

		label = new Label(container, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.FileInfoPropertyPage_Group));
		text = new Text(container, SWT.READ_ONLY);
		text.setText(fileInfo.getGroup());
		text.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

		return container;
	}

	private Composite createPermissionsGroup(Composite parent, IExtendedFileInfo fileInfo)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.FileInfoPropertyPage_Permissions);
		group.setLayout(GridLayoutFactory.fillDefaults().create());

		fPermissionsGroup = new PermissionsGroup(group);
		fPermissionsGroup.setPermissions(fileInfo.getPermissions());
		fPermissionsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return fPermissionsGroup;
	}
}
