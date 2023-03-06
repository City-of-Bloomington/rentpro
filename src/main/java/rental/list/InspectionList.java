package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;
/**
 *
 */

public class InspectionList extends ArrayList<Inspection>{

    boolean debug;
    String registr_id = "";
    final static long serialVersionUID = 460L;
    static Logger logger = LogManager.getLogger(InspectionList.class);
    String order_by = " inspection_date desc";
    NumberFormat dblForma = new DecimalFormat("#0.00");     		
    int inspection_count = 0;
    double average_time = 0;
    boolean compliance_date_not_null = false;
    //
    // basic constructor
    public InspectionList(boolean deb, String val){

	debug = deb;
	registr_id = val;
	//
	// initialize
	//
    }
    public void setOrderBy(String val){
	if(val != null && !val.equals(""))
	    order_by = val;
    }
    public void setComplianceDateNotNull(){
	compliance_date_not_null = true;
    }
    public String getInspectionCount(){
	if(inspection_count >0)
	    return ""+inspection_count;
	else
	    return "";
    }
    public String getAverageTime(){
	if(average_time > 0)
	    return dblForma.format(average_time);
	else
	    return "";
    }
    //
    // setters
    //
    public List<Inspection>  getInspections(){
	return this;
    }
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back = "", qq = "";
	try{
	    qq = "select insp_id,"+
		"to_char(inspection_date,'mm/dd/yyyy'),"+
		"inspection_type,"+
		"to_char(compliance_date,'mm/dd/yyyy'),"+
		"violations,"+
		"inspected_by,"+
		"insp_file,"+
		"comments,"+
		"foundation,"+
		"attic,"+
		"accessory,"+
		"story_cnt,"+
		"heat_src,"+
		"smook_detectors,"+
		"life_safety, "+
		"time_spent,"+
		"time_status, "+
		"has_affidavit "+
		" from "+Inspection.table_name+" where id=? ";
	    if(compliance_date_not_null){
		qq += " and compliance_date is not null ";
	    }
	    if(!order_by.equals("")){
		qq += " order by "+order_by;
	    }
	    if(debug){
		logger.debug(qq);
	    }	
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }		
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, registr_id);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);
		String str4 = rs.getString(4);
		String str5 = rs.getString(5);
		String str6 = rs.getString(6);
		String str7 = rs.getString(7);
		String str8 = rs.getString(8);
		String str9 = rs.getString(9);
		String str10 = rs.getString(10);
		String str11 = rs.getString(11);
		String str12 = rs.getString(12);
		String str13 = rs.getString(13);
		String str14 = rs.getString(14);
		String str15 = rs.getString(15);
		String str16 = rs.getString(16);
		String str17 = rs.getString(17);
		String str18 = rs.getString(18);
		Inspection one = new Inspection(debug,
						str, str2, str3,
						str4, str5, str6,
						str7, str8, str9,
						str10, str11, str12,
						str13, str14, str15,
						registr_id, str16, str17,
						str18
						);
		add(one);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
    public String findStats(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back = "", qq = "";
	try{
	    qq = "select count(*),avg(time_spent)"+
		" from "+Inspection.table_name+" where id = ? ";
	    qq += " and time_spent > 0 ";
	    if(debug){
		logger.debug(qq);
	    }	
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }		
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, registr_id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		inspection_count  = rs.getInt(1);
		average_time = rs.getDouble(2);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
		
}






















































