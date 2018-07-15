/*
 * Class: AddOIDGUI.java
 * Author: Lukas Voetsch
 * Created: 10.06.2018
 * Last Change: 10.06.2018
 * 
 * Description: Form to enter custom OIDs 
 * */
package subprograms;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.snmp4j.smi.OID;

import objects.*;

public class AddOIDGUI extends Dialog{
	private ResourceBundle message;
	private Shell parent;
	private Device device;
	private Button addBtn;
	
	private List list;
	private Label labelDes;
	private Label labelOID;
	private Text textDes;
	private Text textOID;
	
	private Shell shell;
	private ArrayList<OIDDescriptor> arrayOID;
	
	//Create shell for the window
	private void createShell() {
		parent = getParent();
		GridLayout layoutShell = new GridLayout();
		layoutShell.numColumns = 3;
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL); 
	    shell.setText(message.getString("OID")); 
	    shell.setLayout(layoutShell);
	}
	
	//Create input fields for the form
	private void createInput() {
		// Expanding cells
		int horizontalSpan = 3;
		int verticalSpan = 1;
							
		// Grows with the cell
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = true;
								
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
				grabExcessHorizontalSpace, grabExcessVerticalSpace,
				horizontalSpan,verticalSpan);
				
		labelDes = new Label(shell,SWT.LEFT);
		labelDes.setText(message.getString("DescriptorText"));
		labelDes.setLayoutData(gdata);
		
		textDes = new Text(shell, SWT.SINGLE);
		textDes.setLayoutData(gdata);
		
		labelOID = new Label(shell, SWT.LEFT);
		labelOID.setText(message.getString("OID"));
		labelOID.setLayoutData(gdata);
		
		textOID = new Text(shell, SWT.SINGLE);	
		textOID.setLayoutData(gdata);
	}
	
	//Create buttons for the form
	private void createButton() {
		// Expanding cells
		int horizontalSpan = 3;
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
	
	//Create listener to give the buttons their function
	private void createListener(){
		addBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("\"" + textOID.getText() + "\"");
				try {
					
					String desBuffer = textDes.getText();
					String oidBuffer = textOID.getText();
					OIDDescriptor oidDes = new OIDDescriptor(desBuffer, new OID(oidBuffer));					
					arrayOID.add(oidDes);
					list.add(textDes.getText());
					
				} catch (NumberFormatException e) {
					
					MessageBox mBOpen2 = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					mBOpen2.setMessage(message.getString("invalidOID"));
					mBOpen2.setText(message.getString("OID"));
					@SuppressWarnings("unused")
					int x = mBOpen2.open();	
					
				} catch (NullPointerException e) {
					
					MessageBox mBOpen2 = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					mBOpen2.setMessage(message.getString("invalidOID"));
					mBOpen2.setText(message.getString("OID"));
					@SuppressWarnings("unused")
					int x = mBOpen2.open();	
					
				}
				shell.dispose(); //close window
		      }
		});
	}
	
	//Step by step build of the window
	public Device open() {
		createShell();
		createInput();
		createButton();
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
	//Get the resource bundle with the language parameters
	public void setResourceBundle(ResourceBundle message) {
		this.message = message;
	}
	
	//Create message box with error status
	public void errorStatus(Shell shell) {
		MessageBox mBOpen2 = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		mBOpen2.setMessage("No Device Connected");
		mBOpen2.setText("Add Device");
		@SuppressWarnings("unused")
		int x = mBOpen2.open();	
	}
	
	//Class constructor
	public AddOIDGUI(Shell parent, List list, ArrayList<OIDDescriptor> arrayOID, ResourceBundle message) {
		super(parent);		
		this.list = list;
		this.arrayOID = arrayOID;
		this.message = message;
	}
}
