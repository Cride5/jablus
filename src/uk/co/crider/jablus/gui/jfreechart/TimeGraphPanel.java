package uk.co.crider.jablus.gui.jfreechart;

import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;


/** Used for drawing temporal line graphs with a fixed period */
public class TimeGraphPanel extends GraphPanel{

	/** Unique class ID */
    private static final long serialVersionUID = 8070913801773469897L;
    
	/** Chart associated with this graph window */
	protected String title;
	protected JFreeChart chart;
	protected Hashtable<String, Integer> seriesIds;
	protected Hashtable<String, TimeSeries> hiddenSeries;
	protected Hashtable<String, Paint> seriesPaint;
	protected Hashtable<String, JCheckBox> selectBoxes;
	protected boolean drawLegend;
	protected boolean editable;
	protected Date startTime;
	
	private JPanel chartPanel;
	private JPanel legandPanel;
	private GridBagLayout lpLayout;
	private GridBagConstraints lpC;
	
	/** Series counter */
	protected int seriesCount;
	
	public TimeGraphPanel(Date startTime){
		this(null, null, null, startTime, true, false); }
	public TimeGraphPanel(String title, String xlabel, String ylabel, Date startTime){
		this(title, xlabel, ylabel, startTime, true, false); }
	public TimeGraphPanel(String title, String xlabel, String ylabel, Date startTime, boolean window){
		this(title, xlabel, ylabel, startTime, window, true, false); }
	public TimeGraphPanel(String title, String xlabel, String ylabel, Date startTime, boolean window, boolean drawLegend){
		this(title, xlabel, ylabel, startTime, window, true, false); }
	public TimeGraphPanel(String title, String xlabel, String ylabel, Date startTime, boolean window, boolean drawLegend, boolean editable){
		super();
		setLayout(new BorderLayout());
		this.startTime = startTime;
		this.drawLegend = drawLegend;
		this.editable = editable;
		this.chart = ChartFactory.createTimeSeriesChart(title, // Title
			xlabel, // time axis label
			ylabel, // value - axis label
			new TimeSeriesCollection(),
			false, // Show legend
			false, // Tooltips
			false // url
		);
		if(chart.getTitle() != null)
			chart.getTitle().setFont(SimulationDisplayParams.DEFAULT_FONT);
		this.title = title;
		this.seriesIds = new Hashtable<String, Integer>();
		this.hiddenSeries = new Hashtable<String, TimeSeries>();
		this.seriesPaint = new Hashtable<String, Paint>();
		this.selectBoxes = new Hashtable<String, JCheckBox>();
		this.seriesCount = 0;
		if(window)
			setIcon(new ImageIcon(chart.createBufferedImage(500,300)));
		
		chartPanel = new JPanel(){
			public synchronized void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (chart != null) {
					Graphics2D g2d = (Graphics2D) g;
					Dimension size = getSize();
					Rectangle2D.Double rect = new Rectangle2D.Double(0.0, 0.0,
							size.width, size.height);
					chart.draw(g2d, rect);
				}
			}
		};
		chartPanel.setToolTipText("Graph showing weekly change in " + title);
		
