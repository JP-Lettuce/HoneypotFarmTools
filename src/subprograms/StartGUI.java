/*
 * Class name: StartGUI
 * Author: Lukas Voetsch
 * Created: 03.05.2018
 * Last Change: 03.05.2018
 * 
 * Description: Standard GUI after starting the program, builds the GUI and calls the listener
 * */
package subprograms;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class StartGUI {
	private ResourceBundle message;
	
	private Display display;
	private Shell shell;
	
	private TabFolder tabFolder;
	private TabItem deviceTab;

	//Tab Devices	
	private Composite compositeDevices;
	private DeviceTab dTab;
	
	private Button addDeviceBtn;
	private Button takeSnapBtn;
	private Button resetAllDevicesBtn;	
	
	//Creates the display for the application
	private void createDisplay() {
		display = new Display();		
	}
	
	//Creates the shell for the application
	private void createShell() {
		GridLayout layoutShell = new GridLayout();
		layoutShell.numColumns = 1;
		shell = new Shell(display);
		shell.setLayout(layoutShell);
		shell.setText(message.getString("shellTitle"));
	}
	
	//Creates the tabs for devices and network management
	private void createTabs() {
		tabFolder = new TabFolder(shell, SWT.FILL);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		deviceTab = new TabItem(tabFolder, SWT.FILL);
		deviceTab.setText(message.getString("deviceTabTitle"));
		deviceTab.setControl(createDeviceTab());
		
		//This tab was meant to manage the tap, but without a tap there's no need
		/*tapTab = new TabItem(tabFolder, SWT.NULL);
		tapTab.setText(message.getString("tapTabTitle"));	
		tapTab.setControl(createTapTab());*/
	}
	
	//Create the device tab	
	private Control createDeviceTab() {
		GridLayout deviceLayout = new GridLayout();
		deviceLayout.numColumns = 5;
		
		compositeDevices = new Composite(tabFolder, SWT.NONE);		
		compositeDevices.setLayout(deviceLayout);
		
		//Create the device tab using the class DeviceTab
		dTab = new DeviceTab(display, shell, message, compositeDevices);
		dTab.build();
	
		return compositeDevices;
	}
	
	//Create the network management tab --> not required anymore
	private Control createTapTab() {
		Composite composite = new Composite(tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);
	
		addDeviceBtn = new Button(composite,SWT.PUSH);
		addDeviceBtn.setText(message.getString("addDeviceBtnText"));
	
		takeSnapBtn = new Button(composite, SWT.PUSH);
		takeSnapBtn.setText(message.getString("takeSnapBtnText"));
	
		resetAllDevicesBtn = new Button(composite, SWT.PUSH);
		resetAllDevicesBtn.setText(message.getString("resetAllDevicesBtnText"));
	
		return composite;		
	}
	
	//Class Constructor
	public StartGUI (ResourceBundle message) {
		this.message = message;
	}
	
	//Step by step build of the window
	public void open() {
		createDisplay();
		createShell();
		createTabs();
		
		shell.open();
		while (!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}			
		}		
	}
	
}
