package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.SimulationManager;
import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.data.DataLabel;
import uk.co.crider.jablus.gui.data.DataTextField;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.CowDigestion;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Panel for management of livestock feeding regime */
class LivestockFeedPanel extends JPanel {
	
	/** Unique class ID */
    private static final long serialVersionUID = 402437962456050802L;
    
//	private SubjectInterface iface;
	private JablusWindow window;
	private DairyAgent agent;
//	private CowDigestion digest;
//	private DisplayParams params;
	private Data0D.Integer[] cOff;
	private Data0D.Double[] cCon;
	private Data0D.Double[] cRem;
	private Data0D.Integer[] gOff;
	private Data0D.Double[] gCon;
	private Data0D.Double[] gRem;
	private Data0D.Integer yTrg;
	private Data0D.Integer yAct;
	private Data0D.Integer ySrp;
	private List<DataLabel> labels;
	private List<DataTextField> inputs;
	private JCheckBox[] grazingOn;
	private Livestock livestock;
	private CowDigestion digest;
	
	
	public LivestockFeedPanel(DisplayParams params, SubjectInterface iface_, DairyAgent agent_){
		super();
//		this.iface = iface_;
		this.agent = agent_;
//		this.params = params;
		labels = new LinkedList<DataLabel>();
		inputs = new LinkedList<DataTextField>();
		
		
//		SubjectInterface.genInputPanel(this, "Livestock Management");
		
/*		JPanel p = SubjectInterface.genInputPanel(new JPanel(), "Livestock Management");
    	p.add(new Field("Heifers Confined", 130, SubjectDisplayParams.WEST_WIDTH - 130 - 50 - 5, 50, "%"));
    	p.add(new Field("Cows Confined", 130, SubjectDisplayParams.WEST_WIDTH - 130 - 50 - 5, 50, "%"));
    	p.add(new Field("Feed Concentrates", 130, SubjectDisplayParams.WEST_WIDTH - 130 - 50 - 5, 50, "%"));
    	p.add(new Field("Feed Roughage", 130, SubjectDisplayParams.WEST_WIDTH - 130 - 50 - 5, 50, "%"));
*/		
		
	}
	
