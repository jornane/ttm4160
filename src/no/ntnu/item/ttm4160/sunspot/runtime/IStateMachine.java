package no.ntnu.item.ttm4160.sunspot.runtime;


public interface IStateMachine {


    public EAction fire(Event event, Scheduler scheduler);
    public boolean answersTo(String id);
}
