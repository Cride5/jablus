package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.agent.Action;
import uk.co.crider.jablus.agent.Agent;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.DisplayParams;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.agent.DairyAgent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/** Class to represent a generic planner, involving setting actions in perticular weeks of the year */
public abstract class FieldPlanner{
	
	private static final int TITLE_WIDTH = 140;
	private static final int BLOCK_WIDTH = 14;
	private static final int BLOCK_HEIGHT = 20;
	private static final Border NORMAL = new MatteBorder(new Insets(0, 1, 0, 0), new JLabel().getBackground());
	private static final Border CURRENT = new MatteBorder(new Insets(0, 3, 0, 0), Color.YELLOW);
	private static final Border HOVER = new MatteBorder(new Insets(2, 2, 2, 2), Color.GREEN.darker());
	private static final Border SELECTED = new MatteBorder(new Insets(2, 2, 2, 2), Color.GREEN);
	private static final Border MOVING = new MatteBorder(new Insets(2, 2, 2, 2), Color.RED);
	private static final Border BORDER_PLAN_SEL = new LineBorder(Color.YELLOW);
	private static final Color COL_HEADER = Color.LIGHT_GRAY;
	private static final Color COL_HSEL = new Color(220, 220, 220);
	
	// State components
	protected SubjectDisplayParams params;
	protected Agent agent;
	protected Time time;
	protected Action[][] action; // Actions by plan / week
	protected int rows;
	
	// Gui components
	private JablusWindow window;
	private JPanel panel;
	private JPanel mPanel;
	private JLabel spacer;
	private JPanel[] fPlan;
	private JLabel[] fTitle;
	private JLabel[][] wLab;
	
	// Internal state
	private int lastWeek;
	private int fId_from = -1;
	private int i_from = -1;
	private int row_from = -1;
	private Map<Integer, Action>[] actionMap; // Association of each plan with a set of template actions
	private boolean visible;
	
	public FieldPlanner(SubjectDisplayParams params, final Agent agent, final Time time, int rows){
		this.params = params;
		this.agent = agent;
		this.time = time;
		this.rows = rows;
		actionMap = new Hashtable[rows];
		lastWeek = time.getWeekOfYear();	
		// Generate block headings
		mPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		spacer = new JLabel();
		spacer.setPreferredSize(new Dimension(TITLE_WIDTH + time.getFirstWeekDayOfYear() * 2, BLOCK_HEIGHT));
		mPanel.add(spacer);
		for(int i = 0; i < Time.MONTHS_YEAR; i++){
			JLabel mLab = new JLabel(Time.MONTH_NAMES[i]);
			mLab.setHorizontalAlignment(JLabel.CENTER);
			mLab.setBorder(NORMAL);//new MatteBorder(new Insets(0, 1, 0, 0), mLab.getBackground()));
			mLab.setBackground(Color.LIGHT_GRAY);
			mLab.setForeground(Color.WHITE);
			mLab.setFont(mLab.getFont().deriveFont(Font.PLAIN));
			mLab.setOpaque(true);
			mLab.setPreferredSize(new Dimension(BLOCK_WIDTH * Time.MONTH_DAYS[i] / Time.DAYS_WEEK, BLOCK_HEIGHT));
			mPanel.add(mLab);
		}

		// Generate planners for each field
		fPlan = new JPanel[rows];
		fTitle = new JLabel[rows];
		action = new Action[rows][Time.WEEKS_YEAR + 1];
		wLab = new JLabel[rows][Time.WEEKS_YEAR + 1];
		visible = false;
//		for(int fId = 0; fId < fAreas.length; fId++){
	}

