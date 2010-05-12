package com.aptana.editor.html.contentassist;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import com.aptana.editor.common.contentassist.LexemeProvider;
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
	
	private Mockery context;
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		context = new Mockery();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		context = null;
	}
	
	/**
	 * createDocument
	 * 
	 * @param partitionType
	 * @param source
	 * @return
	 */
	protected IDocument createDocument(String partitionType, final String source)
	{
		final IDocument document = context.mock(IDocument.class);
		final ITypedRegion partition = this.createPartition(partitionType, source.length());
		
		context.checking(new Expectations() {
			{
				try
				{
					allowing(document).getPartition(with(any(Integer.class)));
					will(returnValue(partition));
					
					allowing(document).get(with(any(Integer.class)), with(any(Integer.class)));
					will(new Action() {
						@Override
						public void describeTo(Description description)
						{
							description.appendText("returns a substring of <source>");
						}

						@Override
						public Object invoke(Invocation invocation) throws Throwable
						{
							Integer offset = (Integer) invocation.getParameter(0);
							Integer length = (Integer) invocation.getParameter(1);
							
							return source.substring(offset, offset + length);
						}
					});
					
					allowing(document).getLength();
					will(returnValue(source.length()));
					
					allowing(document).getLegalLineDelimiters();
					will(returnValue(DefaultLineTracker.DELIMITERS));
					
					for (int i = 0; i < source.length(); i++)
					{
						allowing(document).getChar(i);
						will(returnValue(source.charAt(i)));
					}
				}
				catch (BadLocationException e)
				{
				}
			}
		});
		
		return document;
	}
	
	/**
	 * createPartition
	 * 
	 * @param partitionType
	 * @return
	 */
	protected ITypedRegion createPartition(final String partitionType, final int length)
	{
		final ITypedRegion partition = context.mock(ITypedRegion.class);
		
		context.checking(new Expectations() {
			{
				allowing(partition).getType();
				will(returnValue(partitionType));
				
				allowing(partition).getOffset();
				will(returnValue(0));
				
				allowing(partition).getLength();
				will(returnValue(length));
			}
		});
		
		return partition;
	}
	
	/**
	 * openTagTests
	 * 
	 * @param source
	 */
	protected void openTagTests(String source)
	{
		this.tagTests(
			source,
			new RangeWithLocation(0, 1, Location.IN_TEXT),
			new RangeWithLocation(1, source.length(), Location.IN_OPEN_TAG)
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
		String partitionType = HTMLSourceConfiguration.HTML_TAG;
		IDocument document = this.createDocument(partitionType, source);
		HTMLContentAssistProcessor processor = new HTMLContentAssistProcessor(null);
		
		for (RangeWithLocation range : ranges)
		{
			// 0 is IN_TEXT
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
		this.openTagTests("<>");
	}
	
	/**
	 * testOpenTagElementName
	 */
	public void testOpenTagElementName()
	{
		this.openTagTests("<body>");
	}
	
	/**
	 * testOpenScriptElement
	 */
	public void testOpenScriptElement()
	{
		this.openTagTests("<script>");
	}
	
	/**
	 * testOpenStyleElement
	 */
	public void testOpenStyleElement()
	{
		this.openTagTests("<style>");
	}
	
	/**
	 * testOpenTagWithClassAttribute
	 */
	public void testOpenTagWithClassAttribute()
	{
		this.openTagTests("<body class=\"testing\">");
	}
	
	/**
	 * testOpenTagWithIDAttribute
	 */
	public void testOpenTagWithIDAttribute()
	{
		this.openTagTests("<body id=\"testing\">");
	}
}
