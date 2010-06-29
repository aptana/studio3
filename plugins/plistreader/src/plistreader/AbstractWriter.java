package plistreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * <p>Title: PlistReader AbstractWriter</p>
 *
 * <p>Description: Package to read and write PLIST files on OsX</p>
 *
 * <p>Copyright: Copyright (c) 2007 Gie Spaepen</p>
 *
 * <p>Company: University of Antwerp</p>
 *
 * <p>This file writes valid <code>plist</code> files based on a given
 * <code>PlistProperties</code> object and a target <code>File</code> object.
 * Be sure to use <code>plist</code> as extention.  The extention will not affect
 * the content of the file but will not be opened by the correct program is you
 * want to edit the file.</p>
 *
 * @author Gie Spaepen
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractWriter {

    /**
     * Holds the file to write to.  Needs to be specified in the constructor,
     * the <code>setFile</code> function or directly in the <code>write</code>
     * function.
     * @see #setFile(File _file)
     * @see #write()
     */
    private File file;
    /**
     * Holds the <code>PlistProperties</code> object which is written to the
     * file.  Needs to be specified in the constructor, the <code>setProperties</code>
     * function or directly in the <code>write</code> function.
     * @see #setFile(File _file)
     * @see #write()
     */
    private PlistProperties props;

    /**
     * Void constructor.  The file and the properties object must be set in order
     * to write a file.
     * @see #Abstractwriter()
     * @see #setFile(File _file)
     */
    public AbstractWriter() {
  }

  /**
   * The properties object must be specified in order to write a file.
   * @see #setProperties(PlistProperties _props)
   * @param _file File
   */
  public AbstractWriter(File _file) {
    file = _file;
  }

  /**
   * <b>Preferred constructor.</b>
   * @param _file File
   * @param _props PlistProperties
   */
  public AbstractWriter(File _file, PlistProperties _props) {
    file = _file;
    props = _props;
  }

  /**
   * Set the file to write to.
   * @param _file File
   */
  public void setFile(File _file) {file = _file;}

  /**
   * And get that file
   * @return File
   */
  public File getFile() {return file;}

  /**
   * Set the properties.
   * @param _props PlistProperties
   */
  public void setProperties(PlistProperties _props) {
    props = _props;
  }
  /**
   * Get the properties.
   * @return PlistProperties
   */
  public PlistProperties getProperties() {
    return props;
  }

  /**
   * Write the <code>PLIST</code> file.  The file and properties must already be
   * specified.
   * @throws PlistReaderException
   */
  public void write() throws PlistReaderException {
    try {
      if(props == null){
        throw PlistReaderException.NO_PROPERTIES_SPECIFIED;
      }
      PrintWriter out = new PrintWriter(new FileOutputStream(file));
      //Print header of file
      out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); //$NON-NLS-1$
      out.print("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\r\n"); //$NON-NLS-1$
      out.print("<plist version=\"1.0\">"); //$NON-NLS-1$
      writeDictionary(out,props,0);
      out.print("</plist>"); //$NON-NLS-1$
      out.flush();
      out.close();
    }
    catch (FileNotFoundException ex) {
      throw PlistReaderException.NO_FILE_SPECIFIED;
    }
    catch (PlistReaderException ex){
      throw ex;
    }
  }


  /**
   * Write the <code>PLIST</code> file.  The properties must already be
   * specified.
   * @param _file File
   * @throws PlistReaderException
   */
  public void write(File _file) throws PlistReaderException {
    setFile(_file);
    write();
  }

  /**
   * Write the <code>PLIST</code> file.
   * @param _file File
   * @param _props PlistProperties
   * @throws PlistReaderException
   */
  public void write(File _file, PlistProperties _props) throws
      PlistReaderException {
    setFile(_file);
    setProperties(_props);
    write();
  }

  /**
   * This pravate function writes away a <code>&lt;dict&gt;</code> node.
   * @param _out PrintWriter
   * @param _props PlistProperties
   * @param _tabIndex int
   */
