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

	public void testDetectBinaryFileChanges() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks("icons/full/elcl16/deploy_package.png           |  Bin 0 -> 669 bytes");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(0, region.getOffset());
		assertEquals(36, region.getLength());
	}

	public void testIgnoresTruncatedFilepath() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks(".../icons/full/elcl16/deploy_package.png           |  Bin 0 -> 669 bytes");
		assertNotNull(links);
		assertEquals(0, links.length);
	}

	public void testAutoMergingLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("Auto-merging plugins/com.aptana.branding/window1616.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(13, region.getOffset());
		assertEquals(42, region.getLength());
	}

	public void testRemovingLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks("Removing plugins/com.aptana.explorer/icons/full/elcl16/command.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(9, region.getOffset());
		assertEquals(57, region.getLength());
	}

	public void testCreateLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks("create mode 100644 plugins/com.aptana.branding/window3232.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(19, region.getOffset());
		assertEquals(42, region.getLength());
	}

	public void testDeleteLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks("delete mode 100644 plugins/com.aptana.explorer/icons/full/elcl16/command.png");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(19, region.getOffset());
		assertEquals(57, region.getLength());
	}

	public void testRenameLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks(" rename plugins/{com.aptana.webserver.core/src/com/aptana/webserver/core/IURLMapper.java => com.aptana.core/src/com/aptana/core/IURIMapper.java} (75%)");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(8, region.getOffset());
		assertEquals(136, region.getLength());
	}
}
