package uk.co.crider.jablus.models.dairy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.vividsolutions.jump.feature.Feature;

import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.data.VectorData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.Popup;
import uk.co.crider.jablus.gui.data.MapView;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Experiments;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.models.dairy.env.Storage;
import uk.co.crider.jablus.models.dairy.env.field.Crop;
import uk.co.crider.jablus.utils.Utils;

/** A weekly planner for scheduling cropping actions */
public class CroppingPlanner extends FieldPlanner {

	private double[] fAreas;
	private int[][] crop;
	private ArrayList<SortedSet<Integer>> fGroups;
	private Hashtable<Integer, Integer> groupMap;
	private MapView maps;
	private VectorData fieldMap;
	private JMenu grpMenu;

	private static final Map<Integer, Character> ACTION_LETTER = new Hashtable<Integer, Character>();
	static{
		ACTION_LETTER.put(DairyAgent.ACTION_SEW_GRASS,   'G');
		ACTION_LETTER.put(DairyAgent.ACTION_SEW_WHEAT,   'W');
		ACTION_LETTER.put(DairyAgent.ACTION_SEW_MAIZE,   'M');
		ACTION_LETTER.put(DairyAgent.ACTION_PLOUGH,      'P');
		ACTION_LETTER.put(DairyAgent.ACTION_FERTILISE,   'F');
		ACTION_LETTER.put(DairyAgent.ACTION_SPREAD_SLURRY, 'S');
		ACTION_LETTER.put(DairyAgent.ACTION_HARVEST,     'H');
		ACTION_LETTER.put(DairyAgent.ACTION_GRAZING_ON,  '+');
		ACTION_LETTER.put(DairyAgent.ACTION_GRAZING_OFF, '-');
	}
		
	public CroppingPlanner(SubjectDisplayParams params, JMenu grpMenu, VectorData fieldMap, MapView maps, Agent agent, Time time, double[] fAreas){
	    super(params, agent, time, fAreas.length);
	    this.grpMenu = grpMenu;
	    this.fieldMap = fieldMap;
	    this.maps = maps;
		this.fAreas = fAreas;
		this.fGroups = new ArrayList<SortedSet<Integer>>();
		crop = new int[fAreas.length][Time.WEEKS_YEAR + 1];
		groupMap = new Hashtable<Integer, Integer>();
		// Set initial actions
//		setActions(
//				((jablus.models.dairy.Parameters)params.params).INIT_CPLAN_ACTIONS,
//				((jablus.models.dairy.Parameters)params.params).INIT_CPLAN_SCHEDULE);
	}
	
	/** @inheritDoc */
	protected void genPlanner(int row){
		// Initialise crops
		for(int i = 0; i < crop[row].length; i++)
			crop[row][i] = Crop.GRASS;
		super.genPlanner(row);
	}
	
	/** @inheritDoc */
	protected Color getColour(int row, int week){
		return week == time.getWeeksThisYear() ? Color.LIGHT_GRAY :
			//i == time.getWeekOfYear()     ? Color.GREEN :
			params.getColour(crop[row][week]);
	}
	
	/** @inheritDoc */
	protected char getActionLetter(Action a){
		return ACTION_LETTER.get(a.id);	
	}
	
	/** @inheritDoc */
	protected String getWindowTitle(){
		return "Cropping Planner";
	}
	
	/** @inheritDoc */
	protected String getRowTitle(int row){
		// Calculate total area
		double a = 0;
		for(int fId : fGroups.get(row))
			a += fAreas[fId];
		return "Group " + (row + 1) + " (" + Utils.roundString(a, 1) + " " + Constants.UNITS_AREA_HIGH + ")";
	}
	
	/** @inheritDoc */
	protected Collection<Action> getWeekActions(int row){
		Collection<Integer> actionsIds = agent.getActions(DairyAgent.FIELD_ACTION);
		Collection<Action> actions = new LinkedList<Action>();
		for(int aId : actionsIds){
			Action a = agent.getActionExecutable(aId);
			a.args[0] = new IntegerData(DairyAgent.ACTION_PARAM_FID, row);
			actions.add(a);
		}
		return actions;
	}
	
	/** @inheritDoc */
	protected boolean setActionParams(Action a){
		if(agent.isActionType(a, DairyAgent.FIELD_FERTILISE)){
			// Cannot set a permissible range because quantities available
			// aren't known yet!
			Action a2 = (Action)a.clone();
			IntegerData d = (IntegerData)Popup.queryData(null,
					Constants.getName(a.id),
					a2.args[1]);
			if(d != null && d.intValue() > 0){
				a.args[1] = d;
				return true;
			}
			return false;
		}
		return true;
	}

