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


public class Structure{

    boolean debug = false;
	
    final static long serialVersionUID = 930L;
    static Logger logger = LogManager.getLogger(Structure.class);

    UnitList units = null;
    String errors = null;
    String rid="", // rental id
	id = "", 
	identifier = "";

    public Structure(boolean val){
	debug = val;
    }
    public Structure(boolean val, String val2){
	debug = val;
	id = val2;
    }
    public Structure(boolean debug,
		     String id,
		     String rid,
		     String identifier){
	this.debug = debug;
	this.id = id;
	this.rid = rid;
	setIdentifier(identifier);
    }
    public void setId(String val){
	if(val != null)
	    id = val;
    }	
    public void setRid(String val){
	if(val != null)
	    rid = val;
    }
    public void setIdentifier(String val){
	if(val != null && !val.equals(""))
	    identifier = val;
    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getRid(){
	return rid;
    }
    public String getIdentifier(){
	return identifier;
    }
    public String toString(){
	return identifier;
    }
    public UnitList getUnits(){
	if(units == null)
	    errors = findUnits();
	return units;
    }
    /**
     * find the number of units group in this structure
     * needed for formating tables (such as in permit)
     */
    public int getTotalItems(){
	if(units == null)
	    errors = findUnits();
	if(units == null) return 0;
	return units.size();
    }
    //
    public String doSave(){
	Connection con = null;
	PreparedStatement stmt = null, stmt2 = null;
	ResultSet rs = null;
	String back = "";
	if(rid.equals("")){
	    back = "Rental id not set";
	    return back;
	}
	if(identifier.equals("")){
	    back = "Identifier is not set";
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	String qq = " select rent_struct_seq.nextval from dual";
	if(debug)
	    logger.debug(qq);
	try{
	    stmt2 = con.prepareStatement(qq);
	    rs = stmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    qq = "insert into rental_structures values(?,?,?)";
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.setString(2, rid);
	    stmt.setString(3, identifier);
	    stmt.executeUpdate();
	}catch(Exception ex){
	    back += ex+":"+qq;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}
	return back;
    }
    //
    public String doUpdate(){
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String back = "";
	if(id.equals("")){
	    back = " id not set";
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	String qq = " update rental_structures set ";
	qq += " identifier = ? ";
	qq += " where id=? ";
	if(debug)
	    logger.debug(qq);	
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, identifier);
	    stmt.setString(2, id);
	    stmt.executeUpdate();
	}catch(Exception ex){
	    back += ex+":"+qq;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String doSelect(){
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	String qq = " select rid,identifier from rental_structures "+
	    " where id=?";
	if(debug)
	    logger.debug(qq);
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1,id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		rid = rs.getString(1);
		identifier = rs.getString(2);
	    }
	}catch(Exception ex){
	    back += ex+":"+qq;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String doDelete(){
		
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String back = "";
	String qq = " delete from rental_structures where id=?";
	if(debug)
	    logger.debug(qq);
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.executeUpdate();
	}catch(Exception ex){
	    back += ex+":"+qq;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);	
	}
	return back;
    }
    public String findUnits(){
	units = new UnitList(debug, id);
	String back = units.find();
	return back;
    }
}























































