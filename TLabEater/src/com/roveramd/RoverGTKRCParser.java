package com.roveramd;

import java.io.*;
import java.util.*;
import static com.roveramd.RoverLabLangSyntaxHighlighter.respectiveSplit;

public class RoverGTKRCParser {
	private Map<String, String> stringProperties = new HashMap<>();
	private Map<String, Boolean> booleanProperties = new HashMap<>();
	private Map<String, Integer> integerProperties = new HashMap<>();
	private String configPath = "";
	
	public RoverGTKRCParser(String path) throws FileNotFoundException, IOException {
		if (! new File(path).exists())
			throw new FileNotFoundException();
		configPath = path;
		readOut();
	}
	
	private void readOut() throws IOException {
		FileReader rdr = new FileReader(configPath);
		int size = (int)(new File(configPath).length());
		char[] buf = new char[size];
		rdr.read(buf);
		rdr.close();
		String ctntRaw = new String(buf).replaceAll("\r\n", "\n");
		List<String> iterableCtnt = respectiveSplit(ctntRaw, '\n');
		Iterator<String> lineIterator = iterableCtnt.iterator();
		while (lineIterator.hasNext()) {
			String line = lineIterator.next();
			List<String> lineSplit = respectiveSplit(line, '=');
			if (lineSplit.size() >= 2) {
				String key = lineSplit.get(0).trim();
				String valueRaw = lineSplit.get(1).trim();
				if (valueRaw.length() >= 2 && valueRaw.charAt(0) == '"' && valueRaw.charAt(valueRaw.length() - 1) == '"')
					stringProperties.put(key, valueRaw.substring(1, valueRaw.length() - 1));
				else if (valueRaw.equals("yes") || valueRaw.equals("no") || valueRaw.equals("true") || valueRaw.equals("false") || valueRaw.equals("meow") || valueRaw.equals("bark")) {
					boolean vl = (valueRaw.equals("yes") || valueRaw.equals("true") || valueRaw.equals("meow"));
					booleanProperties.put(key, vl);
				} else {
					int vl = Integer.parseInt(valueRaw);
					integerProperties.put(key, vl);
				}
			}
		}
	}
	
	public boolean containsString(String label) {
		return stringProperties.containsKey(label);
	}
	
	public boolean containsInteger(String label) {
		return integerProperties.containsKey(label);
	}
	
	public boolean containsBoolean(String label) {
		return booleanProperties.containsKey(label);
	}
	
	public boolean getBoolean(String label) {
		if (containsBoolean(label))
			return booleanProperties.get(label);
		return false;
	}
	
	public String getString(String label) {
		if (containsString(label))
			return stringProperties.get(label);
		return null;
	}
	
	public int getInteger(String label) {
		if (containsInteger(label))
			return integerProperties.get(label);
		return -1;
	}
}
