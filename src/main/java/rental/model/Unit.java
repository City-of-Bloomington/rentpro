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

public class Unit{

    boolean debug = false;
    final static long serialVersionUID = 970L;
    static Logger logger = LogManager.getLogger(Unit.class);
    String uninspected="",
	sid = "", // structure id
	id = "", // unit group id
	sleepRoom = ""; // for units that are classed as sleeping room unit
    String errors = "";
    int	units= 0,  // units count
	bedrooms = 0, // per unit
	baths=0, // not used
	occLoad = 0;  
    Structure structure = null;

    public Unit(boolean val){
	debug = val;
    }
    public Unit(boolean val, String val2){
	debug = val;
	id = val2;
    }
    public Unit(boolean debug,
		String _id,
		String _sid,
		String _units,
		String _bedrooms,
		String _baths,
		String _occLoad,
		String _sleepRoom,
		String _uninspected){
	this.debug = debug;
	this.id = _id;
	this.sid = _sid;
	setUnits(_units);
	setBedrooms(_bedrooms);
	setBaths(_baths);
	setOccLoad(_occLoad);
	setSleepRoom(_sleepRoom);
	setUninspected(_uninspected);
    }
    // without baths
    public Unit(boolean debug,
		String _id,
		String _sid,
		String _units,
		String _bedrooms,
		String _occLoad,
		String _sleepRoom,
		String _uninspected){
	this.debug = debug;
	this.id = _id;
	this.sid = _sid;
	setUnits(_units);
	setBedrooms(_bedrooms);
	setOccLoad(_occLoad);
	setSleepRoom(_sleepRoom);
	setUninspected(_uninspected);
    }	
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setSid(String val){
	if(val != null)
	    sid = val;
    }
    public void setSleepRoom(String val){
	if(val != null && !val.equals(""))
	    sleepRoom = "y";
    }
    public void setUninspected(String val){
	if(val != null && !val.equals(""))
	    uninspected = "y";
    }
    public void setUnits(String val){
	if(val != null && !val.equals("0")){
	    try{
		units = Integer.parseInt(val);
	    }catch(Exception ex){}
	}
    }	
    public void setBedrooms(String val){
	if(val != null && !val.equals("0")){
	    try{
		bedrooms = Integer.parseInt(val);
	    }catch(Exception ex){}
	}
    }
    public void setOccLoad(String val){
	if(val != null && !val.equals("0")){
	    try{
		occLoad = Integer.parseInt(val);
	    }catch(Exception ex){}
	}
    }
    public void setBaths(String val){
	if(val != null && !val.equals("0")){
	    try{
		baths = Integer.parseInt(val);
	    }catch(Exception ex){}
	}
    }	
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getSid(){
	return sid;
    }
    public String getSleepRoom(){
	return sleepRoom;
    }
    public String getUninspected(){
	return uninspected;
    }	
    public int getUnits(){
	return units;
    }	
    public int getBedrooms(){
	return bedrooms;
    }
    public int getOccLoad(){
	return occLoad;
    }
    public int getBaths(){
	return baths;
    }
    public boolean isSleepRoom(){
	return !sleepRoom.equals("");
    }
    public boolean isUninspected(){
	return !uninspected.equals("");
    }
    public boolean isInspected(){
	return uninspected.equals("");
    }	
    public String toString(){
	String ret = "";
	if(!id.equals("")){
	    ret += id;
	}
	if(!sid.equals("")){
	    if(!ret.equals("")) ret += " ";	
	    ret += sid;
	}
	if(units > 0){
	    if(!ret.equals("")) ret += " ";
	    ret += units;
	}	
	if(bedrooms > 0){
	    if(!ret.equals("")) ret += " ";
	    ret += bedrooms;
	}
	if(baths > 0){
	    if(!ret.equals("")) ret += " ";
	    ret += baths;
	}		
	if(occLoad > 0){
	    if(!ret.equals("")) ret += " ";
	    ret += occLoad;
	}
	if(!uninspected.equals("")){
	    ret += " (Uninspected)";
	}
	return ret;
    }
    public Structure getStructure(){
	//
	if(structure == null){
	    if(sid.equals("") && !id.equals("")){
		errors = doSelect();
	    }
	    if(!sid.equals("")){
		structure = new Structure(debug, sid);
		String back = structure.doSelect();
		if(!back.equals("")){
		    errors += " "+back;
		}
	    }
	}
	return structure;
    }
    //
    public String doSave(){
		
	Connection con = null;
	PreparedStatement stmt = null, stmt2 = null;
	ResultSet rs = null;
	String back = "";
	if(sid.equals("")){
	    back = " Structure id not set ";
	    return back;
	}
	// it is OK for bedrooms to be 0 (for Efficiency)
	if(units == 0 || occLoad == 0){
	    back = "Units count, bedrooms or occupancy load not set";
	    return back;
	}
	String qq = " select rent_unit_seq.nextval from dual";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		return back;
	    }
	    stmt2 = con.prepareStatement(qq);
	    rs = stmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    qq = "insert into rental_units values("+id+","+sid+",?,?,?,?,?)";
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setInt(1,units);
	    stmt.setInt(2,bedrooms);
	    stmt.setInt(3,occLoad);
	    if(sleepRoom.equals(""))
		stmt.setNull(4,Types.CHAR);
	    else
		stmt.setString(4,"y");
	    if(uninspected.equals(""))
		stmt.setNull(5,Types.CHAR);
	    else
		stmt.setString(5,"y");
	    stmt.executeUpdate();
	}catch(Exception ex){
	    back += ex+":"+qq;			
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}
	return back;
    }
    //
    public String doUpdate(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	if(id.equals("")){
	    back = " id not set";
	    return back;
	}
	String qq = " update rental_units set ";
	qq += " units=?";
	qq += ", bedrooms=?";
	qq += ", occLoad=?";
	qq += ", sleepRoom=? ";
	qq += ", uninspected=? ";
	qq += "where id=?";

	if(debug)
	    logger.debug(qq);	
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.prepareStatement(qq);
		stmt.setInt(1,units);
		stmt.setInt(2,bedrooms);
		stmt.setInt(3,occLoad);
		if(sleepRoom.equals(""))
		    stmt.setNull(4,Types.CHAR);
		else
		    stmt.setString(4,"y");
		if(uninspected.equals(""))
		    stmt.setNull(5,Types.CHAR);
		else
		    stmt.setString(5,"y");
		stmt.setString(6,id); // change to 6
		//
		stmt.executeUpdate();				
	    }
	    else{
		back = "Could not connect to DB ";
	    }
	}catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);			
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    //
    public String doSelect(){
		
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = " select sid,units,bedrooms,occLoad, sleepRoom "+
	    ",uninspected "+
	    " from rental_units where id=?";
	if(debug)
	    logger.debug(qq);
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.prepareStatement(qq);
		stmt.setString(1, id);
		rs = stmt.executeQuery();
		if(rs.next()){
		    sid = rs.getString(1);
		    units = rs.getInt(2);
		    bedrooms = rs.getInt(3);
		    occLoad = rs.getInt(4);
		    String str = rs.getString(5);
		    if(str != null)
			sleepRoom = str;
		    str = rs.getString(6);
		    if(str != null)
			uninspected = str;

		}				
	    }
	    else{
		back = "Could not connect to DB ";
	    }
	}catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
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
	String qq = " delete from rental_units where id=?";
	if(debug)
	    logger.debug(qq);	
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.prepareStatement(qq);
		stmt.setString(1, id);
		stmt.executeUpdate();
	    }
	    else{
		back = "Could not connect to DB ";
	    }
	}catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);			
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
	
}























































