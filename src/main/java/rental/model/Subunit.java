package rental.model;
import java.util.Vector;
import java.io.*;
import java.sql.*;
import rental.list.*;
import rental.utils.*;

public class Subunit implements java.io.Serializable{

    String 
	id="", type="",identifier="";
    final static long serialVersionUID = 950L;
    boolean debug = false;
    public Subunit(){

    }

    public Subunit(boolean deb){
	debug = deb;
    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getType(){
	return type;
    }
    public String getIdentifier(){
	return identifier;
    }
    public String getAddress(String sep){
	String str = type;
	if(!identifier.equals("")){
	    if(!str.equals("")) str += sep;
	    str += identifier;
	}
	return str;
    }
    public String getAddress(){
	return getAddress(" ");
    }
    public void setId (String val){
	if(val != null && !val.trim().equals(""))
	    id = val;
    }	
    public void setType (String val){
	if(val != null && !val.trim().equals(""))
	    type = val.trim();
    }
    public void setIdentifier (String val){
	if(val != null)
	    identifier = val;
    }
	
    public String toString(){

	return getAddress();
    }
	
	
}