	/** Sets the cow digestion model */
	void setCowDigestion(CowDigestion digest){
		this.digest = digest;
		GridBagLayout layout = new GridBagLayout(); 
		GridBagConstraints c = new GridBagConstraints();
//		c.ipadx = 5;
//		c.ipady = 2;
		Insets padded = new Insets(1, 5, 1, 5);
		Insets toEdge = new Insets(5, 0, 0, 0);
		c.insets = new Insets(0, 0, 0, 0);
		setLayout(layout);
		JLabel conTitle = new JLabel(" Confined Livestock (" + Constants.UNITS_FEED + ")");
//		conTitle.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, 24));
		SubjectDisplayParams.setStyle(conTitle, SubjectDisplayParams.TITLE);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(conTitle, c);
		add(conTitle);
		c.fill = GridBagConstraints.NONE;
		c.insets = padded;
		String[] headings = {"", "Offered", "Consumed", "Surplus"};
		for(int i = 0; i < headings.length; i++){
			JLabel heading = new JLabel(headings[i]);
			SubjectDisplayParams.setStyle(heading, DisplayParams.SMALL);
			c.gridwidth = i < headings.length - 1 ? 1 : GridBagConstraints.REMAINDER;
			layout.setConstraints(heading, c);
			add(heading);
		}
		cOff = digest.getOfferedConfined();
		cCon = digest.getConsumedConfined();
		cRem = digest.getSurplusConfined();
		for(int i = 0; i < cOff.length; i++){
//			cRem[i] = new DoubleData(0, cOff[i].intValue() - cCon[i].doubleValue());
			genFeedControl(cOff[i], cCon[i], cRem[i], DairyAgent.ACTION_FEED_CONFINED, layout, c);
		}
		JLabel grzTitle = new JLabel(" Grazing Livestock  (" + Constants.UNITS_FEED + ")");
//		grzTitle.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, 24));
		SubjectDisplayParams.setStyle(grzTitle, SubjectDisplayParams.TITLE);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = toEdge;
		layout.setConstraints(grzTitle, c);
		add(grzTitle);
		c.fill = GridBagConstraints.NONE;
		c.insets = padded;
//		JLabel headings = new JLabel("                            Offered       Consumed  Remaining");
//		SubjectDisplayParams.setStyle(headings, DisplayParams.SMALL);
//		add(headings);
		gOff = digest.getOfferedGrazing();
		gCon = digest.getConsumedGrazing();
		gRem = digest.getSurplusGrazing();
		for(int i = 0; i < gOff.length; i++){
//			gRem[i] = new DoubleData(0, gOff[i].intValue() - gCon[i].doubleValue());
			genFeedControl(gOff[i], gCon[i], gRem[i], DairyAgent.ACTION_FEED_GRAZING, layout, c);
		}
		JLabel yieldTitle = new JLabel(" Annual Milk Production  (" + Constants.UNITS_MILK_YIELD_DAY + ")");
//		yieldTitle.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, 24));
		SubjectDisplayParams.setStyle(yieldTitle, DisplayParams.TITLE);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = toEdge;
		layout.setConstraints(yieldTitle, c);
		add(yieldTitle);
		c.fill = GridBagConstraints.NONE;
		c.insets = padded;
		String[] yHead = {"", "Target     ", "Actual", "Surplus"};
		for(int i = 0; i < yHead.length; i++){
			JLabel head = new JLabel(yHead[i]);
			SubjectDisplayParams.setStyle(head, DisplayParams.SMALL);
			c.gridwidth = i < yHead.length - 1 ? 1 : GridBagConstraints.REMAINDER;
			layout.setConstraints(head, c);
			add(head);
		}
		yTrg = digest.getTargetYield();
		yAct = digest.getActualYield();
		ySrp = new IntegerData(0, yAct.intValue() - yTrg.intValue());
		genFeedControl(yTrg, yAct, ySrp, DairyAgent.ACTION_FEED_TYIELD, layout, c);
		
		// Grazing management
		JLabel grazing = new JLabel(" Grazing Management");
//		grazing.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, 24));
		SubjectDisplayParams.setStyle(grazing, DisplayParams.TITLE);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = toEdge;
		layout.setConstraints(grazing, c);
		add(grazing);
		c.fill = GridBagConstraints.NONE;
		c.insets = padded;

		// Add confinement checkboxes
		JLabel cHead = new JLabel("Allow Grazing?");
		SubjectDisplayParams.setStyle(cHead, DisplayParams.SMALL);
//		cHead.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, 24));
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(cHead, c);
		add(cHead);
		grazingOn = new JCheckBox[Livestock.COWS3PL + 1 - Livestock.HEIFERS01];
		for(int i = 0; i <= Livestock.COWS3PL - Livestock.HEIFERS01; i++){
			JLabel lab = new JLabel(Constants.getName(i + Livestock.HEIFERS01));
//			lab.setPreferredSize(new Dimension(200, lab.getPreferredSize().height));
			c.gridwidth = 1;
			layout.setConstraints(lab, c);
			add(lab);
			grazingOn[i] = new JCheckBox();
			c.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(grazingOn[i], c);
			add(grazingOn[i]);
			final int cowGroup = i;
			grazingOn[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Action a = null;
					if(grazingOn[cowGroup].isSelected())
						a = new Action(DairyAgent.ACTION_SET_GRAZING,
							new IntegerData(DairyAgent.ACTION_PARAM_ANIMAL_TYPE, cowGroup));
					else
						a = new Action(DairyAgent.ACTION_SET_CONFINED,
								new IntegerData(DairyAgent.ACTION_PARAM_ANIMAL_TYPE, cowGroup));
					agent.performAction(a);
                }
			});
		}
		window = new JablusWindow("Feeding Regieme", false, false, true){
            private static final long serialVersionUID = 1L;
			public void dispose(){
				displayWindow(false);
			}
		};
		window.add(this);
		window.pack();
