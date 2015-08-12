package uk.co.crider.jablus.agent;

import uk.co.crider.jablus.data.Data;

public class ScheduledAction extends Action {

	private int time;
	
	public ScheduledAction(int id, int time) {
		super(id);
		this.time = time;
	}

	public ScheduledAction(int id, int time, Data arg) {
		super(id, arg);
		this.time = time;
	}

	public ScheduledAction(int id, int time, Data[] args) {
		super(id, args);
		this.time = time;
	}

}
