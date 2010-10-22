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
package com.aptana.editor.sass;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

@SuppressWarnings("nls")
public class SassCodeScannerTest extends AbstractTokenScannerTestCase
{
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new SassCodeScanner()
		{
			protected IToken createToken(String string)
			{
				return SassCodeScannerTest.this.getToken(string);
			};
		};
	}

	public void testH1Through6()
	{
		String src = "h1 h2 h3 h4 h5 h6 ";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		for (int i = 0; i < src.length(); i += 3)
		{
			assertToken(getToken("entity.name.tag.css"), i, 2);
			assertToken(Token.WHITESPACE, i + 2, 1);
		}
	}

	public void testCSS3PropertyNames()
	{
		String src = "border-radius: 1px\n" + "border-image-width: 1px\n" + "box-decoration-break: clone";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("support.type.property-name.css"), 0, 13);
		assertToken(getToken("punctuation.separator.key-value.css"), 13, 1);
		assertToken(Token.WHITESPACE, 14, 1);
		assertToken(getToken("constant.numeric.css"), 15, 1);
		assertToken(getToken("keyword.other.unit.css"), 16, 2);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken("support.type.property-name.css"), 19, 18);
		assertToken(getToken("punctuation.separator.key-value.css"), 37, 1);
		assertToken(Token.WHITESPACE, 38, 1);
		assertToken(getToken("constant.numeric.css"), 39, 1);
		assertToken(getToken("keyword.other.unit.css"), 40, 2);
		assertToken(Token.WHITESPACE, 42, 1);
		assertToken(getToken("support.type.property-name.css"), 43, 20);
		assertToken(getToken("punctuation.separator.key-value.css"), 63, 1);
		assertToken(Token.WHITESPACE, 64, 1);
	}

	// FIXME Test actual Sass, not CSS!

	public void testSmallCaps()
	{
		String src = "small { font: small-caps; }";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("entity.name.tag.css"), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 6, 1);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("support.type.property-name.css"), 8, 4);
		assertToken(getToken("punctuation.separator.key-value.css"), 12, 1);
		assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken("support.constant.property-value.css"), 14, 10);
		assertToken(getToken("punctuation.terminator.rule.css"), 24, 1);
		assertToken(Token.WHITESPACE, 25, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 26, 1);
	}

	public void testVariableDefinition()
	{
		String src = "!blue = #3bbfce";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("variable.other.sass"), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("punctuation.definition.entity.sass"), 6, 1);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("constant.other.color.rgb-value.css"), 8, 7);
	}

	public void testVariableUsage()
	{
		String src = ".content_navigation\n  border-color = !blue";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("entity.other.attribute-name.class.css"), 0, 19);
		assertToken(Token.WHITESPACE, 19, 3);
		assertToken(getToken("support.type.property-name.css"), 22, 12);
		assertToken(Token.WHITESPACE, 34, 1);
		assertToken(getToken("punctuation.definition.entity.sass"), 35, 1);
		assertToken(Token.WHITESPACE, 36, 1);
		assertToken(getToken("variable.other.sass"), 37, 5);
	}

	public void testMixinDefinition()
	{
		String src = "=table-scaffolding\n  th\n    text-align: center";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("variable.other.sass"), 0, 18);
		assertToken(Token.WHITESPACE, 18, 3);
		assertToken(getToken("entity.name.tag.css"), 21, 2);
		assertToken(Token.WHITESPACE, 23, 5);
		assertToken(getToken("support.type.property-name.css"), 28, 10);
		assertToken(getToken("punctuation.separator.key-value.css"), 38, 1);
		assertToken(Token.WHITESPACE, 39, 1);
		assertToken(getToken("support.constant.property-value.css"), 40, 6);
	}

	public void testMixinUsage()
	{
		String src = "#data\n  +table-scaffolding";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("entity.other.attribute-name.id.css"), 0, 5);
		assertToken(Token.WHITESPACE, 5, 3);
		assertToken(getToken("variable.other.sass"), 8, 18);
	}

	public void testBasicTokenizing()
	{
		String src = "html { color: red; background-color: #333; }";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("entity.name.tag.css"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 5, 1);
		assertToken(Token.WHITESPACE, 6, 1);
		assertToken(getToken("support.type.property-name.css"), 7, 5);
		assertToken(getToken("punctuation.separator.key-value.css"), 12, 1);
		assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken("support.constant.color.w3c-standard-color-name.css"), 14, 3);
		assertToken(getToken("punctuation.terminator.rule.css"), 17, 1);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken("support.type.property-name.css"), 19, 16);
		assertToken(getToken("punctuation.separator.key-value.css"), 35, 1);
		assertToken(Token.WHITESPACE, 36, 1);
		assertToken(getToken("constant.other.color.rgb-value.css"), 37, 4);
		assertToken(getToken("punctuation.terminator.rule.css"), 41, 1);
		assertToken(Token.WHITESPACE, 42, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 43, 1);
	}

	public void testBasicTokenizing2()
	{
		String src = "body {\n" + "  background-image: url();\n" + "  background-position-x: left;\n"
				+ "  background-position-y: top;\n" + "  background-repeat: repeat-x;\n"
				+ "  font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;\n" + "}\n" + "\n" + ".main {\n"
				+ "  border: 1px dotted #222222;\n" + "  margin: 5px;\n" + "}\n" + "\n" + ".header {\n"
				+ "  background-color: #FFFFFF;\n" + "  color: #444444;\n" + "  font-size: xx-large;\n" + "}\n" + "\n"
				+ ".menu {\n" + "  border-top: 2px solid #FC7F22;\n" + "  background-color: #3B3B3B;\n"
				+ "  color: #FFFFFF;\n" + "  text-align: right;\n" + "  vertical-align: right;\n"
				+ "  font-size: small;\n" + "}\n" + "\n" + ".menu a {\n" + "  color: #DDDDDD;\n"
				+ "  text-decoration: none;\n" + "}\n";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		// line 1
		assertToken(getToken("entity.name.tag.css"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 5, 1);
		assertToken(Token.WHITESPACE, 6, 3);
		// line 2
		assertToken(getToken("support.type.property-name.css"), 9, 16);
		assertToken(getToken("punctuation.separator.key-value.css"), 25, 1);
		assertToken(Token.WHITESPACE, 26, 1);
		assertToken(getToken("support.function.misc.css"), 27, 3);
		assertToken(getToken("punctuation.section.function.css"), 30, 1);
		assertToken(getToken("punctuation.section.function.css"), 31, 1);
		assertToken(getToken("punctuation.terminator.rule.css"), 32, 1);
		assertToken(Token.WHITESPACE, 33, 3);
		// line 3
		assertToken(getToken("support.type.property-name.css"), 36, 21);
		assertToken(getToken("punctuation.separator.key-value.css"), 57, 1);
		assertToken(Token.WHITESPACE, 58, 1);
		assertToken(getToken("support.constant.property-value.css"), 59, 4);
		assertToken(getToken("punctuation.terminator.rule.css"), 63, 1);
		assertToken(Token.WHITESPACE, 64, 3);
		// line 4
		assertToken(getToken("support.type.property-name.css"), 67, 21);
		assertToken(getToken("punctuation.separator.key-value.css"), 88, 1);
		assertToken(Token.WHITESPACE, 89, 1);
		assertToken(getToken("support.constant.property-value.css"), 90, 3);
		assertToken(getToken("punctuation.terminator.rule.css"), 93, 1);
		assertToken(Token.WHITESPACE, 94, 3);
		// line 5
		assertToken(getToken("support.type.property-name.css"), 97, 17);
		assertToken(getToken("punctuation.separator.key-value.css"), 114, 1);
		assertToken(Token.WHITESPACE, 115, 1);
		assertToken(getToken("support.constant.property-value.css"), 116, 8);
		assertToken(getToken("punctuation.terminator.rule.css"), 124, 1);
		assertToken(Token.WHITESPACE, 125, 3);
		// line 6
		assertToken(getToken("support.type.property-name.css"), 128, 11);
		assertToken(getToken("punctuation.separator.key-value.css"), 139, 1);
		assertToken(Token.WHITESPACE, 140, 1);
		assertToken(getToken("support.constant.font-name.css"), 141, 7);
		assertToken(getToken("punctuation.separator.css"), 148, 1);
		assertToken(Token.WHITESPACE, 149, 1);
		assertToken(getToken("support.constant.font-name.css"), 150, 6);
		assertToken(getToken("punctuation.separator.css"), 156, 1);
		assertToken(Token.WHITESPACE, 157, 1);
		assertToken(getToken("support.constant.font-name.css"), 158, 5);
		assertToken(getToken("punctuation.separator.css"), 163, 1);
		assertToken(Token.WHITESPACE, 164, 1);
		assertToken(getToken("support.constant.font-name.css"), 165, 9);
		assertToken(getToken("punctuation.separator.css"), 174, 1);
		assertToken(Token.WHITESPACE, 175, 1);
		assertToken(getToken("support.constant.font-name.css"), 176, 10);
		assertToken(getToken("punctuation.terminator.rule.css"), 186, 1);
		assertToken(Token.WHITESPACE, 187, 1);
		// line 7
		assertToken(getToken("punctuation.section.property-list.css"), 188, 1);
		assertToken(Token.WHITESPACE, 189, 2);
		// line 9
		assertToken(getToken("entity.other.attribute-name.class.css"), 191, 5);
		assertToken(Token.WHITESPACE, 196, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 197, 1);
		assertToken(Token.WHITESPACE, 198, 3);
		// line 10 border: 1px dotted #222222;
		assertToken(getToken("support.type.property-name.css"), 201, 6);
		assertToken(getToken("punctuation.separator.key-value.css"), 207, 1);
		assertToken(Token.WHITESPACE, 208, 1);
		assertToken(getToken("constant.numeric.css"), 209, 1);
		assertToken(getToken("keyword.other.unit.css"), 210, 2);
		assertToken(Token.WHITESPACE, 212, 1);
		assertToken(getToken("support.constant.property-value.css"), 213, 6);
		assertToken(Token.WHITESPACE, 219, 1);
		assertToken(getToken("constant.other.color.rgb-value.css"), 220, 7);
		assertToken(getToken("punctuation.terminator.rule.css"), 227, 1);
		assertToken(Token.WHITESPACE, 228, 3);
		// line 11 margin: 5px;
		assertToken(getToken("support.type.property-name.css"), 231, 6);
		assertToken(getToken("punctuation.separator.key-value.css"), 237, 1);
		assertToken(Token.WHITESPACE, 238, 1);
		assertToken(getToken("constant.numeric.css"), 239, 1);
		assertToken(getToken("keyword.other.unit.css"), 240, 2);
		assertToken(getToken("punctuation.terminator.rule.css"), 242, 1);
		assertToken(Token.WHITESPACE, 243, 1);
		// line 11
		assertToken(getToken("punctuation.section.property-list.css"), 244, 1);
		assertToken(Token.WHITESPACE, 245, 2);
		// line 13 .header {
		assertToken(getToken("entity.other.attribute-name.class.css"), 247, 7);
		assertToken(Token.WHITESPACE, 254, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 255, 1);
		assertToken(Token.WHITESPACE, 256, 3);
		// line 14 background-color: #FFFFFF;
		assertToken(getToken("support.type.property-name.css"), 259, 16);
		assertToken(getToken("punctuation.separator.key-value.css"), 275, 1);
		assertToken(Token.WHITESPACE, 276, 1);
		assertToken(getToken("constant.other.color.rgb-value.css"), 277, 7);
		assertToken(getToken("punctuation.terminator.rule.css"), 284, 1);
		assertToken(Token.WHITESPACE, 285, 3);
		// line 15 color: #444444;
		assertToken(getToken("support.type.property-name.css"), 288, 5);
		assertToken(getToken("punctuation.separator.key-value.css"), 293, 1);
		assertToken(Token.WHITESPACE, 294, 1);
		assertToken(getToken("constant.other.color.rgb-value.css"), 295, 7);
		assertToken(getToken("punctuation.terminator.rule.css"), 302, 1);
		assertToken(Token.WHITESPACE, 303, 3);
		// line 16 font-size: xx-large;
		assertToken(getToken("support.type.property-name.css"), 306, 9);
		assertToken(getToken("punctuation.separator.key-value.css"), 315, 1);
		assertToken(Token.WHITESPACE, 316, 1);
		assertToken(getToken("support.constant.property-value.css"), 317, 8);
		assertToken(getToken("punctuation.terminator.rule.css"), 325, 1);
		assertToken(Token.WHITESPACE, 326, 1);
		// line 17
		assertToken(getToken("punctuation.section.property-list.css"), 327, 1);
		assertToken(Token.WHITESPACE, 328, 2);
		// line 19 .menu {
		assertToken(getToken("entity.other.attribute-name.class.css"), 330, 5);
		assertToken(Token.WHITESPACE, 335, 1);
		assertToken(getToken("punctuation.section.property-list.css"), 336, 1);
		assertToken(Token.WHITESPACE, 337, 3);
		// line 20
	}
}
