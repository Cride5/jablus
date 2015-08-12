package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** Represents a single-valued data item which can be added to a DataView panel */
public class SimpleItem extends DataViewItem{
	
	/** Unique class ID */
    private static final long serialVersionUID = 8357031664470409734L;
    
    private Data0D data;
    private DataView view;
	private JLabel valLabel;
	
	public SimpleItem(Data0D data){ this(data, null, false); }
	public SimpleItem(Data0D data, DataView view, boolean canEdit){
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.data = data;
		this.view = view;
		initComponents(canEdit);
	}
	
	private void initComponents(boolean canEdit){
		JLabel nameLabel = new JLabel(data.getName() + " " + data.getUnits() + ":  ", SwingConstants.RIGHT);
		nameLabel.setFont(SimulationDisplayParams.DEFAULT_FONT);
		nameLabel.setPreferredSize(new Dimension(
				SimulationDisplayParams.DATA_NAME_WIDTH,
				SimulationDisplayParams.LABEL_HEIGHT));
		add(nameLabel);
		valLabel = new JLabel(data.stringValue());
		valLabel.setFont(SimulationDisplayParams.DEFAULT_FONT);
		valLabel.setPreferredSize(new Dimension(
				SimulationDisplayParams.DATA_VALUE_WIDTH,
				SimulationDisplayParams.LABEL_HEIGHT));
		add(valLabel);
		final SimpleItem item = this;
		if(canEdit && view != null){
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					view.hideItem(item);
				}
			});
		}
		setPreferredSize(new Dimension(SimulationDisplayParams.DATA_PANEL_WIDTH, SimulationDisplayParams.DATA_ITEM_HEIGHT));
	}
	
	public void setData(Data data){
//System.out.println("SimpleItem: Setting data from " + this.data + " to " + data);
		if(data instanceof Data0D){
			this.data = (Data0D)data;
			redisplay();
		}
	}
	
	public void redisplay(){
		if(valLabel != null)
			valLabel.setText(data.stringValue());
	}
	
	public int dataId(){
		return data.getId();
	}

	public String toString(){
		return "SimpleItem(" + data + ")";
	}
	
}
