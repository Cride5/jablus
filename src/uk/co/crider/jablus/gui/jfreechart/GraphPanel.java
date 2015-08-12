package uk.co.crider.jablus.gui.jfreechart;

import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/** Used for drawing line graphs */
public class GraphPanel extends JLabel{

	/** Unique class ID */
    private static final long serialVersionUID = 8070913801773469897L;
    
	/** Chart associated with this graph window */
	protected String title;
	protected JFreeChart chart;
	protected Hashtable<String, Integer> seriesIds;
	protected boolean drawLegend;
	
	/** Series counter */
	protected int seriesCount;
	
	public GraphPanel() { this(null, null, null, true); }
	public GraphPanel(String title, String xlabel, String ylabel) { this(title, xlabel, ylabel, true); }
	public GraphPanel(String title, String xlabel, String ylabel, boolean window) { this(title, xlabel, ylabel, window, true); }
	public GraphPanel(String title, String xlabel, String ylabel, boolean window, boolean drawLegend) {
		super();
		this.drawLegend = drawLegend;
		this.chart = ChartFactory.createXYLineChart(title, // Title
			xlabel, // X-Axis label
			ylabel, // Y-Axis label
			new XYSeriesCollection(), // Dataset
			PlotOrientation.VERTICAL, // Orientation
			drawLegend, // Show legend
			false, // Tooltips
			false // url
		);
		if(chart.getTitle() != null)
			chart.getTitle().setFont(SimulationDisplayParams.DEFAULT_FONT);
		this.title = title;
		this.seriesIds = new Hashtable<String, Integer>();
		this.seriesCount = 0;
		if(window)
			setIcon(new ImageIcon(chart.createBufferedImage(500,300)));
	}
	/** Constructor for cloned object */
	protected GraphPanel(
		String title,
		JFreeChart chart,
		Hashtable<String, Integer> seriesIds,
		int seriesCount,
		boolean drawLegend){
		this.title = title;
		this.chart = null;
		this.seriesIds = (Hashtable<String, Integer>)seriesIds.clone();
		this.seriesCount = seriesCount;
		this.drawLegend = drawLegend;
		try{
			XYPlot plot = chart.getXYPlot();
			this.chart = new JFreeChart(
					chart.getTitle().getText(),
					chart.getTitle().getFont(),
					new XYPlot(
							(XYDataset)((XYSeriesCollection)plot.getDataset()).clone(),
							(ValueAxis)plot.getDomainAxis().clone(),
							(ValueAxis)plot.getRangeAxis().clone(),
							plot.getRenderer()),
					drawLegend);
//			this.chart = (JFreeChart)chart.clone();
		}
		catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
	}
	/** Clone the object */
	public Object clone(){
		return new GraphPanel(title, chart, seriesIds, seriesCount, drawLegend);
	}
	
	/** @inheritDoc */
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
	
	/** Displays a stand alone window containing this graph */
	public JFrame displayWindow(){ return displayWindow(0, 0); }
	public JFrame displayWindow(int x, int y){
		JFrame window = new JFrame();
		window.setTitle(title);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout());
		window.getContentPane().add(this, BorderLayout.CENTER);
		window.pack();
		window.setLocation(x, y);
		window.setVisible(true);
		return window;
	}
	
	/** Add a data series */
	public void addSeries(String name, Paint linePaint){ addSeries(name, null, linePaint); }
	/** Add a data series with the given data */
	public void addSeries(String name, double[] data, Paint linePaint){
//System.out.println("GraphPanel: Adding series " + name + "=" + Utils.arrayString(data));
//Thread.currentThread().dumpStack();
		XYSeriesCollection dataset = (XYSeriesCollection)chart.getXYPlot().getDataset(0);
		XYSeries series = new XYSeries(name, false, false);
		if(data != null){
			for(int j = 0; j < data.length; j++){
				series.add(j, data[j]);
			}
		}

		dataset.addSeries(series);
		seriesIds.put(name, seriesCount);
		chart.getXYPlot().getRenderer().setSeriesPaint(seriesCount, linePaint);
		seriesCount++;
		// Set renerer
	}
	
	/** Remove a data series */
	public void removeSeries(String name){
		XYSeriesCollection dataset = (XYSeriesCollection)chart.getXYPlot().getDataset(0);
		int id = ((Integer)seriesIds.get(name)).intValue();
		dataset.removeSeries(id);
		// Decrament all ids greater than id
		for(Enumeration i = seriesIds.keys(); i.hasMoreElements();){
			String k = (String)i.nextElement();
			int tmpId = seriesIds.get(k);
			if(tmpId > id)
				seriesIds.put(k, tmpId - 1);
		}
		seriesCount--;
	}
	
	/** Add a data item to the end of a series */
	public void addData(String name, double value){
//System.out.println("GraphPanel: Adding data " + name + "=" + value);
//Thread.currentThread().dumpStack();
		XYSeriesCollection dataset = (XYSeriesCollection)chart.getXYPlot().getDataset(0);
		int id = seriesIds.get(name);
		XYSeries series = dataset.getSeries(id); 
		series.add(series.getItemCount(), value);
	}
	
	/** Remove a data item from the end of a series */
	public void removeData(String name){
		XYSeriesCollection dataset = (XYSeriesCollection)chart.getXYPlot().getDataset(0);
		int id = seriesIds.get(name);
		XYSeries series = dataset.getSeries(id); 
		series.remove(series.getItemCount()-1);
	}
	
	/** Tests the operation of the GraphPanel object */
	public static void main(String[] args){
		
		// Test data
		double[] data1 = new double[]{0.1, 0.5, 0.9, 0.3, 0.1};
//		double[] data2 = new double[]{0.1, 0.4, 0.9, 0.3, 0.1};
//		double[] data3 = new double[]{1.9, 0.55, 0.1232, 3.5, 9.1};
		
		testChart = new GraphPanel();
		testChart.displayWindow();
		// Test normal operation
		testChart.addSeries("Series1", data1, java.awt.Color.RED);
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
	private static GraphPanel testChart;
	private static void step(){
		System.out.println("Step " + step);
		testChart.repaint();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		step++;
	}
}