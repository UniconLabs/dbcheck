package net.unicon.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;

public class DbCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String driver;
    private String jndiName;
    private HashMap<String, String> driverClassNames = new HashMap<String, String>();
    {
        driverClassNames.put("1", "com.mysql.jdbc.Driver");
        driverClassNames.put("2", "oracle.jdbc.OracleDriver");
        driverClassNames.put("3", "net.sourceforge.jtds.jdbc.Driver");
        driverClassNames.put("4", "org.postgresql.Driver");
    }
    private String pass;
    private String sql;
    private String url;
    private String user;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Override the props file if the user passed in parameters
        // yes, StringUtils would make all of this cleaner, but I was trying to keep another jar out of it.
        if (null != req.getParameter("url") && !"".equals(req.getParameter("url")))
            url = req.getParameter("url");

        if (null != req.getParameter("sql") && !"".equals(req.getParameter("sql")))
            sql = req.getParameter("sql");
        if (sql == null || sql.trim().length() == 0) {
            sql = "select 1 from dual";
        }

        if (null != req.getParameter("user") && !"".equals(req.getParameter("user")))
            user = req.getParameter("user");

        if (null != req.getParameter("pass") && !"".equals(req.getParameter("pass")))
            pass = req.getParameter("pass");

        if (null != req.getParameter("driver") && !"".equals(req.getParameter("driver")))
            driver = req.getParameter("driver");

        if (null != req.getParameter("jndiName") && !"".equals(req.getParameter("jndiName")))
            jndiName = req.getParameter("jndiName");

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = new JdbcTemplate(getDataSource());
        } catch (NamingException e) {
            out.println("<body>");
            out.println("error");
            out.println("</body>");
            out.close();
            return;
        }
        int result = jdbcTemplate.queryForInt(sql);        
        out.println("<body>");
        if (1 == result)
            out.println("OK");
        else
            out.println("error");
        out.println("</body>");
        out.close();
    }

    /**
     * Try to build the JNDI lookup version first. If we don't have a lookup, then build one based on the props
     * @throws NamingException 
     */
    private DataSource getDataSource() throws NamingException {
        if (jndiName != null && !"".equals(jndiName)) {
            JndiObjectFactoryBean jofb = new JndiObjectFactoryBean();
            jofb.setJndiName(jndiName);
            return (DataSource) jofb.getJndiTemplate().lookup("java:comp/env/" + jndiName);
        } else {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setUrl(url);
            ds.setUsername(user);
            ds.setPassword(pass);
            ds.setDriverClassName(driverClassNames.get(driver));
            return ds;
        }
    }

    @Override
    public void init() {
        Properties properties = new Properties();
        InputStream in = getClass().getResourceAsStream("/db.properties");
        try {
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        url = properties.get("url").toString();
        pass = properties.get("pass").toString();
        user = properties.get("user").toString();
        sql = properties.get("sql").toString();
        driver = properties.get("driver").toString();
    }
}
