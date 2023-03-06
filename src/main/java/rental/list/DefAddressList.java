package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class DefAddressList{

    boolean debug;
    String id="", defId="", rental_addr="";
    final static long serialVersionUID = 220L;
    static Logger logger = LogManager.getLogger(DefAddressList.class);	
    String dateFrom="",dateTo="";
    List<DefAddress> addresses = null;
    //
    // basic constructor
    public DefAddressList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public DefAddressList(boolean deb, String defId){

	debug = deb;
	//
	// initialize
	//
	this.defId = defId;
    }
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
    public void setDefId(String val){
	if(val != null && !val.equals(""))
	    defId = val;
    }
    public void setRental_addr(String val){
	if(val != null && !val.equals(""))
	    rental_addr = val;
    }
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public List<DefAddress>  getAddresses(){
	return addresses;
    }
    //
    public String lookFor(){
	//
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;				
	String back = "";
	String qq = "select id,defId,"+
	    "street_num,street_dir,street_name,street_type,"+
	    "sud_type,sud_num,post_dir, "+
	    "invalid_addr,city,state,zip "+
	    // ,streetAddress "+
	    "from legal_def_addresses ";		
	String qw = "";
	if(!defId.equals("")){
	    qw += " defId=?";
	}
	if(!rental_addr.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " rental_addr = 'Y'";
	}
	if(!qw.equals(""))
	    qq += " where "+qw;
	String message = "";
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
	    if(!defId.equals("")){
		stmt.setString(1, defId);
	    }
	    rs = stmt.executeQuery();
	    addresses = new ArrayList<DefAddress>();			
	    while(rs.next()){
		String str   = rs.getString(1); // id
		String str2  = rs.getString(2); // defId
		String str3  = rs.getString(3);
		String str4  = rs.getString(4);
		String str5  = rs.getString(5);
		String str6  = rs.getString(6);
		String str7  = rs.getString(7);
		String str8  = rs.getString(8);
		String str9  = rs.getString(9);
		String str10  = rs.getString(10);
		String str11  = rs.getString(11);
		String str12  = rs.getString(12);
		String str13 = rs.getString(13);
		String str14 = null;//  rs.getString(14);
						
						
		DefAddress addr = new DefAddress(debug,
						 str,str2,str3,str4,str5,
						 str6,str7,str8,str9,str10,
						 str11,str12, str13, str14);
		addresses.add(addr);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return message;
    }

}






















































