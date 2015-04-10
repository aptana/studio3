/**
 * Aptana Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.util.UIUtils;

/**
 * This dialog dynamically builds UI for a single question, and prompt the user for their response.
 *
 * @author pinnamuri
 */
public class InputMessageDialog extends MessageDialog
{

	/**
	 * UI Control types.
	 */
	protected static final String LIST = "list"; //$NON-NLS-1$
	protected static final String CHECKBOX = "checkbox"; //$NON-NLS-1$
	protected static final String PASSWORD = "password"; //$NON-NLS-1$
	protected static final String INPUT = "input"; //$NON-NLS-1$

	/**
	 * Attributes.
	 */
	protected static final String NAME = "name"; //$NON-NLS-1$
	protected static final String TYPE = "type"; //$NON-NLS-1$
	protected static final String CHOICES = "choices"; //$NON-NLS-1$
	protected static final String MESSAGE = "message"; //$NON-NLS-1$
	protected static final String VALUE = "value"; //$NON-NLS-1$

	private List<Object> input;
	private String inputType;
	private String userInput;
	private String dialogMessage;
	private ArrayNode values;

	public InputMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
			int dialogImageType, String[] dialogButtonLabels, int defaultIndex)
	{
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
	}

	public InputMessageDialog(JsonNode questionNode, String title, String description)
	{
		super(UIUtils.getActiveShell(), title, null, description, MessageDialog.CONFIRM, new String[] {
				IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
		this.dialogMessage = questionNode.path(MESSAGE).asText();
		this.inputType = questionNode.path(TYPE).asText();
		this.values = (ArrayNode) questionNode.path(CHOICES);

		input = new ArrayList<Object>();
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		createMessageArea(composite);
		createInput(composite);
		return composite;
	}

	private void createInput(Composite composite)
	{
		new Label(composite, SWT.NONE);
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).create());
		Label label = new Label(parent, SWT.NONE);
		label.setText(dialogMessage);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 25)
				.applyTo(label);

		Composite valueComp = new Composite(parent, SWT.NONE);
		valueComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		if (INPUT.equals(inputType) || PASSWORD.equals(inputType))
		{
			int flags = SWT.BORDER;
			if (PASSWORD.equals(inputType))
			{
				flags |= SWT.PASSWORD;
			}
			Text t = new Text(valueComp, flags);
			input.add(t);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
					.hint(convertHorizontalDLUsToPixels(160), 25).applyTo(valueComp);
		}
		else if (CHECKBOX.equals(inputType))
		{
			for (JsonNode value : values)
			{
				Button b = new Button(valueComp, SWT.CHECK);
				b.setText(value.asText());
				input.add(b);
			}
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(valueComp);
		}
		else if (LIST.equals(inputType))
		{
			Combo l = new Combo(valueComp, SWT.NONE);
			ComboViewer combo = new ComboViewer(l);
			input.add(combo);
			combo.setContentProvider(new ArrayContentProvider()
			{
				@Override
				public Object[] getElements(Object inputElement)
				{
					if (inputElement instanceof ArrayNode)
					{
						ArrayNode arrayNode = (ArrayNode) inputElement;
						JsonNode[] names = new JsonNode[arrayNode.size()];
						int i = 0;
						for (JsonNode node : arrayNode)
						{
							names[i++] = node;
						}
						return names;
					}
					return super.getElements(inputElement);
				}
			});
			combo.setInput(values);

			combo.setLabelProvider(new LabelProvider()
			{
				@Override
				public String getText(Object element)
				{
					if (element instanceof ObjectNode)
					{
						String name = ((ObjectNode) element).path(NAME).asText();
						if (StringUtil.isEmpty(name))
						{
							return ((ObjectNode) element).asText();
						}
						return name;
					}
					else if (element instanceof JsonNode)
					{
						return ((JsonNode) element).asText();
					}
					return element.toString();
				}
			});
			combo.setSelection(new StructuredSelection(values.get(0)));
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(valueComp);
		}
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == 0)
		{
			Object firstElement = CollectionsUtil.getFirstElement(input);
			if (firstElement instanceof Text)
			{
				userInput = ((Text) firstElement).getText();
			}
			else if (firstElement instanceof Button)
			{
				for (Object b : input)
				{
					if (((Button) b).getSelection())
					{
						userInput = ((Button) b).getText();
						break;
					}
				}
			}
			else if (firstElement instanceof ComboViewer)
			{
				ISelection selection = ((ComboViewer) firstElement).getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection)
				{
					Object selectedElement = ((IStructuredSelection) selection).getFirstElement();
					if (selectedElement instanceof JsonNode)
					{
						userInput = ((JsonNode) selectedElement).path(VALUE).asText();
					}
				}
			}
		}
		super.buttonPressed(buttonId);
	}

	public Object getValue()
	{
		// Wrap the output in quotes.
		return "\"" + userInput + "\"";
	}
}
