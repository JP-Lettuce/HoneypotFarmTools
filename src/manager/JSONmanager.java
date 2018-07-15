/*
 * Class: JSONmanager.java
 * Author: Lukas Voetsch
 * Created: 10.06.2018
 * Last Change: 20.06.2018
 * 
 * Description: Manages the JSON Device file and stuff
 * */

package manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.snmp4j.smi.OID;

import objects.*;

public class JSONmanager {
	
	//Path where the json file is located
	private static String path = System.getProperty("user.dir") + "/" + "config/Devices.txt";
	
	@SuppressWarnings("unchecked")
	public static void write(ArrayList<Device> devices){
		
		//Loop over all devices out of the arraylist
		JSONObject jsonDevices = new JSONObject();
		
		for(Device device: devices) {
			JSONObject jsonDevice = new JSONObject();
			jsonDevice.put("name", device.getName());
			jsonDevice.put("type", device.getType());
			jsonDevice.put("fingerprint", device.getLastFingerprint());
			jsonDevice.put("fingerprintOID", device.getFingerprintOID().toString());
			
			//Loop over all custom OIDs
			JSONArray jsonOIDS = new JSONArray();
			ArrayList<OIDDescriptor> oidDesArray = device.getOIDDescriptor();
			if(oidDesArray != null) {
				for(OIDDescriptor oidDes: oidDesArray) {
					JSONObject jsonOID = new JSONObject();
					jsonOID.put("OID", oidDes.getOid().toString());
					jsonOID.put("descriptor", oidDes.getDescriptor());
					//Add OID to the json array
					jsonOIDS.add(jsonOID);
				}				
			}
			
			jsonDevice.put("OIDS", jsonOIDS);			
			jsonDevices.put(device.getipAddress(), jsonDevice);
		}
		
		File fileTest = new File(path);		
		//Write the build json to the selected file
		try (FileWriter file = new FileWriter(fileTest)) {
			file.write(jsonDevices.toJSONString());
			//System.out.println("\nJSON Object: " + jsonDevices);
		} catch (IOException e) {
			System.out.println("Write - IOException");
		}
	}
	
	//Create an ArrayList<device> out of the json file
	public ArrayList<Device> read(){
		
		ArrayList<Device> devices = new ArrayList<Device>();		
		JSONParser parser = new JSONParser();
		try {

			File fileTest = new File(path);	
			Object obj = parser.parse(new FileReader(fileTest));
			JSONObject jsonDevices = (JSONObject) obj;
			
			//String test = jsonDevices.toString();			
			//System.out.println(test);
			
			@SuppressWarnings("rawtypes")
			Iterator iterator = jsonDevices.keySet().iterator();
			
			while(iterator.hasNext()) {
				Device device = new Device();
				String ipAddress = (String)iterator.next();
				device.setipAddress(ipAddress);
				//System.out.println(ipAddress);
				
				JSONObject jsonDevice = (JSONObject) jsonDevices.get(ipAddress);
				String name = (String) jsonDevice.get("name");
				device.setName(name);
				
				String fingerprint = (String) jsonDevice.get("fingerprint");
				device.setLastFingerprint(fingerprint);
				
				String fingerprintOID = (String) jsonDevice.get("fingerprintOID");
				device.setFingerprintOID(new OID(fingerprintOID));
				
				long type = (long) jsonDevice.get("type");
				device.setType((int)type);
				
				JSONArray jsonOIDS = (JSONArray) jsonDevice.get("OIDS");
				
				//Loop over json Array for the OIDs
				ArrayList<OIDDescriptor> oidArray = new ArrayList<OIDDescriptor>();
				for(Object o: jsonOIDS) {;
					JSONObject jsonOID = (JSONObject) o;
					String oid = (String) jsonOID.get("OID");
					String descriptor = (String) jsonOID.get("descriptor");

					OIDDescriptor oidDes = new OIDDescriptor(descriptor, new OID(oid));
					oidArray.add(oidDes);
			
				}
				device.setOIDDescriptor(oidArray);
				devices.add(device);
			}
		}catch (FileNotFoundException e) {
	        System.out.println("Read - FileNotFound");
	    } catch (IOException e) {
	        System.out.println("Read - IOException");
	    } catch (ParseException e) {
	        System.out.println("Read - ParseException");
	    }
		return devices;
	}
}

/* Example for the json
 * {
 * 		"192.168.1.41": {
 * 			"fingerprintOID":"1.3.6.1.4.1.248.11.21.1.1.1.1.13.1.1",
 * 			"name":"RSP_Netzwerk1",
 * 			"fingerprint":"3DDF697F79D46BFB4409289AD23087F182DAABF6",
 * 			"type":2,
 * 			"OIDS":[
 * 				{"OID":"1.3.6.1.4.1.248.11.21.1.1.1.1.13.1.1","descriptor":"CpuUsage"},
 * 				{"OID":"1.3.6.1.4.1.248.11.22.1.8.10.2.0","descriptor":"CpuAverage"},
 * 				{"OID":"1.3.6.1.4.1.248.11.22.1.8.11.1.0","descriptor":"RAMAllocated"},
 * 				{"OID":"1.3.6.1.4.1.248.11.22.1.8.11.2.0","descriptor":"RAMFree"},
 * 				{"OID":"1.3.6.1.4.1.248.11.22.1.8.11.3.0","descriptor":"RAMAllocatedAverage"},
 * 				{"OID":"1.3.6.1.4.1.248.11.22.1.8.11.4.0","descriptor":"RAMFreeAverage"},
 * 				{"OID":"1.3.6.1.4.1.248.11.21.1.3.1.0","descriptor":"CustomOID"}
 * 			]
 * 		}
 * }*/
