/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.webserver.internal.core.builtin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;

import com.aptana.core.IURIMapper;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 */
/* package */class LocalWebServerHttpRequestHandler implements HttpRequestHandler
{

	private static final String METHOD_GET = "GET"; //$NON-NLS-1$
	private static final String METHOD_POST = "POST"; //$NON-NLS-1$
	private static final String METHOD_HEAD = "HEAD"; //$NON-NLS-1$

	private final static String HTML_TEXT_TYPE = "text/html"; //$NON-NLS-1$

	private final static Pattern PATTERN_INDEX = Pattern.compile("(index|default)\\.x?html?"); //$NON-NLS-1$

	private IURIMapper uriMapper;

	/**
	 * @param documentRoot
	 */
	protected LocalWebServerHttpRequestHandler(IURIMapper uriMapper)
	{
		this.uriMapper = uriMapper;
	}

	private void handleRequest(HttpRequest request, HttpResponse response, boolean head) throws HttpException,
			IOException, CoreException, URISyntaxException
	{
		String target = URLDecoder.decode(request.getRequestLine().getUri(), IOUtil.UTF_8);
		URI uri = URIUtil.fromString(target);
		IFileStore fileStore = uriMapper.resolve(uri);
		IFileInfo fileInfo = fileStore.fetchInfo();
		if (fileInfo.isDirectory())
		{
			fileInfo = getIndex(fileStore);
			if (fileInfo.exists())
			{
				fileStore = fileStore.getChild(fileInfo.getName());
			}
		}
		if (!fileInfo.exists())
		{
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			response.setEntity(createTextEntity(MessageFormat.format(
					Messages.LocalWebServerHttpRequestHandler_FILE_NOT_FOUND, uri.getPath())));
		}
		else if (fileInfo.isDirectory())
		{
			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			response.setEntity(createTextEntity(Messages.LocalWebServerHttpRequestHandler_FORBIDDEN));
		}
		else
		{
			response.setStatusCode(HttpStatus.SC_OK);
			if (head)
			{
				response.setEntity(null);
			}
			else
			{
				File file = fileStore.toLocalFile(EFS.NONE, new NullProgressMonitor());
				final File temporaryFile = (file == null) ? fileStore.toLocalFile(EFS.CACHE, new NullProgressMonitor())
						: null;
				response.setEntity(new NFileEntity((file != null) ? file : temporaryFile, getMimeType(fileStore
						.getName()))
				{
					@Override
					public void close() throws IOException
					{
						try
						{
							super.close();
						}
						finally
						{
							if (temporaryFile != null && !temporaryFile.delete())
							{
								temporaryFile.deleteOnExit();
							}
						}
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.HttpRequest,
	 * org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException,
			IOException
	{
		try
		{
			String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
			if (METHOD_GET.equals(method) || METHOD_HEAD.equals(method))
			{
				handleRequest(request, response, METHOD_HEAD.equals(method));
			}
			else if (METHOD_POST.equals(method))
			{
				handleRequest(request, response, METHOD_HEAD.equals(method));
			}
			else
			{
				throw new MethodNotSupportedException(MessageFormat.format(
						Messages.LocalWebServerHttpRequestHandler_UNSUPPORTED_METHOD, method));
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(WebServerCorePlugin.getDefault(), e);
			response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setEntity(createTextEntity(Messages.LocalWebServerHttpRequestHandler_INTERNAL_SERVER_ERROR));
		}
	}

	private static HttpEntity createTextEntity(String text) throws UnsupportedEncodingException
	{
		NStringEntity entity = new NStringEntity(MessageFormat.format("<html><body><h1>{0}</h1></body></html>", text), //$NON-NLS-1$
				IOUtil.UTF_8);
		entity.setContentType(HTML_TEXT_TYPE + HTTP.CHARSET_PARAM + IOUtil.UTF_8);
		return entity;
	}

	private static IFileInfo getIndex(IFileStore parent) throws CoreException
	{
		for (IFileInfo fileInfo : parent.childInfos(EFS.NONE, new NullProgressMonitor()))
		{
			if (fileInfo.exists() && PATTERN_INDEX.matcher(fileInfo.getName()).matches())
			{
				return fileInfo;
			}
		}
		return EFS.getNullFileSystem().getStore(Path.EMPTY).fetchInfo();
	}

	private static ContentType getMimeType(String fileName)
	{
		return ContentType.create(MimeTypesRegistry.INSTANCE.getMimeType(Path.fromPortableString(fileName)
				.getFileExtension()));
	}

}
