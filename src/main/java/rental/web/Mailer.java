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

/**
 * Generate an email message from the input of a request.
 *
 * Gives the user the ability to modify the content of a request
 * and add more feedback before sending the request.
 */

@WebServlet(urlPatterns = {"/Mailer"})
public class Mailer extends TopServlet{

    final static long serialVersionUID = 590L;
    static Logger logger = LogManager.getLogger(Mailer.class);
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
    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String subject="", title="", msg="", msg2="", action="";
	String bcc="", email="",username="";
	String cc = null;
	String message = "", all="", all2="";
	String [] vals;
	boolean success = true;
	int cnt = 0, icnt = 0;
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
	// for saving attachements
	if(file_path.equals("")){ // if not set
	    if(url.indexOf("10.50.103") == -1){
		file_path = "/srv/webapps/rentpro/mail/";
	    }
	    else{
		file_path = "C:\\webapps\\ROOT\\files\\rental\\";
	    }
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
			else if (name.equals("msg")) {
			    msg = value;
			}
			else if(name.equals("action")){
			    action = value;  
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
			// System.err.println(fileName);
			if (fileName != null) {
			    file_name = FilenameUtils.getName(fileName);
			    file_name = file_name.toLowerCase();
			    if(!file_name.equals("")){
				if(file_name.indexOf(".") == -1){
				    file_name += ".pdf";//some pc the ext is hidden
				}
				file_name = file_name.replace(" ","_");
				//System.err.println(file_name);
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
		    else if (name.equals("msg")) {
			msg = value;
		    }
		    else if(name.equals("action")){
			action = value;  
		    }
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	}
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
	List<String> vList = new ArrayList<String>();
	List<String> invList = new ArrayList<String>();
	if(action.equals("Send")){
	    //
	    String qq = "select name,email from name "+
		" where email is not null";
	    String str="",str2="";
	    all = "<table><tr><th>Name</th><th>Email</th></tr>\n";
	    cnt = 0;
	    Vector<String> bccv = new Vector<String>();
	    try{
		rs = stmt.executeQuery(qq);
		while(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    if(str != null && str2 != null){
			str2 = str2.trim();
			if(str2.indexOf("@") > -1){
			    if(str.indexOf(",") > -1){
				str = str.replace(",","");
			    }
			    // check if multiple emails
			    if(str2.indexOf(",") > -1){
				String tmp[] = str2.split(",");
				for(String str22: tmp){
				    str22 = str22.trim();
				    if(Helper.isValidEmail(str22)){
					if(!bcc.equals("")) bcc += ", \n";										
					bcc += str+" <"+str22.trim()+"> ";
					all += "<tr><td>"+str+"</td><td>"+str22+"</td></tr>\n";
					cnt++;
				    }
				}
			    }
			    else{
				if(Helper.isValidEmail(str2)){
				    if(!bcc.equals("")) bcc += ", \n";
				    bcc += str+" <"+str2.trim()+"> ";
				    all += "<tr><td>"+str+"</td><td>"+str2+"</td></tr>\n";
				    cnt++;
				}
			    }
			}
			else{
			    if(all2.equals("")){
				all2 = "<table><caption>Invalid Emails</caption>"+
				    "<tr><th>Name</th><th>Email</th></tr>\n";

			    }
			    all2 += "<tr><td>"+str+"</td><td>"+str2+"</td></tr>";
			}
		    }
		    if(cnt >= 500){
			bccv.add(bcc);
			bcc = "";
			cnt = 0;
		    }
		}
		if(cnt > 0){ // last banch
		    bccv.add(bcc);
		}
		all += "</table>";
		if(!all2.equals("")){
		    all2 += "</table>";
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	    if(subject.equals("")){
		subject = "Rental: ";
		if(!title.equals("")){
		    subject += title;
		}
	    }
	    // 
	    // for testing, make up email list
	    //
	    // bcc = "Walid Sibo <sibow@bloomington.in.gov>, Alan Schertz<schertza@bloomington.in.gov>";
	    //
	    int jj = 0;
	    if(bccv !=null && bccv.size() > 0){
		for(String bc: bccv){
		    jj++;
		    MailHandle mh =
			new MailHandle("Bulk",
				       "hand-rental@bloomington.in.gov", // from
				       subject,
				       msg,
				       cc,  // cc   
				       bc, // Blind CC
				       file_name,
				       file_path,
				       debug);
		    String back = "";
		    if(file_name.equals("")){
			back = mh.send();
		    }
		    else{
			back = mh.sendWAttach();
		    }
		    if(!back.equals("")){
			message += back;
			success = false;
			logger.error(message);
		    }
		    else{
			message += " Send successful to banch "+jj+"<br /> ";
		    }
		}
		cnt = cnt + (jj-1)*500;
	    }
	    if(cnt > 0 && !test){
		EmailLog elog = new EmailLog(debug, "General", user.getUsername());
		String back = elog.doSave();
	    }
	}
	else if(action.startsWith("Check")){
	    //
	    String qq = "select name,email,name_num from name "+
		" where email is not null";
	    String str="",str2="", str3="";
	    cnt = 0;
	    icnt = 0;
	    try{
		rs = stmt.executeQuery(qq);
		while(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    if(str != null && str2 != null){
			str2 = str2.trim();
			if(str2.indexOf("@") > -1){
			    if(str.indexOf(",") > -1){
				str = str.replace(",","");
			    }
			    // check if more than one email
			    if(str2.indexOf(",") > -1){
				String tmp[] = str2.split(",");
				for(String str22: tmp){
				    str22 = str22.trim();
				    if(!Helper.isValidEmail(str22)){
					str3 = "<a href=\""+url+"OwnerServ?"+
					    "&name_num="+str3+
					    "&action=zoom\">"+str3+"</a>";
					invList.add(str3+", "+str+", "+str22);
					icnt++;
				    }
				}
			    }
			    else{
				if(!Helper.isValidEmail(str2)){
				    str3 = "<a href=\""+url+"OwnerServ?"+
					"&name_num="+str3+
					"&action=zoom\">"+str3+"</a>";
				    invList.add(str3+", "+str+", "+str2);
				    icnt++;
				}
			    }
			}
			else{ // with no @ symbol
			    str3 = "<a href=\""+url+"OwnerServ?"+
				"&name_num="+str3+
				"&action=zoom\">"+str3+"</a>";
			    invList.add(str3+", "+str+", "+str2);
			    icnt++;
			}
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	}		
	out.println("<html><head><title>Legal Mailer</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println("  function makeSure(){		                  ");
	out.println("   var y =confirm(\"You are about to close this window before sending the message. Close anyway?\");	                        ");
	out.println("    if(y){ window.close(); }    	          ");
	out.println("	}						                  ");
	out.println("  function cleanText(obj,str){		          ");
	out.println("   if(obj.value.indexOf(str) > -1){          ");
	out.println("      obj.value='';        ");
	out.println("	   }					");
	out.println("	}						");
	out.println("  function validateForm(){		              ");
	out.println("     return true;					          ");
	out.println("	}	         				              ");
	out.println(" </script>		            ");
	out.println(" </head><body><center>     ");
	Helper.writeTopMenu(out, url);	

	//
	if(!message.equals("")){
	    if(success)
		out.println("<h2>"+message+"</h2>");
	    else
		out.println("<h2><font color=red>"+message+"</font></h2>");	
	}
	out.println("<h2>Owners/Agents Bulk Email</h2>");
	out.println("<table align=center width=80% border>");
	if(action.equals("")){
	    out.println("<form name=myForm method=\"post\" "+
			"onsubmit=\"return validateForm()\" "+
			"ENCTYPE=\"multipart/form-data\">");
	    //
	    out.println("<table width=\"100\">");
	    out.println("<tr><td colspan=\"2\"><font color=green>"+
			"Please read before you start<br />"+
			"<ul>"+
			"<li>The system will gather the email addresses from the owners and agents tables and use them in the email list. </li>"+
			"<li>It is recommended that you check the invalid emails first by clicking on 'Check Invalid Emails'</li>"+
			"<li>You may email a copy to yourself or/and to your manager by using the CC field.</li>"+
			"<li>After you made sure that all emails are OK, enter your message and then click on 'Send'</li>"+
			"</ul></font></td></tr>");
	    //
	    // email field
	    email = "Enter an email address here (optional)";
	    out.println("<tr><td><b>CC Email:</b></td><td><input type=text "+
			" onfocus=\"cleanText(this,'"+email+"')\"; "+
			"name=email value=\""+email+
			"\" size=50></td></tr>");
	    // Subject
	    title = "Enter the email subject here";
	    out.println("<tr><td><b>Subject:</b></td>");
	    out.println("<td><input type=text name=subject value=\""+ title+ 
			"\" size=50 "+
			" onfocus=\"cleanText(this,'"+title+"')\"; "+
			" ></td></tr>");
	    //
	    msg = "Enter your message here ";
	    out.println("<tr><td colspan=\"2\"><b>Compose Message</b></td><tr>");
	    //
	    // message
	    out.println("<tr><td colspan=\"2\"> "+
			"<textarea name=msg rows=15 cols=70 "+
			" onfocus=\"cleanText(this,'"+msg+"')\"; "+
			" wrap=\"wrap\" >"+msg+
			"</textarea></td></tr>");
	    out.println("<tr><td colspan=\"2\"><font color=green>If you want to attach a file, you can attach it by clicking on Browse button</font></td></tr>");
	    out.println("<tr><td><b>Document File:</b>");
	    out.println("</td><td>");
	    out.println("<input type=\"file\" name=\"im_file\" "+
			" size=30></td></tr>");
	    // 
	    // submit
	    out.println("<tr><td colspan=2><table width='100%'><tr>");
	    if(user.canEdit()){
		out.println("<td align=right>");
		out.println("<input type=submit name=action "+
			    "value='Check Invalid Emails'></td>");
	    }					
	    if(user.canEdit()){
		out.println("<td align=right>");				
		out.println("<input type=submit name=action "+
			    "value=Send></td>");
	    }
	    out.println("</tr></table></td></tr>");
	    out.println("</table></td></tr>");
	    out.println("</table>");
	    out.println("</form>");
	    EmailLogList elog = new EmailLogList(debug, "General");
	    String back = elog.find();
	    if(back.equals("")){
		if(elog.size() > 0){
		    out.println("<table border width=\"50%\"><caption>Email Log History</caption>");
		    out.println("<tr><th>Sent Date</th><th>Sent By</th></tr>");
		    for(EmailLog el: elog){
			out.println("<tr><td>"+el.getDate()+"</td><td>"+
				    el.getUserid()+"&nbsp;</td></tr>");
		    }
		    out.println("</table><p></p>");
		}
	    }
	}
	else if(action.equals("Send")){ // send the message
	    //
	    out.println("<tr><td>");			
	    if(success){
		out.println("Message sent successfully<br>");
		out.println("Email sent to "+cnt+" owners and agents <br />");
		if(cnt > 0){
		    out.println(all);
		}
		if(!all2.equals("")){
		    out.println(all2);
		}
	    }
	    else{
		out.println("<h2> Email failed </h2>");
		out.println("<p><font color=\"red\">"+message+"</font></p>");
	    }
	    out.println("</td></tr>");
	    out.println("<tr><td>");
	    out.println("</td></tr>");
	    out.println("</table>");
	}
	else if(action.startsWith("Check")){
	    if(invList.size() > 0){
		out.println("<h3> Invalid Emails, Need to be fixed</h3>");
		out.println("<p>Click on the link to go to owner page to do the fix</p>");
		out.println("<p>Found "+invList.size()+" of invalid emails that need to be fixed before sending the emails</p>");
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
	out.print("</body></html>");
	out.close();
	Helper.databaseDisconnect(con, stmt, rs);
		
    }

}






















































