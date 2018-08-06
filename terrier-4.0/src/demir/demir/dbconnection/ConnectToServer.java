/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demir.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author meltem
 */
public class ConnectToServer {
    
    public static void main(String [] args)
    {
        Connect();
    }
    
public static Connection Connect(){
    
//    //String dbUrl = "jdbc:mysql://localhost/TurkishMedMySql";
//    //String dbClass = "com.mysql.jdbc.Driver";
//    
//    try {
///// TO DO MELTEM
////    String dbUrl = org.terrier.utility.ApplicationSetup.DB_URL;
////    String dbClass = org.terrier.utility.ApplicationSetup.DB_CLASS;
////    String dbUser = org.terrier.utility.ApplicationSetup.DB_USER;
////    String dbPassword = org.terrier.utility.ApplicationSetup.DB_PASSWORD;
//     //   Class.forName(dbClass);
//     /// TO DO MELTEM
//        
//      //  Connection con = DriverManager.getConnection (dbUrl, dbUser, dbPassword);
//      
//    } //end try
//    catch(ClassNotFoundException e) {
//        e.printStackTrace();
//    }
//    catch(SQLException e) {
//        e.printStackTrace();
//    }
    
     Connection con = null;
        try {
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rapidminer_repo?" +
//                    "user=root&password=xxx");
              con = DriverManager.getConnection("jdbc:mysql://localhost:3306/demir_tc?" +
                    "user=root&password=Meltem.48");
        } catch (SQLException ex) {
            Logger.getLogger(ConnectToServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return con;
}

public static Connection Connect(Properties prop){
    
    /*MELTEM DB CONNECTION PROPERTIES*/
    String dbUrl = prop.getProperty("db.url", "");
    String dbClass = prop.getProperty("db.class", "");
    String dbUser = prop.getProperty("db.user", "");
    String dbPassword= prop.getProperty("db.password", "");
    /*MELTEM DB CONNECTION PROPERTIES*/
    try {

        Class.forName(dbClass);
        Connection con = DriverManager.getConnection (dbUrl, dbUser, dbPassword);
        return con;
    } //end try

    catch(ClassNotFoundException e) {
        e.printStackTrace();
    }
    catch(SQLException e) {
        e.printStackTrace();
    }
    return null;
}  //end main

//public static Connection Connect(String dbUrl, String dbClass,
//        String dbUser, String dbPassword){
//    
//        try {
//
//        Class.forName(dbClass);
//        Connection con = DriverManager.getConnection (dbUrl, dbUser, dbPassword);
//        return con;
//    } //end try
//
//    catch(ClassNotFoundException e) {
//        e.printStackTrace();
//    }
//    catch(SQLException e) {
//        e.printStackTrace();
//    }
//    return null;
//}  //end main


public static void Disconnect(Connection con)
{
    try {
        if(con != null) {
            con.close();
        }
    } catch (SQLException ex) {
    Logger.getLogger(ConnectToServer.class.getName()).log(Level.SEVERE, null, ex);
    }
}

}
