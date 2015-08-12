package uk.co.crider.jablus.gui.data;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DataTX;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Scroller for text data - used in a TextView */
public class TextScroller extends JScrollPane {

	/** Unique class ID */
    private static final long serialVersionUID = 7439024141064320887L;

    private JTextArea text;
    private DataTX data;
    private Vector<String> pastData;
    private Map<Integer, Integer> timeMapping;
	
	public TextScroller(DataTX data){ this(data, null, null); }
	public TextScroller(DataTX data, List<Data> pastValues, List<Data> time){
		this.data = data;
		this.pastData = new Vector<String>();
		this.timeMapping = new Hashtable<Integer, Integer>();
		initComponents();
		if(pastValues != null && time != null){
			Iterator iData = pastValues.iterator();
			Iterator iTime = time.iterator();
			while(iData.hasNext() && iTime.hasNext()){
				DataTX s = (DataTX)iData.next();
				pastData.add(s.getText());
				timeMapping.put(((Data0D.Integer)iTime.next()).intValue(), pastData.size() -1);
				redisplay(s);
			}
			redisplay();
		}
	}
	
	/** Initialise graphical components */
	private void initComponents(){
	    text = new JTextArea(){
	    	/** Unique class ID */
            private static final long serialVersionUID = 5192673864179846461L;
			public void append(String str) {
				super.append(str);
				setCaretPosition(getText().length() - 1);
			}
			public void setText(String str) {
				super.setText(str);
				int len = getText().length() - 1;
				if(len >= 0)
					setCaretPosition(len);
			}
	    };
	    setViewportView(text);
	}
	
	public void scrollTo(int time) {
	    // Find start and end pos
		Integer i = timeMapping.get(time);
		if(i == null || i >= pastData.size()) return;
		int startPos = 0;
		for(int j = 0; j < i; j++)
			startPos += pastData.get(j).length() + 1;
		text.requestFocus();
		text.setSelectionStart(startPos);
		text.setSelectionEnd(startPos + pastData.get(i).length());
    }
	
	/**  Update the text area with new data */
	public void redisplay(){ redisplay(data); }
	public void redisplay(DataTX data){
		String msg = data.getText();
		if(msg != null && !msg.equals(""))
			text.append(msg + "\n");
	}

	/** For testing */
	public static void main(String[] args) {
	}

}
