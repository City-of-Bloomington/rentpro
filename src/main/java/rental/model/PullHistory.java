package rental.model;
import java.util.*;
import java.util.Vector;
import java.sql.*;
import java.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class PullHistory{

    String id="", pull_date="", userid="", pull_reason="", rental_id="";
    String pull_text = ""; // with join
    final static long serialVersionUID = 627L;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    boolean debug = false;
    String errors = "";
    User user = null;
    static Logger logger = LogManager.getLogger(PullHistory.class);

    public PullHistory(){
		
    }
    public PullHistory(boolean deb){
	debug = deb;
		
    }	
    public PullHistory(boolean deb, String val){
	debug = deb;
	setId(val);
    }
    public PullHistory(boolean deb,
		       String val,
		       String val2,
		       String val3,
		       String val4,
		       String val5
		       ){
	debug = deb;		
	setId(val);
	setRental_id(val2);
	setPull_date(val3);
	setPull_reason(val4);
	setUserid(val5);
    }
    public PullHistory(boolean deb,
		       String val,
		       String val2,
		       String val3,
		       String val4,
		       String val5,
		       String val6
		       ){
	debug = deb;		
	setId(val);
	setRental_id(val2);
	setPull_date(val3);
	setPull_reason(val4);
	setUserid(val5);
	setPull_text(val6);
    }		

    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }
    public void setPull_date(String val){
	if(val != null)
	    pull_date = val;
    }
    public void setPull_reason(String val){
	if(val != null)
	    pull_reason = val;
    }
    public void setUserid(String val){
	if(val != null)
	    userid = val;
    }
    public void setPull_text(String val){
	if(val != null)
	    pull_text = val;
    }
    public void setErrors(String val){
	if(val != null){
	    if(!errors.equals("")) errors += ", ";
	    errors += val;
	}
    }		
    //
    // getters
    //
    public String getId(){
	return id;
    }	
    public String getRental_id(){
	return rental_id;
    }
    public String getPull_date(){
	return pull_date;
    }
    public String getPull_reason(){
	return pull_reason;
    }
    public String getPull_text(){
	return pull_text;
    }		
    public String getUserid(){
	return userid;
    }
    public String getErrors(){
	return errors;
    }
    public User getUser(){
	if(user == null && !userid.equals("")){
	    User one = new User(userid);
	    String back = one.doSelect();
	    if(back.equals(""))
		user = one;
	}
	return user;
    }
    public boolean hasUser(){
	getUser();
	return user != null;
    }
    public boolean hasErrors(){
	return !errors.equals("");
    }		
    public boolean isValid(){
	return !pull_reason.equals("");
    }
    /**
     * isNew if the record does not exist
     * count == 0 ?
     */
    public boolean isNew(){
	String back = "";
	String qq = "select count(*) from pull_history "+
	    "where rental_id=? and pull_date=? and pull_reason=? ";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	int nct = 0;
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	if(pull_date.equals("")){
	    pull_date = Helper.getToday();
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    setErrors(back);
	    return false;
	}		
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, rental_id);
	    pstmt.setDate(2, new java.sql.Date(dateFormat.parse(pull_date).getTime()));
	    pstmt.setString(3, pull_reason);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		nct = rs.getInt(1);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return nct == 0;								
				
    }
    public String doSelect(){
	String back = "";
	String qq = "select ph.rental_id,to_char(ph.pull_date,'MM/DD/YYYY'),ph.pull_reason,ph.username,p.pull_text "+
	    "from pull_history ph, pull_reas pl where ph.id=? and "+
	    " ph.pull_reason=pl.p_reason";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		setRental_id(rs.getString(1));
		setPull_date(rs.getString(2));
		setPull_reason(rs.getString(3));
		setUserid(rs.getString(4));
		setPull_text(rs.getString(5));
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;		
    }

    public String doSave(){
	String back = "";
	String qs = "select pull_hist_seq.nextval from dual";
	String qq = "insert into pull_history values(?,?,?,?,?)";
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	if(pull_reason.equals("") && rental_id.equals("")){
	    back = "No pull_reason to be saved";
	}
	if(rental_id.equals("")){
	    back += " Rental id not set";
	}
	if(userid.equals("")){
	    back += " User id not set";
	}
	if(!back.equals("")){
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	try{

	    pstmt2 = con.prepareStatement(qs);
	    rs = pstmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    pstmt.setString(jj++, id);
	    pstmt.setString(jj++, rental_id);
	    if(pull_date.equals("")){
		pull_date = Helper.getToday();
	    }
	    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(pull_date).getTime()));										
	    pstmt.setString(jj++, pull_reason);
	    pstmt.setString(jj++, userid);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, pstmt, pstmt2);
	}
	return back;	
    }

}