//		window.setSize(new Dimension(320, 700));
	}

	int getTargetYield(){
		return yTrg.intValue();
	}
	
	/** Sets the livestock object */
	void setLivestock(Livestock livestock){
		this.livestock = livestock;
//		this.digest = digest;
		// Generate confinemenet input fields
//		Data[] fields = livestock.getInputFields();
//		add(Popup.genInputFields(fields));
		
	}
	
	/** Generates the feed controls */
	private void genFeedControl(final Data0D off, Data0D con, Data0D rem, final int aId, GridBagLayout layout, GridBagConstraints c){
//		int height = 22;
		final Data0D offClone = (Data0D)off.clone();
		JLabel name = new JLabel(offClone.getName());
		//p.setBorder(new EtchedBorder());
//		name.setPreferredSize(new Dimension(100, height));
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(name, c);
		add(name);
		DataTextField in = new DataTextField(offClone){
            private static final long serialVersionUID = 1L;
			public void valueChanged(){
				Action a = new Action(aId, new Data[]{new IntegerData(0, offClone.getId() - CowDigestion.FEED_CONC), offClone});
				if(agent.isActionPossible(a)) agent.performAction(a);
			}
			public void redisplay(){
				setValue(off.getValue());
				super.redisplay();
			}
		};
//		in.setPreferredSize(new Dimension(60, height));
		inputs.add(in);
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(in, c);
		add(in);
		DataLabel conLab = new DataLabel(con, false, false, false, 2);
		conLab.setHorizontalAlignment(JLabel.RIGHT);
		SubjectDisplayParams.setStyle(conLab, DisplayParams.POSITIVE);
//		conLab.setPreferredSize(new Dimension(50, height));
		labels.add(conLab);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		layout.setConstraints(conLab, c);
		add(conLab);
		DataLabel remLab = new DataLabel(rem, false, false, false, 2);
		remLab.setHorizontalAlignment(JLabel.RIGHT);
		SubjectDisplayParams.setStyle(remLab, DisplayParams.POSITIVE);
//		remLab.setPreferredSize(new Dimension(50, height));
		labels.add(remLab);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(remLab, c);
		add(remLab);
//		p.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, height + 5));
//		return p;
	}
