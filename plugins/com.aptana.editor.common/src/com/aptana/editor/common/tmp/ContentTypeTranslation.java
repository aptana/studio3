/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.editor.common.tmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.editor.common.QualifiedContentType;

/**
 * @author Max Stepanov
 *
 */
public class ContentTypeTranslation {

	private static ContentTypeTranslation instance;
	
	private Map<QualifiedContentType, QualifiedContentType> map = new HashMap<QualifiedContentType, QualifiedContentType>();
	
	/**
	 * 
	 */
	private ContentTypeTranslation() {
		addTranslation(new QualifiedContentType("com.aptana.contenttype.js"), new QualifiedContentType("source.js")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.css"), new QualifiedContentType("source.css")); //$NON-NLS-1$ //$NON-NLS-2$
		
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html"), new QualifiedContentType("text.html.basic")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html", "com.aptana.contenttype.css"), //$NON-NLS-1$ //$NON-NLS-2$
						new QualifiedContentType("text.html.basic", "source.css.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html", "com.aptana.contenttype.js"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.html.basic", "source.js.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		
		addTranslation(new QualifiedContentType("com.aptana.contenttype.ruby"), new QualifiedContentType("source.ruby.rails")); //$NON-NLS-1$ //$NON-NLS-2$
		
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html.erb"), new QualifiedContentType("text.html.ruby")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html.erb", "__common_start_switch_tag"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.html.ruby", "source.erb.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html.erb", "__common_end_switch_tag"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.html.ruby", "source.erb.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$

		addTranslation(new QualifiedContentType("com.aptana.contenttype.html.erb", "com.aptana.contenttype.html"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.html.ruby")); //$NON-NLS-1$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html.erb", "com.aptana.contenttype.css"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.html.ruby", "source.css.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html.erb", "com.aptana.contenttype.js"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.html.ruby", "source.js.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.html.erb", "com.aptana.contenttype.ruby"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.html.ruby", "source.ruby.rails.embedded.html")); //$NON-NLS-1$ //$NON-NLS-2$

		addTranslation(new QualifiedContentType("__html_comment"), new QualifiedContentType("comment.block.html")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__html_tag"), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__html_script"), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__html_style"), new QualifiedContentType("meta.tag.block.any.html")); //$NON-NLS-1$ //$NON-NLS-2$

		addTranslation(new QualifiedContentType("__css_multiline_comment"), new QualifiedContentType("comment.block.css")); //$NON-NLS-1$ //$NON-NLS-2$

		addTranslation(new QualifiedContentType("__js_string_double"), new QualifiedContentType("string.quoted.double.js")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__js_string_single"), new QualifiedContentType("string.quoted.single.js")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__js_regexp"), new QualifiedContentType("string.regexp.js")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__js_singleline_comment"), new QualifiedContentType("comment.line.double-slash.js")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__js_multiline_comment"), new QualifiedContentType("comment.block.js")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__js_sdoc"), new QualifiedContentType("comment.block.js")); //$NON-NLS-1$ //$NON-NLS-2$

		addTranslation(new QualifiedContentType("__rb_string"), new QualifiedContentType("string.quoted.double.ruby")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__rb_singleline_comment"), new QualifiedContentType("comment.line.number-sign.ruby")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__rb_multiline_comment"), new QualifiedContentType("comment.block.documentation.ruby")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("__rb_regular_expression"), new QualifiedContentType("string.regexp.classic.ruby")); //$NON-NLS-1$ //$NON-NLS-2$

		
		addTranslation(new QualifiedContentType("com.aptana.contenttype.xml.erb"), new QualifiedContentType("text.xml.ruby")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.xml.erb", "__common_start_switch_tag"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.xml.ruby", "source.erb.embedded.xml")); //$NON-NLS-1$ //$NON-NLS-2$
		addTranslation(new QualifiedContentType("com.aptana.contenttype.xml.erb", "__common_end_switch_tag"), //$NON-NLS-1$ //$NON-NLS-2$
				new QualifiedContentType("text.xml.ruby", "source.erb.embedded.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static ContentTypeTranslation getDefault() {
		if (instance == null) {
			instance = new ContentTypeTranslation();
		}
		return instance;
	}
	
	public void addTranslation(QualifiedContentType left, QualifiedContentType right) {
		map.put(left, right);
	}
	
	public QualifiedContentType translate(QualifiedContentType contentType) {
		QualifiedContentType i = contentType;
		QualifiedContentType result;
		List<String> parts = new ArrayList<String>();
		while((result = map.get(i)) == null && i.getPartCount() > 0) {
			parts.add(0, i.getLastPart());
			i = i.supertype();
		}
		if (result != null) {
			for (String part : parts) {
				QualifiedContentType qtype = new QualifiedContentType(part);
				if (map.containsKey(qtype)) {
					result = result.subtype(map.get(qtype).getParts());
				}
			}
			return result;
		}
		return contentType;
	}

}
