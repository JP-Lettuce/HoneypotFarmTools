/*
 * Class: AddDeviceGUI.java
 * Author: Lukas Voetsch
 * Created: 03.05.2018
 * Last Change: 15.05.2018
 * 
 * Description: Form to connect new devices to the application
 * */
package subprograms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import listener.AddDeviceListener;
import objects.Device;
import objects.OIDDescriptor;

public class AddDeviceGUI extends Dialog{
	private ResourceBundle message;
	private Shell parent;
	private Device device;
	
	private Spinner[] ipSpinner;	
	
	private Group gBtn;
	private Button batBtn;
	private Button nonBatBtn;
	
	private Group gOID;
	private List listOID;
	private int selected;
	private Button addOIDBtn;
	private Button deleteOIDBtn;
	
	private Button addBtn;
	
	private Shell shell;
	private ArrayList<OIDDescriptor> arrayOID = new ArrayList<OIDDescriptor>();
	private ArrayList<OIDDescriptor> arrayOIDfinal;
	private ArrayList<Device> devices;
	
	//Create shell for the window
	private void createShell() {
		parent = getParent();
		GridLayout layoutShell = new GridLayout();
		layoutShell.numColumns = 2;
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL); 
	    shell.setText(message.getString("addDeviceGUITitle")); 
	    shell.setLayout(layoutShell);
	}
	
	//Create spinner boxes to put in the ip address
	private void createIPInput() {
		// Expanding cells
		int horizontalSpan = 2;
		int verticalSpan = 1;
				
		// Grows with the cell
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = true;
				
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
				grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan,verticalSpan);
				
		// iPv4  --> 4 Spinner
		ipSpinner = new Spinner[4];
				
		// Group for the spinner
		Group gSpin = new Group(shell, SWT.NO_RADIO_GROUP | SWT.SHADOW_ETCHED_IN);
		gSpin.setLayoutData(gdata);
				
		// Spinner should be placed horizontal
		FillLayout layoutIP = new FillLayout(SWT.HORIZONTAL);
		
		// Spacing between the spinner
		layoutIP.spacing = 8;
		// set layout
		gSpin.setLayout(layoutIP);
		gSpin.setText(message.getString("gSpinText"));
				
		//Create and limit the spinner 0->255
		for (int i=0; i<ipSpinner.length;i++) {
			// Create and place Spinner
			ipSpinner[i] = new Spinner(gSpin,SWT.NONE);
			// min = 0, max = 255
			ipSpinner[i].setMinimum(0);
			ipSpinner[i].setMaximum(255);
		}	
	}
	
	//Create radio buttons to select some preset device OIDs
	private void createRadioButtons() {
		// Expanding cells
		int horizontalSpan = 1;
		int verticalSpan = 1;
						
		// Grows with the cell
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = true;
						
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
				grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan,verticalSpan);
		
		gBtn = new Group(shell, SWT.NONE);
		gBtn.setLayoutData(gdata);
		
		//there should be only 3 buttons per row
		GridLayout gLayout = new GridLayout();
		gLayout.numColumns = 2;
		
		// set layout
		gBtn.setLayout(gLayout);
		gBtn.setText(message.getString("gBtnText"));
		
		//Hirschmann non-BAT
		nonBatBtn = new Button(gBtn, SWT.RADIO);
		nonBatBtn.setText(message.getString("nonBatBtnText"));
		nonBatBtn.setLayoutData(gdata);
		
		//Hirschmann BAT
		batBtn = new Button(gBtn, SWT.RADIO);
		batBtn.setText(message.getString("batBtnText"));
		batBtn.setLayoutData(gdata);
		batBtn.setSelection(true);
	}
	
	//Create list for custom OIDs and buttons to add/delete from the list
	private void createOIDInput(){
		// Expanding cells
		int horizontalSpan = 2;
		int verticalSpan = 1;
						
		// Grows with the cell
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = true;
						
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
			grabExcessHorizontalSpace, grabExcessVerticalSpace,
			horizontalSpan,verticalSpan);
		
		gOID = new Group(shell, SWT.NONE);
		gOID.setLayoutData(gdata);
		
		//there should be only 3 buttons per row
		GridLayout gLayout = new GridLayout();
		gLayout.numColumns = 2;
		
		// set layout
		gOID.setLayout(gLayout);
		gOID.setText(message.getString("userOIDTitle"));
		
		// Expanding cells
		horizontalSpan = 1;
		verticalSpan = 2;
						
		// Grows with the cell
		grabExcessHorizontalSpace = true;
		grabExcessVerticalSpace = true;
						
		gdata = new GridData(SWT.FILL,SWT.FILL,
			grabExcessHorizontalSpace, grabExcessVerticalSpace,
			horizontalSpan,verticalSpan);
		
		listOID = new List(gOID, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		listOID.setLayoutData(gdata);
		
		//Create a menu out of the list of OIDs to make it possible to select an item of the list
		//and if wished to delete it.
		final Menu menu = new Menu(listOID);
		listOID.setMenu(menu);
		menu.addMenuListener(new MenuAdapter()
		{
		    public void menuShown(MenuEvent e)
		    {
		        selected = listOID.getSelectionIndex();

		        if(selected < 0 || selected >= listOID.getItemCount())
		            return;

		        MenuItem[] items = menu.getItems();
		        for (int i = 0; i < items.length; i++)
		        {
		            items[i].dispose();
		        }
		        MenuItem newItem = new MenuItem(menu, SWT.NONE);
		        newItem.setText("Delete");
		    }
		});
		
		//Add/Delete Button for the list
		horizontalSpan = 1;
		verticalSpan = 1;
						
		// Grows with the cell
		grabExcessHorizontalSpace = true;
		grabExcessVerticalSpace = true;
						
		gdata = new GridData(SWT.FILL,SWT.FILL,
			grabExcessHorizontalSpace, grabExcessVerticalSpace,
			horizontalSpan,verticalSpan);
		
		addOIDBtn = new Button(gOID, SWT.PUSH);
		addOIDBtn.setText(message.getString("addBtnText"));
		addOIDBtn.setLayoutData(gdata);
		
		deleteOIDBtn = new Button(gOID, SWT.PUSH);
		deleteOIDBtn.setText(message.getString("deleteBtnText"));
		deleteOIDBtn.setLayoutData(gdata);
	}
	
	//Create button to add the device from the form
	private void createFinishButton() {
		// Expanding cells
		int horizontalSpan = 2;
		int verticalSpan = 1;
						
		// Grows with the cell
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = true;
						
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
				grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan,verticalSpan);
		addBtn = new Button(shell, SWT.PUSH);
		addBtn.setText(message.getString("addBtnText"));
		addBtn.setLayoutData(gdata);		
	}
	
	//Function: Check which radio button out of the gBtn is selected
	//Return-Value: int type
	// 1 = Bat, 2 = RSP, 3 = EAGLE
	private int getType() {
		int type = 0;
		if(nonBatBtn.getSelection())
			type = 1;
		else if(batBtn.getSelection())
			type = 2;
		return type;		
	}
	
	//Create listener to give the buttons their functions if there are any errors on the way
	//the return value will be set null --> calls a msgbox with the caption "no device added"
	private void createListener(){
		addBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				AddDeviceListener addListener = new AddDeviceListener();
				try {
					
					int type = getType();					
					device = addListener.createDevice(ipSpinner,listOID, devices, type);
					arrayOIDfinal = device.getOIDDescriptor();
					arrayOIDfinal.addAll(arrayOID);
					device.setOIDDescriptor(arrayOIDfinal);
					
				} catch (IOException | NullPointerException e) {
					
					// TODO Auto-generated catch block
					device = null;
					System.out.println(e);
				}
				shell.dispose();
		      }
		});
		
		//Opens the AddOIDGUI to create a custom OID for the device
		addOIDBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				AddOIDGUI addOID = new AddOIDGUI(shell,listOID,arrayOID,message);
				addOID.open();
		      }
		});
		
		//remove custom OID from the list
		deleteOIDBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                listOID.remove(selected);
                arrayOID.remove(selected);
            }
		});
	}
	
	//Step by step build of the window
	public Device open() {
		createShell();
		createIPInput();
		createRadioButtons();
		createOIDInput();
		createFinishButton();
		createListener();
	    shell.pack();
	    shell.open();
	    Display display = parent.getDisplay(); 
	    while (!shell.isDisposed()) 
	    { 
	    	if (!display.readAndDispatch()) display.sleep(); 
	    }
	    return device;
	}
	
	//Get properties of the selected language
	public void setResourceBundle(ResourceBundle message) {
		this.message = message;
	}
	
	//Message box in case of failure/error
	public void errorStatus(Shell shell) {
		MessageBox mBOpen2 = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		mBOpen2.setMessage("No Device Connected");
		mBOpen2.setText("Add Device");
		@SuppressWarnings("unused")
		int x = mBOpen2.open();	
	}
	
	//Class Constructor
	public AddDeviceGUI(Shell parent, ArrayList<Device> devices) {
		super(parent);		
		this.devices = devices;
	}
}
