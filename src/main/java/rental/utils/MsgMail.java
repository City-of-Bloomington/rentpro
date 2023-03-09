package rental.utils;
import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.Address;
import javax.mail.internet.*;
import javax.activation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MsgMail{

    static Logger logger = LogManager.getLogger(MsgMail.class);
    final static long serialVersionUID = 610L;
    static String msgText = "This is a message body.\nHere's the second line.";
    static String to = "";
    static String from = "";
    static String host = "smtp.bloomington.in.gov";	
    static boolean debug = true;
    static String subject = "Start Legal";
    static String _subject = "Start Legal";
    static String _msgText = "This is a message body.\nHere's the second line.";
    static String _to = "";
    static String _from = "";
    static String _host = "";
    static boolean _debug = true;
    static String cc = null;
    static String bcc = null;
    /**
     * The main constructor.
     *
     * @param to2 to email address
     * @param from2 from email address
     * @param msg2 the message
     * @param cc2 the cc email address
     * @param bcc2 the blind carbon copy list 
     * @param debug2 the debug flag true|false
     */
    public MsgMail( String to2, String from2, String subject2, String msg2, String cc2, String bcc2, boolean debug2){

	to = to2;
	if(cc2 != null && !cc2.equals("")){
	    cc = cc2;
	}
	if(bcc2 != null && !bcc2.equals("")){				
	    bcc = bcc2;
	}
	from = from2;
	msgText = msg2;
	debug = debug2;
	if(subject2 != null) subject = subject2;
	if(bcc != null) to = null;
	//
	// create some properties and get the default Session
	//
    }
    public String doSend(){
	String back = "";
	Properties props = new Properties();
	props.put("mail.smtp.host", host);
	if (debug) props.put("mail.debug", "true");

	Session session = Session.getDefaultInstance(props, null);
	session.setDebug(debug);

	try {
	    // create a message
	    Message msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(from));
	    if(bcc == null){
		InternetAddress[] address = {new InternetAddress(to)};
		msg.setRecipients(Message.RecipientType.TO, address);
	    }
	    if(cc != null){
		InternetAddress[] address2 = {new InternetAddress(cc)};
		msg.setRecipients(Message.RecipientType.CC, address2);
	    }
	    if(bcc != null){
		InternetAddress[] address3 = javax.mail.internet.InternetAddress.parse(bcc);
		msg.setRecipients(Message.RecipientType.BCC, address3);
	    }
	    msg.setSubject(subject);
	    msg.setSentDate(new Date());

	    // If the desired charset is known, you can use
	    // setText(text, charset)
	    msg.setText(msgText);
	    //
	    Transport.send(msg);
	} catch (MessagingException mex){

	    back += " Email error "+mex;
	    logger.error(mex);
	    Exception ex = mex;
	    do {
		if (ex instanceof SendFailedException) {
		    SendFailedException sfex = (SendFailedException)ex;
		    Address[] invalid = sfex.getInvalidAddresses();
		    if (invalid != null) {
			System.out.println("    ** Invalid Addresses");
			if (invalid != null) {
			    for (int i = 0; i < invalid.length; i++) 
				System.out.println("         " + invalid[i]);
			}
		    }
		    Address[] validUnsent = sfex.getValidUnsentAddresses();
		    if (validUnsent != null) {
			System.out.println("    ** ValidUnsent Addresses");
			if (validUnsent != null) {
			    for (int i = 0; i < validUnsent.length; i++) 
				System.out.println("         "+validUnsent[i]);
			}
		    }
		    Address[] validSent = sfex.getValidSentAddresses();
		    if (validSent != null) {
			System.out.println("    ** ValidSent Addresses");
			if (validSent != null) {
			    for (int i = 0; i < validSent.length; i++) 
				System.out.println("         "+validSent[i]);
			}
		    }
		}
		System.out.println();
		if (ex instanceof MessagingException)
		    ex = ((MessagingException)ex).getNextException();
		else
		    ex = null;
	    } while (ex != null);
	}
	return back;
    }



}
