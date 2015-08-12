package uk.co.crider.jablus.models.dairy.gui;

/*

Things to evaluate:
------------------
Maintenence of crops
 * Good overall use of land to increase productivity
    (total t of feed produced)
	(avg % areas dedicated to x)
 * Good crop yields achieved with used land
	(Avg yields of crops, compared with max yield - percent bars)
 * Good use of manure, lower use of fertiliser

Maintainence of herd
 * Good sized herd (herd value)
 * Lots of new cows produced, lots of sales, and revenue from sales, minimal buys
 * Low number of culls
 * Good milk productivity (but not over quota)

Looking after budget
 * Good revenue (good decision making with market, buying low and selling high)
 * Minimal costs (low use of bought in feeds/fertilisers)
 * Good self sufficiency
 * Good maintenence of balance (no time spent in debt)

Environmental friendlyness
 * low nitrate leaching
 * low compulsary spread of manure
 * low reliance on fertiliser 
 * high self sufficiency
 * low number of carcases. High proportion of exported cows sold for beef, rather than as carcases

For each evaluation section, a score will be produced and visualised in the interface.


Some formulas for the investment calculation
For a given comodity:
qb = Quantity Bought
qs = Quantity Sold
vb = Total value lost through all purchases
vs = Total value gained through all sales
g = Financial gain from transactions

Clearly, if qb = qs then the amount of financial gain: g = vs - vb
For example, we buy 1kg of potatoes for £2 and sell for £3, then we have gained g = £3 - £2 = £1
If qb != qs then the financial gain calculation must only inlclude portions of the commodity such
that the amount bought and sold are equal. Any extra bought or sold is not included, since it is
still in a non-liquid form. This means the gain is calculated on the proportion of the comodity
traded which balances amount sold and amount bought.
If qb > qs then gain
	g = vs - vb * (qs / qb)

if qb < qs then gain
	g = vs * (qb / qs) - vb

This means that for all cases gain g = vs * qb - vb * qs
For example: I buy 10kg potatoes for £5, and then sell half of them for twice the price, that's 5kg for £5 then gain is
g = 5 - 5 * (5 / 10) = 5 - 2.5 = £2.50
The reason for this is that of the 10kg that I bought, only 5kg have been used as an investment,
since I'm still holding onto the other 5. This means that I effectively bought 5kg for £2.50 and
then sold the same 5 for £5, which is a gain of £2.50. If I decide to sell the other 5 later on,
then it becomes part of the investment and the gain calculation can be updated.


 */

import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.Popup;
import uk.co.crider.jablus.gui.data.DataLabel;
import uk.co.crider.jablus.gui.data.DataTable;
import uk.co.crider.jablus.gui.data.PercentBar;
import uk.co.crider.jablus.gui.jfreechart.TimeGraphPanel;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.DairyFarm;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.models.dairy.env.Storage;
import uk.co.crider.jablus.models.dairy.env.field.Crop;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/** Class to contain farm evaluation information */
public class EvaluationPanel extends JScrollPane {

	/** Unique class ID */
	private static final long serialVersionUID = 3964226533969128291L;

	private static final int WIDTH = 1000;
	private static final int HEIGHT = 600;

	// Model comopnents
	private Time time;
	private DairyAgent agent;
	private DairyFarm farm;
	private Market market;
	private Storage store;
	private Livestock livestock;

	private JablusWindow window;
	private SubjectInterface iFace;
	private DisplayParams displayParams;
	private JTabbedPane pane;
//	private Component parent;
	private TransactionsPanel transPanel;
	private LivestockFeedPanel feedPanel;

	// Overall performance scores
	private PercentBar[] pBars;
	private DoubleData[] prf;
	public DoubleData[] getPerformance(){ return prf; }
	public TimeGraphPanel pGraph;
	
	
	// Statistical components
	private DataTable cTable;
//	private Object[][] cropData;
	private DoubleData[][] cStats;
	private double[][][] cStatsRecord;
//	private DoubleData[] prevProd;
	
	private DataTable hTable;
//	private Object[][] herdData;
	private DoubleData[][] hStats;
	private double[][][] hStatsRecord;

	private DataTable eTable;
	private DoubleData[] eStats;
	private double[][] eStatsRecord;
/*	private DataLabel mnOflow;
	private DataLabel leaching;
	private DataLabel propDead;
	private DataLabel impFr;
	private DataLabel impAn;
	private DataLabel impFd;
*/	
	
	// For financial performance
	private int[] fItems;
	private DataTable fTable;
	private DoubleData[][] fStats;
	private int[] lItems;
	private DataTable lTable;
	private DoubleData[][] lStats;
	private int[] oItems;
	private DataTable oTable;
	private DoubleData[][] oStats;
	private DataTable tTable;
	private DoubleData[][] tStats;

	// For investment data
/*	private DataTable mTable;
	private Object[][] investData;
	private DataLabel totalGains;
*/
	
	private int iRec;
	private int nRec;
	private double[][] storeRecord;
	
/*	private DataLabel cost;
	private DataLabel revenue;
	private DataLabel profit;
	private DataLabel balance;
*/	private DoubleData value;
	private DoubleData growth;
//	private double prevValue;


