package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class ZoneList extends ArrayList<Zone>{

    boolean debug;
    final static long serialVersionUID = 1060L;
    static Logger logger = LogManager.getLogger(ZoneList.class);
    List<Item> zones;
    //
    // basic constructor
    public ZoneList(boolean deb){

	debug = deb;
	//
    }
    //
    // setters
    //
    public List<Zone>  getZones(){
	return this;
    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	String qq = "select zoned,initcap(zone_text) from zoning_2007 order by zoned";
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.prepareStatement(qq);
	    rs = stmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		Zone zone = new Zone(debug, str, str2);
		add(zone);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
}






















































