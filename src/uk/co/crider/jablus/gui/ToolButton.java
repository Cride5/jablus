package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;

import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/** Convenience subclass of JButton for creating tool buttons */
public class ToolButton extends JButton {

	/** Unique class ID */
    private static final long serialVersionUID = 4859978692691639047L;
    private static final Insets MARGINS = new Insets(5, 5, 5, 5);
    private static final int ICON_SIZE = 15;
	private ImageIcon icon;
	private ImageIcon altIcon;
	private String text;
	private String toolTips;

	public ToolButton(String imageFile){ this(imageFile, null, null, ICON_SIZE, null); }
	public ToolButton(String imageFile, String text){ this(imageFile, text, null, ICON_SIZE, null); }
	public ToolButton(String imageFile, String text, String toolTips){ this(imageFile, text, toolTips, ICON_SIZE, null); }
	public ToolButton(String imageFile, String text, String toolTips, int iconSize){ this(imageFile, text, toolTips, iconSize, null); }
	public ToolButton(String imageFile, String text, String toolTips, int iconSize, String altIcon){
		super();
		this.icon = getIcon(imageFile, iconSize);
		this.text = text;
		this.toolTips = toolTips;
		if(altIcon != null)
			this.altIcon = getIcon(altIcon, iconSize);
		initComponents();
	}

	/** Initialise button's components */
	private void initComponents(){

		// Set icon
		setIcon(icon);
		
		// Set text
		setText(text);
    	setFont(SimulationDisplayParams.DEFAULT_FONT);

		// Set tool tops
		setToolTipText(toolTips);

		// Set display properties
		setMargin(MARGINS);
		setVerticalTextPosition(CENTER);
		setHorizontalTextPosition(LEFT);
//		setBorder(new EmptyBorder(margins));
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
		
		final JButton button = this;
		addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				if(button.isEnabled()){
					setContentAreaFilled(true);
					setBorderPainted(true);
				}
			}
			public void mouseExited(MouseEvent e){
				setContentAreaFilled(false);
				setBorderPainted(false);
			}
		});
	}
		
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		if(!enabled){
			setContentAreaFilled(false);
			setBorderPainted(false);
		}
			
	}
	
	public void showAltIcon(boolean show){
		if(show)
			setIcon(altIcon);
		else
			setIcon(icon);
	}

	/** Loads an icon from an image */
	private ImageIcon getIcon(String file, int iconSize){
		ImageIcon icon = null;
		// Set Icon image
		try {
		
//System.out.println("img file=" + file);
			MediaTracker mediaTracker = new MediaTracker(this);
			Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(file));
//			BufferedImage image = ImageIO.read(new File(file));
			mediaTracker.addImage(image, 0);		
			try{ mediaTracker.waitForID(0); }
			catch (InterruptedException ie){ ie.printStackTrace(); }
			int w = image.getWidth(null);
			int h = image.getHeight(null);
//System.out.println("dims= " + w + ", " + h);
			int sw, sh;
			if(w > h){
				sw = iconSize;
				sh = sw * h / w;
			}
			else{
				sh = iconSize;
				sw = sh * w / h;
			}		
			icon = new ImageIcon(image.getScaledInstance(sw, sh, Image.SCALE_AREA_AVERAGING));
		} catch (Exception e) {
			System.out.println("Couldnt load icon image: " + e);
		}
//System.out.println("loaded img file " + file);
		return icon;
	}
}
