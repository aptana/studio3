package com.aptana.scripting.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CommandTests
{
	@Test
	public void invokeStringCommand()
	{
		Command command = new Command("/");

		command.setInvoke("echo hello");

		CommandResult result = command.execute(null);
		String resultText = result.getResultText();
		
		assertEquals("hello\n", resultText);
	}

	@Test
	public void invokeBlockCommand()
	{
	}
}
