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

@WebServlet(urlPatterns = {"/BillListServ"})
public class BillListServ extends TopServlet{

    final static long serialVersionUID = 110L;
    static Logger logger = LogManager.getLogger(BillListServ.class);	
    PrintWriter os;
    String bgcolor = Rental.bgcolor;

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
	doGet(req, res);
    }
    //
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
	String name="", value="";
	String id="",str="", message="";
	int total=0;
	//
	// these are the default fees that could change on
	// annual basis
	//
	String bul_rate="50",  // building rate
	    unit_rate="12",    // unit rate
	    bath_rate="12",    // 
	    reinsp_rate="50",  // reinspection fees
	    noshow_rate="35",  // no show fees
	    appeal_fee="20",
	    summary_rate="25",
	    IDL_rate="25";
	String bul_cnt="1",unit_cnt="1",bath_cnt="", insp_fee="";
	String reinsp_date="",reinsp_cnt="",due_date="",noshow_date="";
	String action="",bid="",issue_date="",status="Unpaid",bhqa_fine="",
	    noshow_cnt="", paidSum="0",balance="0",prop_type="",
	    appeal="",credit="0",summary_flag="",
	    IDL_flag="";
	Enumeration<String> values = req.getParameterNames();
	String [] vals;

		
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
	    }
	}
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}

	//
	out.println("<!DOCTYPE html public \"-//W3C//DTD HTML 4//EN\">");
	out.println("<html><head><title>Rental Bill History</title>");
	Helper.writeWebCss(out, url);	
	out.println("  </head><body> ");
	out.println("<center>");
	Helper.writeTopMenu(out, url);	
	//
	out.println("<h2>Billing History </h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");	 
	}
	//
	// Check if there are bills issued before
	//
	if(!id.equals("")){
	    String titles[] = { "Bill ID",
		"Issue Date",
		"Due Date",
		"Total",
		"Balance",
		"Status"};
	    String rTitles[] = { "Receipt No.",
		"Received Date",
		"Received Sum"};
	    BillList bills = new BillList(debug, id);
	    String back = bills.find();
	    if(back.equals("") && bills.size() > 0){
		out.println("<br>");
		out.println("<h3>Issued Bills For Permit "+id);
		out.println("</h3>");
		for(Bill bl:bills){
		    out.println("<table border>");
		    out.println("<caption>Bill</caption>");
		    out.println("<tr>");
		    for(int i=0; i<titles.length;i++){
			out.println("<th>"+titles[i]+"</th>");
		    }
		    out.println("</tr>");
					
		    out.println("<tr>");
		    str = bl.getBid();
		    if(!str.equals("")){
			out.println("<td><a href="+url+"BillServ?"+
				    "action=zoom&bid="+str+
				    "&id="+id+
				    ">"+str+
				    "</a></td>");
		    }
		    else
			out.println("<td>&nbsp;</td>");
		    str = bl.getIssue_date();
		    out.println("<td>&nbsp;"+str+"</td>");
		    str = bl.getDue_date();
		    out.println("<td>&nbsp;"+str+"</td>");
		    str = ""+bl.getTotal();
		    out.println("<td>&nbsp;"+str+"</td>");
		    str = ""+bl.getBalance();
		    out.println("<td>&nbsp;"+str+"</td>");		
		    str = bl.getStatus();
		    out.println("<td>&nbsp;"+str+"</td>");
		    out.println("</tr>");
		    out.println("</table>");
		    ReceiptList rlist = bl.getReceipts();
		    if(rlist != null && rlist.size() > 0){
			out.println("<table border>");
			out.println("<caption>Receipts</caption>");
			out.println("<tr>");
			for(int i=0; i<rTitles.length;i++){
			    out.println("<th>"+rTitles[i]+"</th>");
			}
			out.println("</tr>");
			for(Receipt rc:rlist){
			    out.println("<tr>");
			    str = rc.getRid();
			    if(!str.equals("")){
				out.println("<td><a href="+url+"ReceiptServ?"+
					    "action=zoom&rid="+str+">"+str+
					    "</a></td>");
			    }
			    else
				out.println("<td>&nbsp;</td>");
			    str = rc.getRec_date();	
			    out.println("<td>&nbsp;"+str+"</td>");
			    str = ""+rc.getRec_sum();	
			    out.println("<td>&nbsp;"+str+"</td>");
			    out.println("</tr>");
			}
			out.println("</table>");
		    }
		}
	    }
	}
	out.println("<a href='javascript:window.close()'>Close This Window</a>");
	out.println("<br>");
	out.println("<br>");    
	out.print("</body></html>");
	out.close();
    }

}























































