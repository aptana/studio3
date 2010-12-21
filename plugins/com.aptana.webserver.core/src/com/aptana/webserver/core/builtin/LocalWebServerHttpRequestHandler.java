/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

package com.aptana.webserver.core.builtin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
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

import com.aptana.webserver.core.EFSWebServerConfiguration;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 *
 */
/* package */ class LocalWebServerHttpRequestHandler implements HttpRequestHandler {

	private static final String METHOD_GET = "GET"; //$NON-NLS-1$
	private static final String METHOD_POST = "POST"; //$NON-NLS-1$
	private static final String METHOD_HEAD = "HEAD"; //$NON-NLS-1$
	
    private final static String HTML_TEXT_TYPE = "text/html"; //$NON-NLS-1$

    private final static Pattern PATTERN_INDEX = Pattern.compile("(index|default)\\.x?html?"); //$NON-NLS-1$
    
	private EFSWebServerConfiguration configuration;
	
	/**
	 * @param documentRoot
	 */
	public LocalWebServerHttpRequestHandler(EFSWebServerConfiguration configuration) {
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		try {
			String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
			if (METHOD_GET.equals(method) || METHOD_HEAD.equals(method)) {
				String target = URLDecoder.decode(request.getRequestLine().getUri(), HTTP.UTF_8);
				URI uri = URI.create(target);
				IFileStore fileStore = configuration.resolve(uri);
				IFileInfo fileInfo = fileStore.fetchInfo();
				if (fileInfo.isDirectory()) {
					fileInfo = getIndex(fileStore);
					if (fileInfo.exists()) {
						fileStore = fileStore.getChild(fileInfo.getName());
					}
				}
				if (!fileInfo.exists()) {
					response.setStatusCode(HttpStatus.SC_NOT_FOUND);
					response.setEntity(createTextEntity(MessageFormat.format(Messages.LocalWebServerHttpRequestHandler_FILE_NOT_FOUND, target)));
				} else if (fileInfo.isDirectory()) {
					response.setStatusCode(HttpStatus.SC_FORBIDDEN);
					response.setEntity(createTextEntity(Messages.LocalWebServerHttpRequestHandler_FORBIDDEN));
				} else {
					response.setStatusCode(HttpStatus.SC_OK);
					if (METHOD_GET.equals(method)) {
							final File file = fileStore.toLocalFile(EFS.CACHE, new NullProgressMonitor());
							response.setEntity(new NFileEntity(file,  HTML_TEXT_TYPE) {
								@Override
								public void finish() {
									super.finish();
									if (!file.delete()) {
										file.deleteOnExit();
									}
								}
								
							});
					} else {
						response.setEntity(null);
					}
				}
			} else if (METHOD_POST.equals(method)) {
				// TODO
				throw new MethodNotSupportedException(MessageFormat.format(Messages.LocalWebServerHttpRequestHandler_UNSUPPORTED_METHOD, method));
			} else {
				throw new MethodNotSupportedException(MessageFormat.format(Messages.LocalWebServerHttpRequestHandler_UNSUPPORTED_METHOD, method));
			}
		} catch (CoreException e) {
			WebServerCorePlugin.log(e);
			response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			response.setEntity(createTextEntity(Messages.LocalWebServerHttpRequestHandler_INTERNAL_SERVER_ERROR));
		}
	}
	
	private static HttpEntity createTextEntity(String text) throws UnsupportedEncodingException {
		NStringEntity entity = new NStringEntity(
				MessageFormat.format("<html><body><h1>{0}</h1></body></html>", text), //$NON-NLS-1$
				HTTP.UTF_8);
		entity.setContentType(HTML_TEXT_TYPE+HTTP.CHARSET_PARAM+HTTP.UTF_8);
		return entity;
	}
	
	private static IFileInfo getIndex(IFileStore parent) throws CoreException {
		for (IFileInfo fileInfo : parent.childInfos(EFS.NONE, new NullProgressMonitor())) {
			if (fileInfo.exists() && PATTERN_INDEX.matcher(fileInfo.getName()).matches()) {
				return fileInfo;
			}
		}
		return EFS.getNullFileSystem().getStore(Path.EMPTY).fetchInfo();
	}

}
