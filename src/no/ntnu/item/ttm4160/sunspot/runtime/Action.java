package no.ntnu.item.ttm4160.sunspot.runtime;

public interface Action {

	Action DISCARD_EVENT = new Action(){public String toString(){return "DISCARD_EVENT";}};
	Action TERMINATE_SYSTEM = new Action(){public String toString(){return "TERMINATE_SYSTEM";}};
	Action EXECUTE_TRANSITION  = new Action(){public String toString(){return "EXECUTE_TRANSITION ";}};
	
}
