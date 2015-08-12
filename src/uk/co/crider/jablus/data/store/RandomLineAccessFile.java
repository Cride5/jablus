package uk.co.crider.jablus.data.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Map;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/** Allows simultaneous line-wise reading and writing of data
 * Read access is random, while write access is sequential */
public class RandomLineAccessFile {

	private RandomAccessFile file;
	private Hashtable<Integer, Long> linePointers;
	private int nextLine;
	private boolean randomReadable;
	private boolean closed;
	
//	String lineSeparator = (String) java.security.AccessController.doPrivileged(
//			new sun.security.action.GetPropertyAction("line.separator"));
	
	/** Constructs a random read access file, reading the entire contents and */
	public RandomLineAccessFile(File f) throws FileNotFoundException, IOException {
		file = new RandomAccessFile(f, "rwd");
		linePointers = new Hashtable<Integer, Long>();
		nextLine = 0;
		randomReadable = false;
		closed = false;
    }
	
	RandomAccessFile getFile(){
		return file;
	}

	/** Reads the given line, or returns null if the line does not exist */
	public String readLine(int line) throws IOException{
		if(!randomReadable)
			scanLines();
		if(!linePointers.containsKey(line))
			return null;
		long lineAddr = linePointers.get(line);
//System.out.println("Reading from addr:" + lineAddr);
		file.seek(lineAddr);
		return file.readLine();
	}
	
	/** Scans file for line pointers, if it hasn't already been done */
	private void scanLines() throws IOException{
System.out.println("Scanning...");
		file.seek(0);
		int i = 0;
		long addr = 0;
		long eof = file.length();
		do{
			linePointers.put(i++, addr);
			file.readLine();
		}
		while((addr = file.getFilePointer()) < eof);
		nextLine = i;
//System.out.println("Maxline:" + nextLine);
		randomReadable = true;
	}
	
	public synchronized void closeFile() throws IOException {
		if(!closed){
			closed = true;
			file.close();
		}
	}
	
	public Reader getReader(){
		return new Reader(){
			
			private long lastAddr = 0;

            public void close() throws IOException {
            	closeFile();
            }


        	public int read(char[] cbuf, int off, int len) throws IOException {
            	long loc = file.getFilePointer();
            	long eof = file.length();
            	long charsLeft = eof - loc; 
            	if(charsLeft <= 0) return -1;
            	int toRead = len >= charsLeft ? (int)charsLeft : len;
            	int endPoint = off + toRead;
            	for(int i = off; i < endPoint; i++){
//System.out.print("Reading...");
            		cbuf[i] = (char)file.readByte();
//System.out.println(" Done");
            		// If we encounter a newline character then add line to linePointers
            		if(cbuf[i] == '\n'){
            			linePointers.put(nextLine++, lastAddr);
            			lastAddr = file.getFilePointer();
            		}
//System.out.println("Pointer: " + file.getFilePointer());
            	}
//System.out.println("Len = " + file.length());
            	// If we have read this far then random reads is now possible
            	if(file.getFilePointer() >= file.length()){
//System.out.println("File random readable");
            			randomReadable = true;
            	}
//System.out.println("Reader: read called " + cbuf.length + "," + off + "," + len + ", read=" + toRead);
            	return toRead;
            }
		};
	}
	
	public Writer getWriter(){
		return new Writer(){

            public void close() throws IOException {
				closeFile();
            }

            public void flush() throws IOException {
            	// Do nothing
            }

            public void write(char[] cbuf, int off, int len) throws IOException {
            	// Save pointer and seek to end of file
            	long curPointer = file.getFilePointer();
            	long newPointer = file.length(); 
            	file.seek(newPointer);
            	// Write to end of file
            	int endPoint = off + len;
	            for(int i = off; i < endPoint; i++){
	            	if(cbuf[i] == '\n'){
	                	// Add new position to linePointers
	                	linePointers.put(nextLine++, newPointer);
//System.out.println("Adding poinger " + maxLine + " --> " + newPointer);	                	
	            	}
	            	file.writeByte(cbuf[i]);
//	            	file.writeChar(cbuf[i]);
//	            	file.writeUTF(String.valueOf(cbuf[i]));
	            }
	            // Seek back to original position
	            file.seek(curPointer);
//System.out.println("Maxline:" + maxLine);
            }
			
		};
	}
	
	public static void main(String[] args) throws Exception{
//		test1(new RandomLineAccessFile(new File("test/test.txt")));
//		test2(new RandomLineAccessFile(new File("test/test.txt")));
//		test2(new RandomLineAccessFile(new File("test/test.csv")));
//		test3(new RandomLineAccessFile(new File("test/test.csv")));
		test4(new RandomLineAccessFile(new File("test/test.csv")));
	}
	public static void test1(RandomLineAccessFile file) throws Exception{
		System.out.println("|" + file.readLine(3) + "|");
		System.out.println("|" + file.readLine(2) + "|");
		System.out.println("|" + file.readLine(1) + "|");
		System.out.println("|" + file.readLine(0) + "|");
		System.out.println("|" + file.readLine(4) + "|");
		long origSize = file.getFile().length();
//System.out.println("Len = " + file.getFile().length());
		file.getWriter().write("e123456789\n");
//System.out.println("Len = " + file.getFile().length());
		file.getWriter().write("f123456789\n");
//System.out.println("Len = " + file.getFile().length());
		file.getWriter().write("g123456789\n");
//System.out.println("Len = " + file.getFile().length());
		System.out.println("|" + file.readLine(4) + "|");
		file.getFile().setLength(origSize);
//System.out.println("Len = " + file.getFile().length());
		System.out.println("|" + file.readLine(4) + "|");
	}
	public static void test2(RandomLineAccessFile file) throws Exception{
		Reader reader = file.getReader();
		int read = -1;
		while(read != 0){
			char[] buf = new char[10];
			read = reader.read(buf);
			System.out.print(buf);
		}
		System.out.println("\n------------");
		test1(file);
	}
	public static void test3(RandomLineAccessFile file) throws Exception{
		CsvMapReader reader = new CsvMapReader(file.getReader(), CsvPreference.STANDARD_PREFERENCE);
		String[] header = reader.getCSVHeader(false);
		for(String s : header) System.out.print(s + "|");
		System.out.println();
		Map<String, String> row;
		while((row = reader.read(header)) != null){
			for(String data : row.values()) System.out.print(data + "|");
			System.out.println();
		}
		reader.close();
	}
	public static void test4(RandomLineAccessFile file) throws Exception{
		CsvMapReader reader = new CsvMapReader(file.getReader(), CsvPreference.STANDARD_PREFERENCE);
		String[] header = reader.getCSVHeader(false);
		for(String s : header) System.out.print(s + "|");
		System.out.println();
		CellProcessor[] processor = new CellProcessor[]{new Optional(), new ParseDouble(), new ParseLong(), new Optional(), new Optional(), new Optional()};
		Map<String, ? super Object> row = reader.read(header, processor);
		while(row != null && row.size() != 0){
			for(Object data : row.values()) System.out.print(data + "|");
			row = reader.read(header, processor);
			System.out.println();
		}
		reader.close();
	}
}
