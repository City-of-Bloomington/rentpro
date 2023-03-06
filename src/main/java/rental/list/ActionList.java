package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;

public class ActionList{

    boolean debug;
    final static long serialVersionUID = 20L;
    static Logger logger = LogManager.getLogger(ActionList.class);
    String legal_id="",notes="",actionBy="",type="";
    String dateFrom="",dateTo="";
	
    List<Action> actions = null;
    //
    // basic constructor
    public ActionList(boolean deb){

	debug = deb;
    }
    //
    // setters
    //
    public void setLegal_id(String val){
	legal_id = val;
    }
    public void setActionBy(String val){
	actionBy = val;
    }
    public void setType(String val){
	type = val;
    }
    public void setDateFrom(String val){
	dateFrom = val;
    }
    public void setDateTo(String val){
	dateTo = val;
    }
    public void setActions(List<Action> val){
	actions = val;
    }
    //
    // getters
    //
    public String  getLegal_id(){
	return legal_id;
    }
    public List<Action> getActions(){
	return actions;
    }
    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
	
    public String lookFor(){
	//
	String back = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;		
	String qq = "select id,"+
	    "notes,"+
	    "actionBy,"+
	    "legal_id,"+
	    "date_format(actionDate,'%m/%d/%Y') "+
	    " from legal_actions ";
	con = Helper.getLegalConnection();		
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	String where = "";
	if(!legal_id.equals("")){
	    where = "legal_id=?";
	}
	if(!actionBy.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += " actionBy like ?";
	}
	if(!notes.equals("")){
	    if(!where.equals("")) where += " and ";
	    where += " notes like ?";
	}
	if(!dateFrom.equals("")){
	    if(!where.equals("")) where += " and ";
	    where = "actionDate >=str_to_date('"+dateFrom+"','%m/%d/%Y')";
	}
	if(!dateTo.equals("")){
	    if(!where.equals("")) where += " and ";
	    where = "actionDate <=str_to_date('"+dateTo+"','%m/%d/%Y')";
	}
	if(!where.equals("")){
	    qq += " where "+where;
	}
	qq += " order by id DESC ";
		
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    int jj=1;
	    stmt = con.prepareStatement(qq);			
	    if(!legal_id.equals("")){
		stmt.setString(jj++, legal_id);
	    }
	    if(!actionBy.equals("")){
		stmt.setString(jj++, actionBy);
	    }
	    if(!notes.equals("")){
		stmt.setString(jj++, "%"+notes+"%");
	    }
	    if(!type.equals("")){
		stmt.setString(jj++, type);
	    }
	    String str = "", str2="",str3="",str4="",str5="";
	    rs = stmt.executeQuery();
	    while(rs.next()){
		str = rs.getString(1);
		str2 = rs.getString(2);
		str3 = rs.getString(3);
		str4 = rs.getString(4);
		str5 = rs.getString(5);
		if(actions == null)
		    actions = new ArrayList<Action>();
		Action action = new Action(debug, str, str2, str3,str4,str5);
		actions.add(action);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back = ex+":"+qq;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }


}






















































