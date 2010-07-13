package com.aptana.ui.preferences;

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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.misc.StatusUtil;

import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.util.IStatusChangeListener;
import com.aptana.ui.util.StatusInfo;

/**
 */
public class ControlBindingManager {
	private IStatusChangeListener changeListener;

	private Map checkBoxControls;
	private Map comboControls;
	private final Map comboValueProviders = new IdentityHashMap();

	private DependencyManager dependencyManager;

	private IPreferenceDelegate preferenceDelegate;
	private Map radioControls;

	private Map textControls;
	private final Map textTransformers = new HashMap();
	private ValidatorManager validatorManager;

	public static class DependencyMode {
	}

	public static final DependencyMode DEPENDENCY_INVERSE_SELECTION = new DependencyMode();

	public interface IComboSelectedValueProvider {
		String getValueAt(int index);

		int indexOf(String value);
	}

	public ControlBindingManager(IPreferenceDelegate delegate,
			IStatusChangeListener listener) {
		this.checkBoxControls = new HashMap();
		this.comboControls = new HashMap();
		this.textControls = new HashMap();
		this.radioControls = new HashMap();

		this.validatorManager = new ValidatorManager();
		this.dependencyManager = new DependencyManager();

		this.changeListener = listener;
		this.preferenceDelegate = delegate;
	}

