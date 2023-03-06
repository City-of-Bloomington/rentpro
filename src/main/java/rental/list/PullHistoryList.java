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

public class PullHistoryList extends ArrayList<PullHistory>{

    boolean debug = false;
    final static long serialVersionUID = 770L;
    static Logger logger = LogManager.getLogger(PullHistoryList.class);
    String rental_id = "";
    //
    // basic constructor
    public PullHistoryList(boolean deb, String val){

	debug = deb;
	setRental_id(val);
	//
    }
    //
    // setters
    //
    public List<PullHistory> getHistoryPulls(){
	return this;
    }
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	String qq = "";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	/*
	  qq = " select to_char(ph.pull_date,'mm/dd/yyyy'),"+
	  " initcap(pl.pull_text) "+
	  " from rental_pull_hist ph, pull_reas pl "+
	  " where ph.id=? and ph.pull_reason=pl.p_reason and "+
	  " rownum < 10 "+
	  " order by ph.pull_date DESC ";
	*/
	qq = " select * from ( "+
	    " select ph.id,ph.rental_id,to_char(ph.pull_date,'mm/dd/yyyy'),"+
	    " ph.pull_reason,ph.username,initcap(pl.pull_text) "+
	    " from pull_history ph, pull_reas pl "+
	    " where ph.rental_id=? and ph.pull_reason=pl.p_reason "+
	    " order by ph.id DESC ) "+
	    " where rownum < 10 ";
				
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
	    stmt.setString(1, rental_id);
	    rs = stmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);
		String str4 = rs.getString(4);
		String str5 = rs.getString(5);
		String str6 = rs.getString(6);				
		PullHistory one = new PullHistory(debug, str, str2, str3, str4, str5, str6);
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
}






















































