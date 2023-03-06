package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.sql.*;
import javax.naming.directory.*;
import javax.naming.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

// prob not used
//@WebServlet(urlPatterns = {"/Envelop"})
public class Envelop extends TopServlet{

    final static long serialVersionUID = 310L;
    boolean userFoundFlag = false;
    public static String[] allmonths = {"\n","Jan","Feb","March",
	"April","May","June",
	"July","August","Sept",
	"Oct","Nov","Dec"};

    static final String MONTH_SELECT = "<option>JAN\n" + 
	"<option>FEB\n" + 
	"<option>MAR\n" + 
	"<option>APR\n" + 
	"<option>MAY\n" + 
	"<option>JUN\n" + 
	"<option>JUL\n" + 
	"<option>AUG\n" + 
	"<option>SEP\n" + 
	"<option>OCT\n" + 
	"<option>NOV\n" + 
	"<option>DEC\n" + 
	"</select>";

    /**
     * Generates the bill.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{
	boolean connectDbOk = false;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String username = "", password = "";
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String bul_rate="",
	    unit_rate="",
	    bath_rate="", 
	    reinsp_rate="",
	    noshow_rate="";
	String bul_cnt="0",unit_cnt="0",bath_cnt="0";
	String reinsp_date="",reinsp_cnt="0",noshow_cnt="0",
	    noshow_date="",status="", prop_type="",
	    paid="",check_no="",invoice_num="",
	    due_date="",today="",issue_date="",paidSum="0";
	double insp_fee=0,total=0,reinsp_fee=0,noshow_fee=0,balance=0;
	//
	// receipt items
	String rec_date="",rec_from="",bhqa_fine="",rec_sum="",
	    old_balance="";
	//
	String name, value;
	String action="",id="",str="",agent="", message="";
	boolean success = true;
	ArrayList<String> ownerName=new ArrayList<String>(3);
	ArrayList<String> ownerAddr=new ArrayList<String>(3);
	ArrayList<String> ownerAddr2=new ArrayList<String>(3);

	Enumeration<String> values = req.getParameterNames();

	out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
	out.println("<html><head><title> </title></head><body>");

	String [] vals;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
	    }
	}
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		success = false;
		message += " could not connect to database";
	    }
	}
	catch(Exception ex){
	    System.err.println(ex);
	    success = false;
	    message += " could not connect to database "+ex;
	}
	//
	// we need these for default values of dates
	Calendar current_cal = Calendar.getInstance();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	//
	today = allmonths[current_cal.get(Calendar.MONTH)+1] 
	    + "-" +current_cal.get(Calendar.DATE) + "-" +  
	    current_cal.get(Calendar.YEAR);
	String query = "select initcap(name),initcap(address)"+
	    ",initcap(city||', '||state||' '||zip) "+
	    "from name o,regid_name i where o.name_num = i.name_num "+
	    "and i.id="+id;
	String query2 = "select agent from registr where agent > 0 and id="+id;

	if(debug){
	    System.err.println(query);
	}
	try{
	    rs = stmt.executeQuery(query);
	    //
	    while(rs.next()){
		str = rs.getString(1);
		if(str == null)  str = "";
		ownerName.add(str);
		str = rs.getString(2);
		if(str == null)  str ="";
		ownerAddr.add(str);
		str = rs.getString(3);
		if(str == null)  str="";
		ownerAddr2.add(str);
	    }
	    if(debug){
		System.err.println(query2);
	    }
	    rs = stmt.executeQuery(query2);
	    if(rs.next()){
		agent = rs.getString(1);
		if(agent != null){
		    query = "select initcap(name),initcap(address),"+
			"initcap(city||', '||state||' '||zip) "+
			"from name "+
			"where name_num="+agent;
		    if(debug){
			System.err.println(query);
		    }
		    rs = stmt.executeQuery(query);
		    //
		    if(rs.next()){
			str = rs.getString(1);
			if(str == null) str = "";
			ownerName.add(str);
			str = rs.getString(2);
			if(str == null) str = "";
			ownerAddr.add(str);
			str = rs.getString(3);
			if(str == null) str="";
			ownerAddr2.add(str);
		    }
		}
	    }
	}
	catch(Exception ex){
	    System.err.println(ex);
	}
	// 
	String space = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
	    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
	    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
	    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	
	out.println("<font face=\"new century\" size=2>");
	//
	for(int i=0; i<ownerName.size(); i++){
	    out.println("-<br>-<br>-<br>-<br>-<br>-<br>-<br>-<br>-<br>");
	    out.println(space+space+space);
	    out.println(ownerName.get(i)+"<br>");
	    out.println(space+space+space);
	    out.println(ownerAddr.get(i)+"<br>");
	    out.println(space+space+space);
	    out.println(ownerAddr2.get(i)+"<br>");
	    out.println("-<br>-<br>-<br>-<br>");
	}	   
	//
	Helper.databaseDisconnect(con,stmt,rs);
	out.println("</body>");
	out.println("</html>");
    }
    //
    /**
     * Generates the query form for bills.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	doGet(req, res);
    }

}























