private void writeDictionary(PrintWriter _out, PlistProperties _props,
                               int _tabIndex) {
    //Vectors for the keys and the fields
    Vector keys = _props.getKeys();
    Vector values = _props.getValues();
    //Define the tabstring
    String tab = ""; //$NON-NLS-1$
    //Set the tab based on the _tabIndex
    for (int t = 0; t < _tabIndex; t++) {
      tab += "\t"; //$NON-NLS-1$
    }
    //Print header
    _out.print(tab + "<dict>\r\n"); //$NON-NLS-1$
    tab = "\t" + tab; //$NON-NLS-1$
    //loop synchronous through the internalKeys and internalValues Vector
    //and get value
    for (int i = 0; i < keys.size(); i++) {
      Object value = values.elementAt(i);
      _out.print(tab + makeTag("key", (String) keys.elementAt(i))); //$NON-NLS-1$
      printValue(_out, _tabIndex + 1, tab , value);

    }
    //Print footer
    _out.print(tab.substring(1, tab.length()) + "</dict>\r\n"); //$NON-NLS-1$
  }

  /**
   * This pravate function writes away a <code>&lt;array&gt;</code> node.
   * @param _out PrintWriter
   * @param _vector Vector
   * @param _tabIndex int
   */
private void writeArray(PrintWriter _out, Vector _vector, int _tabIndex) {

    //Define the tabstring
    String tab = ""; //$NON-NLS-1$
    //Set the tab based on the _tabIndex
    for (int t = 0; t < _tabIndex; t++) {
      tab += "\t"; //$NON-NLS-1$
    }
    //Print header
    _out.print(tab + "<array>\r\n"); //$NON-NLS-1$
    tab = "\t" + tab; //$NON-NLS-1$
    //loop synchronous through the internalKeys and internalValues Vector
    //and get value
    for (int i = 0; i < _vector.size(); i++) {
      Object value = _vector.elementAt(i);
      printValue(_out, _tabIndex, tab, value);

    }
    //Print footer
    _out.print(tab.substring(1, tab.length()) + "</array>\r\n"); //$NON-NLS-1$

  }

  /**
   * This function prints the value of a given key.  It recurses to <code>
   * writeDictionary</code> and <code>writeArray</code> when a <code>PlistProperties</code>
   * object is encountered or a <code>Vector</code>.
   * @param _out PrintWriter
   * @param _tabIndex int
   * @param tab String
   * @param _value Object
   */
private void printValue(PrintWriter _out, int _tabIndex, String tab, Object _value) {
    //Recurse if it is a PlistProperties
    if (_value instanceof PlistProperties) {
      writeDictionary(_out, (PlistProperties) _value, _tabIndex + 1);
    }
    //Write an array
    else if (_value instanceof Vector) {
      writeArray(_out, (Vector) _value, _tabIndex);
    }
    //Write a string
    else if (_value instanceof String) {
      _out.print(tab + makeTag("string", (String) _value)); //$NON-NLS-1$
    }
    //Write an integer
    else if (_value instanceof Integer) {
      _out.print(tab + makeTag("integer", ( (Integer) _value).toString())); //$NON-NLS-1$
    }
    //Write a double
    else if (_value instanceof Double) {
      _out.print(tab + makeTag("real", ( (Double) _value).toString())); //$NON-NLS-1$
    }
    //Write a boolean
    else if (_value instanceof Boolean) {
      _out.print(tab + makeTag( ( (Boolean) _value).toString().toLowerCase(), null));
    }
    //Write a date
    else if (_value instanceof Date) {
      _out.print(tab + makeTag("date", new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format((Date) _value))); //$NON-NLS-1$ //$NON-NLS-2$
    }
    //Write data
    else if (_value instanceof Byte[]) {
      _out.print(tab + makeTag("data", ( (Byte[]) _value).toString())); //$NON-NLS-1$
    }
  }

  /**
   * Wraps a <code>String</code> value in a given <code>String</code> tagname:
   * <code>&lt;tagname&gt;value&lt;/tagname&gt;</code>.  When the value is
   * null then <code>&lt;tagname /&gt;</code> is returned.
   * @param tagname String
   * @param value String
   * @return String
   */
  private String makeTag(String tagname, String value) {
    if (value == null) {
      return MessageFormat.format("<{0} />\r\n", tagname); //$NON-NLS-1$
    }
	return  MessageFormat.format("<{0}>{1}</{2}>\r\n", tagname, value, tagname); //$NON-NLS-1$
  }

}
