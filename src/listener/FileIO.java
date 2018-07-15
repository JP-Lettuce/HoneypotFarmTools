package listener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import objects.Device;

/****
 * Class FIleIO
 * @author Lukas Voetsch
 * Desc:
 *  Contains Read and Wirte function for the Devicelist file
 */
public class FileIO {
	private static File file = new File("src/resources/Devices.txt");
	
	/***
	 * public method ReadConfig
	 * @return arrayList
	 * Desc:
	 *  Writes from file to the textfield
	 */
	public static ArrayList<Device> ReadConfig() {
		ArrayList<Device> arrayList = new ArrayList<Device>();		
		String line;
		BufferedReader br;
		    		    
		try {
			InputStream in = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(in));
            
			//br = new BufferedReader(new FileReader(path));	
			//Read text from file line for line
            //name, ip, lastUptime, userOIDs[]
			while((line = br.readLine()) != null) {
				String parts[] = line.split(";");
				Device device = new Device();
				device.setName(parts[0]);
				device.setipAddress(parts[1]);
				//device.setLastUptime(part[3]);
				//device.setUserOIDs(part[4]);
				arrayList.add(device);
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: ReadConfig()");
		} catch (IOException e) {
			System.out.println("IOException: ReadConfig()");
		} catch (NullPointerException e) {
			//Abbruch des OpenDialogs
			System.out.println("NullPointerException: ReadConfig()");
		}		
		return arrayList;
	}//eom ReadConfig
		
	/***
	 * public method WriteConfig
	 * @param devices
	 * Desc:
	 *  Writes the properties of a device into the config/device
	 */
	public static void WriteConfig(ArrayList<Device> devices) {
		BufferedWriter bw;
			
		try {
			bw = new BufferedWriter(new FileWriter(file));
			for(Device device: devices) {
				bw.write(device.getName() + ";");
				bw.write(device.getipAddress() + ";");
				//bw.write(device.getLastUptime() + ";"); ab hier anpassen
				//bw.write(device.getUserOIDs() + ";");		
			}//eo for				
				bw.close();
		} catch (IOException e) {
			System.out.println("IOException: WriteConfig()");
		}  catch (NullPointerException e) {
				//Abbruch des SaveDialogs
			System.out.println("NullPointerException: WriteConfig()");
		}//eo try
	}//eom WriteConfig
}//eoc
