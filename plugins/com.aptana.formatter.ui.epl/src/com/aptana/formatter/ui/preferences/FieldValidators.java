/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;

import com.aptana.formatter.preferences.IFieldValidator;
import com.aptana.formatter.ui.util.StatusInfo;

public final class FieldValidators
{
	// Available validators
	public static final IFieldValidator POSITIVE_NUMBER_VALIDATOR = new PositiveNumberValidator();
	public static final IFieldValidator PORT_VALIDATOR = new PortValidator();
	public static final IFieldValidator EMPTY_TEXT_VALIDATOR = new EmptyTextValidator();

	public static class PositiveNumberValidator implements IFieldValidator
	{
		public IStatus validate(String text)
		{
			StatusInfo status = new StatusInfo();

			if (text.trim().length() == 0)
			{
				status.setError(Messages.PositiveNumberIsEmpty);
			}
			else
			{
				try
				{
					int value = Integer.parseInt(text);
					if (value < 0)
					{
						status.setError(MessageFormat.format(Messages.PositiveNumberIsInvalid, new Object[] { text }));
					}
				}
				catch (NumberFormatException e)
				{
					status.setError(MessageFormat.format(Messages.PositiveNumberIsInvalid, new Object[] { text }));
				}
			}

			return status;
		}
	}

	public static class MinimumNumberValidator extends PositiveNumberValidator
	{
		private int minValue;

		public MinimumNumberValidator(int minValue)
		{
			this.minValue = minValue;
		}

		public IStatus validate(String text)
		{
			StatusInfo status = (StatusInfo) super.validate(text);

			if (!status.isOK())
			{
				return status;
			}

			int value = Integer.parseInt(text);
			if (value < minValue)
			{
				status.setError(MessageFormat.format(Messages.MinValueInvalid,
						new Object[] { String.valueOf(minValue) }));
			}

			return status;
		}
	}

	public static class PortValidator implements IFieldValidator
	{
		public IStatus validate(String text)
		{
			StatusInfo status = new StatusInfo();

			if (text.trim().length() == 0)
			{
				status.setError(Messages.PortIsEmpty);
			}
			else
			{
				try
				{
					int value = Integer.parseInt(text);
					if (value < 1000 || value > 65535)
					{
						status.setError(MessageFormat.format(Messages.PortShouldBeInRange, new Object[] { text }));
					}
				}
				catch (NumberFormatException e)
				{
					status.setError(MessageFormat.format(Messages.PortShouldBeInRange, new Object[] { text }));
				}
			}

			return status;
		}
	}

	public static class EmptyTextValidator implements IFieldValidator
	{
		public IStatus validate(String text)
		{
			StatusInfo status = new StatusInfo();
			if (text.trim().length() == 0)
			{
				status.setError(Messages.FieldIsEmpty);
			}
			return status;
		}
	}
}