	/** @inheritDoc */
	protected boolean isActionPossible(int aId, int fId, int week){
		int prevWeek = week == 0 ? Time.WEEKS_YEAR - 1 : week - 1;
/*		if(aId == DairyAgent.ACTION_SEW_GRASS)
			return crop[fId][prevWeek] == Crop.FALLOW || crop[fId][prevWeek] == Crop.GRASS;
		if(aId == DairyAgent.ACTION_SEW_WHEAT)
			return crop[fId][prevWeek] == Crop.FALLOW || crop[fId][prevWeek] == Crop.WHEAT;
		if(aId == DairyAgent.ACTION_SEW_MAIZE)
			return crop[fId][prevWeek] == Crop.FALLOW || crop[fId][prevWeek] == Crop.MAIZE;
*/		if(aId == DairyAgent.ACTION_HARVEST)
			return crop[fId][prevWeek] != Crop.FALLOW;
		if(aId == DairyAgent.ACTION_GRAZING_ON)
			return crop[fId][prevWeek] == Crop.GRASS || crop[fId][prevWeek] == Crop.PASTURE;
		if(aId == DairyAgent.ACTION_GRAZING_OFF)
			return crop[fId][prevWeek] == Crop.PASTURE || crop[fId][prevWeek] == Crop.GRASS;
		if(aId == DairyAgent.ACTION_PLOUGH 
		|| aId == DairyAgent.ACTION_FERTILISE
		|| aId == DairyAgent.ACTION_SPREAD_SLURRY
		|| aId == DairyAgent.ACTION_SEW_GRASS
		|| aId == DairyAgent.ACTION_SEW_WHEAT
		|| aId == DairyAgent.ACTION_SEW_MAIZE)
			return true;
		return false;
	}
	
	/** Updates the crop array to be consistent with actions in the plan */
	protected void updateState(int row){
		// Update crop indicators
		int lastCrop = Crop.GRASS;
		for(int p = 0; p < 2; p++){
			for(int i = 0; i < time.getWeeksThisYear(); i++){
				if(action[row][i] != null){
					switch(action[row][i].id){
					case DairyAgent.ACTION_SEW_GRASS :
						lastCrop = lastCrop == Crop.PASTURE ? Crop.PASTURE : Crop.GRASS;
						break;
					case DairyAgent.ACTION_GRAZING_ON :
						lastCrop = Crop.PASTURE;
						break;
					case DairyAgent.ACTION_GRAZING_OFF :
						lastCrop = Crop.GRASS;
						break;
					case DairyAgent.ACTION_SEW_WHEAT :
						lastCrop = Crop.WHEAT;
						break;
					case DairyAgent.ACTION_SEW_MAIZE :
						lastCrop = Crop.MAIZE;
						break;
					case DairyAgent.ACTION_PLOUGH :
						lastCrop = Crop.FALLOW;
						break;
					case DairyAgent.ACTION_HARVEST :
						if(lastCrop != Crop.GRASS && lastCrop != Crop.PASTURE)
							lastCrop = Crop.FALLOW;
						break;
					}
				}
				crop[row][i] = lastCrop;
			}
		}		
	}
	
	/** Override to show all created group plans */
	public void showPlanners(Component parent){
		SortedSet<Integer> grpToShow = new TreeSet<Integer>();
		for(int i = 0; i < fGroups.size(); i++){
			// Only show groups with fields
			if(fGroups.get(i).size() > 0)
				grpToShow.add(i);
		}
		super.showPlanners(parent, grpToShow);
		if(fGroups.size() > 0) fireRowSelected(0, null);
	}
	
	/** Override show planners to show field groups instead of fields */
	public void showPlanners(Component parent, SortedSet<Integer> toShow){
		// Field groups to display
		SortedSet<Integer> grpToShow = new TreeSet<Integer>();
		for(int fId : toShow){
			if(groupMap.containsKey(fId))
				grpToShow.add(groupMap.get(fId));
		}
		// If no groups selected then create new group for selected items
		if(grpToShow.size() == 0){
			createGroup(toShow);
			grpToShow.add(fGroups.size() - 1);
		}
		super.showPlanners(parent, grpToShow);
		if(grpToShow.size() > 0){
//for(int i = 0; i < fGroups.size(); i++)
//	System.out.println("Group " + i + " gui init?" + isPlanGuiInitialised(i));
			fireRowSelected(grpToShow.first(), null);
		}
//System.out.println("CroppingPlanner: Showing fields: " + toShow);
	}

