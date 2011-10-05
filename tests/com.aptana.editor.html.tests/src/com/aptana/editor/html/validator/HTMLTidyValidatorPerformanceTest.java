/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.test.performance.PerformanceTestCase;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.validator.ValidationManager;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;

public class HTMLTidyValidatorPerformanceTest extends PerformanceTestCase
{

	public void testValidate() throws Exception
	{
		HTMLTidyValidator validator = new HTMLTidyValidator();
		// read in the file
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.html.tests"),
				Path.fromPortableString("performance/amazon.html"), false);
		String src = IOUtil.read(stream);

		FileService fileService = new FileService(IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());
		ValidationManager manager = (ValidationManager) fileService.getValidationManager();
		File fakeFile = File.createTempFile("amazon", ".html");
		URI path = fakeFile.toURI();

		// Ok now actually validate the thing, the real work
		for (int i = 0; i < 350; i++)
		{
			startMeasuring();
			validator.validate(src, path, manager);
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
