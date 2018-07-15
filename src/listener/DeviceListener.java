package listener;

import watcher.Controll;
import manager.ConfigManager;
import objects.Device;
/****
 * Class DeviceListener
 * @author Lukas Voetsch
 * Desc:
 * 	
 */
public class DeviceListener {
	private Device device;
	private Controll ctrl;
	private ConfigManager cm;
	
	/***
	 * public method getConfig
	 * Desc:
	 *  Load the Config from device via SFTP-server to the client
	 */
	public void getConfig(){
		String ip = device.getipAddress();
		String name = device.getName();
		int type = device.getType();
		
		cm.getConfig(name, ip, type);
		cm.saveConf(3, name);
	}//eom getConfig
	
	
	/***
	 * public method setConfig
	 * Desc:
	 *  Get config from sftp to device to reset the device
	 */
	 public void setConfig() {
		String ip = device.getipAddress();
		String name = device.getName();
		int typ = device.getType();
		
		cm.setConfig(ip, name, typ);
	}//eom setConfig
	
	 /***
	  * Constructor DeviceListener
	  * @param device
	  */
	public DeviceListener(Device device) {
		this.device = device;
		ctrl = new Controll(false);
		cm = ctrl.getConfigManager();
	}//eo contructor
}//eoc
