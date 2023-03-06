package rental.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/LegalAddOner"})
public class LegalAddOwner extends TopServlet{

    final static long serialVersionUID = 510L;
    String[] typeIdArr = null;
    String[] typeArr = null;
    static AddressHandle addrHand = null;	
    String mysqlDbStr = "";
    static Logger logger = LogManager.getLogger(LegalAddOwner.class);
    //
    /**
     *
     * @param req
     * @param res
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
	String action="";
		
	//
	HttpSession session = null;
	session = req.getSession(false);

	String id="", rental_id="", case_id="", message="";

	boolean success = true;
	Owner agent = null;
	List<Owner> owners = null;      // from rental
	List<Defendant> defendants = null;  // from legaltrack
		
	User user = null;
		
	if(addrHand == null){
	    addrHand = new AddressHandle(debug);
	}
	Enumeration<String> values = req.getParameterNames();
	String [] ownId = null;
	String [] defId = null;
	List<LegalAddress> addresses = null;		
	String [] vals;
		
	Rent rent = new Rent(debug);
	Legal legal = new Legal(debug);
	Case cCase = new Case(debug);
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		id = value;
		legal.setId(value);
	    }
	    else if (name.equals("defId")) {
		defId = vals;
	    }
	    else if (name.equals("ownId")) {
		ownId = vals;
	    }		
	    else if (name.equals("action")) {
		action = value;
	    }
	}
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=LegalAddOwner&id="+id;
		res.sendRedirect(str);
		return;
	    }
	}
	else{
	    String str = url+"Login?source=LegalAddOwner&id="+id;			
	    res.sendRedirect(str);
	    return; 
	}
		
	if(true){
	    String back = legal.doSelect();
	    if(back.equals("")){
		cCase = legal.getCase();
		rent.setId(legal.getRental_id());
		back = rent.doSelect();
		if(!back.equals("")){
		    message += back;
		    success = false;
		}
		else{
		    agent = rent.getAgent();
		    owners = rent.getOwners();
		}
	    }
	    else{
		message += back;
		success = false;
	    }
	}
	//
	if(action.equals("Save")){
	    if(defId == null && ownId == null){
		message = "You need to pick defendants (owners) to add to this case ";
		success = false;
		action="";
	    }
	}				
	if(action.equals("")){
	    DefendantList defs = new DefendantList(debug);
	    defs.findMatchingDefendants(owners);
	    if(defs.size() > 0){
		defendants = defs;
	    }				
	}
	//
	// Next step adding owners and agents (if any)
	//
	if(action.equals("Save") && success){
	    //
	    // we already have these in the system (legaltrack so we
	    // just add their id to legaltrack associated with case id
	    //
	    String qqDef = "", qqOwn="", qq = "";
	    if(defId != null && defId.length >0){
		String back = cCase.linkDefendantToCase(defId);
	    }
	    if(ownId != null && ownId.length > 0){
		defId = new String[ownId.length];
		Owner[] owns = new Owner[ownId.length];
		Defendant[] defs = new Defendant[ownId.length];
		for(int i=0;i<ownId.length;i++){
		    owns[i] = new Owner(debug, ownId[i]);
		    String back = owns[i].doSelect(); // oracle
		    if(!back.equals("")){
			message += " Could not get owners info "+back;
			success = false;
			logger.error(message);
		    }
		    else{
			defs[i] =
			    setDefendantFromOwner(owns[i]);
			defId[i] = defs[i].getDid();
		    }
		}
		if(defId.length > 0){
		    String back = cCase.linkDefendantToCase(defId);
		}
	    }
	    if(activeMail){
		//
		// send an email to the legal dept using legalContact userid
		// about this case
		//
		String unitsInfo="", address="", reason = legal.getReason();
		unitsInfo += " Structures: "+rent.getStructures();
		unitsInfo += ", Units: "+rent.getUnits();
		addresses = cCase.getAddresses();
		if(addresses != null){
		    for(LegalAddress one:addresses){
			if(!address.equals("")) address += " ";
			address += one.getAddress();
		    }
		}
		String msg = " For your information "+
		    "\n Rental record ID : "+rent.getId()+
		    "\n related Legal Action ID: "+legal.getId() +
		    "\n a new action was added today by "+user.getFullName()+
		    "\n The reason for the legal action was "+legal.getReason()+
		    "\n This permit has "+unitsInfo+
		    "\n ";
		if(addresses.size() > 0){
		    msg += " Rental address(es): ";
		    for(LegalAddress one: addresses){
			msg += one.getAddress();
			msg += "\n";
		    }
		    if(debug){
			logger.debug(" Mail msg: "+msg);
		    }
		}
		String subject = "Legal Actions: ";
		if(!reason.equals("")){
		    if(reason.length() > 30)
			subject += reason.substring(0,30);
		    else
			subject += reason;
		}
		//
		String[] legalContArr = null;
		if(legalContact.indexOf(",") > -1){
		    legalContArr = legalContact.split(",");
		}
		String email = "", cc="", status="Success";
		if(legalContArr != null && legalContArr.length > 1){
		    email = legalContArr[0].trim()+emailStr;
		    for(int i=1;i<legalContArr.length;i++){
			if(!cc.equals("")) cc +=",";
			cc += legalContArr[i].trim()+emailStr;
		    }
		}
		else{
		    email = legalContact+emailStr;
		}
		if(cc.equals("")) cc = null;
		String from = user.getUsername()+emailStr;
		if(email.equals("")){
		    message += " Error setting email ";
		    success = false;
		    LegalItEmailLog llg = new
			LegalItEmailLog(debug,
					rent.getId(),
					null,// date
					from,
					email,
					cc,
					subject,
					msg,
					"Failure",
					"No 'email to' found ");
		    String back = llg.doSave();
		    if(!back.equals("")){
			message += back;
		    }																				
		}
		if(!email.equals("")){
		    MsgMail mgm = 
			new MsgMail(email, // to
				    from,//from
				    subject,
				    msg,
				    cc, // CC
				    null, // bcc
				    false);
		    String back = mgm.doSend();
		    if(!back.equals("")){
			message += back;
			success = false;
			status = "Failure";
		    }
		    LegalItEmailLog llg = new
			LegalItEmailLog(debug,
					rent.getId(),
					null,// date
					from,
					email,
					cc,
					subject,
					msg,
					status,
					back);
		    back = llg.doSave();
		    if(!back.equals("")){
			message += back;
		    }										
		}
	    }
	}
	out.println("<html><head><title>Rentals</title>");
	Helper.writeWebCss(out, url);
	out.println("<script language=Javascript>");
	out.println("  function validateDelete(){	                      ");
	out.println("   var x = false;                                    ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     return x;                                       ");
	out.println("	}						                          ");
	out.println("  function validateForm(){	                          ");
	out.println("     return true;                                    ");
	out.println("	}						                          ");
	out.println(" </script>		                                      ");
	out.println("</head><body>                                        ");
	Helper.writeTopMenu(out, url);	
	out.println("<center><h2>Add Defendant(s) To This Case</h2>         ");
	if(!message.equals("")){
	    if(success)
		out.println("<h2>"+message+"</h2>");
	    else
		out.println("<h3><font color='red'>"+message+"</font></h3>");
	}
	if(action.equals("")){
	    if(user.hasRole("Edit"))
		out.println("<form name='myForm' method='post'>");
	    out.println("<table width=80% border><tr><td>");
	    out.println("<table width=100%>");
	    out.println("<tr><td>Rental ID: "+legal.getRental_id());
	    out.println("</td></tr>");
	    out.println("<tr><td>Case ID: "+legal.getCase_id());
	    out.println("</td></tr>");
	    out.println("</table></td></tr>");
	    if(owners != null && owners.size() > 0){
		out.println("<tr><td><table width=100%>");
		out.println("<caption>Rental Owners</caption>");
		out.println("<tr><td colspan=4>Some of these owners may be already in legaltrack, the matching name will be shown in the next list. If you think some/all of these names match the list below deselect this list and select them from the legaltrack list</td></tr>");
		int j=1;
		String checked = "";
		if(defendants == null || defendants.size() == 0){
		    if(owners.size() == 1){
			checked="checked=\"checked\"";
		    }
		}
		for(Owner one:owners){
		    out.println("<tr><td>");
		    out.print("<input type=\"checkbox\" name=\"ownId\" value=\""+one.getName_num()+"\" "+checked+"/>"+(j++));
		    out.println("</td><td>");
		    out.println(one.getFullName());
		    out.println("</td><td>");
		    out.println(one.getAddress());
		    out.println("</td><td>");
		    out.println(one.getCityStateZip());
		    out.println("</td></tr>");
		}
		out.println("</table>");				
	    }
	    if(defendants != null && defendants.size() > 0){
		out.println("<tr><td><table width=100%>");
		out.println("<caption>Legaltrack matching defendants</caption>");
		out.println("<tr><td colspan=4>This a list of defendants in legaltrack"+
			    "that matched the owners names listed above. Verify their "+
			    "information and if you think they are the same people/businesses, check mark the checkbox in front of their name to be used instead of the owners listed above</td></tr>");
		int j=1;
		for(Defendant one:defendants){
		    out.println("<tr><td>");
		    out.println("<input type=checkbox name=defId value='"+
				one.getDid()+"'>"+(j++));
		    out.println("</td><td>");
		    out.println(one.getFullName());
		    out.println("</td><td>");
		    out.println(one.getAddress().getAddress());
		    out.println("</td><td>");
		    out.println(one.getAddress().getCityStateZip());
		    out.println("</td></tr>");
		}
		out.println("</table>");	
	    }	
	    if(user.canEdit()){
		out.println("<tr><td align=\"right\">  "+
			    "<input type=\"submit\" name=\"action\" "+
			    "value=\"Save\" />&nbsp;&nbsp;"+
			    "</td></tr>");
	    }
	}
	out.println("</table>");
	out.println("<table><tr><td><a href=\""+url+"Rental?action=zoom&id="+
		    legal.getRental_id()+"\">Back to Rental: "+legal.getRental_id()+"</a>");
	out.println("</td></tr>");
	out.println("<tr><td><a href=\""+url+"StartLegal?action=zoom&id="+
		    id+"\">Back to Legal Record: "+id+"</a>");
	out.println("</td></tr>");
	out.println("</table>");
	//	
	out.print("</body></html>");
	out.close();
	//
    }
    Defendant setDefendantFromOwner(Owner owner){
	Defendant def = null;
	if(owner != null){
	    def = new Defendant(debug);
	    def.setFullName(owner.getFullName()); // will be splitted
	    String strAddr = owner.getAddress();
	    Address address = addrHand.extractAddress(strAddr);
	    address.setCity(owner.getCity());
	    address.setState(owner.getState());
	    address.setZip(owner.getZip());
	    DefAddress addr = new DefAddress(debug);
	    addr.setAddress(address);
	    def.setAddress(addr);			
	    def.setPhone(owner.getWorkPhone());
	    def.setEmail(owner.getEmail());			
	    String back = def.doInsert(); // mysql
	    if(!back.equals("")){
		logger.error(back);
	    }
	}
	return def;
    }
	
}





















