	public EvaluationPanel(SubjectInterface iFace, DisplayParams displayParams, LivestockFeedPanel feedPanel){
		this.iFace = iFace;
		this.displayParams = displayParams;
		this.feedPanel = feedPanel;

		// Cropping performance
		// Crop    Total Produced    Change    Avg % Area used    % yield of potential


		// Livestock performance
		// Herd size    New calves   Culled cows    milk productivity


		// Financial performance (values based on avg price for year, not individual buy/sell prices)
		// Feeds :     stock value,  net value imported,  value produced,  net value growth
		// Livestock : stock value,  net value imported,  value produced,  net value growth
		// Investment : Money gained/lost by trend timing  // This is where good trade timing is manifested
		// Summary: Tot Costs     Tot Revenues     Profit   Balance   Est Value  Growth

		// Environmental performance
		// Nitrate leached
		// Manure overflow spread
		// Fertiliser used
		// Reliance on imports: fertiliser, feeds, livestock
		// Proportion of cows sold to cows lost as dead carcases



		window = new JablusWindow("Annual Evaluation", false, false, true){
			private static final long serialVersionUID = 1L;
			public void dispose(){
				displayWindow(false);
			}
		};
		window.add(this);
//		window.setSize(new Dimension(WIDTH, HEIGHT));
//		window.pack();
		window.setLocationRelativeTo(iFace);
		
		prf = new DoubleData[]{
				new DoubleData(Constants.OUTPUT_SCORE_CROPPING),
				new DoubleData(Constants.OUTPUT_SCORE_LIVESTOCK),
				new DoubleData(Constants.OUTPUT_SCORE_ENVIRONMENT),
				new DoubleData(Constants.OUTPUT_SCORE_FINANCIAL),
		};
		
		
		iRec = 0;
		nRec = 1;
		storeRecord = new double[Market.NUM_PRODUCTS][];
		for(int i = 0; i < Market.NUM_PRODUCTS; i++)
			if(Storage.hasStore(i + Market.OFFSET))
				storeRecord[i] = new double[Time.WEEKS_YEAR];
	}


	public void addModelItem(Data item){
//		System.out.println("EvaluationPanel: adding " + item);
//		+ ", id=" + jablus.models.dairy.Constants.getName(item.getId()) +
//		" is store?" + (item instanceof Storage));
		if(item instanceof Storage){
			store = (Storage)item;
			// Initialise storeRecord values
			for(int i = 0; i < Market.NUM_PRODUCTS; i++){
				if(storeRecord[i] != null){
					for(int j = 0; j < storeRecord[i].length; j++)
						storeRecord[i][j] = store.quantityStored(i + Market.OFFSET);
				}
			}
		}
		else if(item instanceof Market)
			market = (Market)item;
		else if(item instanceof DairyFarm)
			farm = (DairyFarm)item;
		else if(item instanceof Time)
			time = (Time)item;
		else if(item instanceof DairyAgent)
			agent = (DairyAgent)item;
		else if(item instanceof Livestock)
			livestock = (Livestock)item;
		else return;
		if(store == null || market == null || farm == null
		|| time == null  || agent == null  || livestock == null){
//			System.out.println(store + "\n\n" + market + "\n\n" + farm + "\n\n" + time + "\n\n" + agent);
			return;
		}
		init();
	}

	public TransactionsPanel getTransPanel(){
		return transPanel;
	}

