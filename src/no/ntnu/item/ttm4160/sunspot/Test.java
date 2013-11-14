package no.ntnu.item.ttm4160.sunspot;

import no.ntnu.item.ttm4160.example.Receiver;
import no.ntnu.item.ttm4160.example.Transmitter;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;

public class Test {

	public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(1, .1);
        scheduler.addMachine(new Transmitter());
        scheduler.addMachine(new Receiver());
        scheduler.run();
	}

}
