package com.roveramd;

import java.util.*;

public class RoverLabLangSyntaxHighlighter {
	private String highlightResult = "";
	private String originalText = "";
	private String quoteColor = "#7CDC59";
	private String inaccColor = "#DCB559";
	private String numbersColor = "#5C6E9E";
	private String debugColor = "#597CDC";
	private String variableColor = "#754B4B";
	private String otherColor = "#000000";
	private String commentColor = "#C1C3CA";
	private String backgroundColor = "#FFFFFF";
	private int preferredFontSize = 14;
	
	//
	// Creates a new instance of RoverLabLangSyntaxHighlighter class - a class
	// that can generate HTML page representing the highlighted HTML code and
	// rendered in the source code browser.
	//
	public RoverLabLangSyntaxHighlighter(String input, int fontSize) {
		preferredFontSize = fontSize;
		setText(input);
	}
	
	//
	// Modifies the color palette used.
	//
	public void setColorScheme(String highlight, String quote, String inacc, String nums, String debug, String vbl, String bg, String other) {
		variableColor = vbl;
		commentColor = highlight;
		quoteColor = quote;
		inaccColor = inacc;
		numbersColor = nums;
		debugColor = debug;
		backgroundColor = bg;
		otherColor = other;
	}
	
	//
	// Makes the class store not only the original source code, but
	// also generate and store the highlighted version of it.
	//
	public void setText(String in) {
		highlightResult = parseUp(in);
		originalText = in;
	}
	
	// 
	// Returns the highlighted version of the source code.
	//
	public String toHTML() {
		return highlightResult;
	}
	
	// 
	// Returns the original version of the source code.
	//
	public String toString() {
		return originalText;
	}
	
	public static List<String> respectiveSplit(String input, char delim) {
		boolean insideQuotes = false;
		String appnd = "";
		List<String> result = new ArrayList<>();
		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);
			char previousChar = (i >= 1) ? input.charAt(i - 1) : '\n';
			if (previousChar != '\\') {
				if (currentChar == '"')
					insideQuotes = !insideQuotes;
				else if (currentChar == delim && !insideQuotes) {
					result.add(new String(appnd));
					appnd = "";
					continue;
				}
			}
			appnd += currentChar;
		}
		if (appnd.length() >= 1)
			result.add(appnd);
		return result;
	}
	
	private String joinList(List<String> input, String delim) {
		String result = "";
		boolean first = true;
		Iterator<String> itrtr = input.iterator();
		while (itrtr.hasNext()) {
			String itm = itrtr.next();
			result += first ? itm : (delim + itm);
			first = false;
		}
		return result;
	}
	
	private String highlightQuotes(String input) {
		boolean insideQuotes = false;
		String result = "";
		for (int i = 0; i < input.length(); i++) {
			char thisChar = input.charAt(i);
			char previousChar = (i < 1) ? '\n' : input.charAt(i - 1);
			if (previousChar != '\\') {
				if (thisChar == '"') {
					insideQuotes = !insideQuotes;
					if (insideQuotes)
						result += "<span style=\"color: " + quoteColor + "\"><em><b>\"</b>";
					else
						result += "<b>\"</b></span>";
					continue;
				}
			}
			result += thisChar;
		}
		return result;
	}
	
	private String xmledQuotedLine(String line) {
		return line.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
	}
	
	private String parseUp(String oinput) {
		String input = oinput.replaceAll("<html>", "").replaceAll("</html>", "").replaceAll("</head>", "").replaceAll("<head>", "").replaceAll("</body>", "").replaceAll("<body>", "");
		List<String> iterableSplitInput = respectiveSplit(input, '\n');
		System.err.println("iterableSplitInput = \n" + iterableSplitInput);
		Iterator<String> comfortIterator = iterableSplitInput.iterator();
		List<String> resultToFlush = new ArrayList<>();
		while (comfortIterator.hasNext()) {
			String line = comfortIterator.next();
			if (line.startsWith("//")) {
				resultToFlush.add("<span style=\"color: " + commentColor + ";\"><em>" + xmledQuotedLine(line) + "</em></span>");
				continue;
			}
			line = highlightQuotes(xmledQuotedLine(line));
			String actualNewLine = "";
			for (int i = 0; i < line.length(); i++) {
				char thisChar = line.charAt(i);
				if (thisChar == '#')
					actualNewLine += "<span style=\"color: " + inaccColor + "\"><b>" + thisChar + "</b></span>";
				else if (thisChar == '$')
					actualNewLine += "<span style=\"color: " + debugColor + "\"><b>" + thisChar + "</b></span>";
				else if (Character.isDigit(thisChar) || thisChar == '.')
					actualNewLine += "<span style=\"color: " + numbersColor + "\">" + thisChar + "</span>";
				else
					actualNewLine += thisChar;
			}
			resultToFlush.add(actualNewLine);
		}
		String result = "<div style=\"font-size: " + preferredFontSize + "; background-color: " + backgroundColor + "; color: " + otherColor + "\">" + joinList(resultToFlush, "<br>") + "</div>";
		System.err.println("Converted result:\n" + result);
		return result;
	}
}
