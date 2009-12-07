package plistreader;

/**
 * <p>Title: PlistReade PlistReaderExceptionr</p>
 *
 * <p>Description: Package to read and write PLIST files on OsX</p>
 *
 * <p>Copyright: Copyright (c) 2007 Gie Spaepen</p>
 *
 * <p>Company: University of Antwerp</p>
 *
 * <p>This package contains the possible errors which can be generated during
 * the parsing process.  These errors can be called by using the <code>static
 * </code> fields of this class which are in turn also <code>PlistReaderException
 * </code> objects.
 *
 * @author Gie Spaepen
 * @version 1.0
 */
public class PlistReaderException extends Exception {
    /**
     * Called when IO problems are encountered for opening the file.
     */
    public static PlistReaderException CANNOT_READ_FILE       = new PlistReaderException("Cannot read the PLIST file.");
    /**
     * Dateformat of the <code>&lt;date&gt;</code> must be formatted according
     * the <code>PLIST</code> and <code>NSDate</code> specifications: <code>
     * yyyy-MM-dd'T'hh:mm:ss'Z'</code>.
     */
    public static PlistReaderException WRONG_DATE_FORMAT      = new PlistReaderException("Date is not correctly formatted: should be dd/mm/yy");
    /**
     * This error is thrown when no argument is specified when starting the
     * test program <code>plistreader.test.testReader</code>.
     */
    public static PlistReaderException NO_ARGUMENTS           = new PlistReaderException("There is no file in the argument list...");
    /**
     * This error is called when the parser can't be instantiated.
     */
    public static PlistReaderException NO_PARSER_AVAILABLE    = new PlistReaderException("There is no SAX parser installed.  Check if the package is available on your machine");
    /**
     * This is thrown when the library can't read a file.
     */
    public static PlistReaderException IO_EXCEPTION_READ      = new PlistReaderException("IO Exception: cannot read the file.");
    /**
     * This is thrown when the library can't write to a file.
     */
    public static PlistReaderException IO_EXCEPTION_WRITE     = new PlistReaderException("IO Exception: cannot write the file.");
    /**
     * When a <code>PLIST</code> file is wrongly formatted and some
     * closing tags are missing, for example, this error will probably be called
     * because the program tries to match an array with a <code>Vector</code>.
     * Alwas called during parsing.
     */
    public static PlistReaderException WRONG_NESTED_ARRAY     = new PlistReaderException("Class casting exception during parsing: probably a malformed nested <array>");
    /**
     * Similar as <code>WRONG_NESTED_ARRAY</code>.
     * @see #WRONG_NESTED_ARRAY
     */
    public static PlistReaderException WRONG_NESTED_DICTIONARY= new PlistReaderException("Class casting exception during parsing: probably a malformed nested <dict>");
    /**
     * This function is also called during parsing.  Malformed XML will cause
     * mismatches when storing key-value pairs into the <code>PlistProperties</code>,
     * then this error is called.
     */
    public static PlistReaderException WRONG_ASSIGNMENT       = new PlistReaderException("Cannot assign a key-value pair to other objects than a PlistProperties or a Vector: malformed XML caused this probably");
    /**
     * Called when the search for a given key in a <code>PlistProperties</code>
     * object fails.
     */
    public static PlistReaderException NON_EXISTING_KEY       = new PlistReaderException("Cannot find key in PlistProperties object.");
    /**
     * The <code>list</code> functions of a <code>PlistProperties</code> object
     * use instances of <code>java.lang.reflect.Method</code> which can cause
     * security issues.
     */
    public static PlistReaderException SECURITY_ERROR         = new PlistReaderException("Errors during printing content of PlistProperties: security error when accessing methods of an object");
    /**
     * Used in the same situation as the <code>SECURITY_ERROR</code> error but
     * now if the program tries to invoke a wrong method.
     */
    public static PlistReaderException PARAMETER_NOT_CORRECT  = new PlistReaderException("Errors during printing content of PlistProperties: Object parameter in printContent function must be PrintStream or PrintWriter");
    /**
     * Used by the <code>AbstractReader</code> and the <code>AbstractWriter</code>
     * when no file is specified.
     */
    public static PlistReaderException NO_FILE_SPECIFIED      = new PlistReaderException("There is no file specified! (null)");
    /**
     * Used by the <code>AbstractReader</code> and the <code>AbstractWriter</code>
     * when no <code>PlistProperties</code> object is specified.
     */
    public static PlistReaderException NO_PROPERTIES_SPECIFIED= new PlistReaderException("There are no properties specified! (null)");

    /**
     * Use this constructor to make a custom <code>PlistReaderException</code>.
     * @param _string String
     */
    public PlistReaderException(String _string) {
      super(_string);
  }
}
