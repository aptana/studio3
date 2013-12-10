/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.sourcemap;

import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;

/**
 * Source map interface.
 * 
 * @author sgibly@appcelerator.com
 */
public interface ISourceMap extends IExecutableExtension
{

	/**
	 * Sets the content for the source mapping.
	 * 
	 * @param contents
	 *            The source-map file content.
	 * @throws Exception
	 */
	void setContents(String contents) throws Exception;

	/**
	 * Sets the content for the source mapping from an {@link InputStream}.
	 * 
	 * @param input
	 *            The {@link InputStream} to read the source-map file content.
	 * @throws Exception
	 */
	void setContents(InputStream input) throws Exception;

	/**
	 * Initialize the source map with an {@link IResource} that will be used to locate the map file. This method will
	 * work only when there are valid values for {@link #getMapLocationPrefix()} and
	 * {@link #getOriginalLocationPrefix()} (or {@link #getGeneratedLocationPrefix()}). A call to
	 * {@link #setContents(String)} will be made after resolving the sourcemap file location.
	 * 
	 * @param resource
	 * @see #setContents(InputStream)
	 * @see #setContents(String)
	 * @throws Exception
	 */
	void initialize(IResource resource) throws Exception;

	/**
	 * Initializes the source map with an {@link IResource} that will be used to locate the map file, and returns an
	 * {@link ISourceMapResult} that represents the location of the original code. Note that in case the original file
	 * name in the result is the same as the generated file name, there is no mapping, and the returned value will be
	 * <code>null</code>.
	 * 
	 * @param resource
	 *            an {@link IResource} that will be used to locate the map file
	 * @param lineNumber
	 *            1-based line number
	 * @param columnNumber
	 *            1-based column number
	 * @return An {@link ISourceMapResult}; <code>null</code> if there is no mapping.
	 * @throws Exception
	 * @see #initialize(IResource)
	 */
	ISourceMapResult getOriginalMapping(IResource resource, int lineNumber, int columnNumber) throws Exception;

	/**
	 * Initializes the source map with an {@link IResource} that will be used to locate the map file, and returns an
	 * {@link ISourceMapResult} that represents the location of the original code. Note that in case the original file
	 * name in the result is the same as the generated file name, there is no mapping, and the returned value will be
	 * <code>null</code>. This method will compute the first non-white character position in the given line and will try
	 * to locate the mapping by that.
	 * 
	 * @param resource
	 *            an {@link IResource} that will be used to locate the map file
	 * @param lineNumber
	 *            1-based line number
	 * @return An {@link ISourceMapResult}; <code>null</code> if there is no mapping.
	 * @throws Exception
	 * @throws Exception
	 * @see #initialize(IResource)
	 * @see #getOriginalMapping(IResource, int, int)
	 */
	ISourceMapResult getOriginalMapping(IResource resource, int lineNumber) throws Exception;

	/**
	 * Returns an {@link ISourceMapResult} that represents the location of the original code. Note that in case the
	 * original file name in the result is the same as the generated file name, there is no mapping, and the returned
	 * value will be <code>null</code>.
	 * 
	 * @param lineNumber
	 *            1-based line number
	 * @param columnNumber
	 *            1-based column number
	 * @return An {@link ISourceMapResult}; <code>null</code> if there is no mapping.
	 * @see #getOriginalMapping(IResource, int, int)
	 */
	ISourceMapResult getOriginalMapping(int lineNumber, int columnNumber);

	/**
	 * Initializes the source map with an {@link IResource} that will be used to locate the map file and returns an
	 * {@link ISourceMapResult} that represents the location of the generated code.The file path in the generated result
	 * will have a value only when the {@link #getGeneratedLocationPrefix()} returns a non-empty/null value.
	 * 
	 * @param resource
	 *            an {@link IResource} that will be used to locate the map file
	 * @param lineNumber
	 *            1-based line number
	 * @param columnNumber
	 *            1-based column number
	 * @return A list of {@link ISourceMapResult} instances; <code>null</code> if there is no mapping.
	 * @throws Exception
	 * @see #initialize(IResource)
	 */
	ISourceMapResult getGeneratedMapping(IResource resource, int lineNumber, int columnNumber) throws Exception;

	/**
	 * Returns an {@link ISourceMapResult} that represents the location of the generated code.The file path in the
	 * generated result will have a value only when the {@link #getGeneratedLocationPrefix()} returns a non-empty/null
	 * value.
	 * 
	 * @param originalFile
	 *            resource that will be used to locate the map file
	 * @param lineNumber
	 *            1-based line number
	 * @param columnNumber
	 *            1-based column number
	 * @return A list of {@link ISourceMapResult} instances; <code>null</code> if there is no mapping.
	 * @see #getGeneratedMapping(IResource, String, int, int)
	 */
	ISourceMapResult getGeneratedMapping(String originalFile, int lineNumber, int columnNumber);

	/**
	 * Returns the prefix path for the location of the sourcemap files.
	 * 
	 * @return An {@link IPath} that represents the project-relative path prefix for the map files. Can be
	 *         <code>null</code> if not defined in the extension contribution.
	 */
	IPath getMapLocationPrefix();

	/**
	 * Returns the prefix path for the location of the original files.
	 * 
	 * @return An {@link IPath} that represents the project-relative path prefix for the original files. Can be
	 *         <code>null</code> if not defined in the extension contribution.
	 */
	IPath getOriginalLocationPrefix();

	/**
	 * Returns the prefix path for the location of the generated files.
	 * 
	 * @return An {@link IPath} that represents the project-relative path prefix for the generated files. Can be
	 *         <code>null</code> if not defined in the extension contribution.
	 */
	IPath getGeneratedLocationPrefix();
}
