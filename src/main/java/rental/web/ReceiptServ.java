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

@WebServlet(urlPatterns = {"/ReceiptServ"})
public class ReceiptServ extends TopServlet{

    final static long serialVersionUID = 820L;
    PrintWriter os;

    String bgcolor = Rental.bgcolor;
    boolean userFoundFlag = false;
    static Logger logger = LogManager.getLogger(ReceiptServ.class);
    public static String[] allmonths = {"\n","Jan","Feb","March",
	"April","May","June",
	"July","August","Sept",
	"Oct","Nov","Dec"};
	
    /**
     * Generates the query form for bills.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	boolean success = true;
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	double old_balance = 0., balance = 0., rec_sum = 0.;
	//
	String name, value;
	String action="", id = "", bid = "", rid="", rec_date = "",
	    message = "";
	//
	String propAddr="";

	Enumeration<String> values = req.getParameterNames();
		
	out.println("<html><head><title>Receipt </title></head><body>");

	String [] vals;
	Receipt receipt = new Receipt(debug);
	Bill bill = new Bill(debug);
	Rent rent = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	

	    if (name.equals("id")){
		id = value;
		bill.setId(value);
	    }
	    else if (name.equals("bid")){
		bid = value;
		bill.setBid(value);
	    }
	    else if (name.equals("rid")){
		receipt.setRid(value);
		rid = value;
	    }	
	    else if (name.equals("action")){ 
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
	if(action.equals("zoom")){
	    String back = receipt.doSelect();
	    if(!back.equals("")){
		success = false;
		message += back;
	    }
	    else{
		bill = receipt.getBill();
		bid = bill.getBid();
		id = bill.getId();
	    }
	}
	else{
	    if(!bid.equals("")){
		String back = bill.doSelect();
		if(!back.equals("")){
		    success = false;
		    message += back;
		}
		else{
		    id = bill.getId();
		    old_balance = bill.getBalance();
		    if(old_balance <= 0.){
			message += " No balance due to be paid ";
			success = false;
		    }
		}
	    }
	}
	if(!user.canEdit()){
	    message += "You can not issue receipt ";
	    success = false;
	}
	if(!success){
	    out.println("<h3>Error</h3>");
	    out.println("<p>"+message+"</p>");
	    out.println("</body></html>");
	    out.close();
	    return;
	}
	//
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateReceipt(){	                 ");
	out.println("   var x = document.myForm.rec_sum.value;           "); 
	out.println("   var y = document.myForm.old_balance.value;        "); 
	out.println("   if(x.length == 0){                               "); 
	out.println("     alert(\"No received value entered yet\");      ");
	out.println("     document.myForm.rec_sum.focus();               "); 
	out.println("     return false;                                  ");
	out.println("   } else if(y.length == 0 || y ==\"0\"){            "); 
	out.println("     alert(\"No balance due to be paid\");          ");
	out.println("     return false;                                  ");
	out.println("	}                 					         ");
	out.println("   return true;                                  ");
	out.println("  }                 					         ");
	out.println("  function updateBalance(){	                 ");
	out.println("   var x = document.myForm.rec_sum.value;           "); 
	out.println("   var y = document.myForm.old_balance.value;        "); 
	out.println("   if(x > 0){                               "); 
	out.println("     var bal = y - x;     ");
	out.println("     document.myForm.balance.value = bal;         "); 
	out.println("	}                 					         ");
	out.println("  }                 					         ");	
	out.println("</script> ");
	out.println("  </head><body> ");
	out.println("<center>");
	//
	out.println("<h2>Rental Receipt </h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");	 
	}
	out.println("<form method=post name=myForm "+
		    "action='"+url+"ReceiptServ?' "+
		    "onSubmit=\"return validateReceipt()\">");		
	out.println("<input type=\"hidden\" name=\"bid\" value=\""+bid+"\">");
	if(!rid.equals(""))
	    out.println("<input type=\"hidden\" name=\"rid\" value=\""+rid+"\">");
	out.println("<table width=\"90%\" border>");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	if(rid.equals("")){
	    out.println("<tr><td align=right><b>Previous Payments:</b></td><td>$");
	    out.println("<input type=text name=paidSum maxlength=10 "+
			"value=\""+bill.getPaidSum()+"\" disabled size=10 />");
	    out.println("</td></tr>");
	    out.println("<tr><td align=right>");
	    out.println("<b>Amount Due:</b></td><td>$");
	    out.println("<input type=text name=old_balance maxlength=10 "+
			"disabled size=10 value=\""+bill.getBalance()+"\" /></td></tr>");
	    out.println("<tr><td align=right>"+
			"<b>Current Balance:</b></td><td>$");
	    out.println("<input type=text name=balance maxlength=10 "+
			"disabled size=10 value=\""+bill.getBalance()+"\" /></td></tr>");		  
	}
	else{
	    out.println("<tr><td colspan=\"2\" align=\"center\"><b> Receipt No. "+rid+"</b></td></tr>");
	}
	//
	rec_date = receipt.getRec_date();
	if(rec_date.equals("") && rid.equals("")){
	    rec_date = Helper.getToday();
	}
	out.println("<tr><td align=right><b>Received Date:</b></td><td>");
	out.println("<input name=\"rec_date\" maxlength=\"10\" "+
		    "value=\""+rec_date+"\" size=\"10\" /></td></tr>");
	//
	// Amount received
	out.println("<tr><td align=right><b>Amount Received:</b></td>");
	out.println("<td>$<input name=\"rec_sum\" maxlength=\"8\" "+
		    "size=\"8\" value=\""+receipt.getRec_sum()+"\" onchange=\"updateBalance();\" />");
	out.println("</td></tr>");
	out.println("<tr><td align=right><b>Received From:</b></td><td>");
	out.println("<input name=\"rec_from\" maxlength=\"40\" "+
		    "size=\"20\" value=\""+receipt.getRec_from()+"\" /></td></tr>");
	// Paid by
	out.println("<tr><td align=right><b>Paid Method</b></td>");
	String checked = "";
	String paid = receipt.getPaidMethod();
	if(paid.equals("") || paid.equals("cash")) checked="checked=\"checked\"";
	out.println("<td><input type=\"radio\" name=\"paid\" value=\"cash\" "+checked+" />Cash");
	checked="";
	if(paid.equals("check")) checked="checked=\"checked\"";
	out.println("<input type=\"radio\" name=\"paid\" value=\"check\" "+checked+"/>Check");
	checked="";
	if(paid.equals("mo")) checked="checked=\"checked\"";
	out.println("<input type=\"radio\" name=\"paid\" value=\"mo\" "+checked+"/>Money Order");
	if(paid.equals("credit")) checked="checked=\"checked\"";
	out.println("<input type=\"radio\" name=\"paid\" value=\"credit\" "+checked+"/>Credit/Debit Card");		
	out.println("</td></tr>");
	//
	// Check/Money order #
	out.println("<tr><td align=right><b>Check/Money Order #:</b></td>");
	out.println("<td><input name=\"check_no\" maxlength=\"20\" "+
		    "size=\"10\" value=\""+receipt.getCheck_no()+"\" />");
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td align=right>");		
	if(rid.equals("")){

	    out.println("<input type=\"submit\" name=\"action\" value=\"Create Receipt\">");
	}
	else{
	    out.println("<input type=\"submit\" name=\"action\" value=\"Printable Receipt\">");
	}
	out.println("</td></tr>");			
	out.println("</table>");
	out.println("<br>");
	out.println("<br>");
	out.println("<a href=\""+url+"BillServ?bid="+bid+"&action=zoom&id="+bill.getId()+"\">Back to Related Bill</a>");
	out.println("<br>");
	out.println("<br>");	
	out.println("<a href='javascript:window.close()'>Close This Window</a>");		
	out.print("</body></html>");
	out.flush();
	out.close();		
    }

    /**
     * Generates the bill.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{

	boolean success = true;
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	double old_balance = 0., balance = 0., rec_sum = 0.;
	//
	String name, value;
	String action="", id = "", bid = "", rec_date = "", message = "", rid="";
	String agentName="",agentAddr="",
	    agentAddr2="";	    
	//
	String propAddr="";

	Enumeration<String> values = req.getParameterNames();

	out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
	out.println("<html><head><title>Receipt </title></head><body>");

	String [] vals;
	Receipt receipt = new Receipt(debug);
	Bill bill = new Bill(debug);
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	

	    if (name.equals("id")){
		id = value;
		bill.setId(value);
		// receipt.setId(value);
	    }
	    else if (name.equals("bid")){
		bid = value;
		bill.setBid(value);
		receipt.setBid(value);
	    }
	    else if(name.equals("rid")){
		if(!value.equals(""))
		    receipt.setRid(value);
		rid = value;
	    }
	    else if (name.equals("rec_sum")) {
		receipt.setRec_sum(value);
	    }
	    else if (name.equals("rec_from")) {
		receipt.setRec_from(value);
	    }
	    else if (name.equals("rec_date")) {
		receipt.setRec_date(value);
	    }
	    else if (name.equals("check_no")) {
		receipt.setCheck_no(value);
	    }
	    else if (name.equals("paid")) {
		receipt.setPaidMethod(value);
	    }	
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}

	User user = null;
	Rent rent = null;
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
	if(true){
	    String back = bill.doSelect();
	    if(!back.equals("")){
		success = false;
		message += back;
	    }
	    else{
		if(rid.equals("")){
		    receipt.setBill(bill);
		    old_balance = bill.getBalance();
		    rec_sum = receipt.getRec_sum();
		    balance = old_balance - rec_sum;
		    if(balance < 0.) balance = 0.;
		    if(old_balance <= 0.){
			message += " No balance due to be paid ";
			success = false;
		    }
		    if(rec_sum <= 0.){
			message += " No money paid to have a receipt ";
			success = false;
		    }
		}
		else{
		    back = receipt.doSelect();
		    if(!back.equals("")){
			success = false;
			message += back;
		    }
		    else{
			rec_sum = receipt.getRec_sum();						
			bill = receipt.getBill();
			bid = bill.getBid();
			id = bill.getId();
		    }
		}
		rent = bill.getRent();
	    }			
	}
	if(!user.canEdit()){
	    message += "You can not issue receipt ";
	    success = false;
	}
	if(!success){
	    out.println("<h3>Error</h3>");
	    out.println("<p>"+message+"</p>");
	    out.println("</body></html>");
	    out.close();
	    return;
	}
	List<Address> addrs = rent.getAddresses();
	if(addrs != null && addrs.size() > 0){
	    for(Address addr:addrs){
		if(!propAddr.equals("")) propAddr += ", ";
		propAddr += addr.getAddress();
	    }
	}		
	//
	if(true){
	    //
	    // make a receipt number 
	    //
	    String back = receipt.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		//
		// receipt stuff
		//
		out.println("<br><br><br><br><br>");
		out.println("<br><br><center>");
		out.println("<b>RECEIPT&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b><br>");
		out.println("<b>No. "+receipt.getReceipt_no()+"</b></font><br><br>");
		//
		// table 
		out.println("<table><tr><td align=right>");
		//
		// received date
		rec_date = receipt.getRec_date();
		if(rec_date.equals("")) rec_date = Helper.getToday();
		out.println("Received Date: </td><td>");
		out.println(rec_date+"</td></tr>");
		//
		// from
		out.println("<tr><td align=right>");
		out.println("Received From: </td><td>");
		out.println(receipt.getRec_from()+"</td></tr>");
		//
		// prop address
		out.println("<tr><td align=right>");
		out.println("For Property Located at: </td><td>");
		out.println(propAddr+"</td></tr>");
		//
		// anount received
		out.println("<tr><td align=right>");
		out.println("Amount Received: </td><td>$");
		out.println(Helper.formatNumber(""+rec_sum)+"</td></tr>");
		//
		if(bill.getAppeal().equals("")){
		    //
		    // inspection fee
		    out.println("<tr><td align=right>");
		    out.println("Inspection Fee: </td><td>$");
		    out.println(Helper.formatNumber(""+bill.getInsp_fee())+"</td></tr>");
		    //
		    // Reinspection fee
		    out.println("<tr><td align=right>");
		    out.println("Reinspection Fee: </td><td>$");
		    out.println(Helper.formatNumber(""+bill.getReinsp_fee())+"</td></tr>");
		    //
		    // no show fee
		    out.println("<tr><td align=right>");
		    out.println("No Show Fee: </td><td>$");
		    out.println(Helper.formatNumber(""+bill.getNoshow_fee())+"</td></tr>");
		    //
		    if(bill.getSummary_fee() > 0){
			out.println("<tr><td align=right>");
			out.println("Failure to Provide Summary of Rights & Responsibilities: </td><td>$");
			out.println(Helper.formatNumber(""+bill.getSummary_fee())+"</td></tr>");
		    }
		    if(bill.getIDL_fee() > 0){
			out.println("<tr><td align=right>");
			out.println("Failure to Provide Inventory & Damage List: </td><td>$");
			out.println(Helper.formatNumber(""+bill.getIDL_fee())+"</td></tr>");
		    }
		    if(bill.getCredit() > 0){
			out.println("<tr><td align=right>");
			out.println("Credits: </td><td>$");
			out.println(Helper.formatNumber(""+bill.getCredit())+"</td></tr>");
		    }
		    out.println("<tr><td align=right>");
		    out.println("B.H.Q.A. Fine: </td><td>$");
		    out.println(Helper.formatNumber(""+bill.getBhqa_fine())+"</td></tr>");
		    //
		}
		else {
		    // appeal fee
		    out.println("<tr><td align=right>");
		    out.println("Appeal Fee: </td><td>$");
		    out.println(Helper.formatNumber(""+bill.getAppeal_fee())+"</td></tr>");
		}
		out.println("<tr><td align=right>");
		out.println("Balance Due: </td><td>$");
		out.println(Helper.formatNumber(""+balance)+"</td></tr>");
		//
		// Paid by
		String paid = receipt.getPaidMethod();
		if(paid.equals("check")) paid = "Check #"+receipt.getCheck_no();
		else if(paid.equals("mo")) paid = "Money Order #"+receipt.getCheck_no();
		else if (paid.equals("cash")) paid = "Cash";
		out.println("<tr><td align=right>");
		out.println("Paid by: </td><td>");
		out.println(paid);
		out.println("</td></tr>");
		//
		// Building /units
		out.println("<tr><td align=right>");
		out.println("Units/Buildings: </td><td>");
		if(bill.getUnit_cnt() > 0)
		    out.println(bill.getUnit_cnt()+" / "+bill.getBul_cnt()+"</td></tr>");
		else 
		    out.println(bill.getBath_cnt()+" / "+bill.getBul_cnt()+"</td></tr>");
		out.println("</table>");
		out.println("<br><br>");
		out.println("<b>Approved by the State Board of Accounts, "+
			    "2004.<b>");
		out.println("<br><br>");
		out.println("<font size=+1>Thank you for your payment"+
			    "</font><br>");
	    }
	}
	out.println("</body>");
	out.println("</html>");
    }

}























































