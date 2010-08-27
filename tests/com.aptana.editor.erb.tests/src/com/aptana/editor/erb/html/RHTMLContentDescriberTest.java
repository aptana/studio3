package com.aptana.editor.erb.html;

import com.aptana.editor.erb.common.ERBContentDescriber;
import com.aptana.editor.erb.common.ERBContentDescriberTestCase;

public class RHTMLContentDescriberTest extends ERBContentDescriberTestCase
{

	@Override
	protected ERBContentDescriber createDescriber()
	{
		return new RHTMLContentDescriber();
	}

}
