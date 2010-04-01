/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.metadata.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import com.aptana.parsing.io.TabledInputStream;
import com.aptana.parsing.metadata.ElementMetadata;
import com.aptana.parsing.metadata.MetadataEnvironment;

/**
 * @author Kevin Lindsey
 */
public class MetadataObjectsReader
{
	/*
	 * Fields
	 */
	private MetadataEnvironment environment;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of NativeObjectsReader
	 * 
	 * @param environment
	 *            The environment to populate when reading native object documentation
	 */
	public MetadataObjectsReader(MetadataEnvironment environment)
	{
		this.environment = environment;
	}

	/*
	 * Methods
	 */

	/**
	 * Load the specified native objects document binary file
	 * 
	 * @param filename
	 *            The name of the file to load
	 * @throws Exception
	 */
	public void load(String filename) throws Exception
	{
		FileInputStream istream = null;
		
		try
		{
			// create stream
			istream = new FileInputStream(filename);

			// load stream
			this.load(istream);
		}
		catch (FileNotFoundException e)
		{
			String msg = MessageFormat.format(Messages.MetadataObjectsReader_UnableToLocateDocumentationXML, new Object[] {filename});
			Exception de = new Exception(msg, e);

			throw de;
		}
		finally
		{
			try
			{
				// close stream
				istream.close();
			}
			catch (IOException e)
			{
				String msg = Messages.MetadataObjectsReader_IOErrorOccurredProcessingDocumentationXML;
				Exception de = new Exception(msg, e);

				throw de;
			}
		}
	}

	/**
	 * Load the specified native objects document binary stream
	 * 
	 * @param stream
	 *            The stream to read from
	 * @throws Exception
	 */
	public void load(InputStream stream) throws Exception
	{
		// DataInputStream input = new DataInputStream(stream);

		try
		{
			TabledInputStream input = new TabledInputStream(stream);

			environment.read(input);
		}
		catch (IOException e)
		{
			String msg = Messages.MetadataObjectsReader_IOErrorOccurredProcessingDocumentationBinary;
			Exception de = new Exception(msg, e);

			throw de;
		}
	}

	/**
	 * Load the specified native objects documentation file
	 * 
	 * @param filename
	 *            The name of the file to load
	 * @throws Exception
	 */
	public void loadXML(String filename) throws Exception
	{
		FileInputStream istream = null;
		try
		{
			// create stream
			istream = new FileInputStream(filename);

			// load stream
			this.loadXML(istream);

		}
		catch (FileNotFoundException e)
		{
			String msg = MessageFormat.format(Messages.MetadataObjectsReader_UnableToLocateDocumentationXML, new Object[] {filename});
			Exception de = new Exception(msg, e);

			throw de;
		}
		finally
		{
			try
			{
				// close stream
				istream.close();
			}
			catch (IOException e)
			{
				String msg = Messages.MetadataObjectsReader_IOErrorOccurredProcessingDocumentationXML;
				Exception de = new Exception(msg, e);

				throw de;
			}
		}
	}

	/**
	 * Load the native objects from the specified input stream
	 * 
	 * @param stream
	 *            The stream containing the native objects documentation
	 * @throws Exception
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		// create a new documentation reader
		MetadataReader reader = new MetadataReader();

		// load the specified document stream
		reader.loadXML(stream);

		Hashtable fields = reader.getGlobalFields();
		Hashtable events = reader.getGlobalEvents();

		environment.setGlobalFields(fields);
		environment.setGlobalEvents(events);

		ArrayList elements = reader.getElements();
		for (int i = 0; i < elements.size(); i++)
		{
			ElementMetadata el = (ElementMetadata) elements.get(i);
			environment.addElement(el);
		}
	}
}
