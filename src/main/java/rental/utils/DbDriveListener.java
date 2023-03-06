package rental.utils;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import oracle.jdbc.OracleDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbDriveListener implements ServletContextListener {

    private static final Logger log = LogManager.getLogger(DbDriveListener.class);
    private Driver oraDriver = null;
    private Driver mysqlDriver = null;
    // private com.mysql.jdbc.Driver mysqlDriver = null;
    /**
     * Register the drivers
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.oraDriver = new OracleDriver(); // load and instantiate the class
	try{
	    this.mysqlDriver = new com.mysql.jdbc.Driver();
	}catch(Exception ex){
	    System.err.println(ex);
	}
        boolean oraSkipReg = false;
	boolean mysqlSkipReg = false;
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver instanceof OracleDriver) {
                OracleDriver oraRegisteredDriver = (OracleDriver) driver;
                if (oraRegisteredDriver.getClass() == this.oraDriver.getClass()) {
                    // same class in the VM already registered itself
                    oraSkipReg = true;
                    this.oraDriver = oraRegisteredDriver;
                }
            }
	    else if(driver instanceof com.mysql.cj.jdbc.Driver){
		Driver myRegisteredDriver = (com.mysql.cj.jdbc.Driver) driver;
		if (myRegisteredDriver.getClass() == this.mysqlDriver.getClass()) {							 
		    mysqlSkipReg = true;
		    this.mysqlDriver = myRegisteredDriver;
		}
	    }
        }
        try {
            if (!oraSkipReg) {
                DriverManager.registerDriver(oraDriver);
            } else {
                log.debug("ora driver was registered automatically");
            }
            if (!mysqlSkipReg) {
                DriverManager.registerDriver(mysqlDriver);
            } else {
                log.debug("mysql driver was registered automatically");
            }						
            log.info(String.format("registered jdbc driver: %s v%d.%d", oraDriver,
				   oraDriver.getMajorVersion(), oraDriver.getMinorVersion()));
            log.info(String.format("registered jdbc driver: %s v%d.%d", mysqlDriver,
				   mysqlDriver.getMajorVersion(), mysqlDriver.getMinorVersion()));						
        } catch (SQLException e) {
            log.error(
		      "Error registering oracle or mysql driver: " + 
		      "database connectivity might be unavailable!",
		      e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deregisters JDBC driver
     * 
     * Prevents Tomcat 7 from complaining about memory leaks.
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (this.oraDriver != null) {
            try {
                DriverManager.deregisterDriver(oraDriver);
                log.info(String.format("deregistering jdbc driver: %s", oraDriver));
            } catch (SQLException e) {
                log.warn(
			 String.format("Error deregistering driver %s", oraDriver),
			 e);
            }
            this.oraDriver = null;
        } else {
            log.warn("No ora driver to deregister");
        }
        if (this.mysqlDriver != null) {
            try {
                DriverManager.deregisterDriver(mysqlDriver);
                log.info(String.format("deregistering jdbc driver: %s", mysqlDriver));
            } catch (SQLException e) {
                log.warn(
			 String.format("Error deregistering driver %s", mysqlDriver),
			 e);
            }
            this.mysqlDriver = null;
        } else {
            log.warn("No mysql driver to deregister");
        }
    }

}
		
