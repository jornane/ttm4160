package no.ntnu.item.ttm4160.example;

import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayer;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.StateMachine;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.util.IEEEAddress;

public abstract class CommunicatingStateMachine extends StateMachine {

    private ICommunicationLayer communications;

	public CommunicatingStateMachine(ICommunicationLayer communications) {
		this.communications = communications;
	}

	/**
	 * Get the MAC address of the current hardware
	 * @return	the MAC address
	 */
	public static String getMacAddress() {
		return new IEEEAddress(Spot.getInstance().getRadioPolicyManager().getIEEEAddress()).asDottedHex();
	}
	
	public void sendRemoteMessage(Message msg) {
		communications.sendRemoteMessage(msg);
	}

	public void sendRemoteMessage(String receiver, String content) {
		sendRemoteMessage(new Message(
				getMacAddress()+":"+hashCode(), 
				receiver, 
				content
			));
	}

}
