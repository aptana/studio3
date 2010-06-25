package plistreader;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * <p>
 * Title: PlistReader PlistProperties
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
 * When you read a <code>PLIST</code> file you'll work afterwards with this class. This object holds all the data
 * structured as key - value pairs. Some functions show similarity with the <code>java.util.Properties</code> class but
 * this class doesn't inherit from that class for the simple reason that a Properties class extends a
 * <code>java.util.Hashtable</code> and that's not flexible enough.
 * </p>
 * <p>
 * The keys are always strings and the values can be instance of the following classes (all non-primitive):
 * </p>
 * <p>
 * <ul>
 * <li>String
 * <li>Integer
 * <li>Double
 * <li>Date
 * <li>Boolean
 * <li>Byte
 * <li>PlistProperties
 * <li>Vector (which can contain the6 other classes mentioned)
 * </ul>
 * </p>
 * <p>
 * All of these classes are <i>translations</i> of the XML code:
 * </p>
 * <ul>
 * <li>string tag: String
 * <li>integer tag: Integer
 * <li>real tag: Double
 * <li>date tag: Date
 * <li>true or false flag: Boolean
 * <li>data tag: Byte
 * <li>dict tag: PlistProperties
 * <li>array tag: Vector
 * </ul>
 * <p>
 * Scroll on to the functions for the usage
 * </p>
 * 
 * @author Gie Spaepen
 * @version 1.2
 */
@SuppressWarnings("rawtypes")
public class PlistProperties
{

	/**
	 * Stores all the key values
	 */
	private Vector<String> internalKeys = new Vector<String>();

	/**
	 * Stores all the values of the keys
	 */
	private Vector<Object> internalValues = new Vector<Object>();

	/**
	 * Stores the key of this object itselves
	 */
	private String key;

	/**
	 * Class object for a String
	 */
	public final static Class<? extends Class> TYPE_STRING = String.class.getClass();

	/**
	 * Class object for an Integer
	 */
	public final static Class<? extends Class> TYPE_INTEGER = Integer.class.getClass();

	/**
	 * Class object for a Double
	 */
	public final static Class<? extends Class> TYPE_DOUBLE = Double.class.getClass();

	/**
	 * Class object for a Date
	 */
	public final static Class<? extends Class> TYPE_DATE = Date.class.getClass();

	/**
	 * Class object for a Byte[]
	 */
	public final static Class<? extends Class> TYPE_DATA = Byte.class.getClass();

	/**
	 * Class object for a Boolean
	 */
	public final static Class<? extends Class> TYPE_BOOLEAN = Boolean.class.getClass();

	/**
	 * Class object for a Vector (follows <code>PLIST</code> syntax)
	 */
	public final static Class<? extends Class> TYPE_ARRAY = Vector.class.getClass();

	/**
	 * Alias for a <code>TYPE_ARRAY</code>
	 * 
	 * @see #TYPE_VECTOR
	 */
	public final static Class<? extends Class> TYPE_VECTOR = Vector.class.getClass();

	/**
	 * Class object for a PlistProperties
	 */
	public final static Class<? extends Class> TYPE_PLISTPROPERTIES = PlistProperties.class.getClass();

	/**
	 * Alias for a <code>TYPE_PLISTPROPERTIES</code> (follows <code>PLIST</code> syntax)
	 * 
	 * @see #TYPE_PLISTPROPERTIES
	 */
	public final static Class<? extends Class> TYPE_DICTIONARY = PlistProperties.class.getClass();

	/**
	 * Empty constructor
	 */
	public PlistProperties()
	{
	}

	/**
	 * Construct a <code>PlistProperties</code> object with a specific key
	 * 
	 * @param _key
	 *            String
	 */
	public PlistProperties(String _key)
	{
		key = _key;
	}

	/**
	 * Search a property value for a given key. The function returns null when nothing is found.
	 * 
	 * @param _key
	 *            String
	 * @return Object
	 */
	public Object getProperty(String _key)
	{
		Object _return = null;
		for (int i = 0; i < internalKeys.size(); i++)
		{
			if (internalKeys.elementAt(i).equals(_key))
			{
				_return = internalValues.elementAt(i);
				break;
			}
		}
		return _return;
	}

	/**
	 * Get the index of a key. When nothing is found the function returns -1.
	 * 
	 * @param _key
	 *            String
	 * @return int
	 */
	public int getKeyIndex(String _key)
	{
		int _index = -1;
		for (int i = 0; i < internalKeys.size(); i++)
		{
			if (internalKeys.elementAt(i).equals(_key))
			{
				_index = i;
			}
		}
		return _index;
	}

