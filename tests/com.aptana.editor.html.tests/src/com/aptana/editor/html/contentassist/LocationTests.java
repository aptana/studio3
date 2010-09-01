package com.aptana.editor.html.contentassist;

import java.text.MessageFormat;

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
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessor.LocationType;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

import junit.framework.TestCase;

public abstract class LocationTests extends TestCase
{
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
	 * coarseLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void coarseLocationTests(String source, LocationTypeRange ... ranges)
	{
		IDocument document = this.createDocument(source);
		HTMLContentAssistProcessor processor = new HTMLContentAssistProcessor(null);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LexemeProvider<HTMLTokenType> lexemeProvider = processor.createLexemeProvider(document, offset); 
				LocationType LocationType = processor.getCoarseLocationType(document, lexemeProvider, offset);
				String message = MessageFormat.format(
					"Expected {0} at LocationType {1} of ''{2}''",
					range.LocationType.toString(),
					Integer.toString(offset),
					source
				);
				assertEquals(message, range.LocationType, LocationType);
			}
		}
	}
	
	/**
	 * fineLocationTests
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param expectedLocation
	 */
	protected void fineLocationTests(String source, LocationTypeRange ... ranges)
	{
		IDocument document = this.createDocument(source);
		HTMLContentAssistProcessor processor = new HTMLContentAssistProcessor(null);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LexemeProvider<HTMLTokenType> lexemeProvider = processor.createLexemeProvider(document, offset); 
				LocationType LocationType = processor.getOpenTagLocationType(lexemeProvider, offset);
				String message = MessageFormat.format(
					"Expected {0} at LocationType {1} of ''{2}''",
					range.LocationType.toString(),
					Integer.toString(offset),
					source
				);
				assertEquals(message, range.LocationType, LocationType);
			}
		}
	}
}
