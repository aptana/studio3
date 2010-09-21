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
package com.aptana.editor.erb.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

public abstract class ERBContentDescriber implements ITextContentDescriber {

    private static final QualifiedName[] SUPPORTED_OPTIONS = new QualifiedName[] {
            IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK };

    public int describe(Reader contents, IContentDescription description) throws IOException {
        BufferedReader reader = new BufferedReader(contents);
        String line = reader.readLine();
        if (line == null) {
            return INDETERMINATE;
        }
        if (!line.startsWith(getPrefix())) {
            return INDETERMINATE;
        }
        return VALID;
    }

    public int describe(InputStream contents, IContentDescription description) throws IOException {
        byte[] bom = getByteOrderMark(contents);
        contents.reset();
        String xmlDeclEncoding = "UTF-8"; //$NON-NLS-1$
        if (bom != null) {
            if (bom == IContentDescription.BOM_UTF_16BE) {
                xmlDeclEncoding = "UTF-16BE"; //$NON-NLS-1$
            } else if (bom == IContentDescription.BOM_UTF_16LE) {
                xmlDeclEncoding = "UTF-16LE"; //$NON-NLS-1$
            }
            // skips BOM
            contents.skip(bom.length);
        }
        byte[] xmlPrefixBytes = getPrefix().getBytes(xmlDeclEncoding);
        byte[] prefix = new byte[xmlPrefixBytes.length];
        if (contents.read(prefix) < prefix.length) {
            return INDETERMINATE;
        }
        for (int i = 0; i < prefix.length; ++i) {
            if (prefix[i] != xmlPrefixBytes[i]) {
                return INDETERMINATE;
            }
        }
        return VALID;
    }

    public QualifiedName[] getSupportedOptions() {
        return SUPPORTED_OPTIONS;
    }

    protected abstract String getPrefix();

    private byte[] getByteOrderMark(InputStream input) throws IOException {
        int first = input.read();
        if (first == 0xEF) {
            // looks for the UTF-8 BOM
            int second = input.read();
            int third = input.read();
            if (second == 0xBB && third == 0xBF) {
                return IContentDescription.BOM_UTF_8;
            }
        } else if (first == 0xFE) {
            // looks for the UTF-16 BOM
            if (input.read() == 0xFF) {
                return IContentDescription.BOM_UTF_16BE;
            }
        } else if (first == 0xFF) {
            if (input.read() == 0xFE) {
                return IContentDescription.BOM_UTF_16LE;
            }
        }
        return null;
    }
}
