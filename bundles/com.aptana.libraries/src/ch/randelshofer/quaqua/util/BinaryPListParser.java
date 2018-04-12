/*
 * Copyright (c) 2005-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.aptana.plist.IPListParser;

/**
 * Reads a binary PList file and returns it as a List of Objects.
 * <p>
 * Description about property list taken from <a href="http://developer.apple.com/documentation/Cocoa/Conceptual/PropertyLists/index.html#//apple_ref/doc/uid/10000048i">
 * Apple's online documentation</a>:
 * <p>
 * "A property list is a data representation used by Mac OS X Cocoa and Core
 * Foundation as a convenient way to store, organize, and access standard object
 * types. Frequently called a plist, a property list is an object of one of
 * several certain Cocoa or Core Foundation types, including  arrays,
 * dictionaries, strings, binary data, numbers, dates, and Boolean values. If
 * the object is a container (an array or dictionary), all objects contained
 * within it must also be supported property list objects. (Arrays and
 * dictionaries can contain objects not supported by the architecture, but are
 * then not property lists, and cannot be saved and restored with the various
 * property list methods.)"
 * <p>
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class BinaryPListParser implements IPListParser {

    private final static boolean DEBUG = false;

    /* Description of the binary plist format derived from
     * http://cvs.opendarwin.org/cgi-bin/cvsweb.cgi/~checkout~/src/CoreFoundation/Parsing.subproj/CFBinaryPList.c?rev=1.1.1.3&content-type=text/plain
     *
     * EBNF description of the file format:
     * <pre>
     * bplist ::= header objectTable offsetTable trailer
     *
     * header ::= magicNumber fileFormatVersion
     * magicNumber ::= "bplist"
     * fileFormatVersion ::= "00"
     *
     * objectTable ::= { null | bool | fill | number | date | data |
     *                 string | uid | array | dict }
     *
     * null  ::= 0b0000 0b0000
     *
     * bool  ::= false | true
     * false ::= 0b0000 0b1000
     * true  ::= 0b0000 0b1001
     *
     * fill  ::= 0b0000 0b1111         // fill byte
     *
     * number ::= int | real
     * int    ::= 0b0001 0bnnnn byte*(2^nnnn)  // 2^nnnn big-endian bytes
     * real   ::= 0b0010 0bnnnn byte*(2^nnnn)  // 2^nnnn big-endian bytes
     *
     * date   ::= 0b0011 0b0011 byte*8       // 8 byte float big-endian bytes
     *
     * data   ::= 0b0100 0bnnnn [int] byte*  // nnnn is number of bytes
     *                                       // unless 0b1111 then a int
     *                                       // variable-sized object follows
     *                                       // to indicate the number of bytes
     *
     * string ::= asciiString | unicodeString
     * asciiString   ::= 0b0101 0bnnnn [int] byte*
     * unicodeString ::= 0b0110 0bnnnn [int] short*
     *                                       // nnnn is number of bytes
     *                                       // unless 0b1111 then a int
     *                                       // variable-sized object follows
     *                                       // to indicate the number of bytes
     *
     * uid ::= 0b1000 0bnnnn byte*           // nnnn+1 is # of bytes
     *
     * array ::= 0b1010 0bnnnn [int] objref* //
     *                                       // nnnn is number of objref
     *                                       // unless 0b1111 then a int
     *                                       // variable-sized object follows
     *                                       // to indicate the number of objref
     *
     * dict ::= 0b1010 0bnnnn [int] keyref* objref* 
     *                                       // nnnn is number of keyref and 
     *                                       // objref pairs
     *                                       // unless 0b1111 then a int
     *                                       // variable-sized object follows
     *                                       // to indicate the number of pairs
     *
     * objref = byte | short                 // if refCount
     *                                       // is less than 256 then objref is
     *                                       // an unsigned byte, otherwise it
     *                                       // is an unsigned big-endian short
     *
     * keyref = byte | short                 // if refCount
     *                                       // is less than 256 then objref is
     *                                       // an unsigned byte, otherwise it
     *                                       // is an unsigned big-endian short
     *
     * unused ::= 0b0111 0bxxxx | 0b1001 0bxxxx |
     *            0b1011 0bxxxx | 0b1100 0bxxxx |
     *            0b1110 0bxxxx | 0b1111 0bxxxx
     *
     *
     * offsetTable ::= { int }               // list of ints, byte size of which 
     *                                       // is given in trailer
     *                                       // these are the byte offsets into
     *                                       // the file
     *                                       // number of these is in the trailer
     *
     * trailer ::= refCount offsetCount objectCount topLevelOffset
     *
     * refCount ::= byte*8                  // unsigned big-endian long
     * offsetCount ::= byte*8               // unsigned big-endian long
     * objectCount ::= byte*8               // unsigned big-endian long
     * topLevelOffset ::= byte*8            // unsigned big-endian long
     * </pre>
     */
	/**
	 * Total count of objrefs and keyrefs.
	 */
	private int refCount;
	/**
	 * Offset in file of top level offset in offset table.
	 */
	private int topLevelOffset;
	/**
	 * Object table. We gradually fill in objects from the binary PList object table into this list.
	 */
	private ArrayList<Object> objectTable;

	/** Holder for a binary PList Uid element. */
	private static class BPLUid
	{

		private final int number;

		public BPLUid(int number)
		{
			super();
			this.number = number;
		}

		public int getNumber()
		{
			return number;
		}
	}

	/**
	 * Holder for a binary PList array element.
	 */
	private static class BPLArray
	{

		ArrayList<Object> objectTable;
		int[] objref;

		public Object getValue(int i)
		{
			return objectTable.get(objref[i]);
		}

		public List<Object> toList()
		{
			List<Object> list = new ArrayList<Object>(objref.length);
			for (int i = 0; i < objref.length; i++)
			{
				list.add(convert(getValue(i)));
			}
			return list;
		}

		@Override
		public String toString()
		{
			StringBuffer buf = new StringBuffer("Array{"); //$NON-NLS-1$
			for (int i = 0; i < objref.length; i++)
			{
				if (i > 0)
				{
					buf.append(',');
				}
				if (objectTable.size() > objref[i] && objectTable.get(objref[i]) != this)
				{
					buf.append(objectTable.get(objref[i]));
				}
				else
				{
					buf.append("*" + objref[i]); //$NON-NLS-1$
				}
			}
			buf.append('}');
			return buf.toString();
		}
	}

	/**
	 * Holder for a binary PList dict element.
	 */
	private static class BPLDict
	{

		ArrayList<Object> objectTable;
		int[] keyref;
		int[] objref;

		public String getKey(int i)
		{
			return objectTable.get(keyref[i]).toString();
		}

		public Object getValue(int i)
		{
			return objectTable.get(objref[i]);
		}

		public Map<String, Object> toMap()
		{
			Map<String, Object> map = new HashMap<String, Object>(keyref.length);
			for (int i = 0; i < keyref.length; i++)
			{
				map.put(getKey(i), convert(getValue(i)));
			}
			return map;
		}

		@Override
		public String toString()
		{
			StringBuffer buf = new StringBuffer("BPLDict{"); //$NON-NLS-1$
			for (int i = 0; i < keyref.length; i++)
			{
				if (i > 0)
				{
					buf.append(',');
				}
				if (keyref[i] < 0 || keyref[i] >= objectTable.size())
				{
					buf.append("#" + keyref[i]); //$NON-NLS-1$
				}
				else if (objectTable.get(keyref[i]) == this)
				{
					buf.append("*" + keyref[i]); //$NON-NLS-1$
				}
				else
				{
					buf.append(objectTable.get(keyref[i]));
					// buf.append(keyref[i]);
				}
				buf.append(":"); //$NON-NLS-1$
				if (objref[i] < 0 || objref[i] >= objectTable.size())
				{
					buf.append("#" + objref[i]); //$NON-NLS-1$
				}
				else if (objectTable.get(objref[i]) == this)
				{
					buf.append("*" + objref[i]); //$NON-NLS-1$
				}
				else
				{
					buf.append(objectTable.get(objref[i]));
					// buf.append(objref[i]);
				}
			}
			buf.append('}');
			return buf.toString();
		}
	}

	/**
	 * Converts dicts to maps, arrays to lists
	 * 
	 * @param obj
	 * @return
	 */
	protected static Object convert(Object obj)
	{
		if (obj instanceof BPLDict)
		{
			return ((BPLDict) obj).toMap();
		}
		if (obj instanceof BPLArray)
		{
			return ((BPLArray) obj).toList();
		}
		if (obj instanceof BPLUid)
		{
			return ((BPLUid) obj).getNumber();
		}
		return obj;
	}

	/**
	 * Creates a new instance.
	 */
	public BinaryPListParser()
	{
	}

	/**
	 * Parses a binary PList file and turns it into a XMLElement. The XMLElement is equivalent with a XML PList file
	 * parsed using NanoXML.
	 * 
	 * @param file
	 *            A file containing a binary PList.
	 * @return Returns the parsed XMLElement.
	 */
	public Map<String, Object> parse(File file) throws IOException
	{
		RandomAccessFile raf = null;
		byte[] buf = null;
		try
		{
			raf = new RandomAccessFile(file, "r"); //$NON-NLS-1$

			// Parse the HEADER
			// ----------------
			// magic number ("bplist")
			// file format version ("00")
			int bpli = raf.readInt();
			int st00 = raf.readInt();
			if (bpli != 0x62706c69 || st00 != 0x73743030)
			{
				throw new IOException("parseHeader: File does not start with 'bplist00' magic."); //$NON-NLS-1$
			}

			// Parse the TRAILER
			// ----------------
			// byte size of offset ints in offset table
			// byte size of object refs in arrays and dicts
			// number of offsets in offset table (also is number of objects)
			// element # in offset table which is top level object
			raf.seek(raf.length() - 32);
			// count of offset ints in offset table
//			offsetCount = (int) raf.readLong();
			raf.readLong();
			// count of object refs in arrays and dicts
			refCount = (int) raf.readLong();
			// count of offsets in offset table (also is number of objects)
//			objectCount = (int) raf.readLong();
			raf.readLong();
			// element # in offset table which is top level object
			topLevelOffset = (int) raf.readLong();
			buf = new byte[topLevelOffset - 8];
			raf.seek(8);
			raf.readFully(buf);
		}
		finally
		{
			if (raf != null)
			{
				raf.close();
			}
		}

		// Parse the OBJECT TABLE
		// ----------------------
		objectTable = new ArrayList<Object>();
		DataInputStream in = null;
		try
		{
			in = new DataInputStream(new ByteArrayInputStream(buf));
			parseObjectTable(in);
		}
		finally
		{
			if (in != null)
			{
				in.close();
			}
		}

		return ((BPLDict) objectTable.get(0)).toMap();
	}

    /**
     * Object Formats (marker byte followed by additional info in some cases)
     * null	0000 0000
     * bool	0000 1000			// false
     * bool	0000 1001			// true
     * fill	0000 1111			// fill byte
     * int	0001 nnnn	...		// # of bytes is 2^nnnn, big-endian bytes
     * real	0010 nnnn	...		// # of bytes is 2^nnnn, big-endian bytes
     * date	0011 0011	...		// 8 byte float follows, big-endian bytes
     * data	0100 nnnn	[int]	...	// nnnn is number of bytes unless 1111 then int count follows, followed by bytes
     * string	0101 nnnn	[int]	...	// ASCII string, nnnn is # of chars, else 1111 then int count, then bytes
     * string	0110 nnnn	[int]	...	// Unicode string, nnnn is # of chars, else 1111 then int count, then big-endian 2-byte shorts
     *          0111 xxxx			// unused
     * uid	1000 nnnn	...		// nnnn+1 is # of bytes
     *          1001 xxxx			// unused
     * array	1010 nnnn	[int]	objref*	// nnnn is count, unless '1111', then int count follows
     *          1011 xxxx			// unused
     *          1100 xxxx			// unused
     * dict	1101 nnnn	[int]	keyref* objref*	// nnnn is count, unless '1111', then int count follows
     *          1110 xxxx			// unused
     *          1111 xxxx			// unused
     */
	private void parseObjectTable(DataInputStream in) throws IOException
	{
		int marker;
		while ((marker = in.read()) != -1)
		{
			switch ((marker & 0xf0) >> 4)
			{
				case 0:
				{
					parsePrimitive(in, marker & 0xf);
					break;
				}
				case 1:
				{
					int count = 1 << (marker & 0xf);
					parseInt(in, count);
					break;
				}
				case 2:
				{
					int count = 1 << (marker & 0xf);
					parseReal(in, count);
					break;
				}
				case 3:
				{
					if ((marker & 0xf) != 3)
					{
						throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
					}
					parseDate(in);
					break;
				}
				case 4:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					parseData(in, count);
					break;
				}
				case 5:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					parseAsciiString(in, count);
					break;
				}
				case 6:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					parseUnicodeString(in, count);
					break;
				}
				case 7:
				{
					if (DEBUG)
					{
						System.out.println("parseObjectTable: illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
					}
					return;
					// throw new IOException("parseObjectTable: illegal marker "+Integer.toBinaryString(marker));
					// break;
				}
				case 8:
				{
					int count = (marker & 0xf) + 1;
					if (DEBUG)
					{
						System.out.println("uid " + count); //$NON-NLS-1$
					}
					parseUID(in, count);
					break;
				}
				case 9:
				{
					throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
					// break;
				}
				case 10:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					if (refCount > 255)
					{
						parseShortArray(in, count);
					}
					else
					{
						parseByteArray(in, count);
					}
					break;
				}
				case 11:
				{
					throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
					// break;
				}
				case 12:
				{
					throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
					// break;
				}
				case 13:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					if (refCount > 256)
					{
						parseShortDict(in, count);
					}
					else
					{
						parseByteDict(in, count);
					}
					break;
				}
				case 14:
				{
					throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
					// break;
				}
				case 15:
				{
					throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
					// break;
				}
			}
		}
	}

    /**
     * Reads a count value from the object table. Count values are encoded
     * using the following scheme:
     *
     * int	0001 nnnn   ...     // # of bytes is 2^nnnn, big-endian bytes
     */
	private int readCount(DataInputStream in) throws IOException
	{
		int marker = in.read();
		if (marker == -1)
		{
			throw new IOException("variableLengthInt: Illegal EOF in marker"); //$NON-NLS-1$
		}
		if (((marker & 0xf0) >> 4) != 1)
		{
			throw new IOException("variableLengthInt: Illegal marker " + Integer.toBinaryString(marker)); //$NON-NLS-1$
		}
		int count = 1 << (marker & 0xf);
		int value = 0;
		for (int i = 0; i < count; i++)
		{
			int b = in.read();
			if (b == -1)
			{
				throw new IOException("variableLengthInt: Illegal EOF in value"); //$NON-NLS-1$
			}
			value = (value << 8) | b;
		}
		return value;
	}

    /**
     * null	0000 0000
     * bool	0000 1000			// false
     * bool	0000 1001			// true
     * fill	0000 1111			// fill byte
     */
	private void parsePrimitive(DataInputStream in, int primitive) throws IOException
	{
		switch (primitive)
		{
			case 0:
				objectTable.add(null);
				break;
			case 8:
				objectTable.add(Boolean.FALSE);
				break;
			case 9:
				objectTable.add(Boolean.TRUE);
				break;
			case 15:
				// fill byte: don't add to object table
				break;
			default:
				throw new IOException("parsePrimitive: illegal primitive " + Integer.toBinaryString(primitive)); //$NON-NLS-1$
		}
	}

    /**
     * array	1010 nnnn	[int]	objref*	// nnnn is count, unless '1111', then int count follows
     */
	private void parseByteArray(DataInputStream in, int count) throws IOException
	{
		BPLArray arr = new BPLArray();
		arr.objectTable = objectTable;
		arr.objref = new int[count];

		for (int i = 0; i < count; i++)
		{
			arr.objref[i] = in.readByte() & 0xff;
			if (arr.objref[i] == -1)
			{
				throw new IOException("parseByteArray: illegal EOF in objref*"); //$NON-NLS-1$
			}
		}

		objectTable.add(arr);
	}

    /**
     * array	1010 nnnn	[int]	objref*	// nnnn is count, unless '1111', then int count follows
     */
	private void parseShortArray(DataInputStream in, int count) throws IOException
	{
		BPLArray arr = new BPLArray();
		arr.objectTable = objectTable;
		arr.objref = new int[count];

		for (int i = 0; i < count; i++)
		{
			arr.objref[i] = in.readShort() & 0xffff;
			if (arr.objref[i] == -1)
			{
				throw new IOException("parseShortArray: illegal EOF in objref*"); //$NON-NLS-1$
			}
		}

		objectTable.add(arr);
	}
	
    /*
     * data	0100 nnnn	[int]	...	// nnnn is number of bytes unless 1111 then int count follows, followed by bytes
     */
	private void parseData(DataInputStream in, int count) throws IOException
	{
		byte[] data = new byte[count];
		in.readFully(data);
		objectTable.add(data);
	}

    /**
     * byte dict	1101 nnnn keyref* objref*	// nnnn is less than '1111'
     */
	private void parseByteDict(DataInputStream in, int count) throws IOException
	{
		BPLDict dict = new BPLDict();
		dict.objectTable = objectTable;
		dict.keyref = new int[count];
		dict.objref = new int[count];

		for (int i = 0; i < count; i++)
		{
			dict.keyref[i] = in.readByte() & 0xff;
		}
		for (int i = 0; i < count; i++)
		{
			dict.objref[i] = in.readByte() & 0xff;
		}
		objectTable.add(dict);
	}

    /**
     * short dict	1101 ffff int keyref* objref*	// int is count
     */
	private void parseShortDict(DataInputStream in, int count) throws IOException
	{
		BPLDict dict = new BPLDict();
		dict.objectTable = objectTable;
		dict.keyref = new int[count];
		dict.objref = new int[count];

		for (int i = 0; i < count; i++)
		{
			dict.keyref[i] = in.readShort() & 0xffff;
		}
		for (int i = 0; i < count; i++)
		{
			dict.objref[i] = in.readShort() & 0xffff;
		}
		objectTable.add(dict);
	}

    /**
     * string	0101 nnnn	[int]	...	// ASCII string, nnnn is # of chars, else 1111 then int count, then bytes
     */
	private void parseAsciiString(DataInputStream in, int count) throws IOException
	{
		byte[] buf = new byte[count];
		in.readFully(buf);
		String str = new String(buf, "ASCII"); //$NON-NLS-1$
		objectTable.add(str);
	}

	private void parseUID(DataInputStream in, int count) throws IOException
	{
		if (count > 4)
		{
			throw new IOException("parseUID: unsupported byte count: " + count); //$NON-NLS-1$
		}
		byte[] uid = new byte[count];
		in.readFully(uid);
		objectTable.add(new BPLUid(new BigInteger(uid).intValue()));
	}

    /**
     * int	0001 nnnn	...		// # of bytes is 2^nnnn, big-endian bytes
     */
	private void parseInt(DataInputStream in, int count) throws IOException
	{
		if (count > 8)
		{
			throw new IOException("parseInt: unsupported byte count: " + count); //$NON-NLS-1$
		}
		long value = 0;
		for (int i = 0; i < count; i++)
		{
			int b = in.read();
			if (b == -1)
			{
				throw new IOException("parseInt: Illegal EOF in value"); //$NON-NLS-1$
			}
			value = (value << 8) | b;
		}
		objectTable.add(new Long(value));
	}

    /**
     * real	0010 nnnn	...		// # of bytes is 2^nnnn, big-endian bytes
     */
	private void parseReal(DataInputStream in, int count) throws IOException
	{
		switch (count)
		{
			case 4:
				objectTable.add(new Float(in.readFloat()));
				break;
			case 8:
				objectTable.add(new Double(in.readDouble()));
				break;
			default:
				throw new IOException("parseReal: unsupported byte count:" + count); //$NON-NLS-1$
		}
	}

    /**
     *  date	0011 0011	...		// 8 byte float follows, big-endian bytes
     */
	private void parseDate(DataInputStream in) throws IOException
	{
		double date = in.readDouble();
		/*
		 * PList time is measured in seconds relative to the absolute reference date of Jan 1 2001 00:00:00 GMT. A
		 * positive value represents a date after the reference date, a negative value represents a date before it. For
		 * example, the absolute time -32940326 is equivalent to December 16th, 1999 at 17:54:34. So we need to generate
		 * a calendar at that point, since Java uses millis since January 1, 1970, 00:00:00 GMT as date epoch.
		 */
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		c.set(Calendar.YEAR, 2001);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		while (date > Integer.MAX_VALUE)
		{
			c.add(Calendar.SECOND, Integer.MAX_VALUE);
			date -= Integer.MAX_VALUE;
		}
		c.add(Calendar.SECOND, (int) date);
		objectTable.add(c.getTime());
	}

    /**
     * string	0110 nnnn	[int]	...	// Unicode string, nnnn is # of chars, else 1111 then int count, then big-endian 2-byte shorts
     */
	private void parseUnicodeString(DataInputStream in, int count) throws IOException
	{
		char[] buf = new char[count];
		for (int i = 0; i < count; i++)
		{
			buf[i] = in.readChar();
		}
		String str = new String(buf);
		objectTable.add(str);
	}
}