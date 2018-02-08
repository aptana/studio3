/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.jruby.Ruby;
import org.jruby.RubyFile;
import org.jruby.RubyInstanceConfig;
import org.jruby.RubyString;
import org.jruby.util.JRubyFile;
import org.junit.Test;

/**
 * @author Fabio
 */
public class UnicodeCharsJRubyTest
{

	@Test
	public void testUnicodeChars() throws Exception
	{
		File file = new File("unicodeáéíóú");
		if (file.exists())
		{
			if (!file.delete())
			{
				fail("Unable to delete file: " + file);
			}
		}
		try
		{
			if (!file.mkdirs())
			{
				fail("Unable to create directory: " + file);
			}
			Ruby runtime;
			RubyInstanceConfig.nativeEnabled = false;
			runtime = Ruby.newInstance();

			JRubyFile rubyFile = RubyFile.file(RubyString.newString(runtime, file.getAbsolutePath()));
			assertTrue(rubyFile.exists());
			assertTrue(file.exists());
			assertTrue(file.isDirectory());
			try
			{
				assertTrue(runtime.getPosix().stat(rubyFile.getAbsolutePath()).isDirectory());
			}
			catch (Exception e)
			{
				throw new RuntimeException("Expecting posix layer to work properly", e);
			}
		}
		finally
		{
			if (file.exists())
			{
				file.delete();
			}
		}
	}

}