	private void init(){
		JTabbedPane pane = new JTabbedPane();
//		this.parent = parent;
//		pane.addTab("Productivity", prodPanel);
//		pane.addTab("Livestock", new JPanel());
//		pane.addTab("Cropping", new JPanel());
//		pane.addTab("Finance", financePanel);
//		pane.addTab("Environmental", new JPanel());
		pane.addTab("Summary", genMainPanel());
		pane.addTab("Livestock Statistics", genLivestockPanel());
		pane.addTab("Cropping Statistics", genCroppingPanel());
		transPanel = new TransactionsPanel(iFace, agent);
		transPanel.setEconomics(market);
		transPanel.setStore(store);
		pane.addTab("Weekly Transactions", transPanel);
//		JScrollPane sPane = new JScrollPane(pane);
//		sPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setViewportView(pane);
//		add(sPane);
		updateStats();
	}

	
	private JPanel genMainPanel(){
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		panel.setLayout(layout);

		// Label for general performance graph
		JLabel pLabel = new JLabel("Performance Trend");
		SubjectDisplayParams.setStyle(pLabel, DisplayParams.TITLE);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(pLabel, c);
		panel.add(pLabel);
		
		pGraph = new TimeGraphPanel("Percentage Performance", null, "%", time.getJavaDate(), false, true, false);
		pGraph.setRange(0, 100);
		layout.setConstraints(pGraph, c);
		panel.add(pGraph);
		pGraph.setPreferredSize(new Dimension(pGraph.getPreferredSize().width, 150));
		for(DoubleData pItem : prf)
			pGraph.addSeries(pItem.getName(), displayParams.getPaint(pItem.getId()));

/*		// Label for general performance
		JLabel sLabel = new JLabel("Overall Performance Calculations");
		SubjectDisplayParams.setStyle(sLabel, DisplayParams.TITLE);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(sLabel, c);
		panel.add(sLabel);

		c.fill = GridBagConstraints.VERTICAL;
*/		pBars = new PercentBar[prf.length];
		for(int i = 0; i < prf.length; i++){
//			JLabel l = new JLabel(Constants.getName(prf[i].getId()));
//			l.setFont(l.getFont().deriveFont(14f).deriveFont(Font.BOLD));
//			l.setForeground(Color.GRAY.darker());
//			c.gridwidth = 1;
//			c.anchor = GridBagConstraints.SOUTHEAST;
//			layout.setConstraints(l, c);
//			panel.add(l);
			pBars[i] = new PercentBar(prf[i]);
			pBars[i].setPreferredSize(new Dimension(200, 15));
//			c.gridwidth = GridBagConstraints.REMAINDER;
//			c.anchor = GridBagConstraints.SOUTHWEST;
//			layout.setConstraints(pBars[i], c);
//			panel.add(pBars[i]);			
		}

		
		
		// Label for other revenues
/*		JLabel oLabel = new JLabel("Annual costs, revenue, stock value and growth");
		SubjectDisplayParams.setStyle(oLabel, DisplayParams.TITLE);
		layout.setConstraints(oLabel, c);
		panel.add(oLabel);
		// Table for other revenues
		oItems = new int[]{
			Market.FARM_RENT,
			Market.INTEREST
		};
		Object[][] otherData = new Object[oItems.length + 1][5];
		otherData[0] = new Object[]{"Product/Service", "Cost", "Revenue", "Stock", "Growth"};
		oStats = new DoubleData[oItems.length][4];
//		prevLvsVal = new DoubleData(0);
		for(int i = 1; i < otherData.length; i++){
			otherData[i][0] = Constants.getName(oItems[i - 1]);
			for(int j = 1; j < otherData[i].length; j++){
				oStats[i - 1][j - 1] = new DoubleData(0, Constants.UNITS_CURRENCY);
				otherData[i][j] =
					j == 1 && !Market.canBuy(oItems[i - 1]) ||
					j == 2 && !Market.canSell(oItems[i - 1]) ||
					j == 3 && !Storage.hasStore(oItems[i - 1]) ? "-" :
					new DataLabel(oStats[i - 1][j - 1], false, true, true, 0);
			}
		}
		oTable = new DataTable(otherData);
		layout.setConstraints(oTable, c);
		panel.add(oTable);
*/		
		// Label for total revenues
		JLabel tLabel = new JLabel("Annual costs, revenues, stock value and growth");
		SubjectDisplayParams.setStyle(tLabel, DisplayParams.TITLE);
		layout.setConstraints(tLabel, c);
		panel.add(tLabel);
		// Table for total assets
		String[] tItems = new String[]{"Feeds", "Livestock", "Farm Rent", "SFP", "Loan Interest", "Balance", "Grand Total"};
		Object[][] totalData = new Object[tItems.length + 1][5];
		totalData[0] = new Object[]{"Category", "Costs", "Revenues", "Assets", "Growth"};
		tStats = new DoubleData[tItems.length][6];
//		prevLvsVal = new DoubleData(0);
		for(int i = 1; i < totalData.length; i++){
			totalData[i][0] = tItems[i - 1];
				for(int j = 1; j < totalData[i].length; j++){
					tStats[i - 1][j - 1] = new DoubleData(0, Constants.UNITS_CURRENCY);
					// Remove irrelevant table entries
					if((i == 3 || i == 5) && (j == 2 || j == 3)) totalData[i][j] = "-";
					else if(i == 4 && (j == 1 || j == 3)) totalData[i][j] = "-";
					else if(i == 6){
						if(j == 3){
							tStats[i - 1][j - 1] = (DoubleData)market.getItem(Constants.DERIVED_ECONOMIC_BALANCE);
							totalData[i][j] = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_BALANCE), false, true, true, 0);
						}
						else totalData[i][j] = "-";
					}
					else{
						totalData[i][j] = new DataLabel(tStats[i - 1][j - 1], false, true, true, 0);
					}
				}
		}
		tTable = new DataTable(totalData);
		layout.setConstraints(tTable, c);
		panel.add(tTable);
		// Link value and growth to table
		value = tStats[tStats.length - 1][2];
		growth = tStats[tStats.length - 1][3];

		
		
		// Label for environmental performance
		JLabel eLabel = new JLabel("Annual Environmental Statistics");
		SubjectDisplayParams.setStyle(eLabel, DisplayParams.TITLE);
		layout.setConstraints(eLabel, c);
		panel.add(eLabel);
		// Table for environmental  performance
		// Nitrate leached
		// Manure overflow spread
		// Fertiliser used
		// Reliance on imports: fertiliser, feeds, livestock
		// Proportion of cows sold to cows lost as dead carcases
