package com.aptana.editor.html.formatting;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.MalformedTreeException;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import com.aptana.core.util.WriterOutputStream;
import com.aptana.editor.html.Activator;

/**
 * Code formatting strategy for HTML.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormattingStrategy extends ContextBasedFormattingStrategy
{
	/** Documents to be formatted by this strategy */
	private final LinkedList<IDocument> documents = new LinkedList<IDocument>();
	/** Partitions to be formatted by this strategy */
	private final LinkedList<TypedPosition> partitions = new LinkedList<TypedPosition>();
	private Properties formatterProperties;

	@SuppressWarnings( { "unchecked", "rawtypes" })
	public void format()
	{
		super.format();

		final IDocument document = (IDocument) documents.removeFirst();
		final TypedPosition partition = (TypedPosition) partitions.removeFirst();

		if (document != null && partition != null)
		{
			Map partitioners = null;
			try
			{
				Tidy tidy = new Tidy();
				tidy.setConfigurationFromProps(formatterProperties);
				String content = document.get(partition.offset, partition.length);
				StringReader reader = new StringReader(content);
				Document dom = tidy.parseDOM(reader, null);
				StringWriter writer = new StringWriter(content.length());
				// TODO: Encoding issues might occur here
				tidy.pprint(dom, new WriterOutputStream(writer));
				partitioners = TextUtilities.removeDocumentPartitioners(document);
				document.replace(partition.offset, partition.length, writer.getBuffer().toString());
			}
			catch (MalformedTreeException e)
			{
				Activator.logError("Error while formatting the HTML content", e); //$NON-NLS-1$
			}
			catch (BadLocationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				if (partitioners != null)
					TextUtilities.addDocumentPartitioners(document, partitioners);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStarts(org.eclipse.jface.text.formatter
	 * .IFormattingContext)
	 */
	public void formatterStarts(final IFormattingContext context)
	{
		super.formatterStarts(context);
		partitions.addLast((TypedPosition) context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
		documents.addLast((IDocument) context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));
		// Load the properties
		formatterProperties = new Properties();
		try
		{
			Bundle bundle = Activator.getDefault().getBundle();
			Enumeration entries = bundle.findEntries("", "html_temp_formatting.properties", false);
			if (entries.hasMoreElements())
			{
				URL url = (URL) entries.nextElement();
				formatterProperties.load(new InputStreamReader(url.openStream()));
			}
			
		}
		catch (Exception e)
		{
			Activator.logError("Error loading the HTML formatter properties", e); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy#formatterStops()
	 */
	public void formatterStops()
	{
		super.formatterStops();
		partitions.clear();
		documents.clear();
	}
}
