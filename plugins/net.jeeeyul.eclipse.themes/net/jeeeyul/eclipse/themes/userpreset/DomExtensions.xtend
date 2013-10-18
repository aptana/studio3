package net.jeeeyul.eclipse.themes.userpreset

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Attr
import org.w3c.dom.CDATASection
import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory

class DomExtensions {
	def Element newElement(Node parent, String name, (Node)=>void initializer){
		var node = parent.document.createElement(name)
		parent.appendChild(node)
		initializer.apply(node)
		return node
	}
	
	def Attr newAttribute(Node parent, String name, String value){
		var node = parent.document.createAttribute(name)
		node.setValue(value)
		parent.attributes.setNamedItem(node)
		return node
	}
	
	def CDATASection newCData(Node parent, String data){
		var node = parent.document.createCDATASection(data)
		parent.appendChild(node)
		return node
	}
	
	def Document getDocument(Node node){
		var Node finger = node;
		
		while(finger != null){
			if(finger instanceof Document){
				return finger as Document;
			}	
			finger = finger.parentNode
		}
		
		return null
	}
	
	def Document newDocument((Document)=>void initializer){
		var factory = DocumentBuilderFactory::newInstance()
		var builder = factory.newDocumentBuilder
		val doc = builder.newDocument()
		
		initializer.apply(doc)
		
		return doc
	}
}