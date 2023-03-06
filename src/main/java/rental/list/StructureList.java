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


public class StructureList extends ArrayList<Structure>{

    boolean debug;
    String rid="";
    final static long serialVersionUID = 940L;
    static Logger logger = LogManager.getLogger(StructureList.class);
    String errors = "";
    String bedDescrp = "";
    String occLoadDescrp = "";
    boolean uninspections = false;
    int totalUnits = 0;
    int totalItems = 0;
    //
    // basic constructor
    public StructureList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public StructureList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	this.rid = val;
    }
    //
    // setters
    //
    public void setRid(String val){
	if(val != null && !val.equals(""))
	    rid = val;
    }
    public boolean hasUninspections(){
	if(totalUnits == 0){
	    findMisc();
	}
	return uninspections;
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
	String qq = "select id,rid,identifier from rental_structures ";
	String qw = "";
	if(!rid.equals("")){
	    qw += " rid=?";
	}
	if(!qw.equals(""))
	    qq += " where "+qw;
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    if(!rid.equals("")){
		stmt.setString(1, rid);
	    }
	    rs = stmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);
		Structure one = new Structure(debug, str, str2, str3);
		add(one);
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
    public int getTotalUnits(){
	if(totalUnits == 0){
	    findMisc();
	}
	return totalUnits;
    }
    public String getBedDescrp(){
	if(bedDescrp.equals("")){
	    findMisc();
	}
	return bedDescrp;
    }
    public String getOccLoadDescrp(){
	if(occLoadDescrp.equals("")){
	    findMisc();
	}
	return occLoadDescrp;
    }
    /**
     * find total units's group in this permit
     */
    public int getTotalItems(){
	if(totalItems == 0){
	    findMisc();
	}
	return totalItems;
    }
    //
    public void findMisc(){
	if(this.size() == 0){
	    if(!rid.equals("")){
		errors = find();
	    }
	}
	if(this.size() == 1){
	    Structure strc = this.get(0);
	    totalItems += strc.getTotalItems();
	    UnitList units = strc.getUnits();
	    if(units.size() == 1){
		if(units.hasUninspections()){
		    uninspections = true;
		}
		Unit unit = units.get(0);
		int beds = unit.getBedrooms();
		occLoadDescrp += ""+unit.getUnits();
		if(beds > 0){
		    bedDescrp += ""+unit.getBedrooms();
		    occLoadDescrp += "/"+beds;
		}
		else{
		    occLoadDescrp += "/Eff";
		    bedDescrp += "Eff";
		}
		totalUnits = unit.getUnits();
		occLoadDescrp += "/"+unit.getOccLoad();
	    }
	    else if(units.size() > 1){
		if(units.hasUninspections()){
		    uninspections = true;
		}
		for(Unit unit:units){
		    totalUnits += unit.getUnits();
		    if(!bedDescrp.equals("")) bedDescrp += " ";
		    if(!occLoadDescrp.equals("")) occLoadDescrp += " ";
		    occLoadDescrp += ""+unit.getUnits();					
		    int beds = unit.getBedrooms();
		    if(beds > 0){
			bedDescrp += ""+unit.getUnits()+"/"+unit.getBedrooms();
			occLoadDescrp += "/"+beds;
		    }
		    else{
			bedDescrp += ""+unit.getUnits()+"/Eff";
			occLoadDescrp += "/Eff";
		    }
		    occLoadDescrp += "/"+unit.getOccLoad();
		}

	    }
	}
	else {
	    for(Structure strc:this){
		totalItems += strc.getTotalItems();
		UnitList units = strc.getUnits();
		if(units != null && units.size() > 0){
		    if(units.hasUninspections()){
			uninspections = true;
		    }
		    if(!bedDescrp.equals("")) bedDescrp += ", ";
		    if(!occLoadDescrp.equals("")) occLoadDescrp += ", ";
		    bedDescrp += "Bld "+strc.getIdentifier()+": ";
		    occLoadDescrp += "Bld "+strc.getIdentifier()+": ";
					
		    for(Unit unit:units){
			totalUnits += unit.getUnits();
			if(!bedDescrp.equals("")) bedDescrp += " ";
			if(!occLoadDescrp.equals("")) occLoadDescrp += " ";
			occLoadDescrp += ""+unit.getUnits();
			int beds = unit.getBedrooms();
			if(beds > 0){
			    bedDescrp += ""+unit.getUnits()+"/"+unit.getBedrooms();
			    occLoadDescrp += "/"+beds;
			}
			else{
			    bedDescrp += ""+unit.getUnits()+"/Eff";
			    occLoadDescrp += "/Eff";
			}
			occLoadDescrp += "/"+unit.getOccLoad();
		    }
		}
	    }
	}
    }

}






















































