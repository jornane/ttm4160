package no.ntnu.item.ttm4160.example;

import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayer;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.StateMachine;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.util.IEEEAddress;

public abstract class CommunicatingStateMachine extends StateMachine {

    private ICommunicationLayer communications;

    /**
     * Construct a new state machine which can send and receive messages.
     * @param communications
     */
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
	
	/**
	 * Send a message object to remote. 
	 * This object will not reach any local state machines.
	 * @param msg	the message to send
	 */
	protected void sendRemoteMessage(Message msg) {
		communications.sendRemoteMessage(msg);
	}

	/**
	 * Send a message to a remote state machine.
	 * This message will not reach any local state machines.
	 * @param receiver	the name of the receiving state machine <Mac address>:<statemachine ID>
	 * @param content	the payload (content) of the message, see {@link Message#getContent()}
	 */
	protected void sendRemoteMessage(String receiver, String content) {
		sendRemoteMessage(new Message(
				getMacAddress()+":"+hashCode(), 
				receiver, 
				content
			));
	}

}
