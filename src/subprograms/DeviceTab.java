/*
 * Class: DeviceTab.java
 * Author: Lukas Voetsch
 * Created: 03.05.2018
 * Last Change: 20.06.2018
 * 
 * Description: Builds the tab for the device management and calls the responsible listeners for action
 * */
package subprograms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import flags.Flags;
import listener.*;
import manager.JSONmanager;
import objects.Device;
import watcher.Controll;

public class DeviceTab {
	
	private Display display;
	private Shell shell;
	private ResourceBundle message;
	
	private Composite composite;
	private Composite listComposite;
	private ScrolledComposite scrolledComposite;
	private Composite btnComposite;
	
	//Array for the loop over the devices out of the resource/Devices.txt
	private int length;
	private Text [] text;
	private Button [] statusBtn;
	private Button [] resetBtn;
	private Button [] exportBtn;
	private Button [] deleteBtn;
	
	private ArrayList<Device> devices;
	
	private Button addDeviceBtn;
	private Button resetAllDevicesBtn;
	private Button refreshAllDevicesBtn;
	
	//Just for fun
	private Label discoLabel;
	private Button discoStartBtn;
	private Button discoStopBtn;
	
	private int listenerCounter;
	private JSONmanager jMan = new JSONmanager();
	
	//Create composite with vertical scrolling for the devices
	private void createComposites() {
		scrolledComposite = new ScrolledComposite(composite, SWT.V_SCROLL);
				
		//Create a composite for the controls
		listComposite = new Composite(scrolledComposite,SWT.NONE);
		//listComposite.setLayout(new FillLayout());
		//Expanding of the cells
		int horizontalSpan = 5;
		int verticalSpan = 1;				
		//Cell grows with the window
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = true;
								
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
				grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan,verticalSpan);
				
		scrolledComposite.setLayoutData(gdata);
		listComposite.setLayoutData(gdata);
		
