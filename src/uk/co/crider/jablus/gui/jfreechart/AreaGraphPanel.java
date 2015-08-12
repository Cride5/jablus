package uk.co.crider.jablus.gui.jfreechart;

import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;

import java.awt.Paint;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;


/** For drawing area graphs */
public class AreaGraphPanel extends GraphPanel{
	
	/** Unique class ID */
    private static final long serialVersionUID = 7067585857305172101L;
    
	private Hashtable<String, Integer> seriesLengths;

	public AreaGraphPanel() { this(null, null, null, false); }
	public AreaGraphPanel(String title, String xlabel, String ylabel) { this(title, xlabel, ylabel, false); }
	public AreaGraphPanel(String title, String xlabel, String ylabel, boolean drawLegend) {
		super(title, xlabel, ylabel, false, drawLegend);
		chart = ChartFactory.createStackedXYAreaChart(title, // Title
				xlabel, // X-Axis label
				ylabel, // Y-Axis label
				new DefaultTableXYDataset(false), // Dataset
				PlotOrientation.VERTICAL, // Orientation
				drawLegend, // Show legend
				false, // Tooltips
				false // url
			);
		if(chart.getTitle() != null)
			chart.getTitle().setFont(SimulationDisplayParams.DEFAULT_FONT);
		this.seriesLengths = new Hashtable<String, Integer>();
	}
	/** Constructor for cloned object */
	private AreaGraphPanel(
			String title,
			JFreeChart chart,
			Hashtable<String, Integer> seriesIds,
			int seriesCount,
			boolean drawLegend,
			Hashtable<String, Integer> seriesLengths){
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
								(XYDataset)((DefaultTableXYDataset)plot.getDataset()).clone(),
								(ValueAxis)plot.getDomainAxis().clone(),
								(ValueAxis)plot.getRangeAxis().clone(),
								plot.getRenderer()),
						drawLegend);
//				this.chart = (JFreeChart)chart.clone();
			}
			catch(CloneNotSupportedException e){
				e.printStackTrace();
			}
		this.seriesLengths = (Hashtable<String, Integer>)seriesLengths.clone(); 
	}
	/** Clone the object */
	public Object clone(){
		return new AreaGraphPanel(title, chart, seriesIds, seriesCount, drawLegend, seriesLengths);
	}
	
	/** Add a data series, */
	public void addSeries(String name, Paint linePaint){ addSeries(name, null, linePaint); }
	public void addSeries(String name, double[] data, Paint linePaint){
		DefaultTableXYDataset dataset = (DefaultTableXYDataset)chart.getXYPlot().getDataset(0);
		XYSeries series = new XYSeries(name, false, false);
		if(data != null){
			for(int j = 0; j < data.length; j++){
				series.add(new Integer(j), new Double(data[j]));
			}
			seriesLengths.put(name, data.length);
		}
		else
			seriesLengths.put(name, 0);

		dataset.addSeries(series);
		seriesIds.put(name, new Integer(seriesCount));
		chart.getXYPlot().getRenderer().setSeriesPaint(seriesCount, linePaint);
		seriesCount++;
		// Set renerer
	}
	
	/** Remove a data series */
	public void removeSeries(String name){
		DefaultTableXYDataset dataset = (DefaultTableXYDataset)chart.getXYPlot().getDataset(0);
		int id = ((Integer)seriesIds.get(name)).intValue();
		dataset.removeSeries(id);
		// Decrament all ids greater than id
		for(Enumeration i = seriesIds.keys(); i.hasMoreElements();){
			String k = (String)i.nextElement();
			int tmpId = ((Integer)seriesIds.get(k)).intValue();
			if(tmpId > id)
				seriesIds.put(k, new Integer(tmpId - 1));
		}
		seriesCount--;
	}
	
	/** Add a data item to the end of a series */
	public void addData(String name, int value){ addData(name, (double)value); }
	public void addData(String name, double value){
		DefaultTableXYDataset dataset = (DefaultTableXYDataset)chart.getXYPlot().getDataset(0);
		int id = seriesIds.get(name);
		int sLen = seriesLengths.get(name);
		XYSeries series = dataset.getSeries(id);
		series.addOrUpdate(sLen, value);
		seriesLengths.put(name, ++sLen);
//System.out.println(id+":"+toString(series));
	}
	

	/** Tests the operation of the GraphPanel object */
	public static void main(String[] args){
		
		// Test data
		double[] data1 = new double[]{0.1, 0.5, 0.9, 0.3, 0.1};
//		double[] data2 = new double[]{0.1, 0.4, 0.9, 0.3, 0.1};
//		double[] data3 = new double[]{1.9, 0.55, 0.1232, 3.5, 9.1};
		double[] data4 = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		
		testChart = new AreaGraphPanel();
		testChart.displayWindow();
		// Test normal operation
		if(false){
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
			testChart.addSeries("Series3", data1, java.awt.Color.YELLOW);
			testChart.addSeries("Series4", data4, java.awt.Color.GREEN);
			testChart.addSeries("Series5", data1, java.awt.Color.MAGENTA);
			step();
			testChart.removeSeries("Series2");
			testChart.removeSeries("Series3");
			testChart.removeSeries("Series4");
			testChart.removeSeries("Series5");
			step();
		}
		
		if(true){
			testChart.addSeries("Series6", java.awt.Color.BLUE);
			testChart.addSeries("Series7", java.awt.Color.RED);
			testChart.addData("Series6", 1.0);
			testChart.addData("Series7", 0.0);
			step();
			testChart.addData("Series6", 1.0);
			testChart.addData("Series7", 0.0);
			step();
			testChart.addData("Series6", 1.0);
			testChart.addData("Series7", 1.0);
			step();
			testChart.addData("Series6", 0.0);
			testChart.addData("Series7", 1.0);
			step();
			testChart.addData("Series6", 0.0);
			testChart.addData("Series7", 1.0);
			step();
		}
		
		if(true){
			double[] data8 = new double[]{1.0, 1.0, 1.0, 0.0, 0.0};
			double[] data9 = new double[]{0.0, 0.0, 1.0, 1.0, 1.0};
			testChart.addSeries("Series8", data8, java.awt.Color.BLUE);
			testChart.addSeries("Series9", data9, java.awt.Color.RED);
		}
		
		// Test abnormal use
		if(false){
			testChart.removeSeries("Egg");
			testChart.addData("Egg", 0.2);
			testChart.addSeries("Egg", java.awt.Color.RED);
		}
	}
	private static int step = 0;
	private static AreaGraphPanel testChart;
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
	
/*
	private static String toString(XYSeries s){
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < s.getItemCount(); i++){
			buf.append(s.getY(i)+",");
		}
		return buf.toString();
	}
*/
	
}