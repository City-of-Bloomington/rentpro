package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/PostCardEmail"})
public class PostCardEmail extends TopServlet{


    final static long serialVersionUID = 720L;
    boolean debug = false;
    static String months[] = {"","Jan","Feb","March","April",
	"May","June","July","Aug",
	"Sept","Oct","Nov","Dec"};
	
    static String letterText = "Our records indicate that the Rental Occupancy Permits for the rental property(s) listed below will expire on the listed date.";
    static String letterText2 = "Please contact this office at (812) 349-3420 to schedule an inspection to renew your permit. City Ordinance requires rental units have valid Rental Occupancy Permits.  Schedule your inspection in advance so all repairs and subsequent reinspections occur prior to the expiration of your current permit.";
    static String letterText3 = "THANK YOU,";
    static String letterText4 = "Housing and Neighborhood Development";
    static String letterText5 = "Please do not reply to this message via e-mail. This address is automated, unattended, and cannot help with questions or requests.";
	
    static Logger logger = LogManager.getLogger(PostCardEmail.class);
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
	PrintWriter out = res.getWriter();
	String name, value;
	String action="",id="",str="",agent="", message="";
	boolean success = true;

	res.setContentType("text/html");
	out.println("<html><head><title>Owners Post Card </title>");
	Enumeration<String> values = req.getParameterNames();
	String [] vals;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("message")){
		message = value;
	    }
	}
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	GregorianCalendar current_cal = new GregorianCalendar();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	String today = ""+months[mm]+" "+dd+", "+yyyy;
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println(
		    " function validateDate(month,day,year){       \n"+
		    "   if(!isNaN(month)){                         \n"+
		    "      month = month - 1;                      \n"+
		    "   }                                          \n"+
		    //
		    // Create a date object
		    "   var date = new Date(year,month,day);        \n"+
		    "   if(year != date.getFullYear()){             \n"+
		    "                                               \n"+
		    "    return false;                              \n"+
		    "  }                                            \n"+
		    "  if(month != date.getMonth()){                \n"+
		    "    return false;                              \n"+
		    "  }                                            \n"+
		    "  if(day != date.getDate()){                   \n"+
		    "    return false;                              \n"+
		    "  }                                            \n"+
		    "  return true;                                 \n"+
		    " }                                             \n");
	out.println(
		    "  function checkDate(dt){                       \n"+     
		    "    var dd = dt.value;                          \n"+
		    "   if(!dd || dd.length == 0) return true;       \n"+
		    "   var dar = dd.split(\"/\");                   \n"+
		    " if(dar.length < 3){                            \n"+
		    " alert(\"Not a valid date: \"+dd);              \n"+
		    "      dt.select();                              \n"+
		    "      dt.focus();                               \n"+
		    "      return false;}                            \n"+
		    "   var m = dar[0];                              \n"+
		    "   var d = dar[1];                              \n"+
		    "   var y = dar[2];                              \n"+
		    "   if(isNaN(m) || isNaN(d) || isNaN(y)){        \n"+
		    "      return false; }                           \n"+
		    "  if(!validateDate(m,d,y)) {                    \n"+
		    "      return false;                             \n"+
		    "      }                                         \n"+
		    "    return true;                                \n"+
		    " }                                              \n");
	out.println("function checkForm(){   ");
	out.println(" with(document.forms[0]){  ");
	out.println("   if(startDate.value == ''){ ");
	out.println("     alert('You need to enter Beginning Date');");
	out.println("     startDate.focus(); ");	
	out.println("     return false; ");	
	out.println("   } ");
	out.println("   if(!checkDate(startDate)){      ");
	out.println("     alert('Beginning date is not a valid date');");
	out.println("     startDate.focus(); ");	
	out.println("     return false; ");	
	out.println("   } ");		
	out.println("   if(endDate.value == ''){ ");
	out.println("     alert('You need to enter End Date');");
	out.println("     endDate.focus(); ");	
	out.println("     return false; ");	
	out.println("   } ");
	out.println("   if(!checkDate(endDate)){      ");
	out.println("     alert('End date is not a valid date');");
	out.println("     endDate.focus(); ");	
	out.println("     return false; ");	
	out.println("   } ");
	out.println("   } ");
	out.println(" return true; ");		
	out.println(" } ");
	out.println("</script>");		
	out.println("</head>");
	out.println("<body><center>");
	Helper.writeTopMenu(out, url);	
	out.println("<h2>Owners and Agents Reminder Emails (Postcards)</h2>");
	//
	if(!message.equals("")){
	    out.println(message);
	}
	out.println("<table width=\"80%\" border=\"1\">");
	out.println("<tr><td>");
	out.println("<ul><li> We are replacing the old postcard merge option by email</li>");
	out.println("<li>This page is intended to generate a list of owners and agents of the rental properties that have permits about to expire within the date range set below.</li>");
	out.println("<li>Therefore, enter the Beginning Date and the End Date below. Click on the Submit button</li>");
	out.println("<li>The program will send emails to the owners/mangers and their agents if their emails are available.</li>");
	out.println("<li>You will get a list of emails that succeeded and another list ot those that failed (if any) </li>"); 
	out.println("</ul></td></tr>");
	out.println("<tr><td>");
	out.println("<form method=\"post\" onsubmit=\"return checkForm()\">");
	out.println("<table>");
	out.println("<tr><td>Beginning Date</td><td>");
	out.println("<input type=\"text\" name=\"startDate\" size=\"10\" maxlength=\"10\" />");
	out.println("<button type=button onClick=\""+
		    "window.open('"+url+"PickDate?wdate=startDate"+
		    "','Date','toolbar=0,location=0,"+
		    "directories=0,status=0,menubar=0,"+
		    "scrollbars=0,top=300,left=300,"+
		    "resizable=1,width=300,height=250');\""+
		    " >");
	out.println("<img src='"+url2+"calendar.jpg' height='15' width='20' alt='pick date' />");	
	out.println("</button>(mm/dd/yyyy)</td></tr>");
	out.println("<tr><td>End Date</td><td>");
	out.println("<input type=\"text\" name=\"endDate\" size=\"10\" maxlength=\"10\" />");
	out.println("<button type=button onClick=\""+
		    "window.open('"+url+"PickDate?wdate=endDate"+
		    "','Date','toolbar=0,location=0,"+
		    "directories=0,status=0,menubar=0,"+
		    "scrollbars=0,top=300,left=300,"+
		    "resizable=1,width=300,height=250');\""+
		    ">");
	out.println("<img src='"+url2+"calendar.jpg' height='15' width='20' alt='pick date' />");		
	out.println("</button>(mm/dd/yyyy)</td></tr>");		
	out.println("<tr><td>Sent Date</td><td>");
	out.println("<input type=\"text\" name=\"cardDate\" size=\"16\" maxlength=\"15\" value=\""+today+"\" disabled=\"disabled\" />");
	out.println("</td></tr>");
	out.println("<tr><td>&nbsp;</td><td align=\"right\">");
	out.println("<input type=\"submit\" name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");
	out.println("</table>");
	out.println("</td></tr>");
	out.println("</table></center>");
	out.println("</body>");
	out.println("</html>");
	out.close();
    }
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
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	PrintWriter out = res.getWriter();
	String name, value;
	String message="", path="";
	String startDate="", endDate="", cardDate="";
	boolean success = true;

	res.setContentType("text/html");
	out.println("<html>");
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("startDate")){
		startDate = value;
	    }
	    else if (name.equals("endDate")){
		endDate = value;
	    }
	    else if (name.equals("cardDate")){
		cardDate = value;
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
	if(url.indexOf("8080") == -1){
	    path = server_path; // server
	}
	else{
	    path = file_path; // PC
	}
	if(cardDate.equals("")) cardDate = Helper.getToday();
	//
	// Create the new table.  Cannot use bind variables so 
	// must draw from handhold
	//
	if(startDate.equals("") || endDate.equals("")){
	    message = " Beginning date and end date are required";
	    success = false;
	    res.sendRedirect(url+"PostCard?message="+message);
	    return;
	}
	int count = 0, failedCount = 0, rows=0;
	String qc = "select count(*) ", qq="";
	String q = "select o.name_num,r.id ";
	String qw = " from name o,regid_name ro,registr r,"
	    + "where o.name_num = ro.name_num "
	    + "and ro.id=r.id "
	    + "and r.inactive is null " // from rs
	    + "and r.property_status = 'R' "
	    + "and r.permit_expires >= "
	    + "to_date('"+startDate+"','mm/dd/yyyy') "
	    + "and r.permit_expires <= "
	    + "to_date('"+endDate+"','mm/dd/yyyy') order by o.name_num";
	Map<String, Set<String>> map = new HashMap<String, Set<String>>();
	try{
	    con = Helper.getConnection();
	    if(con == null){
		success = false;
		message += " could not connect to database";
		logger.error(message);
	    }
	    else{
		stmt = con.createStatement();
	    }
	    if(success){
		qq = qc+qw;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()) {
		    rows = rs.getInt(1);
		    System.err.println(" count "+rows);
		}
		if(rows > 0){
		    qq = q+qw;
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    Set<String> set = new HashSet<String>();
		    String old_own="", oid="", rid="";
		    boolean in = false;
		    while(rs.next()){
			oid = rs.getString(1);
			rid = rs.getString(2);
			in = true;
			if(oid == null || oid.equals("")) continue;
			if(!old_own.equals(oid)){
			    map.put(old_own, set);
			    set = new HashSet<String>();
			    set.add(rid);
			    old_own = oid;
			}
			else{
			    set.add(rid);
			}
		    }
		    if(in)
			map.put(old_own, set);// for the last set
		}
	    }
	}catch(Exception ex){
	    success = false;
	    message += " "+ex+" "+qq;
	    logger.error(message);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	String cc="", bcc ="",to="",file_name="", file_dir ="";
	String all = "", fld="", fullName="";
	for(String oid:map.keySet()){
	    to="";bcc=""; fullName="";
	    String emailText="";
	    Set<String> set = map.get(oid);
	    Owner owner = new Owner(debug, oid);
	    String back = owner.doSelect();
	    emailText = cardDate+"\n\n";
	    emailText += "Dear Owner/Manager/Agent\n\n";
	    emailText += letterText+"\n\n";
	    if(back.equals("")){
		if(owner.hasValidEmail()){
		    fullName = owner.getFullName();
		    if(fullName.indexOf(",") > -1){
			fullName = fullName.replace(",","");
		    }
		    fullName = fullName.trim();
		    List<String> emails = owner.getEmails();
		    if(emails != null){
			for(String stre:emails){
			    if(!to.equals("")){
				if(!bcc.equals("")) bcc += ", ";
				bcc += "\""+fullName+"\" <"+stre+">";
			    }else{							
				to = "\""+fullName+"\"  <"+stre+">";
			    }
			}
		    }
		}
	    }
	    for(String rid:set){
		Rent rent = new Rent(rid, debug);
		back = rent.doSelect();
		if(back.equals("")){
		    Owner agent = rent.getAgent();
		    if(agent != null && agent.hasValidEmail()){
			List<String> emails = agent.getEmails();
			if(emails != null){
			    fullName = agent.getFullName();
			    if(fullName.indexOf(",") > -1){
				fullName = fullName.replace(",","");
			    }
			    fullName = fullName.trim();
			    for(String stre:emails){
				if(!to.equals("")){
				    if(!bcc.equals("")) bcc += ", ";
				    bcc += "\""+fullName+"\" <"+stre+">";
				}else{							
				    to = "\""+fullName+"\"  <"+stre+">";
				}
			    }
			}
		    }
		    List<Address> addrs = rent.getAddresses();
		    if(addrs != null && addrs.size() > 0){
			for(Address addr:addrs){
			    emailText += "\t\t"+addr.getAddress()+", Expires "+rent.getPermit_expires()+"\n";
			}
		    }
		}
	    }
	    emailText +="\n\n"+letterText2+"\n\n";
	    emailText += letterText3+"\n\n";
	    emailText += letterText4+"\n\n";
	    emailText += letterText5+"\n\n";
			
	    if(!to.equals("") || !bcc.equals("")){
		back = sendEmail(to,"Rental Occupancy Permit Expires",emailText,cc,bcc,file_name,file_dir);
		if(!back.equals("")){
		    message += " email to "+to+" bas the following problem "+back+"<br />";
		    failedCount++;
		    fld += to+"<br />";
		    if(!bcc.equals("")) 
			fld += bcc+"<br />";
		}
		else{
		    all += " to:"+to+"<br />";
		    if(!bcc.equals("")) 
			all += "bcc;"+bcc+"<br />";
		    count++;
		}
	    }
	}
	// System.err.println(messages);
	out.println("<head><title>Postcard Reminder</title>");
	out.println("<script type=\"text/javascript\">");
	out.println("</script>");
	out.println("<body><center>");
	out.println("<h2>Owners and Agents Postcard Reminder</h2>");
	if(count > 0){
	    out.println("<p> Emails sent to "+count+" owners/mangers</p>");
	    out.println("email sent to "+all);
	}
	if(!message.equals("")){
	    if(failedCount > 0){
		out.println("<p> failed to email "+failedCount+" owners/mangers</p>");
		out.println("email failed on the following "+fld);
	    }
	    out.println(message);
	}
	//
	out.println("</body>");
	out.println("</html>");
	out.close();

		
    }
    String sendEmail(String to,
		     String subject,
		     String msg,
		     String cc,
		     String bcc,
		     String file_name,
		     String file_path){
	String back="";

	MailHandle mh =
	    new MailHandle(to,
			   "<hand-rental@bloomington.in.gov>", // from
			   subject,
			   msg,
			   cc,  // cc   
			   bcc, // Blind CC
			   file_name,
			   file_path,
			   debug);
	if(file_name.equals("")){
	    back = mh.send();
	}
	else{
	    back = mh.sendWAttach();
	}
	if(!back.equals("")){
	    logger.error(back);
	}
	return back;
		
    }

}























