//		cost    = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_COSTS), true, true, 0);
//		DataLabel mnOflow = new DataLabel(new DoubleData(Constants.OUTPUT_SLURRY_OVERFLOW   , Constants.UNITS_VOLUME_LOW), true, true, 0);

		eStats = new DoubleData[]{
				new DoubleData(Constants.OUTPUT_SLURRY_OVERFLOW    , Constants.UNITS_WEIGHT_HIGH),
				new DoubleData(Constants.OUTPUT_SURPLUS_FEED       , Constants.UNITS_CURRENCY   ),
				new DoubleData(Constants.OUTPUT_NITRATE_LEACHED    , Constants.UNITS_WEIGHT_HIGH),
				new DoubleData(Constants.OUTPUT_DEAD_CULLS                                      ),
				new DoubleData(Constants.OUTPUT_IMPORTED_FERTILISER, Constants.UNITS_WEIGHT_HIGH),
				new DoubleData(Constants.OUTPUT_IMPORTED_FEEDS     , Constants.UNITS_WEIGHTFW_HIGH),
				new DoubleData(Constants.OUTPUT_IMPORTED_ANIMALS                                ),
		};

		eStatsRecord = new double[4][Time.WEEKS_YEAR];
		Object[][] envData = new Object[eStats.length][4];
		for(int i = 0; i < eStats.length; i++){
			envData[i][0] = eStats[i].getName();
			envData[i][1] = new DataLabel(eStats[i], false, true, false, 0);
			envData[i][2] = "";
			envData[i][3] = "";
		}
		eTable = new DataTable(envData);
		layout.setConstraints(eTable, c);
		panel.add(eTable);
		
		JLabel spacer = new JLabel();
		c.weighty = 1;
		layout.setConstraints(spacer, c);
		panel.add(spacer);
		
		return panel;
	}
	

	private JPanel genCroppingPanel(){

		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.setLayout(layout);
		
		// Label for crop performance
		c.fill = GridBagConstraints.BOTH;
		JLabel cLabel = new JLabel("Annual Cropping Statistics");
		SubjectDisplayParams.setStyle(cLabel, DisplayParams.TITLE);
		layout.setConstraints(cLabel, c);
		panel.add(cLabel);
		// Table for crop  performance
		Object[][] cropData = new Object[Market.GROWN_FEEDS.length + 2][5];
//		prevProd = new DoubleData[Market.GROWN_FEEDS.length + 1];
		cropData[0] = new Object[]{"Crop", "Area Used (ha)", "Avg Yield (t/Ha)", "Produced (t)", "Change"};
//		cUnits = new String[]{Constants.UNITS_WEIGHT_HIGH}
		cStats = new DoubleData[Market.GROWN_FEEDS.length + 1][4];
		cStatsRecord = new double[Market.GROWN_FEEDS.length][4][Time.WEEKS_YEAR];
		for(int i = 1; i < cropData.length; i++){
			cropData[i][0] = i < cropData.length - 1?
				Constants.getName(Market.GROWN_FEEDS[i - 1]) :
				"Total";
			for(int j = 1; j < cropData[i].length; j++){
				cStats[i - 1][j - 1] = new DoubleData(0);
				cropData[i][j] = new DataLabel(cStats[i - 1][j - 1], false, false, true, 1);
			}
		}
		cTable = new DataTable(cropData);
		layout.setConstraints(cTable, c);
		panel.add(cTable);
		
		// Label for feed performance
		JLabel fLabel = new JLabel("Annual imports, exports, stock value and growth");
		SubjectDisplayParams.setStyle(fLabel, DisplayParams.TITLE);
		layout.setConstraints(fLabel, c);
		panel.add(fLabel);
		// Table for feed performance
		fItems = new int[]{
				Market.SILAGE_GRASS,
				Market.GRASS_SOW,
				Market.GRASS_HARVEST,
				Market.SILAGE_WHEAT,
				Market.WHEAT_SOW,
				Market.WHEAT_HARVEST,
				Market.SILAGE_MAIZE,
				Market.MAIZE_SOW,
				Market.MAIZE_HARVEST,
				Market.CONCENTRATES,
				Market.HAY,
				Market.STRAW,
				Market.FERTILISER,
				Market.FERTILISE,
				Market.SPREAD,
				Market.PLOUGH,
		};
		Object[][] feedData = new Object[fItems.length + 1][5];
		feedData[0] = new Object[]{"Product/Service", "Bought", "Sold", "Stock", "Growth"};
		fStats = new DoubleData[fItems.length][4];
		for(int i = 1; i < feedData.length; i++){
			feedData[i][0] = Constants.getName(fItems[i - 1]);
			for(int j = 1; j < feedData[i].length; j++){
				fStats[i - 1][j - 1] = new DoubleData(0, Constants.UNITS_CURRENCY);
				feedData[i][j] =
					j == 1 && !Market.canBuy(fItems[i - 1]) ||
					j == 2 && !Market.canSell(fItems[i - 1]) ||
					j == 3 && !Storage.hasStore(fItems[i - 1]) ? "-" :
					new DataLabel(fStats[i - 1][j - 1], false, true, true, 0);
			}
		}
		fTable = new DataTable(feedData);
		layout.setConstraints(fTable, c);
		panel.add(fTable);
		
		JLabel spacer = new JLabel();
		c.weighty = 1;
		layout.setConstraints(spacer, c);
		panel.add(spacer);
		
		return panel;
	}
	
	private JPanel genLivestockPanel(){

		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.setLayout(layout);
		
		// Label for livestock performance
		JLabel hLabel = new JLabel("Annual Herd Statistics");
		SubjectDisplayParams.setStyle(hLabel, DisplayParams.TITLE);
		layout.setConstraints(hLabel, c);
		panel.add(hLabel);
		// Table for livestock  performance
		Object[][] herdData = new Object[Livestock.HERD_GROUPS.length + 2][5];
		herdData[0] = new Object[]{"Group", "Avg Number", "Calves Produced", "Culled", "Milk Produced"};
		hStats = new DoubleData[Livestock.HERD_GROUPS.length + 1][4];
		hStatsRecord = new double[Livestock.HERD_GROUPS.length][4][Time.WEEKS_YEAR];
		for(int i = 1; i < herdData.length; i++){
			herdData[i][0] = i < herdData.length - 1 ?
				Constants.getName(Livestock.HERD_GROUPS[i - 1]) :
				"Total";
			for(int j = 1; j < herdData[i].length; j++){
				hStats[i - 1][j - 1] = new DoubleData(0);
				herdData[i][j] = (i == 1 && j == 2) || (j == 4 && i < 3) ? "-" :
					new DataLabel(hStats[i - 1][j - 1], false, false, true, j == 1 ? 1 : 0);
			}	
		}
		hTable = new DataTable(herdData);
		layout.setConstraints(hTable, c);
		panel.add(hTable);

		// Label for livestock financial performance
		JLabel lLabel = new JLabel("Annual imports, exports, stock value and growth");
		SubjectDisplayParams.setStyle(lLabel, DisplayParams.TITLE);
		layout.setConstraints(lLabel, c);
		panel.add(lLabel);
		// Table for livestock performance
		lItems = new int[]{
				Market.BULL_CALVES,
				Market.HEIFER_CALVES,
				Market.FINISHED_HEIFERS,
				Market.CALVED_HEIFERS,
				Market.CULL_COWS,
				Market.MILK,
				Market.LVS_OVERHEADS,
				Market.INSEMINATION,
				Market.VETINARY_MEDS,
				Market.CARCAS_REMOVAL,
		};
		Object[][] lvsData = new Object[lItems.length + 1][5];
		lvsData[0] = new Object[]{"Product/Service", "Bought", "Sold", "Stock", "Growth"};
		lStats = new DoubleData[lItems.length][4];
//		prevLvsVal = new DoubleData(0);
		for(int i = 1; i < lvsData.length; i++){
			lvsData[i][0] = Constants.getName(lItems[i - 1]);
			for(int j = 1; j < lvsData[i].length; j++){
				lStats[i - 1][j - 1] = new DoubleData(0, Constants.UNITS_CURRENCY);
				lvsData[i][j] = 
					j == 1 && !Market.canBuy(lItems[i - 1]) ||
					j == 2 && !Market.canSell(lItems[i - 1]) ||
					j == 3 && !Storage.hasStore(lItems[i - 1]) ? "-" :
					new DataLabel(lStats[i - 1][j - 1], false, true, true, 0);
			}
		}
		lTable = new DataTable(lvsData);
		layout.setConstraints(lTable, c);
		panel.add(lTable);

		JLabel spacer = new JLabel();
		c.weighty = 1;
		layout.setConstraints(spacer, c);
		panel.add(spacer);
		
		return panel;
	}
	
	private JPanel genFinancePanel(){

		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.setLayout(layout);
		
		

		// Livestock milking costs
/*		int[] lvsItems = { Market.MILK, Market.LVS_OVERHEADS, Market.INSEMINATION, Market.VETINARY_MEDS, Market.CARCAS_REMOVAL };
		lvsSummary = new DataLabel[lvsItems.length];
		for(int i = 0; i < lvsItems.length; i++){
			lvsSummary[i]    = new DataLabel(new DoubleData(lvsItems[i]   , Constants.UNITS_CURRENCY), true, true, 0);
			layout.setConstraints(lvsSummary[i], c);
			financePanel.add(lvsSummary[i]);			
		}
*///		public static final int MILK               = OFFSET +  1;
//		public static final int LVS_OVERHEADS            = OFFSET +  2;
//		public static final int INSEMINATION       = OFFSET +  3;
//		public static final int VETINARY_MEDS      = OFFSET +  4;
//		public static final int CARCAS_REMOVAL     = OFFSET +  5;
		
		// Label for market gains 
/*		JLabel mLabel = new JLabel("Market gains");
		SubjectDisplayParams.setStyle(mLabel, DisplayParams.TITLE);
		layout.setConstraints(mLabel, c);
		financePanel.add(mLabel);
		// Table for invest data
		investData = new Object[Market.SALE_PRODUCTS.length + 2][6];
		investData[0] = new Object[]{"Product"  , "Qty Bought", "Avg Buy Price (£)", "Qty Sold", "Avg Sale Price (£)", "£ Gain"};
		for(int i = 1; i < investData.length; i++){
			if(i < investData.length - 1)
				investData[i] = new Object[]{
					Constants.getName(Market.SALE_PRODUCTS[i - 1]),
//					market.getQtyBought()[i - 1],
					new DoubleData(0),
					new DoubleData(0),
//					market.getQtySold()[i - 1],
					new DoubleData(0),
					new DoubleData(0),
					new DoubleData(0)
			};
			else
				investData[i] = new Object[]{
					"",
					"",
					"",
					"",
					"Total",
					new DoubleData(0)
			};
		}
		mTable = new DataTable(investData);
		layout.setConstraints(mTable, c);
		financePanel.add(mTable);
*/
		
/*		JLabel sLabel = new JLabel("Financial Summary");
		SubjectDisplayParams.setStyle(sLabel, DisplayParams.TITLE);
		layout.setConstraints(sLabel, c);
		financePanel.add(sLabel);
		// Data for market gains
		cost    = new DataLabel(new DoubleData(Constants.DERIVED_ECONOMIC_COSTS   , Constants.UNITS_CURRENCY), true, true, 0);
//		cost    = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_COSTS), true, true, 0);
		layout.setConstraints(cost, c);
		financePanel.add(cost);
//		revenue = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_REVENUE), true, true, 0);
		revenue = new DataLabel(new DoubleData(Constants.DERIVED_ECONOMIC_REVENUE , Constants.UNITS_CURRENCY), true, true, 0);
		layout.setConstraints(revenue, c);
		financePanel.add(revenue);
		profit  = new DataLabel(new DoubleData(Constants.DERIVED_ECONOMIC_PROFIT  , Constants.UNITS_CURRENCY), true, true, 0);
//		profit  = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_PROFIT), true, true, 0);
		layout.setConstraints(profit, c);
		financePanel.add(profit);
		balance = new DataLabel(new DoubleData(Constants.DERIVED_ECONOMIC_BALANCE , Constants.UNITS_CURRENCY), true, true, 0);
//		balance = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_BALANCE), true, true, 0);
		layout.setConstraints(balance, c);
		financePanel.add(balance);
		value   = new DataLabel(new DoubleData(Constants.DERIVED_ECONOMIC_VALUE   , Constants.UNITS_CURRENCY), true, true, 0);
//		value   = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_VALUE), true, true, 0);
		layout.setConstraints(value, c);
		financePanel.add(value);
		growth  = new DataLabel(new DoubleData(Constants.DERIVED_ECONOMIC_GROWTH  , Constants.UNITS_CURRENCY), true, true, 0);
//		growth  = new DataLabel((Data0D)market.getItem(Constants.DERIVED_ECONOMIC_GROWTH), true, true, 0);
		layout.setConstraints(growth, c);
		financePanel.add(growth);

		prevValue = getValue();
*/		return panel;    	   
	}

	/** Update the main panel to reflect changed state */
	private void updateMainPanel(){
// System.out.println("Updating main evaluation panel");

		// Update Crop Statistics
		farm.genCropAreaUsage();
		// Record this year's stats
		for(int i = 0; i < cStatsRecord.length; i++){
			cStatsRecord[i][0][iRec] = farm.getCropAreaUsed()[i].doubleValue();
			cStatsRecord[i][1][iRec] = farm.getCropYield()[i].doubleValue();
			cStatsRecord[i][2][iRec] = farm.getCropYieldArea()[i].doubleValue();
		}
		// Set totals to 0
		int i_ = cStats.length - 1;
		for(int j = 0; j < cStats[i_].length; j++)
			((Data0D)cStats[i_][j]).setValue(0);
		double yaTot = 0; // Calculates total yield are
		for(int i = 0; i < cStats.length - 1; i++){
			double produced = Utils.sum(cStatsRecord[i][1], iRec, nRec);
			double areaProd = Utils.sum(cStatsRecord[i][2], iRec, nRec);
			cStats[i][0].setValue(Utils.sum(cStatsRecord[i][0], iRec, nRec) / nRec);
			cStats[i][1].setValue(areaProd == 0 ? 0 : produced / areaProd);
			cStats[i][2].setValue(produced);
			cStats[i][3].setValue(cStats[i][2].doubleValue() - cStatsRecord[i][3][(iRec + 1) % Time.WEEKS_YEAR]);
			// Record current production level
			cStatsRecord[i][3][iRec] = produced;
			yaTot += areaProd;
			for(int j = 0; j < cStats[i_].length; j++)
				cStats[i_][j].setValue(
						cStats[i_][j].doubleValue() +
						cStats[i][j].doubleValue());
		}
//System.out.println("\niRec:"+ iRec + ", nRec:" + nRec);
//System.out.println("Yild:" + Utils.arrayString(cStatsRecord[0][1]));
//System.out.println("Prod:" + Utils.arrayString(cStatsRecord[0][3]));
//System.out.println("Area:" + Utils.arrayString(cStatsRecord[0][0]));
//System.out.println("YieldAreaTotal:" + yaTot + ", yieldTotal:" + cStats[3][1] + ", produtionTotal:" + cStats[3][2]);
		// Yield total taken to be an average
		((Data0D)cStats[cStats.length - 1][1]).setValue( yaTot == 0 ? 0 :
				((DoubleData)cStats[cStats.length - 1][2]).doubleValue() / yaTot);
		cTable.redisplay();
		
		
		// Update Livestock Statistics
		for(int i = 0; i < hStatsRecord.length; i++){
			int group = i + Livestock.HEIFERS01;
			hStatsRecord[i][0][iRec] = livestock.getTotal(group);
			hStatsRecord[i][1][iRec] = i < 1 ? 0 : livestock.getNewCalves()[i - 1].intValue();
			hStatsRecord[i][2][iRec] = livestock.getCarcases()[i].intValue();
			hStatsRecord[i][3][iRec] = i < 2 ? 0 : farm.getMilkByGroup()[i - 2].doubleValue();
		}
//System.out.println("iRec:"+ iRec + ", nRec:" + nRec + ", hStatsRecord:" + Utils.arrayString(hStatsRecord[0][0]));
		// Set herd totals to 0
		i_ = hStats.length - 1;
		for(int j = 0; j < hStats[i_].length; j++)
			((Data0D)hStats[i_][j]).setValue(0);
		// Calculate new values
		for(int i = 0; i < Livestock.HERD_GROUPS.length ; i++){
			for(int j = 0; j < hStats[i_].length; j++){
				hStats[i][j].setValue(Utils.sum(hStatsRecord[i][j], iRec, nRec) / (j == 0 ? nRec : 1));
				// Add value to total
				hStats[i_][j].setValue(hStats[i_][j].doubleValue() + hStats[i][j].doubleValue());
			}
		}
		hTable.redisplay();
		

		// Update Environmental Statistics
		eStatsRecord[0][iRec] = farm.getManureOverflow().doubleValue();
		eStatsRecord[1][iRec] = farm.getSurplusFeed().doubleValue();
		eStatsRecord[2][iRec] = farm.getNitrateLeaching().doubleValue();
		eStatsRecord[3][iRec] = farm.getCarcasesRemoved().doubleValue();
		for(int i = 0; i < eStatsRecord.length; i++)
			eStats[i].setValue(Utils.sum(eStatsRecord[i], iRec, nRec));
		eStats[4].setValue(market.getQtyBought(Market.FERTILISER));
		double fImports = 0, lImports = 0;
		for(int i = 0; i < Market.FEED_PRODUCTS.length; i++)
			fImports += market.getQtyBought(Market.FEED_PRODUCTS[i]);
		eStats[5].setValue(fImports);
		for(int i = 0; i < Market.LIVESTOCK_PRODUCTS.length; i++)
			lImports += market.getQtyBought(Market.LIVESTOCK_PRODUCTS[i]);
		eStats[6].setValue(lImports);
		eTable.redisplay();
		
		
		
		
		// Update summary bars 
		double[][] w = new double[prf.length][];
		double yieldSuccess = 0;
		double productivity = 0;
		int yn = 0;
		for(int i = 0; i < Market.GROWN_FEEDS.length; i++){
			if(cStats[i][2].doubleValue() != 0){
				yn++;
				yieldSuccess += cStats[i][1].doubleValue() / Crop.MAX_YIELD[i];
			}
			productivity += cStats[i][2].doubleValue() / Crop.MAX_YIELD[i];
		}
		yieldSuccess = yn > 0 ? yieldSuccess / yn : 0;
		productivity /= farm.getFieldArea();
		w[0] = new double[]{
				yieldSuccess,
				productivity,
		};
//System.out.println("Cropping performance: yield:" + w[0][0] + ", productivity:" + w[0][1]);
		int ia = Livestock.HERD_GROUPS.length;
		double hs = hStats[ia][0].doubleValue();
		double hm = hStats[2][0].doubleValue() + hStats[2][0].doubleValue() + hStats[2][0].doubleValue();
		w[1] = new double[]{
				hs / store.getCapacity(Storage.COW_STEADING), 
				hs == 0 ? 0 : (hStats[ia][1].doubleValue() - hStats[ia][2].doubleValue()) * 2 / hs,
				hm == 0 ? 0 : hStats[ia][3].doubleValue() / (hm * feedPanel.getTargetYield()),				
		};
//System.out.println("Livestock performance: popn:" + w[1][0] + ", growth:" + w[1][1] + ", milk:" + w[1][2]);
/*		w[2] = new double[]{
				1 - ((DoubleData)mnOflow.getData()).doubleValue() * 1E-2,
				1 - ((DoubleData)leaching.getData()).doubleValue() * 1E-1,
				1 - ((DoubleData)propDead.getData()).doubleValue() * 1E-1,
				1 - ((DoubleData)impFr.getData()).doubleValue() * 1E-2,
				1 - ((DoubleData)impAn.getData()).doubleValue() * 5E-2,
				1 - ((DoubleData)impFd.getData()).doubleValue() * 1E-1,
		};
*/		w[2] = new double[]{
				1 - eStats[0].doubleValue() * 1E-2,
				1 - eStats[1].doubleValue() * 1E-3,
				1 - eStats[2].doubleValue() * 1E-1,
				1 - eStats[3].doubleValue() * 1E-1,
				1 - eStats[4].doubleValue() * 1E-2,
				1 - eStats[5].doubleValue() * 5E-2,
				1 - eStats[6].doubleValue() * 1E-1,
		};
//System.out.println(Utils.arrayString(w[2]));
		w[3] = new double[]{
				value.doubleValue() * 1E-5,
				(growth.doubleValue() * 1E-4) + 0.5,
		};

		for(int i = 0; i < prf.length; i++){
			double wt = 0;
			for(int j = 0; j < w[i].length; j++){
				// Ensure weight is between 0 and 1
				w[i][j] = w[i][j] < 0 ? 0 : w[i][j] > 1 ? 1 : w[i][j];
				wt += w[i][j];
			}
			pBars[i].getData().setValue(wt / w[i].length);
			pBars[i].redisplay();
		}
		
		
	}

	/** Update the finance panel to reflect changed state */
	private void updateFinance(){
//System.out.println("Updating finance panel.");

		// Update growth tables
		updateTable(fTable, fItems, fStats);
		updateTable(lTable, lItems, lStats);
//		updateTable(oTable, oItems, oStats);
		genValStats(tStats[2], 0, 0,
				market.getAvgPrice(Market.FARM_RENT),
				market.getAvgPriceHistoric(Market.FARM_RENT, Time.WEEKS_YEAR - 1),
				market.getValBought(Market.FARM_RENT), 0);
		genValStats(tStats[3], 0, 0,
				market.getAvgPrice(Market.SFP),
				market.getAvgPriceHistoric(Market.SFP, Time.WEEKS_YEAR - 1),
				0, market.getValSold(Market.SFP));
		genValStats(tStats[4], 0, 0,
				market.getAvgPrice(Market.INTEREST),
				market.getAvgPriceHistoric(Market.INTEREST, Time.WEEKS_YEAR - 1),
				market.getValBought(Market.INTEREST), 0);

		// Update sub-totals and grand total
		for(int j = 0; j < 4; j++){
			double fTotal = 0, lTotal = 0, oTotal = 0;
			for(int i = 0; i < fStats.length; i++) fTotal += fStats[i][j].doubleValue();
			for(int i = 0; i < lStats.length; i++) lTotal += lStats[i][j].doubleValue();
//			for(int i = 0; i < oStats.length; i++) oTotal += oStats[i][j].doubleValue();
			oTotal += tStats[2][j].doubleValue();
			oTotal += tStats[3][j].doubleValue();
			oTotal += tStats[4][j].doubleValue();
			tStats[0][j].setValue(fTotal);
			tStats[1][j].setValue(lTotal);
//			tStats[2][j].setValue(oTotal);
			tStats[6][j].setValue(fTotal + lTotal + oTotal + tStats[5][j].doubleValue());
		}
		tTable.redisplay();

		// Market gains
/*		double total = 0;
		for(int i = 1; i <= Market.SALE_PRODUCTS.length ; i++){
			double qb = ((DoubleData)investData[i][1]).doubleValue();
			double qs = ((DoubleData)investData[i][3]).doubleValue();
			double vb = market.getValBought(Market.SALE_PRODUCTS[i - 1]);
			double vs = market.getValSold(Market.SALE_PRODUCTS[i - 1]);
			((DoubleData)investData[i][2]).setValue(qb == 0 ? 0 : vb / qb);
			((DoubleData)investData[i][4]).setValue(qs == 0 ? 0 : vs / qs);
			((DoubleData)investData[i][5]).setValue(
					qb == 0 || qs == 0 ? 0 :
					qb > qs ? vs - vb * qs / qb :
					vs * qb / qs - vb);
			total += ((DoubleData)investData[i][5]).doubleValue();
		}
		((DoubleData)investData[investData.length - 1][5]).setValue(total);
//		totalGains.getData().setValue(total);
//		totalGains.redisplay();
		mTable.redisplay();
*/
		// Financial summary
/*		cost.getData().setValue(((Data0D.Double)cost.getData()).doubleValue() -
				((Data0D.Double)market.getItem(Constants.DERIVED_ECONOMIC_COSTS)).doubleValue());
		revenue.getData().setValue(((Data0D.Double)revenue.getData()).doubleValue() +
				((Data0D.Double)market.getItem(Constants.DERIVED_ECONOMIC_REVENUE)).doubleValue());
		profit.getData().setValue(((Data0D.Double)revenue.getData()).doubleValue() -
				((Data0D.Double)cost.getData()).doubleValue());
		balance.getData().setValue(
				((Data0D.Double)market.getItem(Constants.DERIVED_ECONOMIC_BALANCE)).doubleValue());
		value.getData().setValue(getValue());
//		System.out.println("value:" + ((Data0D.Double)value.getData()).doubleValue() + " prev val:" + prevValue);
		growth.getData().setValue(((Data0D.Double)value.getData()).doubleValue() - prevValue);
		cost.redisplay();
		revenue.redisplay();
		profit.redisplay();
		balance.redisplay();
		value.redisplay();
		growth.redisplay();
*/
	}

	private void updateTable(DataTable table, int[] items, DoubleData[][] stats){
		// Set totals to 0
		int i_ = stats.length - 1;
		for(int j = 0; j < stats[i_].length; j++) stats[i_][j].setValue(0);
		for(int i = 0; i < items.length ; i++){
			int id = items[i];
//System.out.println("Generating stats for:" + Constants.getName(id));
			double qs = Storage.hasStore(id) ? store.quantityStored(id) : 0;
//if(id == Market.STRAW) System.out.println(Utils.arrayString(storeRecord[id - Market.OFFSET]));
			genValStats(stats[i],
					qs,
					storeRecord[id - Market.OFFSET] == null ? 0 :
						storeRecord[id - Market.OFFSET][(iRec + 1) % Time.WEEKS_YEAR],
					market.getAvgPrice(id),
					market.getAvgPriceHistoric(id, Time.WEEKS_YEAR - 1),
					market.getValBought(id),
					market.getValSold(id));
			// Update totals
			for(int j = 0; j < stats[i_].length; j++)
				stats[i_][j].setValue(
						stats[i_][j].doubleValue() +
						stats[i][j].doubleValue());
		}
		table.redisplay();
	}
	
	
	private void genValStats(DoubleData[] data, double q1, double q2, double pAvg1, double pAvg2, double vb, double vs){
//System.out.println("Generating val stats: q1=" + q1 + "   \tq2=" + q2 + "   \tpAvg1=" + pAvg1 + "   \tpAvg2=" + pAvg2 + "   \tvb=" + vb + "   \tvs=" + vs);
		double vng = q1 * pAvg1 - q2 * pAvg2; // Stock value growth
		double vni = vb - vs;               // Net import cost
		double growth = vng - vni;
		data[0].setValue(-vb);
		data[1].setValue(vs);
		data[2].setValue(q1 * pAvg1);
		data[3].setValue(growth);
	}

	/** updates the items to reflect new state*/
	public void notifyStep(){
		// Update store record
		for(int i = 0; i < Market.NUM_PRODUCTS; i++)
			if(storeRecord[i] != null)
				storeRecord[i][iRec] = store.quantityStored(i + Market.OFFSET);
//System.out.println("UPDATED SORE RECORD POINTER - " + iRec);
		updateStats();
		transPanel.notifyStep();

		// Update performance graph
		for(int i = 0; i < prf.length; i++)
			pGraph.addData(prf[i].getName(), prf[i].doubleValue() * 100);
		pGraph.repaint();
		
		iRec = iRec < Time.WEEKS_YEAR - 1 ? iRec + 1 : 0; 
		nRec = nRec < Time.WEEKS_YEAR ? nRec + 1 : Time.WEEKS_YEAR; 
		farm.resetStats();
	}
	/** Updates stats, but does not incrament time */
	public void updateStats(){
		if(tStats == null)
//		{
//System.out.println("Update canceled for null tStats");
			return;
//		}
		updateMainPanel();
		updateFinance();
		
	}

	/** Not a new step, but stuff has changed and needs to update panels */
	public void notifyUpdate(){
		updateStats();
		transPanel.notifyStep();
	}

	/** Calculates the current value of the farm */