	/** Generates a planner with the given id */
	protected void genPlanner(final int id){
		// Generate planner panel for each field
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1;
		fPlan[id] = new JPanel(layout);
		// Generate field title
		fTitle[id] = new JLabel(" " + getRowTitle(id));
		fTitle[id].addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				fireRowSelected(id, e);
			}
		});
		fTitle[id].setPreferredSize(new Dimension(TITLE_WIDTH, BLOCK_HEIGHT));
		SubjectDisplayParams.setStyle(fTitle[id], DisplayParams.INFO);
		c.gridwidth = 10;
		layout.setConstraints(fTitle[id], c);
		fPlan[id].add(fTitle[id]);
		// Generate planner slots
		c.gridwidth = 1;
		for(int i = 0; i < wLab[id].length; i++){
			wLab[id][i] = new JLabel();
			wLab[id][i].setPreferredSize(new Dimension(BLOCK_WIDTH, BLOCK_HEIGHT));
			wLab[id][i].setHorizontalAlignment(JLabel.CENTER);
			wLab[id][i].setBorder(new MatteBorder(new Insets(0, 1, 0, 0), wLab[id][i].getBackground()));
			wLab[id][i].setForeground(Color.WHITE);//mLab.getBackground());
			wLab[id][i].setFont(wLab[id][i].getFont().deriveFont(Font.BOLD));
			wLab[id][i].setFont(wLab[id][i].getFont().deriveFont(10f));
			wLab[id][i].setBackground(getColour(id, i));
			wLab[id][i].setOpaque(true);
			layout.setConstraints(wLab[id][i], c);
			// Add mouse listener for adding week actions
			if(i < Time.WEEKS_YEAR){
//				final JLabel l = wLab[id][i];
				final int id_ = id;
				final int i_ = i;
				final ActionListener planListener = new ActionListener(){
					public void actionPerformed(final ActionEvent e){
						new Thread(){
							public void run(){
								handle(e);
							}
						}.start();
					}
					void handle(ActionEvent e){
						int aId = Integer.parseInt(((Component)e.getSource()).getName());
						if(aId == DairyAgent.ACTION_MOVE_BACK){
							int ip = i_ == 0 ? Time.WEEKS_YEAR - 1 : i_ - 1;
							action[id_][ip] = action[id_][i_];
							action[id_][i_] = null;
						}
						else if(aId == DairyAgent.ACTION_MOVE_FORWARD){
							action[id_][(i_ + 1) % Time.WEEKS_YEAR] = action[id_][i_];
							action[id_][i_] = null;
						}
						else if(aId == DairyAgent.ACTION_REMOVE){
							action[id_][i_] = null;
						}
						else{
							if(setActionParams(actionMap[id_].get(aId)))
								setAction(id_, i_, actionMap[id_].get(aId));
						}
						update(id_);
					}
				};
				final JPopupMenu planMenu = new JPopupMenu(){
                    private static final long serialVersionUID = 1L;
					public void setVisible(boolean v){
						super.setVisible(v);
						if(!v)
							wLab[id_][i_].setBorder(i_ == time.getWeekOfYear() ?
									CURRENT : NORMAL);
					}
				};
				// Add week summary to menu
				final JLabel titleItem = new JLabel();
				titleItem.setBorder(new EmptyBorder(3, 3, 3, 3));
				titleItem.setMaximumSize(new Dimension(200, 20));
				titleItem.setMinimumSize(new Dimension(200, 20));
				SubjectDisplayParams.setStyle(titleItem, SubjectDisplayParams.INFO);
				planMenu.add(titleItem);
				// Add action items to menu
				Collection<Action> aActions = getWeekActions(id);
				// Add actions to action map
				actionMap[id] = new Hashtable<Integer, Action>();
				for(Action a : aActions)
					actionMap[id].put(a.id, a);
				int[] actions = new int[aActions.size() + 3];
				final JMenuItem[] actionItems = new JMenuItem[actions.length];
				actions[1] = DairyAgent.ACTION_MOVE_BACK;
				actions[0] = DairyAgent.ACTION_MOVE_FORWARD;
				actions[2] = DairyAgent.ACTION_REMOVE;
				{ int j = 3; for(Action a : aActions) actions[j++] = a.id; }
				for(int j = 0; j < actions.length; j++){
					actionItems[j] = new JMenuItem(Constants.getName(actions[j]));
					actionItems[j].setName("" + actions[j]);
					actionItems[j].addActionListener(planListener);
					planMenu.add(actionItems[j]);
					if(j == 2){
						final JLabel newItem = new JLabel("New Action");
						newItem.setBorder(new EmptyBorder(3, 3, 3, 3));
						newItem.setMaximumSize(new Dimension(200, 20));
						newItem.setMinimumSize(new Dimension(200, 20));
						SubjectDisplayParams.setStyle(newItem, SubjectDisplayParams.INFO);
						planMenu.add(newItem);
					}
//					planMenu.add(new JSeparator());
				}
				wLab[id][i].addMouseListener(new MouseAdapter(){
					public void mousePressed(final MouseEvent e){
						new Thread(){ public void run(){
							if(e.getButton() == MouseEvent.BUTTON1){								
//								System.out.println("Pressed: fId=(" + fId_from + " -> " + fId_ + "), i=(" + i_from + " -> " + i_ + ")");
								if(fId_from == -1 && i_from == -1){
									fId_from = id_;
									i_from = i_;
									wLab[id_][i_].setBorder(MOVING);
								}
								else{
									wLab[fId_from][i_from].setBorder(
											i_from == time.getWeekOfYear() ?
													CURRENT : NORMAL);
									if(action[fId_from][i_from] != null && fId_from == id_){
										if(i_from == i_){
											if(canSetAction(id_, null, i_))
												remAction(id_, i_);
										}
										else{
											if(canSetActions(id_,
													new Action[]{action[fId_from][i_from], null  },
													new int[]   {i_                      , i_from})){
												setAction(id_, i_, action[fId_from][i_from]);
												remAction(fId_from, i_from);
											}
										}
										update(id_);
									}
									i_from = -1;
									fId_from = -1;
								}
							}
							else{
								// Reset selector to prevent annoyance
								if(fId_from != -1 && i_from != -1){
									wLab[fId_from][i_from].setBorder(
										i_from == time.getWeekOfYear() ?
												CURRENT : NORMAL);
									i_from = -1; fId_from = -1;
								}
								// Set selection colour
								wLab[id_][i_].setBorder(SELECTED);
								titleItem.setText(action[id_][i_] == null ? "Action" : action[id_][i_].toString());
								for(int j = 0; j < actionItems.length; j++){
									int aId = Integer.parseInt(actionItems[j].getName());
									if(j <= 2){
										if(action[id_][i_] == null) actionItems[j].setEnabled(false);
										else{
											switch(aId){
											case DairyAgent.ACTION_MOVE_BACK :
												actionItems[j].setEnabled(canSetActions(id_,
														new Action[]{action[id_][i_]                      , null},
														new int[]   {i_ == 0 ? Time.WEEKS_YEAR - 1 : i_ - 1, i_  }));
												break;
											case DairyAgent.ACTION_MOVE_FORWARD :
												actionItems[j].setEnabled(canSetActions(id_,
														new Action[]{action[id_][i_]          , null},
														new int[]   {(i_ + 1) % Time.WEEKS_YEAR, i_  }));
												break;
											case DairyAgent.ACTION_REMOVE :
												actionItems[j].setEnabled(canSetAction(id_, null, i_));
												break;
											}
										}
									}
									else{
										actionItems[j].setEnabled(canSetAction(id_, actionMap[id_].get(aId), i_));
									}
								}
								planMenu.show((Component)e.getSource(), e.getX(), e.getY());
							}

						}}.start();
					}
					public void mouseEntered(MouseEvent e){
						wLab[id_][i_].setBorder(
								id_ == fId_from && i_ == i_from ? MOVING :
									planMenu.isVisible() ? SELECTED :
										HOVER);
					}
					public void mouseExited(MouseEvent e){
						if(!planMenu.isVisible()){
							wLab[id_][i_].setBorder(
									id_ == fId_from && i_ == i_from ? MOVING :
										i_ == time.getWeekOfYear() ? CURRENT :
											NORMAL);
						}
					}
				});
			}
			fPlan[id].add(wLab[id][i]);

/*			fPlan[id].setBorder(new LineBorder(Color.YELLOW));
			fPlan[id].setMaximumSize(new Dimension(400, 20));
			fPlan[id].setMinimumSize(new Dimension(400, 20));
			fPlan[id].setPreferredSize(new Dimension(400, 20));
			fPlan[id].setSize(new Dimension(400, 20));
			fPlan[id].setAlignmentY(1);
*/
		}