	/**
	 * Search a property value for a given key. When this object contains other PlistProperties objects, they will also
	 * be searched.
	 * 
	 * @param _key
	 *            String
	 * @return Object
	 */
	public Object getPropertyRecursive(String _key)
	{
		Object _return = null;
		for (int i = 0; i < internalKeys.size(); i++)
		{
			if (internalKeys.elementAt(i).equals(_key))
				return internalValues.elementAt(i);

			// If it is a PlistProperties we have to go recursive and catch the possible
			// error if no key is found
			if (internalValues.elementAt(i) instanceof PlistProperties)
			{
				Object result = ((PlistProperties) internalValues.elementAt(i)).getPropertyRecursive(_key);
				if (result != null)
					return result;
			}
			// If it is a Vector it is an array so loop through the elements to search
			// PlistProperties and then go recursive
			else if (internalValues.elementAt(i) instanceof Vector)
			{
				Vector array = (Vector) internalValues.elementAt(i);
				for (Object obj : array)
				{
					if (obj instanceof PlistProperties)
					{
						_return = ((PlistProperties) obj).getPropertyRecursive(_key);
					}
				}
			}
		}
		return _return;
	}

	/**
	 * Set the key of this object
	 * 
	 * @param _key
	 *            String
	 */
	public void setPropertiesKey(String _key)
	{
		key = _key;
	}

	/**
	 * Get the key of this object
	 * 
	 * @return String
	 */
	public String getPropertiesKey()
	{
		return key;
	}

	/**
	 * Get the value of a given key and return the <code>_default</code> object if nothing is found. This function is
	 * not recursive.
	 * 
	 * @param _key
	 *            String
	 * @param _default
	 *            Object
	 * @return Object
	 */
	public Object getProperty(String _key, Object _default)
	{
		Object _return = getProperty(_key);
		if (_return == null)
			return _default;
		return _return;
	}

	/**
	 * Set the value of a given key <code>_key</code> to a value <code>_value</code>. This function generates an error
	 * if the wrong key is given. If the key exists its value is changed. Otherwise a new element is added.
	 * 
	 * @param _key
	 *            String
	 * @param _value
	 *            Object
	 * @throws PlistReaderException
	 */
	public void setProperty(String _key, Object _value) throws PlistReaderException
	{
		int _index = getKeyIndex(_key);
		if (_index != -1)
		{
			internalValues.setElementAt(_value, _index);
		}
		else
		{
			internalKeys.addElement(_key);
			internalValues.addElement(_value);
		}
	}

	/**
	 * Change the value of a given key <code>_key</code> to a value <code>_value</code>. The difference with the
	 * <code>setProperty</code> function is the fact that this function throws an exception when no key is found instead
	 * of adding a new node.
	 * 
	 * @param _key
	 *            String
	 * @param _value
	 *            Object
	 * @throws PlistReaderException
	 */
	public void changeProperty(String _key, Object _value) throws PlistReaderException
	{
		int _index = getKeyIndex(_key);
		if (_index != -1)
		{
			internalValues.setElementAt(_value, _index);
		}
		else
		{
			throw PlistReaderException.NON_EXISTING_KEY;
		}
	}

	/**
	 * Get the number of properties belonging to a certain type. For example if you want to know how many elements there
	 * are into the <code>PlistProperties</code> object of the type String (equals to a <code>&lt;string&gt;</code> tag
	 * you can call this function. As parameter <code>_type</code> you can use the static fields of this class:
	 * <ul>
	 * <li>TYPE_STRING
	 * <li>TYPE_INTEGER
	 * <li>TYPE_DOUBLE
	 * <li>TYPE_DATE
	 * <li>TYPE_DATA
	 * <li>TYPE_BOOLEAN
	 * <li>TYPE_ARRAY
	 * <li>TYPE_VECTOR
	 * <li>TYPE_PLISTPROPERTIES
	 * <li>TYPE_DICTIONARY
	 * </ul>
	 * 
	 * @param _type
	 *            Class
	 * @return int
	 */
	public int propertyTypeCount(Class _type)
	{
		return propertyTypeCount(_type, 0);
	}

