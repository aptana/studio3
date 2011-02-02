/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.validator.ValidatorLanguage;
import com.aptana.editor.common.validator.ValidatorLoader;
import com.aptana.editor.common.validator.ValidatorReference;
import com.aptana.ui.widgets.CListTable;

public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	// to distinguish between the default state and user-modified one for preference value
	private static final String EMPTY_LIST = "empty"; //$NON-NLS-1$
	private static final String VALIDATORS_DELIMITER = ","; //$NON-NLS-1$
	private static final String FILTER_DELIMITER = "####"; //$NON-NLS-1$

	private ListViewer languagesViewer;
	private CheckboxTableViewer validatorsViewer;
	private CListTable filterViewer;

	// stores the list of selected validators by language type
	private Map<String, List<String>> selectedValidatorsMap;
	// stores the list of filter expressions by language type
	private Map<String, List<String>> filterExpressionsMap;

	private String selectedLanguage;

	public ValidationPreferencePage()
	{
		selectedValidatorsMap = new HashMap<String, List<String>>();
		filterExpressionsMap = new HashMap<String, List<String>>();
	}

	public void init(IWorkbench workbench)
	{
		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
		loadAllSelectedValidators();
		loadAllFilterExpressions();
	}

	@Override
	public void dispose()
	{
		selectedValidatorsMap.clear();
		filterExpressionsMap.clear();
		super.dispose();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// the left side
		languagesViewer = new ListViewer(sash, SWT.BORDER | SWT.SINGLE);
		languagesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().create());
		languagesViewer.setContentProvider(ArrayContentProvider.getInstance());
		languagesViewer.setLabelProvider(new LabelProvider()
		{

			@Override
			public String getText(Object element)
			{
				if (element instanceof ValidatorLanguage)
				{
					return ((ValidatorLanguage) element).getName();
				}
				return super.getText(element);
			}
		});
		List<ValidatorLanguage> languages = ValidatorLoader.getInstance().getLanguages();
		languagesViewer.setInput(languages.toArray(new ValidatorLanguage[languages.size()]));

		if (languages.size() > 0)
		{
			ValidatorLanguage language = languages.get(0);
			languagesViewer.setSelection(new StructuredSelection(language));
			selectedLanguage = language.getType();
		}
		languagesViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				if (selectedLanguage != null)
				{
					// stores the selected validators and filter expressions for the previously selected language
					storeCurrentSelectedValidators();
					storeCurrentFilterExpressions();
				}

				selectedLanguage = getSelectedLanguageType();
				// updates the validators and filter expressions to the newly selected language
				updateValidators();
				updateFilterExpressions();
			}
		});

		// the right side
		Composite rightComp = new Composite(sash, SWT.NONE);
		rightComp.setLayout(GridLayoutFactory.fillDefaults().create());

		Control validators = createValidators(rightComp);
		validators.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Control filter = createFilter(rightComp);
		filter.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		sash.setWeights(new int[] { 1, 3 });
		return sash;
	}

	@Override
	public boolean performOk()
	{
		if (selectedLanguage != null)
		{
			storeCurrentSelectedValidators();
			storeCurrentFilterExpressions();
		}
		// persists the selected validators and filter expressions for each language
		saveAllSelectedValidators();
		saveAllFilterExpressions();

		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		List<ValidatorLanguage> languages = ValidatorLoader.getInstance().getLanguages();
		String languageType, list;
		for (ValidatorLanguage language : languages)
		{
			languageType = language.getType();
			list = getPreferenceStore().getDefaultString(getFilterExpressionsPrefKey(languageType));
			if (!StringUtil.isEmpty(list))
			{
				List<String> expressions = new ArrayList<String>();
				expressions.addAll(Arrays.asList(list.split(FILTER_DELIMITER)));
				filterExpressionsMap.put(languageType, expressions);
			}
		}
		updateFilterExpressions();

		super.performDefaults();
	}

	private Control createValidators(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.ValidationPreferencePage_LBL_Validators);
		group.setLayout(GridLayoutFactory.fillDefaults().create());

		validatorsViewer = CheckboxTableViewer.newCheckList(group, SWT.SINGLE);
		validatorsViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		validatorsViewer.setContentProvider(ArrayContentProvider.getInstance());
		validatorsViewer.setLabelProvider(new LabelProvider()
		{

			@Override
			public String getText(Object element)
			{
				if (element instanceof ValidatorReference)
				{
					return ((ValidatorReference) element).getName();
				}
				return super.getText(element);
			}
		});
		updateValidators();

		return group;
	}

	private Control createFilter(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.ValidationPreferencePage_LBL_Filter);
		group.setLayout(GridLayoutFactory.fillDefaults().create());

		filterViewer = new CListTable(group, SWT.NONE);
		filterViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_Description);
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
		});
		updateFilterExpressions();

		return group;
	}

	private void loadAllSelectedValidators()
	{
		List<ValidatorLanguage> languages = ValidatorLoader.getInstance().getLanguages();
		String languageType, list;
		for (ValidatorLanguage language : languages)
		{
			languageType = language.getType();
			list = getPreferenceStore().getString(getSelectedValidatorsPrefKey(languageType));
			if (!StringUtil.isEmpty(list))
			{
				List<String> names = new ArrayList<String>();
				if (!EMPTY_LIST.equals(list))
				{
					names.addAll(Arrays.asList(list.split(VALIDATORS_DELIMITER)));
				}
				selectedValidatorsMap.put(languageType, names);
			}
		}
	}

	private void saveAllSelectedValidators()
	{
		Set<String> languages = selectedValidatorsMap.keySet();
		List<String> selectedValidators;
		String value;
		for (String language : languages)
		{
			selectedValidators = selectedValidatorsMap.get(language);
			int size = selectedValidators.size();
			if (size == 0)
			{
				value = EMPTY_LIST;
			}
			else
			{
				value = StringUtil.join(VALIDATORS_DELIMITER,
						selectedValidators.toArray(new String[selectedValidators.size()]));
			}
			getPreferenceStore().setValue(getSelectedValidatorsPrefKey(language), value);
		}
	}

	private void loadAllFilterExpressions()
	{
		List<ValidatorLanguage> languages = ValidatorLoader.getInstance().getLanguages();
		String languageType, list;
		for (ValidatorLanguage language : languages)
		{
			languageType = language.getType();
			list = getPreferenceStore().getString(getFilterExpressionsPrefKey(languageType));
			if (!StringUtil.isEmpty(list))
			{
				List<String> expressions = new ArrayList<String>();
				expressions.addAll(Arrays.asList(list.split(FILTER_DELIMITER)));
				filterExpressionsMap.put(languageType, expressions);
			}
		}
	}

	private void saveAllFilterExpressions()
	{
		Set<String> languages = filterExpressionsMap.keySet();
		List<String> expressions;
		for (String language : languages)
		{
			expressions = filterExpressionsMap.get(language);
			getPreferenceStore().setValue(getFilterExpressionsPrefKey(language),
					StringUtil.join(FILTER_DELIMITER, expressions.toArray(new String[expressions.size()])));
		}
	}

	private void storeCurrentSelectedValidators()
	{
		List<String> validatorsList = selectedValidatorsMap.get(selectedLanguage);
		if (validatorsList == null)
		{
			validatorsList = new ArrayList<String>();
			selectedValidatorsMap.put(selectedLanguage, validatorsList);
		}
		validatorsList.clear();
		Object[] selectedValidators = validatorsViewer.getCheckedElements();
		for (Object validator : selectedValidators)
		{
			if (validator instanceof ValidatorReference)
			{
				validatorsList.add(((ValidatorReference) validator).getName());
			}
		}
	}

	private void storeCurrentFilterExpressions()
	{
		List<String> filterList = filterExpressionsMap.get(selectedLanguage);
		if (filterList == null)
		{
			filterList = new ArrayList<String>();
			filterExpressionsMap.put(selectedLanguage, filterList);
		}
		filterList.clear();
		List<Object> expressions = filterViewer.getItems();
		for (Object expression : expressions)
		{
			filterList.add(expression.toString());
		}
	}

	private void updateValidators()
	{
		if (selectedLanguage == null)
		{
			validatorsViewer.setInput(new ValidatorReference[0]);
		}
		else
		{
			List<ValidatorReference> validators = ValidatorLoader.getInstance().getValidators(selectedLanguage);
			validatorsViewer.setInput(validators.toArray(new ValidatorReference[validators.size()]));

			// makes appropriate validators checked
			if (selectedValidatorsMap.containsKey(selectedLanguage))
			{
				List<String> selectedValidators = selectedValidatorsMap.get(selectedLanguage);
				for (ValidatorReference validator : validators)
				{
					validatorsViewer.setChecked(validator, selectedValidators.contains(validator.getName()));
				}
			}
			else if (validators.size() > 0)
			{
				// default case; the first validator will be selected if one exists
				validatorsViewer.setChecked(validators.get(0), true);
			}
		}
	}

	private void updateFilterExpressions()
	{
		Object[] items;
		if (selectedLanguage == null)
		{
			items = new Object[0];
		}
		else
		{
			List<String> expressions = filterExpressionsMap.get(selectedLanguage);
			if (expressions == null)
			{
				items = new Object[0];
			}
			else
			{
				items = expressions.toArray(new String[expressions.size()]);
			}
		}
		filterViewer.setItems(items);
	}

	private String getSelectedLanguageType()
	{
		IStructuredSelection selection = (IStructuredSelection) languagesViewer.getSelection();
		if (selection.isEmpty())
		{
			return null;
		}
		return ((ValidatorLanguage) selection.getFirstElement()).getType();
	}

	private static String getSelectedValidatorsPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.SELECTED_VALIDATORS; //$NON-NLS-1$
	}

	private static String getFilterExpressionsPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.FILTER_EXPRESSIONS; //$NON-NLS-1$
	}
}