		lpLayout = new GridBagLayout();
		lpC = new GridBagConstraints();
		lpC.weighty = 0.0;
		lpC.gridwidth = GridBagConstraints.REMAINDER;
		lpC.fill = GridBagConstraints.HORIZONTAL;
//		lpC.ipady = 2;
		lpC.insets = new Insets(2, 0, 2, 0);
		legandPanel = new JPanel(lpLayout);
		legandPanel.setOpaque(true);
//		legandPanel.setLayout(new BoxLayout(legandPanel, BoxLayout.Y_AXIS));
		add(chartPanel, BorderLayout.CENTER);
		add(legandPanel, BorderLayout.EAST);
	}
	
	/** @inheritDoc */
	public void setRange(double min, double max){
		chart.getXYPlot().getRangeAxis().setLowerBound(min);
		chart.getXYPlot().getRangeAxis().setUpperBound(max);
	}
	
	/** Displays a stand alone window containing this graph */
	public JFrame displayWindow(){ return displayWindow(0, 0); }
	public JFrame displayWindow(int x, int y){
		JFrame window = new JFrame();
		window.setTitle(title);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout());
		window.getContentPane().add(this, BorderLayout.CENTER);

		if(editable){
//			JPanel 
		}
		window.pack();
		window.setLocation(x, y);
		window.setVisible(true);
		return window;
	}
	
	/** Add a data series, */
	public void addSeries(String name, Paint linePaint){ addSeries(name, (double[])null, linePaint); }
	public void addSeries(final String name, double[] data, Paint linePaint){
//System.out.println("TimeGraphPanel: Adding series to graph " + title +  ", series name = " + name + "=" + Utils.arrayString(data));
		TimeSeries timeSeries = new TimeSeries(name, Week.class);
		timeSeries.setMaximumItemCount(Time.WEEKS_YEAR);
		timeSeries.removeAgedItems(true);
		if(data != null){
			for(int j = 0; j < data.length; j++){
				timeSeries.add(new Week(j % Time.WEEKS_YEAR, 1900 + (int)(j / Time.WEEKS_YEAR)), data[j]);
			}
		}
		addSeries(name, timeSeries, linePaint);
		// Add checkbox if editable, label otherwise
		JComponent kItem = null;
		if(editable){
			final JCheckBox c = new JCheckBox(drawLegend ? name : null);
			c.setSelected(true);
			c.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					setVisible(name, c.isSelected());
				}
			});
			selectBoxes.put(name, c);
			kItem = c;
		}
		else kItem = new JLabel(drawLegend ? name : " ");
		kItem.setToolTipText(name);
		Paint p = seriesPaint.get(name);
		if(p != null && p instanceof Color){
			if(drawLegend) kItem.setForeground((Color)p);
			else kItem.setBackground((Color)p);
		}
		if(!drawLegend){
			kItem.setOpaque(true);
			kItem.setPreferredSize(new Dimension(19, 18));
		}
//		kItem.setMinimumSize(new Dimension(15, 15));
//		kItem.setMaximumSize(new Dimension(15, 15));
		lpLayout.setConstraints(kItem, lpC);
		legandPanel.add(kItem);
//		kItem.setPreferredSize(new Dimension(kItem.getPreferredSize().width, 15));
	}
	private void addSeries(String name, TimeSeries timeSeries, Paint linePaint){
		TimeSeriesCollection dataset = (TimeSeriesCollection)chart.getXYPlot().getDataset(0);
		dataset.addSeries(timeSeries);
		chart.getXYPlot().getRenderer().setSeriesPaint(seriesCount, linePaint);
		seriesIds.put(name, seriesCount++);
		seriesPaint.put(name, linePaint);
	}
	
	/** Remove a data series */
	public void removeSeries(String name){
		TimeSeriesCollection dataset = (TimeSeriesCollection)chart.getXYPlot().getDataset(0);
		int id = ((Integer)seriesIds.get(name)).intValue();
		dataset.removeSeries(id);
		// Decrament all ids greater than id
		for(Enumeration i = seriesIds.keys(); i.hasMoreElements();){
			String k = (String)i.nextElement();
			int tmpId = seriesIds.get(k);
			if(tmpId > id){
				tmpId--;
				seriesIds.put(k, tmpId);
				chart.getXYPlot().getRenderer().setSeriesPaint(tmpId, seriesPaint.get(k));
			}
		}
		seriesCount--;
	}
	
	/** Add a data item to the end of a series */
	public void addData(String name, double value){
//System.out.println("TimeGraphPanel: Adding data " + name + "=" + value);
		TimeSeries series = null;
		if(hiddenSeries.containsKey(name))
			series = hiddenSeries.get(name);
		else{
			TimeSeriesCollection dataset = (TimeSeriesCollection)chart.getXYPlot().getDataset(0);
			int id = seriesIds.get(name);
			series = dataset.getSeries(id);
		}
		if(series.getItemCount() == 0){
			series.add(new Week(startTime), value);
		}
		else{
//System.out.println("TimeGraphPanel: seriesDescription = " + series.getDomainDescription());
			series.add(series.getNextTimePeriod(), value);
		}
		
	}
	
	/** Remove a data item from the end of a series */
