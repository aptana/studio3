package com.aptana.formatter.ui.internal;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;

import com.aptana.ui.preferences.IFieldValidator;
import com.aptana.ui.util.StatusInfo;

public final class FieldValidators
{
	public static class PositiveNumberValidator implements IFieldValidator
	{
		public IStatus validate(String text)
		{
			StatusInfo status = new StatusInfo();

			if (text.trim().length() == 0)
			{
				status.setError(ValidatorMessages.PositiveNumberIsEmpty);
			}
			else
			{
				try
				{
					int value = Integer.parseInt(text);
					if (value < 0)
					{
						status.setError(MessageFormat.format(ValidatorMessages.PositiveNumberIsInvalid,
								new Object[] { text }));
					}
				}
				catch (NumberFormatException e)
				{
					status.setError(MessageFormat.format(ValidatorMessages.PositiveNumberIsInvalid,
							new Object[] { text }));
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
				status.setError(MessageFormat.format(ValidatorMessages.MinValueInvalid, new Object[] { String
						.valueOf(minValue) }));
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
				status.setError(ValidatorMessages.PortIsEmpty);
			}
			else
			{
				try
				{
					int value = Integer.parseInt(text);
					if (value < 1000 || value > 65535)
					{
						status.setError(MessageFormat.format(ValidatorMessages.PortShouldBeInRange,
								new Object[] { text }));
					}
				}
				catch (NumberFormatException e)
				{
					status.setError(MessageFormat.format(ValidatorMessages.PortShouldBeInRange, new Object[] { text }));
				}
			}

			return status;
		}
	}

	// Available validators
	public static IFieldValidator POSITIVE_NUMBER_VALIDATOR = new PositiveNumberValidator();
	public static IFieldValidator PORT_VALIDATOR = new PortValidator();
}