/*	public double getValue(){
		// Add stored feeds
		double value = 0;
		for(int i = 0; i < Market.FEED_PRODUCTS.length ; i++){
			int id = Market.FEED_PRODUCTS[i];
			double pAvg = market.getAvgPrice(id);
			value += store.quantityStored(id) * pAvg;
		}
		// Add herd animals
		for(int i = 0; i < Market.LIVESTOCK_PRODUCTS.length ; i++){
			int id = Market.LIVESTOCK_PRODUCTS[i];
			double pAvg = market.getAvgPrice(id);
			value += store.quantityStored(id) * pAvg;
		}
		// Add current balance
		value += ((Data0D.Double)balance.getData()).doubleValue();
		return value;
	}
*/
	/** Displays the panel in a window */
	public void displayWindow(boolean display){
		if(display) window.pack();
		window.setLocation(650, 0);
		window.setVisible(display);  	
	}
	
	/** Close the window */
	public void close(){
		if(window != null)
			window.dispose();
	}

	/** For Testing */
	public static void main(String[] args){
		new JButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// Go ahead and complete round
				new Thread(){
					public void run(){
						Popup.inform("FARM STATUS\n" +
								" * Field Area\n" +
								" * Farm buildings capacity" +
								"PRODUCTIVITY\n" +
								" * Milk Produced:     Quota:     Overquota:    OQ Fines:    Avg Price:   Revenue:  \n" +
								" * Wheat Silage Produced:   Sold:   Stored Value:    Avg Price:     Revenue:  \n" +
								" * Maize Silage Produced:   Sold:   Stored Value:    Avg Price:     Revenue:  \n" +
								" * Grass Silage Produced:   Sold:   Stored Value:    Avg Price:     Revenue:  \n" +
								" * New Livestock:    Sold:    Culled:   Herd Value:   Avg Price:   Revenue:   \n" +
								"FINANCIAL PERFORMANCE\n" +
								" * Farm Value:    Costs:   Revenues:    Profit:    Balance:" +
								"ENVIRONMENTAL FRIENDLINESS\n" +
								" * Nitrate leaching: \n" +
								" * Fertiliser Use: \n" +
								" * Environmental Schemes:",
						"Evaluation");
					}
				}.start();
			}
		});

	}

}
