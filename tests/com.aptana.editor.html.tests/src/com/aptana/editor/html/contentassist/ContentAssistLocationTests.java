package com.aptana.editor.html.contentassist;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.IExtendedPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.Location;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public class ContentAssistLocationTests extends TestCase
{
	private static class RangeWithLocation
	{
		public final int startingOffset;
		public final int endingOffset;
		public final Location location;
		
		public RangeWithLocation(int startingOffset, int endingOffset, Location location)
		{
			this.startingOffset = startingOffset;
			this.endingOffset = endingOffset;
			this.location = location;
		}
	}
	
	/**
	 * createDocument
	 * 
	 * @param partitionType
	 * @param source
	 * @return
	 */
	protected IDocument createDocument(String source)
	{
		CompositePartitionScanner partitionScanner = new CompositePartitionScanner(
			HTMLSourceConfiguration.getDefault().createSubPartitionScanner(),
			new NullSubPartitionScanner(),
			new NullPartitionerSwitchStrategy()
		);
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(
			partitionScanner,
			HTMLSourceConfiguration.getDefault().getContentTypes()
		);
		partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
		
		final IDocument document = new Document(source);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		
		return document;
	}
	
	/**
	 * openTagTests
	 * 
	 * @param source
	 */
	protected void tagTests(String source, Location mainLocation)
	{
		// this ends with Eclipse's default partition
		this.tagTests(
			source,
			new RangeWithLocation(0, 1, Location.IN_TEXT),
			new RangeWithLocation(1, source.length(), mainLocation),
			new RangeWithLocation(source.length(), source.length() + 1, Location.IN_TEXT)
		);
		
		// this ends with one of our language's default partitions
		source += "\n";
		
		this.tagTests(
			source,
			new RangeWithLocation(0, 1, Location.IN_TEXT),
			new RangeWithLocation(1, source.length() - 1, mainLocation),
			new RangeWithLocation(source.length() - 1, source.length(), Location.IN_TEXT)
		);
	}
	
	/**
	 * tagTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void tagTests(String source, RangeWithLocation ... ranges)
	{
		IDocument document = this.createDocument(source);
		HTMLContentAssistProcessor processor = new HTMLContentAssistProcessor(null);
		
		for (RangeWithLocation range : ranges)
		{
			for (int offset = range.startingOffset; offset < range.endingOffset; offset++)
			{
				LexemeProvider<HTMLTokenType> lexemeProvider = processor.createLexemeProvider(document, offset); 
				Location location = processor.getLocation(document, lexemeProvider, offset);
				String message = MessageFormat.format(
					"Expected {0} at location {1} of ''{2}''",
					range.location.toString(),
					Integer.toString(offset),
					source
				);
				assertEquals(message, range.location, location);
			}
		}
	}
	
	/**
	 * testOpenTagNoElementName
	 */
	public void testOpenTagNoElementName()
	{
		this.tagTests("<>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenTagElementName
	 */
	public void testOpenTagElementName()
	{
		this.tagTests("<body>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenScriptElement
	 */
	public void testOpenScriptElement()
	{
		this.tagTests("<script>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenStyleElement
	 */
	public void testOpenStyleElement()
	{
		this.tagTests("<style>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenTagWithClassAttribute
	 */
	public void testOpenTagWithClassAttribute()
	{
		this.tagTests("<body class=\"testing\">", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenTagWithIDAttribute
	 */
	public void testOpenTagWithIDAttribute()
	{
		this.tagTests("<body id=\"testing\">", Location.IN_OPEN_TAG);
	}

	/**
	 * testSelfClosingTag
	 */
	public void testSelfClosingTag()
	{
		this.tagTests("<body/>", Location.IN_OPEN_TAG);
	}
	
	/**
	 * testOpenAndCloseTag
	 */
	public void testCloseTag()
	{
		this.tagTests("</body>", Location.IN_CLOSE_TAG);
	}
}