/*	void setLivestock(Livestock livestock){
		JLabel housing = new JLabel("  Livestock Housing");
		housing.setPreferredSize(new Dimension(SubjectDisplayParams.INPUT_PANEL_WIDTH, 24));
		add(housing);
		SubjectDisplayParams.setStyle(housing, SubjectDisplayParams.TITLE);
//		Data[] fields = livestock.getInputFields();
//		add(Popup.genInputFields(fields));
	}
*/	
	/** Displays the feed panel in a window */
	void displayWindow(boolean display){
		window.setVisible(display);
	}
	
	/** Close the window */
	void close(){
		if(window != null)
			window.dispose();
	}
	
	/** Notify the panel that a simulation step is complete */
	void notifyStep(){
		// Synchronise text fields with actual data
		for(DataTextField f : inputs){
			f.redisplay();
		}
		// Update feed totals
/*		if(livestock.allGrazing())
			for(int i = 0; i < cRem.length; i++)
				cRem[i].setValue(0);
		else
			for(int i = 0; i < cRem.length; i++)
				cRem[i].setValue(cOff[i].intValue() - cCon[i].doubleValue());
		
		if(livestock.allConfined())
			for(int i = 0; i < gRem.length; i++)
				gRem[i].setValue(0);
		else
			for(int i = 0; i < gRem.length; i++)
				gRem[i].setValue(gOff[i].intValue() - gCon[i].doubleValue());
*/		
		ySrp.setValue(yAct.intValue() - yTrg.intValue());
		for(DataLabel l : labels)
			l.redisplay();
		// Update grazing check boxes
		for(int i = 0; i <= Livestock.COWS3PL - Livestock.HEIFERS01; i++){
			grazingOn[i].setSelected(livestock.getPropGrazing(i + Livestock.HEIFERS01) != 0);
		}
		repaint();
	}
	
	/** Clears feeding regime data */
	public void clearData(){
		if(digest != null)
			digest.resetRegime();
		for(DataTextField ip : inputs)
			ip.redisplay();
	}

	/** Reads feeding regime from a buffered reader */
	public void readData(BufferedReader reader) throws IOException{
		String line = reader.readLine();
//System.out.println("1 read line: " + line);
		if(line == null || !line.contains("FEEDING REGIME")) return;
		line = reader.readLine();
//System.out.println("2 read line: " + line);
		if(line == null) return;
		String[] l = line.split(",\\s*");
		for(int i = 0; i < cOff.length && i + 1 < l.length; i++){
			cOff[i].setValue(Integer.parseInt(l[i + 1]));
		}
		line = reader.readLine();
//System.out.println("3 read line: " + line);
		if(line == null) return;
		l = line.split(",\\s*");
		for(int i = 0; i < gOff.length && i + 1 < l.length; i++){
			gOff[i].setValue(Integer.parseInt(l[i + 1]));
		}
		line = reader.readLine();
//System.out.println("4 read line: " + line);
		if(line == null) return;
		l = line.split(",\\s*");
		if(l.length < 2) return;
		yTrg.setValue(Integer.parseInt(l[1]));	
//System.out.println(Utils.arrayString(l));
		for(DataTextField ip : inputs)
			ip.redisplay();
	}
	
	/** Write feeding regieme as csv */
	void writeData(BufferedWriter writer) throws IOException{
/*		private Data0D.Integer[] cOff;
		private Data0D.Double[] cCon;
		private Data0D.Double[] cRem;
		private Data0D.Integer[] gOff;
		private Data0D.Double[] gCon;
		private Data0D.Double[] gRem;
		private Data0D.Integer yTrg;
		private Data0D.Integer yAct;
		private Data0D.Integer ySrp;
		private List<DataLabel> labels;
		private JCheckBox[] grazingOn;
*/
		writer.write("FEEDING REGIME");
		for(int i = 0; i < cOff.length; i++){
			writer.write(", " + cOff[i].getName() + " (" + cOff[i].getUnits() + ")");
		}
		writer.newLine();
		writer.write("Confined Cows");
		for(int i = 0; i < cOff.length; i++){
			writer.write(", " + cOff[i].getValue());
		}
		writer.newLine();
		writer.write("Grazing Cows");
		for(int i = 0; i < gOff.length; i++){
			writer.write(", " + gOff[i].getValue());
		}
		writer.newLine();
		writer.write("Target Yield (" + yTrg.getUnits() + "), " + yTrg.getValue());
		writer.newLine();	
		writer.newLine();	
	}
	
	// ======================= Event Handlers ===============================
/*	private ActionListener HANDLE_MARKET_CLICK = new ActionListener(){
		public void actionPerformed(final ActionEvent e){
			new Thread(){
				public void run(){
					handle(e);
				}
			}.start();
		}
		public void handle(ActionEvent e){	
/*			JMenuItem item = (JMenuItem)e.getSource();
			String aName = ((Component)e.getSource()).getName();
			Action action = agent.getActionExecutable(aName);
			if(iface.isActionPossible(action)){
				// Set up permissable range
				if(agent.isActionType(action, DairyAgent.ACTION_BUY)){
					int storeId = Storage.STORE_FOR.get(((DataTX)action.args[0]).getText());
					((Data0D)action.args[1]).setRange(0, store.getStore(storeId).spaceAvailable());
				}
				else if(agent.isActionType(action, DairyAgent.ACTION_SELL)){
					int storeId = Storage.STORE_FOR.get(((DataTX)action.args[0]).getText());
					int itemId = Storage.ITEM_ID.get(((DataTX)action.args[0]).getText());
					((Data0D)action.args[1]).setRange(0, store.getStore(storeId).items[itemId].getValue());
				}
				// Get quantity
				action.args[1] = DecisionPopup.queryData(null, action.args[1].getName(), action.args[1]);
//System.out.println("POPUP RETURNED:" + nCows);
//				agent.addActionToPerform(action);
				agent.performAction(action);
				addTransaction();
				iface.updateStores();
				//iface.redisplay();
				//System.out.println("Selected feature:" + );
//			System.out.println("right button used?" + maps.getSelector().isRightMouseButtonUsed());
			}
			
		}
	};
*/
}
