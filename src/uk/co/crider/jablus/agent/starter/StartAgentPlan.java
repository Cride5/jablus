package uk.co.crider.jablus.agent.starter;

import jadex.planlib.StartAgentInfo;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.SwingUtilities;


/** Starts the decision making agents */
public class StartAgentPlan extends Plan {
	
	/** Unique class ID */
    private static final long serialVersionUID = -4733777892367849618L;
    private static int instanceCount = 0; // Used to ensure no agents have the same name
    private String agentName;
    private int agentId;
    private String agentSchemaFile;
    private String agentDataFile;
    private int simId;
    
    public StartAgentPlan(){
    	this.agentName       =  (String) getBeliefbase().getBelief("agent_name"       ).getFact();
    	this.agentId         = ((Integer)getBeliefbase().getBelief("agent_id"         ).getFact()).intValue();
		this.agentSchemaFile =  (String) getBeliefbase().getBelief("agent_schema_file").getFact();
		this.agentDataFile   =  (String) getBeliefbase().getBelief("agent_data_file"  ).getFact();
		this.simId           = ((Integer)getBeliefbase().getBelief("sim_id"           ).getFact()).intValue();
    }
    
	/** Plan content */
	public void body() {
//System.out.println("StartAgentPlan");
		
		// Get sim_id from message
/*	    IMessageEvent msg = (IMessageEvent)getInitialEvent();
	    String content = (String)msg.getContent();
	    int simId = -1;
	    try{
	    	simId = Integer.parseInt(content.substring(content.length() -1));
	    }
	    catch(NumberFormatException e){
	    	e.printStackTrace();
	    }
*/
		// Set up arguements
		Hashtable<String, Object> args = new Hashtable<String, Object>();
		args.put("name",      agentName);
		args.put("id",        agentId);
		args.put("data_file", agentDataFile);
		args.put("sim_id",    simId);

		SwingUtilities.invokeLater(new AgentStarter(
				agentName + "." + instanceCount++,
				agentSchemaFile,
				args
		));

				
/*		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				IGoal await = getExternalAccess().getGoalbase().createGoal("start_goal");
				getExternalAccess().getGoalbase().dispatchTopLevelGoal(await);
			}
		});
*/	}
	
	class AgentStarter implements Runnable{
		
		private String name;
		private String file;
		private Map args;
		
		AgentStarter(String name, String file, Map args){
			this.name = name;
			this.file = file;
			this.args = args;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
//System.out.println("StartAgentPlan: Starting agent with name=" + name + ", file=" + file + ", args=" + args);
			StartAgentInfo info = new StartAgentInfo(file, name, 0, args);
			IGoal start = getExternalAccess().getGoalbase().createGoal("start_agents");
			start.getParameterSet("agentinfos").addValue(info);
			getExternalAccess().getGoalbase().dispatchTopLevelGoal(start);
			
		}
	}

}
