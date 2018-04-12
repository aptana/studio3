/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.deploy.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.activities.ITriggerPoint;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.internal.dialogs.WizardActivityFilter;
import org.eclipse.ui.internal.dialogs.WizardContentProvider;
import org.eclipse.ui.internal.dialogs.WizardPatternFilter;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardNode;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardSelectionPage;
import org.eclipse.ui.model.AdaptableList;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

@SuppressWarnings("restriction")
public class DeployWizardPage extends WorkbenchWizardSelectionPage
{
	protected static final String DIALOG_SETTING_SECTION_NAME = "DeployWizardPage."; //$NON-NLS-1$

	private static final String STORE_SELECTED_DEPLOY_WIZARD_ID = DIALOG_SETTING_SECTION_NAME
			+ "STORE_SELECTED_DEPLOY_WIZARD_ID"; //$NON-NLS-1$

	private static final String STORE_EXPANDED_DEPLOY_CATEGORIES = DIALOG_SETTING_SECTION_NAME
			+ "STORE_EXPANDED_DEPLOY_CATEGORIES"; //$NON-NLS-1$

	CategorizedWizardSelectionTree wizardTree;

	// tree viewer of wizard selections
	private TreeViewer treeViewer;

	private Label descriptionLabel;

	/*
	 * Class to create a control that shows a categorized tree of wizard types.
	 */
	protected static class CategorizedWizardSelectionTree
	{
		private final static int SIZING_LISTS_HEIGHT = 200;

		private IWizardCategory wizardCategories;
		private String message;
		private TreeViewer viewer;

		/**
		 * Constructor for CategorizedWizardSelectionTree
		 * 
		 * @param categories
		 *            root wizard category for the wizard type
		 * @param msg
		 *            message describing what the user should choose from the tree.
		 */
		protected CategorizedWizardSelectionTree(IWizardCategory categories, String msg)
		{
			this.wizardCategories = categories;
			this.message = msg;
		}

		/**
		 * Create the tree viewer and a message describing what the user should choose from the tree.
		 * 
		 * @param parent
		 *            Composite on which the tree viewer is to be created
		 * @return Comoposite with all widgets
		 */
		protected Composite createControl(Composite parent)
		{
			Font font = parent.getFont();

			// create composite for page.
			Composite outerContainer = new Composite(parent, SWT.NONE);
			outerContainer.setLayout(new GridLayout());
			outerContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
			outerContainer.setFont(font);

			Label messageLabel = new Label(outerContainer, SWT.NONE);
			if (message != null)
			{
				messageLabel.setText(message);
			}
			messageLabel.setFont(font);

			createFilteredTree(outerContainer);

			layoutTopControl(viewer.getControl());

			return outerContainer;
		}

		/**
		 * Create the categorized tree viewer.
		 * 
		 * @param parent
		 */
		private void createFilteredTree(Composite parent)
		{
			// Create a FilteredTree for the categories and wizards
			FilteredTree filteredTree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
					new WizardPatternFilter(), true);
			viewer = filteredTree.getViewer();
			filteredTree.setFont(parent.getFont());

			viewer.setContentProvider(new WizardContentProvider());
			viewer.setLabelProvider(new WorkbenchLabelProvider());
			viewer.setComparator(DataTransferWizardCollectionComparator.INSTANCE);

			List<IWizardCategory> inputArray = new ArrayList<IWizardCategory>();
			boolean expandTop = false;

			if (wizardCategories != null)
			{
				if (wizardCategories.getParent() == null)
				{
					IWizardCategory[] children = wizardCategories.getCategories();
					for (int i = 0; i < children.length; i++)
					{
						inputArray.add(children[i]);
					}
				}
				else
				{
					expandTop = true;
					inputArray.add(wizardCategories);
				}
			}

			// ensure the category is expanded. If there is a remembered expansion it will be set later.
			if (expandTop)
			{
				viewer.setAutoExpandLevel(2);
			}

			AdaptableList input = new AdaptableList(inputArray);

			// filter wizard list according to capabilities that are enabled
			viewer.addFilter(new WizardActivityFilter());
			// TODO Filter out wizards that can't handle the project we've selected!

			viewer.setInput(input);
		}

		/**
		 * @return the categorized tree viewer
		 */
		protected TreeViewer getViewer()
		{
			return viewer;
		}

