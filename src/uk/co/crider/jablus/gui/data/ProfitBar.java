package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/** Displays profit as a positive/negative progress bar */
public class ProfitBar extends JLabel {
	
	/** Unique class ID */
    private static final long serialVersionUID = -6879265198339674744L;
    
	private static final Color RED1 = new Color(234, 71, 71);
	private static final Color RED2 = new Color(243, 153, 153);
	private static final Color RED3 = new Color(249, 202, 202);
	private static final Color GREEN1 = new Color(103, 234, 71);
	private static final Color GREEN2 = new Color(171, 243, 153);
	private static final Color GREEN3 = new Color(211, 249, 202);

	private static final int MAX_PROFIT = 10000;
	
	private Data0D.Double profit;
	
	public ProfitBar(Data0D.Double profit){
		this.profit = profit;
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setFont(getFont().deriveFont(Font.BOLD, 11.0f));
		setForeground(new Color(100, 100, 100));
		setHorizontalAlignment(JLabel.CENTER);
	}
	
	/** Re draws the progress bar to represent current state */
	public void redisplay(){
		repaint();
	}

	/** @inheritDoc */
	public void paint(Graphics g){
		if(profit.doubleValue() >= 0){
			setText("+£" + Utils.roundString(Math.abs(profit.doubleValue()), 0) + "  ");
			setHorizontalAlignment(JLabel.RIGHT);
		}else{
			setText("  -£" + Utils.roundString(Math.abs(profit.doubleValue()), 0));
			setHorizontalAlignment(JLabel.LEFT);				
		}
		Insets in = getInsets();
		int w = getSize().width - in.left - in.right;
		int h = getSize().height - in.top - in.bottom;
		double c = getSize().width / 2.0;
		double p = profit.doubleValue() / MAX_PROFIT;
		p = p < -1 ? -1 : p > 1 ? 1 : p;
		double bw = Math.abs(w * p / 2);
		double xo = p > 0 ? c : c - bw;
		// Draw profit percent
		if(p != 0){
			g.setColor(p > 0 ? GREEN1 : RED1);
			g.fillRect((int)xo, in.top, (int)Math.ceil(bw), h);
			g.setColor(p > 0 ? GREEN2 : RED2);
			g.fillRect((int)xo, in.top + 2, (int)Math.ceil(bw), h - 4);
			g.setColor(p > 0 ? GREEN3 : RED3);
			g.fillRect((int)xo, in.top + 4, (int)Math.ceil(bw), h - 8);
		}
		// Draw center line
		g.setColor(Color.GRAY);
		g.fillRect((int)c, in.top, 1, h); 
		g.setColor(Color.BLACK);
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
		ProfitBar bar = new ProfitBar(profit);
		f.add(bar);
//		f.pack();
		f.setSize(300, 55);
		f.setVisible(true);
		while(running){
			try{ Thread.sleep(2000); }
			catch(Exception e){}
			profit.setValue((Math.random() - 0.5) * 3000);
			bar.redisplay();
			bar.repaint();
		}
	}

}
