package uk.co.crider.jablus.gui.sim;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.gui.EventHandler;
import uk.co.crider.jablus.gui.ToolButton;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/** Component for housing simulation controls. */
public class ControlPanel extends JPanel {

	/** Unique class ID */
    private static final long serialVersionUID = 3941484755809663059L;

    private EventHandler eventHandler;

    public ControlPanel(EventHandler eventHandler){
    	super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.eventHandler = eventHandler;
    	initComponents();
    }
    
    private void initComponents(){
    	// Create components
    	ToolButton narrativesButton = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/down.png"     , "Narratives", "Toggle narratives panel", 10);
		ToolButton startButton      = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/first.png"    , null        , "Skip to start"              );
		ToolButton backButton       = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/previous.png" , null        , "Step back"                  );
		ToolButton playButton       = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/right.png"    , null        , "Resume simulation"      , 18);
		ToolButton pauseButton      = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/pause.png"    , null        , "Pause simulation"           );
		ToolButton forwardButton    = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/next.png"     , null        , "Step forward"               );
		ToolButton endButton        = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/last.png"     , null        , "Skip to end"                );
		ToolButton recordButton     = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/record.png"   , null        , "Record all frames"      , 15, "/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/record_on.png");
		ToolButton snapshotButton   = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/snapshot.png" , null        , "Take frame snapshot"    , 28);
    	ToolButton outputButton     = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/basic/right.png"    , "Output"    , "Toggle narratives panel", 10);

    	// Add to panel
    	add(narrativesButton);
    	add(startButton     );
    	add(backButton      );
    	add(playButton      );
    	add(pauseButton     );
    	add(forwardButton   );
    	add(endButton       );
    	add(recordButton    );
    	add(snapshotButton  );
    	add(outputButton    );

    	// Set up eventhandlers
    	eventHandler.add(narrativesButton, EventHandler.SHOW_NARRATIVES);
    	eventHandler.add(startButton     , EventHandler.START);
    	eventHandler.add(backButton      , EventHandler.BACK);
    	eventHandler.add(playButton      , EventHandler.PLAY);
    	eventHandler.add(pauseButton     , EventHandler.PAUSE);
    	eventHandler.add(forwardButton   , EventHandler.FORWARD);
    	eventHandler.add(endButton       , EventHandler.END);
    	eventHandler.add(recordButton    , EventHandler.RECORD);
    	eventHandler.add(snapshotButton  , EventHandler.SNAPSHOT);
    	eventHandler.add(outputButton    , EventHandler.SHOW_OUTPUT);
    	
    }
    
	/** For testing only */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Testing ControlPanel");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(new ControlPanel(new EventHandler(null, null)), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

}
