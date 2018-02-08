/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.preferences.IFieldValidator;
import com.aptana.formatter.preferences.IPreferenceDelegate;
import com.aptana.formatter.ui.epl.FormatterUIEplPlugin;
import com.aptana.formatter.ui.util.IStatusChangeListener;
import com.aptana.formatter.ui.util.StatusInfo;
import com.aptana.formatter.ui.util.StatusUtil;
import com.aptana.formatter.ui.widgets.CListViewer;
import com.aptana.formatter.ui.widgets.IListDataChangeListener;

/**
 */
public class ControlBindingManager
{
	private IStatusChangeListener changeListener;

	private Map<Button, Object> checkBoxControls;
	private Map<Combo, Object> comboControls;
	private final Map<Combo, IComboSelectedValueProvider> comboValueProviders = new IdentityHashMap<Combo, IComboSelectedValueProvider>();

	private DependencyManager dependencyManager;

	private IPreferenceDelegate preferenceDelegate;
	private Map<Button, String> radioControls;
	private Map<Spinner, Object> spinnerControls;
	private Map<CListViewer, Object> listControls;

	private Map<Text, Object> textControls;
	private ValidatorManager validatorManager;

	public static class DependencyMode
	{
	}

	public static final DependencyMode DEPENDENCY_INVERSE_SELECTION = new DependencyMode();

	public interface IComboSelectedValueProvider
	{
		String getValueAt(int index);

		int indexOf(String value);
	}

	public ControlBindingManager(IPreferenceDelegate delegate, IStatusChangeListener listener)
	{
		this.checkBoxControls = new HashMap<Button, Object>();
		this.comboControls = new HashMap<Combo, Object>();
		this.textControls = new HashMap<Text, Object>();
		this.radioControls = new HashMap<Button, String>();
		this.spinnerControls = new HashMap<Spinner, Object>();
		this.listControls = new HashMap<CListViewer, Object>();

		this.validatorManager = new ValidatorManager();
		this.dependencyManager = new DependencyManager();

		this.changeListener = listener;
		this.preferenceDelegate = delegate;
	}

	public void bindControl(final Combo combo, final Object key)
	{
		bindControl(combo, key, new IComboSelectedValueProvider()
		{

			public String getValueAt(int index)
			{
				return index >= 0 && index < combo.getItemCount() ? combo.getItem(index) : null;
			}

			public int indexOf(String value)
			{
				final String[] items = combo.getItems();
				for (int i = 0; i < items.length; i++)
				{
					if (items[i].equals(value))
					{
						return i;
					}
				}
				return -1;
			}
		});
	}

	public void bindControl(Combo combo, Object key, final String[] itemValues)
	{
		bindControl(combo, key, new IComboSelectedValueProvider()
		{
			public String getValueAt(int index)
			{
				return itemValues[index];
			}

			public int indexOf(String value)
			{
				for (int i = 0; i < itemValues.length; i++)
				{
					if (itemValues[i].equals(value))
					{
						return i;
					}
				}
				return -1;
			}
		});
	}

	public void bindControl(final Combo combo, final Object key, final IComboSelectedValueProvider itemValueProvider)
	{
		if (key != null)
		{
			comboControls.put(combo, key);
		}
		comboValueProviders.put(combo, itemValueProvider);

		combo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// do nothing
			}

