/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.TarUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.ide.core.io.downloader.DownloadManager;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodeJSService;
import com.aptana.js.core.preferences.IPreferenceConstants;
import com.aptana.ui.util.UIUtils;

public class NodePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	public static final String NODE_JS_SOURCE_URL = "http://go.appcelerator.com/nodejs-source"; //$NON-NLS-1$
	public static final String NODE_JS_ROOT_NAME = "node-v0.8.3"; //$NON-NLS-1$

	public static final String ID = "com.aptana.editor.js.nodejs.page"; //$NON-NLS-1$

	private StringFieldEditor sfe;
	private Composite fep;

	private DirectoryFieldEditor sourceEditor;

	public NodePreferencePage()
	{
		// This will align the field-editors in a nice way.
		super(FieldEditorPreferencePage.GRID);
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		// Node Executable location
		FileFieldEditor fileEditor = new FileFieldEditor(IPreferenceConstants.NODEJS_EXECUTABLE_PATH,
				StringUtil.makeFormLabel(Messages.NodePreferencePage_LocationLabel), true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent())
		{

			@Override
			protected boolean doCheckState()
			{
				// Now check that the executable is ok
				String text = getTextControl().getText();
				if (!StringUtil.isEmpty(text))
				{
					IStatus status = getNodeService().acceptBinary(Path.fromOSString(text));
					if (!status.isOK())
					{
						setErrorMessage(status.getMessage());
						return false;
					}
				}

				return true;
			}
		};
		addField(fileEditor);

		sfe = new StringFieldEditor("some_non_existent_pref_key", //$NON-NLS-1$
				StringUtil.makeFormLabel(Messages.NodePreferencePage_DetectedPathLabel), fep = getFieldEditorParent());
		addField(sfe);

		// Node Source location
		sourceEditor = new DirectoryFieldEditor(IPreferenceConstants.NODEJS_SOURCE_PATH,
				StringUtil.makeFormLabel(Messages.NodePreferencePage_SourceLocationLabel), getFieldEditorParent())
		{
			@Override
			public int getNumberOfControls()
			{
				return super.getNumberOfControls() + 1;
			}

			@Override
			protected void doFillIntoGrid(Composite parent, int numColumns)
			{
				super.doFillIntoGrid(parent, numColumns - 1);
				createDownloadButton(parent);
				Text textControl = getTextControl();
				((GridData) textControl.getLayoutData()).widthHint = convertHorizontalDLUsToPixels(textControl, 180);
			}

			@Override
			protected void adjustForNumColumns(int numColumns)
			{
				super.adjustForNumColumns(numColumns - 1);
			}

			@Override
			protected boolean doCheckState()
			{
				// Now check that the dir is ok
				String text = getTextControl().getText();
				if (!StringUtil.isEmpty(text))
				{
					IPath path = Path.fromOSString(text);
					IStatus status = getNodeService().validateSourcePath(path);
					if (!status.isOK())
					{
						setErrorMessage(status.getMessage());
						return false;
					}
				}

				return true;
			}

			// Create the NodeJS download button
			private Button createDownloadButton(Composite parent)
			{
				Button downloadBt = new Button(parent, SWT.PUSH);
				downloadBt.setText(Messages.NodePreferencePage_downloadButtonText);
				downloadBt.setFont(parent.getFont());
				GridData gd = GridDataFactory.fillDefaults().create();
				int widthHint = convertHorizontalDLUsToPixels(downloadBt, IDialogConstants.BUTTON_WIDTH);
				gd.widthHint = Math.max(widthHint, downloadBt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
				downloadBt.setLayoutData(gd);
				downloadBt.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent evt)
					{
						downloadNodeJS();
					}
				});
				return downloadBt;
			}
		};
		addField(sourceEditor);
	}

	private void downloadNodeJS()
	{
		// First, ask for the location that the source will be extracted into.
		final IPath selectedDir = getDirectory();
		if (selectedDir == null)
		{
			return;
		}

		final ProgressMonitorDialog downloadProgressMonitor = new ProgressMonitorDialog(UIUtils.getActiveShell());
		try
		{
			downloadProgressMonitor.run(true, true, new IRunnableWithProgress()
			{

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					final DownloadManager dm = new DownloadManager();
					try
					{
						dm.addURI(new URI(NODE_JS_SOURCE_URL));
						IStatus status = dm.start(monitor);
						if (status.isOK())
						{
							// We got the source code. Now we need to extract it and set the value in the text field (or
							// the preferences, in case the dialog was closed).
							List<IPath> contentPaths = dm.getContentsLocations();
							status = TarUtil.extractTGZFile(contentPaths.get(0), selectedDir);
							if (status.isOK())
							{
								// Set the path in the editor field
								Control control = NodePreferencePage.this.getControl();
								if (control != null && !control.isDisposed())
								{
									UIUtils.runInUIThread(new Runnable()
									{

										public void run()
										{
											sourceEditor.setStringValue(selectedDir.append(NODE_JS_ROOT_NAME)
													.toOSString());
										}
									});
								}
							}
						}
					}
					catch (Exception e)
					{
						IdeLog.logError(JSPlugin.getDefault(), "Error while downloading NodeJS sources", e); //$NON-NLS-1$
					}

				}
			});
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
		}
		// setStringValue(value)
	}

	private IPath getDirectory()
	{
		DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
		fileDialog.setMessage(MessageFormat.format(Messages.NodePreferencePage_nodejsDirSelectionMessage,
				NODE_JS_ROOT_NAME));
		String dir = fileDialog.open();
		if (!StringUtil.isEmpty(dir))
		{
			return Path.fromOSString(dir);
		}
		return null;
	}

	@Override
	protected void initialize()
	{
		super.initialize();

		INodeJS path = getDetectedPath();
		sfe.setStringValue(path == null ? Messages.NodePreferencePage_NotDetected : path.getPath().toOSString());
		sfe.setEnabled(false, fep);
	}

	private INodeJS getDetectedPath()
	{
		return getNodeService().detectInstall();
	}

	protected INodeJSService getNodeService()
	{
		return JSCorePlugin.getDefault().getNodeJSService();
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, JSCorePlugin.PLUGIN_ID);
	}
}
