/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.IPropertyDialogProvider;
import com.aptana.webserver.core.SimpleWebServer;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.ui.WebServerUIPlugin;

/**
 * @author Max Stepanov
 */
public class SimpleWebServerPropertyDialog extends TitleAreaDialog implements IPropertyDialog
{

	private static final int LABEL_WIDTH = 70;

	private SimpleWebServer source;

	private Text nameText;
	private Text baseUrlText;
	private Text documentRootText;
	private ModifyListener modifyListener;

	/**
	 * @param parentShell
	 */
	public SimpleWebServerPropertyDialog(Shell parentShell)
	{
		super(parentShell);
		setHelpAvailable(false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource()
	{
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IPropertyDialog#setPropertySource(java.lang.Object)
	 */
	public void setPropertySource(Object element)
	{
		source = null;
		if (element instanceof SimpleWebServer)
		{
			source = (SimpleWebServer) element;
		}
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		setTitle(Messages.SimpleWebServerPropertyDialog_Title);
		getShell().setText(Messages.SimpleWebServerPropertyDialog_ShellTitle);

		Composite composite = new Composite(dialogArea, SWT.NONE);
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		PixelConverter converter = new PixelConverter(composite);
		composite.setLayout(GridLayoutFactory
				.swtDefaults()
				.margins(converter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN),
						converter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(converter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING),
						converter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING)).numColumns(3)
				.create());

		/* row 1 */
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.SimpleWebServerPropertyDialog_Name_Label));

		nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.fillDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT).span(2, 1)
				.grab(true, false).create());

		/* row 1 */
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.SimpleWebServerPropertyDialog_BaseURL_Label));

		baseUrlText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		baseUrlText
				.setLayoutData(GridDataFactory
						.swtDefaults()
						.hint(new PixelConverter(baseUrlText)
								.convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
						.span(2, 1).align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		baseUrlText.setText("http://"); //$NON-NLS-1$

		/* row 2 */
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.SimpleWebServerPropertyDialog_DocRoot_Label));

		documentRootText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		documentRootText.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(documentRootText)
						.convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());

		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText('&' + StringUtil.ellipsify(CoreStrings.BROWSE));
		browseButton.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(Math.max(
						new PixelConverter(browseButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
						browseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x), SWT.DEFAULT).create());

		/* -- */
		browseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				browseFileSystem();
			}
		});

		if (source != null)
		{
			String name = source.getName();
			nameText.setText((name != null) ? name : StringUtil.EMPTY);
			URL url = source.getBaseURL();
			if (url != null)
			{
				baseUrlText.setText(url.toExternalForm());
			}
			IPath path = source.getDocumentRootPath();
			if (path != null)
			{
				documentRootText.setText(path.toOSString());
			}
		}

		addListeners();

		return dialogArea;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse. swt.widgets.Composite)
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
		if (source != null)
		{
			source.setName(nameText.getText());
			try
			{
				source.setBaseURL(new URL(baseUrlText.getText()));
			}
			catch (MalformedURLException e)
			{
				IdeLog.logError(WebServerUIPlugin.getDefault(), e);
			}
			IPath path = Path.fromOSString(documentRootText.getText());
			source.setDocumentRootPath(path);
		}
		WebServerCorePlugin.getDefault().saveServerConfigurations();
		super.okPressed();
	}

	private boolean isValid()
	{
		// TODO Ensure name is unique!
		String message = null;
		if (nameText.getText().length() == 0)
		{
			message = Messages.SimpleWebServerPropertyDialog_EmptyNameError;
		}
		else
		{
			try
			{
				if (new URL(baseUrlText.getText()).getHost().length() == 0)
				{
					message = Messages.SimpleWebServerPropertyDialog_InvalidURLError;
				}
			}
			catch (MalformedURLException e)
			{
				message = Messages.SimpleWebServerPropertyDialog_InvalidURLError;
			}
		}
		if (message == null)
		{
			File file = Path.fromOSString(documentRootText.getText()).toFile();
			if (!file.exists() || !file.isDirectory())
			{
				message = Messages.SimpleWebServerPropertyDialog_DocumentRootError;
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

	private void validate()
	{
		boolean valid = isValid();
		getButton(OK).setEnabled(valid);
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
		baseUrlText.addModifyListener(modifyListener);
		documentRootText.addModifyListener(modifyListener);
	}

	protected void removeListeners()
	{
		if (modifyListener != null)
		{
			nameText.removeModifyListener(modifyListener);
			baseUrlText.removeModifyListener(modifyListener);
			documentRootText.removeModifyListener(modifyListener);
		}
	}

	private void browseFileSystem()
	{
		DirectoryDialog dlg = new DirectoryDialog(getShell());
		dlg.setFilterPath(documentRootText.getText());
		String path = dlg.open();
		if (path != null)
		{
			documentRootText.setText(Path.fromOSString(path).toPortableString());
		}
	}

	public static class Provider implements IPropertyDialogProvider
	{

		/*
		 * (non-Javadoc)
		 * @see com.aptana.ui.IPropertyDialogProvider#createPropertyDialog(org.eclipse .jface.window.IShellProvider)
		 */
		public Dialog createPropertyDialog(IShellProvider shellProvider)
		{
			return new SimpleWebServerPropertyDialog(shellProvider.getShell());
		}

	}

}
