/*
 * Class: Device.java
 * Author: Lukas Voetsch
 * Created: 03.05.2018
 * Last Change: 03.05.2018
 * 
 * Description: Main object of the application
 * 
 * Parameter: 	String name --> name of the device got via SNMP
 * 				String ipAddress --> IP of the device got via AddDeviceGUI
 * 				int type --> preselected type got via AddDeviceGUI
 * 				String lastFingerprint --> Hash for the configuration to compare if anything got changed
 * 				int currentStatus --> Status of the device 1->ok 2->changed 3->error
 * 				OID nameOID --> OID to get the name of the device via SNMP
 * 				OID statusOID --> OID to get the status of the device via SNMP
 * 				OID fingerprintOID --> OID to get the status for the device via SNMP
 * 				ArrayList<OIDDescriptor> --> ArrayList filled with OIDs that refer to interesting
 * 				properties of a device
 * */
package objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.snmp4j.smi.OID;

import manager.SNMPManager;

public class Device{
	
	private String name; //get via snmp
	private String ipAddress;
	private int type; 
	//non-bat = 1
	//bat = 2
	private String lastFingerprint;
	
	private int currentStatus;
	
	private OID nameOID = new OID(".1.3.6.1.2.1.1.5.0");
	private OID statusOID = new OID(".1.3.6.1.4.1.248.11.21.1.3.1.0");
	private OID fingerprintOID;
	
	//private network cpu
	private ArrayList<OIDDescriptor> deviceOIDs;
	
	//private Date lastUptime; --> einbinden
	private Date lastUptime;
	
	//Checks the status and the fingerprint
	//Uses the SNMPManager out of the package snmp
	public void checkAvailibilty(){
		int i;
		SNMPManager client = new SNMPManager("udp:" + this.ipAddress + "/161");
		
		//try to open snmp-connection
		try {
			client.start();

			String buffer = client.getAsString(this.statusOID);
			//System.out.println("Buffer" + buffer);				
			
			//check if the return value is not Null --> BAT-Devices don't support the statusOID
			if(!(buffer.equals("Null"))) {
				i = Integer.parseInt(buffer);
				this.currentStatus = i;
				//System.out.println("*0*");
			} else {
				this.currentStatus = 1; //BAT workaround --> set 1 for green
			}
				
			//fingerprint != lastFingerprint
			buffer = client.getAsString(this.fingerprintOID);
			if(!(buffer.equals(this.lastFingerprint)) | buffer.equals("Null")) {
				this.currentStatus = 2; // i = 2 --> yellow
			}
		} catch (IOException | NullPointerException | NumberFormatException e) {
			//Can't reach device --> status 3 --> red
			this.currentStatus = 3;
			//System.out.println("*1*");
		}
	}
	
	//Returns an overview over different status information
	//Return Type String
	//message als Übergabe Parameter
	public String getStatusInformation(){
		String status = "";
		
		SNMPManager client = new SNMPManager("udp:" + this.ipAddress + "/161");
		try {
			client.start();
			
			String buffer = client.getAsString(this.nameOID);
			status += "Name: " + buffer + "\n";
			
			status += "IP-Address: " + this.ipAddress + "\n";
			
			buffer = client.getAsString(this.fingerprintOID);
			status += "Fingerprint: " + buffer + "\n\n";
			
			buffer = client.getAsString(this.statusOID);
			if(!(buffer.equals("Null"))) //in case you get a bat
				status += "Status: " + buffer + "\n\n";
			
			ArrayList<OIDDescriptor> oidDes = this.getOIDDescriptor();
			if(oidDes != null) {
				for(OIDDescriptor oid: oidDes) {
					String descriptor = oid.getDescriptor();
					String oidString = client.getAsString(oid.getOid());
					status += descriptor +": "+ oidString + "\n";
					System.out.println(descriptor);
				}
			}
			else {
				System.out.println("um");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			status = "Failed to get status";
		} catch (java.lang.NullPointerException e) {
			status = "Can't reach device";
		}
		return status;	
	}
	
	//Add preselected OIDs for the non-bat Hirschmann devices
	public void nonBat() {
		System.out.println("Create Non-BAT");
		this.deviceOIDs = new ArrayList<OIDDescriptor>();
		
		//Set the fringerprintOID for the non-BAT
		this.fingerprintOID = new OID(".1.3.6.1.4.1.248.11.21.1.1.1.1.13.1.1");
	}
	
	//Add preselected OIDs for the device Hirschmann BAT
	public void bat() {
		System.out.println("Create BAT");
		this.deviceOIDs = new ArrayList<OIDDescriptor>();
		
		//Set fingerprintOID for BAT
		this.fingerprintOID = new OID("1.3.6.1.4.1.248.32.18.1.11.21.0");		
	}
	
	//Getter/Setter--------------------------------------------------------------------------------	
	public Device() {
		
	}
	
	public ArrayList<OIDDescriptor> getOIDDescriptor(){
		return deviceOIDs;		
	}
	
	public int getType() {
		return type;
	}
	
	public int getCurrentStatus() {
		return currentStatus;
	}
	
	public String getLastFingerprint() {
		return lastFingerprint;
	}

	public String getName() {
		return name;
	}

	public String getipAddress() {
		return ipAddress;
	}

	public OID getNameOID() {
		return nameOID;
	}

	public OID getStatusOID() {
		return statusOID;
	}

	public OID getFingerprintOID() {
		return fingerprintOID;
	}

	public Date getLastUptime() {
		return lastUptime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setipAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setLastUptime(Date lastUptime) {
		this.lastUptime = lastUptime;
	}
	
	public void setLastFingerprint(String lastFingerprint) {
		this.lastFingerprint = lastFingerprint;
	}
	
	public void setType (int type) {
		this.type = type;
	}
	
	public void setFingerprintOID(OID fingerprintOID) {
		this.fingerprintOID = fingerprintOID;
	}
	
	public void setOIDDescriptor(ArrayList<OIDDescriptor> deviceOIDs){
		this.deviceOIDs = deviceOIDs;		
	}
}