	public void bindControl(final Combo combo, final Object key) {
		bindControl(combo, key, new IComboSelectedValueProvider() {

			public String getValueAt(int index) {
				return index >= 0 && index < combo.getItemCount() ? combo
						.getItem(index) : null;
			}

			public int indexOf(String value) {
				final String[] items = combo.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].equals(value)) {
						return i;
					}
				}
				return -1;
			}
		});
	}

	public void bindControl(Combo combo, Object key, final String[] itemValues) {
		bindControl(combo, key, new IComboSelectedValueProvider() {
			public String getValueAt(int index) {
				return itemValues[index];
			}

			public int indexOf(String value) {
				for (int i = 0; i < itemValues.length; i++) {
					if (itemValues[i].equals(value)) {
						return i;
					}
				}
				return -1;
			}
		});
	}

	public void bindControl(final Combo combo, final Object key,
			final IComboSelectedValueProvider itemValueProvider) {
		if (key != null) {
			comboControls.put(combo, key);
		}
		comboValueProviders.put(combo, itemValueProvider);

		combo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				int index = combo.getSelectionIndex();
				preferenceDelegate.setString(key, itemValueProvider
						.getValueAt(index));

				changeListener.statusChanged(StatusInfo.OK_STATUS);
			}
		});
	}

	public void bindControl(final Button button, final Object key,
			Control[] slaves) {
		if (key != null) {
			checkBoxControls.put(button, key);
		}

		createDependency(button, slaves);

		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				boolean state = button.getSelection();
				preferenceDelegate.setBoolean(key, state);

				updateStatus(StatusInfo.OK_STATUS);
			}
		});
	}

	public void bindControl(final Text text, final Object key,
			IFieldValidator validator) {
		bindControl(text, key, validator, null);
	}

	public void bindControl(final Text text, final Object key,
			IFieldValidator validator, final ITextConverter transformer) {
		if (key != null) {
			if (textControls.containsKey(key)) {
				final RuntimeException error = new IllegalArgumentException(
						"Duplicate control " + key); //$NON-NLS-1$
				UIEplPlugin.logError(error.getMessage(), error);
				if (UIEplPlugin.DEBUG) {
					throw error;
				}
			}

			textControls.put(text, key);
			if (transformer != null) {
				textTransformers.put(text, transformer);
			}
		}

		if (validator != null) {
			validatorManager.registerValidator(text, validator);
		}

		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				IStatus status = validateText(text);

				if (key != null) {
					if (status.getSeverity() != IStatus.ERROR) {
						String value = text.getText();
						if (transformer != null) {
							value = transformer.convertInput(value);
						}

						preferenceDelegate.setString(key, value);
					}
				}

				updateStatus(status);
			}
		});
	}

	public void bindRadioControl(final Button button, final String key,
			final Object enable, Control[] dependencies) {
		if (key != null) {
			radioControls.put(button, key);
		}

		createDependency(button, dependencies);

		button.setData(String.valueOf(enable));
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				String value = String.valueOf(enable);
				preferenceDelegate.setString(key, value);
			}
		});
	}

	public void createDependency(final Button button, Control[] dependencies) {
		createDependency(button, dependencies, null);
	}

	public void createDependency(final Button button, Control[] dependencies,
			DependencyMode mode) {
		if (dependencies != null) {
			dependencyManager.createDependency(button, dependencies, mode);
		}
	}

	public IStatus getStatus() {
		IStatus status = StatusInfo.OK_STATUS;
		Iterator iter = textControls.keySet().iterator();
		while (iter.hasNext()) {
			IStatus s = validateText((Text) iter.next());
			status = StatusUtil.getMoreSevere(s, status);
		}

		return status;
	}

	public void initialize() {
		initTextControls();
		initCheckBoxes();
		initRadioControls();
		initCombos();

		dependencyManager.initialize();
	}

	protected void updateStatus(IStatus status) {
		if (!status.matches(IStatus.ERROR)) {
			Iterator iter = textControls.keySet().iterator();
			while (iter.hasNext()) {
				IStatus s = validateText((Text) iter.next());
				status = StatusUtil.getMoreSevere(s, status);
			}
		}

		changeListener.statusChanged(status);
	}

	private void initCheckBoxes() {
		Iterator it = checkBoxControls.keySet().iterator();
		while (it.hasNext()) {
			final Button button = (Button) it.next();
			final Object key = checkBoxControls.get(button);
			button.setSelection(preferenceDelegate.getBoolean(key));
		}
	}

	private void initCombos() {
		for (Iterator it = comboControls.entrySet().iterator(); it.hasNext();) {
			final Map.Entry entry = (Map.Entry) it.next();
			final Combo combo = (Combo) entry.getKey();
			final Object key = entry.getValue();
			final String value = preferenceDelegate.getString(key);
			final IComboSelectedValueProvider valueProvider = (IComboSelectedValueProvider) comboValueProviders
					.get(combo);
			if (valueProvider != null) {
				int index = valueProvider.indexOf(value);
				if (index >= 0) {
					combo.select(index);
				} else {
					combo.select(0);
				}
			}
		}
	}

	private void initRadioControls() {
		Iterator it = radioControls.keySet().iterator();
		while (it.hasNext()) {
			Button button = (Button) it.next();
			Object key = radioControls.get(button);

			String enable = (String) button.getData();
			String value = preferenceDelegate.getString(key);

			if (enable != null && enable.equals(value)) {
				button.setSelection(true);
			} else {
				button.setSelection(false);
			}
		}
	}

	private void initTextControls() {
		Iterator it = textControls.keySet().iterator();
		while (it.hasNext()) {
			final Text text = (Text) it.next();
			final Object key = textControls.get(text);
			String value = preferenceDelegate.getString(key);
			final ITextConverter textTransformer = (ITextConverter) textTransformers
					.get(text);
			if (textTransformer != null) {
				value = textTransformer.convertPreference(value);
			}

			text.setText(value);
		}
	}

	private IStatus validateText(Text text) {
		IFieldValidator validator = validatorManager.getValidator(text);
		if ((validator != null) && text.isEnabled()) {
			return validator.validate(text.getText());
		}

		return StatusInfo.OK_STATUS;
	}

	/**
     */
	class DependencyManager {
		private List masterSlaveListeners = new ArrayList();

		public void createDependency(final Button master,
				final Control[] slaves, final DependencyMode mode) {
			SelectionListener listener = new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					boolean state = master.getSelection();
					// set enablement to the opposite of the selection value
					if (mode == DEPENDENCY_INVERSE_SELECTION) {
						state = !state;
					}

					for (int i = 0; i < slaves.length; i++) {
						slaves[i].setEnabled(state);
					}

					changeListener.statusChanged(StatusInfo.OK_STATUS);
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing
				}
			};

			master.addSelectionListener(listener);
			masterSlaveListeners.add(listener);
		}

		public void initialize() {
			Iterator it = masterSlaveListeners.iterator();
			while (it.hasNext()) {
				((SelectionListener) it.next()).widgetSelected(null);
			}
		}
	}

	/**
     */
	class ValidatorManager {

		private Map map = new HashMap();

		public IFieldValidator getValidator(Control control) {
			return (IFieldValidator) map.get(control);
		}

		public void registerValidator(Text text, IFieldValidator validator) {
			map.put(text, validator);
		}

		public void unregisterValidator(Text text) {
			map.remove(text);
		}

	}

}
