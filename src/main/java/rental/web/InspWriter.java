package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;


@WebServlet(urlPatterns = {"/InspWriter"})
public class InspWriter extends TopServlet{

    String tag = "";
    final static long serialVersionUID = 410L;	
    boolean debug = false;
    static Logger logger = LogManager.getLogger(InspWriter.class);
    String alt_path = "J:\\departments\\HAND\\common\\RENTAL\\";
    String [] tap = {"0k\\","1k\\","2k\\","3k\\","4k\\","5k\\",
	"6k\\","7k\\","8k\\","9k\\"};

    /**
     * Generates the inspection main form with the view of
     * previously entered information.
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
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean success = true;
		
	String saveDirectory = "C:/temp",inspection_date2="", message="";
	String action="", inspection_date="", violations="0";
	String inspection_type="", id="", tid="", compliance_date="",
	    insp_file="",comments="",inspected_by="",heat_src="",
	    insp_fee="", variance="", dyy="", dmm="", ddd="", report="";
	// 
	// the threshold date where the inspection files started being
	// classed in year/month subdirectories, while all the old files
	// will stay in the same original directory
	//
	String filePath = "";
	// 
	// for inspection report
	String units="",structures="",bedrooms="",occ_load="",zoning="",
	    story_cnt="", allOldFiles="",old_insp="";
	int access=0;
	String [] vals;
	Hashtable<String, String> inspMap = new Hashtable<String, String>(7);
	inspMap.put("CYCL","Cycle Report");
	inspMap.put("REIN","Remaining Violations Report");
	inspMap.put("EE","Exterior Extension Reminder");
	inspMap.put("COMP","Complaint Inspection Report");
	inspMap.put("TV","Tenant Violation Report");
	inspMap.put("FIRE","Fire Report");
	inspMap.put("HOME","Home Inspections Report");
	// 
	Enumeration<String> values = req.getParameterNames();

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    if(vals == null) continue;
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
	    }
	    else if (name.equals("inspection_date")){
		inspection_date = value;
	    }
	    else if (name.equals("report")){
		report=value;
	    }
	    else if (name.equals("tag")){
		tag =value;
	    }
	    else if(name.equals("action")){
		action = value;  
	    }
	}
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?";
	    res.sendRedirect(str);
	    return; 
	}
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		success = false;
		message += " could not connect to database";
		logger.error(message);
	    }
	}
	catch(Exception ex){
	    success = false;
	    message += " could not connect to database "+ex;
	    logger.error(message);
	}
	//        
	if(true){
	    //
	    // Generate the inspection file
	    //
	    // Get the owner and agent addresses
	    //
	    ArrayList<String> ownerName = new ArrayList<String>(3);
	    ArrayList<String> ownerAddr = new ArrayList<String>(3);
	    ArrayList<String> ownerAddr2 = new ArrayList<String>(3);
	    ArrayList<String> varianceList = new ArrayList<String>(3);
	    String agent="", agentName="", agentAddr="", agentAddr2="",
		propAddr="",str="", qq="";
	    String query = "select n.name,n.address,"+
		"n.city||', '||n.state||' '||n.zip "+
		"from name n, regid_name r "+
		"where n.name_num=r.name_num and r.id="+id;
	    String query2 = "select street_num||' '||street_dir||' '||"+
		"street_name||' '||street_type||' '||post_dir||' '||"+
		"sud_type||' '||sud_num "+
		"from address2 "+
		"where registr_id="+id;
	    String query3 = "select agent from registr where id="+id;
	    qq = query;
	    if(debug){
		logger.debug(query);
	    }
	    try{
		rs = stmt.executeQuery(query);
		//
		while(rs.next()){
		    str = rs.getString(1);
		    if(str == null) str = "";
		    ownerName.add(str);
		    str = rs.getString(2);
		    if(str == null) str = "";
		    ownerAddr.add(str);
		    str = rs.getString(3);
		    if(str == null) str = "";
		    ownerAddr2.add(str);
		}
		qq = query2;
		if(debug){
		    logger.debug(query2);
		}
		rs = stmt.executeQuery(query2);
		//
		while(rs.next()){
		    str = rs.getString(1);
		    if(str != null){
			if(!propAddr.equals(""))propAddr += ", ";
			propAddr += str;
		    }
		}
		qq = query3;
		if(debug){
		    logger.debug(query3);
		}
		rs = stmt.executeQuery(query3);
		//
		if(rs.next()){
		    agent = rs.getString(1);
		    if(!(agent == null || agent.equals("0"))){
			query = "select name,address,"+
			    "city||' '||state||' '||zip "+
			    "from name "+
			    "where name_num="+agent;
			qq = query;
			if(debug){
			    logger.debug(query);
			}
			rs = stmt.executeQuery(query);
			//
			if(rs.next()){
			    str = rs.getString(1);
			    if(str != null)agentName = str;
			    str = rs.getString(2);
			    if(str != null)agentAddr = str;
			    str = rs.getString(3);
			    if(str != null)agentAddr2 = str;
			}
		    }
		}
		query3 = " select to_char(variance_date,'mm/dd/yyyy'),"+
		    "variance from variance where id="+id;
		qq = query3;
		if(debug){
		    logger.debug(query3);
		}
		rs = stmt.executeQuery(query3);
		//
		variance = "";
		while(rs.next()){
		    variance += "    ";
		    str = rs.getString(1);
		    String str2 = rs.getString(2);
		    if(str2 != null){ 
			if(str != null){ 
			    variance += str; 
			    variance += " ";
			}
			variance += str2; 
			variance += "\n";
		    }
		}
	    }
	    catch(Exception ex){
		success = false;
		out.println(ex);
		logger.error(ex+": "+qq);
	    }
	    //
	}
	if(report.equals(""))
	    //
	    report = "<table border=0 width=90%><tr><td colspan=2><br><br><br><br><br><br><br><center><b>Cycle "+
		"Report</b></center><br><br></td></tr><tr><td colspan=2 align=right>7686</td></tr"+
		"><tr><td colspan=2><br><br><u><b>OWNERS</b></u><br></td></tr><tr><td>COPPER BEACH TOWNH"+
		"OME COMMUNITIES</td><td></td></tr><tr><td>2766 W. COLLEGE AVE. STE. 2</td><td></"+
		"td></tr><tr><td>STATE COLLEGE, PA 16801</td><td></td></tr><tr><td><br><br></td><"+
		"/tr><tr><td colspan=2><br><br><u><b>AGENT</b></u><br></td></tr><tr><td>YOUNG, DIANE<br>"+
		"986-A  S. COPPER BEACH WAY<br>BLOOMINGTON IN 47403<br><br><br></td></tr><tr><td>"+
		"Prop. Location: 915 S BASSWOOD DR</td><td>Number of Units/Structures: 88 / 12</t"+
		"d></tr><tr><td>Date Inspected: 11/09/2005</td><td>Number of Bedrooms: 1,2,3,4</t"+
		"d></tr><tr><td>Inspectors: Barry Collins</td><td"+
		">Max # of Occupants: 2-5</td></tr><tr><td> Primary Heat Source: </td><td>Propert"+
		"y Zoning: RM15</td></tr><tr><td>Number of Stories: <br></td></tr>";
	//

	out.println("<html><head><title>Rental Inspection</title>");
	Helper.writeWebCss(out, url);	
	out.println("<script type=\"text/javascript\" src=\"/FCKeditor/fckeditor.js\"></script>");
	out.println("<script type=\"text/javascript\">             ");
	out.println(" function replaceTextareas(){                 ");
	out.println(" var oFCKeditor = new FCKeditor('report');    ");
	out.println(" oFCKeditor.BasePath = \"/FCKeditor/\";       ");
	out.println(" oFCKeditor.Width	= '600';                   ");
	out.println(" oFCKeditor.Height	= '600';                   ");
	out.println(" oFCKeditor.ReplaceTextarea() ;               ");
	out.println(" }                                            ");
	out.println(" </script>		                           ");
	out.println(" </head>                                      ");
	//
	out.println("<body onload=\"replaceTextareas()\"> ");
	Helper.writeTopMenu(out, url);
	out.println("<center><h2>Inspection Report Editor</h2>");

	out.println("<h2><font color=green>Prototype: For Illustration "+
		    "Purpose</font></h2>");
	out.println("<h3>Any data entered will not be saved, just give "+
		    "it a try</h3>");
	//
	out.println("<table border width=90%>");
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	//
	out.println("<input type=hidden name=id value="+id+">");
	out.println("<input type=hidden name=inspection_date value=\""+
		    inspection_date+"\">");
	//
	// 1st block
	//
	out.println("<tr><td><font color=green size=-1>"+
		    "Format your text using the toolbar below."+
		    " <br></td></tr>");
	out.println("<div>");
	out.println("<tr><td>");
	out.println("<textarea name=report rows=80 cols=70 wrap>");
	out.println(report);
	out.println("</textarea></td></tr>");
	out.println("</div>");
	//
	out.println("<tr><td align=right>  "+
		    "<input type=submit "+
		    "name=action "+
		    "value=Submit> "+
		    "</td></tr>"); 
	out.println("</table></td></tr>");
	out.println("</form>");
	//
	// send what we have so far
	//
	out.print("</body></html>");
	out.flush();
	out.close();
	Helper.databaseDisconnect(con, stmt, rs);

    }


}






















































