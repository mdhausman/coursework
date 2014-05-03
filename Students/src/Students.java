/**
 *  Students.java
 *  @author Maura Hausman
 *  @date 2013
 *  
 *  A program for manipulating a fictitious database on the UMass Boston 
 *  network. Written for a course homework assignment. This was used as the 
 *  official solution for the assignment.
 *  
 *  Uses Oracle's JDBC.
 */

import java.sql.*;
import javax.sql.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.Console;

class Students {
   // the host name of the server and the server instance name/id
	private static final String ORACLE_SERVER = "dbs2.cs.umb.edu";
	private static final String ORACLE_SERVER_SID = "dbs2";
	
	private static final int UNDECLARED = -2000;
	private static final String MENU = 
	"MAIN MENU\n"
	+
	"L # List: lists all available courses\n"
	+
   "E # Enroll: enroll in a course\n"
   +
   "W # Withdraw: withdraw from a course\n"
   +
   "S # Search: search courses by name\n"
   +
   "M # My Classes: lists all classes enrolled in by the active student.\n"
   +
   "X # Exit: exit application\n"
   +
   "Please enter a command";
	
	public static void main(String args[]) {
		Connection conn = getConnection();
		if (conn == null)
		   System.exit(1);
		
		int activeSID = entryPrompt(conn);
		
		mainMenu(activeSID, conn);
	}

