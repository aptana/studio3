package plistreader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * Title: PlistReader ReaderXMLHandler
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
 * THis class is used by the SAX Parser to, event based, handle the content of the XML file and the DTD file. This class
 * needs a <code>PlistProperties</code> object as input parameter to construct a data tree.
 * </p>
 * <p>
 * <b>Versions</b>
 * </p>
 * <ul>
 * <li>Version 1.0: parsed almost everything except arrays or dictionaries that were nested into arrays.
 * <li>Version 1.1: enhanced error codes to facilitate debugging of <code>PLIST</code> files.
 * <li>Version 1.2: update to parse every nested object possible
 * <li>Version 1.3: Dateformat corrected for <code>date</code> tags and <code>real</code> tags can now also be
 * understood by the handler and will be stored as a <code>Double</code>.
 * </ul>
 * 
 * @author Gie Spaepen
 * @version 1.3
 */
@SuppressWarnings("rawtypes")
public class ReaderXMLHandler extends DefaultHandler
{

	/**
	 * Flag to determine if a key tag just passed by
	 */
	private boolean isKeyLoaded = false;
	/**
	 * Flag to determine if a string tag just passed by
	 */
	private boolean isStringLoaded = false;
	/**
	 * Flag to determine if an integer tag just passed by
	 */
	private boolean isIntegerLoaded = false;
	/**
	 * Flag to determine if a real tag just passed by
	 */
	private boolean isRealLoaded = false;
	/**
	 * Flag to determine if a date tag just passed by
	 */
	private boolean isDateLoaded = false;
	/**
	 * Flag to determine if a data tag just passed by
	 */
	private boolean isDataLoaded = false;
	/**
	 * Main <code>PlistProperties</code> object that acts as a root object
	 */
	private PlistProperties rootObject;
	/**
	 * String to temporally hold a key
	 */
	private String tempKey;
	/**
	 * Vector to hold the different data nodes
	 */
	private Vector storageObjects = new Vector();

	/**
	 * This constructor needs a valid <code>PlistProperties</code> object. Don't use the the following code as
	 * constructor:<br>
	 * <code>ReaderXMLHandler reader = new ReaderXMLHandler(new PlistProperties());</code><br>
	 * If you do so, there is no way to retrieve the data since the internal data storage is <code>private</code>
	 * 
	 * @param _properties
	 *            PlistProperties - The object you want to use afterwards.
	 */
	public ReaderXMLHandler(PlistProperties _properties)
	{
		super();
		rootObject = _properties;
	}

	/**
	 * This function extends the <code>startElement</code> function of the <code>DefaultHandler</code> class. It handles
	 * basically the tagnames of opening tags.<br>
	 * When a key, string, date, data or a integer tag passes by it sets the resp. flag to true in order that the
	 * <code>characters</code> function knows what to do with the content. A boolean is immediately stored since it is
	 * an empty tag. The dict or the array are handled by creating a new level (say node) in the internal data storage
	 * object.
	 * 
	 * @param _URI
	 *            String - The namespace URI
	 * @param _local
	 *            String - The tagname without prefixes (the one we need since all the Plist tags are 'simple' tags.
	 * @param _raw
	 *            String - The tagname with prefixes
	 * @param atts
	 *            Attributes - The attributes attached to the element
	 */
	public void startElement(String _URI, String _local, String _raw, Attributes atts)
	{

		if (_local.equals("key")) //$NON-NLS-1$
		{
			isKeyLoaded = true;
		} // Handle a key node
		else if (_local.equals("string")) //$NON-NLS-1$
		{
			isStringLoaded = true;
		} // Handle a string node
		else if (_local.equals("date")) //$NON-NLS-1$
		{
			isDateLoaded = true;
		} // Handle a date node
		else if (_local.equals("data")) //$NON-NLS-1$
		{
			isDataLoaded = true;
		} // Handle a data node
		else if (_local.equals("integer")) //$NON-NLS-1$
		{
			isIntegerLoaded = true;
		} // Handle an integer node
		else if (_local.equals("real")) //$NON-NLS-1$
		{
			isRealLoaded = true;
		} // Handle a real node
		else if (_local.equals("true")) //$NON-NLS-1$
		{
			setProperty(tempKey, new Boolean(_local));
		} // Handle a true boolean
		else if (_local.equals("false")) //$NON-NLS-1$
		{
			setProperty(tempKey, new Boolean(_local));
		} // Handle a false boolean
		else if (_local.equals("dict")) //$NON-NLS-1$
		{
			levelUp(false);
		} // Handle a dictionary node
		else if (_local.equals("array")) //$NON-NLS-1$
		{
			levelUp(true);
		} // Handle an array
	}

