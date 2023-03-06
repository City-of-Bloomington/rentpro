package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class LegalAddressList{

    boolean debug;
    String id="", caseId="", rental_addr="";
    final static long serialVersionUID = 530L;
    static Logger logger = LogManager.getLogger(LegalAddressList.class);	
    String dateFrom="",dateTo="";
    List<LegalAddress> addresses = null;
    //
    // basic constructor
    public LegalAddressList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public LegalAddressList(boolean deb, String caseId){

	debug = deb;
	//
	// initialize
	//
	this.caseId = caseId;
    }
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.equals(""))
	    id = val;
    }
    public void setCase_id(String val){
	if(val != null && !val.equals(""))
	    caseId = val;
    }
    public void setCaseId(String val){
	if(val != null && !val.equals(""))
	    caseId = val;
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
    public List<LegalAddress>  getAddresses(){
	return addresses;
    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    public String lookFor(){
	//
	String back;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String qq = "select id,caseId, "+
	    " street_num,street_dir,"+
	    " street_name,street_type,"+
	    " post_dir, sud_type,"+
	    " sud_num,invalid_addr,"+
	    " rental_addr "+
	    // ,streetAddress "+
	    "from legal_addresses ";
	String qw = "";
	if(!caseId.equals("")){
	    qw += " caseId="+caseId;
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
	else{
	    try{
		stmt = con.createStatement();
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		while(rs.next()){
		    if(addresses == null)
			addresses = new ArrayList<LegalAddress>();
		    String str   = rs.getString(1);
		    String str2  = rs.getString(2);
		    String str3  = rs.getString(3);
		    String str4  = rs.getString(4);
		    String str5  = rs.getString(5);
		    String str6  = rs.getString(6);
		    String str7  = rs.getString(7);
		    String str8  = rs.getString(8);
		    String str9  = rs.getString(9);
		    String str10 = rs.getString(10);
		    String str11 = rs.getString(11);
		    // String str12 = rs.getString(12);
		    LegalAddress addr =
			new LegalAddress(debug,
					 str, str2, str3,
					 str4,str5,str6,
					 str7,str8,str9,str10,
					 str11,null);
		    addresses.add(addr);
		}
	    }
	    catch(Exception ex){
		logger.error(ex+" : "+qq);
		return ex.toString();
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);		
	    }
	}
	return message;
    }
	
}






















