	private static Connection getConnection(){

		// first we need to load the driver
		String jdbcDriver = "oracle.jdbc.OracleDriver";
		try {
			Class.forName(jdbcDriver); 
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Get username and password
		Scanner input = new Scanner(System.in);
		System.out.print("Username:");
		String username = input.nextLine();
		System.out.print("Password:");
		//the following is used to mask the password
		Console console = System.console();
		String password = new String(console.readPassword()); 
		String connString = "jdbc:oracle:thin:@" + ORACLE_SERVER + ":1521:"
				+ ORACLE_SERVER_SID;

		System.out.println("Connecting to the database...");
	
		Connection conn;
		// Connect to the database
		try{
			conn = DriverManager.getConnection(connString, username, password);
			System.out.println("Connection Successful");
		}
		catch(SQLException e){
			System.out.println("Connection ERROR");
			e.printStackTrace();	
			return null;
		}

		return conn;
	}
	
	private static int entryPrompt(Connection conn){
	   
		int activeSID = UNDECLARED;
		String sname, sql;
		
		while (activeSID == UNDECLARED){
		   Scanner input = new Scanner(System.in);
		   System.out.println("Please enter your SID, or -1 for new student:");
		   try {
		      activeSID = input.nextInt();
		      if (activeSID == -1){
		         System.out.println("Create new student? (Y/n)");
		         if(input.next().startsWith("Y")){
		            System.out.println("Please enter your new SID:");
		            activeSID = input.nextInt();
		            System.out.println("Please enter your name:");
		            sname = input.next();
		            
		            sql = "INSERT INTO Students VALUES(" + activeSID + ",'" +
		                     sname + "')";
		            System.out.println(sql);
		           	Statement stmt = conn.createStatement();
		            stmt.executeUpdate(sql);
		         }else{
		            activeSID = UNDECLARED;
		         }
		      }else{
		         sql = "SELECT sid FROM Students WHERE sid = " + activeSID;
	         	Statement stmt = conn.createStatement();
	            ResultSet rs = stmt.executeQuery(sql);
	            if(!rs.next()){
	               System.out.println("Unknown SID " + activeSID);
	               activeSID = UNDECLARED;
	            }
		      }
		   }catch(InputMismatchException e){
		      System.out.println("Invalid input.");
		      activeSID = UNDECLARED;
		   }catch (SQLException e){
		      System.out.println(e.getMessage());
		      activeSID = UNDECLARED;
		   }
		}
		return activeSID;
	}
	
	private static void mainMenu(int activeSID, Connection conn){
	   String command;
		while(true){
		   Scanner input = new Scanner(System.in);
		   clearConsole();
		   System.out.println(MENU);
		   command = input.next().toLowerCase();
		   switch (command.charAt(0)) {
		      case 'l': 
		         listCourses(conn);
		         break;
		      case 'e': 
		         enrollMenu(activeSID, conn);
		         break;
		      case 'w':
		         withdrawMenu(activeSID, conn);
		         break;
		      case 's':
		         searchMenu(conn);
		         break;
		      case 'm':
		         myCourses(activeSID, conn);
		         break;
		      case 'x':
		         System.exit(0);
		      default:
		         System.out.println("\nI didn't understand that.");
		         break;
		   }
		   
		}
	}
	
	private static void listCourses(Connection conn){ //list all courses
	   clearConsole();
	   
	   String sql = "SELECT * FROM Courses";
	   try {
	  	 Statement stmt = conn.createStatement();
		   ResultSet rs = stmt.executeQuery(sql);
	      int cid, credits;
	 	   String cname;
	 	   System.out.println("COURSES");
	 	   while(rs.next()){
	 	      cid = rs.getInt(1);
	 	      cname = rs.getString(2);
	 	      credits = rs.getInt(3);
	 	      System.out.println("" + cid + " " + cname + ", " + 
	 	                           credits + " credits");
	 	   }
	 	   rs.close();
	   }catch (SQLException e){
	      System.out.println(e.getMessage());
	      return;
	   }
	   
	   Scanner input = new Scanner(System.in);
	   System.out.println("Press <RET> to return to the menu.");
	   input.nextLine();
	}
	
	private static void enrollMenu(int sid, Connection conn){ //enroll in courses
	   clearConsole();
	   int cid;
	   System.out.println("ENROLL");
	   while(true){
	      Scanner input = new Scanner(System.in);
	      try {
	         System.out.println("Please give a CID, or -1 to cancel.");
	         cid = input.nextInt();
	         if (cid < 0)
	            return;
	     
	      	Statement stmt = conn.createStatement();
		      String check = "SELECT * FROM Enrolled WHERE cid = " + cid + " AND " +
		                   "sid = " + sid;
		      ResultSet rs = stmt.executeQuery(check);
	         if (rs.next()){//results exist
	            System.out.println("You're already registered for this course!");
	         }else{
	            String sql = "INSERT INTO Enrolled VALUES(" + 
	                           sid + "," + cid + ")";
	            stmt.executeUpdate(sql);
	            System.out.println("You are now registered for course number " +
	                                 cid);
	         }
	      }catch (SQLException e) {
	         System.out.println(e.getMessage());
	      }catch (InputMismatchException e){
	         System.out.println("Invalid input.");
	      }
	   }
	}
	
	private static void withdrawMenu(int sid, Connection conn){//withdraw from courses
	   clearConsole();
	   int cid;
	   System.out.println("WITHDRAW");
	   while(true){
	      Scanner input = new Scanner(System.in);
	      try {
	         System.out.println("Please give a CID, or -1 to cancel.");
	         cid = input.nextInt();
	         if (cid < 0)
	            return;

	      	Statement stmt = conn.createStatement();
		      String check = "SELECT * FROM Enrolled WHERE cid = " + cid + " AND "
		                        + "sid = " + sid;
		      ResultSet rs = stmt.executeQuery(check);
	         if (!rs.next()){//results don't exist
	            System.out.println("You're not registered for this course!");
	         }else{
	            String sql = "DELETE FROM Enrolled WHERE sid = " + sid + " AND "
	                           + "cid = " + cid;
	            stmt.executeUpdate(sql);
	            System.out.println("You are now withdrawn from course number " +
	                                 cid);
	         }
	      }catch (SQLException e) {
	         System.out.println(e.getMessage());
	      }catch (InputMismatchException e){
	         System.out.println("Invalid input.");
	      }
	   }
	}
	
	private static void searchMenu(Connection conn){ //search among course names
	   clearConsole();
	   String term;
	   System.out.println("SEARCH");
	   while(true){
	      Scanner input = new Scanner(System.in);
	      try {
	         System.out.println("Please enter a substring of the name of the " +
	                           "course you're looking for:");
	         term = input.next();
	      
	      	Statement stmt = conn.createStatement();
		      String check = "SELECT * FROM Courses WHERE cname LIKE '%" + 
		                        term + "%'";
	      	ResultSet rs = stmt.executeQuery(check);
	         int cid, credits;
	         String cname;
	         boolean found = false;
	         while(rs.next()){
	            cid = rs.getInt(1);
	            cname = rs.getString(2);
	            credits = rs.getInt(3);
	            System.out.println("" + cid + " " + cname + ", " + 
	                           credits + " credits");
	            found = true;
	         }
	         rs.close();
	         if (!found) System.out.println("No results found.");
	         
	         System.out.println("Search another term? (Y/n)");
	         if(!input.next().startsWith("Y"))
	            return;
	      }catch (SQLException e) {
	         System.out.println(e.getMessage());
	      }catch (InputMismatchException e){
	         System.out.println("Invalid input.");
	      }
	   }
	}
	
	private static void myCourses(int sid, Connection conn){ //list my courses
	   clearConsole();
	   String sql = "SELECT C.cid, C.cname FROM Enrolled E, Courses C WHERE " +
	                "E.sid = " + sid + "AND C.cid = E.cid";
	   try {
	  	 Statement stmt = conn.createStatement();
	  	 ResultSet rs = stmt.executeQuery(sql);
	      
	      int cid;
	      String cname;
	      boolean found = false;
	 	   System.out.println("COURSES");
	 	   while(rs.next()){
	 	      cid = rs.getInt(1);
	 	      cname = rs.getString(2);
	 	      System.out.println("" + cid + " " + cname);
	 	      found = true;
	 	   }
	 	   rs.close();
	 	   if (!found) System.out.println("You are not registered for any " +
	 	                                    "courses.");
	   }catch (SQLException e){
	      System.out.println(e.getMessage());
	      return;
	   }
	   
	   Scanner input = new Scanner(System.in);
	   System.out.println("Press <RET> to return to the menu.");
	   input.nextLine();
	}
	
	/* a quick console clearing method to beautify my menu screens.
	 * only slightly modified from where I found it on StackOverflow.
	 */
	private static void clearConsole(){
     
     System.out.print(((char) 27)+"[2J");
     
     //None of this works for some reason. Clunky hack above.
     /* Process p;
      try {
         if (System.getProperty("os.name").contains("Windows"))
            p = Runtime.getRuntime().exec("cls");
         else
            p = Runtime.getRuntime().exec("clear");
            
         p.waitFor();
      }
      catch (Exception e){
         //console didn't clear, but whatever. Life moves on.
      }*/
   }
}
