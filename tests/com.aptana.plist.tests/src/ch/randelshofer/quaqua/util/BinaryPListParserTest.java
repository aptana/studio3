package ch.randelshofer.quaqua.util;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class BinaryPListParserTest extends TestCase
{

	public void testParseExample() throws Exception
	{
		BinaryPListParser plistParser = new BinaryPListParser();
		URL url = FileLocator.find(Platform.getBundle("com.aptana.plist.tests"),
				Path.fromPortableString("example.plist"), null);
		url = FileLocator.toFileURL(url);

		Map<String, Object> result = plistParser.parse(new File(url.toURI()));

		assertNotNull(result);
		assertEquals(5, result.size());
		assertEquals(123L, ((Long) result.get("my_number")).longValue());
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.YEAR, 2010);
		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 23);
		cal.set(Calendar.HOUR_OF_DAY, 14);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 51);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), result.get("my_date"));
		assertEquals("Hello world!", result.get("my_string"));
		assertTrue((Boolean) result.get("my_boolean"));
		List<Object> array = (List<Object>) result.get("my_array");
		assertEquals("array value 1", array.get(0));
		Map<String, Object> dict = (Map<String, Object>) array.get(1);
		assertEquals(1, dict.size());
		assertFalse((Boolean) dict.get("my_false_boolean"));
	}
}
