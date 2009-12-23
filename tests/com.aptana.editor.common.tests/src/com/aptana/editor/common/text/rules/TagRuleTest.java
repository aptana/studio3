package com.aptana.editor.common.text.rules;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.TagRule;

public class TagRuleTest extends TestCase
{

	public void testSimpleOpenTag()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris>");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}

	public void testOpenTagWithAttribute()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr='value'>");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}

	public void testSelfClosingTagWithAttribute()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr='value' />");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}

	public void testTagWithEndBraceInsideSingleQuotedString()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr='>'>");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}

	public void testTagWithEndBraceInsideDoubleQuotedString()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr=\">value\">");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}

	public void testTagWithEndBraceInsideMultiLineDoubleQuotedString()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr=\">\nvalue\">");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}
	
	public void testTagWithEndBraceInsideMultiLineDoubleQuotedStringContainingEscapes()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr=\">\nvalue\\\">\">");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}

	public void testTagWithEndBraceInsideMultiLineSingleQuotedString()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr='>\nvalue'>");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}
	
	public void testTagWithEndBraceInsideMultiLineSingleQuotedStringContainingEscapes()
	{
		IToken successToken = new Token("name");
		TagRule rule = new TagRule("chris", successToken);
		RuleBasedScanner scanner = new RuleBasedScanner();
		IDocument document = new Document("<chris attr='>\nvalue\\'>'>");
		scanner.setRange(document, 0, document.getLength());
		assertEquals(successToken, rule.evaluate(scanner));
		assertEquals(0, scanner.getTokenOffset());
		assertEquals(document.getLength(), scanner.getTokenLength());
	}
}
