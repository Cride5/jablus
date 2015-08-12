package uk.co.crider.jablus.gui.data;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.Data1D;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.jfreechart.AreaGraphPanel;
import uk.co.crider.jablus.gui.jfreechart.GraphPanel;
import uk.co.crider.jablus.gui.jfreechart.TimeGraphPanel;
import uk.co.crider.jablus.gui.sim.SimulationDisplayParams;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

/** Represents a graph wich can be added to a DataView */
public class GraphItem extends DataViewItem{
	
	/** Unique class ID */
    private static final long serialVersionUID = 2935938294627244452L;

    private boolean canEdit;
    private Hashtable<String, Data> data;
    private DataView view;
    private GraphPanel graph;
    private DisplayParams displayParams;
    private int id;
	
	public GraphItem(int id, String units, DataView view, DisplayParams displayParams, Date startTime, boolean canEdit, boolean displayLegend, boolean itemsEditable){
		super(new BorderLayout());
		this.id = id;
		this.view = view;
		this.displayParams = displayParams;
		this.canEdit = canEdit;
		this.data = new Hashtable<String, Data>();
		String title = Constants.getName(id);
		String xAxisLabel = null; //Constants.TEMPORAL_TIME + " [" + Time.TIME_UNITS.get(displayParams.params.TIME_STEP) + "]";
		String yAxisLabel = Constants.getName(id) + " (" + units + ")";
		if(displayParams.display(id, DisplayParams.AREA_GRAPH)){
			graph = new AreaGraphPanel(title, xAxisLabel, yAxisLabel, false);
		}
		else if(displayParams.display(id, DisplayParams.TIME_GRAPH)){
			title = title  + " " + "(" + units + ")";
			graph = new TimeGraphPanel(title, null, null, startTime, false, displayLegend, itemsEditable);
		}
		else{
			title = title + "(" + Time.TIME_UNITS.get(displayParams.params.TIME_STEP) + ")";
			graph = new GraphPanel(title, xAxisLabel, yAxisLabel, false, displayLegend);
		}
		initComponents();
	}
	
