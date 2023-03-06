package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;
/**
 *
 */

public class OwnerList extends ArrayList<Owner>{

    boolean debug;
    String registr_id=""; // registr_id from rental
    String name_num="", fullName="", address="", city="",
	state="", zip="", phone="", 
	confirm_status = "",
	email="", withEmailOnly="", noEmail="", notes="";
    String name_opt="", addr_opt="";
    final static long serialVersionUID = 640L;
    Logger logger = LogManager.getLogger(OwnerList.class);
    //
    // for to expire permits options
    //
    String startDate="", endDate="";
    boolean soonToExpire = false, ownersOnly= false, agentsOnly=false;
    boolean activeRent = false;
    //
    // basic constructor
    public OwnerList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public OwnerList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	this.registr_id = val;
    }
    public void setName_num (String val){
	if(val != null)
	    name_num = val;
    }
    public void setFullName (String val){
	if(val != null)
	    fullName = val.toUpperCase();
    }
    public void setAddress (String val){
	if(val != null)
	    address = val.toUpperCase();
    }
    public void setCity (String val){
	if(val != null)
	    city = val.toUpperCase();
		
    }
    public void setState (String val){
	if(val != null)
	    state = val.toUpperCase();
    }
    public void setZip (String val){
	if(val != null)
	    zip = val;
    }
    public void setEmail (String val){
	if(val != null)
	    email = val.toLowerCase();
    }
    public void setPhone (String val){
	if(val != null)
	    phone = val;
    }
    public void setConfirmStatus (String val){
	if(val != null)
	    confirm_status = val;
    }
		
    public void setNotes (String val){
	if(val != null)
	    notes = val;
    }	
    public void setWithEmailOnly(){
	withEmailOnly = "y";
    }
    public void setNoEmail(){
	noEmail = "y";
    }
    public void setOwnersOnly(){
	ownersOnly = true;
    }
    public void setAgentsOnly(){
	agentsOnly = true;
    }	
    public void setNameOpt(String val){
	if(val != null)
	    name_opt = val;
    }
    public void setAddrOpt(String val){
	if(val != null)
	    addr_opt = val;
    }
    public void setStartDate(String val){
	if(val != null)
	    startDate = val;
    }
    public void setEndDate(String val){
	if(val != null)
	    endDate = val;
    }
    public void setSoonToExpire(){
	soonToExpire = true;
    }
    public void setActiveRent(){
	activeRent = true;
    }
    //
    // setters
    //
    public void setRegistr_id(String val){
	if(val != null)
	    registr_id = val;
    }
    public void setId(String val){
	if(val  != null)
	    registr_id = val;
    }	
    //
    // search for rental owners
    //
    public String lookFor(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;	
	ResultSet rs = null;
	String q = "select o.name_num,initcap(o.name),initcap(o.address),initcap(o.city),"+
	    "o.state,o.zip,o.phone_home,o.phone_work,o.notes,o.email, "+
	    "o.unconfirmed "+
	    "from name o ";		
	String back = "", qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	try{
	    qq = setQueryOptions(q);
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    back = setStatementData(pstmt);
	    if(back.equals("")){
		rs = pstmt.executeQuery();			
		while(rs.next()){
		    Owner owner = new Owner(debug,
					    rs.getString(1),
					    rs.getString(2),
					    rs.getString(3),
					    rs.getString(4),
					    rs.getString(5),
					    rs.getString(6),
					    rs.getString(7),
					    rs.getString(8),
					    rs.getString(9),
					    rs.getString(10),
					    rs.getString(11));
		    if(!this.contains(owner))
			add(owner);
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
    //
    // search for rental owners
    // this is shared between the two find methods
    //
    public String setQueryOptions(String q){
	//
	String qw = "where not (o.name_num = 0 or o.name_num=6010) ";
	String qo = " order by o.name ";
	String back = "";
	String qq = q;
	if(!registr_id.equals("")){
	    if(ownersOnly){
		qq += ", registr rr ";								
		qq += ", regid_name rn ";
		qw += " and rr.id=rn.id and o.name_num = rn.name_num and rn.id=? ";
	    }
	    else if(agentsOnly){
		qq += ", registr rr ";
		qw += " and o.name_num = rr.agent and rr.id=? ";								
	    }
	    else{
		qq += ", regid_name rn ";
		qw += " and o.name_num = rn.name_num and rn.id=? ";
	    }
	}
	else if(ownersOnly){
	    qq += ", regid_name rn, registr rr ";
	    qw += " and o.name_num = rn.name_num ";
	    qw += " and rr.id = rn.id ";				
	}
	else if(agentsOnly){
	    qq += ", registr rr ";						
	    qw += " and o.name_num = rr.agent ";	
	}
	// looking for permits that will expire soon
	if(soonToExpire){
	    if(qq.indexOf("registr") == -1){
		qq += ", registr rr ";
		qq += ", regid_name rn ";								
		if(!qw.equals("")) qw += " and ";
		qw += " rr.id=rn.id and (rn.name_num = o.name_num or o.name_num=rr.agent)";
	    }
	    qw += "and rr.inactive is null "
		+ "and rr.property_status = 'R' ";
	    if(!startDate.equals("")){
		qw += "and rr.permit_expires >= "
		    + "to_date('"+startDate+"','mm/dd/yyyy') ";
	    }
	    if(!endDate.equals("")){
		qw += "and rr.permit_expires <= "
		    + "to_date('"+endDate+"','mm/dd/yyyy') ";
	    }
	}
	else if(activeRent){
	    if(qq.indexOf("registr") == -1){
		qq += ", registr rr ";
		qq += ", regid_name rn ";
		if(!qw.equals("")) qw += " and ";
		qw += " rr.id=rn.id ";
	    }
	    if(!qw.equals("")) qw += " and ";						
	    qw += " rr.inactive is null "
		+ "and rr.property_status = 'R' ";
	    if(!ownersOnly && !agentsOnly){ // any owner or agent
		qw += " and ((o.name_num = rn.name_num ";
		qw += " ) or  ";				
		qw += " (o.name_num = rr.agent)) ";
	    }
	}
	if(!name_num.equals("")){
	    qw += " and o.name_num = ?";
	}
	if(!fullName.equals("")){
	    qw += " and upper(o.name) like ?";
	}
	if(!confirm_status.equals("")){
	    if(confirm_status.equals("Confirmed"))
		qw += " and o.uncofirmed is null ";
	    else
		qw += " and o.uncofirmed is not null ";								
	}				
	if(!address.equals("")){
	    qw += " and upper(o.address) like ?";
	}
	if(!city.equals("")){
	    qw += " and o.city like ?";
	}
	if(!state.equals("")){
	    qw += " and o.state like ?";
	}
	if(!zip.equals("")){
	    qw += " and o.zip like ?";
	}
	if(!phone.equals("")){
	    qq += ", owner_phones op ";
	    qw += " and o.name_num= op.name_num and op.phone_num like ? ";
	}
	if(!email.equals("")){
	    qw += " and lower(o.email) like ?";
	}
	if(!notes.equals("")){
	    qw += " and o.notes like ?";
	}	
	if(!withEmailOnly.equals("")){
	    qw += " and o.email is not null ";
	}
	else if(!noEmail.equals("")){
	    qw += " and o.email is null ";
	}
	qq += qw + qo;
	return qq;
    }
    String setStatementData(PreparedStatement pstmt){
	String back = "";
	try{
	    int jj = 1;
	    if(!registr_id.equals("")){
		pstmt.setString(jj++, registr_id);
	    }
	    if(!name_num.equals("")){
		pstmt.setString(jj++, name_num);
	    }
	    if(!fullName.equals("")){
		pstmt.setString(jj++, "%"+fullName+"%");
	    }
	    if(!address.equals("")){
		pstmt.setString(jj++, "%"+address+"%");
	    }
	    if(!city.equals("")){
		pstmt.setString(jj++, city);
	    }
	    if(!state.equals("")){
		pstmt.setString(jj++, state);
	    }
	    if(!zip.equals("")){
		pstmt.setString(jj++, zip+"%");
	    }
	    if(!phone.equals("")){ 
		pstmt.setString(jj++, phone);
	    }
	    if(!email.equals("")){
		pstmt.setString(jj++, "%"+email+"%");
	    }
	    if(!notes.equals("")){
		pstmt.setString(jj++, "%"+notes+"%");
	    }	
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	return back;
    }
    /**
     * needed to create list of owners/agents using id and name only
     */
    public String findAbbreviated(){
		
	Connection con = null;
	PreparedStatement pstmt = null;	
	ResultSet rs = null;
	String q = "select o.name_num,initcap(o.name) from name o ";		
	String back = "", qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	try{
	    qq = setQueryOptions(q);
	    pstmt = con.prepareStatement(qq);
	    back = setStatementData(pstmt);
	    if(back.equals("")){
		rs = pstmt.executeQuery();
		while(rs.next()){
		    Owner owner = new Owner(debug,
					    rs.getString(1),
					    rs.getString(2));
		    if(!this.contains(owner))
			add(owner);
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
	
}






















































