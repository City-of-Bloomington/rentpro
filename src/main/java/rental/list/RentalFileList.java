package rental.list;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;

public class RentalFileList{

    boolean debug;
    static final long serialVersionUID = 23L;
    static Logger logger = LogManager.getLogger(RentalFileList.class);
    String rental_id="";
    List<RentalFile> files = null;
    //
    public RentalFileList(boolean deb){

	debug = deb;
    }
    public RentalFileList(boolean deb,
			  String val){

	debug = deb;
	setRental_id(val);

    }
    //
    // setters
    //
    public void setRental_id(String val){
	if(val != null){
	    rental_id = val;
	}
    }
    public List<RentalFile> getFiles(){
	return files;
    }
    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qo = "";
	String qq = "select id,rental_id,added_by_id,"+
	    "to_char(added_date,'mm/dd/yyyy'),name, "+
	    "old_name,notes ";
	String qf =	"from rental_files ";
	String back="", qw = "";
		
	if(!rental_id.equals("")){
	    qw = " rental_id = ? ";
	}
	if(!qw.equals("")){
	    qw = " where "+qw;
	}
	qq = qq + qf + qw;			
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}		
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(!rental_id.equals(""))
		pstmt.setString(1, rental_id);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		RentalFile one = new RentalFile(debug,
						rs.getString(1),
						rs.getString(2),
						rs.getString(3),
						rs.getString(4),
						rs.getString(5),
						rs.getString(6),
						rs.getString(7));
		if(files == null)
		    files = new ArrayList<>();
		if(!files.contains(one))
		    files.add(one);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    return ex.toString();
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return "";
    }

	

}






















































