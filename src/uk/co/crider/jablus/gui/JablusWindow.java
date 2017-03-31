package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.SimulationInterface;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.DataSet;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jwo.utils.gui.JWStatusBar;

/** Basic top level jablus window, with jablus logo and status bar.
 * All windows used by JABLUS components should extend this class */
public class JablusWindow extends JFrame{

	/** Unique class ID */
    private static final long serialVersionUID = 1207764473502636155L;

    /** Image used to represent this program */
//    private static Image iconImage = Toolkit.getDefaultToolkit().getImage(JablusWindow.class.getResource(Constants.GUI_ICON_FILE));
//    private static ImageIcon iconImage = new ImageIcon(Constants.GUI_ICON_FILE);

    /** The status bar */
    protected JMenuBar menuBar;
    protected JMenu fileMenu;
    protected JMenuItem quitItem;
    protected JWStatusBar statusBar;
    private Image iconImage;
    private boolean showMenuBar;
    private boolean allowClose;
    private boolean showStatusBar;

	public JablusWindow(){ this("", true, true, true); }
	public JablusWindow(String title, boolean showMenuBar, boolean allowClose){ this(title, showMenuBar, true, allowClose); }
	public JablusWindow(String title, boolean showMenuBar, boolean showStatusBar, boolean allowClose){
		super();
		setTitle(title);
		this.showMenuBar = showMenuBar;
		this.showStatusBar = showStatusBar;
		this.allowClose = allowClose;
		initComponents();
	}

	/** Initialise graphical components */
	private void initComponents(){

		final JFrame thisFrame = this;

		// Set native look and feel
		try{ UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (UnsupportedLookAndFeelException ex) { System.out.println("Unable to load native look and feel");	}
		catch (ClassNotFoundException e) {           System.out.println("Unable to load native look and feel"); }
		catch (InstantiationException e) {           System.out.println("Unable to load native look and feel"); }
		catch (IllegalAccessException e) {           System.out.println("Unable to load native look and feel"); }

		// Set Icon

		//  This doesnt work for packaged jars, need to use getResource
/*	    try {
	        iconImage = ImageIO.read(new File(Constants.GUI_ICON_FILE));
        } catch (IOException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
 */

		String filePath = "/" + Constants.JABLUS_GRAPHICS_DIR + "/" + Constants.GUI_ICON_FILE;
//		String filePath = "/" + Constants.GUI_ICON_FILE;
	    URL url = getClass().getResource(filePath);
	    System.out.println("path=" + filePath + " url=" + url + " iconfile=" + Constants.GUI_ICON_FILE);
		iconImage = Toolkit.getDefaultToolkit().getImage(url);
		setIconImage(iconImage);

		// Set top-level layout
//		setLayout(new BorderLayout(0, 0));

		// Set up basic menu
        if(showMenuBar){
			menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			fileMenu = new JMenu("File");
			menuBar.add(fileMenu);
			quitItem = new JMenuItem("Quit", KeyEvent.VK_Q);
			quitItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					getWindowListeners()[0].windowClosing(new WindowEvent(thisFrame, WindowEvent.WINDOW_CLOSING));
				}
			});
			fileMenu.add(quitItem);
        }

		// Set up toolbar
//		toolBar = new JToolBar();
//		add(toolBar, BorderLayout.NORTH);

		// Set Status bar
        if(showStatusBar){
			statusBar = new JWStatusBar();
			add(statusBar, BorderLayout.SOUTH);
        }

		// Terminate program on close
		if(allowClose)
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		else
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	}

	/** Set the window title */
	public void setTitle(String title){
		super.setTitle(title.equals("") ? Constants.JABLUS_WINDOW_TITLE : Constants.JABLUS_WINDOW_TITLE + " - " + title);
	}

	/** Set the value of the status bar */
	public void setStatus(String status){
		if(statusBar != null)
			statusBar.setMessage(status);
	}

	/** Set the progress of the progress bar */
	public void setProgress(int progress){
		if(statusBar != null)
			statusBar.setProgress(progress);
	}

	/** Moves the window to the center of the screen */
	protected void center(){
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		setLocation(p.x - getWidth() / 2, p.y - getHeight() / 2);
	}

	/** Get historic data used to populate temporal data views (eg trend graphs) */
	public static Hashtable<String, List<Data>> getPastData(SimulationInterface sim, Data item, List<Data> time, DisplayParams displayParams){
//System.out.println("SimulationGUI: Generating past data for  " + item);
		Hashtable<String, List<Data>> pastData = new Hashtable<String, List<Data>>();
		if(sim.canToStart()){
			sim.toStart();
			// Incrament through history data until we reach current state
			// filling pastData object as we go
			while(!sim.atCurrentState()){
				// Retrive history snapshot
				DataSet snapshot = sim.getSnapshot();
				// Add time to time list
				time.add(snapshot.getItem(Constants.TEMPORAL_TIME));
//System.out.println("JablusWindow: Adding past data  " + snapshot);
				// Get stored data item
				Data pastItem = snapshot.getItem(item.getId());
//System.out.println("JablusWindow: Getting past item  " + item.getName() + ", resulting in pastItem=" + pastItem);
				if(pastItem != null){
					getPastData((Data)pastItem, pastData, displayParams);
				}
	    		// Incrament history pointer
				sim.stepForward();
			}
		}
//System.out.println("JablusWindow: PastData=");
//for(List list : pastData.values()){
//	System.out.println(list);
//}
//System.out.println(time);
		return pastData;
	}

	private static void getPastData(Data item, Hashtable<String, List<Data>> pastData, DisplayParams displayParams){
		if(item instanceof DataSet){
			for(Data subItem : ((DataSet)item).getItems())
				getPastData(subItem, pastData, displayParams);
		}
		else{
			// Only add pastData item if it needs to be displayed
			if(displayParams.display(item.getId(), DisplayParams.ADD_GRAPH | DisplayParams.SHOW_TEXT)){
				// Retrive existing list if it exists
				List<Data> l = pastData.get(item.getName());
				// If it doesn't exist then create it and add it to the pastData object
				if(l == null){
					l = new LinkedList<Data>();
					pastData.put(item.getName(), l);
				}
				// Add item to past data
				l.add(item);
			}
		}
	}

	/** For testing purposes */
	public static void main(String[] args){
		JablusWindow window = new JablusWindow();
		window.setVisible(true);

	}

}
