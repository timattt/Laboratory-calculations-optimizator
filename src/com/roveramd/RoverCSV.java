// Ok, i haven't used Javadoc in years, please don't beat me up
// TODO: Turn the comments below -> Javadoc

package com.roveramd;

import java.io.*;
import java.util.*;


public class RoverCSV {
    private final ArrayList<Map<String, String>> readContents;
    private String[] headers;
    public String associatedFile;
    private String preferredDelimiter;
    
    //
	// This exception is thrown when the specified map representing a row does not
	// contain values for all of the columns that are declared in the first line of
	// the CSV file.
	//
	public class CSVMissingColumnValuesException extends Exception {
	    /**
		 * TODO write docs for this element
		 */
		private static final long serialVersionUID = -3639584737031827853L;

		public CSVMissingColumnValuesException(String nm) {
	        super(nm);
	    }
	}
	
    //
	// This exception is thrown when the file was not read during RoverCSV initia-
	// lization or when the file is corrupted.
	//
	public class CSVUnfinishedOrCorruptFileException extends Exception {
	    /**
		 * TODO write docs for this element
		 */
		private static final long serialVersionUID = -3324745637907547503L;

		public CSVUnfinishedOrCorruptFileException() {
	        super();
	    }
	}
	
	//
	// This exception is thrown only when the instance of RoverCSV is not associ-
	// ated with a specific CSV file.
	//
	public class CSVUnspecifiedTargetException extends Exception {
	    /**
		 * TODO write docs for this element
		 */
		private static final long serialVersionUID = 6961474877142084909L;

		public CSVUnspecifiedTargetException() {
	        super();
	     }
	}

   
    //
    // The constructor that creates you a new ameowzing instance of RoverCSV tied to
    // a specific CSV file (and, of course, reads its contents during startup).
    // 
    // Parameters:
    // - String fn - the CSV file to read
    // - String delimiter - the delimited used in the file. Usually, CSVs are delimited
    // with ";"s or ","s.
    //
    public RoverCSV(String fn, String delimiter) throws IOException, CSVUnfinishedOrCorruptFileException {
        File mf = new File(fn);
        associatedFile = fn;
        readContents = new ArrayList<>();
        headers = null;
        preferredDelimiter = delimiter;
        if (mf.exists())
            preprocessContents(fn, delimiter, (int)(mf.length()));
    }
    
    //
    // The constructor that creates you an instance of RoverCSV that can be used
    // to initialize an empty CSV with the specified headers.
    //
    public RoverCSV(String[] headersAvailable) {
        headers = headersAvailable;
        readContents = new ArrayList<>();
        associatedFile = null;
        preferredDelimiter = ",";
    }
    
    //
    // The constructor that creates you an instance of RoverCSV that can be used
    // to initialize an empty CSV with the specified headers.
    //
    public RoverCSV(Set<String> headersSet) {
    	headers = new String[headersSet.size()];
    	Iterator<String> itrtr = headersSet.iterator();
    	int index = -1;
    	while (itrtr.hasNext()) {
    		index += 1;
    		headers[index] = itrtr.next();
    	}
    	readContents = new ArrayList<>();
    	associatedFile = null;
    	preferredDelimiter = ",";
    }
    
    private void preprocessContents(String fn, String delimiter, int maxCharactersForPlainCSV) throws IOException, CSVUnfinishedOrCorruptFileException {
        String ctnt = "";
        try (FileReader ctntReader = new FileReader(fn)) {
            char[] arr = new char[maxCharactersForPlainCSV];
            ctntReader.read(arr);
            ctnt = (new String(arr)).replaceAll("\r\n", "\n");
        }
        String[] splitCtnt = ctnt.split("\n");
        if (splitCtnt.length < 2) 
            throw new CSVUnfinishedOrCorruptFileException();
        String heading = splitCtnt[0];
        String[] possibleHeadings = heading.split(delimiter);
        headers = possibleHeadings;
        for (int i = 1; i < splitCtnt.length; i++) {
            String subsequentItem = splitCtnt[i].replaceAll("@" + delimiter + "@", "@DLM@");
            String[] allSubsequentItems = subsequentItem.split(delimiter);
            Map<String, String> resultingMap = new HashMap<String, String>();
            for (int j = 0; j < allSubsequentItems.length; j++) {
                if (possibleHeadings.length <= j)
                    break;
                resultingMap.put(possibleHeadings[j], allSubsequentItems[j].replaceAll("@DLM@", delimiter));
            }
            readContents.add(resultingMap);
        }
    }
    
