package manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import watcher.Controll;
import flags.Flags;

/****
 * Class ConfigManager
 * @author JP
 * @version 1.0
 * Desc:
 * 	The ConfigManager manages all interaktion with the configfile
 * 	and the down/upload from sources.
 */ 
public class ConfigManager {
	
	private Controll controll;				//Controll to contorll workeflow interaction
    private String pathBase; 				//Basic path for Files on system
    private String pathBaseSFTP = "/config/";
    
    //Global Variables throu Options
    private String sftpHost;						//IP Adress of sftp-server
    private String sftpUser;						//User of sftp-server
    private String sftpPassword;					//Password for sftp-user
    private String sshUser;							//User for ssh connetions
    private String sshPassword;						//Password for ssh User
	
	public ConfigManager(Controll pcontroll) {
		this.controll = pcontroll;
		
			this.pathBase = System.getProperty("user.dir");
			this.pathBase += "/config/";
		
		this.setOptionsDefault();
	}// eo ConfigManager()
	
	/***
	 * public method setOptions
	 * @param pSftpHost
	 * @param pSftpUser
	 * @param pSftpPassword
	 * @param pSshUser
	 * @param pSshPassword
	 * Desc:
	 * 	Set Options for custom usage
	 */
	public void setOption(String pSftpHost, String pSftpUser, String pSftpPassword, String pSshUser, String pSshPassword) {
		this.sftpHost = pSftpHost;
		this.sftpUser = pSftpUser;
		this.sftpPassword = pSftpPassword;
		this.sshUser = pSshUser;
		this.sshPassword = pSshPassword;
	}
	
	/***
	 * public method setOptionsDefault
	 * Desc:
	 * 	set Options as deployed
	 */
	public void setOptionsDefault() {
		this.sftpHost = "192.168.200.200";
		this.sftpUser = "user";
		this.sftpPassword = "12345678";
		this.sshUser = "";
		this.sshPassword = "private";
	}
	
	/***
	 * private method configcheck
	 * @return int status
	 * Desc:
	 * 	Checks the current config witch the default config
	 */
	public int configcheck(String name, String ip, int type){
	    
	    this.controll.toPrint(" --- Kontrolliere die Konfiguration von " + name);
	    int status = 404;					//set status to malfunction
	    boolean ident;						//create an check boolean for the not alike scenario
	    
	    
	    File c = new File(pathBase + "default/" + name + ".xml");	//locate the default config	
	    String ac = "";				//string with current config line
	    String comp = "";			//string with to compare config line
	    String diff = "";			//string with the collection of lines witch differ
	    int line = 1;				//number of current line

	    
	    if(!c.exists()) { 			//check for the default config
	    	
	    	this.controll.toPrint(" ---- Geraete-Default ist unbekannt.");
	    	this.controll.toPrint(" ---- Bearbeitung der Konfiguration wird uebersprungen.");
	    	status = 5;
	    	
	    }else {		//default config found
	    	
		    this.getConfig(name, ip, type);	//get the current config
	    	
	    	try {
	    		this.controll.toPrint(" --- Suche unterschiede");
	    		//Default Config:
	            FileReader fileReaderdef = new FileReader(pathBase + "default\\" + name + ".xml");	// FileReader reads text files in the default encoding.
	            BufferedReader bufferedReaderdef = new BufferedReader(fileReaderdef);						// Always wrap FileReader in BufferedReader.

	            //new Config:
	            FileReader fileReader = new FileReader(pathBase + "acConf.xml");	 // FileReader reads text files in the default encoding.
	            BufferedReader bufferedReader = new BufferedReader(fileReader);		// Wrapping FileReader in BufferedReader.
	            
	            ident = true;	//set comparing to identical
	            
	            while((ac = bufferedReader.readLine()) != null){	//check for each line in the actual config
	               comp = bufferedReaderdef.readLine();				//read the corresponding line in the default config
	               
	               if(!comp.equals(ac)) {			//do if line differ
	            	   ident = false;				//update the identical status
	            		diff += "" + line + "\n";  	//log the line witch differ
	               }//eo if, comparing
	               
	               line++;	 						//increment linecounter
	            }//eo while, file comparing  
	            
	            if(!ident){	// doif files differ
	            	status = 6;
			        this.controll.toPrint(" ---> Die Konfiguration weicht vom Default ab.");
			        this.controll.toPush(2,"Abweichende zeilen: \n" + diff, name, ip);
			        this.saveConf(2, name);	//call funktion to save the altered config
			    }
			    else{
			        this.controll.toPrint(" ---> Die konfiguration weicht nicht vom Default ab.");
			        status = 5;
			    }//eo if-else, check ident
	            
	            bufferedReaderdef.close();			//close default file
	            bufferedReader.close();         	//close new file
	        }
	        catch(FileNotFoundException | NullPointerException ex) {
	        	this.controll.toErrorPrint("!---- FILE-FEHLER: " + ex );
	        }
	        catch(IOException ex) {
	        	this.controll.toErrorPrint("!---- FILE-FEHLER: " + ex);
	        	//thats the sound of the police~~
	        }//eo try
	    	   
	    }//eo if-else
	    
	    return status;
	}//eom configcheck
	
