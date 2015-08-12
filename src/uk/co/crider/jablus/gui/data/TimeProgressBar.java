package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.JablusWindow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/** A gui comonent which draws a dynamic time progress bar with a defined start and end point */
public class TimeProgressBar extends JPanel {
	
	/** Unique class ID */
    private static final long serialVersionUID = 7155358825876383591L;
    
	public TimeProgressBar(Time time, int start, int end){
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		setLayout(layout);
		// Create start and end time lables
		JLabel startTime = new JLabel((int)(start / Time.DAYS_YEAR) + " ");
		startTime.setHorizontalAlignment(JLabel.LEFT);
		c.gridwidth = 1;
		layout.setConstraints(startTime, c);
		add(startTime);
		JLabel endTime = new JLabel(" " + (int)(end / Time.DAYS_YEAR));
		endTime.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(endTime, c);
		add(endTime);
		// Create time progress bar
		TimeBar bar = new TimeBar(time, start, end);
		layout.setConstraints(bar, c);
		add(bar);
	}

	/** Re-draw the object to reflect new state */
	public void redisplay(){
		repaint();
	}

	/** The actual progress bar */
	class TimeBar extends JLabel{

		/** Unique class ID */
        private static final long serialVersionUID = -9134988223738792055L;
		private Time time;
		private int start;
		private int end;
		public TimeBar(Time time, int start, int end){
//			super();
			this.time = time;
			this.start = start;
			this.end = end;
			setBorder(new BevelBorder(BevelBorder.LOWERED));
			setFont(getFont().deriveFont(Font.BOLD, 11.0f));
			setForeground(new Color(100, 100, 100));
			setHorizontalAlignment(JLabel.CENTER);
		}
		public void paint(Graphics g){
			setText(time.dateString());
			Insets in = getInsets();
			int w = getSize().width - in.left - in.right;
			int h = getSize().height - in.top - in.bottom;
			double pos = in.left;
			double percent = (double)(time.intValue() - start) / (end - start);
			percent = percent < 0 ? 0 : percent > 1 ? 1 : percent;
			double bw = w * percent;
			g.setColor(new Color(71, 167, 234));
			g.fillRect((int)pos, in.top, (int)Math.ceil(bw), h);
			g.setColor(new Color(153, 206, 243));
			g.fillRect((int)pos, in.top + 2, (int)Math.ceil(bw), h - 4);
			g.setColor(new Color(202, 230, 249));
			g.fillRect((int)pos, in.top + 4, (int)Math.ceil(bw), h - 8);
			pos = pos + bw;
			
			double years = (end - start) / Time.DAYS_YEAR;
			bw = w / years;
			pos = in.left + bw - (bw * (start % Time.DAYS_YEAR) / Time.DAYS_YEAR);
			for(int i = 0; i < years; i++){
				g.setColor(new Color(220, 220, 220));
				g.fillRect((int)pos, in.top, 1, h);
				pos = pos + bw;
			}
			super.paint(g);
		}
	}
	
	/** For testing */
	public static boolean running = true;
	/** For testing */
	public static void main(String[] args){
		int start = (int)(365.25 * 1998.8);
		int end = (int)(365.25 * 2000.2);
		JablusWindow f = new JablusWindow("", false, false, true){
            private static final long serialVersionUID = 1L;
			public void dispose(){
				running = false;
				super.dispose();
			}
		};
		Time t = new Time(Time.WEEK);
		t.setTime(Time.DAY, start);
		TimeProgressBar bar = new TimeProgressBar(t, start, end);
		f.add(bar);
		f.setSize(300, 55);
		f.setVisible(true);
		while(running && t.intValue() < end){
			try{ Thread.sleep(250); }
			catch(Exception e){}
			bar.redisplay();
			bar.repaint();
			t.execStep();
		}
	}

}
