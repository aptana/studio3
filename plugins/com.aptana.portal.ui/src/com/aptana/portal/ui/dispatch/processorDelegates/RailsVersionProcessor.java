package com.aptana.portal.ui.dispatch.processorDelegates;

/**
 * A rails version processor that can get the current rails version in the system
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RailsVersionProcessor extends BaseVersionProcessor
{
	private static final String RAILS = "rails"; //$NON-NLS-1$

	/**
	 * @return "rails"
	 */
	@Override
	public String getSupportedApplication()
	{
		return RAILS;
	}
}
