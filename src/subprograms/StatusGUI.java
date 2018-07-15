/*
 * Class: StatusGUI.java
 * Author: Lukas Voetsch
 * Created: 20.05.2018
 * Last Change: 20.06.2018
 * 
 * Description: Builds a window to show the user the status information of the selected device
 * */
package subprograms;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import objects.Device;

public class StatusGUI extends Dialog{
	
	private Shell shell;
	private Display display;
	private Device device;
	
	private Label label;
	
	//Create shell for the window
	private void createShell() {
		//display = parent.getDisplay();		
		GridLayout layoutShell = new GridLayout();
		layoutShell.numColumns = 2;
		shell = new Shell(display, SWT.DIALOG_TRIM); 
	    shell.setText(device.getName()); 
	    shell.setLayout(layoutShell);
	}
	
	//Fill the window with information
	private void createLabel(Device device) {
		//Get information of the device
		String status = device.getStatusInformation();
		// Expanding cells
		int horizontalSpan = 1;
		int verticalSpan = 1;
								
		// Grows with the cell
		boolean grabExcessHorizontalSpace = true;
		boolean grabExcessVerticalSpace = true;
								
		GridData gdata = new GridData(SWT.FILL,SWT.FILL,
			grabExcessHorizontalSpace, grabExcessVerticalSpace,
			horizontalSpan,verticalSpan);
						
		label = new Label(shell, SWT.None);
		label.setText(status);
		label.setLayoutData(gdata);
	}

	//Step by step build of the window
	public void open(Device device) throws IOException {
		createShell();
		createLabel(device);
		shell.pack();
	    shell.open();
	    while (!shell.isDisposed()) 
	    { 
	    	if (!display.readAndDispatch()) display.sleep(); 
	    }		
	}
	
	//Class Constructor
	public StatusGUI(Shell parent, Display display,Device device) {
		super(parent);
		this.display = display;
		this.device = device;
	}
}
