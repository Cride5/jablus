package uk.co.crider.jablus.models.absluc;

/*
	TODO:
	Test on windows, import jars properly
	Send email advert + fix date
	Collect all data including: (check with femke if that's ok)
		* Matric
		* Sex
		* Nationallity (considered sensitive data)
		  - instead go for first language + are they fluent in english.
		* Age
		* Year of Study
		* Area of Study  (drop down + other)
		* Highest qualification achieved
	Fillout all instructions (on-line and pre-experiment info)
	- Possibly explain to subjects that there is an underlying spatio-temporal model
	- Explain to subjects that for reason, i want what youre doing and why.
	Figure out and document reasoning for experiment setups chosen, poss add another experiments
	Create consent form, check legal stuff etc:
	   another experiment is here: http://www.homepages.ed.ac.uk/sdarlin1/vme/vptseq/vptseq.html
	Do pre-experiment tests
	Talk to Caroline (postgrad secretary) or lisa thornburn (finance secretary) about paying subjects
	
	Fill out ethics form
*/

import uk.co.crider.jablus.CloseListener;
import uk.co.crider.jablus.models.absluc.Constants;
import uk.co.crider.jablus.Simulation;
import uk.co.crider.jablus.SimulationManager;
import uk.co.crider.jablus.data.Data;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.store.DataSetIO;
import uk.co.crider.jablus.data.store.TupleData;
import uk.co.crider.jablus.gui.Popup;
import uk.co.crider.jablus.models.absluc.gui.DataCollectionInterface;
import uk.co.crider.jablus.utils.Utils;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

public class Run {

	private static String STARTING_PRACTICE =
		"You are about to start the short practice game, which lasts for 5 rounds.\n\n" +
		"Please note that the practice game is there to allow you to\n" +
		"familiarise yourself with the interface. Your answers will not be\n" +
		"recorded, and money rewarded will not be added to your bank total\n" +
		"so there is no need to worry about your performance.\n\n" +
		"Please refer to your instructions (url below) if you are unsure what to do.\n" +
		Constants.HELP_URL;
	private static String STARTING_EXPT =
		"OK, that's the practice game over.\n" +
		"If you have any questions please ask the supervisor now.\n\n" +
		"Would you like to continue to the paid experiment?\n" +
		"Click Yes to begin the paid experiment\n" +
		"or No to play another practice game.";
	private static String ENDING_EXPT =
		"Congratulations! You've reached the end of the experiment." +
		"\n\nYou raised a total revenue of £__REV__\n\n" +
		"Thank you kindly for taking part.";
	
	/** Starts a human experiment */
	public static void main(String[] args) {
		String[] toCollect = new String[]{
				"Name",
				"Matric",
				"Email",
				"Age",
				"Language",
				"StudyLevel",
				"StudyArea",
				"StudyYear"
		};
		DataCollectionInterface iface = new DataCollectionInterface(toCollect);
		matric = iface.getData("Matric");
		// Ensure experiment doesn't already exist
		if(new File(Constants.JABLUS_EXPT_DIR + File.separator + "SubjectData-" + matric + ".csv").exists()){
			Popup.error("An experiment with the matriculation number " + matric + " already exists");
			System.exit(1);
		}
		// Construct Table to store values
		io = new DataSetIO(new File(Constants.JABLUS_EXPT_DIR));
		subjectData = new TupleData(Constants.SUBJECT_DATA , io); //+ matric
		// Write all datavalues so far
		io.initialise();
		for(String itemName : toCollect){
			subjectData.put(itemName, iface.getData(itemName));
		}
		Popup.inform(STARTING_PRACTICE,
				"Starting practice game...");
		// Start practice, experiments will start from there
		doPractice();
	}
	
	/** Called when experiment is complete */
	private static void exptDone(){
		// Write total earned to table
		subjectData.put("Total Earned", ""+Utils.round(total, 2));
		subjectData.finalise();
		io.close();
		// Inform user that the experiment is complete
		Popup.inform(ENDING_EXPT.replaceAll("__REV__", Utils.roundString(total, 2)), "Experiment Complete");
	}
	
	// For recording data 
	private static DataSetIO io;
	private static TupleData subjectData;
	
	private static String matric;
	private static String[] experiments = Experiments.HUMAN_SET1;
	private static double total;
	private static double totalLast;
	private static int practices;
		
	private static void doPractice(){
		Map<String, Object> args = new Hashtable<String, Object>();
		args.put(""+Constants.BANK_TOTAL, 0.0);
		args.put(""+Constants.GAME_TITLE, "Practice Game");
		SimulationManager.startSimulation(
				0, //Experiments.HUMAN_PRACTICE,
				new File(Constants.JABLUS_EXPT_DIR + File.separator +
				Experiments.HUMAN_PRACTICE + Constants.SEPARATOR +
				matric + Constants.SEPARATOR + practices++),
				new CloseListener(){
					public void simluationClosed(Simulation sim){
System.out.println("close called");
						// Add up totals
						if(Popup.verify(STARTING_EXPT, "Another Practice Game?"))
							doExperiment(0);
						else
							doPractice();
					}
				},
				args);
	}

	private static void doExperiment(final int i){
//		System.out.println("Run: doing experiment " + i);
		if(i < 0 || i >= experiments.length) return;
//		String lastPerformance = "";
//		if(i > 0)
//		lastPerformance = "You raised a total of £" + Utils.roundString(totalLast/100, 2) + " in the last game.\n\n";
//		DecisionPopup.inform(lastPerformance + "Starting game " + (i + 1) + "\n Press OK to continue...");
//		System.out.println("Run: starting expt" + i +"... ");
		// TODO: tot up totals here
		Map<String, Object> args = new Hashtable<String, Object>();
		args.put(""+Constants.BANK_TOTAL, total);
		args.put(""+Constants.GAME_TITLE, "Game " + (i+1));
		SimulationManager.startSimulation(
				0, //experiments[i],
				new File(Constants.JABLUS_EXPT_DIR + File.separator +
				experiments[i] + Constants.SEPARATOR +
				matric),
				new CloseListener(){
					public void simluationClosed(Simulation sim){
						// Add up totals
						for(Data agent : ((DataSet)sim.getAgents()).getItems()){
							totalLast = ((Data0D.Double)((DataSet)agent).getItem(Constants.OUTPUT_REVENUE_GAME_TOTAL)).doubleValue();
						}
//System.out.println("Run: totalRound=" + totalRound);
						total += totalLast;
//System.out.println("Run: Simulation closed, starting experiment " + (i + 1));
						// Start next experiment when last is closed
						if(i < experiments.length - 1)
							doExperiment(i + 1);
						else
							exptDone();
							
					}
				},
				args);
	}

}
