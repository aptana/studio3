package com.aptana.plist;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface IPListParser
{
	public Map<String, Object> parse(File file) throws IOException;
}
