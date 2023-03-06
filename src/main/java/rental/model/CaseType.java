package rental.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class CaseType {

    boolean debug = false;
    static Logger logger = LogManager.getLogger(CaseType.class);
    String typeId = "", typeDesc = "";
    final static long serialVersionUID = 170L;	
    public CaseType(boolean val){
	debug = val;
    }
	
    public CaseType(String val, boolean deb){
	typeId = val;
	debug = deb;
    }
    public CaseType(String val, String val2, boolean deb){
	typeId = val;
	typeDesc = val2;
	debug = deb;
    }	
    //
    //setters
    //
    public void setId(String val){
	if(val != null)
	    typeId = val;
    }
    public void setDesc(String val){
	if(val != null)
	    typeDesc = val;
    }
    //
    // getters
    //
    public String getId(){
	return typeId ;
    }
    public String getDesc(){
	return typeDesc;
    }
    public String toString(){
	return typeDesc;
    }
	
}






















































