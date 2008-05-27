// $Id$

package com.cosylab.vdct.rdb.group;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import com.cosylab.vdct.rdb.RdbDataMapper;

// SQLHelper:
// Connects to EPICS RDB Schema.
//
// Also some shortcuts, inspired by perl-DBI,
// for getting one row, single column from all returned rows etc.
public class SQLHelper
{
    private static SQLHelper sql_helper;
    
    public static int debug=1;     // compile-time option
    public static boolean use_MySQL=false;
    private Connection connection; // init. in constructor

    static public SQLHelper create(RdbDataMapper mapper)
    {
        sql_helper = new SQLHelper();
        try
        {
        	sql_helper.connection = mapper.createNewConnection();
        }
        catch(Exception e)
        {
            System.err.println("SQLHelper exception: " + e.getMessage());
            e.printStackTrace();
            sql_helper = null;
        }
        return sql_helper;
    }
    
/*    
    static public SQLHelper create()
    {
        sql_helper = new SQLHelper();
        try
        {
            String mysql = "epics";//System.getProperty("EPICS_RDB_MYSQL", "");
            String two_task = System.getProperty("TWO_TASK", "");
            String oracle_userid = System.getProperty("ORACLE_USERID", "");
            String host     = "localhost";
            String DB       = "DEVL";
            String user     = "EPICS_SA";
            String password = "epics";
	    String connect_txt;
            int pos;

            // Use MySQL or Oracle?
            if (StringUtil.isValid(mysql))
            {
                DB=mysql;
                user="testuser";//System.getProperty("USER", "");
                password="password";

                // Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		connect_txt = "jdbc:mysql://" + host + "/" + DB +
                    "?user=" + user + "&password=" + password;
                if (debug>0)
		{
		    System.out.println("EPICS_RDB_MYSQL=" + mysql);
		    System.out.println("Connecting to MySQL DB '" + DB
				       + "' on '" + host + 
                                       "' as '" + user + "/" + password + "'");
		    System.out.println("Connection String '" + connect_txt
				       + "'");
                }
                use_MySQL = true;

		sql_helper.connection = DriverManager.getConnection(
			    connect_txt);
            }
            else
            {
                if (StringUtil.isValid(two_task))
                {
                    // TWO_TASK=<HOST>.<DB>
                    pos = two_task.lastIndexOf('.');
                    if (pos >= 0)
                    {
                        host = two_task.substring(0, pos);
                        DB = two_task.substring(pos+1);
                    }
                }
                if (StringUtil.isValid(oracle_userid))
                {
                    // ORACLE_USERID=<USER>/PASSWORD@<HOST>.<DB>
                    StringTokenizer parser =
                        new StringTokenizer(oracle_userid,"/@");
                    if (parser.hasMoreTokens())
                        user = parser.nextToken();
                    if (parser.hasMoreTokens())
                        password = parser.nextToken();
                    if (parser.hasMoreTokens())
                    {
                        String host_db = parser.nextToken();
                        pos = host_db.lastIndexOf('.');
                        if (pos >= 0)
                        {
                            host = host_db.substring(0, pos);
                            DB = host_db.substring(pos+1);
                        }
                    }
                }
                if (debug>0)
                {
                    System.out.println("TWO_TASK=" +
                                       (StringUtil.isValid(two_task) ?
                                        two_task : "<undefined>"));
                    System.out.println("ORACLE_USERID=" +
                                       (StringUtil.isValid(oracle_userid) ?
                                        oracle_userid : "<undefined>"));
                    System.out.println("Connecting to '" + DB
                                       + "' on '" + host + 
                                       "' as " + user + "/" + password);
                }
                
                // Glue jdbc to Oracle
                Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

                // DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
                
                // Connect to RDB as user/password
                sql_helper.connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + host + ":1521:" + DB, user, password);
                if (debug>1)
                {
                    DatabaseMetaData meta = sql_helper.connection.getMetaData();
                    System.out.println ("Database:");
                    System.out.println (meta.getDatabaseProductName() +
                                        ", version " +
                                        meta.getDatabaseProductVersion());
                    System.out.println ("Driver  :");
                    System.out.println (meta.getDriverName() +
                                        ", version " +
                                        meta.getDriverVersion());
                }
            }
            sql_helper.connection.setAutoCommit(false);
        }
        catch(Exception e)
        {
            System.err.println("SQLHelper exception: " + e.getMessage());
            e.printStackTrace();
            sql_helper = null;
        }
        return sql_helper;
    }
*/

    static public SQLHelper get()
    {   return sql_helper; }

    public Connection getConnection()
    {   return connection;  }
        
    // Quote string:
    // Not complete, does not handle embedded '
    public static String quote(String var)
    {   return "'" + var + "'";   }

    public static String quote(StringBuffer var)
    {   return quote(var.toString());   }

    // Returns first/only result of string query.
    public String queryString(String sql)
    {
        try
        {
            String result = null;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                result = rs.getString(1);
            rs.close();
            stmt.close();
            return result;
        }
        catch (Exception e)
        {
            System.err.println("SQLHelper exception: " + e.getMessage());
            System.err.println("sql statement: " + sql);
            e.printStackTrace();
        }
        return null;
    }
    
    // Returns results of string query.
    // Gives null in case of error or empty result,
    // otherwise it's a String-Vector
    public String [] queryRow(String sql, int count)
    {
        try
        {
            String result[] = null;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
            {
                result = new String [count];
                int i;

                for (i=0; i<count; ++i)
                    result[i] = rs.getString(i+1);
            }
            rs.close();
            stmt.close();
            return result;
        }
        catch (Exception e)
        {
            System.err.println("SQLHelper exception: " + e.getMessage());
            System.err.println("sql statement: " + sql);
            e.printStackTrace();
        }
        return null;
    }

    // Returns null or Vector of rows,
    // each entry is first column of result set
    public Vector queryRows(String sql)
    {
        Vector result = null;
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
            {
                result = new Vector();
                do
                    result.add (rs.getString(1));
                while (rs.next());
            }
            rs.close();
            stmt.close();
            return result;
        }
        catch (Exception e)
        {
            System.err.println("SQLHelper exception: " + e.getMessage());
            System.err.println("sql statement: " + sql);
            e.printStackTrace();
        }
        return null;
    }

    // Execute UPDATE or INSERT, return # of affected rows
    // Returns 0 for NOP, -1 for error
    public int execute(String sql)
    {
        try
        {
            Statement stmt = connection.createStatement();
            int rows = stmt.executeUpdate(sql);
            stmt.close();
            return rows;
        }
        catch (Exception e)
        {
            System.err.println("SQLHelper exception: " + e.getMessage());
            System.err.println("sql statement: " + sql);
            e.printStackTrace();
        }
        return -1;
    }
};

