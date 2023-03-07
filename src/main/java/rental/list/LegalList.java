package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;

public class LegalList extends ArrayList<Legal>{

    boolean debug = false;
    final static long serialVersionUID = 540L;
    String id="",reason="",startBy="",status="", rental_id="";
    String dateFrom="",dateTo="", whichDate="", attention="", case_id="";
    String sortby="";
    //  List<Legal> legals = null;
    List<String> starters = null;
    Logger logger = LogManager.getLogger(LegalList.class);
    //
    // basic constructor
    public LegalList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public LegalList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	setRental_id(val);
    }	
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }
    public void setCase_id(String val){
	if(val != null)
	    case_id = val;
    }
    public void setStartBy(String val){
	startBy = val;
    }
    public void setStatus(String val){
	status = val;
    }
    public void setAttention(String val){
	attention = val;
    }
    public void setDateFrom(String val){
	dateFrom = val;
    }
    public void setDateTo(String val){
	dateTo = val;
    }
    public void setReason(String val){
	reason = val;
    }
    public void setWhichDate(String val){
	whichDate = val;
    }
    public void setSortbyLast(){
	sortby = " order by l.id desc "; 
    }
    //
    // getters
    //
    public List<Legal>  getLegals(){
	return this;
    }
    public List<String>  getStarters(){
	return starters;
    }
    //
    public String lookFor(){
	//
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "select l.id, "+		
	    " l.reason,"+
	    " l.startBy,"+
	    " u.fullName,"+
	    " l.rental_id,"+
						
	    " l.status,"+
	    " date_format(l.startDate,'%m/%d/%Y'), "+
	    " l.attention, "+
	    " l.case_id "+
						
	    " from rental_legals l "+
	    " left join users u on u.userid=l.startBy ";

	String where = "";
	if(!id.equals("")){
	    where = "l.id=?";// +id;
	}
	if(!rental_id.equals("")){
	    if(!where.equals("")) where += " and ";
	    where = "l.rental_id=? ";// +rental_id;
	}
	if(!case_id.equals("")){
	    if(!where.equals("")) where += " and ";
	    where = "l.case_id=?";// +case_id;
	}
	if(!startBy.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += " l.startBy like ? ";//'"+startBy+"'";
	}
	if(!attention.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += " l.attention = ?";// '"+attention+"'";
	}
	if(!reason.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += " l.reason like ? ";// '%"+reason+"%'";
	}
	if(!status.equals("")){
	    if(!where.equals("")) where += " and ";
	    if(status.equals("both")) // need this for legal dept
		// where += "(status='New' or status='Pending')";
		where += "(l.status=? or l.status=?)";			
	    else
		where += "l.status=?";
	}
	if(!dateFrom.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += "l.startDate >=str_to_date('"+dateFrom+"','%m/%d/%Y')";
	}
	if(!dateTo.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += "l.startDate <=str_to_date('"+dateTo+"','%m/%d/%Y')";
	}
	if(!where.equals("")){
	    qq += " where "+where;
	}
	if(!sortby.equals("")){
	    qq += sortby;
	}
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    int jj=1;
	    stmt = con.prepareStatement(qq);
	    if(!id.equals("")){
		stmt.setString(jj++, id);	
	    }
	    if(!rental_id.equals("")){
		stmt.setString(jj++, rental_id);	
	    }
	    if(!case_id.equals("")){
		stmt.setString(jj++, case_id);	
	    }
	    if(!startBy.equals("")){
		stmt.setString(jj++, startBy);				
	    }
	    if(!attention.equals("")){
		stmt.setString(jj++, attention);		
	    }
	    if(!reason.equals("")){
		stmt.setString(jj++, "%"+reason+"%");		
	    }
	    if(!status.equals("")){
		if(status.equals("both")){ // need this for legal dept
		    stmt.setString(jj++, "New");
		    stmt.setString(jj++, "Pending");	
		}
		else
		    stmt.setString(jj++, status);
	    }
	    rs = stmt.executeQuery();
	    while(rs.next()){
		Legal one =
		    new Legal(debug,
			      rs.getString(1),
			      rs.getString(2),
			      rs.getString(3),
			      rs.getString(4),
			      rs.getString(5),
			      rs.getString(6),
			      rs.getString(7),
			      rs.getString(8),
			      rs.getString(9));
		add(one);
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}		
	return back;
    }
	

    public String findStarters(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	starters = new ArrayList<String>();
	String qq = " select distinct startBy from rental_legals order by startBy ";		
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    stmt = con.createStatement();
	    if(debug)
		logger.debug(qq);
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		String str = rs.getString(1);
		if(str != null) starters.add(str);
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
    //	

}






















































