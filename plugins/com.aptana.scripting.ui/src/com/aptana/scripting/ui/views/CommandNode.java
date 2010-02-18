package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.ui.ScriptingUIPlugin;

public class CommandNode extends BaseNode
{
	private static final Image COMMAND_ICON = ScriptingUIPlugin.getImage("icons/command.png"); //$NON-NLS-1$
	
	private static final String BUNDLE_COMMAND_NAME = "bundle.command.name";
	private static final String BUNDLE_COMMAND_PATH = "bundle.command.path";
	private static final String BUNDLE_COMMAND_INPUTS = "bundle.command.inputs";
	private static final String BUNDLE_COMMAND_OUTPUT = "bundle.command.output";
	private static final String BUNDLE_COMMAND_TRIGGERS = "bundle.command.triggers";

	private CommandElement _command;

	/**
	 * CommandNode
	 * 
	 * @param command
	 */
	public CommandNode(CommandElement command)
	{
		this._command = command;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getImage()
	 */
	public Image getImage()
	{
		return COMMAND_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return this._command.getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(BUNDLE_COMMAND_NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(BUNDLE_COMMAND_PATH, "Path");
		PropertyDescriptor inputsProperty = new PropertyDescriptor(BUNDLE_COMMAND_INPUTS, "Inputs");
		PropertyDescriptor outputProperty = new PropertyDescriptor(BUNDLE_COMMAND_OUTPUT, "Output");
		PropertyDescriptor triggersProperty = new PropertyDescriptor(BUNDLE_COMMAND_TRIGGERS, "Triggers");

		return new IPropertyDescriptor[] { nameProperty, pathProperty, inputsProperty, outputProperty, triggersProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id.equals(BUNDLE_COMMAND_NAME))
		{
			result = this._command.getDisplayName();
		}
		else if (id.equals(BUNDLE_COMMAND_PATH))
		{
			result = this._command.getPath();
		}
		else if (id.equals(BUNDLE_COMMAND_INPUTS))
		{
			InputType[] inputs = this._command.getInputTypes();
			StringBuilder buffer = new StringBuilder();

			for (int i = 0; i < inputs.length; i++)
			{
				if (i > 0)
					buffer.append(", ");

				buffer.append(inputs[i].getName());
			}

			result = buffer.toString();
		}
		else if (id.equals(BUNDLE_COMMAND_OUTPUT))
		{
			result = this._command.getOutputType();
		}
		else if (id.equals(BUNDLE_COMMAND_TRIGGERS))
		{
			String[] triggers = this._command.getTriggers();

			if (triggers != null)
			{
				StringBuilder buffer = new StringBuilder();

				for (int i = 0; i < triggers.length; i++)
				{
					if (i > 0)
						buffer.append(", ");

					buffer.append(triggers[i]);
				}

				result = buffer.toString();
			}
		}

		return result;
	}
}