		/**
		 * Layout for the given control.
		 * 
		 * @param control
		 */
		private void layoutTopControl(Control control)
		{
			GridData data = new GridData(GridData.FILL_BOTH);

			int availableRows = DialogUtil.availableRows(control.getParent());

			// Only give a height hint if the dialog is going to be too small
			if (availableRows > 40)
			{
				data.heightHint = SIZING_LISTS_HEIGHT;
			}
			else
			{
				data.heightHint = availableRows * 2;
			}

			control.setLayoutData(data);
		}
	}

	/**
	 * Constructor for deploy wizard page.
	 * 
	 * @param aWorkbench
	 *            current workbench
	 * @param currentSelection
	 *            current selection
	 */
	protected DeployWizardPage(IWorkbench aWorkbench, IStructuredSelection currentSelection)
	{
		super("deployWizardPage", aWorkbench, currentSelection, null, null); //$NON-NLS-1$
		setTitle(WorkbenchMessages.DeployWizardPage_DeploymentOption);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		Font font = parent.getFont();

		// create composite for page.
		Composite outerContainer = new Composite(parent, SWT.NONE);
		outerContainer.setLayout(GridLayoutFactory.swtDefaults().spacing(0, 10).create());
		outerContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		outerContainer.setFont(font);

		Composite comp = createTreeViewer(outerContainer);
		Dialog.applyDialogFont(comp);

		descriptionLabel = new Label(comp, SWT.WRAP);
		if (descriptionLabel != null)
		{
			descriptionLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 50)
					.create());
			descriptionLabel.setFont(font);
		}

		restoreWidgetValues();

		setControl(outerContainer);

		initialize();
	}

	/**
	 * Create the tree viewer from which a wizard is selected.
	 */
	protected Composite createTreeViewer(Composite parent)
	{
		IWizardCategory root = DeployWizardRegistry.getInstance().getRootCategory();
		wizardTree = new CategorizedWizardSelectionTree(root,
				com.aptana.deploy.wizard.WorkbenchMessages.DeployWizardPage_SelectDeploymentProvider);
		Composite exportComp = wizardTree.createControl(parent);
		wizardTree.getViewer().addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				listSelectionChanged(event.getSelection());
			}
		});
		wizardTree.getViewer().addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				treeDoubleClicked(event);
			}
		});
		setTreeViewer(wizardTree.getViewer());
		return exportComp;
	}

	/**
	 * Method to call when an item in one of the lists is double-clicked. Shows the first page of the selected wizard or
	 * expands a collapsed tree.
	 * 
	 * @param event
	 */
	protected void treeDoubleClicked(DoubleClickEvent event)
	{
		ISelection selection = event.getViewer().getSelection();
		IStructuredSelection ss = (IStructuredSelection) selection;
		listSelectionChanged(ss);

		Object element = ss.getFirstElement();
		TreeViewer v = (TreeViewer) event.getViewer();
		if (v.isExpandable(element))
		{
			v.setExpandedState(element, !v.getExpandedState(element));
		}
		else if (element instanceof WorkbenchWizardElement)
		{
			if (canFlipToNextPage())
			{
				getContainer().showPage(getNextPage());
				return;
			}
		}
		getContainer().showPage(getNextPage());
	}

	/*
	 * Update the wizard's message based on the given (selected) wizard element.
	 */
	private void updateSelectedNode(WorkbenchWizardElement wizardElement)
	{
		setErrorMessage(null);
		if (wizardElement == null)
		{
			updateMessage();
			setSelectedNode(null);
			return;
		}

		setSelectedNode(createWizardNode(wizardElement));
		descriptionLabel.setText(wizardElement.getDescription());
	}

	/*
	 * Update the wizard's message based on the currently selected tab and the selected wizard on that tab.
	 */
	protected void updateMessage()
	{
		setMessage(com.aptana.deploy.wizard.WorkbenchMessages.DeployWizardPage_SelectYourDesiredDeploymentOption);
		TreeViewer viewer = getTreeViewer();
		if (viewer != null)
		{
			ISelection selection = viewer.getSelection();
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object sel = ss.getFirstElement();
			if (sel instanceof WorkbenchWizardElement)
			{
				updateSelectedNode((WorkbenchWizardElement) sel);
			}
			else
			{
				setSelectedNode(null);
			}
		}
		else
		{
			descriptionLabel.setText(null);
		}
	}

	/*
	 * Method to call whenever the selection in one of the lists has changed. Updates the wizard's message to reflect
	 * the description of the currently selected wizard.
	 */
	protected void listSelectionChanged(ISelection selection)
	{
		setErrorMessage(null);
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object sel = ss.getFirstElement();
		if (sel instanceof WorkbenchWizardElement)
		{
			WorkbenchWizardElement currentWizardSelection = (WorkbenchWizardElement) sel;
			updateSelectedNode(currentWizardSelection);
		}
		else
		{
			updateSelectedNode(null);
		}
	}

	/*
	 * Create a wizard node given a wizard's descriptor.
	 */
	private IWizardNode createWizardNode(IWizardDescriptor element)
	{
		return new WorkbenchWizardNode(this, element)
		{
			public IWorkbenchWizard createWizard() throws CoreException
			{
				return wizardElement.createWizard();
			}
		};
	}

	/**
	 * Uses the dialog store to restore widget values to the values that they held last time this wizard was used to
	 * completion.
	 */
	protected void restoreWidgetValues()
	{
		IWizardCategory exportRoot = WorkbenchPlugin.getDefault().getExportWizardRegistry().getRootCategory();
		expandPreviouslyExpandedCategories(STORE_EXPANDED_DEPLOY_CATEGORIES, exportRoot, wizardTree.getViewer());
		selectPreviouslySelected(STORE_SELECTED_DEPLOY_WIZARD_ID, exportRoot, wizardTree.getViewer());
		updateMessage();
	}

	/**
	 * Expands the wizard categories in this page's category viewer that were expanded last time this page was used. If
	 * a category that was previously expanded no longer exists then it is ignored.
	 */
	protected void expandPreviouslyExpandedCategories(String setting, IWizardCategory wizardCategories,
			TreeViewer viewer)
	{
		String[] expandedCategoryPaths = getDialogSettings().getArray(setting);
		if (expandedCategoryPaths == null || expandedCategoryPaths.length == 0)
		{
			return;
		}

		List<IWizardCategory> categoriesToExpand = new ArrayList<IWizardCategory>(expandedCategoryPaths.length);

		if (wizardCategories != null)
		{
			for (int i = 0; i < expandedCategoryPaths.length; i++)
			{
				IWizardCategory category = wizardCategories.findCategory(new Path(expandedCategoryPaths[i]));
				if (category != null)
				{
					categoriesToExpand.add(category);
				}
			}
		}

		if (!categoriesToExpand.isEmpty())
		{
			viewer.setExpandedElements(categoriesToExpand.toArray());
		}

	}

	/**
	 * Selects the wizard category and wizard in this page that were selected last time this page was used. If a
	 * category or wizard that was previously selected no longer exists then it is ignored.
	 */
	protected void selectPreviouslySelected(String setting, IWizardCategory wizardCategories, final TreeViewer viewer)
	{
		String selectedId = getDialogSettings().get(setting);
		if (selectedId == null)
		{
			return;
		}

		if (wizardCategories == null)
		{
			return;
		}

		Object selected = wizardCategories.findCategory(new Path(selectedId));

		if (selected == null)
		{
			selected = wizardCategories.findWizard(selectedId);

			if (selected == null)
			{
				// if we cant find either a category or a wizard, abort.
				return;
			}
		}

		viewer.setSelection(new StructuredSelection(selected), true);
	}

	/**
	 * Stores the collection of currently-expanded categories in this page's dialog store, in order to recreate this
	 * page's state in the next instance of this page.
	 */
	protected void storeExpandedCategories(String setting, TreeViewer viewer)
	{
		Object[] expandedElements = viewer.getExpandedElements();
		List<String> expandedElementPaths = new ArrayList<String>(expandedElements.length);
		for (Object element : expandedElements)
		{
			if (element instanceof IWizardCategory)
			{
				expandedElementPaths.add(((IWizardCategory) element).getPath().toString());
			}
		}
		getDialogSettings().put(setting, expandedElementPaths.toArray(new String[expandedElementPaths.size()]));
	}

	/**
	 * Stores the currently-selected element in this page's dialog store, in order to recreate this page's state in the
	 * next instance of this page.
	 */
	protected void storeSelectedCategoryAndWizard(String setting, TreeViewer viewer)
	{
		Object selected = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

		if (selected != null)
		{
			if (selected instanceof IWizardCategory)
			{
				getDialogSettings().put(setting, ((IWizardCategory) selected).getPath().toString());
			}
			else
			{
				// else its a wizard
				getDialogSettings().put(setting, ((IWizardDescriptor) selected).getId());
			}
		}
	}

	/**
	 * When Finish is pressed, write widget values to the dialog store so that they will persist into the next
	 * invocation of the wizard page.
	 */
	public void saveWidgetValues()
	{
		storeExpandedCategories(STORE_EXPANDED_DEPLOY_CATEGORIES, wizardTree.getViewer());
		storeSelectedCategoryAndWizard(STORE_SELECTED_DEPLOY_WIZARD_ID, wizardTree.getViewer());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizardPage#getNextPage()
	 */
	public IWizardPage getNextPage()
	{
		ITriggerPoint triggerPoint = getTriggerPoint();

		if (triggerPoint == null || WorkbenchActivityHelper.allowUseOf(triggerPoint, getSelectedNode()))
		{
			return super.getNextPage();
		}
		return null;
	}

	/**
	 * Get the trigger point for the wizard type, if one exists.
	 * 
	 * @return the wizard's trigger point
	 */
	protected ITriggerPoint getTriggerPoint()
	{
		return null; // default implementation
	}

	/**
	 * Set the tree viewer that is used for this wizard selection page.
	 * 
	 * @param viewer
	 */
	protected void setTreeViewer(TreeViewer viewer)
	{
		treeViewer = viewer;
	}

	/**
	 * Get the tree viewer that is used for this wizard selection page.
	 * 
	 * @return tree viewer used for this wizard's selection page
	 */
	protected TreeViewer getTreeViewer()
	{
		return treeViewer;
	}

	/**
	 * Perform any initialization of the wizard page that needs to be done after widgets are created and main control is
	 * set.
	 */
	protected void initialize()
	{
		// do nothing by default
	}
}
