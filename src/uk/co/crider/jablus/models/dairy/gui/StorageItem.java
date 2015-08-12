package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.gui.data.DataLabel;
import uk.co.crider.jablus.models.dairy.env.Storage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

/** Visualises a storage item within a Storage object */
public class StorageItem extends JPanel {
	

	/** Unique class ID */
    private static final long serialVersionUID = -8928816307728405241L;

    private DataLabel[] labels;

	public StorageItem(DisplayParams params, String title, Data0D capacity, Data0D[] items, Color[] colours){
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.HORIZONTAL;
		con.anchor = GridBagConstraints.NORTH;
		con.weightx = 1.0;
		con.insets = new Insets(2, 5, 0, 5);
		setLayout(layout);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		JLabel storeTitle = new JLabel(title);
		storeTitle.setFont(storeTitle.getFont().deriveFont(Font.BOLD));
		layout.setConstraints(storeTitle, con);
		add(storeTitle);
		DataLabel capLabel  = new DataLabel(capacity, false, true, false, 1);
//		JLabel(capacity.getValue() + " " + capacity.getUnits());
		capLabel.setHorizontalAlignment(JLabel.RIGHT);
		con.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(capLabel, con);
		add(capLabel);
		StoreBar bar = new StoreBar(capacity, items, colours);
		bar.setPreferredSize(new Dimension(getPreferredSize().width - 15, 18));
		bar.setMinimumSize(new Dimension(getPreferredSize().width - 15, 18));
		con.insets = new Insets(1, 5, 1, 5);
		layout.setConstraints(bar, con);
		add(bar);
		labels = new DataLabel[items.length + 1];
		for(int i = 0; i < items.length; i++){
			if(i == items.length - 1) con.weighty = 1.0;
			final Color c = colours[i];
			JLabel l = new JLabel(items[i].getName());
			l.setFont(l.getFont().deriveFont(11f));
			l.setIcon(new Icon(){
				private Color col = c;
				public int getIconHeight(){ return 10; }
				public int getIconWidth(){ return 10; }
				public void paintIcon(Component c, Graphics g, int x, int y) {
					g.setColor(col);
	                g.fillRect(x, y, getIconWidth(), getIconHeight());
                }
			});
			con.gridwidth = 1;
			layout.setConstraints(l, con);
			add(l);
			labels[i] = new DataLabel(items[i], false, true, false, 2);
			labels[i].setFont(labels[i].getFont().deriveFont(11f));
			labels[i].setHorizontalAlignment(JLabel.RIGHT);
			con.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(labels[i], con);
			add(labels[i]);
		}
		labels[labels.length - 1] = capLabel;
	}
	/** Rederaws the object to reflect the updated state */
	public void redisplay(){
		for(DataLabel l : labels){
			l.redisplay();
		}
		repaint();
	}
	/** A visualisation of the quantity of items in the store and the space remaining */
	class StoreBar extends JLabel{
		/** */
        private static final long serialVersionUID = -1808854213079492214L;
		private Data0D capacity;
		private Data0D[] items;
		private Color[] colours;
		public StoreBar(Data0D capacity, Data0D[] items, Color[] colours){
			this.capacity = capacity;
			this.items = items;
			this.colours = colours;
			setBorder(new BevelBorder(BevelBorder.LOWERED));
		}
		public void paint(Graphics g){
			super.paint(g);
			Insets in = getInsets();
			int w = getSize().width - in.left - in.right;
			int h = getSize().height - in.top - in.bottom;
			double pos = in.left;
			for(int i = 0 ; i < items.length; i++){
				double percent = items[i].getValue().doubleValue() / (capacity.getValue().doubleValue() * Storage.DENSITY.get(items[i].getId()));
				percent = percent < 0 ? 0 : percent > 1 ? 1 : percent;
				double bw = w * percent;
				g.setColor(colours[i]);
				g.fillRect((int)pos, in.top, (int)Math.ceil(bw), h);
				pos = pos + bw;
			}
		}
	}
	
	/** For testing purposes */
	public static void main(String[] args){
		JablusWindow f = new JablusWindow("", false, false, true);
		Data0D[] data = new Data0D[]{
				new DoubleData(0, 20.6),
				new IntegerData(1, 43),
		};
		StorageItem store = new StorageItem(null, "Cow Steading", new uk.co.crider.jablus.data.IntegerData(0, 100), data, new Color[]{
				Color.GREEN.darker(),
				Color.BLUE.darker()
		});
		f.add(store);
		f.pack();
		f.setVisible(true);
		while(true){
			try{ Thread.sleep(500); }
			catch(Exception e){}
			store.redisplay();
			store.repaint();
			for(int i = 0; i < data.length; i++){
				data[i].setValue(Math.random() * 50);
			}
		}
	}

}
