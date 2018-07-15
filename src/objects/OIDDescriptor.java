/*
 * Class: OIDDescriptor.java
 * Author: Lukas Voetsch
 * Created: 10.06.2018
 * Last Change: 10.06.2018
 * 
 * Description: Object for the creation and for the management of custom OIDs
 * 
 * Parameter: 	OID oid --> OID
 * 				String descriptor --> descripes what is behind the OID
 * */
package objects;

import org.snmp4j.smi.OID;

//Help-Class to describe the OID of an object
public class OIDDescriptor {
	private OID oid;
	private String descriptor;
	
	public OIDDescriptor(String descriptor, OID oid) {
		this.descriptor = descriptor;
		this.oid = oid;
	}

	public OID getOid() {
		return oid;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setOid(OID oid) {
		this.oid = oid;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}
}
