/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.core.tests.GlobalTimePerformanceTestCase;
import com.aptana.core.util.IOUtil;
import com.aptana.editor.epl.tests.EditorTestHelper;

public class CSSCodeScannerPerformanceTest extends GlobalTimePerformanceTestCase
{

	private ITokenScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fScanner = new CSSCodeScannerFlex();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fScanner = null;
		super.tearDown();
	}

	public void testScanningFromMetadata() throws Exception
	{
		perfScan("from-metadata.css", 10);
	}

	public void testGithubFormatted() throws Exception
	{
		perfScan("github-formatted.css", 20);
	}

	public void testGithubMinimized() throws Exception
	{
		perfScan("github-minimized.css", 20);
	}

	public void testScanningWordpressAdminCSS() throws Exception
	{
		perfScan("wp-admin.css", 80);
	}

	public void testScanningWordpressAdminDevCSS() throws Exception
	{
		perfScan("wp-admin.dev.css", 30);
	}

	public void testScanningYuiCSS() throws Exception
	{
		perfScan("yui.css", 80);
	}

	protected void perfScan(String fileName, int iterations) throws IOException, CoreException
	{
		IDocument document = createDocument(fileName);
		perfScan(document, iterations, 0, document.getLength());
	}

	protected void perfScan(IDocument document, int iterations, int offset, int len) throws IOException, CoreException
	{
		EditorTestHelper.joinBackgroundActivities();
		// Ok now actually scan the thing, the real work
		for (int i = 0; i < iterations; i++)
		{
			startMeasuring();
			fScanner.setRange(document, offset, len);
			while (fScanner.nextToken() != Token.EOF)
			{
				fScanner.getTokenOffset();
				fScanner.getTokenLength();
			}
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	protected IDocument createDocument(String fileName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.editor.css.tests"),
				Path.fromPortableString("performance/" + fileName), false);
		return new Document(IOUtil.read(stream));
	}
}
