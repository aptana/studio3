/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old.views;

import java.io.File;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.WorkspaceConnectionPoint;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.syncing.core.old.ConnectionPointSyncPair;
import com.aptana.ide.syncing.core.old.ILogger;
import com.aptana.ide.syncing.core.old.ISyncEventHandler;
import com.aptana.ide.syncing.core.old.ISyncResource;
import com.aptana.ide.syncing.core.old.SyncFile;
import com.aptana.ide.syncing.core.old.SyncFolder;
import com.aptana.ide.syncing.core.old.SyncJob;
import com.aptana.ide.syncing.core.old.SyncModelBuilder;
import com.aptana.ide.syncing.core.old.SyncState;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.core.old.handlers.SyncEventHandlerAdapterWithProgressMonitor;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.old.SyncingConsole;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ide.ui.io.navigator.RemoteNavigatorView;
import com.aptana.ui.UIPlugin;
import com.aptana.ui.ftp.preferences.UpdatePermissionsComposite;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.SearchComposite;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia (mxia@aptana.com)
 */
@SuppressWarnings("deprecation")
public class SmartSyncDialog extends TitleAreaDialog implements SelectionListener, ModifyListener,
		DirectionToolBar.Client, OptionsToolBar.Client, SyncJob.Client, SearchComposite.Client
{

	private static final String ICON = "icons/full/elcl16/arrow_up_down.png"; //$NON-NLS-1$
	private static final String ICON_UPDATE = "icons/full/obj16/sync.png"; //$NON-NLS-1$
	private static final String ICON_SKIP = "icons/full/obj16/skip.png"; //$NON-NLS-1$
	private static final String ICON_DELETE = "icons/full/obj16/delete.png"; //$NON-NLS-1$
	private static final String IMAGE_LOCAL_SITE = "icons/full/wizban/local_site.png"; //$NON-NLS-1$
	private static final String IMAGE_REMOTE_SITE = "icons/full/wizban/remote_site.png"; //$NON-NLS-1$
	private static final String CLOSE_ICON = "icons/full/elcl16/close.png"; //$NON-NLS-1$

	/**
	 * Key to store the dialog settings for the initial directory to open when exporting themes (saves last directory).
	 */
	private static final String LOG_EXPORT_DIRECTORY = "logExportDirectory"; //$NON-NLS-1$

	private static final String CLOSE_WHEN_DONE = "com.aptana.ide.syncing.views.CLOSE_WHEN_DONE"; //$NON-NLS-1$
	private static final String COMPARE_IN_BACKGROUND = IPreferenceConstants.COMPARE_IN_BACKGROUND;
	private static final String USE_CRC = IPreferenceConstants.USE_CRC;

	private static final String SKIPPED_LABEL = Messages.SmartSyncDialog_NumFilesToSkip;
	private static final String UPDATED_LABEL = Messages.SmartSyncDialog_NumFilesToUpdate;
	private static final String DELETED_LABEL = Messages.SmartSyncDialog_NumFilesToDelete;
	private static final String SYNC_LABEL = Messages.SmartSyncDialog_Comparing;

	private Composite dialogArea;
	private Label updatedLabel;
	private Label skippedLabel;
	private Label deletedLabel;
	private Font boldFont;
	private SmartSyncViewer syncViewer;
	private Button startSync;
	private Button cancel;
	private Button closeWhenDone;
	private Button deleteRemoteFiles;
	private Button deleteLocalFiles;
	private Button useCrc;
	private Button syncInBackground;

	private UpdatePermissionsComposite uploadPermComposite;
	private UpdatePermissionsComposite downloadPermComposite;

	private Composite loadingComp;
	private Label loadingLabel;

	private DirectionToolBar directionBar;
	private OptionsToolBar optionsBar;

	private SyncFolder root;
	private String end1;
	private String end2;
	private Synchronizer syncer;
	private IFileStore source;
	private IFileStore dest;
	private IConnectionPoint sourceConnectionPoint;
	private IConnectionPoint destConnectionPoint;
	private ISyncEventHandler handler;
	private boolean compareInBackground;

	private Composite swappable;
	private Composite errorComp;
	private Label errorLabel;
	private Link retryLink;
	private Composite synced;
	private Label syncedIcon;
	private Label syncedText;
	private SyncJob syncJob;

	private int skipped;

	private Job buildSmartSync;

	private IFileStore[] sourceFilesToBeSynced;
	private IFileStore[] destFilesToBeSynced;
	private Image titleImage;
	private Button left_arrow;
	private Button right_arrow;
	private Label sync_label;
	private GridData filterLayoutData;
	private CLabel filterLabel;
	private Composite filterComp;
	private ViewerFilter viewerFilter;
	private String searchText;
	private Button saveLog;
	private Pattern searchPattern;
	private SearchComposite searchComposite;

	/**
	 * Creates a new sync dialog.
	 * 
	 * @param parent
	 *            the parent shell
	 * @param file1
	 *            the first file element
	 * @param file2
	 *            the second file element
	 * @param end1
	 *            the first end point
	 * @param end2
	 *            the second end point
	 */
	public SmartSyncDialog(Shell parent, IConnectionPoint sourceManager, IConnectionPoint destManager,
			IFileStore source, IFileStore dest, String end1, String end2)
	{
		super(parent);
		sourceConnectionPoint = sourceManager;
		destConnectionPoint = destManager;
		setShellStyle(getDefaultOrientation() | SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		this.source = source;
		this.dest = dest;
		this.end1 = end1;
		this.end2 = end2;
		this.compareInBackground = getCoreUIPreferenceStore().getBoolean(COMPARE_IN_BACKGROUND);
		this.syncer = new Synchronizer(getCoreUIPreferenceStore().getBoolean(USE_CRC), 1000);
		if (source != null)
		{
			this.syncer.setClientFileManager(sourceManager);
			this.syncer.setClientFileRoot(source);
		}
		if (dest != null)
		{
			this.syncer.setServerFileManager(destManager);
			this.syncer.setServerFileRoot(dest);
		}
		this.syncer.setLogger(new ILogger()
		{
			public void logWarning(String message, Throwable th)
			{
				SyncingConsole.println(message);
			}

			public void logInfo(String message, Throwable th)
			{
				SyncingConsole.println(message);
			}

			public void logError(String message, Throwable th)
			{
				SyncingConsole.println(message);
			}
		});
	}

	/**
	 * Creates a new sync dialog on a list of selected files.
	 * 
	 * @param parent
	 *            the parent shell
	 * @param conf
	 *            the file manager pair
	 * @param sourceFilesToBeSynced
	 *            the selected files to be synced
	 * @throws CoreException
	 * @throws CoreException
	 */
	public SmartSyncDialog(Shell parent, ConnectionPointSyncPair conf, IFileStore[] sourceFilesToBeSynced,
			IFileStore[] destFilesToBeSynced) throws CoreException
	{
		this(parent, conf.getSourceFileManager(), conf.getDestinationFileManager(), conf.getSourceFileManager()
				.getRoot(), conf.getDestinationFileManager().getRoot(), conf.getSourceFileManager().getName(), conf
				.getDestinationFileManager().getName());
		this.syncer.setClientFileManager(conf.getSourceFileManager());
		this.syncer.setServerFileManager(conf.getDestinationFileManager());
		sourceConnectionPoint = conf.getSourceFileManager();
		destConnectionPoint = conf.getDestinationFileManager();
		this.syncer.setClientFileRoot(sourceConnectionPoint.getRoot());
		this.syncer.setServerFileRoot(destConnectionPoint.getRoot());

		if (sourceFilesToBeSynced == null || sourceFilesToBeSynced.length == 0)
		{
			this.sourceFilesToBeSynced = null;
		}
		else
		{
			this.sourceFilesToBeSynced = sourceFilesToBeSynced;
			if (sourceFilesToBeSynced.length == 1)
			{
				IPath path = EFSUtils.getRelativePath(sourceConnectionPoint, sourceFilesToBeSynced[0]);
				if (path == null || Path.EMPTY.equals(path))
				{
					// the selection is from the project level, so we are doing a full sync
					this.sourceFilesToBeSynced = null;
				}
				else
				{
					end1 = MessageFormat.format("{0} ({1})", end1, path); //$NON-NLS-1$
					if (destFilesToBeSynced == null || destFilesToBeSynced.length == 0)
					{
						// checks if the path exists on remote also
						IFileStore fileStore = destConnectionPoint.getRoot().getFileStore(path);
						if (Utils.exists(fileStore))
						{
							this.destFilesToBeSynced = new IFileStore[] { fileStore };
							return;
						}
					}
				}
			}
		}
		if (destFilesToBeSynced == null || destFilesToBeSynced.length == 0)
		{
			this.destFilesToBeSynced = null;
		}
		else
		{
			this.destFilesToBeSynced = destFilesToBeSynced;
			if (destFilesToBeSynced.length == 1)
			{
				IPath path = EFSUtils.getRelativePath(destConnectionPoint, destFilesToBeSynced[0]);
				if (path == null || Path.EMPTY.equals(path))
				{
					// can't find the path
					this.destFilesToBeSynced = null;
				}
				else
				{
					end2 = MessageFormat.format("{0} ({1})", end2, path); //$NON-NLS-1$
				}
			}
		}
	}

	private void disconnectAndClose()
	{
		// disconnects explicitly upon closing if the sync is completed
		Job disconnectJob = new Job("disconnect the sync file manager") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if (buildSmartSync != null)
				{
					if (buildSmartSync.getResult() == null)
					{
						buildSmartSync.cancel();
					}
					try
					{
						buildSmartSync.join();
					}
					catch (InterruptedException e)
					{
					}
				}
				if (syncJob != null)
				{
					if (syncJob.getResult() == null)
					{
						syncJob.cancel();
					}
					try
					{
						syncJob.join();
					}
					catch (InterruptedException e)
					{
					}
				}
				syncer.disconnect();
				return Status.OK_STATUS;
			}

		};
		disconnectJob.setPriority(Job.INTERACTIVE);
		try
		{
			disconnectJob.setSystem(false);
		}
		catch (IllegalStateException ise)
		{
			// ignore
		}
		disconnectJob.schedule();
		close();
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.SmartSyncDialog_Title);
		newShell.setImage(SyncingUIPlugin.getImage(ICON));
	}

	private Composite createDirectionOptions(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));

		optionsBar = new OptionsToolBar(main, this);
		optionsBar.setPresentationType(getPresentationTypePref());
		optionsBar.setShowDatesSelected(getShowModificationTimePref());
		optionsBar.setEnabled(false);

		directionBar = new DirectionToolBar(main, this, end1, end2);
		directionBar.setSelection(getDirectionPref());
		directionBar.setEnabled(false);
		GridData gridData = new GridData(SWT.END, SWT.CENTER, true, false);
		directionBar.getControl().setLayoutData(gridData);

		return main;
	}

	private Composite createHeader(Composite parent)
	{
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite description = new Composite(top, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		description.setLayout(layout);
		description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label descriptionLabel = new Label(description, SWT.LEFT);
		FontData[] data = SWTUtils.resizeFont(top.getFont(), 4);
		for (int i = 0; i < data.length; i++)
		{
			data[i].setStyle(SWT.BOLD);
		}
		final Font headerFont = new Font(top.getDisplay(), data);
		descriptionLabel.setFont(headerFont);
		descriptionLabel.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				headerFont.dispose();
			}
		});

		Composite endpoints = new Composite(description, SWT.NONE);
		layout = new GridLayout(3, true);
		layout.marginHeight = 0;
		layout.marginWidth = 10;
		layout.verticalSpacing = 0;
		endpoints.setLayout(layout);
		endpoints.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		Label local_site = new Label(endpoints, SWT.VERTICAL);
		local_site.setImage(SyncingUIPlugin.getImage(IMAGE_LOCAL_SITE));
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, true);
		local_site.setLayoutData(gridData);

		Composite directions = new Composite(endpoints, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginTop = 0;
		directions.setLayout(layout);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, true);
		directions.setLayoutData(gridData);

		left_arrow = new Button(directions, SWT.TOGGLE);
		left_arrow.setImage(SyncingUIPlugin.getImage("icons/full/wizban/sync_arrow_left.png")); //$NON-NLS-1$
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, true);
		left_arrow.setLayoutData(gridData);
		left_arrow.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (!left_arrow.getSelection())
				{
					directionBar.setSelection(DirectionToolBar.UPLOAD);
				}
				else if (right_arrow.getSelection())
				{
					directionBar.setSelection(DirectionToolBar.BOTH);
				}
				else
				{
					directionBar.setSelection(DirectionToolBar.DOWNLOAD);
				}
				updateSyncArrows(directionBar.getSelection());
				load(false);
			}
		});

		right_arrow = new Button(directions, SWT.TOGGLE);
		right_arrow.setImage(SyncingUIPlugin.getImage("icons/full/wizban/sync_arrow_right.png")); //$NON-NLS-1$
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, true);
		right_arrow.setLayoutData(gridData);
		right_arrow.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (!right_arrow.getSelection())
				{
					directionBar.setSelection(DirectionToolBar.DOWNLOAD);
				}
				else if (left_arrow.getSelection())
				{
					directionBar.setSelection(DirectionToolBar.BOTH);
				}
				else
				{
					directionBar.setSelection(DirectionToolBar.UPLOAD);
				}
				updateSyncArrows(directionBar.getSelection());
				load(false);
			}
		});

		Label remote_site = new Label(endpoints, SWT.VERTICAL | SWT.CENTER);
		remote_site.setImage(SyncingUIPlugin.getImage(IMAGE_REMOTE_SITE));
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, true);
		remote_site.setLayoutData(gridData);

		if (this.sourceFilesToBeSynced == null || this.sourceFilesToBeSynced.length <= 1)
		{
			Label end1Label = new Label(endpoints, SWT.CENTER);
			end1Label.setText(FileUtil.compressPath(source.toString(), 30));
			end1Label.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
		}
		else
		{
			// multiple files/folders are selected; adds a custom label
			Composite end1Comp = new Composite(endpoints, SWT.NONE);
			layout = new GridLayout(2, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			end1Comp.setLayout(layout);
			end1Comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

			Label end1Label = new Label(end1Comp, SWT.CENTER);
			end1Label.setText(FileUtil.compressPath(source.toString(), 30));
			end1Label.setLayoutData(new GridData(SWT.CENTER, SWT.HORIZONTAL, false, false));

			final Label end1Extra = new Label(end1Comp, SWT.NONE);
			end1Extra.setText(Messages.SmartSyncDialog_LBL_MultipleFiles);
			end1Extra.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));

			// uses a custom tooltip
			end1Extra.setToolTipText(null);
			new LabelToolTip(end1Extra, sourceConnectionPoint, sourceFilesToBeSynced);
		}

		sync_label = new Label(endpoints, SWT.VERTICAL | SWT.CENTER);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, true);
		gridData.widthHint = 150;
		sync_label.setLayoutData(gridData);
		sync_label.setText(StringUtil.EMPTY);

		if (destFilesToBeSynced == null || destFilesToBeSynced.length <= 1)
		{
			Label end2Label = new Label(endpoints, SWT.CENTER);
			end2Label.setText(FileUtil.compressPath(dest.toString(), 30));
			end2Label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		else
		{
			// multiple files/folders are selected; adds a custom label
			Composite end2Comp = new Composite(endpoints, SWT.NONE);
			end2Comp.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
			end2Comp.setLayoutData(GridDataFactory.fillDefaults().create());

			Label end2Label = new Label(end2Comp, SWT.CENTER);
			end2Label.setText(FileUtil.compressPath(dest.toString(), 30));
			end2Label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).create());

			Label end2Extra = new Label(end2Comp, SWT.NONE);
			end2Extra.setText(Messages.SmartSyncDialog_LBL_MultipleFiles);
			end2Extra.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).create());

			// uses a custom tooltip
			end2Extra.setToolTipText(null);
			new LabelToolTip(end2Extra, destConnectionPoint, destFilesToBeSynced);
		}

		Label shadow_sep_h = new Label(endpoints, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.HORIZONTAL);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, true);
		gridData.horizontalSpan = 3;
		gridData.verticalIndent = 15;
		shadow_sep_h.setLayoutData(gridData);

		Composite status = new Composite(description, SWT.NONE);
		layout = new GridLayout(7, false);
		layout.marginWidth = 40;
		layout.marginTop = 12;
		status.setLayout(layout);
		status.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label updatedSymbol = new Label(status, SWT.VERTICAL);
		updatedSymbol.setImage(SyncingUIPlugin.getImage(ICON_UPDATE));
		updatedLabel = new Label(status, SWT.LEFT);
		updatedLabel.setText(UPDATED_LABEL);
		Label skippedSymbol = new Label(status, SWT.VERTICAL);
		skippedSymbol.setImage(SyncingUIPlugin.getImage(ICON_SKIP));
		skippedLabel = new Label(status, SWT.LEFT);
		skippedLabel.setText(SKIPPED_LABEL);
		Label deletedSymbol = new Label(status, SWT.VERTICAL);
		deletedSymbol.setImage(SyncingUIPlugin.getImage(ICON_DELETE));
		deletedLabel = new Label(status, SWT.LEFT);
		deletedLabel.setText(DELETED_LABEL);

		searchComposite = createSearchComposite(status);
		filterComp = createFilterComposite(status);

		return top;
	}

	private SearchComposite createSearchComposite(Composite myComposite)
	{
		SearchComposite search = new SearchComposite(myComposite, this);
		search.setSearchOnEnter(false);
		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		search.setInitialText(Messages.SmartSyncDialog_searchText);
		return search;
	}

	private Composite createFilterComposite(final Composite myComposite)
	{

		Composite filter = new Composite(myComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginBottom = 2;
		filter.setLayout(gridLayout);

		filterLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filterLayoutData.exclude = true;
		filter.setLayoutData(filterLayoutData);

		filterLabel = new CLabel(filter, SWT.LEFT);
		filterLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		ToolBar toolBar = new ToolBar(filter, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(SyncingUIPlugin.getImage(CLOSE_ICON));
		toolItem.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				removeFilter();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		return filter;
	}

	protected void hideFilterLable()
	{
		filterLayoutData.exclude = true;
		filterComp.setVisible(false);
		filterComp.getParent().layout();
	}

	protected void showFilterLabel(Image image, String text)
	{
		filterLabel.setImage(image);
		filterLabel.setText(text);
		filterLayoutData.exclude = false;
		filterComp.setVisible(true);
		filterComp.getParent().layout();
	}

	protected void removeFilter()
	{
		hideFilterLable();
	}

	private Composite createDeleteOptions(Composite parent)
	{
		Composite deletes = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 8;
		layout.marginWidth = 8;
		deletes.setLayout(layout);
		deletes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		deleteLocalFiles = new Button(deletes, SWT.CHECK);
		deleteLocalFiles.setText(Messages.SmartSyncDialog_DeleteExtra + "'" + end1 + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		deleteLocalFiles.setToolTipText(Messages.SmartSyncDialog_DeleteExtraTooltip + end1 + "'"); //$NON-NLS-1$
		deleteLocalFiles.setSelection(getDeleteLocalPreference());
		deleteLocalFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		deleteLocalFiles.addSelectionListener(this);

		deleteRemoteFiles = new Button(deletes, SWT.CHECK);
		deleteRemoteFiles.setText(Messages.SmartSyncDialog_DeleteExtra + "'" + end2 + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		deleteRemoteFiles.setToolTipText(Messages.SmartSyncDialog_DeleteExtraTooltip + "'" + end2 + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		deleteRemoteFiles.setSelection(getDeleteRemotePreference());
		deleteRemoteFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		deleteRemoteFiles.addSelectionListener(this);

		closeWhenDone = new Button(deletes, SWT.CHECK);
		closeWhenDone.setText(Messages.SmartSyncDialog_CloseWhenDone);
		closeWhenDone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		closeWhenDone.setSelection(getSyncingPreferenceStore().getBoolean(CLOSE_WHEN_DONE));
		closeWhenDone.addSelectionListener(this);

		return deletes;
	}

	private Composite createFooter(Composite parent)
	{
		Composite footer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		footer.setLayout(layout);
		footer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		createDeleteOptions(footer);
		createDirectionOptions(footer);

		return footer;
	}

	private Composite createAdvancedSection(Composite parent)
	{
		final Composite advanced = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 8;
		advanced.setLayout(layout);
		advanced.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final Cursor hand = new Cursor(advanced.getDisplay(), SWT.CURSOR_HAND);
		final Font boldFont = new Font(advanced.getDisplay(), SWTUtils.boldFont(advanced.getFont()));
		advanced.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				if (hand != null && !hand.isDisposed())
				{
					hand.dispose();
				}
				if (boldFont != null && !boldFont.isDisposed())
				{
					boldFont.dispose();
				}
			}

		});

		final Label advancedIcon = new Label(advanced, SWT.LEFT);
		advancedIcon.setImage(SyncingUIPlugin.getImage("icons/full/obj16/maximize.png")); //$NON-NLS-1$
		advancedIcon.setCursor(hand);
		advancedIcon.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		Label advancedLabel = new Label(advanced, SWT.LEFT);
		advancedLabel.setText(Messages.SmartSyncDialog_AdvancedOptions);
		advancedLabel.setCursor(hand);
		advancedLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		advancedLabel.setFont(boldFont);

		final Composite advancedOptions = new Composite(advanced, SWT.NONE);
		layout = new GridLayout();
		layout.marginLeft = 15;
		advancedOptions.setLayout(layout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		gridData.exclude = true;
		advancedOptions.setLayoutData(gridData);
		advancedOptions.setVisible(false);

		MouseAdapter expander = new MouseAdapter()
		{

			public void mouseDown(MouseEvent e)
			{
				if (advancedOptions.isVisible())
				{
					advancedOptions.setVisible(false);
					advancedIcon.setImage(SyncingUIPlugin.getImage("icons/full/obj16/maximize.png")); //$NON-NLS-1$
					((GridData) advancedOptions.getLayoutData()).exclude = true;
				}
				else
				{
					advancedOptions.setVisible(true);
					advancedIcon.setImage(SyncingUIPlugin.getImage("icons/full/obj16/minimize.png")); //$NON-NLS-1$
					((GridData) advancedOptions.getLayoutData()).exclude = false;
				}

				dialogArea.layout(true, true);
			}

		};
		advancedIcon.addMouseListener(expander);
		advancedLabel.addMouseListener(expander);

		useCrc = new Button(advancedOptions, SWT.CHECK);
		useCrc.setText(Messages.SmartSyncDialog_UseCrc);
		useCrc.setSelection(getCoreUIPreferenceStore().getBoolean(USE_CRC));
		useCrc.addSelectionListener(this);

		syncInBackground = new Button(advancedOptions, SWT.CHECK);
		syncInBackground.setText(Messages.SmartSyncDialog_SyncInBackground);
		syncInBackground.setSelection(getCoreUIPreferenceStore().getBoolean(COMPARE_IN_BACKGROUND));
		syncInBackground.addSelectionListener(this);

		Group group = new Group(advancedOptions, SWT.NONE);
		group.setText(Messages.SmartSyncDialog_LBL_PermforUploads);
		group.setLayout(GridLayoutFactory.fillDefaults().create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		uploadPermComposite = new UpdatePermissionsComposite(group, PermissionDirection.UPLOAD);
		uploadPermComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		group = new Group(advancedOptions, SWT.NONE);
		group.setText(Messages.SmartSyncDialog_LBL_PermForDownloads);
		group.setLayout(GridLayoutFactory.fillDefaults().create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		downloadPermComposite = new UpdatePermissionsComposite(group, PermissionDirection.DOWNLOAD);
		downloadPermComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return advanced;
	}

	private Composite createMainSection(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		swappable = createTable(main);
		createFooter(main);

		return main;
	}

	private Composite createErrorSection(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setBackgroundMode(SWT.INHERIT_DEFAULT);
		// main.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gridData.exclude = true;
		main.setLayoutData(gridData);

		errorLabel = new Label(main, SWT.CENTER | SWT.WRAP);
		final Font font = new Font(main.getDisplay(), "Arial", 12, SWT.NONE); //$NON-NLS-1$
		errorLabel.setFont(font);
		errorLabel.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				font.dispose();
			}
		});
		errorLabel.setForeground(main.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		errorLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		retryLink = new Link(main, SWT.NONE);
		retryLink.setText("<a>" + Messages.SmartSyncDialog_Retry + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		retryLink.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));
		retryLink.addSelectionListener(this);

		main.setVisible(false);

		return main;
	}

	private void updateStatLabels()
	{
		int deleted = 0;
		int updated = 0;
		int skipped = 0;
		int selection = directionBar.getSelection();
		ISyncResource[] resources = syncViewer.getCurrentResources();
		for (ISyncResource resource : resources)
		{
			if (resource.isSkipped())
			{
				skipped++;
			}
			else if ((selection == DirectionToolBar.BOTH || selection == DirectionToolBar.DOWNLOAD)
					&& deleteLocalFiles.getSelection() && resource.getSyncState() == SyncState.ClientItemOnly)
			{
				deleted++;
			}
			else if ((selection == DirectionToolBar.BOTH || selection == DirectionToolBar.UPLOAD)
					&& deleteRemoteFiles.getSelection() && resource.getSyncState() == SyncState.ServerItemOnly)
			{
				deleted++;
			}
			else
			{
				if (resource.getPair() != null)
				{
					updated++;
				}
			}
		}
		updatedLabel.setText(MessageFormat.format(UPDATED_LABEL, updated));
		if (deleted == 0)
		{
			deletedLabel.setFont(updatedLabel.getFont());
			deletedLabel.setForeground(null);
		}
		else
		{
			// makes the delete label bold and red to make user aware there are
			// going to be files deleted
			if (boldFont == null)
			{
				FontData[] data = SWTUtils.boldFont(deletedLabel.getFont());
				boldFont = new Font(deletedLabel.getDisplay(), data);
				deletedLabel.addDisposeListener(new DisposeListener()
				{

					public void widgetDisposed(DisposeEvent e)
					{
						boldFont.dispose();
					}

				});
			}
			deletedLabel.setFont(boldFont);
			deletedLabel.setForeground(deletedLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		deletedLabel.setText(MessageFormat.format(DELETED_LABEL, deleted));
		skippedLabel.setText(MessageFormat.format(SKIPPED_LABEL, skipped));
		this.skipped = skipped;
		skippedLabel.getParent().layout(true, true);
		startSync.setEnabled(deleted + updated > 0);
	}

	private void updateSyncArrows(int selection)
	{

		switch (selection)
		{
			case DirectionToolBar.BOTH:
				left_arrow.setSelection(true);
				right_arrow.setSelection(true);
				sync_label.setText(Messages.SmartSyncDialog_BothDirection);
				break;
			case DirectionToolBar.UPLOAD:
			case DirectionToolBar.FORCE_UPLOAD:
				left_arrow.setSelection(false);
				right_arrow.setSelection(true);
				sync_label.setText(Messages.SmartSyncDialog_Upload);
				break;
			case DirectionToolBar.FORCE_DOWNLOAD:
			case DirectionToolBar.DOWNLOAD:
				left_arrow.setSelection(true);
				right_arrow.setSelection(false);
				sync_label.setText(Messages.SmartSyncDialog_Download);
				break;
		}
	}

	private Composite createTable(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		main.setLayout(layout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 400;
		main.setLayoutData(gridData);
		// main.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		syncViewer = new SmartSyncViewer(main, end1, end2);
		syncViewer.setPresentationType(getPresentationTypePref());
		syncViewer.setShowDatesSelected(getShowModificationTimePref());
		syncViewer.setCellModifier(new ICellModifier()
		{

			public void modify(Object element, String property, Object value)
			{
				// Only allow checking of skipped box when sync isn't
				// running
				if (startSync.getText().equals(Messages.SmartSyncDialog_StartSync))
				{
					if (element instanceof Item)
					{
						element = ((Item) element).getData();
					}
					ISyncResource resource = (ISyncResource) element;
					resource.setSkipped(Boolean.parseBoolean(value.toString()));
					syncViewer.update(element, null);
					if (resource instanceof SyncFolder)
					{
						// refreshes the children of the folder
						Collection<ISyncResource> children = ((SyncFolder) resource).getAllChildren();
						for (ISyncResource child : children)
						{
							syncViewer.update(child, null);
						}
					}
					if (!resource.isSkipped() && resource.getParent() != null)
					{
						resource.getParent().setSkipped(false, false);
						syncViewer.update(resource.getParent(), null);
					}
					updateStatLabels();
				}
			}

			public Object getValue(Object element, String property)
			{
				return Boolean.valueOf(((ISyncResource) element).isSkipped());
			}

			public boolean canModify(Object element, String property)
			{
				return Messages.SmartSyncDialog_ColumnSkip.equals(property);
			}

		});

		viewerFilter = new ViewerFilter()
		{
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{

				if (element instanceof SyncFile || element instanceof SyncFolder)
				{
					ISyncResource resource = (ISyncResource) element;
					if (searchText == null)
					{
						return true;
					}

					String path = resource.getPath().toString();
					Matcher m = searchPattern.matcher(path);
					return m.find();
				}
				return false;
			}
		};

		syncViewer.addFilter(viewerFilter);

		errorComp = createErrorSection(main);
		loadingComp = createLoadingSection(main);
		synced = createSyncedSection(main);

		return main;
	}

	private Composite createSyncedSection(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setBackgroundMode(SWT.INHERIT_DEFAULT);
		// main.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.exclude = true;
		main.setLayoutData(gridData);

		syncedIcon = new Label(main, SWT.CENTER);
		syncedIcon.setImage(SyncingUIPlugin.getImage("icons/full/obj16/synced.png")); //$NON-NLS-1$
		syncedIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		syncedText = new Label(main, SWT.CENTER | SWT.WRAP);
		final Font font = new Font(main.getDisplay(), "Arial", 12, SWT.NONE); //$NON-NLS-1$
		syncedText.setFont(font);
		syncedText.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				font.dispose();
			}
		});
		syncedText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		syncedText.setText(end1 + " and " + end2 + Messages.SmartSyncDialog_InSync); //$NON-NLS-1$

		main.setVisible(false);

		return main;
	}

	private Composite createLoadingSection(Composite parent)
	{
		Composite loadingComp = new Composite(parent, SWT.NONE);
		loadingComp.setLayout(new GridLayout());
		loadingComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		loadingComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		// loadingComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		loadingLabel = new Label(loadingComp, SWT.NONE);
		loadingLabel.setText(SYNC_LABEL + "..."); //$NON-NLS-1$
		loadingLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		ProgressBar loadingBar = new ProgressBar(loadingComp, SWT.SMOOTH | SWT.INDETERMINATE);
		loadingBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		return loadingComp;
	}

	protected void createButtonsForButtonBar(Composite parent)
	{
		// create OK and Cancel buttons by default
		startSync = createButton(parent, IDialogConstants.PROCEED_ID, Messages.SmartSyncDialog_StartSync, true);
		GridData gridData = new GridData(SWT.FILL, SWT.END, false, false);
		GC gc = new GC(startSync);
		// calculates the ideal width
		gridData.widthHint = Math.max(gc.stringExtent(Messages.SmartSyncDialog_StartSync).x,
				gc.stringExtent(Messages.SmartSyncDialog_RunInBackground).x) + 50;
		gc.dispose();
		startSync.setLayoutData(gridData);
		startSync.addSelectionListener(this);

		saveLog = createButton(parent, IDialogConstants.DETAILS_ID, "Save Log...", false); //$NON-NLS-1$
		saveLog.addSelectionListener(this);
		saveLog.setEnabled(false);

		cancel = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		cancel.addSelectionListener(this);
	}

	private void setEnabled(boolean enabled)
	{
		directionBar.setEnabled(enabled);
		optionsBar.setEnabled(enabled);
		boolean syncEnabled = enabled && syncViewer.getCurrentResources().length > 0;
		if (!startSync.isDisposed())
		{
			startSync.setEnabled(syncEnabled);
		}

		if (enabled)
		{
			updateFileButtonsState();
			updateSyncArrows(directionBar.getSelection());
		}
	}

	/**
	 * @see org.eclipse.jface.window.Window#open()
	 */
	@Override
	public int open()
	{
		setBlockOnOpen(false);
		if (!compareInBackground)
		{
			super.open();
		}
		load(true);
		return OK;
	}

	private void load(final boolean showSyncedMessage)
	{
		if (!compareInBackground)
		{
			setEnabled(false);
			GridData data = (GridData) loadingComp.getLayoutData();
			data.exclude = false;
			loadingComp.setVisible(true);
			loadingComp.getParent().layout(true, true);
		}

		final boolean forceUp = compareInBackground ? false
				: (directionBar.getSelection() == DirectionToolBar.FORCE_UPLOAD);
		final boolean forceDown = compareInBackground ? false
				: (directionBar.getSelection() == DirectionToolBar.FORCE_DOWNLOAD);

		if (buildSmartSync != null)
		{
			// cancels the existing one
			buildSmartSync.cancel();
		}
		buildSmartSync = new Job("Generating Synchronize Status") //$NON-NLS-1$
		{

			protected IStatus run(final IProgressMonitor monitor)
			{
				syncer.setEventHandler(new SyncEventHandlerAdapterWithProgressMonitor(monitor)
				{

					public boolean syncEvent(final VirtualFileSyncPair item, int index, int totalItems,
							IProgressMonitor monitor)
					{
						if (item != null)
						{
							if (!compareInBackground)
							{
								PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
								{

									public void run()
									{
										if (loadingLabel == null || loadingLabel.isDisposed())
										{
											return;
										}
										String name = getFilename(item);
										if (name != null)
										{
											loadingLabel.setText(SYNC_LABEL + name);
											loadingLabel.getParent().layout(true, true);
										}
									}

								});
							}
						}
						return super.syncEvent(item, index, totalItems, monitor);
					}

				});
				VirtualFileSyncPair[] items = new VirtualFileSyncPair[0];
				Exception error = null;
				try
				{
					if (forceUp)
					{
						IFileStore[] clientFiles = (sourceFilesToBeSynced == null) ? EFSUtils.getFiles(source, true,
								false, null) : EFSUtils.getAllFiles(sourceFilesToBeSynced, true, false, monitor);
						items = syncer.createSyncItems(clientFiles, new IFileStore[0], monitor);
						Map<String, VirtualFileSyncPair> pairs = new HashMap<String, VirtualFileSyncPair>();
						for (VirtualFileSyncPair item : items)
						{
							pairs.put(item.getRelativePath(), item);
						}
						IFileStore[] serverFiles = EFSUtils.getFiles(dest, true, false, monitor);
						VirtualFileSyncPair pair;
						for (IFileStore file : serverFiles)
						{
							pair = pairs.get(EFSUtils.getRelativePath(destConnectionPoint.getRoot(), file, null));
							if (pair != null)
							{
								pair.setDestinationFile(file);
								pair.setSyncState(SyncState.ClientItemIsNewer);
							}
						}
					}
					else if (forceDown)
					{
						IFileStore[] serverFiles = (destFilesToBeSynced == null) ? EFSUtils.getFiles(dest, true, false,
								null) : SyncUtils.getDownloadFiles(sourceConnectionPoint, destConnectionPoint,
								destFilesToBeSynced, false, true, monitor);
						items = syncer.createSyncItems(new IFileStore[0], serverFiles, monitor);
						Map<String, VirtualFileSyncPair> pairs = new HashMap<String, VirtualFileSyncPair>();
						for (VirtualFileSyncPair item : items)
						{
							pairs.put(item.getRelativePath(), item);
						}
						IFileStore[] clientFiles = EFSUtils.getFiles(source, true, false, monitor);
						VirtualFileSyncPair pair;
						for (IFileStore file : clientFiles)
						{
							pair = pairs.get(Synchronizer.getCanonicalPath(sourceConnectionPoint.getRoot(), file));
							if (pair != null)
							{
								pair.setSourceFile(file);
								pair.setSyncState(SyncState.ServerItemIsNewer);
							}
						}
					}
					else
					{
						if (sourceFilesToBeSynced == null && destFilesToBeSynced == null)
						{
							items = syncer.getSyncItems(sourceConnectionPoint, destConnectionPoint, source, dest,
									monitor);
						}
						else
						{
							IFileStore[] clientFiles = (sourceFilesToBeSynced == null) ? EFSUtils.getFiles(source,
									true, false, null) : EFSUtils.getAllFiles(sourceFilesToBeSynced, true, false,
									monitor);
							IFileStore[] serverFiles = (destFilesToBeSynced == null) ? EFSUtils.getFiles(dest, true,
									false, null) : SyncUtils.getDownloadFiles(sourceConnectionPoint,
									destConnectionPoint, destFilesToBeSynced, false, true, monitor);

							items = syncer.createSyncItems(clientFiles, serverFiles, monitor);
						}
					}
				}
				catch (OperationCanceledException e)
				{
					return Status.CANCEL_STATUS;
				}
				catch (Exception e1)
				{
					IdeLog.logError(SyncingUIPlugin.getDefault(), Messages.SmartSyncDialog_ErrorSmartSync, e1);
					error = e1;
				}
				if (monitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}
				if (items != null && error == null)
				{
					// no error
					root = SyncModelBuilder.buildSyncFolder(sourceConnectionPoint, destConnectionPoint, items);
					UIJob update = new UIJob("Loading Sync") //$NON-NLS-1$
					{

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							if (compareInBackground)
							{
								SmartSyncDialog.super.open();
							}
							if (loadingComp != null && !loadingComp.isDisposed())
							{
								GridData data = (GridData) loadingComp.getLayoutData();
								data.exclude = true;
								loadingComp.setVisible(false);
								data = (GridData) errorComp.getLayoutData();
								data.exclude = true;
								errorComp.setVisible(false);
								syncViewer.setInput(root);

								if (syncViewer.getCurrentResources().length > 0)
								{
									data = (GridData) synced.getLayoutData();
									data.grabExcessVerticalSpace = true;
									data.exclude = true;
									synced.setVisible(false);
									data = (GridData) syncViewer.getTree().getLayoutData();
									data.exclude = false;
									syncViewer.setVisible(true);
									setEnabled(true);
									startSync.setFocus();
								}
								else if (showSyncedMessage)
								{
									data = (GridData) syncViewer.getTree().getLayoutData();
									data.exclude = true;
									syncViewer.setVisible(false);
									data = (GridData) synced.getLayoutData();
									data.grabExcessVerticalSpace = true;
									data.exclude = false;
									synced.setVisible(true);
									cancel.setText(Messages.SmartSyncDialog_Close);
									setEnabled(true);
									syncer.disconnect();
								}
								else
								{
									setEnabled(true);
								}
								swappable.getParent().layout(true, true);
								updateStatLabels();
							}
							return Status.OK_STATUS;
						}

					};
					update.schedule();
				}
				else
				{
					final StringBuilder errorMessage = new StringBuilder();
					if (error != null)
					{
						// when it is UnknownHostException, adds some more details in the message
						if (error instanceof UnknownHostException)
						{
							errorMessage.append(MessageFormat.format(Messages.SmartSyncDialog_UnknownHostError, end1,
									end2));
						}
						else
						{
							errorMessage.append(Messages.SmartSyncDialog_ErrorSync);
							errorMessage.append("\n " + Messages.SmartSyncDialog_ErrorMessage + error.getMessage()); //$NON-NLS-1$
						}
					}
					UIJob showError = new UIJob("Showing sync error") //$NON-NLS-1$
					{

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							if (compareInBackground)
							{
								SmartSyncDialog.super.open();
							}
							if (loadingComp != null && !loadingComp.isDisposed())
							{
								GridData data = (GridData) loadingComp.getLayoutData();
								data.exclude = true;
								loadingComp.setVisible(false);
								data = (GridData) syncViewer.getTree().getLayoutData();
								data.exclude = true;
								syncViewer.setVisible(false);
								data = (GridData) errorComp.getLayoutData();
								data.exclude = false;
								errorComp.setVisible(true);
								errorLabel.setText(errorMessage.toString());
								swappable.getParent().layout(true, true);
								setEnabled(false);
								syncer.disconnect();
							}
							return Status.OK_STATUS;
						}

					};
					showError.schedule();
				}
				return Status.OK_STATUS;
			}

		};
		buildSmartSync.setPriority(Job.LONG);
		try
		{
			buildSmartSync.setSystem(false);
		}
		catch (IllegalStateException ise)
		{
			// ignore
		}
		buildSmartSync.schedule();
	}

	protected void dispose()
	{
		if (titleImage != null)
		{
			setTitleImage(null);
			titleImage.dispose();
			titleImage = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		dialogArea = (Composite) super.createDialogArea(parent);

		titleImage = SyncingUIPlugin.getImageDescriptor("/icons/full/wizban/sync.png").createImage(); //$NON-NLS-1$
		dialogArea.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				dispose();
			}
		});

		setTitleImage(titleImage);
		getShell().setText("Synchronize"); //$NON-NLS-1$

		setTitle("Synchronize files between two endpoints"); //$NON-NLS-1$

		Composite displayArea = new Composite(dialogArea, SWT.NONE);
		displayArea.setLayout(new GridLayout());
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createHeader(displayArea);
		createMainSection(displayArea);
		createAdvancedSection(displayArea);

		syncViewer.setSyncDirection(directionBar.getSelection());

		return dialogArea;
	}

	@Override
	protected Control createContents(Composite parent)
	{

		Control composite = super.createContents(parent);

		setEnabled(false);
		updateDeleteStates();
		if (getShell() != null && getParentShell() != null)
		{
			SWTUtils.center(getShell(), getParentShell());
		}
		return composite;
	}

	/**
	 * Sets the handler for syncing events.
	 * 
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(ISyncEventHandler handler)
	{
		this.handler = handler;
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == deleteLocalFiles)
		{
			saveDeleteLocalPreference(deleteLocalFiles.getSelection());
			updateDeleteStates();
		}
		else if (source == deleteRemoteFiles)
		{
			saveDeleteRemotePreference(deleteRemoteFiles.getSelection());
			updateDeleteStates();
		}
		else if (source == cancel)
		{
			cancel();
		}
		else if (source == closeWhenDone)
		{
			getSyncingPreferenceStore().setValue(CLOSE_WHEN_DONE, closeWhenDone.getSelection());
		}
		else if (source == useCrc)
		{
			getCoreUIPreferenceStore().setValue(USE_CRC, useCrc.getSelection());
		}
		else if (source == syncInBackground)
		{
			getCoreUIPreferenceStore().setValue(COMPARE_IN_BACKGROUND, syncInBackground.getSelection());
		}
		else if (source == retryLink)
		{
			load(true);
		}
		else if (source == startSync)
		{
			savePermissions();
			String text = startSync.getText();
			if (text.equals(Messages.SmartSyncDialog_StartSync))
			{
				startSync.setText(Messages.SmartSyncDialog_RunInBackground);
				startSync.getParent().layout();
				startSync();
			}
			else if (text.equals(Messages.SmartSyncDialog_RunInBackground))
			{
				setReturnCode(CANCEL);
				close();
			}
		}
		else if (source == saveLog)
		{
			FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
			IDialogSettings editorSettings = SyncingUIPlugin.getDefault().getDialogSettings();
			String value = editorSettings.get(LOG_EXPORT_DIRECTORY);
			if (value != null)
			{
				fileDialog.setFilterPath(value);
			}

			DateFormat fileFormat = new SimpleDateFormat("yyyyMMddHHmmss"); //$NON-NLS-1$
			Date d = new Date();
			fileDialog.setFileName("Aptana Synchronize Log " + fileFormat.format(d) + ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
			String path = fileDialog.open();
			if (path == null)
			{
				return;
			}

			File logFile = new File(path);
			editorSettings.put(LOG_EXPORT_DIRECTORY, logFile.getParent());

			SyncExporter exporter = new SyncExporter();
			ISyncResource[] resources = syncViewer.getCurrentResources();
			exporter.export(logFile, resources);
		}
	}

	private void savePermissions()
	{
		PreferenceUtils.setUpdatePermissions(uploadPermComposite.getUpdatePermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils
				.setSpecificPermissions(uploadPermComposite.getSpecificPermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils.setFilePermissions(uploadPermComposite.getFilePermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils.setFolderPermissions(uploadPermComposite.getFolderPermissions(), PermissionDirection.UPLOAD);
		PreferenceUtils
				.setUpdatePermissions(downloadPermComposite.getUpdatePermissions(), PermissionDirection.DOWNLOAD);
		PreferenceUtils.setSpecificPermissions(downloadPermComposite.getSpecificPermissions(),
				PermissionDirection.DOWNLOAD);
		PreferenceUtils.setFilePermissions(downloadPermComposite.getFilePermissions(), PermissionDirection.DOWNLOAD);
		PreferenceUtils
				.setFolderPermissions(downloadPermComposite.getFolderPermissions(), PermissionDirection.DOWNLOAD);
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e)
	{
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.DirectionToolBar.Client#selectionChanged(boolean)
	 */
	public void selectionChanged(int direction, boolean reload)
	{
		updateFileButtonsState();
		updateSyncArrows(directionBar.getSelection());
		syncViewer.setSyncDirection(directionBar.getSelection());
		saveDirectionPref(direction);
		if (reload)
		{
			load(false);
		}
		else
		{
			syncViewer.refreshAndExpandTo(2);
		}
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.OptionsToolBar.Client#stateChanged(int)
	 */
	public void stateChanged(int type)
	{
		syncViewer.setPresentationType(type);
		savePresentationTypePref(type);
	}

	/**
	 * @see com.aptana.ide.syncing.ui.views.views.OptionsToolBar.Client#showDatesSelected(boolean)
	 */
	public void showDatesSelected(boolean show)
	{
		syncViewer.setShowDatesSelected(show);
		saveShowModificationTimePref(show);
	}

	public void syncItem(final VirtualFileSyncPair item)
	{
		// syncing on a specific item has started
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				ISyncResource resource = root.find(item);
				if (resource != null)
				{
					resource.setTransferState(ISyncResource.SYNCING);

					if (dialogArea != null && !dialogArea.isDisposed())
					{
						syncViewer.showProgress(item);
					}
				}
			}

		});
	}

	public void syncProgress(final VirtualFileSyncPair item, final long bytes)
	{
		// updates the progress on a specific item
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				ISyncResource resource = root.find(item);
				if (resource != null)
				{
					resource.setTransferredBytes(bytes);

					if (dialogArea != null && !dialogArea.isDisposed())
					{
						syncViewer.update(resource, null);
					}
				}
			}

		});
	}

	public void syncDone(final VirtualFileSyncPair item, boolean allDone)
	{
		// syncing is completed for a specific item
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				ISyncResource resource = root.find(item);
				if (resource != null)
				{
					resource.setTransferState(ISyncResource.SYNCED);

					if (dialogArea != null && !dialogArea.isDisposed())
					{
						syncViewer.update(resource, null);
						syncViewer.reveal(resource);
					}
				}
			}

		});

		if (allDone)
		{
			syncJobDone();
		}
	}

	public void syncError(final VirtualFileSyncPair item, boolean allDone)
	{
		// an error was encountered during syncing
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				ISyncResource resource = root.find(item);
				if (resource != null)
				{
					resource.setTransferState(ISyncResource.ERROR);

					if (syncViewer != null && !syncViewer.getTree().isDisposed())
					{
						syncViewer.update(resource, null);
						syncViewer.reveal(resource);
					}
				}
			}

		});

		if (allDone)
		{
			syncJobDone();
		}
	}

	private void startSync()
	{
		// disables the options when sync has started
		directionBar.setEnabled(false);
		optionsBar.setEnabled(false);
		right_arrow.setEnabled(false);
		left_arrow.setEnabled(false);

		List<VirtualFileSyncPair> pairs = new ArrayList<VirtualFileSyncPair>();
		ISyncResource[] resources = syncViewer.getCurrentResources();
		for (ISyncResource resource : resources)
		{
			VirtualFileSyncPair pair = resource.getPair();
			if (!resource.isSkipped() && pair != null)
			{
				pairs.add(pair);
			}
		}

		boolean deleteLocal = deleteLocalFiles.getEnabled() && deleteLocalFiles.getSelection();
		boolean deleteRemote = deleteRemoteFiles.getEnabled() && deleteRemoteFiles.getSelection();
		deleteRemoteFiles.setEnabled(false);
		deleteLocalFiles.setEnabled(false);

		int direction = -1;
		int selection = directionBar.getSelection();
		if (selection == DirectionToolBar.DOWNLOAD || selection == DirectionToolBar.FORCE_DOWNLOAD)
		{
			direction = SyncJob.DOWNLOAD;
		}
		else if (selection == DirectionToolBar.UPLOAD || selection == DirectionToolBar.FORCE_UPLOAD)
		{
			direction = SyncJob.UPLOAD;
		}
		else if (selection == DirectionToolBar.BOTH)
		{
			direction = SyncJob.BOTH;
		}

		if (syncJob != null)
		{
			// cancels the previous job if exists
			syncJob.cancel();
		}
		syncJob = new SyncJob(syncer, pairs, direction, deleteRemote, deleteLocal, this, MessageFormat.format(
				Messages.SmartSyncDialog_Endpoints, end1, end2));
		syncJob.schedule();
	}

	private void syncJobDone()
	{
		if (handler != null)
		{
			handler.syncDone(null, null);
		}
		if (source != null && dest != null)
		{
			String comment = ""; //firstEdit ? "" : commentStr; //$NON-NLS-1$
			SmartSyncEventManager.getManager().fireEvent(syncJob.getCompletedPairs(), sourceConnectionPoint,
					destConnectionPoint, comment);
		}

		UIJob updateEndJob = new UIJob("Updating sync") //$NON-NLS-1$
		{

			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (closeWhenDone != null && !closeWhenDone.isDisposed())
				{
					if (closeWhenDone.getSelection())
					{
						setReturnCode(CANCEL);
						disconnectAndClose();
					}
					else
					{
						cancel.setText(Messages.SmartSyncDialog_Close);
						startSync.setEnabled(false);

						int errorCount = syncJob.getErrorCount();
						if (errorCount == 0)
						{
							// completely synced
							if (skipped > 0)
							{
								// adds more words to the success text if there are skipped files
								syncedText.setText(end1 + " and " + end2 + Messages.SmartSyncDialog_InSync //$NON-NLS-1$ 
										+ "\n " + Messages.SmartSyncDialog_SkippedFilesInSync); //$NON-NLS-1$ 
							}
						}
						else
						{
							GridData data = (GridData) syncedIcon.getLayoutData();
							data.exclude = true;
							syncedIcon.setVisible(false);
							syncedText.setText(errorCount + Messages.SmartSyncDialog_SyncError);
						}
						GridData data = (GridData) synced.getLayoutData();
						data.exclude = false;
						synced.setVisible(true);
						setEnabled(false);
						swappable.layout(true, true);
						right_arrow.setEnabled(true);
						left_arrow.setEnabled(true);

						saveLog.setEnabled(true);
					}
				}
				else
				{
					// disconnect directly
					syncer.disconnect();
				}
				refresh(monitor);

				return Status.OK_STATUS;
			}

			/**
			 * Performs post-sync refresh.
			 */
			private void refresh(IProgressMonitor monitor)
			{
				IConnectionPoint clientConnection = syncer.getClientFileManager();
				if (clientConnection instanceof WorkspaceConnectionPoint)
				{
					IResource resource = ((WorkspaceConnectionPoint) clientConnection).getResource();
					IViewPart viewPart = UIUtils.findView(IPageLayout.ID_PROJECT_EXPLORER);
					if (viewPart instanceof CommonNavigator)
					{
						CommonViewer viewer = ((CommonNavigator) viewPart).getCommonViewer();
						viewer.refresh(resource);
					}
				}

				IConnectionPoint serverConnection = syncer.getServerFileManager();
				ConnectionPointType type = CoreIOPlugin.getConnectionPointManager().getType(serverConnection);
				if (type != null && type.getCategory().isRemote())
				{
					IViewPart viewPart = UIUtils.findView(RemoteNavigatorView.ID);
					if (viewPart instanceof RemoteNavigatorView)
					{
						RemoteNavigatorView view = (RemoteNavigatorView) viewPart;
						view.getCommonViewer().refresh(serverConnection);
					}
				}
			}
		};
		EclipseUtil.setSystemForJob(updateEndJob);
		updateEndJob.schedule();
	}

	private void updateDeleteStates()
	{
		syncViewer.setDeleteLocalFiles(deleteLocalFiles.getSelection());
		syncViewer.setDeleteRemoteFiles(deleteRemoteFiles.getSelection());
		updateStatLabels();
		boolean syncEnabled = syncViewer.getCurrentResources().length > 0;
		startSync.setEnabled(syncEnabled);
	}

	private void updateFileButtonsState()
	{
		int selection = directionBar.getSelection();
		switch (selection)
		{
			case DirectionToolBar.UPLOAD:
				deleteLocalFiles.setEnabled(false);
				deleteRemoteFiles.setEnabled(true);
				break;
			case DirectionToolBar.DOWNLOAD:
				deleteLocalFiles.setEnabled(true);
				deleteRemoteFiles.setEnabled(false);
				break;
			case DirectionToolBar.FORCE_DOWNLOAD:
			case DirectionToolBar.FORCE_UPLOAD:
				deleteLocalFiles.setEnabled(false);
				deleteRemoteFiles.setEnabled(false);
				break;
			default:
				deleteRemoteFiles.setEnabled(true);
				deleteLocalFiles.setEnabled(true);
		}
	}

	private void cancel()
	{
		if (buildSmartSync != null)
		{
			buildSmartSync.cancel();
		}
		if (syncJob != null)
		{
			syncJob.cancel();
		}
		disconnectAndClose();
	}

	private static String getFilename(VirtualFileSyncPair item)
	{
		if (item.getDestinationFile() != null)
		{
			return item.getDestinationFile().getName();
		}
		if (item.getSourceFile() != null)
		{
			return item.getSourceFile().getName();
		}
		return null;
	}

	private static IPreferenceStore getCoreUIPreferenceStore()
	{
		return UIPlugin.getDefault().getPreferenceStore();
	}

	private static IPreferenceStore getSyncingPreferenceStore()
	{
		return SyncingUIPlugin.getDefault().getPreferenceStore();
	}

	private static int getPresentationTypePref()
	{
		String viewPref = getSyncingPreferenceStore().getString(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.VIEW_MODE);
		if (com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.VIEW_TREE.equals(viewPref))
		{
			return OptionsToolBar.TREE_VIEW;
		}
		return OptionsToolBar.FLAT_VIEW;
	}

	private static int getDirectionPref()
	{
		String directionPref = getSyncingPreferenceStore().getString(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_MODE);
		if (com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_BOTH.equals(directionPref))
		{
			return DirectionToolBar.BOTH;
		}
		if (com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_UPLOAD.equals(directionPref))
		{
			return DirectionToolBar.UPLOAD;
		}
		if (com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_DOWNLOAD.equals(directionPref))
		{
			return DirectionToolBar.DOWNLOAD;
		}
		if (com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_FORCE_UPLOAD.equals(directionPref))
		{
			return DirectionToolBar.FORCE_UPLOAD;
		}
		return DirectionToolBar.FORCE_DOWNLOAD;
	}

	private static boolean getDeleteLocalPreference()
	{
		return getSyncingPreferenceStore().getBoolean(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DELETE_LOCAL_FILES);
	}

	private static boolean getDeleteRemotePreference()
	{
		return getSyncingPreferenceStore().getBoolean(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DELETE_REMOTE_FILES);
	}

	private static boolean getShowModificationTimePref()
	{
		return getSyncingPreferenceStore().getBoolean(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_MODIFICATION_TIME);
	}

	private static void savePresentationTypePref(int type)
	{
		IPreferenceStore prefs = getSyncingPreferenceStore();
		switch (type)
		{
			case OptionsToolBar.FLAT_VIEW:
				prefs.setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.VIEW_MODE,
						com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.VIEW_FLAT);
				break;
			case OptionsToolBar.TREE_VIEW:
				prefs.setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.VIEW_MODE,
						com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.VIEW_TREE);
				break;
		}
	}

	private static void saveDirectionPref(int direction)
	{
		IPreferenceStore prefs = getSyncingPreferenceStore();
		switch (direction)
		{
			case DirectionToolBar.BOTH:
				prefs.setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_MODE,
						com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_BOTH);
				break;
			case DirectionToolBar.UPLOAD:
				prefs.setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_MODE,
						com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_UPLOAD);
				break;
			case DirectionToolBar.DOWNLOAD:
				prefs.setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_MODE,
						com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_DOWNLOAD);
				break;
			case DirectionToolBar.FORCE_UPLOAD:
				prefs.setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_MODE,
						com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_FORCE_UPLOAD);
				break;
			case DirectionToolBar.FORCE_DOWNLOAD:
				prefs.setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_MODE,
						com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DIRECTION_FORCE_DOWNLOAD);
		}
	}

	private static void saveDeleteLocalPreference(boolean selected)
	{
		getSyncingPreferenceStore().setValue(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DELETE_LOCAL_FILES, selected);
	}

	private static void saveDeleteRemotePreference(boolean selected)
	{
		getSyncingPreferenceStore().setValue(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.DELETE_REMOTE_FILES, selected);
	}

	private static void saveShowModificationTimePref(boolean selected)
	{
		getSyncingPreferenceStore().setValue(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_MODIFICATION_TIME, selected);
	}

	/**
	 * The custom tooltip class for the end point label.
	 */
	private static class LabelToolTip extends ToolTip
	{

		private IConnectionPoint connectionPoint;
		private IFileStore[] files;

		LabelToolTip(Control control, IConnectionPoint connectionPoint, IFileStore[] files)
		{
			super(control, ToolTip.NO_RECREATE, false);
			this.connectionPoint = connectionPoint;
			this.files = files;
		}

		@Override
		protected Composite createToolTipContentArea(Event event, Composite parent)
		{
			Composite contentArea = new Composite(parent, SWT.NONE);
			contentArea.setLayout(new GridLayout());

			StringBuilder buf = new StringBuilder();
			for (IFileStore file : files)
			{
				buf.append(EFSUtils.getRelativePath(connectionPoint, file, null));
				buf.append('\n');
			}
			Text text;
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			GC gc = new GC(contentArea);
			if (gc.textExtent(buf.toString()).y > 200)
			{
				// uses scrollbar when there are many files
				text = new Text(contentArea, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
				gridData.heightHint = 200;
			}
			else
			{
				text = new Text(contentArea, SWT.MULTI | SWT.READ_ONLY);
			}
			gc.dispose();
			text.setLayoutData(gridData);
			text.setText(buf.toString());

			return contentArea;
		}

	}

	public void search(String text, boolean isCaseSensitive, boolean isRegularExpression)
	{
		searchText = text;
		searchPattern = searchComposite.createSearchPattern();
		syncViewer.refresh();
	}

}
