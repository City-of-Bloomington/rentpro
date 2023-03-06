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
/**
 *
 *
 */
@WebServlet(urlPatterns = {"/PostCard"})
public class PostCard extends TopServlet{

    final static long serialVersionUID = 710L;
    static String months[] = {"","Jan","Feb","March","April",
	"May","June","July","Aug",
	"Sept","Oct","Nov","Dec"};
    static Logger logger = LogManager.getLogger(PostCard.class);
    /**
     * 
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
	    str = url+"Login?";
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
	out.println("<h2>Owners and Agents Reminder Postcards</h2>");
	//
	if(!message.equals("")){
	    out.println(message);
	}
	out.println("<table width=\"80%\" border=\"1\">");
	out.println("<tr><td>");
	out.println("<br />");
	out.println("<p>This page is intended to generate a list of owners and agents of the rental properties that have permits about to expire within the date range set below.</p>");
	out.println("<p>Therefore, enter the Beginning Date and the End Date below. Set the post card date, this date will be printed on the post card. Click on the Submit button</p>");
	out.println("<p>After you click on Submit, a pop-up window will ask you to save or open the file. Click on Open. A pdf reader will show the selected owners/agents and their related rentals addresses and expire date.</p>");
	out.println("To print, make sure the following are set before you click on the Print button:<br />");
	out.println("<ul><li>Actual size is checked</li>");
	out.println("<li>Choose paper source by PDF page size is checked</li>");
	out.println("<li>Print on both sides of paper is checked</li>");
	out.println("<li>Flip on long edge is checked</li>");
	out.println("<li>Orientation, Portrait is checked</li>");
	out.println("</ul>");
	out.println("You may expermint of printing pages 1-2 to make sure your setting are correct before you print the whole document<br />");
	out.println("Make sure you have enough 4-UP postcard type papers (4 postcards per paper) in the printer.<br /> Normally you will need about a few more than pages count divided by 2 (pages count is listed in the pdf file top menu)<br />");				
	out.println("<br />");
	out.println("</td></tr>");
	out.println("<tr><td>");
	out.println("<form method=\"post\" action=\""+url+"PostCardPdf.do\" onsubmit=\"return checkForm()\">");
	out.println("<table>");
	out.println("<tr><td>Beginning Date</td><td>");
	out.println("<input type=\"text\" name=\"startDate\" size=\"10\" maxlength=\"10\" class=\"date\" />");
	out.println("</td></tr>");
	out.println("<tr><td>End Date</td><td>");
	out.println("<input type=\"text\" name=\"endDate\" size=\"10\" maxlength=\"10\" class=\"date\" />");
	out.println("</td></tr>");		
	out.println("<tr><td>Postcard Date</td><td>");
	out.println("<input type=\"text\" name=\"cardDate\" size=\"11\" maxlength=\"15\" value=\""+today+"\" />");
	out.println("<button type=button onClick=\""+
		    "window.open('"+url+"PickDate?wdate=cardDate&monthType=text"+
		    "','Date','toolbar=0,location=0,"+
		    "directories=0,status=0,menubar=0,"+
		    "scrollbars=0,top=300,left=300,"+
		    "resizable=1,width=300,height=250');\""+
		    " >");
	out.println("<img src='"+url2+"calendar.jpg' height='15' width='20' alt='pick date' />");		
	out.println("</button>(Month dd, yyyy)");

	out.println("</td></tr>");
	out.println("<tr><td>&nbsp;</td><td align=\"right\">");
	out.println("<input type=\"submit\" name=\"action\" value=\"Submit\" />");
	out.println("</td></tr>");
	out.println("</table>");
	out.println("</td></tr>");
	out.println("</table></center>");
	Helper.writeWebFooter(out, url);
	//
	out.println("</script>");	
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
		String str = url+"Login?source=PostCard";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=PostCard";
	    res.sendRedirect(str);
	    return; 
	}
	if(url.indexOf("10.50.103") == -1){
	    path = server_path; // server
	}
	else{
	    path = file_path; // PC
	}
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
	String qq = "";
	int rows = 0;
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
	    qq = "delete from handfull";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt.executeUpdate(qq);
	    qq = "insert into handfull (name_num,type) "
		+ "(select distinct name.name_num, 'Owner/Manager' type "
		+ "from name,regid_name,registr " 
		+ "where name.name_num = regid_name.name_num "
		+ "and regid_name.id=registr.id "
		+ "and inactive is null "
		+ "and property_status = 'R' "
		+ "and registr.permit_expires >= "
		+ "to_date('"+startDate+"','mm/dd/yyyy') "
		+ "and registr.permit_expires <= "
		+ "to_date('"+endDate+"','mm/dd/yyyy') "
		+ " ) "
		+ "UNION "
		+ "( select distinct agent, 'Agent' type from registr "
		+" where "
		+ " inactive is null "		
		+" and agent is not null and agent != 0 and agent != 6010 "
		+ "and registr.permit_expires >= "
		+ "to_date('"+startDate+"','mm/dd/yyyy') "
		+ "and registr.permit_expires <= "
		+ "to_date('"+endDate+"','mm/dd/yyyy') "
		+ "and property_status = 'R') ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt.executeUpdate(qq);
	    qq = "select count(*) from handfull";
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()) {
		rows = rs.getInt(1);
	    }
	    //
	    qq = "update handfull set indices = rownum";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt.executeUpdate(qq);
	    qq = "select name.name_num,type,initcap(name),"+
		"initcap(address),initcap(city) || ', ' || state || "+
		"' ' || zip "+
		"from  name,handfull "+
		"where handfull.name_num = name.name_num "+
		"and indices = ?";
	    if(debug){
		logger.debug(qq);
	    }
	    PreparedStatement selectOwner = con.prepareStatement(qq);
	    qq = "select initcap("+
		"street_num || ' ' || street_dir || ' ' "+
		" || street_name || ' ' || street_type || "+
		" ' ' || sud_type || ' ' || sud_num), "+
		"to_char(permit_expires,'mm/dd/yyyy') "+
		"from handfull,address2,regid_name,"+
		"registr "+
		"where handfull.name_num = "+
		"regid_name.name_num "+
		"and indices = ? "+
		"and type like 'O%' "+ // Owner
		"and regid_name.id = address2.registr_id "+
		"and registr.id = regid_name.id "+
		"and permit_expires >= "+
		"to_date('"+startDate+"','mm/dd/yyyy') "+
		"and permit_expires <= "+
		"to_date('"+endDate+"','mm/dd/yyyy') "+
		"UNION "+
		"select initcap(street_num || ' ' || street_dir || "+
		"' ' || street_name || ' ' || street_type"+
		" || ' ' || sud_type || ' ' || sud_num), "+
		"to_char(permit_expires,'mm/dd/yyyy') "+
		"from handfull,address2,registr "+
		"where handfull.name_num = agent "+
		"and indices = ? "+
		"and type like 'A%' "+ //  agent
		"and address2.registr_id = registr.id "+
		"and permit_expires >= "+
		"to_date('"+startDate+"','mm/dd/yyyy') "+
		"and permit_expires <= "+
		"to_date('"+endDate+"','mm/dd/yyyy') ";	
	    if(debug){
		logger.debug(qq);
	    }
            PreparedStatement selectAddress = con.prepareStatement(qq);
	    BufferedWriter bw = new BufferedWriter(new FileWriter(path+"Postcard/Goeshere.txt", false));
	    bw.write("\"OneCDate\",\"OneType\",\"OneName\",\"OneAddress\",\"OneCSZ\",\"TwoCDate\",\"TwoType\",\"TwoName\",\"TwoAddress\",\"TwoCSZ\",\"OneAddr1\",\"OneAddr1X\",\"OneAddr2\",\"OneAddr2X\",\"OneAddr3\",\"OneAddr3X\",\"OneAddr4\",\"OneAddr4X\",\"OneOther1\",\"OneOther2\",\"TwoAddr1\",\"TwoAddr1X\",\"TwoAddr2\",\"TwoAddr2X\",\"TwoAddr3\",\"TwoAddr3X\",\"TwoAddr4\",\"TwoAddr4X\",\"TwoOther1\",\"TwoOther2\"");
	    bw.newLine();
	    String str="",str2="",str3="",str4="";
	    for (int t=1;t <= rows;t += 2) {
		selectOwner.setInt(1,t);
		ResultSet rs1 = selectOwner.executeQuery();
		while (rs1.next()){
		    str= rs1.getString(2);
		    if(str == null) str = " ";
		    str2=    rs1.getString(3);
		    if(str2 == null) str2 = " ";
		    str3=    rs1.getString(4);
		    if(str3 == null) str3 = " ";
		    str4=    rs1.getString(5);
		    if(str4 == null) str4 = " ";
		    bw.write("\"" + cardDate + "\",\"" + 
			     str + "\",\"" + 
			     str2 + "\",\"" + 
			     str3 + "\",\"" + 
			     str4 +"\",");
		    bw.flush();
		}
		rs1.close();
		selectOwner.setInt(1,t+1);
		rs1 = selectOwner.executeQuery();
		int addCount = 0;
		while (rs1.next()) {
		    str= rs1.getString(2);
		    if(str == null) str = " ";
		    str2=    rs1.getString(3);
		    if(str2 == null) str2 = " ";
		    str3=    rs1.getString(4);
		    if(str3 == null) str3 = " ";
		    str4=    rs1.getString(5);
		    if(str4 == null) str4 = " ";
		    bw.write("\"" + cardDate + "\",\"" + 
			     str + "\",\"" + 
			     str2 + "\",\"" + 
			     str3 + "\",\"" + 
			     str4 +"\"");
		    bw.flush();
		    addCount++;
		}
		rs1.close();
		if (addCount == 0){
		    bw.write("\"\",\"\",\"\",\"\",\"\"");
		}
		selectAddress.setInt(1,t);
		selectAddress.setInt(2,t);
		ResultSet rs2 = selectAddress.executeQuery();
		addCount = 0 ;
		while (rs2.next()){
		    if (addCount == 4){
			bw.write(",\"OTHERS\",\"\"");
			addCount++;
			break;
		    }
		    bw.write(",\"" + rs2.getString(1) + "\",\"" + 
			     rs2.getString(2)+ "\"");
		    addCount++;
		}
		if (addCount < 5) {
		    for (int f = 1; f <= 5-addCount;f++){
			bw.write(",\"\",\"\"");
		    }
		}
		bw.flush();
		selectAddress.setInt(1,t+1);
		selectAddress.setInt(2,t+1);
		rs2 = selectAddress.executeQuery();
		addCount = 0;
		while (rs2.next()){
		    if (addCount == 4){
			bw.write(",\"OTHERS\",\"\"");
			addCount++;
			break;
		    }
		    bw.write(",\""+rs2.getString(1) +  "\",\"" + 
			     rs2.getString(2)+ "\"");
		    addCount++;
		}
		if (addCount < 5) {
		    for (int f = 1; f <= 5-addCount;f++){
			bw.write(",\"\",\"\"");
		    }
		}
		bw.flush();
		bw.newLine();
	    }
	    bw.close() ;
	}
	catch(Exception ex){
	    success = false;
	    message += ex+" : "+qq;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	out.println("<head><title>Postcard Reminder</title>");
	out.println("<script type=\"text/javascript\">");
	out.println(" function showCount(){           ");
	out.println("  alert('"+rows+" records will be processed');");
	out.println(" } ");
	out.println("</script>");
	out.println("<body onload=showCount()><center>");
	out.println("<h2>Owners and Agents Postcard Reminder</h2>");
	if(success){
	    out.println("<table><tr><td>");
	    out.println("<ul>");
	    out.println("<li>Data gathered successfully. </li>"+
			"<li>Now you can run the Postcard shortcut on your desktop to perform the mail merge to MS Word"+
			"<li>Put postcard paper in the printer, you will need at least "+(rows/2)+
			"<li>Send the Word documents to the specified printer"+
			"</ul>");
	    out.println("</td></tr></table>");
	}
	if(!message.equals("")){
	    out.println(message);
	}
	//
	out.println("</body>");
	out.println("</html>");
	out.close();

		
    }
}























