			public void widgetSelected(SelectionEvent e)
			{
				int index = combo.getSelectionIndex();
				preferenceDelegate.setString(key, itemValueProvider.getValueAt(index));

				changeListener.statusChanged(StatusInfo.OK_STATUS);
			}
		});
	}

	/**
	 * @param spinner
	 * @param key
	 */
	public void bindControl(final Spinner spinner, final Object key)
	{
		if (key != null)
		{
			spinnerControls.put(spinner, key);
		}
		spinner.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// do nothing
			}

			public void widgetSelected(SelectionEvent e)
			{
				preferenceDelegate.setString(key, spinner.getText());
				changeListener.statusChanged(StatusInfo.OK_STATUS);
			}
		});

	}

	public void bindControl(final Button button, final Object key, Control[] slaves)
	{
		if (key != null)
		{
			checkBoxControls.put(button, key);
		}

		createDependency(button, slaves);

		button.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// do nothing
			}

			public void widgetSelected(SelectionEvent e)
			{
				boolean state = button.getSelection();
				preferenceDelegate.setBoolean(key, state);

				updateStatus(StatusInfo.OK_STATUS);
			}
		});
	}

	public void bindControl(final Text text, final Object key, IFieldValidator validator)
	{
		if (key != null)
		{
			if (textControls.containsKey(key))
			{
				final RuntimeException error = new IllegalArgumentException("Duplicate control " + key); //$NON-NLS-1$
				IdeLog.logError(FormatterUIEplPlugin.getDefault(), error, IDebugScopes.DEBUG);
				if (FormatterUIEplPlugin.DEBUG)
				{
					throw error;
				}
			}

			textControls.put(text, key);
		}

		if (validator != null)
		{
			validatorManager.registerValidator(text, validator);
		}

		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				IStatus status = validateText(text);

				if (key != null)
				{
					if (status.getSeverity() != IStatus.ERROR)
					{
						String value = text.getText();
						preferenceDelegate.setString(key, value);
					}
				}

				updateStatus(status);
			}
		});
	}

	public void bindRadioControl(final Button button, final String key, final Object enable, Control[] dependencies)
	{
		if (key != null)
		{
			radioControls.put(button, key);
		}

		createDependency(button, dependencies);

		button.setData(String.valueOf(enable));
		button.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
				// do nothing
			}

			public void widgetSelected(SelectionEvent e)
			{
				String value = String.valueOf(enable);
				preferenceDelegate.setString(key, value);
			}
		});
	}

	public void bindControl(final CListViewer list, final Object key)
	{
		if (key != null)
		{
			listControls.put(list, key);
		}
		list.addListDataChangeListener(new IListDataChangeListener()
		{
			public void inputChanged(Object input, Object oldInput)
			{
				Object[] in = (Object[]) input;
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < in.length; i++)
				{
					builder.append(in[i]);
					if (i + 1 < in.length)
					{
						builder.append(IPreferenceDelegate.PREFERECE_DELIMITER);
					}
				}
				String oldValue = preferenceDelegate.getString(key);
				String newValue = builder.toString();
				if (!newValue.equals(oldValue))
				{
					preferenceDelegate.setString(key, newValue);
					changeListener.statusChanged(StatusInfo.OK_STATUS);
				}
			}
		});
	}

	public void createDependency(final Button button, Control[] dependencies)
	{
		createDependency(button, dependencies, null);
	}

	public void createDependency(final Button button, Control[] dependencies, DependencyMode mode)
	{
		if (dependencies != null)
		{
			dependencyManager.createDependency(button, dependencies, mode);
		}
	}

	public IStatus getStatus()
	{
		IStatus status = StatusInfo.OK_STATUS;
		Iterator<Text> iter = textControls.keySet().iterator();
		while (iter.hasNext())
		{
			IStatus s = validateText(iter.next());
			status = StatusUtil.getMoreSevere(s, status);
		}

		return status;
	}

	public void initialize()
	{
		initTextControls();
		initCheckBoxes();
		initRadioControls();
		initCombos();
		initSpinners();
		initListControls();

		dependencyManager.initialize();
	}

	protected void updateStatus(IStatus status)
	{
		if (!status.matches(IStatus.ERROR))
		{
			Iterator<Text> iter = textControls.keySet().iterator();
			while (iter.hasNext())
			{
				IStatus s = validateText(iter.next());
				status = StatusUtil.getMoreSevere(s, status);
			}
		}

		changeListener.statusChanged(status);
	}

	private void initCheckBoxes()
	{
		Iterator<Button> it = checkBoxControls.keySet().iterator();
		while (it.hasNext())
		{
			final Button button = it.next();
			final Object key = checkBoxControls.get(button);
			button.setSelection(preferenceDelegate.getBoolean(key));
		}
	}

	private void initCombos()
	{
		for (Iterator<Map.Entry<Combo, Object>> it = comboControls.entrySet().iterator(); it.hasNext();)
		{
			final Map.Entry<Combo, Object> entry = it.next();
			final Combo combo = (Combo) entry.getKey();
			final Object key = entry.getValue();
			final String value = preferenceDelegate.getString(key);
			final IComboSelectedValueProvider valueProvider = (IComboSelectedValueProvider) comboValueProviders
					.get(combo);
			if (valueProvider != null)
			{
				int index = valueProvider.indexOf(value);
				if (index >= 0)
				{
					combo.select(index);
				}
				else
				{
					combo.select(0);
				}
			}
		}
	}

	private void initSpinners()
	{
		Iterator<Spinner> it = spinnerControls.keySet().iterator();
		while (it.hasNext())
		{
			final Spinner spinner = it.next();
			final Object key = spinnerControls.get(spinner);
			String value = preferenceDelegate.getString(key);
			if (!StringUtil.isEmpty(value))
			{
				spinner.setSelection(Integer.parseInt(value));
			}
		}
	}

	private void initRadioControls()
	{
		Iterator<Button> it = radioControls.keySet().iterator();
		while (it.hasNext())
		{
			Button button = it.next();
			String key = radioControls.get(button);

			String enable = (String) button.getData();
			String value = preferenceDelegate.getString(key);

			if (enable != null && enable.equals(value))
			{
				button.setSelection(true);
			}
			else
			{
				button.setSelection(false);
			}
		}
	}

	private void initListControls()
	{
		Iterator<CListViewer> it = listControls.keySet().iterator();
		while (it.hasNext())
		{
			final CListViewer list = it.next();
			final Object key = listControls.get(list);
			String value = preferenceDelegate.getString(key);
			String[] elements = (value != null) ? value.split(IPreferenceDelegate.PREFERECE_DELIMITER) : new String[0];
			list.setInput(elements);
		}
	}

	private void initTextControls()
	{
		Iterator<Text> it = textControls.keySet().iterator();
		while (it.hasNext())
		{
			final Text text = it.next();
			final Object key = textControls.get(text);
			String value = preferenceDelegate.getString(key);
			/*
			 * final ITextConverter textTransformer = (ITextConverter) textTransformers .get(text); if (textTransformer
			 * != null) { value = textTransformer.convertPreference(value); }
			 */

			text.setText(value);
		}
	}

	protected IStatus validateText(Text text)
	{
		IFieldValidator validator = validatorManager.getValidator(text);
		if ((validator != null) && text.isEnabled())
		{
			return validator.validate(text.getText());
		}

		return StatusInfo.OK_STATUS;
	}

	/**
     */
	class DependencyManager
	{
		private List<SelectionListener> masterSlaveListeners = new ArrayList<SelectionListener>();

		public void createDependency(final Button master, final Control[] slaves, final DependencyMode mode)
		{
			SelectionListener listener = new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					boolean state = master.getSelection();
					// set enablement to the opposite of the selection value
					if (mode == DEPENDENCY_INVERSE_SELECTION)
					{
						state = !state;
					}

					for (int i = 0; i < slaves.length; i++)
					{
						slaves[i].setEnabled(state);
					}

					changeListener.statusChanged(StatusInfo.OK_STATUS);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
					// do nothing
				}
			};

			master.addSelectionListener(listener);
			masterSlaveListeners.add(listener);
		}

		public void initialize()
		{
			Iterator<SelectionListener> it = masterSlaveListeners.iterator();
			while (it.hasNext())
			{
				it.next().widgetSelected(null);
			}
		}
	}

	/**
     */
	class ValidatorManager
	{
		private Map<Text, IFieldValidator> map = new HashMap<Text, IFieldValidator>();

		public IFieldValidator getValidator(Text text)
		{
			return map.get(text);
		}

		public void registerValidator(Text text, IFieldValidator validator)
		{
			map.put(text, validator);
		}

		public void unregisterValidator(Text text)
		{
			map.remove(text);
		}
	}
}
