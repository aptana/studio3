package ch.randelshofer.quaqua.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.plist.tests.AbstractPlistParserTestCase;

public class BinaryPListParserTest extends AbstractPlistParserTestCase
{

	@Override
	protected IPath getExampleFilePath()
	{
		return Path.fromPortableString("plists/example.plist");
	}

}
