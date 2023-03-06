package rental.model;
import java.util.*;
import java.util.Vector;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class RentalNote{

    String id="", note_date="", userid="", notes="", rental_id="";

    final static long serialVersionUID = 625L;
    boolean debug = false;
    String errors = "";
    static Logger logger = LogManager.getLogger(RentalNote.class);
    User user = null;
    public RentalNote(){
		
    }
    public RentalNote(boolean deb){
	debug = deb;
		
    }	
    public RentalNote(boolean deb, String val){
	debug = deb;
	setId(val);
    }
    public RentalNote(boolean deb,
		      String val,
		      String val2,
		      String val3,
		      String val4,
		      String val5
		      ){
	debug = deb;		
	setId(val);
	setRental_id(val2);
	setNote_date(val3);
	setNotes(val4);
	setUserid(val5);
    }	

    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }
    public void setNote_date(String val){
	if(val != null)
	    note_date = val;
    }
    public void setNotes(String val){
	if(val != null)
	    notes = val;
    }
    public void setUserid(String val){
	if(val != null)
	    userid = val;
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
    public String getNotes(){
	return notes;
    }
    public String getNote_date(){
	return note_date;
    }
    public String getUserid(){
	return userid;
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
    public boolean isValid(){
	return !notes.equals("");
    }
    public String doSelect(){
	String back = "";
	String qq = "select rental_id,to_char(note_date,'MM/DD/YYYY'),notes,userid "+
	    "from rental_notes where id=?";
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
		setNote_date(rs.getString(2));
		setNotes(rs.getString(3));
		setUserid(rs.getString(4));
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
	String qs = "select rental_note_seq.nextval from dual";
	String qq = "insert into rental_notes values(?,?,SYSDATE,?,?)";
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	if(notes.equals("")){
	    back = "No notes to be saved";
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
	    pstmt.setString(jj++, notes);
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