	/**
	 * This function extends the <code>characters</code> function of the <code>DefaultHandler</code> class. It handles
	 * basically the tagnames of opening tags.<br>
	 * This function handles the content of a tag. Since booleans, dictionaries and arrays are already handled by the
	 * <code>startElement</code> function we only need to handle key, string, integer, data and data tags. A key tag is
	 * temporally stored in a string and later on reused to match it's value. The rest is passed to the
	 * <code>setProperty</code> function and converted to the resp. object.
	 * 
	 * @param _chars
	 *            char[]
	 * @param _start
	 *            int
	 * @param _len
	 *            int
	 */
	public void characters(char[] _chars, int _start, int _len)
	{

		// Convert _chars to a normal String object
		String value = new String(_chars, _start, _len);
		value.trim();

		// If a keynode is loaded get its value and store it in the temp object
		if (isKeyLoaded)
		{
			tempKey = value;
		}
		// All the other nodes
		else
		{
			// Handle the different content types by adding the corresponging key
			// value pair to the current object which can be an array or a dictionary
			if (isStringLoaded)
			{
				setProperty(tempKey, new String(value));
			}
			else if (isIntegerLoaded)
			{
				setProperty(tempKey, new Integer(value));
			}
			else if (isRealLoaded)
			{
				setProperty(tempKey, new Double(value));
			}
			else if (isDateLoaded)
			{
				setProperty(tempKey, convertStringToDate(value));
			}
			else if (isDataLoaded)
			{
				setProperty(tempKey, new Byte(value));
			}
		}
	}

	/**
	 * Little helper function to convert a string value to a date object. Generates an error if the date is malformed.
	 * 
	 * @param value
	 *            String
	 * @return Date
	 */
	private Date convertStringToDate(String value)
	{
		try
		{
			return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").parse(value); //$NON-NLS-1$
		}
		catch (ParseException ex)
		{
			PlistReaderException.WRONG_DATE_FORMAT.printStackTrace();
			return new Date();
		}
	}

	/**
	 * This function extends the <code>endElement</code> function of the <code>DefaultHandler</code> class. It handles
	 * basically the tagnames of opening tags.<br>
	 * This function is in fact the inverse function of the <code>startElement
   * </code> function by 1) setting all the flags to false for a
	 * key, string, integer, date or data tag and moving a level down (say node) when encountering a dictionary or an
	 * array.
	 * 
	 * @see plistreader.ReaderXMLHandler#startElement
	 * @param _URI
	 *            String - The namespace URI
	 * @param _local
	 *            String - The tagname without prefix
	 * @param _raw
	 *            String - The tagname with prefix
	 */
	public void endElement(String _URI, String _local, String _raw)
	{
		if (_local.equals("key")) //$NON-NLS-1$
		{
			isKeyLoaded = false;
		} // Handle a key
		else if (_local.equals("string")) //$NON-NLS-1$
		{
			isStringLoaded = false;
		} // Handle a string
		else if (_local.equals("integer")) //$NON-NLS-1$
		{
			isIntegerLoaded = false;
		} // Handle an integer
		else if (_local.equals("real")) //$NON-NLS-1$
		{
			isRealLoaded = false;
		}
		else if (_local.equals("date")) //$NON-NLS-1$
		{
			isDateLoaded = false;
		} // Handle a date
		else if (_local.equals("data")) //$NON-NLS-1$
		{
			isDataLoaded = false;
		} // Handle data
		else if (_local.equals("dict")) //$NON-NLS-1$
		{
			levelDown(false);
		} // Handle a dict
		else if (_local.equals("array")) //$NON-NLS-1$
		{
			levelDown(true);
		} // Handle an array
	}

	/**
	 * Function to implement the <code>org.xml.sax.EntityResolver</code> class. Since the PLIST files generally have a
	 * <code>DOCTYPE</code> tag with an URL in it the (stupid) SAX Parser want's to make an internet connection and
	 * generates a fatal error if it can't. But, the correct DTD file is located also in the JAR file of this library,
	 * so if we meet this tag redirect the parser to the internal DTD file.
	 * 
	 * @param _pubId
	 *            String
	 * @param _sysId
	 *            String
	 * @return InputSource
	 */
	public InputSource resolveEntity(String _pubId, String _sysId)
	{
		// Detect the DOCTYPE line from the plist file
		if (_pubId.equals("-//Apple Computer//DTD PLIST 1.0//EN") || _pubId.equals("-//Apple//DTD PLIST 1.0//EN") //$NON-NLS-1$ //$NON-NLS-2$
				|| _pubId.contains("//DTD PLIST 1.0//EN")) //$NON-NLS-1$
		{
			return new InputSource(getClass().getResourceAsStream("plist.dtd")); //$NON-NLS-1$
		}
		// Pro forma
		return new InputSource(""); //$NON-NLS-1$
	}

