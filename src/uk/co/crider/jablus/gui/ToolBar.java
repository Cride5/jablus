package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.Constants;

import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** For holding rows of tool buttons */
public class ToolBar extends JPanel {

	/** Unique class ID */
    private static final long serialVersionUID = -8504623539323070433L;
    
	public ToolBar(){
		super(new FlowLayout(FlowLayout.LEFT, 4, 0));
		initComponents();
	}
	
	private void initComponents(){
		// Set up toolbar
		ToolButton startButton   = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/control_start.png"  , null, "Start"  ); add(startButton  );
		ToolButton backButton    = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/control_back.png"   , null, "Back"   ); add(backButton   );
		ToolButton pauseButton   = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/control_pause.png"  , null, "Pause"  ); add(pauseButton  );
		ToolButton playButton    = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/control_play.png"   , null, "Play"   ); add(playButton   );
		ToolButton stopButton    = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/control_stop.png"   , null, "Stop"   ); add(stopButton   );
		ToolButton forwardButton = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/control_forward.png", null, "Forward"); add(forwardButton);
		ToolButton endButton     = new ToolButton("/" + Constants.JABLUS_GRAPHICS_DIR + "/control_end.png"    , null, "End"    ); add(endButton    );
		
		setBorder(new EmptyBorder(new Insets(5,5,5,5)));
	}
}
