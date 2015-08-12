package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** Represents a 1-dimensional data series which can be added to a DataView */
public class SimpleItem1D extends DataViewItem{
	
	/** Unique class ID */
    private static final long serialVersionUID = 5055304895820643335L;
    
    private boolean canEdit;
    private Data1D data;
    private DataView view;
	private JLabel valLabel;
	
	public SimpleItem1D(Data1D data, DataView view, boolean canEdit){
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.canEdit = canEdit;
		this.data = data;
		this.view = view;
		initComponents();
	}
	
	private void initComponents(){
		JLabel nameLabel = new JLabel(data.getName() + ":  ", SwingConstants.RIGHT);
		nameLabel.setFont(SimulationDisplayParams.DEFAULT_FONT);
		nameLabel.setPreferredSize(new Dimension(
				SimulationDisplayParams.DATA_NAME_WIDTH,
				SimulationDisplayParams.LABEL_HEIGHT));
		add(nameLabel);
		valLabel = new JLabel(getLabelText());
		valLabel.setFont(SimulationDisplayParams.DEFAULT_FONT);
		valLabel.setPreferredSize(new Dimension(
				SimulationDisplayParams.DATA_VALUE_WIDTH,
				SimulationDisplayParams.LABEL_HEIGHT));
		add(valLabel);
		final SimpleItem1D item = this;
		if(canEdit){
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					view.hideItem(item);
				}
			});
		}
		setPreferredSize(new Dimension(SimulationDisplayParams.DATA_PANEL_WIDTH, SimulationDisplayParams.DATA_ITEM_HEIGHT));
	}
	
	private String getLabelText(){
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.getLength(); i++){
			if(i == data.getLength() - 1)
				buf.append(data.stringValue(i));
			else
				buf.append(data.stringValue(i) + ", ");
		}
		return buf.toString();
	}
	
	public void setData(Data data){
//System.out.println("SimpleItem1D: Setting data from " + this.data + " to " + data);
		if(data instanceof Data1D){
			this.data = (Data1D)data;
			redisplay();
		}
	}
	
	public void redisplay(){
		if(valLabel != null)
			valLabel.setText(getLabelText());
	}
	
	public int dataId(){
		return data.getId();
	}
	
	public String toString(){
		return "SimpleItem1D(" + data + ")";
	}

}
