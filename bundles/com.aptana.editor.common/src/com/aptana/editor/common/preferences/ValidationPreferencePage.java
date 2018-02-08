/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.build.ui.BuildUIPlugin;
import com.aptana.build.ui.preferences.IBuildParticipantPreferenceCompositeFactory;
import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IBuildParticipantManager;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.ui.util.UIUtils;

public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	// TODO Move this class to the build.ui plugin. Not sure how we'd handle doing the forced reconcile.
	/**
	 * Property names for columns.
	 */
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String BUILD = "build"; //$NON-NLS-1$
	private static final String RECONCILE = "reconcile"; //$NON-NLS-1$

	private TableViewer validatorsViewer;
	private Control filterComp;

	private List<IBuildParticipantWorkingCopy> participants;
	private Composite sash;

	private final class ApplyChangesAndBuildJob extends Job
	{
		private final boolean rebuild;
		private final boolean reReconcile;

		private ApplyChangesAndBuildJob(String name, boolean rebuild, boolean reReconcile)
		{
			super(name);
			this.rebuild = rebuild;
			this.reReconcile = reReconcile;
			setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
			setUser(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			SubMonitor sub = SubMonitor.convert(monitor, Messages.ValidationPreferencePage_RebuildJobTaskName, 100);
			try
			{
				IWorkspace workspace = ResourcesPlugin.getWorkspace();

				if (rebuild)
				{
					sub.subTask(Messages.ValidationPreferencePage_CleaningProjects);
					workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, sub.newChild(20));
					sub.setWorkRemaining(80);
				}

				sub.subTask(Messages.ValidationPreferencePage_ApplyingChangesToParticipants);
				// apply the changes in participants!
				for (IBuildParticipantWorkingCopy change : participants)
				{
					change.doSave();
				}
				sub.worked(10);

				// if the reconcile enablement/filters change? We'll need to force a new reconcile on
				// open editors...
				if (reReconcile)
				{
					UIJob job = new UIJob(Messages.ValidationPreferencePage_ForcingReconcile)
					{

						@Override
						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							IEditorReference[] refs = UIUtils.getActivePage().getEditorReferences();
							monitor.beginTask(Messages.ValidationPreferencePage_ReconcilingOpenEditors, refs.length);
							for (IEditorReference ref : refs)
							{
								if (monitor.isCanceled())
								{
									return Status.CANCEL_STATUS;
								}
								IEditorPart part = ref.getEditor(false);
								if (part instanceof AbstractTextEditor)
								{

									monitor.subTask(part.getTitle());
									// For AbstractThemeableEditors, we can adapt to SourceViewerConfiguration and
									// then cast to CommonSourceViewerconfiguration and call forceReconcile...
									if (part instanceof AbstractThemeableEditor)
									{
										AbstractThemeableEditor ate = (AbstractThemeableEditor) part;
										CommonSourceViewerConfiguration csvc = (CommonSourceViewerConfiguration) ate
												.getAdapter(SourceViewerConfiguration.class);
										csvc.forceReconcile();
									}
									else
									{
										// This is a hack to force a reconcile. We set the document's contents to
										// it's existing contents. Unfortunately this marks the editor as dirty
										AbstractTextEditor editor = (AbstractTextEditor) part;
										IDocument doc = editor.getDocumentProvider().getDocument(
												editor.getEditorInput());
										doc.set(doc.get());
									}
								}
								monitor.worked(1);
							}
							return Status.OK_STATUS;
						}
					};
					job.schedule(500);
				}

				if (rebuild)
				{
					sub.subTask(Messages.ValidationPreferencePage_RebuildingProjects);
					workspace.build(IncrementalProjectBuilder.FULL_BUILD, sub.newChild(70));
				}
			}
			catch (CoreException e)
			{
				return e.getStatus();
			}
			catch (OperationCanceledException e)
			{
				return Status.CANCEL_STATUS;
			}
			finally
			{
				sub.done();
			}
			return Status.OK_STATUS;
		}

		@Override
		public boolean belongsTo(Object family)
		{
			return ResourcesPlugin.FAMILY_MANUAL_BUILD == family;
		}
	}

	public ValidationPreferencePage()
	{
		List<IBuildParticipant> participants = getBuildParticipantManager().getAllBuildParticipants();
		// TODO Filter out all the participants that are required and have no name
		participants = CollectionsUtil.filter(participants, new IFilter<IBuildParticipant>()
		{
			public boolean include(IBuildParticipant item)
			{
				return !item.isRequired() && !StringUtil.isEmpty(item.getName());
			}
		});
		// Now sort them by name
		Collections.sort(participants, new Comparator<IBuildParticipant>()
		{
			public int compare(IBuildParticipant o1, IBuildParticipant o2)
			{
				return StringUtil.compare(o1.getName(), o2.getName());
			}
		});

		// Now map them into objects we can track changes on
		this.participants = CollectionsUtil.map(participants,
				new IMap<IBuildParticipant, IBuildParticipantWorkingCopy>()
				{
					public IBuildParticipantWorkingCopy map(IBuildParticipant item)
					{
						return item.getWorkingCopy();
					}
				});
	}

	public void init(IWorkbench workbench)
	{
		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent)
	{
		sash = new Composite(parent, SWT.NONE);
		sash.setLayout(new GridLayout());
		sash.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Control validators = createValidators(sash);
		validators.setLayoutData(GridDataFactory.fillDefaults().create());

		createFiltersComposite();
		return sash;
	}

	/**
	 * Dynamically replaces the composite containing the options for a given participant.
	 */
	private void createFiltersComposite()
	{
		if ((filterComp != null) && (!filterComp.isDisposed()))
		{
			filterComp.dispose();
		}
		filterComp = getBuildParticipantPreferenceCompositeFactory().createPreferenceComposite(sash,
				getSelectedBuildParticipant());
		filterComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		getShell().pack(true);
		sash.layout(true);
	}

	protected IBuildParticipantPreferenceCompositeFactory getBuildParticipantPreferenceCompositeFactory()
	{
		return BuildUIPlugin.getDefault().getBuildParticipantPreferenceCompositeFactory();
	}

	protected IBuildParticipantManager getBuildParticipantManager()
	{
		return BuildPathCorePlugin.getDefault().getBuildParticipantManager();
	}

	@Override
	protected void performDefaults()
	{
		for (IBuildParticipant participant : participants)
		{
			participant.restoreDefaults();
		}
		validatorsViewer.refresh();
		updateFilterExpressions();

		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		final boolean rebuild = rebuild();
		final boolean reReconcile = needsReconcile();
		Job buildJob = new ApplyChangesAndBuildJob(Messages.ValidationPreferencePage_RebuildJobTitle, rebuild,
				reReconcile);

		IPreferencePageContainer container = getContainer();
		if (container instanceof IWorkbenchPreferenceContainer)
		{
			((IWorkbenchPreferenceContainer) container).registerUpdateJob(buildJob);
		}
		else
		{
			buildJob.schedule();
		}

		return true;
	}

	private boolean needsReconcile()
	{
		for (IBuildParticipantWorkingCopy change : participants)
		{
			if (change.needsReconcile())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * If changes don't require a rebuild, return false. Otherwise prompt user and take their answer.
	 * 
	 * @return
	 */
	private boolean rebuild()
	{
		if (promptForRebuild())
		{
			MessageDialog dialog = new MessageDialog(getShell(), Messages.ValidationPreferencePage_RebuildDialogTitle,
					null, Messages.ValidationPreferencePage_RebuildDialogMessage, MessageDialog.QUESTION, new String[] {
							IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
			return (dialog.open() == 0);
		}
		return false;
	}

	/**
	 * Determines if any changes will require a rebuild.
	 * 
	 * @return
	 */
	private boolean promptForRebuild()
	{
		for (IBuildParticipantWorkingCopy change : participants)
		{
			if (change.needsRebuild())
			{
				return true;
			}
		}
		return false;
	}

	private Control createValidators(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());

		Composite labelComp = new Composite(main, SWT.NONE);
		labelComp.setLayout(GridLayoutFactory.fillDefaults().extendedMargins(5, 0, 0, 0).numColumns(2)
				.spacing(2, SWT.DEFAULT).create());
		labelComp.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).create());
		Label label = new Label(labelComp, SWT.NONE);
		label.setText(Messages.ValidationPreferencePage_LBL_Validators);
		Label helpImage = new Label(labelComp, SWT.NONE);
		helpImage.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_LCL_LINKTO_HELP));
		helpImage.setToolTipText(Messages.ValidationPreferencePage_EnablingValidatorWarning);

		Group group = new Group(main, SWT.NONE);
		group.setLayout(GridLayoutFactory.swtDefaults().margins(4, 4).create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Table table = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLayoutData(GridDataFactory.fillDefaults().hint(300, 100).grab(true, false).create());
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// set up columns
		// Name column
		TableColumn name = new TableColumn(table, SWT.LEFT);
		name.setWidth(250);
		name.setText(Messages.ValidationPreferencePage_NameColumn);
		name.setToolTipText(Messages.ValidationPreferencePage_NameColumn);

		// Build column
		TableColumn build = new TableColumn(table, SWT.CENTER);
		build.setWidth(40);
		build.setText(Messages.ValidationPreferencePage_BuildColumn);
		build.setToolTipText(Messages.ValidationPreferencePage_BuildColumn);

		// Reconcile column
		TableColumn reconcile = new TableColumn(table, SWT.CENTER);
		reconcile.setWidth(75);
		reconcile.setText(Messages.ValidationPreferencePage_ReconcileColumn);
		reconcile.setToolTipText(Messages.ValidationPreferencePage_ReconcileColumn);

		// Now set up table viewer!
		validatorsViewer = new TableViewer(table);
		// validatorsViewer.setUseHashlookup(true);
		validatorsViewer.setColumnProperties(new String[] { NAME, BUILD, RECONCILE });

		// Assign the cell editors to the viewer
		validatorsViewer.setCellEditors(new CellEditor[] { null, new CheckboxCellEditor(table),
				new CheckboxCellEditor(table) });

		// Set the cell modifier for the viewer
		validatorsViewer.setCellModifier(new ParticipantCellModifier(validatorsViewer));

		// Now set up content/label providers
		validatorsViewer.setContentProvider(ArrayContentProvider.getInstance());
		validatorsViewer.setLabelProvider(new ParticipantLabelProvider());
		// Hide required participants
		validatorsViewer.addFilter(new RequiredParticipantFilter());
		// check the selected build participant, show it's filters
		validatorsViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateFilterExpressions();
			}
		});

		// Now set input
		validatorsViewer.setInput(this.participants);

		return main;
	}

	private IBuildParticipantWorkingCopy getSelectedBuildParticipant()
	{
		IStructuredSelection selection = (IStructuredSelection) validatorsViewer.getSelection();
		if (selection.isEmpty())
		{
			return null;
		}
		return (IBuildParticipantWorkingCopy) selection.getFirstElement();
	}

	private void updateFilterExpressions()
	{
		createFiltersComposite();
	}

	/**
	 * Allows modifying cells of a table containing build participants.
	 * 
	 * @author cwilliams
	 */
	private final class ParticipantCellModifier implements ICellModifier
	{
		private TableViewer tableViewer;

		private ParticipantCellModifier(TableViewer tableViewer)
		{
			this.tableViewer = tableViewer;
		}

		public void modify(Object element, String property, Object value)
		{
			if (element instanceof TableItem)
			{
				element = ((TableItem) element).getData();
			}
			IBuildParticipantWorkingCopy participant = (IBuildParticipantWorkingCopy) element;
			if (BUILD.equals(property))
			{
				participant.setEnabled(IBuildParticipant.BuildType.BUILD, ((Boolean) value).booleanValue());
			}
			else if (RECONCILE.equals(property))
			{
				participant.setEnabled(IBuildParticipant.BuildType.RECONCILE, ((Boolean) value).booleanValue());
			}
			tableViewer.refresh(participant);
		}

		public Object getValue(Object element, String property)
		{
			IBuildParticipant participant = (IBuildParticipant) element;
			if (BUILD.equals(property))
			{
				return participant.isEnabled(IBuildParticipant.BuildType.BUILD);
			}
			if (RECONCILE.equals(property))
			{
				return participant.isEnabled(IBuildParticipant.BuildType.RECONCILE);
			}
			return null;
		}

		public boolean canModify(Object element, String property)
		{
			return BUILD.equals(property) || RECONCILE.equals(property);
		}
	}

	/**
	 * Provides text and images for build participants.
	 * 
	 * @author cwilliams
	 */
	private static final class ParticipantLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		private static final String RED_X_ICON = "platform:/plugin/com.aptana.ui/icons/delete.gif"; //$NON-NLS-1$
		private static final String CHECKMARK_ICON = "platform:/plugin/com.aptana.ui/icons/ok.png"; //$NON-NLS-1$

		public String getColumnText(Object element, int columnIndex)
		{
			IBuildParticipant participant = (IBuildParticipant) element;
			if (columnIndex == 0)
			{
				return participant.getName();
			}
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			IBuildParticipant participant = (IBuildParticipant) element;
			if (columnIndex == 1)
			{
				if (participant.isEnabled(IBuildParticipant.BuildType.BUILD))
				{
					return UIUtils.getImage(CommonEditorPlugin.getDefault(), CHECKMARK_ICON);
				}
				return UIUtils.getImage(CommonEditorPlugin.getDefault(), RED_X_ICON);
			}
			else if (columnIndex == 2)
			{
				if (participant.isEnabled(IBuildParticipant.BuildType.RECONCILE))
				{
					return UIUtils.getImage(CommonEditorPlugin.getDefault(), CHECKMARK_ICON);
				}
				return UIUtils.getImage(CommonEditorPlugin.getDefault(), RED_X_ICON);
			}
			return null;
		}
	}

	/**
	 * Hides required participants.
	 * 
	 * @author cwilliams
	 */
	private static final class RequiredParticipantFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element)
		{
			if (element instanceof IBuildParticipant)
			{
				IBuildParticipant participant = (IBuildParticipant) element;
				return !participant.isRequired();
			}
			return true;
		}
	}
}
