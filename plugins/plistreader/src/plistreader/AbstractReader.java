package plistreader;

import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * <p>
 * Title: PlistReader AbstractReader
 * </p>
 * <p>
 * Description: Package to read and write PLIST files on OsX
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007 Gie Spaepen
 * </p>
 * <p>
 * Company: University of Antwerp
 * </p>
 * <p>
 * This abstract class can be used to parse a <code>PLIST</code> file. The main function is hence <code>parse()</code>.
 * That function needs a specified file and a specified handler to function. These can be specified using the
 * constructor(s) or the other setting functions like <code>setFile</code> and <code>setHandler</code>.
 * </p>
 * 
 * @author Gie Spaepen
 * @version 1.0
 */
public abstract class AbstractReader
{

	private InputSource source;

	public AbstractReader()
	{
	}

	public void setSource(InputSource source)
	{
		this.source = source;
	}

	/**
	 * Parse a file using a SAX Parser (<code>org.apache.xerces.parsers.SAXParser</code>) and a specific XML Handler.
	 * 
	 * @return PlistProperties
	 * @throws PlistReaderException
	 * @see plistreader.ReaderXMLHandler
	 * @see org.apache.xerces.parses.SAXParser
	 */
	public PlistProperties parse() throws PlistReaderException
	{

		// Make a new property object
		PlistProperties props = new PlistProperties();
		// Instantiate a new handler
		ReaderXMLHandler handler = new ReaderXMLHandler(props);
		// Declare a reader
		XMLReader reader;

		try
		{
			// Create a reader...
			reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
			// ...set the handler...
			reader.setContentHandler(handler);
			// ...set the entity resolver...
			reader.setEntityResolver(handler);
			// ...parse the file.
			reader.parse(source);
		}
		// Catch exceptions
		catch (SAXException ex)
		{
			ex.printStackTrace();
			throw PlistReaderException.NO_PARSER_AVAILABLE;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			throw PlistReaderException.CANNOT_READ_FILE;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw PlistReaderException.NO_FILE_SPECIFIED;
		}

		return props;
	}

}
