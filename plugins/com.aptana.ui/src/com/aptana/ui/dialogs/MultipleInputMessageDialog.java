/**
 * Aptana Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.eclipse.swt.graphics.Point;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;

/**
 * This dialog dynamically builds UI based on the a array of questions, and prompt the user for their response. This
 * will keep track the response for each question in the UI and then form a JSON object as a response.
 *
 * @author pinnamuri
 */
public class MultipleInputMessageDialog extends InputMessageDialog
{

	private static final int MIN_MESSAGE_WIDTH = 250;

	/*
	 * This will keep track of all user responses for each question in UI.
	 */
	private ObjectNode userInput;

	private ObjectNode response;
	private JsonNode questionsNode;

	public MultipleInputMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
			String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex)
	{
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
	}

	public MultipleInputMessageDialog(JsonNode questionNode, String dialogTitle, String dialogMessage)
	{
		super(UIUtils.getDisplay().getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.CONFIRM,
				new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
		this.questionsNode = questionNode;
		userInput = JsonNodeFactory.instance.objectNode();
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

	@Override
	protected Control createMessageArea(Composite composite)
	{
		Control control = super.createMessageArea(composite);
		if (message != null)
		{
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
					.hint(convertHorizontalDLUsToPixels(MIN_MESSAGE_WIDTH), SWT.DEFAULT).applyTo(messageLabel);
		}

		return control;
	}

	private void createInput(Composite composite)
	{
		for (JsonNode question : questionsNode)
		{
			List<Object> input = new ArrayList<Object>(1);

			new Label(composite, SWT.NONE);
			Composite parent = new Composite(composite, SWT.NONE);
			parent.setLayout(GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).equalWidth(false).create());
			Label label = new Label(parent, SWT.NONE);
			String lblTxt = question.path(MESSAGE).asText();
			label.setText(lblTxt);
			Point requiredSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int minSize = convertHorizontalDLUsToPixels(80);
			if (requiredSize.x > minSize)
			{
				minSize = requiredSize.x;
			}
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).hint(minSize, SWT.DEFAULT)
					.applyTo(label);

			Composite valueComp = new Composite(parent, SWT.NONE);
			valueComp.setLayout(new FillLayout(SWT.HORIZONTAL));
			JsonNode choices = question.path(CHOICES);
			ArrayNode values = JsonNodeFactory.instance.arrayNode();
			if (!choices.isArray())
			{
				values.add(choices);
			}
			else
			{
				values = (ArrayNode) choices;
			}
			String inputType = question.path(TYPE).asText();
			String responseKey = question.path(NAME).asText();

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
			else if (CONFIRMATION.equals(inputType))
			{
				input.add(Boolean.TRUE);
				setButtonLabels(new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL });//TISTUD-7408
			}

			userInput.putPOJO(responseKey, input);

		}
	}

	@Override
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == 0)
		{
			response = JsonNodeFactory.instance.objectNode();

			Iterator<String> iterator = userInput.fieldNames();
			while (iterator.hasNext())
			{
				String fieldName = iterator.next();
				JsonNode value = userInput.path(fieldName);
				if (!(value instanceof POJONode))
				{
					// something bad. FIXME we should recover from here.
					return;
				}
				Object controls = ((POJONode) value).getPojo();
				if (!(controls instanceof List<?>))
				{
					return;
				}
				List<Object> input = (List<Object>) controls;

				Object firstElement = CollectionsUtil.getFirstElement(input);
				if (firstElement instanceof Boolean)
				{
					response.put(fieldName, (Boolean) firstElement);
				}
				else if (firstElement instanceof Text)
				{
					response.put(fieldName, ((Text) firstElement).getText());

				}
				else if (firstElement instanceof Button)
				{
					for (Object b : input)
					{
						if (((Button) b).getSelection())
						{
							response.put(fieldName, ((Button) b).getText());
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
							response.put(fieldName, ((JsonNode) selectedElement).path(VALUE).asText());
						}
					}
				}

			}

		}
		super.buttonPressed(buttonId);
	}

	@Override
	public Object getValue()
	{
		return response;
	}

}