	/** Override show planners to show field groups instead of fields */
	public void createGroup(SortedSet<Integer> toAdd){
		for(SortedSet<Integer> group : fGroups){
			for(int fId : toAdd){
				if(group.contains(fId)){
					group.remove(fId);
				}
			}
		}
		final int gId = fGroups.size();
//System.out.println("Create group from: " + toAdd + ", fGroups=" + fGroups);
		fGroups.add(gId, toAdd);
		genGroupMap();
		// Add new group to group menu
		JMenuItem mItem = new JMenuItem("Group " + (gId + 1));
		mItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				TreeSet<Integer> selected = new TreeSet<Integer>();
				for(Object f : maps.getLayerViewPanel().getSelectionManager().getFeaturesWithSelectedItems(
						maps.getLayerViewPanel().getLayerManager().getLayer(Constants.getName(Constants.STATIC_FIELD_MAP))))
					selected.add(((Feature)f).getID() - maps.getVectorData(Constants.STATIC_FIELD_MAP).getFidOffset());
				addFieldsToGroup(gId, selected);
			}
		});
		grpMenu.add(mItem);
		// Generate gui components for plan
		if(!isPlanGuiInitialised(gId)){
			genPlanner(gId);
		}
//System.out.println("CroppingPlanner: Creating group, from fields:" + toAdd + "\n\nnew groups=" + fGroups + "\n\ngroupMap=" + groupMap);
	}
	
	public void addFieldsToGroup(int gId, SortedSet<Integer> toAdd){
		if(gId < 0 || gId >= fGroups.size()) return;
		// Remove any items for new group from existing groups
		for(SortedSet<Integer> group : fGroups){
			for(int fId : toAdd){
				if(group.contains(fId)){
					group.remove(fId);
				}
			}
		}
		fGroups.get(gId).addAll(toAdd);
		genGroupMap();
//System.out.println("CroppingPlanner: Added to group:" + gId + " fields:" + toAdd + "\n\nnew groups=" + fGroups + "\n\ngroupMap=" + groupMap);
	}
	
	/** Re-generates the group map */
	private void genGroupMap(){
		groupMap.clear();
		for(int i = 0; i < fGroups.size(); i++){
			for(int fId : fGroups.get(i)){
				groupMap.put(fId, i);
			}
		}
	}
	
	public void removeFromGroup(SortedSet<Integer> toRemove){
		// Remove any items from existing groups
		for(SortedSet<Integer> group : fGroups){
			for(int fId : toRemove){
				if(group.contains(fId)){
					group.remove(fId);
				}
			}
		}
		genGroupMap();
	}
	
	public int getNumGroups(){
		return fGroups.size();
	}
	
	/** Hilight fields group on map */
	protected void rowSelected(final int row, MouseEvent e){
		if(e != null && e.getButton() == MouseEvent.BUTTON3){
			JPopupMenu m = new JPopupMenu();
			JLabel title = new JLabel("Swap with:");
			title.setMaximumSize(new Dimension(200, 18));
			SubjectDisplayParams.setStyle(title, SubjectDisplayParams.INFO);
			m.add(title);
			for(int gid = 0; gid < fGroups.size(); gid++){
				final int row_ = gid;
				JMenuItem item = new JMenuItem("Group " + (gid + 1));
				item.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						swapPlans(row, row_);
					}
				});
				if(gid == row) item.setEnabled(false);
				m.add(item);
			}
			m.show(e.getComponent(), e.getX(), e.getY());
		}
		Collection<Feature> toSelect = new LinkedList<Feature>();
		for(int fId : fGroups.get(row))
			toSelect.add(fieldMap.getFeature(fId));
		maps.getLayerViewPanel().getSelectionManager().getFeatureSelection().unselectItems();
		maps.getLayerViewPanel().getSelectionManager().getFeatureSelection().selectItems(
				maps.getLayerViewPanel().getLayerManager().getLayer(fieldMap.getName()), toSelect);		
	}
	
	/** @inheritDoc */
	protected void windowClosed(){
		// Deselect fields
		maps.getLayerViewPanel().getSelectionManager().getFeatureSelection().unselectItems();
	}
	
	/** @inheritDoc */
	public Action getAction(int fId){
		if(!groupMap.containsKey(fId)) return null;
		int gId = groupMap.get(fId);
		Action ga = super.getAction(gId);
		if(ga == null) return null;
		Action a = (Action)ga.clone();
		// Set appropriate field Id parameter
		((Data0D.Integer)a.args[0]).setValue(fId);
		return a;
	}
	
	public void clearData(){
		fGroups.clear();
		groupMap.clear();
		clearActions();
		for(int i = 0; i < grpMenu.getComponentCount(); i++){
			Component c = grpMenu.getComponent(i);
			if(c instanceof JMenuItem && ((JMenuItem)c).getText().startsWith("Group"))
				grpMenu.remove(c);
		}		
	}
	
	/** Reads plan actions from a buffered reader */
	public void readData(BufferedReader reader) throws IOException{
		// Read field groups
		String line = null;
		do{
			line = reader.readLine();
//System.out.println("CroppingPlanner (fgroups): 1 read line:" + line);
			if(line == null) return;
		}while(!line.contains("FIELD GROUPS"));
		fGroups.clear();
		clearActions();
		for(int i = 0; i < grpMenu.getComponentCount(); i++){
			Component c = grpMenu.getComponent(i);
			if(c instanceof JMenuItem && ((JMenuItem)c).getText().startsWith("Group"))
				grpMenu.remove(c);
		}
		try{
			do{
				line = reader.readLine();
//System.out.println("CroppingPlanner (fgroups): x read line:" + line);
				String[] data = line.split("\"?\\s*,\\s*\"?");
				if(data.length < 2) break;
				TreeSet<Integer> grp = new TreeSet<Integer>();
				for(int i = 1; i < data.length; i++)
					grp.add(Integer.parseInt(data[i]));
				createGroup(grp);
				int gId = fGroups.size() -1;
//System.out.println("CroppingPlanner (fgroups): created group: " + data[0] + "=" + grp);
			}while(true);
		}catch(Exception e){ e.printStackTrace(); }
		genGroupMap();
		// Read cropping actions
		do{
			line = reader.readLine();
//System.out.println("CroppingPlanner (cplan): 1 read line:" + line);
			if(line == null) return;
		}while(!line.contains("CROPPING PLAN"));
		try{
			do{
				line = reader.readLine();
//System.out.println("CroppingPlanner (cplan): x read line:" + line);
				String[] data = line.split("\"?\\s*,\\s*\"?");
				if(data.length < 3) break;
				int[] d = new int[data.length];
				for(int i = 0; i < data.length; i++)
					d[i] = Integer.parseInt(data[i]);
				Action a = agent.getActionExecutable(d[2]);
				((IntegerData)a.args[0]).setValue(d[0]);
				if(d.length >= 4)
					((IntegerData)a.args[1]).setValue(d[3]);
				action[d[0]][d[1]] = a;
//System.out.println("CroppingPlanner (cplan): Setting action: " + a);
			}while(true);
		}catch(Exception e){ ; }
		grpMenu.revalidate();
		// Generate gui components if not done already
		for(int i = 0; i < fGroups.size(); i++)
			if(!isPlanGuiInitialised(i))
				genPlanner(i);
		update();
	}

	/** Writes plan actions to buffered writer */
	public void writeData(BufferedWriter writer) throws IOException{
		writer.write("FIELD GROUPS, Fields...");
		writer.newLine();
		for(int i = 0; i < fGroups.size(); i++){
			writer.write("" + i);
			for(int j : fGroups.get(i)){
				writer.write(", " + j);
			}
			writer.newLine();
		}
		writer.newLine();
		writer.write("CROPPING PLAN, Time, Action ID, Quantity");
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
		SortedSet<Integer> toShow = new TreeSet<Integer>();
		toShow.add(1);
		toShow.add(3);
		Time t = new Time(Time.WEEK, 0, 0, 2009);
		Parameters params = Experiments.create();
		SubjectDisplayParams displayParams = new SubjectDisplayParams(params);
		FieldPlanner p = new CroppingPlanner(displayParams, null, null, null, new DairyAgent("Test", 0, null, null, null, params), t, new double[]{3.4, 2.5, 8.3, 2.59999});
		p.showPlanners(null, toShow);
		

		int i = 0;
		while(running && i++ < 504){
			try{ Thread.sleep(250); }
			catch(Exception e){}
//			System.out.println(t.dateString() + "\t| " + t.getFirstWeekDayOfYear() + "\t| " + t.getWeekOfYear() + "\t| " + t.getWeeksThisYear());
			p.redisplay();
			t.execStep();
/*			if(i == 5) p.setAction(1, 5, new Action(DairyAgent.ACTION_SEW_GRASS));
			if(i == 10) p.setAction(1, 10, new Action(DairyAgent.ACTION_PLOUGH));
			if(i == 30){
				p.setAction(1, 30, new Action(DairyAgent.ACTION_SEW_WHEAT));
				p.setAction(1, 45, new Action(DairyAgent.ACTION_PLOUGH));
			}
*/			
		}

	}

}
