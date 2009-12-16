package plistreader;

/**
 * <p>Title: PlistReader PlistFactory</p>
 *
 * <p>Description: Package to read and write PLIST files on OsX</p>
 *
 * <p>Copyright: Copyright (c) 2007 Gie Spaepen</p>
 *
 * <p>Company: University of Antwerp</p>
 *
 * <p>Use this factory class to create a useable reader or writer class.  Since
 * <code>AbstractReader</code> and <code>AbstractWriter</code> are declared
 * abstract you have to extend these classes to add functionality to the reader
 * / writer.
 * @see plistreader.AbstractReader
 * @see plistreader.AbstractWriter
 * @author Gie Spaepen
 * @version 1.1
 */
public final class PlistFactory {


  /**
   * Create a useable reader class.
   * @return AbstractReader
   */
  public final static AbstractReader createReader(){
    return new AbstractReader(){};
  }

  /**
   * Create a useable writer class
   * @return AbstractWriter
   */
  public final static AbstractWriter createWriter(){
    return new AbstractWriter(){};
  }



}
