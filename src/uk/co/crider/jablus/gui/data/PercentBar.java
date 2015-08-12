package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.utils.ColourMixer;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/** Displays profit as a positive/negative progress bar */
public class PercentBar extends JLabel {
	
	/** Unique class ID */
    private static final long serialVersionUID = -6879265198339674744L;
	
	private Data0D.Double data;
	private boolean showPercent;
	private boolean centred;
	
	public PercentBar(Data0D.Double data){ this(data, false, false); }
	public PercentBar(Data0D.Double data, boolean showPercent, boolean centred){
		this.data = data;
		this.showPercent = showPercent;
		this.centred = centred;
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		if(showPercent){
			setFont(getFont().deriveFont(Font.BOLD, 11.0f));
			setHorizontalAlignment(JLabel.CENTER);
		}
	}
	
	public Data0D.Double getData(){
		return data;
	}
	
	/** Re draws the progress bar to represent current state */
	public void redisplay(){
		repaint();
	}

	/** @inheritDoc */
	public void paint(Graphics g){
		Insets in = getInsets();
		int w = getSize().width - in.left - in.right;
		int h = getSize().height - in.top - in.bottom;
		double p = data.doubleValue();
		if(showPercent)
			setText(Utils.roundString(p * 100, 0) + "%");
		p = p < 0 ? 0 : p > 1 ? 1 : p;
		double bw, c = 0, xo;
		if(centred){
			bw = Math.abs(w * (p - 0.5));
			c = getSize().width / 2.0;
			xo = p > 0.5 ? c : c - bw;		
		}
		else{
			bw = w * p;
			xo = in.left;
		}
		// Draw profit percent
		Color col = ColourMixer.interpolate(p, new Color[]{
				new Color(219, 88, 88),
				new Color(219, 208, 88),
				new Color(110, 219, 88)});
		if((centred && p != 0.5) || (!centred && p != 0)){
			g.setColor(col.darker().darker());
			g.fillRect((int)xo, in.top, (int)Math.ceil(bw), h);
			g.setColor(col.darker());
			g.fillRect((int)xo, in.top + 2, (int)Math.ceil(bw), h - 4);
			g.setColor(col);
			g.fillRect((int)xo, in.top + 4, (int)Math.ceil(bw), h - 8);
		}
		if(centred){
			// Draw center line
			g.setColor(Color.GRAY);
			g.fillRect((int)c, in.top, 1, h); 
			g.setColor(Color.BLACK);
		}
		super.paint(g);
	}
	
	public static boolean running = true;
	/** For testing */
	public static void main(String[] args){
		JablusWindow f = new JablusWindow("", false, false, true){
            private static final long serialVersionUID = 1L;
			public void dispose(){
				running = false;
				super.dispose();
			}
		};
		DoubleData profit = new DoubleData(0);
		PercentBar bar = new PercentBar(profit, true, false);
		f.add(bar);
//		f.pack();
		f.setSize(300, 55);
		f.setVisible(true);
		while(running){
			try{ Thread.sleep(2000); }
			catch(Exception e){}
			profit.setValue((Math.random()));
			bar.redisplay();
			bar.repaint();
		}
	}

}
