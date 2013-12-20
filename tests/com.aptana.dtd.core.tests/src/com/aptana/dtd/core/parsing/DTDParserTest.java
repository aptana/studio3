/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import beaver.Parser.Exception;

import com.aptana.dtd.core.parsing.ast.DTDAndExpressionNode;
import com.aptana.dtd.core.parsing.ast.DTDAttListDeclNode;
import com.aptana.dtd.core.parsing.ast.DTDAttributeNode;
import com.aptana.dtd.core.parsing.ast.DTDElementDeclNode;
import com.aptana.dtd.core.parsing.ast.DTDElementNode;
import com.aptana.dtd.core.parsing.ast.DTDEnumerationTypeNode;
import com.aptana.dtd.core.parsing.ast.DTDGeneralEntityDeclNode;
import com.aptana.dtd.core.parsing.ast.DTDNodeType;
import com.aptana.dtd.core.parsing.ast.DTDNotationDeclNode;
import com.aptana.dtd.core.parsing.ast.DTDNotationTypeNode;
import com.aptana.dtd.core.parsing.ast.DTDOneOrMoreNode;
import com.aptana.dtd.core.parsing.ast.DTDOptionalNode;
import com.aptana.dtd.core.parsing.ast.DTDOrExpressionNode;
import com.aptana.dtd.core.parsing.ast.DTDParsedEntityDeclNode;
import com.aptana.dtd.core.parsing.ast.DTDProcessingInstructionNode;
import com.aptana.dtd.core.parsing.ast.DTDTypeNode;
import com.aptana.dtd.core.parsing.ast.DTDZeroOrMoreNode;
import com.aptana.parsing.ast.IParseNode;

public class DTDParserTest
{
	@Test
	public void testEmptyElement()
	{
		String source = "<!ELEMENT svg EMPTY>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.EMPTY);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();

		assertEquals("svg", elementDecl.getName());
	}

	@Test
	public void testAnyElement()
	{
		String source = "<!ELEMENT svg ANY>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ANY);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();

		assertEquals("svg", elementDecl.getName());
	}

	@Test
	public void testPCDataElement()
	{
		String source = "<!ELEMENT svg (#PCDATA)>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.PCDATA);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();

		assertEquals("svg", elementDecl.getName());
	}

	@Test
	public void testZeroOrMorePCDataElement()
	{
		String source = "<!ELEMENT svg (#PCDATA)*>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ZERO_OR_MORE,
				DTDNodeType.PCDATA);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();

