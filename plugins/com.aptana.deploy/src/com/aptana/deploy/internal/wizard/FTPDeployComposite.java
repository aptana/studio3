package com.aptana.deploy.internal.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.ide.ui.secureftp.internal.CommonFTPConnectionPropertyComposite;

@SuppressWarnings("restriction")
public class FTPDeployComposite extends CommonFTPConnectionPropertyComposite implements SelectionListener
{

	private Button autoSyncCheckbox;
	private Button uploadButton;
	private Button downloadButton;
	private Button syncButton;

	public FTPDeployComposite(Composite parent, int style, IBaseRemoteConnectionPoint connectionPoint, Listener listener)
	{
		super(parent, style, connectionPoint, listener);

		createAutoSyncOptions(this);
		optionsExpandable.setExpanded(true);
	}

	public boolean isAutoSyncSelected()
	{
		return autoSyncCheckbox.getSelection();
	}

	public SyncDirection getSyncDirection()
	{
		if (uploadButton.getSelection())
		{
			return SyncDirection.UPLOAD;
		}
		if (downloadButton.getSelection())
		{
			return SyncDirection.DOWNLOAD;
		}
		return SyncDirection.BOTH;
	}

	private void createAutoSyncOptions(Composite parent)
	{
		Composite sync = new Composite(parent, SWT.NONE);
		sync.setLayout(GridLayoutFactory.fillDefaults().create());
		sync.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.END).span(2, 1).create());

		autoSyncCheckbox = new Button(sync, SWT.CHECK);
		autoSyncCheckbox.setText(Messages.FTPDeployComposite_AutoSync);
		autoSyncCheckbox.setSelection(true);
		autoSyncCheckbox.addSelectionListener(this);

		uploadButton = new Button(sync, SWT.RADIO);
		uploadButton.setText(Messages.FTPDeployComposite_Upload);
		uploadButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());

		downloadButton = new Button(sync, SWT.RADIO);
		downloadButton.setText(Messages.FTPDeployComposite_Download);
		downloadButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());

		syncButton = new Button(sync, SWT.RADIO);
		syncButton.setText(Messages.FTPDeployComposite_Synchronize);
		syncButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());
		syncButton.setSelection(true);

		updateEnableState();
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();

		if (source == autoSyncCheckbox)
		{
			updateEnableState();
		}
	}

	private void updateEnableState()
	{
		boolean enabled = autoSyncCheckbox.getSelection();
		uploadButton.setEnabled(enabled);
		downloadButton.setEnabled(enabled);
		syncButton.setEnabled(enabled);
	}
}
