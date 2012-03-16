/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.dialogs;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ui.IPropertyDialog;

/**
 * @author Max Stepanov
 */
public class LocalConnectionPropertyDialog extends TitleAreaDialog implements IPropertyDialog
{

	private static final String DEFAULT_NAME = Messages.LocalConnectionPropertyDialog_NewShortcut;

	private LocalConnectionPoint localConnectionPoint;
	private boolean isNew = false;

	private Text nameText;
	private Text localPathText;
	private Button browseButton;

	private Image titleImage;

	private ModifyListener modifyListener;

	/**
	 * @param parentShell
	 */
	public LocalConnectionPropertyDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertyDialog#setPropertySource(java.lang.Object)
	 */
	public void setPropertySource(Object element)
	{
		localConnectionPoint = null;
		if (element instanceof LocalConnectionPoint)
		{
			localConnectionPoint = (LocalConnectionPoint) element;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource()
	{
		return localConnectionPoint;
	}

	private String getConnectionPointType()
	{
		return LocalConnectionPoint.TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		titleImage = IOUIPlugin.getImageDescriptor("/icons/full/wizban/local.png").createImage(); //$NON-NLS-1$
		dialogArea.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (titleImage != null)
				{
					setTitleImage(null);
					titleImage.dispose();
					titleImage = null;
				}
			}
		});

		setTitleImage(titleImage);
		if (localConnectionPoint != null)
		{
			setTitle(Messages.LocalConnectionPropertyDialog_EditTitle);
			getShell().setText(Messages.LocalConnectionPropertyDialog_EditText);
		}
		else
		{
			setTitle(Messages.LocalConnectionPropertyDialog_CreateTitle);
			getShell().setText(Messages.LocalConnectionPropertyDialog_CreateText);
		}

		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory
				.swtDefaults()
				.margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN),
						convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING),
						convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING)).numColumns(3).create());

		/* row 1 */
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
						SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.LocalConnectionPropertyDialog_ShortcutName));

		nameText = new Text(container, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.fillDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT).span(2, 1)
				.grab(true, false).create());

		/* row 2 */
		label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
						SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.LocalConnectionPropertyDialog_LocalPath));

		localPathText = new Text(container, SWT.SINGLE | SWT.BORDER);
		localPathText.setLayoutData(GridDataFactory.swtDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT).grab(true, false)
				.create());

		browseButton = new Button(container, SWT.PUSH);
		browseButton.setText('&' + StringUtil.ellipsify(CoreStrings.BROWSE));
		browseButton.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(Math.max(
						new PixelConverter(browseButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
						browseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x), SWT.DEFAULT).create());

		/* -- */
		addListeners();

		browseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				browseFileSystem();
			}
		});

		if (localConnectionPoint == null)
		{
			try
			{
				localConnectionPoint = (LocalConnectionPoint) CoreIOPlugin.getConnectionPointManager()
						.createConnectionPoint(getConnectionPointType());
				localConnectionPoint.setName(DEFAULT_NAME);
				isNew = true;
			}
			catch (CoreException e)
			{
				IdeLog.logError(IOUIPlugin.getDefault(), Messages.LocalConnectionPropertyDialog_FailedToCreate, e);
				close(); // $codepro.audit.disable closeInFinally
			}
		}
		loadPropertiesFrom(localConnectionPoint);

		return dialogArea;
	}

	protected void addListeners()
	{
		if (modifyListener == null)
		{
			modifyListener = new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					validate();
				}
			};
		}
		nameText.addModifyListener(modifyListener);
		localPathText.addModifyListener(modifyListener);
	}

	protected void removeListeners()
	{
		if (modifyListener != null)
		{
			nameText.removeModifyListener(modifyListener);
			localPathText.removeModifyListener(modifyListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		if (!isValid())
		{
			return;
		}
		if (savePropertiesTo(localConnectionPoint))
		{
			/* TODO: notify */
			localConnectionPoint.hashCode();
		}
		if (isNew)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(localConnectionPoint);
		}
		super.okPressed();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		try
		{
			return super.createContents(parent);
		}
		finally
		{
			validate();
		}
	}

	protected void loadPropertiesFrom(LocalConnectionPoint connectionPoint)
	{
		removeListeners();
		try
		{
			nameText.setText(valueOrEmpty(connectionPoint.getName()));
			IPath path = connectionPoint.getPath();
			localPathText.setText((path != null) ? path.toPortableString() : ""); //$NON-NLS-1$
		}
		finally
		{
			addListeners();
		}
	}

	protected boolean savePropertiesTo(LocalConnectionPoint connectionPoint)
	{
		boolean updated = false;
		String name = nameText.getText();
		if (!name.equals(connectionPoint.getName()))
		{
			connectionPoint.setName(name);
			updated = true;
		}
		IPath path = Path.fromPortableString(localPathText.getText());
		if (!path.equals(connectionPoint.getPath()))
		{
			connectionPoint.setPath(path);
			updated = true;
		}
		return updated;
	}

	private void browseFileSystem()
	{
		DirectoryDialog dlg = new DirectoryDialog(getShell());
		dlg.setFilterPath(localPathText.getText());
		String path = dlg.open();
		if (path != null)
		{
			localPathText.setText(Path.fromOSString(path).toPortableString());
			if (DEFAULT_NAME.equals(nameText.getText()))
			{
				nameText.setText(Path.fromOSString(path).lastSegment());
			}
		}
	}

	public void validate()
	{
		boolean valid = isValid();
		getButton(OK).setEnabled(valid);
	}

	public boolean isValid()
	{
		String message = null;
		String name = nameText.getText().trim();
		if (name.length() == 0)
		{
			message = Messages.LocalConnectionPropertyDialog_SpecifyShortcutName;
		}
		else if ((localConnectionPoint == null || !name.equals(localConnectionPoint.getName()))
				&& !ConnectionPointUtils.isConnectionPointNameUnique(name))
		{
			message = MessageFormat.format(Messages.LocalConnectionPropertyDialog_DuplicateShortcutName, name);
		}
		else
		{
			File file = Path.fromPortableString(localPathText.getText()).toFile();
			if (!file.exists() || !file.isDirectory())
			{
				message = Messages.LocalConnectionPropertyDialog_LocationNotExist;
			}
		}
		if (message != null)
		{
			setErrorMessage(message);
		}
		else
		{
			setErrorMessage(null);
			setMessage(null);
			return true;
		}
		return false;
	}

	protected static String valueOrEmpty(String value)
	{
		if (value != null)
		{
			return value;
		}
		return StringUtil.EMPTY;
	}
}
