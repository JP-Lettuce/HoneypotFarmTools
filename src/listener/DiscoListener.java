package listener;

import java.io.IOException;
import java.util.ArrayList;

import org.snmp4j.smi.OID;

import manager.SNMPManager;
import objects.Device;

/****
 * Class DiscoListener
 * @author Lukas Voetsch
 * Desc:
 * 	Allowes the user to visually mark all devices
 */
public class DiscoListener {
	
	private ArrayList<Device> devices;
	private OID discoOID = new OID(".1.3.6.1.4.1.248.11.20.1.4.3.0");
	
	/***
	 * public method start
	 * Desc:
	 * 	start the markup
	 */
	public void start() {
		for(Device d: devices) {
			SNMPManager client = new SNMPManager("udp:" + d.getipAddress() + "/161");
			try {
				client.start();
				//Get the party started :)
				client.set(this.discoOID, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}		
	}//eom start
	
	/***
	 * public method stop
	 * Desc:
	 * 	stop the markup
	 */
	public void stop() {
		for(Device d: devices) {
			SNMPManager client = new SNMPManager("udp:" + d.getipAddress() + "/161");
			try {
				client.start();
				//Get the party stopped :(
				client.set(this.discoOID, 2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}		
	}//eom stop
	
	/***
	 * Constructor DiscoListener
	 * @param devices
	 */
	public DiscoListener(ArrayList<Device> devices) {
		this.devices = devices;		
	}//eo Constructor
}//eoc
