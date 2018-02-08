/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.views;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia (mxia@aptana.com)
 */
public class DirectionToolBar
{

	public static interface Client
	{
		/**
		 * Indicates the selection has changed.
		 * 
		 * @param direction
		 *            the new direction selected
		 * @param reload
		 *            true if the content should reload, false otherwise
		 */
		public void selectionChanged(int direction, boolean reload);
	}

	public static final int BOTH = 0;
	public static final int UPLOAD = 1;
	public static final int DOWNLOAD = 2;
	public static final int FORCE_UPLOAD = 3;
	public static final int FORCE_DOWNLOAD = 4;

	private ToolBar fDirectionBar;
	private ToolItem fDirectionDown;
	private MenuItem fBoth;
	private MenuItem fUpload;
	private MenuItem fForceUpload;
	private MenuItem fDownload;
	private MenuItem fForceDownload;

	private MenuItem fPreviousSelection;

	private String fEndpoint1;
	private String fEndpoint2;

	private Client fClient;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param client
	 *            the client to be notified for possible events
	 */
	public DirectionToolBar(Composite parent, Client client, String endpoint1, String endpoint2)
	{
		fClient = client;
		fEndpoint1 = endpoint1;
		fEndpoint2 = endpoint2;
		fDirectionBar = createContents(parent);
	}

	/**
	 * Gets the control for the toolbar.
	 * 
	 * @return the control for the toolbar
	 */
	public Control getControl()
	{
		return fDirectionBar;
	}

	/**
	 * Returns the current menu selection.
	 * 
	 * @return the current menu selection (could be BOTH, UPLOAD, DOWNLOAD, FORCE_UPLOAD, or FORCE_DOWNLOAD)
	 */
	public int getSelection()
	{
		if (fUpload.getSelection())
		{
			return UPLOAD;
		}
		if (fDownload.getSelection())
		{
			return DOWNLOAD;
		}
		if (fForceUpload.getSelection())
		{
			return FORCE_UPLOAD;
		}
		if (fForceDownload.getSelection())
		{
			return FORCE_DOWNLOAD;
		}
		return BOTH;
	}

	/**
	 * Sets the current selected item.
	 * 
	 * @param direction
	 *            the item to be selected (could be BOTH, UPLOAD, DOWNLOAD, FORCE_UPLOAD, or FORCE_DOWNLOAD)
	 */
	public void setSelection(int direction)
	{
		fBoth.setSelection(false);
		fUpload.setSelection(false);
		fDownload.setSelection(false);
		fForceUpload.setSelection(false);
		fForceDownload.setSelection(false);
		switch (direction)
		{
			case BOTH:
				fBoth.setSelection(true);
				fPreviousSelection = fBoth;
				break;
			case UPLOAD:
				fUpload.setSelection(true);
				fPreviousSelection = fUpload;
				break;
			case DOWNLOAD:
				fDownload.setSelection(true);
				fPreviousSelection = fDownload;
				break;
			case FORCE_UPLOAD:
				fForceUpload.setSelection(true);
				fPreviousSelection = fForceUpload;
				break;
			case FORCE_DOWNLOAD:
				fForceDownload.setSelection(true);
				fPreviousSelection = fForceDownload;
				break;
		}
		updateText();
		updateToolTip();
	}

	/**
	 * Set the tool bar to be enabled or disabled.
	 * 
	 * @param enabled
	 *            true if the tool bar is to be enabled, false for disabling it
	 */
	public void setEnabled(boolean enabled)
	{
		if (!fDirectionBar.isDisposed())
		{
			fDirectionBar.setEnabled(enabled);
		}
	}

