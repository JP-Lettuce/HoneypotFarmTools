/*
 * Class: AddDeviceGUI.java
 * Author: Lukas Voetsch
 * Created: 03.05.2018
 * Last Change: 15.05.2018
 * 
 * Description: Form to connect new devices to the application
 * */
package subprograms;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import objects.Device;
import objects.OIDDescriptor;

public class ChangeOIDGUI extends Dialog{
	private ResourceBundle message;
	private Shell parent;
	private Device device;
	
	private Group gOID;
	private List listOID;
	private int selected;
	private Button addOIDBtn;
	private Button deleteOIDBtn;
	
	private Button addBtn;
	
	private Shell shell;
	private ArrayList<OIDDescriptor> arrayOID = new ArrayList<OIDDescriptor>();
	
	//Create shell for the window
	private void createShell() {
		parent = getParent();
		GridLayout layoutShell = new GridLayout();
		layoutShell.numColumns = 1;
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL); 
	    shell.setText(message.getString("OID")); 
	    shell.setLayout(layoutShell);
	}
	
	//Create list for custom OIDs and buttons to add/delete from the list
	private void createOIDInput(){
		// Expanding cells
		int horizontalSpan = 1;
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
		gLayout.numColumns = 4;
		
		// set layout
		gOID.setLayout(gLayout);
		gOID.setText(message.getString("userOIDTitle"));
		
		// Expanding cells
		horizontalSpan = 3;
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
		int horizontalSpan = 1;
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
	
	//Fill the list with the available OIDs of the device
	public void fillList() {
		for(OIDDescriptor o: arrayOID) {
			String name = o.getDescriptor();
			listOID.add(name);
		}		
	}
	
	//Create listener to give the buttons their functions if there are any errors on the way
	//the return value will be set null --> calls a msgbox with the caption "no device added"
	private void createListener(){
		addBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				device.setOIDDescriptor(arrayOID);
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
	public void open() {
		createShell();
		createOIDInput();
		createFinishButton();
		fillList();
		createListener();
	    shell.pack();
	    shell.open();
	    Display display = parent.getDisplay(); 
	    while (!shell.isDisposed()) 
	    { 
	    	if (!display.readAndDispatch()) display.sleep(); 
	    }
	}
	
	//Get properties of the selected language
	public void setResourceBundle(ResourceBundle message) {
		this.message = message;
	}
	
	//Class Constructor
	public ChangeOIDGUI(Shell parent, Device device) {
		super(parent);		
		this.device = device;
		arrayOID = device.getOIDDescriptor();
	}
}
