/*
 * File:   Popup.java
 * Author: Conrad Rider
 * Email:  cride5@crider.co.uk
 * Date:   22-02-05
 */

package uk.co.crider.jablus.gui;

import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.models.dairy.agent.DecisionNarrative;
import uk.co.crider.jablus.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

/** Generates and displays popups */
public class Popup extends JablusWindow{

	/** Unique class ID */
    private static final long serialVersionUID = -6681956311085176724L;

    private final MouseListener CLICK_LISTENER = new MouseAdapter(){
		public void mousePressed(MouseEvent e){
			clicked = true;
			System.out.println("Click");
		}
	};

    protected boolean okPressed;
    protected boolean showCancel;
    protected boolean clicked;
	public Popup(String title){ this(title, true); }
	public Popup(String title, boolean showCancel){
		super(title, false, false, false);
		this.showCancel = showCancel;
		this.clicked = false;
		addMouseListener(CLICK_LISTENER);
		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
	}

	public boolean hasBeenClicked(){
		return clicked;
	}

	/** Displays a basic popup with OK and Cancel buttons,
	 * the given jpanel forms the main body of the popup */
	public void displayPopup(Frame parent, Component fp){
		okPressed = false;
		fp.addMouseListener(CLICK_LISTENER);
		Container pn = getContentPane();
		SpringLayout layout = new SpringLayout();
		pn.setLayout(layout);

		JSeparator sp = new JSeparator(); pn.add(sp);
		JButton ok = new JButton("OK"); pn.add(ok);
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				okPressed = true;
				dispose();
            }
		});
		JButton cn = null;
		if(showCancel){
			cn = new JButton("Cancel"); pn.add(cn);
			cn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
		}
		pn.add(fp);
		layout.putConstraint(SpringLayout.NORTH, fp, 5, SpringLayout.NORTH, pn);
		layout.putConstraint(SpringLayout.WEST,  fp, 5, SpringLayout.WEST,  pn);
		layout.putConstraint(SpringLayout.EAST,  pn, 5, SpringLayout.EAST,  fp);

		layout.putConstraint(SpringLayout.NORTH, sp, 10, SpringLayout.SOUTH, fp);
		layout.putConstraint(SpringLayout.WEST,  sp, 0, SpringLayout.WEST,  fp);
		layout.putConstraint(SpringLayout.EAST,  sp, 0, SpringLayout.EAST,  fp);
		layout.putConstraint(SpringLayout.NORTH, ok, 5, SpringLayout.SOUTH, sp);
		layout.putConstraint(SpringLayout.EAST,  ok, 0, SpringLayout.EAST,  sp);
		layout.putConstraint(SpringLayout.EAST,  pn, 5, SpringLayout.EAST,  ok);
		layout.putConstraint(SpringLayout.SOUTH, pn, 5, SpringLayout.SOUTH, ok);
		if(showCancel){
			layout.putConstraint(SpringLayout.NORTH, cn, 5, SpringLayout.SOUTH, sp);
			layout.putConstraint(SpringLayout.EAST,  cn, -10, SpringLayout.WEST,  ok);
		}

		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	/** Generates input fields on a JPanel, based on the given data array.
	 * When the user inputs data, it is populated in the given data array */
	public static JPanel genInputFields(Data[] data){
		SpringLayout layout = new SpringLayout();
		JPanel pn = new JPanel(layout);
		final String[] prev = new String[data.length];
		JLabel lb_ = null;
		JTextField in_ = null;
		for(int i = 0; i < data.length; i++){
			JLabel lb = new JLabel(data[i].getName()); pn.add(lb);
			final JTextField in = new JTextField(); pn.add(in);
			in.setPreferredSize(new Dimension(80, in.getPreferredSize().height));
			if(data[i] instanceof Data0D){
				final Data0D d = (Data0D)data[i];
				String lTxt = d.getName();
				if(d.hasRange() || !d.getUnits().equals("")){
					lTxt = lTxt + " (";
					if(d.hasRange()){
						lTxt = lTxt + d.getMin() + " - " + d.getMax();
						if(!d.getUnits().equals("")) lTxt = lTxt + " "; }
					if(!d.getUnits().equals("")) lTxt = lTxt + d.getUnits();
					lTxt = lTxt + ")";
				}
				lb.setText(lTxt);
				in.setText("" + d.getValue());
				final int j = i;
				in.addKeyListener(new KeyListener(){
					public void keyPressed(KeyEvent e){
						String newTxt = in.getText();
						Number min = d.getMin();
						Number max = d.getMax();
						if(newTxt.equals("-") || newTxt.equals("")){
					//	|| (newTxt.endsWith(".") && d instanceof Data0D.Double)){
							d.setValue(0);
							prev[j] = newTxt;
							return;
						}
						if(newTxt.length() > 5000){
							in.setText(prev[j]);
							return;
						}
						try{
							if(d instanceof Data0D.Integer){
									int v = Integer.parseInt(newTxt);
								if(d.hasRange() && v < min.intValue()){
									in.setText("" + min);
									d.setValue(min);
								}
								else if(d.hasRange() && v > max.intValue()){
									in.setText("" + max);
									d.setValue(max);
								}else{
									prev[j] = newTxt;
									d.setValue(v);
								}
							}else{
								double v = Double.parseDouble(newTxt);
								if(d.hasRange() && v < min.doubleValue()){
									in.setText("" + min);
									d.setValue(min);
								}
								else if(d.hasRange() && v > max.doubleValue()){
									in.setText("" + max);
									d.setValue(max);
								}
								else{
									prev[j] = newTxt;
									d.setValue(v);
								}
							}
						}catch(NumberFormatException ex){
							in.setText(prev[j]);
						}
					}
					public void keyReleased(KeyEvent e){ keyPressed(e); }
					public void keyTyped(KeyEvent e){ keyPressed(e); }
				});
			}
			if(data[i] instanceof Data0D){
				if(i == 0){
					layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, pn);
					layout.putConstraint(SpringLayout.WEST,  lb, 5, SpringLayout.WEST,  pn);
					layout.putConstraint(SpringLayout.NORTH, in, 5, SpringLayout.NORTH, pn);
					layout.putConstraint(SpringLayout.EAST,  pn, 5, SpringLayout.EAST,  in);
					layout.putConstraint(SpringLayout.WEST,  in, 80, SpringLayout.EAST, lb);
				}else{
					layout.putConstraint(SpringLayout.NORTH, lb, 15, SpringLayout.SOUTH,  lb_);
					layout.putConstraint(SpringLayout.WEST,  lb,  0, SpringLayout.WEST,   lb_);
					layout.putConstraint(SpringLayout.NORTH, in, 15, SpringLayout.SOUTH,  lb_);
					layout.putConstraint(SpringLayout.EAST,  in,  0, SpringLayout.EAST,   in_);
					layout.putConstraint(SpringLayout.WEST,  in,  0, SpringLayout.WEST,   in_);
				}
			}else{
				if(i == 0){
					layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.NORTH, pn);
					layout.putConstraint(SpringLayout.WEST,  lb, 5, SpringLayout.WEST,  pn);
					layout.putConstraint(SpringLayout.EAST,  pn, 5, SpringLayout.EAST,  lb);
				}else{
					layout.putConstraint(SpringLayout.NORTH, lb, 5, SpringLayout.SOUTH, lb_);
					layout.putConstraint(SpringLayout.WEST,  lb, 0, SpringLayout.WEST,  lb_);
					layout.putConstraint(SpringLayout.EAST,  lb, 0, SpringLayout.EAST,  lb_);
				}
			}
			lb_ = lb;
			in_ = in;
		}
		layout.putConstraint(SpringLayout.SOUTH, pn, 5, SpringLayout.SOUTH, in_);
		return pn;
	}

	/** Called when user presses cancel or window's close button */
	public void dispose(){
		super.dispose();
		synchronized(this){
			notify();
		}
	}

	public static Data queryData(Data data){
		return queryData(null, "Input " + data.getName(), data); }
	public static Data queryData(String title, Data data){
		return queryData(null, title, data); }
	public static Data queryData(Frame parent, String title, Data data){
		Data[] r = queryData(parent, title, new Data[]{ data });
		return r == null ? null : r[0]; }
	public static Data[] queryData(Data[] data){
		return queryData(null, "Input Data", data); }
	public static Data[] queryData(String title, Data[] data){
		return queryData(null, title, data); }
	/** Displays a popup for user to enter data based on given Data objects */
	public static Data[] queryData(final Frame parent, String title, final Data[] data){
		final Popup p = new Popup(title);

		// Clone data to allow reversion to default
//		Data[] defData = new Data[data.length];
//		for(int i = 0; i < data.length; i++) defData[i] = (Data)data[i].clone();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				p.displayPopup(parent, genInputFields(data));
			}
		});
		synchronized(p){
			try{ p.wait(); }
			catch(InterruptedException e){
				e.printStackTrace(); }
		}
		if(p.okPressed) return data;
		return null;