	/***
	 * public method pullConfDown
	 * @param ip
	 * @param name
	 * Desc:
	 * 	Connect to the given Device and use a unique command 
	 *  to pull the Config to the sftp-server
	 */
	public void pullConfDown(String ip, String name) {
	    String sshuser = this.sshUser;   			//username of the target device
	    String sshhost = ip;					//ip of the target device
	    String sshpassword = this.sshPassword;		//pw for the user of target device
		
	    String sftpuser = this.sftpUser + ":" + this.sftpPassword;
	    String sftpIP = this.sftpHost;
	    
	    //Build the shellcommand for the Devce to execute
	    String getCommand = "copy config nvm remote sftp://"+ sftpuser + "@" + sftpIP + pathBaseSFTP + name;
	    String command = "enable\n" + getCommand + " \nexit";
	    
	    this.controll.toPrint(" ----- Pull Konfiguration zu SFTP-server");
	    
		JSch jsch = new JSch();   //create new Java Secure Channel
	    Session session;     	 //create new Session
	    
	    try {
		      session = jsch.getSession(sshuser, sshhost);        	//connect to
		      session.setPassword(sshpassword);            			//authenticate
		      session.setConfig("StrictHostKeyChecking", "no");		//ignore KnownHost check by ssh
		      session.connect();
		      
		      this.controll.toPrint(" -----> Tunnel erstellt");
		      this.controll.toPrint(" ----- Stosse Pull an");
		      
		      Channel channel = session.openChannel("shell");						//open a remote shell
		      PrintStream shellStream = new PrintStream(channel.getOutputStream());	//enable a printStream for the shell
		      channel.connect();				//connect the remoteshell

		      	
		      	shellStream.println(command);	//wirte the command to r-shell
		      	shellStream.flush();			//execute the command
		      	
		      	//wait for execution
		      	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      	
		      	shellStream.close();		//close the shell
				channel.disconnect();		//close shell channel
				session.disconnect();		//close shh
		   
		      this.controll.toPrint(" -----> Pull beendet");
		      
		    } catch (JSchException e) {
		      this.controll.toErrorPrint("!---- SSH-FEHLER: "+e);
		      
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//eo try
	    this.controll.makeGap();	//print a blanc line
		
	}//eom pullConfDown
	
	/***
	 * private method pushConfUp
	 * @param ip
	 * @param name
	 * Desc:
	 * 	Tell the Device to reload his Config from the sftp-server
	 */
	private void pushConfUp(String ip, String name) {
		
		String sshuser = this.sshUser;   			//username of the target device
		String sshhost = ip;						//ip of the target device
		String sshpassword = this.sshPassword;		//pw for the user of target device
			
		String sftpuser = this.sftpUser + ":" + this.sftpPassword;
		String sftpIP = this.sftpHost;
		    
		//Build the shellcommand
		String getCommand = "copy config remote sftp://"+ sftpuser + "@" + sftpIP + pathBaseSFTP + name;
		String command = "enable\n" + getCommand + " \nexit";
		    
		this.controll.toPrint(" ----- Pushe Konfiguration zum Device");
		    
		JSch jsch = new JSch();   //create new Java Secure Channel
		Session session;      //create new Session
		    
		try {
			session = jsch.getSession(sshuser, sshhost);        	//connect to
			session.setPassword(sshpassword);            			//authenticate
			session.setConfig("StrictHostKeyChecking", "no");		//ignore KnownHost check by ssh
			session.connect();
			      
			this.controll.toPrint(" -----> Tunnel erstellt");
			this.controll.toPrint(" ----- Stosse Konfig-Push an");
			      
			Channel channel = session.openChannel("shell");						//open a remote shell
			PrintStream shellStream = new PrintStream(channel.getOutputStream());	//enable a printStream for the shell
			channel.connect();				//connect the remoteshell
      	
			shellStream.println(command);	//wirte the command to r-shell
			shellStream.flush();			//execute the command
			      	
			//wait for execution
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			      	
			shellStream.close();		//close the shell
			channel.disconnect();		//close shell channel
			session.disconnect();		//close shh
			   
			this.controll.toPrint(" -----> Konfig-Push beendet");
			      
		} catch (JSchException e) {
			this.controll.toErrorPrint("!---- SSH-FEHLER: "+e);
			      
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   this.controll.makeGap();	//print a blanc line	
	}//eom pushConfUp
	
	/***
	 * public method checkDefault
	 * @param name
	 * @param ip
	 * Desc:
	 * 	Checks if there is a Config default 
	 *  and loads the current config as default if not
	 */
	public void checkDeafult(String name, String ip, int type) {
		this.controll.toPrint(" ---- Ueberpruefe ob das Geraet bekannt ist");
		 File c = new File(pathBase + "default/" + name + ".xml");	//locate the default config					
		 //check if a default config file exists
		 if(!c.exists()) { 			
			 this.controll.toPrint(" ----> Geraet ist noch unbekannt.");
		   this.getConfig(name, ip, type);	//load the config from source
		   this.saveConf(1, name);		//save config as default
		 }else {
			 this.controll.toPrint(" ----> Geraet ist bekannt.");
		 }//eo if-else	 
	}//eom checkDefault
	
	/***
	 * public method getConfig
	 * @param name
	 * @param ip
	 * Desc:
	 * 	checks witch type of Config needs to be worked on
	 * 	initialises the work to download the Config
	 */
	public void getConfig(String name, String ip, int type) {
		switch(type) {
			case 1 : this.controll.toPrint(" -----> Geraet wurde als non-Bat erkannt");
					this.getConfigNormal(name, ip);
					break;
			case 2: this.controll.toPrint(" -----> Geraet wurde als Bat erkannt");
					this.getConfigBat(name, ip);
					break;
			default: this.controll.toErrorPrint("!-----> Geraetetyp " + type + " wurde nicht erkannt.");
					
		}
	}//eom getConfig
	
	/***
	 * public method getConfigBat
	 * @param name
	 * @param ip
	 * Desc:
	 * 	loads the config from the BAT-shell and
	 *  writes it direct into a local file (BETA)
	 */
	public void getConfigBat(String name, String ip) {
		String sshuser = this.sshUser;   		//username of the target device
	    String sshhost = ip;					//ip of the target device
	    String sshpassword = this.sshPassword;	//pw for the user of target device
	    
	    //Build the shellcommand
	    String command = "readconfig \nexittoken";
	    
	    this.controll.toPrint(" ----- Pulle Konfiguration von BAT-Geraet");
	    
		JSch jsch = new JSch();   //create new Java Secure Channel
	    Session session;     	 //create new Session
	    
	    try {
		      session = jsch.getSession(sshuser, sshhost);      //connect to
		      session.setPassword(sshpassword);            		//authenticate
		      session.setConfig("StrictHostKeyChecking", "no");	//ignore KnownHost check by ssh
		      session.connect();
		      
		      this.controll.toPrint(" -----> Tunnel erstellt");
		      this.controll.toPrint(" ----- Lade Konfig von der Konsole");
		      
		      Channel channel = session.openChannel("shell");						//open a remote shell
		      PrintStream shellStream = new PrintStream(channel.getOutputStream());	//enable a printStream for the shell
		      channel.connect();				//connect the remoteshell

		      	InputStream response = channel.getInputStream();
		      	shellStream.println(command);	//wirte the command to r-shell
		      	shellStream.flush();			//execute the command
		      	
		        //build the outputstream to fetch the consolereturn directly into a file
				FileOutputStream outputStream = new FileOutputStream(new File(this.pathBase + "acConf.xml"));
				
				int read;							//length of line to read
				byte[] bytes = new byte[1024];		//buffer of line to read
				boolean work = true;				//still load data from repsonse?
				String checkstring;					//exittoken catcher

				//write as long as a line is filled
				while (work) {
					//read the response if its not emty
					if((read = response.read(bytes, 0 , bytes.length)) > 0){
						checkstring = new String(bytes);
						outputStream.write(bytes, 0, read);
						//check if exittoken is/was displayed
						if(checkstring.contains("exittoken")) {
							work = false;	//aboard the read in
						}
					}
				}
		      	
		      	outputStream.close();		//close the outputstream / file
		      	response.close();			//clsoe the response fetcher
		      	shellStream.close();		//close the shell
				channel.disconnect();		//close shell channel
				session.disconnect();		//close shh

		   
		      this.controll.toPrint(" -----> Laden beendet");
		      Flags.getConfigFlag = true;
		      
		    } catch (JSchException | IOException e) {
		      this.controll.toErrorPrint("!---- SSH-FEHLER: "+e);
		      Flags.getConfigFlag = false;
		    }//eo try
	    this.controll.makeGap();	//print a blanc line	
	}//eom getConfigBat
	
	/***
	 * public method getConfigNormal
	 * Desc:
	 *  Loads the current config of the given Mashine via SFTP
	 */
	public void getConfigNormal(String name, String ip) {
		String sourcefile = pathBaseSFTP + name;			//name of the file to get
	    String sftpuser = this.sftpUser;   					//username of the target device
	    String sftphost = this.sftpHost;					//ip of the target device
	    String sftppassword = this.sftpPassword;			//pw for the user of sftp server
	    
	    this.pullConfDown(ip, name);						//pull config to sftp-server
	    
	    this.controll.toPrint(" ---- Lade Config herrunter");
	    this.controll.toPrint(" ----- Erstelle SSH Tunnel");
	    
	    JSch jsch = new JSch();  	//create new Java Secure Channel
	    Session session;      		//create new Session
	    
	    try {
	    	session = jsch.getSession(sftpuser, sftphost);      //connect to target
	    	session.setPassword(sftppassword);            		//authenticate
	    	session.setConfig("StrictHostKeyChecking", "no");	//ignore KnownHost check by ssh
	    	session.connect();									//open ssh session
		      
		    this.controll.toPrint(" -----> Tunnel erstellt");
		    this.controll.toPrint(" ----- Erstelle SFTP Tunnel");
		      
		    //create sfpt channel and connect
		    ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp"); 
		    sftpChannel.connect();      						//open sftp channel to server         		  		
		    
	    	this.controll.toPrint(" -----> Tunnel erstellt");
	    	this.controll.toPrint(" ----- Lade Konfiguration");
		    
	    	sftpChannel.get(sourcefile, this.pathBase + "acConf.xml");	//pull file 
	    	this.controll.toPrint(" -----> Herrunterladen beendet");
		    
	    	sftpChannel.disconnect();				//close sftp channel
	    	session.disconnect();					//close ssh session
	
	        Flags.getConfigFlag = true;
	    } catch (JSchException | SftpException e) {
			this.controll.toErrorPrint("!---- SSH-FEHLER: "+ e);
			Flags.getConfigFlag = false;
		}//eo try
	}//eom getConfig
	
	/***
	 * public method setConfig
	 * @param ip
	 * @param name
	 */
	public void setConfig(String ip, String name, int type) {
		String destfile = pathBaseSFTP + name;			//name of the file to get
	    String sftpuser = this.sftpUser;   				//username of the target device
	    String sftphost = this.sftpHost;				//ip of the target device
	    String sftppassword = this.sftpPassword;		//pw for the user of sftp server
	    String sourcefile = this.pathBase + "default/" + name + ".xml";
	    
	    //Load up if the device is NOT a BAT
	    if(type == 1) {   
	    File s = new File(sourcefile);
	    if(s.exists()) {
		    this.controll.toPrint(" ---- Lade Konfiguration hoch");
		    this.controll.toPrint(" ----- Erstelle SSH Tunnel");
		    
		    JSch jsch = new JSch();  	//create new Java Secure Channel
		    Session session;      		//create new Session
		    
		    try {
			      session = jsch.getSession(sftpuser, sftphost);        //connect to target
			      session.setPassword(sftppassword);            		//authenticate
			      session.setConfig("StrictHostKeyChecking", "no");		//ignore KnownHost check by ssh
			      session.connect();									//open ssh session
			      
			      this.controll.toPrint(" -----> Tunnel erstellt");
			      this.controll.toPrint(" ----- Erstelle SFTP Tunnel");
			      
			      //create sfpt channel and connect
			      ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp"); 
			      sftpChannel.connect();      							//open sftp channel          		  		
			      
			      this.controll.toPrint(" -----> Tunnel erstellt");
			      this.controll.toPrint(" ----- Konfiguration wird hochgeladen");
			      
			      sftpChannel.put(sourcefile, destfile); //put the Config on sftp-server
			      this.controll.toPrint(" -----> Hochladen beendet");
			      
			      sftpChannel.disconnect();		//close the sftp channel
			      session.disconnect();			//close the ssh connection
			      
			      this.pushConfUp(ip, name);	//tell the Device to get the Config
			      Flags.setConfigFlag = true; 
			    } catch (JSchException | SftpException e) {
			      this.controll.toErrorPrint("!---- SSH-FEHLER: "+e);
			      Flags.setConfigFlag = false;
			    }//eo try
	    }else{
	    	Flags.setConfigFlag = false;
	    	this.controll.toErrorPrint("!- FEHLER: keine Defaultkonfiguration gefunden");
	    }//eo if-else, files exists
	    } else {
	    	this.controll.toErrorPrint("!- Diese Funktion ist f�r BAT-Geraete fehlerhaft");
	    	this.controll.toPrint(" - Vorgang abgebrochen");
	    }//eo if-else, Non-Bat-check
	}//eom setConfig
	
	/***
	 * public method saveConf
	 * @param mode
	 * @param name
	 * Desc:
	 * 	saves the acConf.xml as log, default or manual conf
	 */
	public void saveConf(int mode, String name){
		
		String dest = this.pathBase;		//set destination pre-folders
		//create a timestamp for logpurpose
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date());
		
		this.controll.toPrint(" ----- Sichere Konfiguration");
		File dir = new File(dest + "/store/" + name);
		if(!dir.isDirectory()) {
			dir.mkdirs();
		}
		
		//switch mode for new file 
		switch(mode) {	
			//save as new default 
			case 1:	this.controll.toPrint(" -----> Wird als Defaultfile gesichert");
					dest += "default/" + name + ".xml";								//set destination for new deafult
					break;
				
			//save as logfile	
			case 2:	this.controll.toPrint(" -----> Wird als Logfile gesichert");
					dest += "store/" + name +"/" + "ConfAt_" + timeStamp + ".xml";	//set destination for new storagelog
					break;
			//save as manuelcreated-logfile	
			case 3: this.controll.toPrint(" -----> Wird als manueller Abruf gesichert");
					dest += "store/" + name +"/" + "Manuel_ConfAt_" + timeStamp + ".xml";	//set destination for new storagelog
					break;
				
			default: this.controll.toErrorPrint("!---- Unbekannter Fehler beim speichern der Konfig");
					return;
			}
		
		this.controll.toPrint(" ------ Erstelle neuen File");
		
		File filesource = new File(this.pathBase + "acConf.xml");		//open filepath to source
		File filedest = new File(dest);					//open filepath to destination

		try {
			
			filedest.createNewFile();					//create the new file
			FileUtils.copyFile(filesource, filedest);	//copy file from a to b
			FileUtils.deleteQuietly(filesource);		//delete source for safety
	
		}catch(IOException ex) {
			this.controll.toErrorPrint("!------ FILE-FEHLER: " + ex);
		}
		this.controll.toPrint(" ------> Neuer File wurde erstellt");
		
		return;
	}//eom saveConf
	
}//eoc
