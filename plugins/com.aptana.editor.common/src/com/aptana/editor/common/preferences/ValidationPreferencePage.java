/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IBuildParticipantManager;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.index.core.build.BuildContext;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.CListTable;

public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	// TODO Move this to a buildpath.ui plugin!
	/**
	 * Property names for columns.
	 */
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String BUILD = "build"; //$NON-NLS-1$
	private static final String RECONCILE = "reconcile"; //$NON-NLS-1$

	private ListViewer contentTypesViewer;
	private TableViewer validatorsViewer;
	private CListTable filterViewer;
	private List<ParticipantChanges> participants;

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
				for (ParticipantChanges change : participants)
				{
					change.apply();
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

	private class ParticipantChanges implements IBuildParticipant
	{
		private IBuildParticipant wrapped;
		private Boolean enabledForBuild;
		private Boolean enabledForReconcile;
		private List<String> filters;
		private IScopeContext context;

		public ParticipantChanges(IBuildParticipant wrapped)
		{
			this.wrapped = wrapped;
		}

		public void clean(IProject project, IProgressMonitor monitor)
		{
			wrapped.clean(project, monitor);
		}

		public void buildStarting(IProject project, int kind, IProgressMonitor monitor)
		{
			wrapped.buildStarting(project, kind, monitor);
		}

		public void buildEnding(IProgressMonitor monitor)
		{
			wrapped.buildEnding(monitor);
		}

		public int getPriority()
		{
			return wrapped.getPriority();
		}

		public void buildFile(BuildContext context, IProgressMonitor monitor)
		{
			wrapped.buildFile(context, monitor);
		}

		public void deleteFile(BuildContext context, IProgressMonitor monitor)
		{
			wrapped.deleteFile(context, monitor);
		}

		public Set<IContentType> getContentTypes()
		{
			return wrapped.getContentTypes();
		}

		public String getName()
		{
			return wrapped.getName();
		}

		public String getId()
		{
			return wrapped.getId();
		}

		public boolean isEnabled(BuildType type)
		{
			switch (type)
			{
				case BUILD:
					if (enabledForBuild != null)
					{
						return enabledForBuild;
					}
					break;

				case RECONCILE:
					if (enabledForReconcile != null)
					{
						return enabledForReconcile;
					}
					break;

				default:
					break;
			}
			return wrapped.isEnabled(type);
		}

		public void setEnabled(BuildType type, boolean enabled)
		{
			switch (type)
			{
				case BUILD:
					enabledForBuild = enabled;
					break;

				case RECONCILE:
					enabledForReconcile = enabled;
					break;
				default:
					break;
			}
		}

		public void restoreDefaults()
		{
			wrapped.restoreDefaults();
			enabledForBuild = null;
			enabledForReconcile = null;
			filters = null;
		}

		public boolean isRequired()
		{
			return wrapped.isRequired();
		}

		public List<String> getFilters()
		{
			if (filters != null)
			{
				return filters;
			}
			return wrapped.getFilters();
		}

		public boolean isEnabled(IProject project)
		{
			return wrapped.isEnabled(project);
		}

		public void setFilters(IScopeContext context, String... filters)
		{
			this.context = context;
			this.filters = CollectionsUtil.newList(filters);
		}

		public boolean needsRebuild()
		{
			return enabledForBuild != null || (filters != null && wrapped.isEnabled(BuildType.BUILD));
		}

		public void apply()
		{
			if (enabledForBuild != null)
			{
				wrapped.setEnabled(BuildType.BUILD, enabledForBuild);
				enabledForBuild = null;
			}
			if (enabledForReconcile != null)
			{
				wrapped.setEnabled(BuildType.RECONCILE, enabledForReconcile);
				enabledForReconcile = null;
			}
			if (filters != null)
			{
				((AbstractBuildParticipant) wrapped).setFilters(context, filters.toArray(new String[filters.size()]));
				filters = null;
				context = null;
			}

		}

		public boolean needsReconcile()
		{
			return enabledForReconcile != null || (filters != null && wrapped.isEnabled(BuildType.RECONCILE));
		}
	}

	public ValidationPreferencePage()
	{
		super();
		this.participants = CollectionsUtil.map(getBuildParticipantManager().getAllBuildParticipants(),
				new IMap<IBuildParticipant, ParticipantChanges>()
				{
					public ParticipantChanges map(IBuildParticipant item)
					{
						return new ParticipantChanges(item);
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
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// the left side
		contentTypesViewer = new ListViewer(sash, SWT.BORDER | SWT.SINGLE);
		contentTypesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().create());
		contentTypesViewer.setContentProvider(ArrayContentProvider.getInstance());
		contentTypesViewer.setLabelProvider(new LabelProvider()
		{

			@Override
			public String getText(Object element)
			{
				if (element instanceof IContentType)
				{
					return ((IContentType) element).getName();
				}
				return super.getText(element);
			}
		});
		// Filter out content types that have no optional participants
		contentTypesViewer.addFilter(new EmptyContentTypeParticipantListFilter());

		List<IContentType> contentTypes = new ArrayList<IContentType>(getContentTypes());
		Collections.sort(contentTypes, new Comparator<IContentType>()
		{

			public int compare(IContentType o1, IContentType o2)
			{
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		contentTypesViewer.setInput(contentTypes);

		contentTypesViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				// updates the validators and filter expressions to the newly selected language
				if (validatorsViewer != null)
				{
					updateValidators();
					updateFilterExpressions();
				}
			}
		});

		// the right side
		Composite rightComp = new Composite(sash, SWT.NONE);
		rightComp.setLayout(GridLayoutFactory.fillDefaults().create());

		Control validators = createValidators(rightComp);
		validators.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Control filter = createFiltersComposite(rightComp);
		filter.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		sash.setWeights(new int[] { 1, 3 });
		return sash;
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
		for (ParticipantChanges change : participants)
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
		for (ParticipantChanges change : participants)
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
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.ValidationPreferencePage_LBL_Validators);
		group.setLayout(GridLayoutFactory.fillDefaults().margins(4, 4).create());

		Label label = new Label(group, SWT.WRAP);
		label.setLayoutData(GridDataFactory.fillDefaults().hint(300, 70).create());
		label.setText(Messages.ValidationPreferencePage_EnablingValidatorWarning);

		Table table = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

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
		updateValidators();

		return group;
	}

	private Control createFiltersComposite(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.ValidationPreferencePage_LBL_Filter);
		group.setLayout(GridLayoutFactory.fillDefaults().margins(4, 4).create());

		filterViewer = new CListTable(group, SWT.NONE);
		filterViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_SelectParticipant);
		final IInputValidator inputValidator = new IInputValidator()
		{

			public String isValid(String newText)
			{
				if (StringUtil.isEmpty(newText))
				{
					return Messages.ValidationPreferencePage_ERR_EmptyExpression;
				}
				return null;
			}
		};
		filterViewer.addListener(new CListTable.Listener()
		{

			public Object addItem()
			{
				InputDialog dialog = new InputDialog(getShell(), Messages.ValidationPreferencePage_Ignore_Title,
						Messages.ValidationPreferencePage_Ignore_Message, null, inputValidator);
				if (dialog.open() == Window.OK)
				{
					return dialog.getValue();
				}
				return null;
			}

			public Object editItem(Object item)
			{
				String expression = item.toString();
				InputDialog dialog = new InputDialog(getShell(), Messages.ValidationPreferencePage_Ignore_Title,
						Messages.ValidationPreferencePage_Ignore_Message, expression, inputValidator);
				if (dialog.open() == Window.OK)
				{
					return dialog.getValue();
				}
				// the dialog is canceled; returns the original item
				return item;
			}

			public void itemsChanged(List<Object> rawFilters)
			{
				// Store the new filter expressions in our temporary copy
				@SuppressWarnings("cast")
				ParticipantChanges participant = (ParticipantChanges) getSelectedBuildParticipant();
				String[] filters = new String[rawFilters.size()];
				int i = 0;
				for (Object item : rawFilters)
				{
					filters[i++] = item.toString();
				}
				participant.setFilters(EclipseUtil.instanceScope(), filters);
			}
		});
		filterViewer.setEnabled(false);

		return group;
	}

	protected Set<IContentType> getContentTypes()
	{
		return getBuildParticipantManager().getContentTypes();
	}

	private void updateValidators()
	{
		IContentType selected = getSelectedContentType();
		if (selected == null)
		{
			validatorsViewer.setInput(Collections.emptyList());
		}
		else
		{
			validatorsViewer.setInput(getBuildParticipants(selected.getId()));
		}
	}

	private List<IBuildParticipant> getBuildParticipants(String contentTypeId)
	{
		List<IBuildParticipant> participantsForContentType = getBuildParticipantManager().filterParticipants(
				this.participants, contentTypeId);
		// removes the ones that don't have a name defined
		return CollectionsUtil.filter(participantsForContentType, new IFilter<IBuildParticipant>()
		{

			public boolean include(IBuildParticipant participant)
			{
				return !StringUtil.isEmpty(participant.getName());
			}
		});
	}

	private ParticipantChanges getSelectedBuildParticipant()
	{
		IStructuredSelection selection = (IStructuredSelection) validatorsViewer.getSelection();
		if (selection.isEmpty())
		{
			return null;
		}
		return (ParticipantChanges) selection.getFirstElement();
	}

	private void updateFilterExpressions()
	{
		IBuildParticipant participant = getSelectedBuildParticipant();
		if (participant != null)
		{
			filterViewer.setEnabled(true);
			filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_Description);
			List<String> expressions = participant.getFilters();
			filterViewer.setItems(expressions.toArray());
		}
		else
		{
			filterViewer.setEnabled(false);
			filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_SelectParticipant);
			filterViewer.setItems(ArrayUtil.NO_OBJECTS);
		}
	}

	private IContentType getSelectedContentType()
	{
		IStructuredSelection selection = (IStructuredSelection) contentTypesViewer.getSelection();
		if (selection.isEmpty())
		{
			return null;
		}
		return (IContentType) selection.getFirstElement();
	}

	/**
	 * This filters the content type list to only those who have at least one non-required build participant (so we can
	 * change it's enablement).
	 * 
	 * @author cwilliams
	 */
	private final class EmptyContentTypeParticipantListFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element)
		{
			if (element instanceof IContentType)
			{
				IContentType type = (IContentType) element;
				List<IBuildParticipant> participants = getBuildParticipants(type.getId());
				if (CollectionsUtil.isEmpty(participants))
				{
					return false;
				}
				for (IBuildParticipant participant : participants)
				{
					if (!participant.isRequired())
					{
						return true;
					}
				}
			}
			return false;
		}
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
			IBuildParticipant participant = (IBuildParticipant) element;
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
