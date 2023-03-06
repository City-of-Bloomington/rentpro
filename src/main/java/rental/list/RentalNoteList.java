package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;

public class RentalNoteList extends ArrayList<RentalNote>{

    boolean debug;
    String rental_id=""; // registr_id from rental
    final static long serialVersionUID = 645L;
    Logger logger = LogManager.getLogger(RentalNoteList.class);
    //
    // for to expire permits options
    //
    String startDate="", endDate="";
    //
    // basic constructor
    public RentalNoteList(boolean deb){

	debug = deb;
    }
    public RentalNoteList(boolean deb, String val){

	debug = deb;
	setRental_id(val);
    }
    public void setRental_id(String val){
	if(val != null)
	    rental_id = val;
    }
    public void setStartDate(String val){
	if(val != null)
	    startDate = val;
    }
    public void setEndDate(String val){
	if(val != null)
	    endDate = val;
    }
    //
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;	
	ResultSet rs = null;
	String qq = "select id,rental_id,to_char(note_date,'MM/DD/YYYY'),notes,userid "+
	    "from rental_notes where rental_id=? order by id desc ";
	String back = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	try{
	    logger.debug(qq);
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, rental_id);
	    rs = pstmt.executeQuery();			
	    while(rs.next()){
		RentalNote one = new RentalNote(debug,
						rs.getString(1),
						rs.getString(2),
						rs.getString(3),
						rs.getString(4),
						rs.getString(5));
		add(one);
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






















































