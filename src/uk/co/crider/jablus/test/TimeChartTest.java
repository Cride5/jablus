package uk.co.crider.jablus.test;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;

import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.data.TimeProgressBar;

public class TimeChartTest extends JPanel{

	protected JFreeChart chart;

	public synchronized void paintComponent(Graphics2D g) {
		super.paintComponent(g);
		if (chart != null) {
			Dimension size = getSize();
			Rectangle2D.Double rect = new Rectangle2D.Double(0.0, 0.0,
					size.width, size.height);
			chart.draw(g, rect);
		}
	}

	public static boolean running = true;
	public static void main(String[] args){
		int start = (int)(365.25 * 1998.8);
		int end = (int)(365.25 * 2000.2);
		JablusWindow f = new JablusWindow("", false, false, true){
			public void dispose(){
				running = false;
				super.dispose();
			}
		};
		Time t = new Time(Time.WEEK);
		t.setTime(Time.DAY, start);
		TimeProgressBar bar = new TimeProgressBar(t, start, end);
		f.add(bar);
//		f.pack();
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
