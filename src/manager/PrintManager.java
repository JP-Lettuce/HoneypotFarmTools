package manager;

public class PrintManager {
	
	//Global Variables throu Options
	private boolean silentmode;
	private int sleepTimePrint;		//sleep time between two console prints
	private int sleepNextCycle;		//sleep time between two cycles
	
	/***
	 * default Constructor PrintManager
	 */
	public PrintManager() {
		
		this.silentmode = true;
		this.setOptionsDefault();
	}
	
	/***
	 * public method setOptions
	 * @param pSleepTimePrint
	 * @param pSleepNextCycle
	 * Desc:
	 * 	Set custom options for usage
	 */
	public void setOptions(int pSleepTimePrint, int pSleepNextCycle) {
		this.sleepNextCycle = pSleepNextCycle;
		this.sleepTimePrint = pSleepTimePrint;
		
	}//eom setOptions
	
	/***
	 * public method setSilentmode
	 * @param pmode
	 * Desc:
	 * 	enable or disalbe the silentmode
	 */
	public void setSilentmode(boolean pmode) {
		this.silentmode = pmode;
	}
	
	/***
	 * public method setOptionsDefault
	 * Desc:
	 * 	load options with default values
	 */
	public void setOptionsDefault() {
		this.sleepTimePrint = 600;
		this.sleepNextCycle = 120000;
	}//eom setOptionsDefault
	
	/***
	 * public method bootScreen
	 * Desc:
	 * 	shows a smal bootscreen on service start
	 */
	public void bootScreen() {
		this.gap();

		System.out.println("/------------------------------------------------------------\\");
		System.out.println("|              HIRSCHMANN - INDUSTRIAL HONEYNET              |");
		System.out.println("|                   TOOLS: Service-Mode                      |");
		System.out.println("|                    NETWORK WATCHER v1.1                    |");;
		System.out.println("X------------------------------------------------------------X");
	}//eom bootScreen
	
	/***
	 * public method print
	 * @param pstring
	 * Desc:
	 * 	print shortcut method
	 */
	public void print(String pstring) {
		if(!this.silentmode) {
			System.out.println(pstring);
			try {
				Thread.sleep(this.sleepTimePrint);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}//eom print
	
	public void errorPrint(String pstring) {
		System.err.println(pstring);
	}
	
	
	/***
	 * public method endM
	 * Desc:
	 * 	outprint after finished working on a machine
	 */
	public void endM(String name) {
		this.print(" -- Ende der bearbeitung von " + name);
		this.print("\n ------------------------------------------------------------ \n");
	}//eom endM
	
	/***
	 * public method startM
	 * Desc:
	 * 	outprint at start on wokring on a machine
	 */
	public void startM(String name, String ip) {
		this.print(" -- Beginne Arbeit an: " + name + " (" + ip + ")");
	}// eom startM
	
	/***
	 * public method endC
	 * Desc:
	 * 	outprint after finishing a work cycle
	 */
	public void endC() {
		
		int ncInMin = this.sleepNextCycle / 60000;
		
		this.print(" - Ende des Arbeitszyclus");
		this.print(" - Naechster Zyklus in " + ncInMin + " Minuten");
		this.gap();
		this.print("|------------------------------------------------------------|");
		this.waitNextCycle();
	}// oem endC
	
	/***
	 * public method waitNextCycle
	 * Desc:
	 * 	Wait till the next cycle starts
	 *  Show a beautiful loadingbar auf "x"
	 */
	private void waitNextCycle() {
		
		int progress = this.sleepNextCycle / 60;	//calculate the secounds per x print
		System.out.print("|");
		
		for( int i = 0; i< 60; i++) {
			try {
				Thread.sleep(progress);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("x");
		}
		
		System.out.print("| \n");
		this.print("|------------------------------------------------------------|");
		this.gap();
	}
	
	/***
	 * public method gap
	 * Desc:
	 * 	Print a empty line on the console
	 */
	public void gap() {
		this.print("");
	}//eom gap

}//eoc
