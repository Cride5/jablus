package uk.co.crider.jablus.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/** Simple class to represent a field with a label and value */
public class Field extends JPanel {

	/** Unique class ID */
    private static final long serialVersionUID = 4710800720477266700L;
	
	private JTextField value;
	
	public Field(String title, int labelWidth, int valueWidth){ this(title, labelWidth, valueWidth, 0, null); }
	public Field(String title, int labelWidth, int valueWidth, int unitsWidth, String units){
		super(new BorderLayout());
//		super.setInsets(new Insets(0, 0, 0, 0));
		setPreferredSize(new Dimension(labelWidth + valueWidth + unitsWidth - 15, 26));
		JLabel label = new JLabel(title + ":");
		label.setPreferredSize(new Dimension(labelWidth, 26));
		value = new JTextField();
		value.setPreferredSize(new Dimension(valueWidth, 26));
		add(label, BorderLayout.WEST);
		add(value, BorderLayout.CENTER);
		if(unitsWidth > 0 && units != null){
			JLabel unitsLabel = new JLabel(units);
			unitsLabel.setPreferredSize(new Dimension(unitsWidth, 26));
			add(unitsLabel, BorderLayout.EAST);
		}
	}
	
	public void setValue(String value){
		this.value.setText(value);
	}
	
	public String getValue(){
		return value.getText();
	}
}