/*	public void removeData(String name){
		TimeSeriesCollection dataset = (TimeSeriesCollection)chart.getXYPlot().getDataset(0);
//		XYSeriesCollection dataset = (XYSeriesCollection)chart.getXYPlot().getDataset(0);
		int id = seriesIds.get(name);
		TimeSeries series = dataset.getSeries(id); 
		series.removeAgedItems(true);// (series.getItemCount()-1);
	}
*/	
	public void show(String seriesName, boolean visible){
		if(editable)
			selectBoxes.get(seriesName).setSelected(visible);
		setVisible(seriesName, visible);
	}
	
	private void setVisible(String seriesName, boolean visible){
		TimeSeriesCollection dataset = (TimeSeriesCollection)chart.getXYPlot().getDataset(0);
		if(!seriesIds.containsKey(seriesName)) return;
		int id = seriesIds.get(seriesName);
		if(visible){
			if(!hiddenSeries.containsKey(seriesName)) return;
			addSeries(seriesName, hiddenSeries.get(seriesName), seriesPaint.get(seriesName));
			hiddenSeries.remove(seriesName);
		}
		else{
			if(hiddenSeries.containsKey(seriesName)) return;
			TimeSeries series = dataset.getSeries(id);
			removeSeries(seriesName);
			hiddenSeries.put(seriesName, series);
		}
		repaint();
	}

	/** Tests the operation of the GraphPanel object */
	public static void main(String[] args){
//		testTimeFlow();
		testActivation();
	}

	private static boolean visible = true;
	public static void testActivation(){
		
		testChart = new TimeGraphPanel(new Date(0));
		final JFrame f = testChart.displayWindow();
		testChart.addSeries("Series1", java.awt.Color.RED);
		testChart.addSeries("Series2", java.awt.Color.BLUE);
		for(int i = 0; i < 10; i++){
			testChart.addData("Series1", Math.random());
			testChart.addData("Series2", Math.random() + 0.5);
//			step();
		}
		JButton toggle = new JButton("Toggle");
		toggle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				visible = !visible;
				testChart.setVisible("Series1", visible);
				//f.repaint();
			}
		});
		f.getContentPane().add(toggle, BorderLayout.SOUTH);
		f.getContentPane().addNotify();
		f.getContentPane().repaint();
	}
	
	
	public static void testTimeFlow(){
		
		// Test data
//		double[] data1 = new double[]{0.1, 0.5, 0.9, 0.3, 0.1};
//		double[] data2 = new double[]{0.1, 0.4, 0.9, 0.3, 0.1};
//		double[] data3 = new double[]{1.9, 0.55, 0.1232, 3.5, 9.1};
		
		testChart = new TimeGraphPanel(new Date(0));
		testChart.displayWindow();
		// Test normal operation
//		testChart.addSeries("Series1", data1, java.awt.Color.RED);
//		testChart.addSeries("Series2", data1, java.awt.Color.BLUE);
		testChart.addSeries("Series1", java.awt.Color.RED);
		testChart.addSeries("Series2", java.awt.Color.BLUE);
		if(true){
			while(true){
				testChart.addData("Series1", Math.random());
				testChart.addData("Series2", Math.random() + 0.5);
				step();
			}
		}
		step();
		testChart.addData("Series1", 1.3);
		step();
		testChart.addSeries("Series2", java.awt.Color.BLUE);
		step();
		testChart.addData("Series2", 0.2);
		testChart.addData("Series2", 0.3);
		testChart.addData("Series2", 0.4);
		testChart.addData("Series2", 0.6);
		step();
		testChart.addData("Series2", 0.3);
		step();
		testChart.removeSeries("Series1");
		step();
		testChart.addData("Series2", 0.0);
		
		step();
		
		// Test abnormal use
		testChart.removeSeries("Egg");
		testChart.addData("Egg", 0.2);
		testChart.addSeries("Egg", java.awt.Color.RED);
	}
	private static int step = 0;
	private static TimeGraphPanel testChart;
	private static void step(){
//		System.out.println("Step " + step);
		testChart.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		step++;
	}
}