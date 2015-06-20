package ca.mestevens.ios.xcode.parser.utils;

import java.util.ArrayList;
import java.util.List;

import ca.mestevens.ios.xcode.parser.models.CommentedIdentifier;

public class ObjectParser {
	
	private String body;
	
	public ObjectParser(final String body) {
		if (body == null) {
			this.body = "";
		} else {
			this.body = body.trim();
		}
		removeSurroundingComments();
	}
	
	public String parseNextObject() {
		removeSurroundingComments();
		int nestLevel = 0;
		String returnString = "";
		int index = 0;
		for(char c : body.toCharArray()) {
			returnString += c;
			index++;
			if (c == ';' && nestLevel == 0) {
				body = body.substring(index).trim();
				return returnString.trim();
			}
			if (c == '{') {
				nestLevel++;
			}
			if (c == '}') {
				nestLevel--;
			}
		}
		return null;
	}
	
	public ObjectParser getNextNestedObjects() {
		removeSurroundingComments();
		int startIndex = this.body.indexOf('{');
		String newBody = this.body.substring(startIndex + 1).trim();
		return new ObjectParser(newBody);
	}
	
	private void removeSurroundingComments() {
		while(this.body.startsWith("/*")) {
			int indexOfCommentEnd = this.body.indexOf("*/");
			this.body = this.body.substring(indexOfCommentEnd + 2).trim();
		}
		while (this.body.endsWith("*/")) {
			while (!this.body.endsWith("/*")) {
				this.body = this.body.substring(0, this.body.length() - 1).trim();
			}
			this.body = this.body.substring(0, this.body.length() - 2).trim();
		}
	}
	
	public CommentedIdentifier getCommentedIdentifier(String value) {
		CommentedIdentifier commentedIdentifier = null;
		if (value.contains("/*")) {
			String valueValue = value.substring(0, value.indexOf("/*")).trim();
			String valueComment = value.substring(value.indexOf("/*") + 2).trim();
			valueComment = valueComment.substring(0, valueComment.indexOf("*/")).trim();
			commentedIdentifier = new CommentedIdentifier(valueValue, valueComment);
		} else {
			commentedIdentifier = new CommentedIdentifier(value, null);
		}
		return commentedIdentifier;
	}
	
	public List<String> getStringList(String value) {
		value = value.substring(1, value.indexOf(')'));
		List<String> list = new ArrayList<String>();
		if (!value.trim().equals("")) {
			String[] values = value.split(",");
			for(String listValue : values) {
				listValue = listValue.trim();
				if (!listValue.equals("")) {
					list.add(listValue);
				}
			}
		}
		return list;
	}
	
	public List<CommentedIdentifier> getIdentifierList(String value) {
		String commentPart = "";
		value = value.substring(1, value.indexOf(')'));
		List<CommentedIdentifier> list = new ArrayList<CommentedIdentifier>();
		if (!value.trim().equals("")) {
			String[] values = value.split(",");
			for(String listValue : values) {
				listValue = listValue.trim();
				if (!listValue.equals("")) {
					if (listValue.contains("/*")) {
						int commentStartIndex = listValue.indexOf("/*");
						int commentEndIndex = listValue.indexOf("*/");
						commentPart = listValue.substring(commentStartIndex + 2, commentEndIndex).trim();
						listValue = listValue.substring(0, commentStartIndex).trim();
					}
					list.add(new CommentedIdentifier(listValue, commentPart));
				}
			}
		}
		return list;
	}

}
