package rental.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Item{

    boolean debug = false;
    final static long serialVersionUID = 500L;
    static Logger logger = LogManager.getLogger(Item.class);

    String id="", name = "";

    public Item(boolean val){
	debug = val;
    }
    public Item(boolean val, String val2){
	debug = val;
	setId(val2);
    }	
    public Item(boolean val, String val2, String val3){
	debug = val;
	setId(val2);
	setName(val3);
    }
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setName(String val){
	if(val != null)
	    name = val;
    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getName(){
	return name;
    }
    public String toString(){
	return name;
    }
	
}























































