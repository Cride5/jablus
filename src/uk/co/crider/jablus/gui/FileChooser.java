package uk.co.crider.jablus.gui;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/** Used for selecting files to open/save */
public class FileChooser{

	/** Select directory and return to user */
	public static File chooseDir(Component parent, String title, FileFilter filter) {
		File chosen = null;		
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// Add filter if present
		if(filter != null)
			chooser.addChoosableFileFilter(filter);
		// disable the "All files" option.
		chooser.setAcceptAllFileFilterUsed(false);
		// Return file if user clicked OK
		if(chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
			chosen = chooser.getSelectedFile();
		return chosen;
	}

	/** Select directory and return to user */
	public static File openFile(Component parent, String title, String workDir, final String ext){
		File chosen = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File(workDir));
		chooser.setDialogTitle(title);
		// Add filter for extension if present
		if(ext != null){
			// Add a filter to accept given extension
			chooser.addChoosableFileFilter(new FileFilter(){
	            public boolean accept(File f){
		            return f.isDirectory() || f.getName().endsWith(ext);
	            }
	            public String getDescription(){
		            return ext + " files";
	            }});
			// disable the "All files" option.
			chooser.setAcceptAllFileFilterUsed(false);
		}
		// Return file if user clicked OK
		if(chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
			chosen = chooser.getSelectedFile();
		// Add extension if not present
		try{
			if(chosen != null && ext != null && !chosen.getName().endsWith(ext))
				chosen = new File(chosen.getCanonicalPath() + ext);
		}catch(IOException e){
			e.printStackTrace();
			chosen = null;
		}
		return chosen;
	}

	/** Select directory and return to user */
	public static File saveFile(Component parent, String title, String workDir, String defFile, final String ext){
		File chosen = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File(workDir));
		chooser.setDialogTitle(title);
		// Add filter for extension if present
		if(ext != null){
			// Add a filter to accept given extension
			chooser.addChoosableFileFilter(new FileFilter(){
	            public boolean accept(File f){
		            return f.isDirectory() || f.getName().endsWith(ext);
	            }
	            public String getDescription(){
		            return ext + " files";
	            }});
			// disable the "All files" option.
			chooser.setAcceptAllFileFilterUsed(false);
		}
		defFile = defFile == null ? "untitled" : defFile;
		chooser.setSelectedFile(new File(defFile +
				(ext == null || defFile.endsWith(ext) ? "" : ext)));
		// Return file if user clicked OK
		if(chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)
			chosen = chooser.getSelectedFile();
		// Add extension if not present
		try{
			if(chosen != null && ext != null && !chosen.getName().endsWith(ext))
				chosen = new File(chosen.getCanonicalPath() + ext);
		}catch(IOException e){
			e.printStackTrace();
			chosen = null;
		}
		return chosen;
	}

	/** For testing purposes */
	public static void main(String s[]) {
//		System.out.println(chooseDir(null, "Choose a direcctory", null));
		System.out.println(saveFile(null, "Save a file", ".",  null, null));
		System.out.println(saveFile(null, "Save a file", ".",  null, ".csv"));
		System.out.println(saveFile(null, "Save a file", ".",  "test", null));
		System.out.println(saveFile(null, "Save a file", ".",  "test", ".csv"));
	}
}
