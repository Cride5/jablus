package uk.co.crider.jablus.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/** Extention of JTable object representing a scrollable table */
public class Table extends JTable {

	/** Unique class ID */
	private static final long serialVersionUID = 1L;

	private JScrollPane pane;


	public Table(JScrollPane pane, String[] row, String[] col, String[][] content){
		this.pane = pane;
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		updateModel(row, col, content);
		pane.setViewportView(this);
	}


	private void updateModel(String[] row, String[] col, String[][] content){

		if (col == null)
			setModel(new DefaultTableModel(content,
					new String[content[0].length]));
		else
			setModel(new DefaultTableModel(content, col));

		if (row != null) {
			JList rowHeader = new JList(row);
			// rowHeader.setFixedCellWidth(50);
			rowHeader.setFixedCellHeight(getRowHeight());
			// + getRowMargin()
			// + getIntercellSpacing().height);
			rowHeader.setCellRenderer(new RowHeaderRenderer(this));
			rowHeader.setBackground(getTableHeader().getBackground());
			pane.setRowHeaderView(rowHeader);
		}
		if (col == null)
			setTableHeader(null);
	}

	class RowHeaderRenderer extends JLabel implements ListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5248644041536315437L;

		RowHeaderRenderer(JTable table) {
			JTableHeader header = table.getTableHeader();
			setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(CENTER);
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setFont(header.getFont());
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}


}