	/**
	 * Get the number of properties belonging to a certain type. See <code>propertyTypeCount(Class _type)</code> for
	 * more information. The only difference with that previous function is the fact that the counting doesn't start
	 * from 0 but from <code>_baseCount</code>.
	 * 
	 * @param _type
	 *            Class
	 * @param _baseCount
	 *            int
	 * @return int
	 */
	public int propertyTypeCount(Class _type, int _baseCount)
	{
		int count = _baseCount;
		for (int i = 0; i < internalKeys.size(); i++)
		{
			if (internalValues.elementAt(i).getClass().equals(_type.getClass()))
			{
				count++;
			}
			if (internalValues.elementAt(i).getClass().equals(TYPE_DICTIONARY))
			{
				count += propertyTypeCount(_type, count);
			}
			else if (internalValues.elementAt(i).getClass().equals(TYPE_ARRAY))
			{
				for (int j = 0; j < ((Vector) internalValues.elementAt(i)).size(); j++)
				{
					Object element = ((Vector) internalValues.elementAt(i)).elementAt(j);
					if (element.getClass().equals(_type.getClass()))
					{
						count++;
					}
					if (element.getClass().equals(TYPE_DICTIONARY))
					{
						count += propertyTypeCount(_type, count);
					}
				}
			}
		}
		return count;
	}

	/**
	 * This function converts a <code>PlistPropertiesObject</code> to a regular <code>java.util.Properties</code>
	 * object. Only the first-level childs of the root <code>dict</code> node are converted to <code>
   * String-String</code>
	 * key-value pairs. Nested arrays or dictionaries are not added to the <code>Properties</code> object.
	 * 
	 * @return Properties
	 */
	public Properties convertToProperties()
	{
		Properties props = new Properties();
		for (int i = 0; i < internalKeys.size(); i++)
		{
			props.setProperty(internalKeys.elementAt(i), internalValues.elementAt(i).toString());
		}
		return props;
	}

	public static Properties convertToProperties(PlistProperties _props)
	{
		return _props.convertToProperties();
	}

