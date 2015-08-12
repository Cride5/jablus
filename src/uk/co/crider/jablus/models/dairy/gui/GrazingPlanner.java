package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Experiments;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.field.Crop;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/** Represents a planner for the grazing schedule */
public class GrazingPlanner extends FieldPlanner {
	
	private int[][] grazing;

	public GrazingPlanner(SubjectDisplayParams params, Agent agent, Time time, int cowGroups) {
	    super(params, agent, time, cowGroups);
		grazing = new int[cowGroups][Time.WEEKS_YEAR + 1];
		// Generate planners on initialisation
		for(int i = 0; i < cowGroups; i++)
			genPlanner(i);
		// Set initial plan
//		setActions(
//				((jablus.models.dairy.Parameters)params.params).INIT_GPLAN_ACTIONS,
//				((jablus.models.dairy.Parameters)params.params).INIT_GPLAN_SCHEDULE);
    }

	@Override
    protected char getActionLetter(Action a){
		switch(a.id){
		case DairyAgent.ACTION_SET_GRAZING : return '+';
		case DairyAgent.ACTION_SET_CONFINED : return '-';
		}
	    return ' ';
    }

	@Override
    protected Color getColour(int row, int week){
		Color gc = params.getColour(Crop.PASTURE);
		Color cc = Color.LIGHT_GRAY;
		double p = (double)grazing[row][week] / 100;
	    return new Color(
	    		(int)(cc.getRed()   + (gc.getRed()   - cc.getRed())   * p),
	    		(int)(cc.getGreen() + (gc.getGreen() - cc.getGreen()) * p),
	    		(int)(cc.getBlue()  + (gc.getBlue()  - cc.getBlue())  * p)
	    );
    }

	@Override
    protected String getRowTitle(int row){
	    return Constants.getName(Livestock.OFFSET + row);
    }
	
	/** @inheritDoc */
	protected String getWindowTitle(){
		return "Grazing Planner";
	}

	@Override
    protected Collection<Action> getWeekActions(int row){
		Collection<Integer> actionsIds = agent.getActions(DairyAgent.LIVESTOCK_ACTION);
		Collection<Action> actions = new LinkedList<Action>();
		for(int aId : actionsIds){
			Action a = agent.getActionExecutable(aId);
			((IntegerData)a.args[0]).setValue(row);
//System.out.println("GrazingPlanner adding action:" + a);
			actions.add(a);
		}
		return actions;
    }
	
	protected boolean setActionParams(Action a){
		return true;
	}

	@Override
    protected boolean isActionPossible(int aId, int fId, int week){
	    return true;
    }

	@Override
    protected void updateState(int row) {
		int lastVal = 0;
		for(int p = 0; p < 2; p++){
			for(int i = 0; i < time.getWeeksThisYear(); i++){
				if(action[row][i] != null){
					switch(action[row][i].id){
					case DairyAgent.ACTION_SET_GRAZING :
						lastVal = 100;
						break;
					case DairyAgent.ACTION_SET_CONFINED :
						lastVal = 0;
						break;
					}
				}
				grazing[row][i] = lastVal;
			}
		}		
    }

	protected void rowSelected(int row, MouseEvent e){
		// Do nothing
	}
	
	protected void windowClosed(){
		// Do nothing
	}

	public void clearData(){
		clearActions();
		update();
	}

	/** Reads plan actions from a buffered reader */
	public void readData(BufferedReader reader) throws IOException{			
		String line = null;
		do{
			line = reader.readLine();
//System.out.println("GrazingPlanner: 1 read line:" + line);
			if(line == null) return;
		}while(!line.contains("GRAZING PLAN"));
		clearActions();
		try{
			do{
				line = reader.readLine();
//System.out.println("GrazingPlanner: x read line:" + line);
				String[] data = line.split(",\\s*");
				if(data.length < 3) break;
				int[] d = new int[data.length];
				for(int i = 0; i < data.length; i++)
					d[i] = Integer.parseInt(data[i]);
				Action a = agent.getActionExecutable(d[2]);
				((IntegerData)a.args[0]).setValue(d[0]);
				action[d[0]][d[1]] = a;
//System.out.println("GrazingPlanner: Setting action: " + a);
			}while(true);
		}catch(Exception e){ ; }
		update();
	}
	
		/** Writes plan actions to buffered writer */
	public void writeData(BufferedWriter writer) throws IOException{
		writer.write("GRAZING PLAN, Time, Action ID");
		writer.newLine();
		for(int i = 0; i < action.length; i++){
			for(int j = 0; j < action[i].length; j++){
				if(action[i][j] != null){
					writer.write(i + ", " + j + ", " + action[i][j].id);
					for(int k = 1; k < action[i][j].args.length; k++)
						writer.write(", " + ((Data0D)action[i][j].args[k]).getValue());
					writer.newLine();
				}
			}
		}
		writer.newLine();
	}

	/** For Testing */
	public static boolean running = true;
	public static void main(String[] args) {
		Constants.init();
		Time t = new Time(Time.WEEK, 0, 0, 2009);
		Parameters params = Experiments.create();
		SubjectDisplayParams displayParams = new SubjectDisplayParams(params);
		FieldPlanner p = new GrazingPlanner(displayParams, new DairyAgent("Test", 0, null, null, null, params), t, Livestock.COWS3PL + 1 - Livestock.HEIFERS01);
		p.showPlanners(null);
		
		/*		
		JablusWindow f = new JablusWindow("", false, false, true){
			public void dispose(){
				running = false;
				super.dispose();
			}
		};

		FieldPlanner p = new FieldPlanner(1, 5.6, t); 
		f.add(p);
		f.pack();
//		f.setSize(300, 55);
		f.setVisible(true);
		*/
		int i = 0;
		while(running && i++ < 504){
			try{ Thread.sleep(250); } catch(Exception e){}
//			System.out.println(t.dateString() + "\t| " + t.getFirstWeekDayOfYear() + "\t| " + t.getWeekOfYear() + "\t| " + t.getWeeksThisYear());
			p.redisplay();
			t.execStep();
/*			if(i == 5) p.setAction(1, 5, new Action(Constants.ACTION_SEW_GRASS));
			if(i == 10) p.setAction(1, 10, new Action(Constants.ACTION_PLOUGH));
			if(i == 30){
				p.setAction(1, 30, new Action(Constants.ACTION_SEW_WHEAT));
				p.setAction(1, 45, new Action(Constants.ACTION_PLOUGH));
			}
*/			
		}

	}
}
