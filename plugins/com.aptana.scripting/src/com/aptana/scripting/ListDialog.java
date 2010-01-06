package com.aptana.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Dialog to pick a branch.
 * 
 * @author cwilliams
 */
public class ListDialog extends Dialog
{

	private Map options;
	private Combo combo;
	private String value;

	public ListDialog(Shell parentShell, Map options)
	{
		super(parentShell);
		this.options = options;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText((String) options.get("title")); //$NON-NLS-1$
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		Label label = new Label(composite, SWT.WRAP);
		label.setText((String) options.get("prompt")); //$NON-NLS-1$

		combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		List items = (List) options.get("items"); //$NON-NLS-1$
		List<String> branchNames = convertToStrings(items);

		combo.setItems(branchNames.toArray(new String[branchNames.size()]));
		String first = branchNames.iterator().next();
		value = first;
		String defaultValue = (String) options.get("string"); //$NON-NLS-1$
		if (defaultValue != null && defaultValue.trim().length() > 0)
			value = defaultValue;
		combo.setText(value);
		combo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				value = combo.getText();
			}
		});
		return composite;
	}

	@SuppressWarnings("rawtypes")
	private List<String> convertToStrings(List items)
	{
		List<String> strings = new ArrayList<String>();
		for (Object item : items)
		{
			if (item instanceof IRubyObject)
			{
				IRubyObject rubyObj = (IRubyObject) item;
				strings.add(rubyObj.asJavaString());
			}
			else
			{
				strings.add(item.toString());
			}
		}
		return strings;
	}

	public String getValue()
	{
		return value;
	}

}
