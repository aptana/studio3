package com.aptana.ui.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Similar to a boolean field editor, but respects a chained preferenc store for finding out the default value
 * 
 * @author Ingo Muschenetz
 */
public class ChainedBooleanFieldEditor extends FieldEditor
{
	protected IPreferenceStore chainedPreferenceStore;

	/**
	 * Style constant (value <code>0</code>) indicating the default layout where the field editor's check box appears to
	 * the left of the label.
	 */
	public static final int DEFAULT = 0;

	/**
	 * Style constant (value <code>1</code>) indicating a layout where the field editor's label appears on the left with
	 * a check box on the right.
	 */
	public static final int SEPARATE_LABEL = 1;

	/**
	 * Style bits. Either <code>DEFAULT</code> or <code>SEPARATE_LABEL</code>.
	 */
	private int style;

	/**
	 * The previously selected, or "before", value.
	 */
	private boolean wasSelected;

	/**
	 * The checkbox control, or <code>null</code> if none.
	 */
	private Button checkBox = null;

	/**
	 * Creates a new boolean field editor
	 */
	protected ChainedBooleanFieldEditor()
	{
	}

	/**
	 * Creates a boolean field editor in the given style.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param style
	 *            the style, either <code>DEFAULT</code> or <code>SEPARATE_LABEL</code>
	 * @param parent
	 *            the parent of the field editor's control
	 * @param store
	 *            the preference store to look for the default value
	 * @see #DEFAULT
	 * @see #SEPARATE_LABEL
	 */
	public ChainedBooleanFieldEditor(String name, String labelText, int style, Composite parent, IPreferenceStore store)
	{
		init(name, labelText);
		this.style = style;
		createControl(parent);
		chainedPreferenceStore = store;
	}

	/**
	 * Creates a boolean field editor in the default style.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param label
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param store
	 *            the preference store to look for the default value
	 */
	public ChainedBooleanFieldEditor(String name, String label, Composite parent, IPreferenceStore store)
	{
		this(name, label, DEFAULT, parent, store);
		chainedPreferenceStore = store;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		if (style == SEPARATE_LABEL)
		{
			numColumns--;
		}
		((GridData) checkBox.getLayoutData()).horizontalSpan = numColumns;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		String text = getLabelText();
		switch (style)
		{
			case SEPARATE_LABEL:
				getLabelControl(parent);
				numColumns--;
				text = null;
				//$FALL-THROUGH$
			default:
				checkBox = getChangeControl(parent);
				GridData gd = new GridData();
				gd.horizontalSpan = numColumns;
				checkBox.setLayoutData(gd);
				if (text != null)
				{
					checkBox.setText(text);
				}
		}
	}

	/**
	 * Returns the control responsible for displaying this field editor's label. This method can be used to set a
	 * tooltip for a <code>BooleanFieldEditor</code>. Note that the normal pattern of
	 * <code>getLabelControl(parent).setToolTipText(tooltipText)</code> does not work for boolean field editors, as it
	 * can lead to duplicate text (see bug 259952).
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the control responsible for displaying the label
	 * @since 3.5
	 */
	public Control getDescriptionControl(Composite parent)
	{
		if (style == SEPARATE_LABEL)
		{
			return getLabelControl(parent);
		}
		return getChangeControl(parent);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor. Loads the value from the preference store and sets it to the check
	 * box.
	 */
	protected void doLoad()
	{
		if (checkBox != null)
		{
			boolean value = getLookupPreferenceStore().getBoolean(getPreferenceName());
			checkBox.setSelection(value);
			wasSelected = value;
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor. Loads the default value from the preference store and sets it to
	 * the check box.
	 */
	protected void doLoadDefault()
	{
		if (checkBox != null)
		{
			boolean value = getLookupPreferenceStore().getDefaultBoolean(getPreferenceName());
			checkBox.setSelection(value);
			wasSelected = value;
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		boolean value = checkBox.getSelection();
		// if the value is false and the pref key is not set in the local preference store
		// it will never actually set the false value in prefs.
		if (!getPreferenceStore().contains(getPreferenceName()) && value == false)
		{
			getPreferenceStore().putValue(getPreferenceName(), Boolean.toString(value));
		}
		getPreferenceStore().setValue(getPreferenceName(), checkBox.getSelection());
	}

	/**
	 * Returns this field editor's current value.
	 * 
	 * @return the value
	 */
	public boolean getBooleanValue()
	{
		return checkBox.getSelection();
	}

	/**
	 * Returns the change button for this field editor.
	 * 
	 * @param parent
	 *            The Composite to create the receiver in.
	 * @return the change button
	 */
	protected Button getChangeControl(Composite parent)
	{
		if (checkBox == null)
		{
			checkBox = new Button(parent, SWT.CHECK | SWT.LEFT);
			checkBox.setFont(parent.getFont());
			checkBox.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					boolean isSelected = checkBox.getSelection();
					valueChanged(wasSelected, isSelected);
					wasSelected = isSelected;
				}
			});
			checkBox.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					checkBox = null;
				}
			});
		}
		else
		{
			checkParent(checkBox, parent);
		}
		return checkBox;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls()
	{
		switch (style)
		{
			case SEPARATE_LABEL:
				return 2;
			default:
				return 1;
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus()
	{
		if (checkBox != null)
		{
			checkBox.setFocus();
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setLabelText(String text)
	{
		super.setLabelText(text);
		Label label = getLabelControl();
		if (label == null && checkBox != null)
		{
			checkBox.setText(text);
		}
	}

	/**
	 * Informs this field editor's listener, if it has one, about a change to the value (<code>VALUE</code> property)
	 * provided that the old and new values are different.
	 * 
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	protected void valueChanged(boolean oldValue, boolean newValue)
	{
		setPresentsDefaultValue(false);
		if (oldValue != newValue)
		{
			fireStateChanged(VALUE, oldValue, newValue);
		}
	}

	/*
	 * @see FieldEditor.setEnabled
	 */
	public void setEnabled(boolean enabled, Composite parent)
	{
		// Only call super if there is a label already
		if (style == SEPARATE_LABEL)
		{
			super.setEnabled(enabled, parent);
		}
		getChangeControl(parent).setEnabled(enabled);
	}

	/**
	 * Returns the preference store used by this field editor for lookup.
	 * 
	 * @return
	 */
	protected IPreferenceStore getLookupPreferenceStore()
	{
		return chainedPreferenceStore;
	}
}
