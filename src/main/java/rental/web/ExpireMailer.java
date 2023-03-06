package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.sql.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.io.*;
import java.util.regex.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;		

@WebServlet(urlPatterns = {"/ExpireMailer"})
@SuppressWarnings("unchecked")
public class ExpireMailer extends TopServlet{

    final static long serialVersionUID = 320L;
    public static String[] allmonths = {"JAN","FEB","MAR",
	"APR","MAY","JUN",
	"JUL","AUG","SEP",
	"OCT","NOV","DEC"};

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
    static Logger logger = LogManager.getLogger(ExpireMailer.class);
    /**
     * Generates the mail form and then sends the email.
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
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String subject="", title="", msg="", msg2="", action="";
	String bcc="", email="",username="";
	String cc = null, date_from="", date_to="";;
	String message = "", all="", all2="";
	String emailLogsAt = ""; // date
	String [] vals;
	boolean success = true;
	String pgraphs[] = {"Dear Owner/Manager \n\n"+
	    "Our records indicate that the Rental Occupancy Permits for the rental property permit(s) listed below will expire on the listed dates: \n",

	    "Please contact this office at (812) 349-3420 to schedule an inspection to renew your permit. We cannot schedule inspections via email. Bloomington City Ordinance requires each rental unit to have a valid Rental Occupancy Permits. Please schedule your inspection in advance so all repairs and subsequent reinspections may occur prior to the expiration of your current permit.\n\n",
														
	    "If you have already contacted our office and scheduled your inspection, please disregard this email.\n\n"+ 
	    "     Thank You\n"+
	    " Housing and Neighborhood Development\n"+
	    " City Of Bloomington, Indiana\n\n"+
	    " This is a courtesy reminder. The responsibility to schedule all inspections falls on property owners/managers.\n\n"+
	    "***This is an automated generated email. Please do not reply to this email. If you have questions or concerns, please contact our department at the above mentioned phone number.\n"
	};
	int cnt = 0;
	int maxMemorySize = 5000000; // 5 MB , int of bytes
	int maxRequestSize = 5000000; // 5 MB
	//
	// Create a factory for disk-based file items
	DiskFileItemFactory factory = new DiskFileItemFactory();
	//
	// Set factory constraints
	factory.setSizeThreshold(maxMemorySize);
	//
	// if not set will use system temp directory
	// factory.setRepository(fileDirectory); 
	//
	// Create a new file upload handler
	ServletFileUpload upload = new ServletFileUpload(factory);
	// ServletFileUpload upload = new ServletFileUpload();
	//
	// Set overall request size constraint
	upload.setSizeMax(maxRequestSize);
		
	String file_path = "";
	String file_name = "";
	if(url.indexOf("8080") == -1){
	    file_path = "/var/www/sites/rentpro/mail/";
	}
	else{
	    file_path = "C:\\webapps\\ROOT\\files\\rental\\";
	}
	List<FileItem> items = null;
	try{
	    if(req.getContentType() != null &&
	       req.getContentType().toLowerCase().indexOf("multipart/form-data") > -1){			
		items = upload.parseRequest(req);
		Iterator<FileItem> iter = items.iterator();
		while (iter.hasNext()) {
		    FileItem item = iter.next();
		    if (item.isFormField()){
			name = item.getFieldName();
			value = item.getString();
			if (name.equals("subject")) {
			    subject = value;
			}
			else if (name.equals("cc")) {
			    if(!value.startsWith("Enter an"))
				cc = value;
			}
			else if (name.equals("date_from")) {
			    date_from = value;
			}
			else if (name.equals("date_to")) {
			    date_to = value;
			}
			else if (name.startsWith("pgraphs")) {
			    try{
				int jj = Integer.parseInt(name.substring(8,9));
				pgraphs[jj] = value;
			    }catch(Exception ex){}
			}
			else if(name.equals("action")){
			    action = value;  
			}
			else{
			    System.err.println(" unknown "+name);
			}
						
		    }
		    else{
			//
			// process uploaded item/items
			//
			String fieldName = item.getFieldName();
			String contentType = item.getContentType();
			// boolean isInMemory = item.isInMemory();
			// sizeInBytes = item.getSize();
						
			String fileName = item.getName();
			System.err.println(fileName);
			if (fileName != null) {
			    file_name = FilenameUtils.getName(fileName);
			    file_name = file_name.toLowerCase();
			    if(!file_name.equals("")){
				if(file_name.indexOf(".") == -1){
				    file_name += ".pdf";//some pc the ext is hidden
				}
				file_name = file_name.replace(" ","_");
				System.err.println(file_name);
				File file = new File(file_path, file_name);
				item.write(file);
			    }
			}
		    }
		}
	    }
	    else{
		Enumeration<String> values = req.getParameterNames();
		while (values.hasMoreElements()){
		    name = values.nextElement().trim();
		    vals = req.getParameterValues(name);
		    value = vals[vals.length-1].trim();	
		    if (name.equals("subject")) {
			subject = value;
		    }
		    else if (name.equals("cc")) {
			if(!value.startsWith("Enter an"))
			    cc = value;
		    }
		    else if (name.equals("pgraphs")) {
			pgraphs = vals;
		    }						
		    else if(name.equals("action")){
			action = value;  
		    }
		    else if (name.equals("date_from")) {
			date_from = value;
		    }
		    else if (name.equals("date_to")) {
			date_to = value;
		    }
		    else if (name.equals("emailLogsAt")) {
			emailLogsAt = value;
		    }									
		    else{
			System.err.println(" unknown "+name);
		    }
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	}
	out.println("<html><head><title>Legal Mailer</title>");
	
	Helper.writeWebCss(out, url);
	if(action.equals("")){
	    out.println("<script>");
	    out.println("  function makeSure(){		                  ");
	    out.println("   var y =confirm(\"You are about to close this window before sending the message. Close anyway?\");	                        ");
	    out.println("    if(y){ window.close(); }    	          ");
	    out.println("	}						                  ");
	    out.println("  function cleanText(obj,str){		          ");
	    out.println("   if(obj.value.indexOf(str) > -1){          ");
	    out.println("      obj.value=\"\";        ");
	    out.println("	   }					");
	    out.println("	}						");
	    out.println("  function checkDate(dd){		         ");
	    out.println("   if(dd.length == 0) return true;          "); 
	    out.println("   else if(dd.length != 10){                "); 
	    out.println("      return false; }                       ");
	    out.println("   else {                                   "); 
	    out.println("   var m = dd.substring(0,2);               "); 
	    out.println("   var d = dd.substring(3,5);               "); 
	    out.println("   var y = dd.substring(6,10);              "); 
	    out.println("   if(!(dd.charAt(2) == \"/\" && dd.charAt(5) == \"/\")){ ");
	    out.println("      return false; }                            ");
	    out.println("   if(isNaN(m) || isNaN(d) || isNaN(y)){         ");
	    out.println("      return false; }                            ");
	    out.println("   if( !((m > 0 && m < 13) && (d > 0 && d <32) && ");
	    out.println("    (y > 1970 && y < 2099))){                    "); 
	    out.println("      return false; }                            ");
	    out.println("       }                                         ");
	    out.println("    return true;                                 ");
	    out.println("    }                                            ");
	    out.println("  function validateForm(){		              ");
	    out.println("    var dd = document.forms[0].date_from.value; ");
	    out.println("    if(dd == '' || !checkDate(dd)){	   ");
	    out.println("      alert('You need to enter a valid start date');");
	    out.println("      document.forms[0].date_from.focus(); ");
	    out.println("      return false;					    ");
	    out.println("	 }	         				              ");
	    out.println("    dd = document.forms[0].date_to.value; ");
	    out.println("    if(dd == '' || !checkDate(dd)){ ");
	    out.println("      alert('You need to enter a valid end date');");
	    out.println("      document.forms[0].date_to.focus(); ");
	    out.println("      return false;					     ");
	    out.println("	 }	         			               ");
	    out.println("     return true;					       ");
	    out.println("	}	         				               ");
	    out.println(" </script>		                     ");
	}
	out.println(" </head><body><center>     ");
	Helper.writeTopMenu(out, url);	
	out.println("<h2>Owners/Agents Permits Expire Email</h2>");
	//		
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user != null){
		username = user.getUsername();
	    }
	}
	if(user == null){
	    res.sendRedirect(url+"Login?");
	    return;
	}
	List<String> invList = new ArrayList<String>();
	if(action.equals("Send")){
	    //
	    RentList rl = new RentList(debug);
	    rl.setDate_from(date_from);
	    rl.setDate_to(date_to);
	    rl.setWhichDate("permit_expires");
	    String back = rl.lookFor();
	    if(!back.equals("")){
		success = false;
		message += back;
		logger.error(message);
	    }
	    else{
		List<Rent> rents = rl.getRents();
		cnt = 0;
		Map<String, List<Rent>> map = new HashMap<String, List<Rent>>();
		for(Rent rent: rents){
		    OwnerList owners = rent.getOwners();
		    Owner agent = rent.getAgent();
		    for(Owner owner: owners){
			if((owner.hasEmail() && owner.hasValidEmail()) ||
			   (agent != null && agent.hasEmail())){
			    String oid = owner.getId();
			    if(map.containsKey(oid)){
				List<Rent> lr = map.get(oid);
				lr.add(rent);
				map.put(oid, lr);
			    }
			    else{
				List<Rent> lr = new ArrayList<Rent>();
				lr.add(rent);
				map.put(oid, lr);
			    }
			}
		    }
		}
		if(map.size() > 0){
		    Iterator<Map.Entry<String, List<Rent>>> it = map.entrySet().iterator();
		    cnt = map.size();
		    out.println("<center><h2>"+map.size()+" owwners and their agents will be emailed </h2>");
		    out.println("Note: if certain emails get any errors during sending you will see a note about the error after that email <br />"); 
		    out.println("<table><tr><th>ID</th>"+
				"<th>Expire Date</th>"+
				"<th>Owner</th><th>Email</th></tr>\n");
		    while (it.hasNext()) {
			Map.Entry<String, List<Rent>> pair = it.next();
			String ownId = pair.getKey();
			rents = pair.getValue();
			Owner owner = new Owner(debug, ownId);
			back = owner.doSelect();
			if(back.equals("")){
			    back = composeExpirePermit(rents,
						       owner,
						       cc,
						       file_name,
						       file_path,
						       out,
						       subject,
						       pgraphs,
						       user
						       );
														
			}
		    }
		    out.println("</table></center>");
		    if(!test){
			EmailLog elog = new EmailLog(debug, "Expire", user.getUsername());
			back = elog.doSave();
		    }
		}
		else{
		    out.println("No records matched this period");
		}
	    }
	}
	else if(action.startsWith("Check")){
	    //
	    String str="",str2="", str3="";
	    OwnerList owners = null;
	    OwnerList ol = new OwnerList(debug);
	    ol.setWithEmailOnly();
	    str = ol.lookFor();
	    if(!str.equals("")){
		success = false;
		message += str;
	    }
	    else{
		owners = ol;
	    }
	    if(owners != null){
		for(Owner own:owners){
		    //
		    // we want those with invalid emails
		    // so that the staff can go ahead and fix them
		    //
		    if(!own.hasValidEmail()){
			str = own.getId();
			str2 = own.getFullName();
			str3 = own.getEmail();
			str = "<a href=\""+url+"OwnerServ?"+
			    "&name_num="+str+
			    "&action=zoom\">"
			    +str+"</a>";
			invList.add(str+", "+str2+", "+str3);
		    }
		}
	    }
	}		
	if(!message.equals("")){
	    if(!success)
		out.println("<h2><font color=red>"+message+"</font></h2>");	
	}
	if(action.equals("")){
	    out.println("<form name=myForm method=\"post\" "+
			"onsubmit=\"return validateForm()\" "+
			"enctype=\"multipart/form-data\">");
	    out.println("<table width=\"80%\" border=\"1\">");
	    out.println("<tr><td align=\"center\">");
	    out.println("<table width=\"100\">");
	    out.println("<tr><td colspan=\"2\">"+
			"<font color=\"green\">"+
			"Please read before you start<br />"+
			"<ul>"+
			"<li>The system will gather the email addresses from the owners and agents in rental system and use them in the email list. </li>"+
			"<li>Permits that have expiration date within the date range set below will only be selected</li>"+
			"<li>It is recommended that you check the invalid emails first by clicking on 'Check Invalid Emails' button below.</li>"+
			"<li>You may email a copy to yourself or/and to your manager by using the CC field. Make sure it is a complete email address such as 'hand@bloomington.in.gov'</li>"+
			"<li>You can edit the email message as you see fit by editing the email paragraphs below.</li>"+
			"<li>Click on the 'Send' button</li>"+
			"</ul></font></td></tr>");
	    //
	    String today = Helper.getToday();
	    String str_date = Helper.addDaysToDate(today, 60);
	    String end_date = Helper.addDaysToDate(today, 90);

	    out.println("<tr><td align=\"right\"><b>Date Range From:</b></td><td align=\"left\"><input type=\"text\" "+
			"name=\"date_from\" value=\""+str_date+"\" size=\"10\" class=\"date\" /></td></tr>");
	    out.println("<tr><td align=\"right\"><b>Date Range To:</b></td><td align=\"left\"><input type=\"text\" "+
			"name=\"date_to\" value=\""+end_date+"\" size=\"10\" class=\"date\" /></td></tr>");			
	    // email field			
	    email = "Enter an email address here (optional)";
	    out.println("<tr><td align=\"right\"><b>CC Email:</b></td><td align=\"left\"><input type=\"text\" "+
			" onfocus=\"cleanText(this,'"+email+"')\"; "+
			" name=\"cc\" value=\""+email+"\" "+
			" size=\"50\" /></td></tr>");
	    // Subject
	    title = "Rental Permit Expires Soon ";
	    out.println("<tr><td align=\"right\"><b>Subject:</b></td>");
	    out.println("<td align=\"left\"><input type=\"text\" name=\"subject\" value=\""+
			title+"\" size=\"50\" /></td></tr>");
	    //
	    msg = "Enter your message here ";
	    out.println("<tr><td colspan=\"2\" align=\"left\">Email Introduction</td><tr>");
	    //
	    // message paragraphs
	    //
	    out.println("<tr><td colspan=\"2\"> "+
			"<textarea name=\"pgraphs[0]\" rows=\"5\" cols=\"70\" "+
			" wrap=\"wrap\" >"+pgraphs[0]+
			"</textarea></td></tr>");
	    out.println("<tr><td colspan=\"2\"><font color=green>");
	    out.println(" Unit Address(es) and expire dates will come next.  <br /><br /> ");
	    out.println(" Then the following paragraphs will follow. </font></td><tr>");		
	    out.println("<tr><td colspan=\"2\"> "+
			"<textarea name=\"pgraphs[1]\" rows=\"7\" cols=\"70\" "+
			" wrap=\"wrap\" >"+pgraphs[1]+"</textarea></td></tr>");
	    out.println("<tr><td colspan=\"2\"> "+
			"<textarea name=\"pgraphs[2]\" rows=\"14\" cols=\"70\" "+
			" wrap=\"wrap\" >"+pgraphs[2]+"</textarea></td></tr>");
	    //
	    out.println("<tr><td colspan=\"2\"><font color=green>If you want to attach a file, you can attach it by clicking on Browse button (optional).</font></td></tr>");
	    out.println("<tr><td>&nbsp;</td></tr>");						
	    out.println("<tr><td align=\"right\"><b>Document File:</b>");
	    out.println("</td><td align=\"left\">");
	    out.println("<input type=\"file\" name=\"im_file\" /></td></tr>");
	    out.println("<tr><td>&nbsp;</td></tr>");			
	    out.println("</table></td></tr>");
	    // 
	    // submit
	    if(user.canEdit()){
		out.println("<tr><td><table width=\"100%\">");
		out.println("<tr><td align=\"center\">");
		out.println("<input type=\"submit\" name=\"action\" "+
			    "value=\"Check Invalid Emails\" /></td>");
		out.println("<td align=\"right\">");				
		out.println("<input type=\"submit\" name=\"action\" "+
			    "value=\"Send\" /></td>");
		out.println("</tr></table></td></tr>");								
	    }
	    out.println("</table>");
	    out.println("<br />");
	    out.println("</form>");
	    EmailDetailLogList elogs = new EmailDetailLogList(debug);
	    List<String> dateList = elogs.getDateList();
	    if(dateList != null && dateList.size() > 0){
								
		out.println("<table border width=\"50%\"><caption>Latest Email Batches </caption>");
		out.println("<tr><th>Send Date</th><th>Action</th></tr>");
		for(String str:dateList){
		    out.println("<tr>");
		    out.println("<td>"+str+"</td>");
		    out.println("<td><a href=\""+url+"ExpireMailer?emailLogsAt="+str+"\">Click for more details</a></td>");
		    out.println("</tr>");
		}
		out.println("</table>");
	    }
	    if(!emailLogsAt.equals("")){
		elogs.setDate_at(emailLogsAt);
	    }
	    String back = elogs.find();
	    if(back.equals("")){
		String tableTitle = "Last email batch";
		if(!emailLogsAt.equals("")){
		    tableTitle = "Email batch sent on "+emailLogsAt;
		}
		tableTitle += " ("+elogs.size()+")";
		if(elogs.size() > 0){
		    out.println("<table border width=\"100%\"><caption>"+tableTitle+"</caption>");
		    out.println("<tr>"+
				"<th>Date</th><th>Sent By</th>"+
				"<th>To</th><th>Cc</th><th>Bcc</th>"+
				"<th>Owner id</th><th>Agents ID</th>"+
				"<th>Rental ids</th><th>Sent Status</th>"+
				"</tr>");
		    for(EmailDetailLog el: elogs){
			out.println("<tr><td>"+el.getDate()+"</td>"+
				    "<td>"+el.getUserid()+"</td>"+
				    "<td>"+el.getTo()+"&nbsp;</td>"+
				    "<td>"+el.getCc()+"&nbsp;</td>"+
				    "<td>"+el.getBcc()+"&nbsp;</td>"+
				    "<td>"+el.getOwners_id()+"&nbsp;</td>"+
				    "<td>"+el.getAgents_id()+"&nbsp;</td>"+
				    "<td>"+el.getRentals_id()+"&nbsp;</td>"+
				    "<td>"+el.getStatus()+"&nbsp;</td>"+
				    "</tr>");
		    }
		    out.println("</table><br />");
		}
	    }
	}
	else if(action.equals("Send")){ // send the message
	    //
	    if(!success){
		out.println("<h2> Email failed </h2>");
		out.println("<p><font color=\"red\">"+message+"</font></p>");
	    }
	}
	else if(action.startsWith("Check")){
			
	    if(invList.size() > 0){
		out.println("<h3> Invalid Emails, Need to be fixed</h3>");
		out.println("<p>We found "+invList.size()+" invalid emails that need to be fixed before sending the email. ");
		out.println("Click on the link to go to owner page to do the fix</p>");
		out.println("<p>If some emails cannot be fixed, they will be ignored from this bulk</p>");
		out.println("<table><tr><th>Owner ID, Name, Email</th>");
		for(String str: invList){
		    out.println("<tr><td>"+str+"</td></tr>");
		}
		out.println("</table>");
	    }
	    else{
		out.println("<h3> All Emails are OK </h3>");
		out.println("<p>You can go ahead and send the emails</p>");
	    }
	}
	out.println("</center>");
	Helper.writeWebFooter(out, url);
	out.print("</body></html>");
	out.close();
		
    }
    /**
     * compose the email text for permit that will expire soon
     * @param Rent, Owner objects
     */
    String composeExpirePermit(List<Rent> rents,
			       Owner owner,
			       String cc,
			       String file_name,
			       String file_path,
			       PrintWriter out,
			       String subject,
			       String[] pgraphs,
			       User user
			       ){
	String back = "";
	if(subject.equals("")){
	    subject = "Rental Permit Expires Soon ";
	}
	String msg="", bcc="", str="", to="";
	String ids="", owner_id="", agents_id="";
				
	if(owner.hasEmail() && owner.hasValidEmail()){
	    owner_id = owner.getId();
	    str = owner.getEmail();
	    if(str.indexOf(",") > 0){
		String tnp[] = str.split(",");
		for(String str2: tnp){
		    if(to.equals("")){
			to = owner.getFullName().replace(",","")+" <"+ str2.trim()+">";
		    }
		    else{
			if(!bcc.equals("")) bcc += ", ";
			bcc += owner.getFullName().replace(",","")+" <"+ str2.trim()+">";
		    }
		}
	    }
	    else{
		to = owner.getFullName().replace(",","")+" <"+ str.trim()+">";
	    }
	}
	msg = pgraphs[0]+"\n\n";
	for(Rent rent: rents){
	    // msg += "Permit ID:"+rent.getId()+ " expires on "+rent.getPermit_expires()+"\n\n";
	    if(!ids.equals("")) ids +=",";
	    ids += rent.getId();
	    List<Address> addresses = rent.getAddresses();
	    for(Address addr: addresses){
		msg += "  "+addr.getAddress()+" "+rent.getPermit_expires()+" ("+rent.getId()+")\n";
	    }
	    out.println("<tr><td>"+
			rent.getId()+"</td><td>"+
			rent.getPermit_expires()+"</td><td>"+
			owner.getFullName()+"</td><td>"+
			owner.getEmail()+"</td></tr>");
	    if(rent.hasAgent()){
		Owner agent = rent.getAgent();
		if(agent.hasEmail() && agent.hasValidEmail()){
		    str = agent.getId();
		    if(agents_id.indexOf(str) == -1){
			if(!agents_id.equals("")) agents_id += ",";
			agents_id += str;
		    }
		    str = agent.getEmail();
		    if(str.indexOf(",") > 0){
			String tnp[] = str.split(",");
			for(String str2: tnp){
			    str2 = str2.trim();
			    if(to.equals("")){
				to = agent.getFullName().replace(",","")+" <"+ str2.trim()+">";
			    }
			    else{
				if(to.indexOf(str2) > -1 || bcc.indexOf(str2) > -1) continue;// avoid dups
				if(!bcc.equals("")) bcc += ", ";
				bcc += agent.getFullName().replace(",","")+" <"+ str2.trim()+">";
			    }
			    out.println("<tr><td>"+
					rent.getId()+"</td><td>"+
					rent.getPermit_expires()+"</td><td>"+
					agent.getFullName()+" (Agent)</td><td>"+
					str2+"</td></tr>");
			}
		    }
		    else{
			if(to.equals("")){
			    to = agent.getFullName().replace(",","")+" <"+ str.trim()+">";
			}
			else{
			    if(to.indexOf(str) > -1 || bcc.indexOf(str) > -1) continue;	 // avoid dups
			    if(!bcc.equals("")) bcc += ", ";					
			    bcc += agent.getFullName().replace(",","")+" <"+ str.trim()+">";
			}
			out.println("<tr><td>"+
				    rent.getId()+"</td><td>"+
				    rent.getPermit_expires()+"</td><td>"+
				    agent.getFullName()+" (Agent)</td><td>"+
				    str+"</td></tr>");
						
		    }
		}
	    }
	}
	msg += "\n";
	msg += pgraphs[1]+"\n\n";
	msg += pgraphs[2]+"\n\n";
	//
	// if we have email list
	//
	if(!to.equals("")){
	    if(!test){
		MailHandle mh =
		    new MailHandle(to,
				   "hand-rental@bloomington.in.gov", // from
				   subject,
				   msg,
				   cc,  // cc   
				   bcc, // Blind CC
				   file_name,
				   file_path,
				   debug);
		back = mh.send();
		EmailDetailLog emailog = new EmailDetailLog(debug,
							    user.getUsername(),
							    to,
							    cc,
							    bcc,
							    owner_id,
							    agents_id,
							    ids,
							    back.equals("")? "Success":"Failure"
							    );
		emailog.doSave();
		if(!back.equals("")){
		    out.println("<tr><td colspan=\"4\"><font color=\"red\">Email Error:"+back+"</font></td></tr>");
		}
	    }
	    else{
		System.err.println("email to: "+to+" "+bcc);
		System.err.println("Subject "+subject);
		System.err.println("msg "+msg);				
	    }
	}
	return back;
    }
}






















































