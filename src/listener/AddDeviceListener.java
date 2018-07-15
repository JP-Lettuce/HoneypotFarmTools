/*
 * Class: AddDeviceListener.java
 * Author: Lukas Voetsch
 * Created: 12.05.2018
 * Last Change: 20.05.2018
 * 
 * Description: Listener called in the AddDeviceGUI
 * */

package listener;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.snmp4j.smi.OID;

import manager.SNMPManager;
import objects.*;


public class AddDeviceListener {
	private Device newDevice;	
	
	/***
	 * public method createDevice
	 * @param ipSpinner
	 * @param listOID
	 * @param devices
	 * @param type
	 * @return Device device
	 * @throws IOException
	 * @throws NullPointerException
	 * Desc: 
	 * 	Creates a new object of the type device using Device.java
	 */
	public Device createDevice(Spinner[] ipSpinner, List listOID, ArrayList<Device> devices, int type) throws IOException, NullPointerException{
		String ipAddress = "";
		String name;
		String fingerprint;
		
		newDevice = new Device();
		
		newDevice.setType(type);
		switch(type) {
			case 1:
				newDevice.nonBat();
				break;
			case 2: 
				newDevice.bat();
				break;
			default:
				break;
		}//eo switch(type)
		
		for(int i=0; i<4; i++) {
			ipAddress+= ipSpinner[i].getText();
			if(i < 3) {
				ipAddress += ".";
			}
		}//eo for
		
		newDevice.setipAddress(ipAddress);
		
		//Get infos via SNMP
		SNMPManager client = new SNMPManager("udp:" + ipAddress + "/161");
		client.start();
		
		name = client.getAsString(new OID(newDevice.getNameOID()));
		newDevice.setName(name);
		
		fingerprint = client.getAsString(new OID(newDevice.getFingerprintOID()));
		newDevice.setLastFingerprint(fingerprint);
		
		return newDevice;
	}//eom createDevice
	
	/***
	 * Constructor AddDeviceListener
	 */
	public AddDeviceListener() {
	}//eo Constructor
}//eoc