	/**
	 * This function handles a key-value pair by adding them to the current and active object: in other words the last
	 * open node in a manner of speaking. This can be an array, so only the value is added to a temporal
	 * <code>Vector</code> holding the array values, or to a <ocde>PlistProperties</code> object.
	 * 
	 * @param _key
	 *            String
	 * @param _value
	 *            Object
	 */
	@SuppressWarnings("unchecked")
	private void setProperty(String _key, Object _value)
	{
		// Get the last element from the data storage
		Object currentObject = storageObjects.elementAt(storageObjects.size() - 1);
		// Handle current arrays
		if (currentObject instanceof Vector)
		{
			((Vector) currentObject).add(_value);
		}
		// Handle current dictionaries
		else if (currentObject instanceof PlistProperties)
		{
			try
			{
				PlistProperties props = (PlistProperties) currentObject;
				if (props.getKeyIndex(_key) == -1) // new property
				{
					props.setProperty(_key, _value);
				}
				else
				{
					if (_value instanceof String) // HACK Fix when string value has square brackets we get it in pieces
					{
						String oldValue = (String) props.getProperty(_key);
						props.setProperty(_key, oldValue + (String) _value);
					}
				}
			}
			catch (PlistReaderException ex)
			{
				ex.printStackTrace();
			}
		}
		// Or go mad
		else
		{
			PlistReaderException.WRONG_ASSIGNMENT.printStackTrace();
		}
	}

	/**
	 * When a a dict or array tag is encountered we have to create a new level or node. When it is an array a
	 * <code>Vector</code> is added to the data storage and when it is a dict a <code>PlistProperties</code> is added
	 * and all the data content is added to those objects. Last: use the parameter to the data storage. The added object
	 * will then act as current object to say whether it is an array or not...
	 * 
	 * @param _array
	 *            boolean - new level is an array level (true) or a dict level (false)
	 */
	@SuppressWarnings("unchecked")
	private void levelUp(boolean _array)
	{
		// Handle arrays
		if (_array)
		{
			// Make a new vector and set the first element as it's key
			Vector currentObject = new Vector();
			currentObject.add(tempKey);
			// Add to data storage
			storageObjects.add(currentObject);
		}
		// Handle dictionaries
		else
		{
			// If the data storage is empty we have to add the root object
			if (storageObjects.isEmpty())
			{
				storageObjects.add(rootObject);
			}
			else
			{
				// Otherwise add a new PlistProperties to the data storage
				PlistProperties currentObject = new PlistProperties();
				currentObject.setPropertiesKey(tempKey);
				storageObjects.add(currentObject);
			}
		}
	}

	/**
	 * When a closing array or dict tag is encountered this function is called to close the current object, say to fold
	 * the last node, and add them to the parent node. Determine if you're closing an array or not.
	 * 
	 * @param _array
	 *            boolean - True for arrays and false for dictionaries.
	 */
	private void levelDown(boolean _array)
	{
		// Determine if there's more than one element
		if (storageObjects.size() > 1)
		{
			// Close an array object
			if (_array)
			{
				try
				{
					// Get the current object...
					Vector currentObject = (Vector) storageObjects.elementAt(storageObjects.size() - 1);
					// ...and its key.
					String currentObjectKey = (String) currentObject.elementAt(0);
					// Remove it from the data storage...
					storageObjects.removeElement(currentObject);
					// ...and the key from the current object...
					currentObject.removeElement(currentObjectKey);
					// ...and add it to the parent node
					setProperty(currentObjectKey, currentObject);
				}
				catch (ClassCastException ex)
				{
					// Malformed XML will cause class cast exceptions
					PlistReaderException.WRONG_NESTED_ARRAY.printStackTrace();
				}
			}
			// Handle dictionaries
			else
			{
				try
				{
					// Get the current object...
					PlistProperties currentObject = (PlistProperties) storageObjects
							.elementAt(storageObjects.size() - 1);
					// ..remove it from the data storage
					storageObjects.removeElement(currentObject);
					// And add it to the parent...
					setProperty(currentObject.getPropertiesKey(), currentObject);
				}
				catch (ClassCastException ex)
				{
					// Malformed XML will cause class cast exceptions
					PlistReaderException.WRONG_NESTED_DICTIONARY.printStackTrace();
				}
			}
		}
	}

}
