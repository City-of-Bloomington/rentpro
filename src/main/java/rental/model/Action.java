package rental.model;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;



public class Action{
	
    boolean debug;
    final static long serialVersionUID = 10L;
    static Logger logger = LogManager.getLogger(Action.class);
	
    String id="", legal_id="", errors="",
	notes="", actionBy="", actionByName="",
    //
	actionDate="";
    //
    User user = null;
    //
    // basic constructor
    public Action(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public Action(boolean deb, String id){

	debug = deb;
	//
	// initialize
	//
	this.id=id;
    }
    //
    public Action(boolean deb, String val, String val2, String val3, String val4, String val5){

	debug = deb;
	//
	// initialize
	//
	setId(val);
	setNotes(val2);
	setActionBy(val3);		
	setLegal_id(val4);
	setActionDate(val5);
    }	
    //
    // setters
    //
    public void setId(String val){
	id = val;
    }
    public void setLegal_id(String val){
	legal_id = val;
    }
    public void setActionBy(String val){
	actionBy = val;
    }
    public void setActionByName(String val){
	actionByName = val;
    }
    public void setActionDate(String val){
	actionDate = val;
    }	
    public void setNotes(String val){
	notes = val;
    }
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public String  getLegal_id(){
	return legal_id;
    }
    public String  getNotes(){
	return notes;
    }
    public String  getActionBy(){
	return actionBy;
    }
    public String  getActionByName(){
	if(actionByName.equals("")){
	    if(user == null){
		if(!actionBy.equals("")){
		    User usr = new User(debug, actionBy);
		    String back = usr.doSelect();
		    if(back.equals("")){
			user = usr;
		    }
		}
	    }
	    if(user != null){
		actionByName = user.getFullName();
	    }
	}
	return actionByName;
    }
    public String  getActionDate(){
	return actionDate;
    }
    public String  getErrors(){
	return errors;
    }

    public String doSave(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
	String qq = "insert into legal_actions values(0,?,now(),?,?)";
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }			
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, legal_id);
	    stmt.setString(2, actionBy);
	    stmt.setString(3, notes);
	    stmt.executeUpdate();
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt2 = con.prepareStatement(qq);			
	    rs = stmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }			
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}			
	return back;

    }
    public String doUpdate(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "update legal_actions set notes = ? where id=? ";
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }			
	    stmt = con.prepareStatement(qq);
	    if(notes.equals(""))
		stmt.setString(1, null);
	    else
		stmt.setString(1, notes);
	    stmt.setString(2, id);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;

    }
    public String doDelete(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "delete from  legal_actions where id = ?";
	if(debug){
	    logger.debug(qq);
	}		
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}

	return back;

    }
	
    public String doSelect(){
		
	String back = "";
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "select "+
	    "notes,"+
	    "actionBy,"+
	    "legal_id,"+
	    "date_format(actionDate,'%m/%d/%Y') "+
	    " from legal_actions where id=?";		
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) notes = str;
		str = rs.getString(2);
		if(str != null) actionBy = str;
		str = rs.getString(3);
		if(str != null) legal_id = str;
		str = rs.getString(4);  
		if(str != null) actionDate = str;
	    }
	    else{
		back = "Record "+id+" Not found";
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;

    }	

}






















