	private void initComponents(){
		add(graph, BorderLayout.CENTER);
		final JPanel item = this;
		if(canEdit){
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					view.hideItem(item);
				}
			});
		}
		setPreferredSize(new Dimension(SimulationDisplayParams.DATA_PANEL_WIDTH, SimulationDisplayParams.GRAPH_ITEM_HEIGHT));
	}
	
	public GraphPanel getGraph(){
		return graph;
	}

	public void addData(Data data, List<Data> pastValues, List<Data> time){
		if(data instanceof Data0D){
			Paint plotPaint = displayParams.getPaint(data.getId());
			graph.addSeries(data.getName(), plotPaint);
		}
		if(data instanceof Data1D){
			Paint[] plotPaints = displayParams.getPaint1D(data.getId());
			for(int i = 0; i < ((Data1D)data).getLength(); i++){
				Paint plotPaint = null;
				if(plotPaints == null || plotPaints[i] == null)
					plotPaint = DisplayParams.DEFAULT_PAINT;
				else
					plotPaint = plotPaints[i];
				graph.addSeries(((Data1D)data).getRawHeader()[i], plotPaint);
			}
		}
		this.data.put(data.getName(), data);
		// Populate graph from past data
//System.out.println("GraphItem: " + data.getNameId() + ",\t pastValues=" + pastValues + ", time=" + time);
		if(pastValues != null && pastValues.size() > 0){
			if(time != null && time.size() > 0){
				Iterator ti = time.iterator();
				Iterator vi = pastValues.iterator();
//System.out.println("GraphItem: time="+time);
				if(data instanceof Data0D){
					int t, i = 1, iLast = 1;
					double v = 0, vLast = 0;
					while(vi.hasNext()){
						// Retrive time value
						t = ((Data0D.Integer)ti.next()).intValue();
//System.out.println("GraphItem: t=" + t);
						// Retrive data value
						Data0D d = (Data0D)vi.next();
						// Don't add the 0'th value
						if(t == 0) continue;
						// Get numerical value
						if(d instanceof Data0D.Integer)
							v = ((Data0D.Integer)d).intValue();
						else if(d instanceof Data0D.Double)
							v = ((Data0D.Double)d).doubleValue();
						else
							System.out.println("GraphItem: Data0D object " + data.getName() + " not supported.");
						double vItp = vLast; // Interpolated value
						double vDiff = v - vLast; // Difference between last value and current
						// If first value then assume vLast is same as this value (as opposed to 0)
						if(i == 1){
							vLast = v;
							vDiff = 0;
							vItp = v;
						}
						while(i <= t){
							if(t > iLast && i > iLast && vDiff > 0)
								vItp = vLast + (vDiff) * ((i - iLast)/(t - iLast));
							graph.addData(data.getName(), vItp);
							i++;
						}
						vLast = v;
						iLast = i;
					}
				}else if(data instanceof Data1D){
					int t, i = 1, iLast = 1, len = ((Data1D)data).getLength();
					double[] v = new double[len], vLast = new double[len], vDiff = new double[len], vItp = new double[len];
					while(vi.hasNext()){
						// Retrive time value
						t = ((Data0D.Integer)ti.next()).intValue();
						// Retrive data value
						Data1D d = (Data1D)vi.next();
						// Don't add the 0'th value
						if(t == 0) continue;
						// Get numerical values
						if(d instanceof Data1D.Integer){
							// Get values and copy to v
							int[] vInt = ((Data1D.Integer)d).getValues();
							for(int j = 0; j < len; j++) v[j] = vInt[j];
						}
						else if(d instanceof Data1D.Double){
							v = ((Data1D.Double)d).getValues();
						}
						else
							System.out.println("GraphItem: Data1D object " + data.getName() + " not supported.");
						// If first value then assume vLast is same as this value (as opposed to 0)
						if(i == 1){
							vLast = v;
							vItp = v;
							vDiff = new double[len];
						}
						else{
							for(int j = 0; j < len; j++){
								vDiff[j] = v[j] - vLast[j]; // Difference between last value and current
								vItp[j] = vLast[j]; // Interpolated value
							}
						}
						while(i <= t){
							// For each value
							for(int j = 0; j < len; j++){
								// Calculate interpolated value
								if(t > iLast && i > iLast && vDiff[j] > 0)
									vItp[j] = vLast[j] + (vDiff[j]) * ((i - iLast)/(t - iLast));
								// Graph it
								graph.addData(((Data1D)data).getRawHeader()[j], vItp[j]);
							}
							i++;
						}
						vLast = v;
						iLast = i;
					}
				}else{
					System.out.println("GraphItem: Data object " + data.getName() + " not supported.");
				}
			}
			else{
				for(Data iPast : pastValues){
					redisplay(iPast);
				}
			}
			redisplay();
		}
	}
	
	public void setData(Data data){
		// Do nothing, graphs are made up of incramental data
/*		remove(this.data.getGraph());
		this.data = data;
		add(data.getGraph(), BorderLayout.CENTER);
		revalidate();
		repaint();
*/	}

	public void redisplay(){
		for(Data d : data.values())
			redisplay(d);
	}
	public void redisplay(Data data){
//System.out.println("redisplaying " + data.getNameId());
		if(data instanceof Data0D.Double){
			graph.addData(data.getName(), ((Data0D.Double)data).doubleValue());
		}if(data instanceof Data0D.Integer)
			graph.addData(data.getName(), ((Data0D.Integer)data).intValue());
		if(data instanceof Data1D.Integer){
			for(int i = 0; i < ((Data1D)data).getLength(); i++){
				graph.addData(((Data1D)data).getRawHeader()[i], ((Data1D.Integer)data).getValue(i));
			}
		}
		graph.repaint();
	}
	
	public int dataId(){
		return id;
	}
	
	public String toString(){
		return "GraphItem(" + data + ")";
	}
	
}
