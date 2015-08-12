package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.Popup;
import uk.co.crider.jablus.gui.data.DataTable;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;
import uk.co.crider.jablus.models.dairy.env.Market;
import uk.co.crider.jablus.models.dairy.env.Storage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

class TransactionsPanel extends JPanel {
	
	/** Unique class ID */
    private static final long serialVersionUID = 402437962456050802L;
    
	private SubjectInterface iface;
	private DairyAgent agent;
	private Market market;
	private DoubleData balance;
	private DoubleData netProfit;
	private DoubleData costs;
	private DoubleData revenue;
	private Storage store;
	
	private JablusWindow window;	
	private JScrollPane tableScroller;
//	private DataTable summary;
	private JPanel summaryPanel;
	private DataTable transactionTable;
//	private JButton buyButton;
//	private JButton sellButton;
	
	
	private int[] weights = new int[]{10, 5, 2, 5};
	private int[] cTypes = new int[]{DataTable.NORMAL, DataTable.NORMAL, DataTable.NORMAL, DataTable.BALANCE};
	
	private static EmptyBorder border = new EmptyBorder(2, 10, 2, 10);

	public TransactionsPanel(SubjectInterface iface_, DairyAgent agent_){
		super(new BorderLayout());
		this.iface = iface_;
		this.agent = agent_;
//		SubjectInterface.genInputPanel(this, "Market Trading");
//		p.add(tableEntry("Item", "Qty", "Gained"), BorderLayout.NORTH);
		DataTable headings = new DataTable(new Object[][]{{"Item", "Quantity", "", "SubTotal"}}, weights);
		headings.setBorder(border);
		
/*		Object[][] data = {
				{"Cows Born", 3, "", 32.2},
				{"Milk Production", 1490, "litres", 12.44},
				{"Wheat Harvested", 23.22, "t", 932},
				{"Eggs egged", -32.002, "eggs", 23},
				{"Manure produced", 23, "", -20.25},
				{"Tractors bogged", 2.4, "litres", -200.44},
				{"Chickens choaked", 12.4, "t", 232},
				{"Moos moved", 34, "eggs", 0.03},
				{"Cows Born", 3, "", 32.2},
				{"Milk Production", 1490, "litres", 12.44},
				{"Wheat Harvested", 23.22, "t", 932},
				{"Eggs egged", -32.002, "eggs", 23},
				{"Manure produced", 23, "", -20.25},
				{"Tractors bogged", 2.4, "litres", -200.44},
				{"Chickens choaked", 12.4, "t", 232},
				{"Moos moved", 34, "eggs", 0.03},
				{"Cows Born", 3, "", 32.2},
				{"Milk Production", 1490, "litres", 12.44},
				{"Wheat Harvested", 23.22, "t", 932},
				{"Eggs egged", -32.002, "eggs", 23},
				{"Manure produced", 23, "", -20.25},
				{"Tractors bogged", 2.4, "litres", -200.44},
				{"Chickens choaked", 12.4, "t", 232},
				{"Moos moved", 34, "eggs", 0.03},
		};
		JPanel table = new DataTable(data, weights );
		Insets bi = border.getBorderInsets();
		int th = table.getPreferredSize().height;
		if(th < HEIGHT)
			table.setBorder(new EmptyBorder(bi.top, bi.left, HEIGHT - th, bi.right));
		else
			table.setBorder(border);
*///		tableScroller.getViewport().add(table);
		
		tableScroller = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel bottom = new JPanel();
		BoxLayout bl = new BoxLayout(bottom, BoxLayout.Y_AXIS);
		bottom.setLayout(bl);
		summaryPanel = new JPanel(new BorderLayout());
		summaryPanel.setBorder(new EmptyBorder(0, 0, 0, 27));
		bottom.add(summaryPanel);
		JPanel trade = new JPanel(new GridLayout(1,2));
/*		buyButton = new JButton("Buy");
		trade.add(buyButton);
		buyButton.addMouseListener(SHOW_MENU);
		sellButton = new JButton("Sell");
		trade.add(sellButton);
		sellButton.addMouseListener(SHOW_MENU);
*/		
/*new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				final JPopupMenu sellMenu = new JPopupMenu();
				ActionListener marketListener = HANDLE_MARKET_SELL;
				
				for(int i = 0; i < Market.SALE_PRODUCTS.length; i++){
					Action a = agent.getActionExecutable(DairyAgent.ACTION_SELL);
					a.args[0] = new IntegerData(DairyAgent.ACTION_PARAM_PRODUCT, Market.SALE_PRODUCTS[i]);
					JMenuItem actionItem = new JMenuItem(Constants.getName(Market.SALE_PRODUCTS[i]));
					actionItem.setName("" + Market.SALE_PRODUCTS[i]);
					actionItem.addActionListener(marketListener);
					sellMenu.add(actionItem);
					actionItem.setEnabled(iface.isActionPossible(a));
				}
				sellMenu.show(e.getComponent(), e.getX(), e.getY());	            
            }
		});
		*/
		bottom.add(trade, BorderLayout.CENTER);
		
		
		add(headings, BorderLayout.NORTH);
		add(tableScroller, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		window = new JablusWindow("Market Data", false, false, true){
            private static final long serialVersionUID = 1L;
			public void dispose(){
				displayWindow(false);
			}
		};
		window.add(this);
		window.setSize(new Dimension(290, 600));
	}
	
	/** Sets the model's economic object */
	void setEconomics(Market economics){
		this.market = economics;
		for(Data item : economics.getItems()){
			if(item.getId() == Constants.DERIVED_ECONOMIC_COSTS)
				costs = (DoubleData)item;
			else if(item.getId() == Constants.DERIVED_ECONOMIC_REVENUE)
				revenue = (DoubleData)item;
			else if(item.getId() == Constants.DERIVED_ECONOMIC_PROFIT)
				netProfit = (DoubleData)item;
			else if(item.getId() == Constants.DERIVED_ECONOMIC_BALANCE)
				balance = (DoubleData)item;
		}
		genTransactionTable();
		genSummaryTable();
		alignTransactionTable();
	}
	
	/** Sets the mode's storage object */
	void setStore(Storage store){
		this.store = store;
	}
	
	/** Notify the panel that a simulation step is complete */
	void notifyStep(){
		genTransactionTable();
		genSummaryTable();
//		alignTransactionTable();
	}
	
	/** Displays the panel in a window */
	void displayWindow(boolean display){
		window.setVisible(display);
	}
	
	/** Generates a table of transactions */
	private void genTransactionTable(){
		// Add data to table
		transactionTable = new DataTable(market.getTransactions(), weights, cTypes);
		tableScroller.getViewport().removeAll();
		tableScroller.getViewport().add(transactionTable);
	}
	/** Causes the transaction table to be aligned with the top of the panel */
	private void alignTransactionTable(){
		// This blocking call to validate ensures sizes are properly
		// updated before measuring scrollwindow height
		validate();
		Insets bi = border.getBorderInsets();
		int th = transactionTable.getPreferredSize().height;
		int HEIGHT = tableScroller.getSize().height - 10;
		if(th < HEIGHT){
			transactionTable.setBorder(new EmptyBorder(bi.top, bi.left, HEIGHT - th, bi.right));
		}else{
			transactionTable.setBorder(border);
		}
	}
	/** Generates summary at bottom of transaction table */
	private void genSummaryTable(){
		// Generate balance summary
		DataTable summary = new DataTable(new Object[][]{
				{costs.getName(), costs.getValue()},
				{revenue.getName(), revenue.getValue()},
				{netProfit.getName(), netProfit.getValue()},
				{balance.getName(), balance.getValue()},
			}, new int[]{1}, new int[]{
				DataTable.NORMAL,
				DataTable.BALANCE
			}
		);
		summary.setBorder(border);
		summaryPanel.removeAll();
		summaryPanel.add(summary, BorderLayout.CENTER);
	}
	
	/** Updates table for new transacitons */
	public void addTransaction(){
		List<Object[]> latest = market.getTransactions();
		if(latest.size() <= 0) return;
		transactionTable.addAnotherRow(latest.get(latest.size() - 1));
		transactionTable.setBorder(null);
		genSummaryTable();
//		alignTransactionTable();
	}
	
	
}
