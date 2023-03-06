package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.util.regex.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;
import rental.list.*;

/**
 *
 *
 */
@WebServlet(urlPatterns = {"/CycleEmailList"})
@SuppressWarnings("unchecked")
public class CycleEmailList extends TopServlet{

    final static long serialVersionUID = 320L;

    static Logger logger = LogManager.getLogger(CycleEmailList.class);
    /**
     * Generates the email list from a form.
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
	String action="";
	String cycle_date_from="", cycle_date_to="";
	String reinsp_date_from="", reinsp_date_to="";				
	String message = "";
	String [] vals;
	boolean success = true;
	if(url.equals("")){
	    //
	    url    = getServletContext().getInitParameter("url");
	    String str = getServletContext().getInitParameter("debug");
	    if(str.equals("true")) debug = true;
	}
	Enumeration<String> values = req.getParameterNames();
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if(name.equals("action")){
		action = value;  
	    }
	    else if (name.equals("cycle_date_from")) {
		cycle_date_from = value;
	    }
	    else if (name.equals("cycle_date_to")) {
		cycle_date_to = value;
	    }
	    else if (name.equals("reinsp_date_from")) {
		reinsp_date_from = value;
	    }
	    else if (name.equals("reinsp_date_to")) {
		reinsp_date_to = value;
	    }
	    else{
		System.err.println(" unknown "+name);
	    }
	}
	out.println("<html><head><title>Cycle & Reinspection Email list</title>");
	Helper.writeWebCss(out, url);	
	if(action.isEmpty()){
	    out.println("<script>");
	    out.println("  function makeSure(){		                  ");
	    out.println("   var y =confirm(\"You are about to close this window before sending the message. Close anyway?\");	                        ");
	    out.println("    if(y){ window.close(); }    	          ");
	    out.println("	}						                  ");
	    out.println(" </script>		                     ");
	}
	out.println(" </head><body><center>     ");
	Helper.writeTopMenu(out, url);	
	out.println("<h2>Owners/Agents Cycle & Reinspection Email List</h2>");
	//		
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	}
	if(user == null){
	    res.sendRedirect(url+"Login?");
	    return;
	}
	List<String> invList = new ArrayList<String>();
	if(!action.isEmpty()){
	    Map<String, List<Rent>> map = new HashMap<String, List<Rent>>();
	    if(!cycle_date_from.isEmpty() && !cycle_date_to.isEmpty()){
		RentList rl = new RentList(debug);
		rl.setDate_from(cycle_date_from);
		rl.setDate_to(cycle_date_to);
		rl.setWhichDate("pull_date");
		rl.setPull_reason("1L"); // 1L:cycle, RI: Reinspection
		String back = rl.lookFor();
		if(!back.equals("")){
		    success = false;
		    message += back;
		    logger.error(message);
		}
		else{
		    List<Rent> rents = rl.getRents();
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
		}
	    }
	    if(!reinsp_date_from.isEmpty() && !reinsp_date_to.isEmpty()){
		RentList rl = new RentList(debug);
		rl.setDate_from(reinsp_date_from);
		rl.setDate_to(reinsp_date_to);
		rl.setWhichDate("pull_date");
		rl.setPull_reason("RI"); // RI: Reinspection
		String back = rl.lookFor();
		if(!back.equals("")){
		    success = false;
		    message += back;
		    logger.error(message);
		}
		else{
		    List<Rent> rents = rl.getRents();
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
		}
	    }
	    if(map.size() > 0){
		out.println("<center><h2>"+map.size()+" owwners and their agents email list </h2>");
		out.println("<table><tr><th>ID</th>"+
			    "<th>Cycle/Reinspection Date</th>"+
			    "<th>Owner/Agent</th><th>Email(s)</th></tr>\n");
		String all = "";								
		Iterator<Map.Entry<String, List<Rent>>> it = map.entrySet().iterator();
		// cnt = map.size();
		while (it.hasNext()) {
		    Map.Entry<String, List<Rent>> pair = it.next();
		    String ownId = pair.getKey();
		    List<Rent> rents = pair.getValue();
		    for(Rent rent:rents){
			OwnerList owners = rent.getOwners();
			Owner agent = rent.getAgent();
			if(agent != null && agent.hasValidEmail()){
			    String row = "<tr><td>";
			    row += rent.getId()+"</td><td>"+rent.getPull_date()+"</td><td>"+agent.getFullName()+"</td><td>"+agent.getEmail()+"</td></tr>";
			    all += row+"\n";
			}
			for(Owner owner:owners){
			    String row = "<tr><td>";
			    String oid = owner.getId();
			    if(oid.equals(ownId) && owner.hasValidEmail()){
				row += rent.getId()+"</td><td>"+rent.getPull_date()+"</td><td>"+owner.getFullName()+"</td><td>"+owner.getEmail()+"</td></tr>";
				all += row+"\n";
			    }
			}
		    }
		}
		if(!all.isEmpty()){
		    all += "</table>";
		    out.println(all);
		}
	    }
	    else{
		out.println("No records found");
	    }
	}
	if(!message.equals("")){
	    if(!success)
		out.println("<h2><font color=red>"+message+"</font></h2>");	
	}
	if(action.isEmpty()){
	    out.println("<form name=myForm method=\"post\" >");
	    out.println("<table width=\"90%\" border=\"1\">");
	    out.println("<tr><td align=\"center\">");
	    out.println("<table width=\"100%\">");
	    out.println("<tr><td colspan=\"3\">"+
			"<font color=\"green\">"+
			"Please read before you start<br />"+
			"<ul>"+
			"<li>The system will gather the email addresses from the owners and agents in rental system and use them in the email list. </li>"+
			"<li>Permits that have cycle date within the date range set below will only be selected</li>"+
			"<li>Permits that have reinspection date within the date range set below will only be selected as well</li>"+
			"<li>If you want to show cycle permits use cycle  date range only and leave the reinspection dates empty, vice versa as well</li>"+
			"</ul></font></td></tr>");
	    //
	    String str_date = Helper.getNextWeekStartDate();
	    String end_date = Helper.getNextWeekEndDate();
	    out.println("<tr><td></td><td>From</td><td>To</td></tr>");
						
	    out.println("<tr><td align=\"right\"><b>Cycle Date Range:</b></td><td><input type=\"text\" "+
			"name=\"cycle_date_from\" value=\""+str_date+"\" size=\"10\" class=\"date\" /></td>");
	    out.println("<td><input type=\"text\" "+
			"name=\"cycle_date_to\" value=\""+end_date+"\" size=\"10\" class=\"date\" /></td></tr>");
	    out.println("<tr><td align=\"right\"><b>Reinspection Date Range</b></td><td><input type=\"text\" "+
			"name=\"reinsp_date_from\" value=\""+str_date+"\" size=\"10\" class=\"date\" /></td>");						
	    out.println("<td><input type=\"text\" "+
			"name=\"reinsp_date_to\" value=\""+end_date+"\" size=\"10\" class=\"date\" /></td></tr>");						
	    // 
	    if(user.canEdit()){
		out.println("<tr><td colspan=\"3\" align=\"right\">");
		out.println("<input type=\"submit\" name=\"action\" "+
			    "value=\"Find\" />");
		out.println("</td></tr>");								
	    }
	    out.println("</table>");
	    out.println("</td></tr></table>");
	    out.println("<br />");
	    out.println("</form>");
	    Helper.writeWebFooter(out, url);
	}
	out.println("</center>");
	out.print("</body></html>");
	out.close();
		
    }

}






















































