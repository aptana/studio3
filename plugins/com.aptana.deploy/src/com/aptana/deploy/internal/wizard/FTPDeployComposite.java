package com.aptana.deploy.internal.wizard;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.aptana.deploy.Activator;
import com.aptana.deploy.preferences.IPreferenceConstants;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.ui.secureftp.internal.CommonFTPConnectionPropertyComposite;

@SuppressWarnings("restriction")
public class FTPDeployComposite extends CommonFTPConnectionPropertyComposite implements SelectionListener
{

	public static enum Direction
	{
		UPLOAD, DOWNLOAD, BOTH
	}

	private Button autoSyncCheckbox;
	private Button uploadButton;
	private Button downloadButton;

	public FTPDeployComposite(Composite parent, int style, IBaseRemoteConnectionPoint connectionPoint, Listener listener)
	{
		super(parent, style, connectionPoint, listener);

		createAutoSyncOptions(this);
	}

	public boolean isAutoSyncSelected()
	{
		return autoSyncCheckbox.getSelection();
	}

	public Direction getSyncDirection()
	{
		if (uploadButton.getSelection())
		{
			return Direction.UPLOAD;
		}
		if (downloadButton.getSelection())
		{
			return Direction.DOWNLOAD;
		}
		return Direction.BOTH;
	}

	private void createAutoSyncOptions(Composite parent)
	{
		Composite sync = new Composite(parent, SWT.NONE);
		sync.setLayout(GridLayoutFactory.fillDefaults().create());
		sync.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.END).span(2, 1).create());

		autoSyncCheckbox = new Button(sync, SWT.CHECK);
		autoSyncCheckbox.setText(Messages.FTPDeployComposite_AutoSync);
		autoSyncCheckbox.setSelection(Platform.getPreferencesService().getBoolean(Activator.getPluginIdentifier(),
				IPreferenceConstants.AUTO_SYNC, true, null));
		autoSyncCheckbox.addSelectionListener(this);

		uploadButton = new Button(sync, SWT.RADIO);
		uploadButton.setText(Messages.FTPDeployComposite_Upload);
		uploadButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());
		uploadButton.setSelection(true);

		downloadButton = new Button(sync, SWT.RADIO);
		downloadButton.setText(Messages.FTPDeployComposite_Download);
		downloadButton.setLayoutData(GridDataFactory.swtDefaults().indent(10, 0).create());

		updateEnableState();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	@Override
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
	}
}
