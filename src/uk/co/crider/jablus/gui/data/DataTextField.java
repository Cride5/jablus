package uk.co.crider.jablus.gui.data;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import uk.co.crider.jablus.data.Data0D;

import javax.swing.JTextField;

/** A text field for populating a given data item.
 * Input data is validated according to the data's characteristics */
public class DataTextField extends JTextField {

	/** Unique class ID */
	private static final long serialVersionUID = -891426302054745316L;
	
	private String prev = "";
	private Data0D d;
	private Data0D d_;
	
	public DataTextField(final Data0D d){
		this.d = d;
		this.d_ = (Data0D)d.clone();
		setText("" + d.getValue());
		addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
				String newTxt = getText();
				Number min = d.getMin();
				Number max = d.getMax();
				if(newTxt.equals("-") || newTxt.equals("")){
			//	|| (newTxt.endsWith(".") && d instanceof Data0D.Double)){
					d.setValue(0);
					prev = newTxt;
					return;
				}
				if(newTxt.length() > 8){
					setText(prev);
					return;
				}
				try{
					if(d instanceof Data0D.Integer){
							int v = Integer.parseInt(newTxt);
						if(d.hasRange() && v < min.intValue()){
							setText("" + min);
							d.setValue(min);
						}
						else if(d.hasRange() && v > max.intValue()){
							setText("" + max);
							d.setValue(max);
						}else{
							prev = newTxt;
							d.setValue(v);
						}
					}else{
						double v = Double.parseDouble(newTxt);
						if(d.hasRange() && v < min.doubleValue()){
							setText("" + min);
							d.setValue(min);
						}
						else if(d.hasRange() && v > max.doubleValue()){
							setText("" + max);
							d.setValue(max);
						}
						else{
							prev = newTxt;
							d.setValue(v);
						}
					}
				}catch(NumberFormatException ex){
					setText(prev);
				}
			}
			public void keyReleased(KeyEvent e){ keyPressed(e); }
			public void keyTyped(KeyEvent e){ keyPressed(e); }         
		});
		
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){
            }
			public void focusLost(FocusEvent e){
				boolean changed = !d.getValue().equals(d_.getValue());
				d_ = (Data0D)d.clone();
				if(changed) valueChanged();
           }});
	}
	
	/** Called when its value has changed */
	protected void valueChanged(){
		// Up to subclasses to implement
	}
	
	public void setValue(Number n){
		d.setValue(n);
		d_.setValue(n);
	}
	
	public void redisplay(){
		setText(d.getValue().toString());
	}
	
}
