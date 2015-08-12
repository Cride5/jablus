package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.gui.JablusWindow;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** User Interface for humans acting as land use agents.
 * An instance of this is created for each HumanAgent object */
public class DataCollectionInterface extends JablusWindow {

	/** Unique class ID */
    private static final long serialVersionUID = 4953148206580318511L;
    
    private TreeMap<String, JTextField> data;
//    private JTextField matricNo;
    private boolean done;
    
    private Map<String, String> fieldMappings = new Hashtable<String, String>();
    
    private static final int LABEL_WIDTH = 220;
    private static final int FIELD_WIDTH = 250;
    private static final int FIELD_HEIGHT = 28;
    
	public DataCollectionInterface(String[] toCollect){
	    super(SubjectDisplayParams.DATA_COLLECTION_TITLE, false, true);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    data = new TreeMap<String, JTextField>();
		done = false;
		// Retrive subject details
		fieldMappings.put(toCollect[0], "Name");
		fieldMappings.put(toCollect[1], "Matric No");
		fieldMappings.put(toCollect[2], "Email");
		fieldMappings.put(toCollect[3], "Age");
		fieldMappings.put(toCollect[4], "First Language");
		fieldMappings.put(toCollect[5], "Course Level [eg. BSc, MSc, PhD]");
		fieldMappings.put(toCollect[6], "Area of Study [eg. Biology]");
		fieldMappings.put(toCollect[7], "Year of Study [eg. 2nd]");
		initComponents(toCollect);

		pack();
		center();
	    setVisible(true);
	    
	    // Await user input
	    while(!done){
	    	Thread.yield();
		}
    }
	
	/** Initialise display components */
	private void initComponents(String[] toCollect){
		final JFrame thisFrame = this;
		JPanel mainPanel = new JPanel();
		final JButton doneButton = new JButton("OK");
		doneButton.setEnabled(false);
		for(String name : toCollect){
			JLabel l = new JLabel(fieldMappings.get(name) + ":");
			l.setPreferredSize(new Dimension(LABEL_WIDTH, FIELD_HEIGHT));
			mainPanel.add(l);
			JTextField f = new JTextField();
			f.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
			f.addKeyListener(new KeyAdapter(){
				public void keyReleased (KeyEvent e){
					boolean done = true;
					for(JTextField f_ : data.values()){
						if(f_.getText().trim().equals("")){
							done = false;
							break;
						}
					}
					doneButton.setEnabled(done);
				}
			});
			mainPanel.add(f);
			JLabel spacer = new JLabel();
			spacer.setPreferredSize(new Dimension(LABEL_WIDTH + FIELD_WIDTH + 20, 0));
			mainPanel.add(spacer);
			mainPanel.setPreferredSize(new Dimension(LABEL_WIDTH + FIELD_WIDTH + 20, toCollect.length * (FIELD_HEIGHT + 10) + 45));
			data.put(name, f);
		}
		//setLayout(new FlowLayout());
/*		JLabel matricNoLabel = new JLabel("Matriculation Number:");
		mainPanel.add(matricNoLabel);
*/		
/*		matricNo  = new JTextField();
		matricNo.addKeyListener(new KeyAdapter(){
			public void keyReleased (KeyEvent e){
				char c = e.getKeyChar();
				if(!(
				   c == '0'
				|| c == '1'
				|| c == '2'
				|| c == '3'
				|| c == '4'
				|| c == '5'
				|| c == '6'
				|| c == '7'
				|| c == '8'
				|| c == '9'
				))
					matricNo.setText(matricNo.getText().replaceAll(""+c, ""));
				if(matricNo.getText().length() > 7)
					matricNo.setText(matricNo.getText().substring(0, 7));
				doneButton.setEnabled(matricNo.getText().length() == 7);
			}
		});
		matricNo.setPreferredSize(new Dimension(SubjectDisplayParams.FIELD_WIDTH, matricNo.getPreferredSize().height));
		mainPanel.add(matricNo);
*/		
		doneButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
//				if(!matricNo.getText().equals("")){
					thisFrame.dispose();
					done = true;
//				}
//				else
//					setStatus("Please enter your matriculation number");
			}
		});
		mainPanel.add(doneButton);
		add(mainPanel);
	}
	
	public String getData(String field){
		return data.get(field).getText();
	}
		
}
