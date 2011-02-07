package com.aptana.git.ui.hyperlink;

import junit.framework.TestCase;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class HyperlinkDetectorTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testDetectFileWithLinesAdded() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("railties/test/application/rake_test.rb             |   17 ++");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(38, region.getLength());
	}
	
	public void testDetectFileWithLinesRemoved() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("railties/test/application/rake_test.rb             |    2 --");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(38, region.getLength());
	}
	
	public void testDetectFileWithLinesAddedAndRemoved() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("railties/test/application/rake_test.rb             |    2 +-");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(38, region.getLength());
	}

}