	public static PlistProperties convertToPlistProperties(Properties _props)
	{
		PlistProperties newprops = new PlistProperties();
		Enumeration<Object> keys = _props.keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			try
			{
				newprops.setProperty(key, _props.getProperty(key));
			}
			catch (PlistReaderException ex)
			{
				ex.printStackTrace();
			}
		}
		return newprops;
	}

	/**
	 * This function prints the contents of this object to a Printstream <code>
   * _out</code>. An '-' means an element of this
	 * object and '+' refers to elements of an array.
	 * 
	 * @see #list(PrintStream _out, int _tabIndex)
	 * @param _out
	 *            PrintStream
	 */
	public void list(PrintStream _out)
	{
		list(_out, 0);
	}

	/**
	 * This function prints the contents of this object to a Printstream <code>
   * _out</code>. An '-' means an element of this
	 * object and '+' refers to elements of an array.
	 * 
	 * @see #list(PrintStream _out, int _tabIndex)
	 * @param _out
	 *            PrintStream
	 */
	public void list(PrintWriter _out)
	{
		list(_out, 0);
	}

	/**
	 * This function prints the contents of this object to a Printstream <code>
   * _out</code>. An '-' means an element of this
	 * object and '+' refers to elements of an array. With the integer <code>_tabIndex</code> you can d define the
	 * indentation of the text.
	 * 
	 * @see #list(PrintStream _out)
	 * @param _out
	 *            PrintStream
	 * @param _tabIndex
	 *            int - the left indentation
	 */
	public void list(PrintStream _out, int _tabIndex)
	{
		try
		{
			printContent(_out, _tabIndex);
		}
		catch (PlistReaderException ex)
		{
			ex.printStackTrace(_out);
		}
	}

	/**
	 * This function prints the contents of this object to a PrintWriter <code>
   * _out</code>. An '-' means an element of this
	 * object and '+' refers to elements of an array. With the integer <code>_tabIndex</code> you can d define the
	 * indentation of the text.
	 * 
	 * @see #list(PrintWriter _out)
	 * @param _out
	 *            PrintStream
	 * @param _tabIndex
	 *            int - the left indentation
	 */
	public void list(PrintWriter _out, int _tabIndex)
	{
		try
		{
			printContent(_out, _tabIndex);
		}
		catch (PlistReaderException ex)
		{
			ex.printStackTrace(_out);
		}
	}

	/**
	 * This function handles the actions described in the various <code>list()
   * </code> functions in this document. Because
	 * <code>printWriter</code> and <code>PrintStream</code> have for the <code>list()</code> functions the same method
	 * syntax these methods are blended to this function using some aspects of <code>java.lang.reflect</code> to print
	 * it to the resp. streams.
	 * 
	 * @param _out
	 *            Object
	 * @param _tabIndex
	 *            int
	 * @throws PlistReaderException
	 */
	private void printContent(Object _out, int _tabIndex) throws PlistReaderException
	{
		try
		{
			// Get the method from the object _out which must be a PrintStream or a
			// PrintWriter... otherwise errors.
			// Next print by typing printMethod.invoke(Object,Object[]);
			Method printMethod = _out.getClass().getMethod("print", new Class[] { String.class }); //$NON-NLS-1$
			// Define the tabstring
			String tab = ""; //$NON-NLS-1$
			// Set the tab based on the _tabIndex
			for (int t = 0; t < _tabIndex; t++)
			{
				tab += "\t"; //$NON-NLS-1$
			}
			// loop synchronous through the internalKeys and internalValues Vector
			// and get value
			for (int i = 0; i < internalKeys.size(); i++)
			{
				Object value = internalValues.elementAt(i);
				// Print the key
				printMethod.invoke(_out, new Object[] { tab + " - " }); //$NON-NLS-1$
				printMethod.invoke(_out, new Object[] { internalKeys.elementAt(i).toString() + ":" }); //$NON-NLS-1$
				// If value is instance of PlistProperties: recurse via list function of
				// that value.
				if (value instanceof PlistProperties)
				{
					printMethod.invoke(_out, new Object[] { "\r\n" }); //$NON-NLS-1$
					if (_out instanceof PrintStream)
					{
						((PlistProperties) value).list((PrintStream) _out, _tabIndex + 1);
					}
					else
					{
						((PlistProperties) value).list((PrintWriter) _out, _tabIndex + 1);
					}
					// Jump over the next lines and start new turn of the loop
					continue;
				}
				// If it is a vector print all the elements after the key.
				if (value instanceof Vector)
				{
					printMethod.invoke(_out, new Object[] { "\r\n" }); //$NON-NLS-1$
					// Loop through the vector
					for (int j = 0; j < ((Vector) value).size(); j++)
					{
						Object element = ((Vector) value).elementAt(j);
						// If the element is instance of PlistProperties: recurse via list
						// function of that function.
						if (element instanceof PlistProperties)
						{
							// Print key of PlistProperties
							printMethod.invoke(_out, new Object[] { tab + "   + " //$NON-NLS-1$
									+ ((PlistProperties) element).getPropertiesKey() + ":\r\n" }); //$NON-NLS-1$
							if (_out instanceof PrintStream)
							{
								((PlistProperties) element).list((PrintStream) _out, _tabIndex + 1);
							}
							else
							{
								((PlistProperties) element).list((PrintWriter) _out, _tabIndex + 1);
							}

						}
						// Print the array element
						else
						{
							printMethod.invoke(_out, new Object[] { tab + "   + " + element.toString() + " (Class: " //$NON-NLS-1$ //$NON-NLS-2$
									+ element.getClass().getName() + ")\r\n" }); //$NON-NLS-1$
						}
					}
					// Jump over the next lines and start new turn of the loop
					continue;
				}
				// If the current key - value pair contains no PlistProperties or Vector
				// object whe should reach this line which only prints out the value :)
				printMethod.invoke(_out, new Object[] { internalValues.elementAt(i).toString() + "\r\n" }); //$NON-NLS-1$
			}

		}
		// Catch all exceptions
		catch (SecurityException ex)
		{
			throw PlistReaderException.SECURITY_ERROR;
		}
		catch (NoSuchMethodException ex)
		{
			throw PlistReaderException.PARAMETER_NOT_CORRECT;
		}
		catch (InvocationTargetException ex)
		{
			ex.printStackTrace();
		}
		catch (IllegalArgumentException ex)
		{
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex)
		{
			ex.printStackTrace();
		}

	}

	/**
	 * Get the <code>Vector</code> of all the keys
	 * 
	 * @return Vector
	 */
	public Vector<String> getKeys()
	{
		return internalKeys;
	}

	/**
	 * Get the <code>Vector</code> of all the values
	 * 
	 * @return Vector
	 */
	public Vector<Object> getValues()
	{
		return internalValues;
	}

	public boolean hasKey(String key)
	{
		return getKeys().contains(key);
	}

}
