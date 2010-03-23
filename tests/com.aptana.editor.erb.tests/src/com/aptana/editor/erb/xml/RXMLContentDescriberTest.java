package com.aptana.editor.erb.xml;

import com.aptana.editor.erb.common.ERBContentDescriber;
import com.aptana.editor.erb.common.ERBContentDescriberTestCase;

public class RXMLContentDescriberTest extends ERBContentDescriberTestCase
{

	@Override
	protected ERBContentDescriber createDescriber()
	{
		return new RXMLContentDescriber();
	}

}