//		if(visible){
//			panel.revalidate();
//			panel.repaint();
//		}
	}
	
	/** Returns true if gui components have been initialised for the given plan */
	protected boolean isPlanGuiInitialised(int row){
		return fPlan[row] != null;
	}

	protected final void fireRowSelected(int row, MouseEvent e){
		if(row_from >= 0){
//			fTitle[row_from].setBackground(COL_HEADER);
			fTitle[row_from].setBorder(null);
		}
//		fTitle[row].setBackground(COL_HSEL);
		fTitle[row].setBorder(BORDER_PLAN_SEL);
		row_from = row;
		rowSelected(row, e);
	}
	
	protected final void swapPlans(int r1, int r2){
//		System.out.println("swapping " + r1 + " and " + r2);
		Action[] a_ = action[r1];
		action[r1] = action[r2];
		action[r2] = a_;
		Map<Integer, Action> m_ = actionMap[r1];
		actionMap[r1] = actionMap[r2];
		actionMap[r2] = m_;
		update(r1);
		update(r2);
	}
	
	/** Returns the title of the plan with the given id */
	protected abstract String getRowTitle(int id);
	/** Returns the title used for the planner window */
	protected abstract String getWindowTitle();
	/** Returns the colour which should be applied to the given plan on the given week */
	protected abstract Color getColour(int id, int week);
	/** Returns the actions which should should be currently executed for the given plan id */
	protected abstract Collection<Action> getWeekActions(int id);
	/** Returns the letter which should be used to visualise the given action in the plan */
	protected abstract char getActionLetter(Action a);
	/** Returns true if the given action id is possible on the given plan in the given week */
	protected abstract boolean isActionPossible(int aId, int id, int week);
	/** Updates the state to be consistent with actions in the plan */
	protected abstract void updateState(int id);
	/** Called when a user selects a row */
	protected abstract void rowSelected(int row, MouseEvent e);
	/** Called when the window is closed */
	protected abstract void windowClosed();

	/** Sets multiple actions to execute, also generates plans if they don't already exist
	 * NOTE: It is assumed that the first action arguement specifies the plan id */
	protected void setActions(Action[] a, int[] s){
		for(int i = 0; i < a.length; i++){
			int id = ((Data0D.Integer)a[i].args[0]).intValue();
			if(fPlan[id] == null) genPlanner(id);
			action[id][s[i]] = a[i];
		}
		update();
	}

	/** Sets the action to execute for the given plan id, in the given week */
	protected void setAction(int id, int week, Action a){
		action[id][week] = a;
	}
	
	/** Sets up the parameters for the given action before it is added to the plan,
	 * returns false if user cancels the action  */
	protected abstract boolean setActionParams(Action a);
	
	/** Removes the action on the given plan in the given week */
	private void remAction(int id, int week){
		action[id][week] = null;
	}
	
	/** returns whether the given action can be set at the given point in the plan
	 * by simulating placement of the action and its effect on other actions */
	private boolean canSetAction(int id, Action a, int week){
		return canSetActions(id, new Action[]{a}, new int[]{week});
	}
	/** returns whether the given action can be set at the given point in the plan
	 * by simulating placement of the action and its effect on other actions */
	private boolean canSetActions(int id, Action[] a, int[] week){
		// If no existing actions and 
		// Record existing actions
		Action[] cur = new Action[a.length];
		for(int i = 0; i < a.length; i++){
			cur[i] = action[id][week[i]];
			// Can't set action of the same action is already present
			// or if its not possible
			if((a[i] != null  && cur[i] != null && a[i].id == cur[i].id)
			|| (a[i] != null && !isActionPossible(a[i].id, id, week[i])))
//System.out.println("Action:" + Constants.getName(a[i].id) + " isn't possible");
				return false;
		}
		// Set simulated actions
		for(int i = 0; i < a.length; i++) action[id][week[i]] = a[i];
		// Calculate new crop state
//		update();
//		fPlan[fId].repaint();
//		try{ Thread.sleep(300); }catch(Exception e){}
		updateState(id);
		// Check if this is acceptable
		for(int w = 0; w < action[id].length; w++){
			// If any of the actions in the newly updated crop scheme are impossible
			// then we cannot set this action
			if(action[id][w] != null){
				// Only check non-candidate actions
				boolean isCand = false;
				for(int i = 0; i < a.length; i++){
					if(action[id][w] == a[i]){
						isCand = true; 
						break;
					}
				}
				if(!isCand && !isActionPossible(action[id][w].id, id, w)){
					// return to previous state
					for(int i = 0; i < a.length; i++) action[id][week[i]] = cur[i];
					updateState(id);
//System.out.println("Action:" + Constants.getName(action[fId][w].id) + " isnt compatible with current state");
					return false;
				}
			}
		}
		// return to previous state
		for(int i = 0; i < a.length; i++) action[id][week[i]] = cur[i];
		updateState(id);
		return true;
	}
	
	/** Removes all actions from the action array */
	protected final void clearActions(){
		for(int id = 0; id < wLab.length; id++)
			if(fPlan[id] != null){
				for(int wk = 0; wk < action[id].length; wk++)
					action[id][wk] = null;
			}
	}
	
	/** Updates all plan visualisations after actions have been changed */
	protected final void update(){
		for(int id = 0; id < wLab.length; id++)
			if(fPlan[id] != null) update(id);
	}

	/** Updates the given plan visualisation after actions have been changed */
	protected final void update(int id){
		updateState(id);
		for(int i = 0; i < time.getWeeksThisYear(); i++){
			if(action[id][i] != null)
				wLab[id][i].setText("" + getActionLetter(action[id][i]));
			else
				wLab[id][i].setText("");
			wLab[id][i].setBackground(getColour(id,i));
		}
	}
		
	/** TODO: Fix non-alginment bug, reproduce by running training scenario, clicking to second year
	 * and creating/opening a new crop plan. Its not aligned properly!!
	 * 
	 * Update after new time step */
	public void redisplay(){
		int week = time.getWeekOfYear();
		for(int fId = 0; fId < wLab.length; fId++){
			// Don't redisplay plans which don't exist yet
			if(fPlan[fId] == null) continue;
			// Reset previous 'current week' indicator
//			wLab[fId][lastWeek].setBackground(Color.GREEN.darker());
			wLab[fId][lastWeek].setBorder(NORMAL);
			if(week == 0){
				// Update block heading positions
				spacer.setPreferredSize(new Dimension(TITLE_WIDTH + time.getFirstWeekDayOfYear() * 2, BLOCK_HEIGHT));
				spacer.revalidate();
				// Grey out last week if current year doen't have that many
				if(time.getWeeksThisYear() <= Time.WEEKS_YEAR)
					wLab[fId][Time.WEEKS_YEAR].setBackground(Color.LIGHT_GRAY);
				else
					wLab[fId][Time.WEEKS_YEAR].setBackground(getColour(fId, Time.WEEKS_YEAR - 1));
			}
			// Set 'current week' indicator
//			wLab[fId][week].setBackground(Color.GREEN);
			wLab[fId][week].setBorder(CURRENT);
		}
		// set lastweek
		lastWeek = week;
	}
	
	/** Displays planner for all fields which have a planner */
	public void showPlanners(Component parent){
		SortedSet<Integer> toShow = new TreeSet<Integer>();
		for(int i = 0; i < fPlan.length; i++)
			if(fPlan[i] != null) toShow.add(i);
		showPlanners(parent, toShow);
	}
	
	/** Displays planner for the given fields */
	public void showPlanners(Component parent, SortedSet<Integer> toShow){
		if(visible)
			window.dispose();
		visible = true;
		panel = new JPanel(new GridLayout(toShow.size() + 1, 1, 0, 1));
		panel.setSize(0, 0);
//		panel.setPreferredSize(new Dimension(0, 0));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(mPanel);
		for(int fId : toShow){
			// Generate planners as needed at last minute
			if(fPlan[fId] == null) genPlanner(fId);
			// Update title text
			fTitle[fId].setText(getRowTitle(fId));
			panel.add(fPlan[fId]);
		}
		window = new JablusWindow(getWindowTitle(), false, false, true){
            private static final long serialVersionUID = 1L;
			public void dispose(){
				windowClosed();
				visible = false;				
				super.dispose();
			}
		};
		JScrollPane pn = new JScrollPane(panel);
//		pn.getViewport().setSize(0, 0);
//		pn.
//		pn.validate();
		
//		window.pack();
		window.add(pn);
		window.pack();
		int aw = window.getPreferredSize().width - window.getContentPane().getComponents()[0].getPreferredSize().width;
		int ah = window.getPreferredSize().height - window.getContentPane().getComponents()[0].getPreferredSize().height;
//		System.out.println("aw:" + aw + ", ah:" + ah + " panelSize" + panel.getPreferredSize());
//		window.setSize(panel.getPreferredSize().width + 12, panel.getPreferredSize().height + 31);
		window.setSize(panel.getPreferredSize().width + aw + 2, panel.getPreferredSize().height + ah + 2);
//		window.setResizable(false);
//		window.setLocationRelativeTo(parent);
		window.setLocation(
				parent.getLocation().x + parent.getSize().width / 2 - window.getSize().width / 2,
				parent.getLocation().y + parent.getSize().height - window.getSize().height
		);
		redisplay();
		window.setVisible(true);
	}
	
	public void close(){
		if(window != null)
			window.dispose();
	}

	/** Returns the action planned for the given field (or null if none present) */
	public Action getAction(int row){
		int wk = time.getWeekOfYear();
		if(wk == Time.WEEKS_YEAR) return null;
		return action[row][wk == 0 ? Time.WEEKS_YEAR - 1 : wk - 1];
	}
	
}
