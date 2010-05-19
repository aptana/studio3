package com.aptana.editor.css.contentassist;

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
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.css.contentassist.CSSContentAssistProcessor.LocationType;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class LocationTests extends TestCase
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
			CSSSourceConfiguration.getDefault().createSubPartitionScanner(),
			new NullSubPartitionScanner(),
			new NullPartitionerSwitchStrategy()
		);
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(
			partitionScanner,
			CSSSourceConfiguration.getDefault().getContentTypes()
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
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LexemeProvider<CSSTokenType> lexemeProvider = processor.createLexemeProvider(document, offset); 
				LocationType location = processor.getCoarseLocationType(lexemeProvider, offset);
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
		CSSContentAssistProcessor processor = new CSSContentAssistProcessor(null);
		
		for (LocationTypeRange range : ranges)
		{
			for (int offset = range.startingOffset; offset <= range.endingOffset; offset++)
			{
				LexemeProvider<CSSTokenType> lexemeProvider = processor.createLexemeProvider(document, offset); 
				LocationType location = processor.getInsideLocationType(lexemeProvider, offset);
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
}
