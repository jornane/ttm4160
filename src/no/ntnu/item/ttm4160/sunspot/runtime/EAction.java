package no.ntnu.item.ttm4160.sunspot.runtime;

public interface EAction {

	EAction DISCARD_EVENT = new EAction(){public String toString(){return "DISCARD_EVENT";}};
	EAction TERMINATE_SYSTEM = new EAction(){public String toString(){return "TERMINATE_SYSTEM";}};
	EAction EXECUTE_TRANSITION  = new EAction(){public String toString(){return "EXECUTE_TRANSITION";}};
	
}
