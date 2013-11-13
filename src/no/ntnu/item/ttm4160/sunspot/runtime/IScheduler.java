package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Vector;

public interface IScheduler extends Runnable {

	Vector getMachinesForString(String machine);

}
