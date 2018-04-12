package com.aptana.git.ui.hyperlink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.Test;

public class HyperlinkDetectorTest
{
	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testIgnoresTruncatedFilepath() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector
				.detectHyperlinks(".../icons/full/elcl16/deploy_package.png           |  Bin 0 -> 669 bytes");
		assertNotNull(links);
		assertEquals(0, links.length);
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testRenameLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks(
				" rename plugins/{com.aptana.webserver.core/src/com/aptana/webserver/core/IURLMapper.java => com.aptana.core/src/com/aptana/core/IURIMapper.java} (75%)");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(8, region.getOffset());
		assertEquals(136, region.getLength());
	}

	@Test
	public void testRenameWithEmptySideLine() throws Exception
	{
		HyperlinkDetector detector = new HyperlinkDetector();
		IHyperlink[] links = detector.detectHyperlinks(
				" rename plugins/com.aptana.editor.ruby/src/com/aptana/editor/ruby/{ => parsing}/RubyParseState.java (94%)");
		assertNotNull(links);
		assertEquals(1, links.length);
		IRegion region = links[0].getHyperlinkRegion();
		assertEquals(8, region.getOffset());
		assertEquals(91, region.getLength());
		assertEquals("plugins/com.aptana.editor.ruby/src/com/aptana/editor/ruby/parsing/RubyParseState.java",
				links[0].getHyperlinkText());
	}

}