		assertEquals("svg", elementDecl.getName());
	}

	@Test
	public void testZeroOrMorePCDataAndNamesElement()
	{
		String source = "<!ELEMENT svg (#PCDATA | circle)*>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ZERO_OR_MORE,
				DTDNodeType.PCDATA, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode kleene = elementDecl.getFirstChild();
		assertNotNull(kleene);

		IParseNode element = kleene.getLastChild();
		assertNotNull(element);
		assertTrue(element instanceof DTDElementNode);

		assertEquals("circle", ((DTDElementNode) element).getName());
	}

	@Test
	public void testSingleChild()
	{
		String source = "<!ELEMENT svg (circle)>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode element = elementDecl.getFirstChild();
		assertNotNull(element);
		assertTrue(element instanceof DTDElementNode);

		assertEquals("circle", ((DTDElementNode) element).getName());
	}

	@Test
	public void testZeroOrMoreSingleChild()
	{
		String source = "<!ELEMENT svg (circle)*>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ZERO_OR_MORE,
				DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode kleene = elementDecl.getFirstChild();
		assertNotNull(kleene);

		IParseNode element = kleene.getFirstChild();
		assertNotNull(element);
		assertTrue(element instanceof DTDElementNode);

		assertEquals("circle", ((DTDElementNode) element).getName());
	}

	@Test
	public void testOneOrMoreSingleChild()
	{
		String source = "<!ELEMENT svg (circle)+>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ONE_OR_MORE,
				DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode positive = elementDecl.getFirstChild();
		assertNotNull(positive);

		IParseNode element = positive.getFirstChild();
		assertNotNull(element);
		assertTrue(element instanceof DTDElementNode);

		assertEquals("circle", ((DTDElementNode) element).getName());
	}

	@Test
	public void testOptionalSingleChild()
	{
		String source = "<!ELEMENT svg (circle)?>";

		IParseNode root = this
				.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.OPTIONAL, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode optional = elementDecl.getFirstChild();
		assertNotNull(optional);

		IParseNode element = optional.getFirstChild();
		assertNotNull(element);
		assertTrue(element instanceof DTDElementNode);

		assertEquals("circle", ((DTDElementNode) element).getName());
	}

	@Test
	public void testOrExpression()
	{
		String source = "<!ELEMENT svg (circle | ellipse)>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.OR_EXPRESSION,
				DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode orExpr = elementDecl.getFirstChild();
		assertTrue(orExpr instanceof DTDOrExpressionNode);
		assertEquals(2, orExpr.getChildCount());

		IParseNode circle = orExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = orExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testZeroOrMoreOrExpression()
	{
		String source = "<!ELEMENT svg (circle | ellipse)*>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ZERO_OR_MORE,
				DTDNodeType.OR_EXPRESSION, DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode zom = elementDecl.getFirstChild();
		assertTrue(zom instanceof DTDZeroOrMoreNode);

		IParseNode orExpr = zom.getFirstChild();
		assertTrue(orExpr instanceof DTDOrExpressionNode);
		assertEquals(2, orExpr.getChildCount());

		IParseNode circle = orExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = orExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testOneOrMoreOrExpression()
	{
		String source = "<!ELEMENT svg (circle | ellipse)+>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ONE_OR_MORE,
				DTDNodeType.OR_EXPRESSION, DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode oom = elementDecl.getFirstChild();
		assertTrue(oom instanceof DTDOneOrMoreNode);

		IParseNode orExpr = oom.getFirstChild();
		assertTrue(orExpr instanceof DTDOrExpressionNode);
		assertEquals(2, orExpr.getChildCount());

		IParseNode circle = orExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = orExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testOptionalOrExpression()
	{
		String source = "<!ELEMENT svg (circle | ellipse)?>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.OPTIONAL,
				DTDNodeType.OR_EXPRESSION, DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode opt = elementDecl.getFirstChild();
		assertTrue(opt instanceof DTDOptionalNode);

		IParseNode orExpr = opt.getFirstChild();
		assertTrue(orExpr instanceof DTDOrExpressionNode);
		assertEquals(2, orExpr.getChildCount());

		IParseNode circle = orExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = orExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testAndExpression()
	{
		String source = "<!ELEMENT svg (circle, ellipse)>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.AND_EXPRESSION,
				DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode andExpr = elementDecl.getFirstChild();
		assertTrue(andExpr instanceof DTDAndExpressionNode);
		assertEquals(2, andExpr.getChildCount());

		IParseNode circle = andExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = andExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testZeroOrMoreAndExpression()
	{
		String source = "<!ELEMENT svg (circle, ellipse)*>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ZERO_OR_MORE,
				DTDNodeType.AND_EXPRESSION, DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode zom = elementDecl.getFirstChild();
		assertTrue(zom instanceof DTDZeroOrMoreNode);

		IParseNode andExpr = zom.getFirstChild();
		assertTrue(andExpr instanceof DTDAndExpressionNode);
		assertEquals(2, andExpr.getChildCount());

		IParseNode circle = andExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = andExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testOneOrMoreAndExpression()
	{
		String source = "<!ELEMENT svg (circle, ellipse)+>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.ONE_OR_MORE,
				DTDNodeType.AND_EXPRESSION, DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode oom = elementDecl.getFirstChild();
		assertTrue(oom instanceof DTDOneOrMoreNode);

		IParseNode andExpr = oom.getFirstChild();
		assertTrue(andExpr instanceof DTDAndExpressionNode);
		assertEquals(2, andExpr.getChildCount());

		IParseNode circle = andExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = andExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testOptionalAndExpression()
	{
		String source = "<!ELEMENT svg (circle, ellipse)?>";

		IParseNode root = this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.OPTIONAL,
				DTDNodeType.AND_EXPRESSION, DTDNodeType.ELEMENT, DTDNodeType.ELEMENT);

		DTDElementDeclNode elementDecl = (DTDElementDeclNode) root.getFirstChild();
		assertEquals("svg", elementDecl.getName());

		IParseNode opt = elementDecl.getFirstChild();
		assertTrue(opt instanceof DTDOptionalNode);

		IParseNode andExpr = opt.getFirstChild();
		assertTrue(andExpr instanceof DTDAndExpressionNode);
		assertEquals(2, andExpr.getChildCount());

		IParseNode circle = andExpr.getFirstChild();
		assertTrue(circle instanceof DTDElementNode);
		assertEquals("circle", ((DTDElementNode) circle).getName());

		IParseNode ellipse = andExpr.getLastChild();
		assertTrue(ellipse instanceof DTDElementNode);
		assertEquals("ellipse", ((DTDElementNode) ellipse).getName());
	}

	@Test
	public void testNestedAndExpression()
	{
		String source = "<!ELEMENT svg ((circle, ellipse), (rectangle, path))>";

		this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.AND_EXPRESSION, DTDNodeType.AND_EXPRESSION,
				DTDNodeType.ELEMENT, DTDNodeType.ELEMENT, DTDNodeType.AND_EXPRESSION, DTDNodeType.ELEMENT,
				DTDNodeType.ELEMENT);
	}

	@Test
	public void testNestedOrExpression()
	{
		String source = "<!ELEMENT svg ((circle | ellipse) | (rectangle | path))>";

		this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.OR_EXPRESSION, DTDNodeType.OR_EXPRESSION,
				DTDNodeType.ELEMENT, DTDNodeType.ELEMENT, DTDNodeType.OR_EXPRESSION, DTDNodeType.ELEMENT,
				DTDNodeType.ELEMENT);
	}

	@Test
	public void testMixedExpression()
	{
		String source = "<!ELEMENT svg ((circle | ellipse), (rectangle | path))>";

		this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.AND_EXPRESSION, DTDNodeType.OR_EXPRESSION,
				DTDNodeType.ELEMENT, DTDNodeType.ELEMENT, DTDNodeType.OR_EXPRESSION, DTDNodeType.ELEMENT,
				DTDNodeType.ELEMENT);
	}

	@Test
	public void testMixedExpression2()
	{
		String source = "<!ELEMENT svg ((circle, ellipse) | (rectangle, path))>";

		this.parse(source, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.OR_EXPRESSION, DTDNodeType.AND_EXPRESSION,
				DTDNodeType.ELEMENT, DTDNodeType.ELEMENT, DTDNodeType.AND_EXPRESSION, DTDNodeType.ELEMENT,
				DTDNodeType.ELEMENT);
	}

	@Test
	public void testEmptyAttList()
	{
		String source = "<!ATTLIST svg>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());
	}

	@Test
	public void testRequiredStringAttribute()
	{
		String source = "<!ATTLIST svg name CDATA #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("CDATA", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredIDAttribute()
	{
		String source = "<!ATTLIST svg name ID #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("ID", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredIDRefAttribute()
	{
		String source = "<!ATTLIST svg name IDREF #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("IDREF", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredIDRefsAttribute()
	{
		String source = "<!ATTLIST svg name IDREFS #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("IDREFS", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredEntityAttribute()
	{
		String source = "<!ATTLIST svg name ENTITY #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("ENTITY", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredEntitiesAttribute()
	{
		String source = "<!ATTLIST svg name ENTITIES #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("ENTITIES", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredNMTokenAttribute()
	{
		String source = "<!ATTLIST svg name NMTOKEN #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("NMTOKEN", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredNMTokensAttribute()
	{
		String source = "<!ATTLIST svg name NMTOKENS #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("NMTOKENS", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testRequiredNotationAttribute()
	{
		String source = "<!ATTLIST svg name NOTATION(abc) #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.NOTATION);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDNotationTypeNode);
	}

	@Test
	public void testRequiredEnumerationAttribute()
	{
		String source = "<!ATTLIST svg name (abc) #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.ENUMERATION);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDEnumerationTypeNode);
	}

	@Test
	public void testRequiredEnumerationAttribute2()
	{
		String source = "<!ATTLIST svg name (abc | def) #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.ENUMERATION);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDEnumerationTypeNode);
	}

	@Test
	public void testRequiredEnumerationAttribute3()
	{
		String source = "<!ATTLIST svg font-weight (bold | 100) #REQUIRED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.ENUMERATION);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("font-weight", ((DTDAttributeNode) att).getName());
		assertEquals("#REQUIRED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDEnumerationTypeNode);
	}

	@Test
	public void testImpliedStringAttribute()
	{
		String source = "<!ATTLIST svg name CDATA #IMPLIED>";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#IMPLIED", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("CDATA", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testStringAttribute()
	{
		String source = "<!ATTLIST svg name CDATA \"default\">";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("default", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("CDATA", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testFixedStringAttribute()
	{
		String source = "<!ATTLIST svg name CDATA #FIXED \"default\">";

		IParseNode root = this.parse(source, DTDNodeType.ATTRIBUTE_LIST_DECLARATION, DTDNodeType.ATTRIBUTE,
				DTDNodeType.TYPE);

		DTDAttListDeclNode attListDecl = (DTDAttListDeclNode) root.getFirstChild();
		assertEquals("svg", attListDecl.getName());

		IParseNode att = attListDecl.getFirstChild();
		assertTrue(att instanceof DTDAttributeNode);
		assertEquals("name", ((DTDAttributeNode) att).getName());
		assertEquals("#FIXED default", ((DTDAttributeNode) att).getMode());

		IParseNode type = att.getFirstChild();
		assertTrue(type instanceof DTDTypeNode);
		assertEquals("CDATA", ((DTDTypeNode) type).getType());
	}

	@Test
	public void testGeneralEntityString()
	{
		String source = "<!ENTITY test \"hello\">";

		IParseNode root = this.parse(source, DTDNodeType.G_ENTITY_DECLARATION);

		DTDGeneralEntityDeclNode entity = (DTDGeneralEntityDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test value
	}

	@Test
	public void testGeneralEntitySystemString()
	{
		String source = "<!ENTITY test SYSTEM \"hello\">";

		IParseNode root = this.parse(source, DTDNodeType.G_ENTITY_DECLARATION);

		DTDGeneralEntityDeclNode entity = (DTDGeneralEntityDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test values
	}

	@Test
	public void testGeneralEntityExternalPublicString()
	{
		String source = "<!ENTITY test PUBLIC \"hello\" \"world\">";

		IParseNode root = this.parse(source, DTDNodeType.G_ENTITY_DECLARATION);

		DTDGeneralEntityDeclNode entity = (DTDGeneralEntityDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test values
	}

	@Test
	public void testGeneralEntityExternalIDWithNDataDeclaration()
	{
		String source = "<!ENTITY test SYSTEM \"hello\" NDATA world>";

		IParseNode root = this.parse(source, DTDNodeType.G_ENTITY_DECLARATION, DTDNodeType.NDATA_DECLARATION);

		DTDGeneralEntityDeclNode entity = (DTDGeneralEntityDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test values
	}

	@Test
	public void testParsedEntityString()
	{
		String source = "<!ENTITY % test \"hello\">";

		IParseNode root = this.parse(source, DTDNodeType.P_ENTITY_DECLARATION);

		DTDParsedEntityDeclNode entity = (DTDParsedEntityDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test value
	}

	@Test
	public void testParsedEntitySystemString()
	{
		String source = "<!ENTITY % test SYSTEM \"hello\">";

		IParseNode root = this.parse(source, DTDNodeType.P_ENTITY_DECLARATION);

		DTDParsedEntityDeclNode entity = (DTDParsedEntityDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test values
	}

	@Test
	public void testParsedEntityPublicString()
	{
		String source = "<!ENTITY % test PUBLIC \"hello\" \"world\">";

		IParseNode root = this.parse(source, DTDNodeType.P_ENTITY_DECLARATION);

		DTDParsedEntityDeclNode entity = (DTDParsedEntityDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test values
	}

	@Test
	public void testNotationDeclarationSystemString()
	{
		String source = "<!NOTATION test SYSTEM \"hello\">";

		IParseNode root = this.parse(source, DTDNodeType.NOTATION_DECLARATION);

		DTDNotationDeclNode entity = (DTDNotationDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test value
	}

	@Test
	public void testNotationDeclarationPublicString()
	{
		String source = "<!NOTATION test PUBLIC \"hello\" \"world\">";

		IParseNode root = this.parse(source, DTDNodeType.NOTATION_DECLARATION);

		DTDNotationDeclNode entity = (DTDNotationDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test value
	}

	@Test
	public void testNotationDeclarationPublicID()
	{
		String source = "<!NOTATION test PUBLIC \"id\">";

		IParseNode root = this.parse(source, DTDNodeType.NOTATION_DECLARATION);

		DTDNotationDeclNode entity = (DTDNotationDeclNode) root.getFirstChild();
		assertEquals("test", entity.getName());

		// TODO: test value
	}

	@Test
	public void testProcessingInstruction()
	{
		String source = "<?TEST this is a test?>";

		IParseNode root = this.parse(source, DTDNodeType.PROCESSING_INSTRUCTION);

		DTDProcessingInstructionNode pi = (DTDProcessingInstructionNode) root.getFirstChild();
		assertEquals("TEST this is a test", pi.getText());
	}

	@Test
	public void testEmptyIncludeSection()
	{
		String source = "<![INCLUDE[]]>";

		this.parse(source, DTDNodeType.INCLUDE_SECTION);
	}

	@Test
	public void testSimpleIncludeSection()
	{
		String source = "<![INCLUDE[\n<!ELEMENT svg EMPTY>\n]]>";

		this.parse(source, DTDNodeType.INCLUDE_SECTION, DTDNodeType.ELEMENT_DECLARATION, DTDNodeType.EMPTY);
	}

	@Test
	public void testEmptyIgnoreSection()
	{
		String source = "<![IGNORE[]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	@Test
	public void testSimpleIgnoreSection()
	{
		String source = "<![IGNORE[\n<!ELEMENT svg EMPTY>\n]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	@Test
	public void testNestedIgnoreSectionAfter()
	{
		String source = "<![IGNORE[\n<!ELEMENT svg EMPTY>\n<![IGNORE[]]>]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	@Test
	public void testNestedIncludeSectionAfter()
	{
		String source = "<![IGNORE[\n<!ELEMENT svg EMPTY>\n<![INCLUDE[]]>]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	@Test
	public void testNestedIgnoreSectionBefore()
	{
		String source = "<![IGNORE[<![IGNORE[]]>\n<!ELEMENT svg EMPTY>\n]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	@Test
	public void testNestedIncludeSectionBefore()
	{
		String source = "<![IGNORE[<![INCLUDE[]]>\n<!ELEMENT svg EMPTY>\n]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	@Test
	public void testNestedIgnoreSectionBeforeAndAfter()
	{
		String source = "<![IGNORE[<![IGNORE[]]>\n<!ELEMENT svg EMPTY>\n<![IGNORE[]]>]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	@Test
	public void testNestedIncludeSectionBeforeAndAfter()
	{
		String source = "<![IGNORE[<![INCLUDE[]]>\n<!ELEMENT svg EMPTY>\n<![INCLUDE[]]>]]>";

		this.parse(source, DTDNodeType.IGNORE_SECTION);
	}

	/**
	 * parse
	 * 
	 * @param source
	 * @param types
	 * @throws IOException
	 * @throws Exception
	 */
	protected IParseNode parse(String source, DTDNodeType... types)
	{
		// create parser
		DTDParser parser = new DTDParser();

		// create scanner and associate source
		DTDScanner scanner = new DTDScanner();
		scanner.setSource(source);

		// parse it
		IParseNode result = null;

		try
		{
			result = (IParseNode) parser.parse(scanner);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}

		// make sure we got something
		assertNotNull(result);

		IParseNode current = result;

		// check node types
		for (DTDNodeType type : types)
		{
			current = current.getNextNode();

			assertNotNull(current);
			assertEquals(type.getIndex(), current.getNodeType());
		}

		return result;
	}
}
