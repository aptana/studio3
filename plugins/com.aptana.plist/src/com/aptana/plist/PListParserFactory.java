package com.aptana.plist;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.Path;

import ch.randelshofer.quaqua.util.BinaryPListParser;

import com.aptana.core.util.ProcessUtil;

public class PListParserFactory
{

	public static Map<String, Object> parse(File file) throws IOException
	{
		return getParser(file).parse(file);
	}

	public static IPListParser getParser(File file)
	{
		// FIXME We need to determine if the file is XML or binary and delegate to the correct impl!
		try
		{
			Process p = ProcessUtil.run("/usr/bin/plutil", Path.fromOSString(file.getParent()), "-convert", "binary1",
					file.getName());
			int exitCode = p.waitFor();
			if (exitCode != 0)
			{
				// Not necessarily an error, it may already be XML
				// System.err.println("Bad exit code for conversion: " + exitCode);
			}
			return new BinaryPListParser();
		}
		catch (Exception e)
		{
			System.err.println("An error occurred processing: " + file.getAbsolutePath());
		}
		return null;
	}
}
