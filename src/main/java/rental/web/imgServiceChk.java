package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/imgServiceChk"})
public class imgServiceChk extends TopServlet{

    final static long serialVersionUID = 1070L;
    /**
     * Generates the main upload or view image form.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGet
     * @see #doGet
     *
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("application/xml");
	PrintWriter out = res.getWriter();
	String name, value;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean success = true;
	String saveDirectory ="",image_path="";
	String action="", image_date="", image_file="";
	String id="", idim="", notes="", action2="";
	boolean successFlag = true, found=false;
	String username = "", message = "";
	int access = 0;
	// 
	// class to handle multipart request (for example text + image)
	// the image file or any upload file will be saved to the 
	// specified directory
	// 
	String today = Helper.getToday();
	String yy = today.substring(8,10); // last two digits of the year
	//
	if(url.indexOf("10.50.103") == -1){
	    image_path = "/var/www/html/images/rent/";
	    saveDirectory =image_path+yy+"/";
	}
	else{
	    image_path = "C:\\webapps\\ROOT\\images\\rent\\";
	    saveDirectory =image_path+yy+"\\";
	}
	// 
	// we have to make sure that this directory exits
	// if not we create it
	//
	File myDir = new File(saveDirectory);
	if(!myDir.isDirectory()){
	    myDir.mkdirs();
	}
	//
	action = req.getParameter("action");
	image_file = req.getParameter("imgfile");

	//
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
	// this is the only action for now
	//
	if(action.equals("check")){
	    //
	    // since we are dealing with new image files 
	    // we assume that the year is the current year
	    //
	    String qq = "select count (*) "+
		" from rental_image where image_file='"+image_file+"'"+
		" and to_char(image_date,'yy') = '"+yy+"'";

	    String str="";
	    int cnt = 0;
	    if(debug){
		System.err.println(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    cnt = rs.getInt(1);
		    if(cnt > 0) found = true;
		}
	    }
	    catch(Exception ex){
		System.err.println(ex);
	    }
	}
	//
	// write the xml response based on our findings
	//
	out.println("<?xml version=\"1.0\"?>");
	out.println("<respond generated=\""+System.currentTimeMillis()+"\">");
	out.println("<answer>");
	if(found){
	    out.println("No");
	}
	else{
	    out.println("Ok");
	}
	out.println("</answer>");
	out.println("</respond>");
	Helper.databaseDisconnect(con,stmt,rs);
	out.close();
	out.flush();

    }

}






















































