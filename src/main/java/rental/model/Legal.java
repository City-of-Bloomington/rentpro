package rental.model;

import java.util.*;
import java.sql.*;
import java.util.Date;
import java.io.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Legal{

    SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");		
    boolean debug;
    String id="", rental_id="", status="", reason="", 
	startDate="", startBy="", startByName="", 
	attention="", case_id="", pull_date="", pull_reason="",
	errors="";
    final static long serialVersionUID = 510L;
    Logger logger = LogManager.getLogger(Legal.class);
    //
    List<Action> actions = null;
    Case cCase = null;
    //
    // basic constructor
    public Legal(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public Legal(boolean deb, String id){

	debug = deb;
	this.id = id;
	//
	// initialize
	//
    }
    public Legal(boolean deb, String val, String val2, String val3, String val4, String val5, String val6, String val7, String val8, String val9){
				
	debug = deb;
	//
	// initialize
	//
	setVals(val, val2, val3, val4, val5, val6, val7, val8, val9);
    }
    public Legal(boolean deb, String val, String val2, String val3, String val4, String val5, String val6, String val7, String val8, String val9, String val10, String val11){
				
	debug = deb;
	//
	// initialize
	//
	setVals(val, val2, val3, val4, val5, val6, val7, val8, val9, val10, val11);
    }		
    void setVals( String val, String val2, String val3, String val4, String val5, String val6, String val7, String val8, String val9){
	setId(val);
	setReason(val2);
	setStartBy(val3);
	setStartByName(val4);
	setRental_id(val5);
	setStatus(val6);
	setStartDate(val7);
	setAttention(val8);
	setCase_id(val9);
    }
    void setVals( String val, String val2, String val3, String val4, String val5, String val6, String val7, String val8, String val9, String val10, String val11){
	setId(val);
	setReason(val2);
	setStartBy(val3);
	setStartByName(val4);
	setRental_id(val5);
	setStatus(val6);
	setStartDate(val7);
	setAttention(val8);
	setCase_id(val9);
	setPull_date(val10);
				
	setPull_reason(val11);
    }		
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setCase_id(String val){
	if(val != null)
	    case_id = val;
    }
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }
    public void setStatus(String val){
	if(val != null)
	    status = val;
    }
    public void setStartBy(String val){
	if(val != null)
	    startBy = val;
    }
    public void setStartByName(String val){
	if(val != null)
	    startByName = val;
    }		
    public void setStartDate(String val){
	if(val != null)
	    startDate = val;
    }	
    public void setReason(String val){
	if(val != null)
	    reason = val;
    }
		
    public void setAttention(String val){
	if(val != null)
	    attention = val;
    }
    public void setPull_reason(String val){
	if(val != null)
	    pull_reason = val;
    }
    public void setPull_date(String val){
	if(val != null)
	    pull_date = val;
    }		
    //
    // getters
    //
    public String  getId(){
	return id;
    }
    public String  getRental_id(){
	return rental_id;
    }
    public String  getCase_id(){
	return case_id;
    }
    public String  getReason(){
	return reason;
    }
    public String  getStatus(){
	return status;
    }
    public String  getStartBy(){
	return startBy;
    }
    public String  getStartByName(){
	return startByName;
    }
    public String  getStartDate(){
	return startDate;
    }
    public String  getErrors(){
	return errors;
    }
    public String  getPull_reason(){
	return pull_reason;
    }
    public String  getPull_date(){
	return pull_date;
    }		
    public String  getAttention(){
	return attention;
    }
    public Case getCase(){
	if(!case_id.equals("") && cCase == null){
	    Case cc = new Case(case_id, debug);
	    String back = cc.doSelect();
	    if(back.equals("")){
		cCase = cc;
	    }
	    else{
		logger.error(back);
	    }
	}
	return cCase;
    }
    public List<Action>  getActions(){
	return actions;
    }
    //
    public String doSave(){
		
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null, stmt2=null;
	ResultSet rs = null;
		
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	String qq = "insert into rental_legals values(0,now(),?,?,?, ?,?,?)";
	if(debug){
	    logger.debug(qq);
	}
	try{
			
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1,rental_id);
	    stmt.setString(2,reason);
	    stmt.setString(3,startBy);			
	    stmt.setString(4,status);			
	    stmt.setString(5,attention);
	    stmt.setString(6,case_id);
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
	if(reason.equals("")) return back;
	String qq = "update rental_legals set reason=? where id=?";
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, reason);
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
	doSelect();
	return back;
    }
    //
    public String doUpdate(String newStatus, String newAttention){
		
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	boolean needUpdate = false;
	String qq = "update rental_legals set ";
	if(newStatus != null && !status.equals(newStatus)){
	    qq += "status = ?";
	    needUpdate = true;
	}
	if(newAttention != null && !attention.equals(newAttention)){
	    if(needUpdate) qq += ", ";
	    qq += "attention = ?";
	    needUpdate = true;
	}
	if(needUpdate){
	    qq += " where id=?";		
	    con = Helper.getLegalConnection();
	    if(con == null){
		back = "Could not connect to DB";
		return back;
	    }
	    try{
		int jj=1;
		stmt = con.prepareStatement(qq);
		if(newStatus != null && !status.equals(newStatus)){
		    stmt.setString(jj++, newStatus);
		}
		if(newAttention != null && !attention.equals(newAttention)){
		    stmt.setString(jj++, newAttention);
		}
		stmt.setString(jj++, id);
		stmt.executeUpdate();
	    }
	    catch(Exception ex){
		back += ex;
		logger.error(ex);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	doSelect();
	return back;
    }
	
    public String doUpdate(String _case_id){
		
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
		
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	if(_case_id == null || _case_id.equals("")) return "";
	case_id = _case_id;		
	String qq = "update rental_legals set ";		
	qq += "case_id = ? ";
	qq += " where id=?";
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, case_id);
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
	doSelect();
	return back;

    }
	
    public String doCloseCase(){

	String qq = "update legal_cases set ";
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}		
	if(!case_id.equals("")){
	    qq += "status ='CL'";
	    qq += " where id=?";
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		stmt = con.prepareStatement(qq);
		stmt.setString(1, case_id);
		stmt.executeUpdate();
	    }
	    catch(Exception ex){
		logger.error(ex);
		back += ex;
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);
	    }
	}
	doSelect();
	return back; // success
		
    }
	
    //
    // from case info
    //
    public String getViolationType(){

	String qq = "select t.typeDesc from legal_case_types t, legal_cases c where t.typeId=c.case_type and c.id=?";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String back = "";
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}				
	String type="";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, case_id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null)
		    type = str;
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return type; // success
		
    }
    //
    // This rarely will be needed
    // 
    public String doDelete(){
	//
	// System.err.println("delete record");
	//
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = "delete from  rental_legals where id=?";
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
	    logger.error(ex);
	    return ex.toString();
	}
	return back;
    }
    //
	
    public String doSelect(){
		
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	String qq = " select l.id,"+
	    " l.reason,"+
	    " l.startBy,"+
	    " u.fullName,"+
	    " l.rental_id,"+
						
	    " l.status,"+
	    " date_format(l.startDate,'%m/%d/%Y'), "+
	    " l.attention, "+
	    " l.case_id "+
	    //" date_format(l.pull_date,'%m/%d/%Y'), "+
	    //" l.pull_reason "+
	    " from rental_legals l "+
	    " left join users u on l.startBy = u.userid "+
	    " where id=?";		
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
		setVals(id,
			rs.getString(2),
			rs.getString(3),
			rs.getString(4),
			rs.getString(5),
			rs.getString(6),  
			rs.getString(7),
			rs.getString(8),
			rs.getString(9));
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
	if(back.equals("")){
	    ActionList al = new ActionList(debug);
	    al.setLegal_id(id);
	    back += al.lookFor();
	    if(back.equals("")){
		actions = al.getActions();
	    }
	    else{
		logger.error(back);
	    }
	}
	return back;
    }
	

}






















