    public String[] getHeaders() {
    	return headers;
    }
    
    //
    // Method that allows you to retreive a list of rows that store data that
    // matches the specified patterns.
    //
    public List<Map<String, String>> getWhere(Map<String, String> columnCriterias) {
        Set<String> allColumnsSet = columnCriterias.keySet();
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < readContents.size(); i++) {
            Map<String, String> checkedMap = readContents.get(i);
            Iterator<String> itrtr = allColumnsSet.iterator();
            boolean matches = true;
            while (itrtr.hasNext()) {
                String columnName = itrtr.next();
                String awaitedValueBasically = columnCriterias.get(columnName);
                if (checkedMap.containsKey(columnName)) {
                    String vl = checkedMap.get(columnName);
                    if ((vl == null ? awaitedValueBasically != null : !vl.equals(awaitedValueBasically)) && !"*".equals(awaitedValueBasically)) {
                        matches = false;
                        break;
                    }
                } else {
                    matches = false;
                    break;
                }
            }
            if (matches)
                result.add(checkedMap);
        }
        return result;
    }
    
    public List<Map<String, String>> getAll() {
        return readContents;
    }
    
    private void verifyHeaderIntegrity(Map<String, String> mp) throws CSVUnfinishedOrCorruptFileException, CSVMissingColumnValuesException {
        if (headers == null)
            throw new CSVUnfinishedOrCorruptFileException();
        for (int i = 0; i < headers.length; i++) {
            if (!mp.containsKey(headers[i]))
                throw new CSVMissingColumnValuesException(headers[i]);
        }
    }
    
    public void setWhere(Map<String, String> pattern, Map<String, String> value) throws CSVMissingColumnValuesException  {
        try {
            verifyHeaderIntegrity(value);
        } catch (CSVUnfinishedOrCorruptFileException ex) {
            System.err.println("Unfinished or corrupt (or empty) CSV.");
            return;
        }
        List<Map<String, String>> matches = getWhere(pattern);
        Iterator<Map<String, String>> matchesIterator = matches.iterator();
        while (matchesIterator.hasNext()) {
            Map<String, String> rw = matchesIterator.next();
            int index = readContents.indexOf(rw);
            if (index >= 0) {
                rw.putAll(value);
                readContents.remove(index);
                readContents.add(index, rw);
            }
        }
    }
    
    public void add(Map<String, String> value) throws CSVMissingColumnValuesException, CSVUnfinishedOrCorruptFileException {
        verifyHeaderIntegrity(value);
        readContents.add(value);
    }
    
    private String joinHeaders() {
        if (headers == null)
            return "";
        String result = "";
        boolean first = true;
        for (int i = 0; i < headers.length; i++) {
            result = first ? headers[i] : (result + preferredDelimiter + headers[i]);
            first = false;
        }
        return result;
    }
    
    private String joinSingleRow(Map<String, String> rw) throws CSVUnfinishedOrCorruptFileException {
        if (headers == null)
            throw new CSVUnfinishedOrCorruptFileException();
        String result = "";
        boolean first = true;
        for (int i = 0; i < headers.length; i++) {
            String hdr = headers[i];
            if (!rw.containsKey(hdr))
                throw new CSVUnfinishedOrCorruptFileException();
            result = first ? rw.get(hdr) : (result + preferredDelimiter + rw.get(hdr));
            first = false;
        }
        return result;
    }
    
    public String join() throws CSVUnfinishedOrCorruptFileException {
        if (headers == null)
            throw new CSVUnfinishedOrCorruptFileException();
        String kickstart = joinHeaders();
        Iterator<Map<String, String>> itrtr = readContents.iterator();
        while (itrtr.hasNext()) {
            Map<String, String> row = itrtr.next();
            kickstart += "\n" + joinSingleRow(row);
        }
        return kickstart;
    }
    
    public void save() throws IOException, CSVUnspecifiedTargetException, CSVUnfinishedOrCorruptFileException {
        if (headers == null)
            throw new CSVUnfinishedOrCorruptFileException();
        else if (associatedFile == null)
            throw new CSVUnspecifiedTargetException();
        FileWriter wrt = new FileWriter(associatedFile);
        wrt.write(join());
        wrt.flush();
        wrt.close();
    }
}