//		return defData;
	}

	/** Displays a popup allowing the user to input decision data */
	public static void queryDecisions(final Frame parent, final List<DecisionNarrative> decisions){
		final Popup p = new Popup("Decisions");

		// Clone data to allow reversion to default

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				p.displayPopup(parent, p.genDecisionFields(decisions));
			}
		});
		synchronized(p){
			try{ p.wait(); }
			catch(InterruptedException e){
				new Thread(){
					public void run(){
						p.dispose();}
				}.start();
//				e.printStackTrace();
			}
		}
		if(p.okPressed){
			decisions.clear();
			Iterator<JTextField> what = p.whatList.iterator();
			Iterator<JTextField> why = p.whyList.iterator();
			while(what.hasNext()){
				String wt = what.next().getText();
				String wy = why.next().getText();
				if(wt.trim().equals("") && wy.trim().equals("")) continue;
				decisions.add(new DecisionNarrative(wt, wy));
			}
		}
	}
	/** Generates JPanel with decision query fields */
	private synchronized JPanel genDecisionFields(List<DecisionNarrative> decisions){
		final int width = 800;
		final JPanel body = new JPanel(new BorderLayout());
		final JPanel dPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JScrollPane decisionScroller = new JScrollPane(dPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		decisionScroller.setPreferredSize(new Dimension(width + 20, 500));
		body.add(decisionScroller);

		final IntegerData i = new IntegerData(0, 0);
		for(Iterator<DecisionNarrative> it = decisions.iterator(); it.hasNext(); i.setValue(i.intValue() + 1)){
			dPanel.add(genDecisionField(i, it.next(), width - 10));
			dPanel.add(genSpacer(width - 10, 10));
		}
		dPanel.add(genDecisionField(i, null, width - 10));
		final JButton addButton = new JButton("Add Another Decision");
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dPanel.remove(addButton);
				dPanel.add(genSpacer(width - 10, 10));
				dPanel.add(genDecisionField(i, null, width - 10));
				dPanel.add(addButton);
				dPanel.setPreferredSize(new Dimension(width, whatList.size() * 140 + 35));
				dPanel.revalidate();
			}
		});
		dPanel.setPreferredSize(new Dimension(width, whatList.size() * 140 + 35));
		dPanel.add(addButton);

		return body;
	}
	private List<JTextField> whatList = new LinkedList<JTextField>();
	private List<JTextField> whyList = new LinkedList<JTextField>();
	private JLabel genSpacer(int width, int height){
		JLabel spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(width, height));
		return spacer;
	}
	private JPanel genDecisionField(IntegerData n, DecisionNarrative d, int width){
		JPanel dPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		dPanel.setPreferredSize(new Dimension(width, 120));
		dPanel.setBorder(new EtchedBorder());
		dPanel.setBackground(Color.LIGHT_GRAY);
		JLabel whatLabel = new JLabel("What is the decision and how is it being carried out?");
		dPanel.add(whatLabel);
		JTextField whatField = new JTextField(d == null ? "" : d.what);
		whatField.setPreferredSize(new Dimension(width - 20, whatField.getPreferredSize().height));
		dPanel.add(whatField);
		whatList.add(whatField);
		JLabel whyLabel = new JLabel("Why has this decision been made?");
		dPanel.add(whyLabel);
		JTextField whyField = new JTextField(d == null ? "" : d.why);
		whyField.setPreferredSize(new Dimension(width - 20, whyField.getPreferredSize().height));
		dPanel.add(whyField);
		whyList.add(whyField);
		return dPanel;
	}


	/** Displays a popup to inform user of a messsage */
	public static void inform(String message){
		JOptionPane.showMessageDialog(null, message);}
	/** Displays a popup to inform user of a messsage. Includes custom window title */
	public static void inform(String message, String title){
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/** Displays a popup to query the user for a decision.
	 * Method blocks until user makes decision */
	public static boolean verify(String message){
		return 0 == JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.YES_NO_OPTION);}
	/** Displays a popup to query the user for a decision.
	 * Method blocks until user makes decision. Includes custom window title */
	public static boolean verify(String message, String title){
		return 0 == JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);}

	/** Displays popup informing user of an error */
	public static void error(String message){
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);}

	/** For testing */
	public static void main(String[] args){
//		System.out.println("p=" + queryNumber("Number", INTEGER, 0, 10));
//		Popup p = new DecisionPopup(null, "test", new JLabel("TEST asdf asdf asdfa df"));
/*		query(null, "test", new Data[]{
//				new DoubleData("DoubleData1"),
				new IntegerData("IntegerData1"),
				new IntegerData("IntegerData2  "),
				new IntegerData("IntegerData3"),
				new IntegerData("IntegerData4 "),
		});
*/
		//Popup p = new DecisionPopup("test");
		queryDecisions(null, null);

		if(true) return;
		Data[] data = queryData(null, "test", new Data[]{
				new DoubleData(0),
				new DoubleData(0, "m²"),
				new DoubleData(0, -10, 10),
				new DoubleData(0, "m²", 100, 500),
				new IntegerData(0),
				new IntegerData(0, "m²"),
				new IntegerData(0, -10, 10),
				new IntegerData(0, "m²", 10, 100),
		});
		System.out.println(Utils.arrayString(data));
		if(true) return;
	}

}