		//Give the composite its own GridLayout
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		listComposite.setLayout(gridLayout);
	}

	//Build a entry for every single device saved in the Devices.txt for the ArrayList devices
	private void createList() {						
		//Expanding of the cells
		int horizontalSpan = 1;
		int verticalSpan = 5;
				
		//Cell grows with the window
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = false;
				
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
			grabExcessHorizontalSpace, grabExcessVerticalSpace,
			horizontalSpan,verticalSpan);
		//Defines the height of the controls to 40 px
		gdata.heightHint  = 40;		
		
		//Get length of the ArrayList
		length = devices.size();
		text = new Text[length];
		statusBtn = new Button[length];
		exportBtn = new Button[length];
		resetBtn = new Button[length];
		deleteBtn = new Button[length];
		
		//Get all controls for the devices
		for(int i=0; i<length; i++) {
			Device device = devices.get(i);
			
			text[i] = new Text(listComposite, SWT.None);
			text[i].setText(device.getName());
			text[i].setLayoutData(gdata);
			
			resetBtn[i] = new Button(listComposite,SWT.PUSH);
			resetBtn[i].setText(message.getString("resetBtnText"));
			resetBtn[i].setLayoutData(gdata);
			
			statusBtn[i] = new Button(listComposite, SWT.PUSH);
			Color color;
			switch(device.getCurrentStatus()){
				case 1:
					color = display.getSystemColor(SWT.COLOR_GREEN);
		            break; 
		        case 2:
		        	color = display.getSystemColor(SWT.COLOR_YELLOW);
		        	break;
		        default:
		        	color = display.getSystemColor(SWT.COLOR_RED);
		        	break;
	        }
			statusBtn[i].setBackground(color);
			statusBtn[i].setLayoutData(gdata);		
			
			exportBtn[i] = new Button(listComposite, SWT.PUSH);
			exportBtn[i].setText(message.getString("exportBtnText"));
			exportBtn[i].setLayoutData(gdata); 
			
			deleteBtn[i] = new Button(listComposite,SWT.PUSH);
			deleteBtn[i].setText(message.getString("deleteBtnText"));
			deleteBtn[i].setLayoutData(gdata);
		}
		//Fill the scrolled composite with content
		scrolledComposite.setContent(listComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinSize(listComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	//Create composite for the buttons add, reset all, refresh all
	private void createButtons() {
		btnComposite = new Composite(composite,SWT.NONE);		
		
		//Expanding of the cells
		int	horizontalSpan = 3;
		int	verticalSpan = 1;
		//Cell grows with the window
		boolean	grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = false;
								
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
				grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan,verticalSpan);
				
		btnComposite.setLayoutData(gdata);
		//Give the composite its own GridLayout
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;		
		btnComposite.setLayout(gridLayout);
		
		//Create content for the composite				
		//Expanding of the cells
		horizontalSpan = 1;
		verticalSpan = 3;
						
		//Cell grows with the window
		grabExcessHorizontalSpace = false;
		grabExcessVerticalSpace = false;
						
		gdata = new GridData(SWT.FILL,SWT.FILL,
				grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan,verticalSpan);
		
		addDeviceBtn = new Button(btnComposite,SWT.PUSH);
		addDeviceBtn.setText(message.getString("addDeviceBtnText"));
		addDeviceBtn.setLayoutData(gdata);

		resetAllDevicesBtn = new Button(btnComposite, SWT.PUSH);
		resetAllDevicesBtn.setText(message.getString("resetAllDevicesBtnText"));
		resetAllDevicesBtn.setLayoutData(gdata);
		
		refreshAllDevicesBtn = new Button(btnComposite, SWT.PUSH);
		refreshAllDevicesBtn.setText(message.getString("refreshAllDevicesBtnText"));
		refreshAllDevicesBtn.setLayoutData(gdata);
		
		discoLabel = new Label(btnComposite, SWT.NULL);
		discoLabel.setText("Disco");
		
		discoStartBtn = new Button(btnComposite, SWT.PUSH);
		discoStartBtn.setText("Start");
		discoStartBtn.setLayoutData(gdata);
		
		discoStopBtn = new Button(btnComposite, SWT.PUSH);
		discoStopBtn.setText("Stop");
		discoStopBtn.setLayoutData(gdata);
	}
	
	//Create listener for the buttons to call their function
	private void createListener() {
		//Create Listener for every Device in the list
		for(listenerCounter=0; listenerCounter<length; listenerCounter++) {
			Device device = devices.get(listenerCounter);
			DeviceListener dListener = new DeviceListener(device);
			
			//Opens a little GUI with an overview over certain information
			statusBtn[listenerCounter].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					StatusGUI sGUI = new StatusGUI(shell,display, device);
					try {
						sGUI.open(device);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Error - StatusGUI");
					}
			      }
			});
			
			//Gets the current configuration file of the device
			exportBtn[listenerCounter].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					dListener.getConfig();
					if(Flags.getConfigFlag) {
						msgBox("Export: Success", "Export config");
					} else {
						msgBox("Export: Failed, see service for details", "Export config");
					}
				}
			});
			
			
			
			//Reset the device with the previous selected script
			resetBtn[listenerCounter].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					dListener.setConfig();
					if(Flags.setConfigFlag) {
						msgBox("Default reset: Success", "Reset config");
					} else {
						msgBox("Default reset: No default found", "Reset config");
					}
			      }
			});
			
			//Delete the device out of the list of devices
			deleteBtn[listenerCounter].addSelectionListener(new SelectionAdapter() {
				@Override 
				public void widgetSelected(SelectionEvent event) {
					devices.remove(device);
					JSONmanager.write(devices);
					//Update the device overview
					refresh();					
					shell.layout();
			      }
			});
		}
		
		//Call listener to add a device to the list
		addDeviceBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				AddDeviceGUI add = new AddDeviceGUI(shell, devices);
				add.setResourceBundle(message);
				//Bei Add open() muss als Rückgabewert ein Device herauskommen
				Device device = add.open();
				if(device != null) {
					device.checkAvailibilty();
					devices.add(device);
					JSONmanager.write(devices);
					//Update the device overview
					refresh();
					
					shell.layout();					
				}
				else {
					add.errorStatus(shell);
				}				
		      }
		});
		
		//Reset all the devices from the networks
		resetAllDevicesBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				for(Device device : devices) {
					DeviceListener dListener = new DeviceListener(device);
					dListener.setConfig();
				}
		      }
		});
		
		//Refresh the list of devices
		refreshAllDevicesBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				refresh();
			}
		});
		
		//Just for fun------------------------------------------------------*
		discoStartBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DiscoListener dl = new DiscoListener(devices);
				dl.start();
			}
		});
		discoStopBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DiscoListener dl = new DiscoListener(devices);
				dl.stop();
			}
		});
		//------------------------------------------------------------------*
	}
	
	//Function: Disposes all elements inside the DeviceTab and rebuilds the tab with new information
	public void refresh() {
		for(Control control: listComposite.getChildren())
			control.dispose();
		
		//Destroy
		listComposite.dispose();
		scrolledComposite.dispose();
		btnComposite.dispose();
		
		//Rebuild
		build();
		composite.layout();		
	}
	
	//Function: gets devices from the config file --> later if wanted from a sql-db
	//Fills the ArrayList<Device> devices with the information about all the devices and checks
	//if they are available  
	private void getDevices() {
		devices = jMan.read();
		for(Device d: devices) {
			d.checkAvailibilty();
		}
	}
	
	//Step by step build of the composite
	public void build() {
		getDevices();		
		createComposites();
		createList();
		createButtons();
		createListener();
	}
	
	//Class Constructor
	public DeviceTab (Display display, Shell shell, ResourceBundle message, Composite composite) {
		this.display = display;
		this.shell = shell;
		this.message = message;
		this.composite = composite;
	}
	
	//Message box in case of failure/error
	private void msgBox(String text, String title) {
		MessageBox mBOpen2 = new MessageBox(this.shell, SWT.ICON_INFORMATION | SWT.OK);
		mBOpen2.setMessage(text);
		mBOpen2.setText(title);
		@SuppressWarnings("unused")
		int x = mBOpen2.open();	
	}
}