	private ToolBar createContents(final Composite parent)
	{
		final ToolBar directionBar = new ToolBar(parent, SWT.FLAT);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		directionBar.setLayout(layout);

		fDirectionDown = new ToolItem(directionBar, SWT.DROP_DOWN);
		fDirectionDown.setImage(SyncingUIPlugin.getImage("icons/full/elcl16/arrow_up_down.png")); //$NON-NLS-1$

		final Menu directionMenu = new Menu(directionBar);
		fDirectionDown.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				// displays the menu when user clicks on the drop-down arrow
				Rectangle rect = directionBar.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = parent.toDisplay(pt);
				directionMenu.setLocation(pt.x, pt.y);
				directionMenu.setVisible(true);
			}

		});

		fBoth = new MenuItem(directionMenu, SWT.RADIO);
		fBoth.setText(Messages.SmartSyncDialog_BothDirection);
		fBoth.setSelection(true);

		SelectionAdapter directionAdapter = new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				MenuItem item = (MenuItem) e.widget;
				if (!item.getSelection())
				{
					return;
				}

				updateText();
				updateToolTip();
				parent.getParent().layout(true, true);

				boolean load = true;
				if (fBoth.getSelection() || fUpload.getSelection() || fDownload.getSelection())
				{
					if (fPreviousSelection == fBoth || fPreviousSelection == fUpload || fPreviousSelection == fDownload)
					{
						load = false;
					}
				}
				else if (fForceUpload.getSelection() || fForceDownload.getSelection())
				{
					if (fPreviousSelection == item)
					{
						load = false;
					}
				}
				fPreviousSelection = item;
				fClient.selectionChanged(getSelection(), load);
			}

		};
		fBoth.addSelectionListener(directionAdapter);

		fUpload = new MenuItem(directionMenu, SWT.RADIO);
		fUpload.setText(Messages.SmartSyncDialog_Upload);
		fUpload.addSelectionListener(directionAdapter);

		fForceUpload = new MenuItem(directionMenu, SWT.RADIO);
		fForceUpload.setText(Messages.SmartSyncDialog_UploadAll);
		fForceUpload.addSelectionListener(directionAdapter);

		fDownload = new MenuItem(directionMenu, SWT.RADIO);
		fDownload.setText(Messages.SmartSyncDialog_Download);
		fDownload.addSelectionListener(directionAdapter);

		fForceDownload = new MenuItem(directionMenu, SWT.RADIO);
		fForceDownload.setText(Messages.SmartSyncDialog_DownloadAll);
		fForceDownload.addSelectionListener(directionAdapter);

		// fDirectionDown.setText(fBoth.getText());
		updateToolTip();
		fPreviousSelection = fBoth;

		return directionBar;
	}

	private void updateText()
	{
		if (fBoth.getSelection())
		{
			fDirectionDown.setImage(SyncingUIPlugin.getImage("icons/full/elcl16/arrow_up_down.png")); //$NON-NLS-1$
			// fDirectionDown.setText(fBoth.getText());
		}
		else if (fUpload.getSelection())
		{
			fDirectionDown.setImage(SyncingUIPlugin.getImage("icons/full/elcl16/arrow_up.png")); //$NON-NLS-1$
			// fDirectionDown.setText(fUpload.getText());
		}
		else if (fForceUpload.getSelection())
		{
			fDirectionDown.setImage(SyncingUIPlugin.getImage("icons/full/obj16/arrow_up_emphasis.png")); //$NON-NLS-1$
			// fDirectionDown.setText(fForceUpload.getText());
		}
		else if (fDownload.getSelection())
		{
			fDirectionDown.setImage(SyncingUIPlugin.getImage("icons/full/elcl16/arrow_down.png")); //$NON-NLS-1$
			// fDirectionDown.setText(fDownload.getText());
		}
		else if (fForceDownload.getSelection())
		{
			fDirectionDown.setImage(SyncingUIPlugin.getImage("icons/full/obj16/arrow_down_emphasis.png")); //$NON-NLS-1$
			// fDirectionDown.setText(fForceDownload.getText());
		}
	}

	private void updateToolTip()
	{
		if (fBoth.getSelection())
		{
			fDirectionDown.setToolTipText(MessageFormat.format(Messages.DirectionToolBar_SyncToolTip, fEndpoint1,
					fEndpoint2));
		}
		else if (fUpload.getSelection())
		{
			fDirectionDown.setToolTipText(MessageFormat.format(Messages.DirectionToolBar_UploadToolTip, fEndpoint2,
					fEndpoint1));
		}
		else if (fForceUpload.getSelection())
		{
			fDirectionDown.setToolTipText(MessageFormat.format(Messages.DirectionToolBar_ForceUploadToolTip,
					fEndpoint2, fEndpoint1));
		}
		else if (fDownload.getSelection())
		{
			fDirectionDown.setToolTipText(MessageFormat.format(Messages.DirectionToolBar_DownloadToolTip, fEndpoint1,
					fEndpoint2));
		}
		else if (fForceDownload.getSelection())
		{
			fDirectionDown.setToolTipText(MessageFormat.format(Messages.DirectionToolBar_ForceDownloadToolTip,
					fEndpoint1, fEndpoint2));
		}
	}

}
