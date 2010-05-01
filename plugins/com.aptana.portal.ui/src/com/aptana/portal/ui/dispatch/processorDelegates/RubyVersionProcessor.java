package com.aptana.portal.ui.dispatch.processorDelegates;

/**
 * A Ruby version processor that can get the current Ruby version in the system
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RubyVersionProcessor extends BaseVersionProcessor
{
	private static final String RUBY = "ruby"; //$NON-NLS-1$

	/**
	 * @return "ruby"
	 */
	@Override
	public String getSupportedApplication()
	{
		return RUBY;
	}
}
