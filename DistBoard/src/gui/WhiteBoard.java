package gui;

/* 
 * Name: Walid Moustafa 
 * Student ID: 563080 
 * Subject: COMP90015 - Distributed Systems 
 * Assignment: Assignment 2 - Distributed Whiteboard 
 * Project: WhiteBoard 
 * File: WhiteBoard.java 
*/ 
import server.*;

import java.awt.BorderLayout; 
import java.awt.Dimension; 
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 
import java.net.MalformedURLException; 
import java.rmi.Naming; 
import java.rmi.NotBoundException; 
import java.rmi.RemoteException; 
import javax.swing.*; 
 
public class WhiteBoard  
{ 
 static String serverName;
 static String serviceName;
 static IBoardServer boardServer; 
 static String userID; 
 static String adminID; 
 static boolean isAdmin; 
 static UserPanel userPanel; 
 static SharedPanel sharedPanel; 
  
 private static void initialize()  
    { 
   
        //Create and set up the window. 
        final JFrame frame = new JFrame("White Board"); 
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        frame.addWindowListener(new WindowAdapter() { 
        	public void windowClosing(WindowEvent we) { 
        		if(!isAdmin) { //this implementation makes no sense
        			int choice = JOptionPane.showConfirmDialog(frame,  
        					"Are you sure you want to exit the application?", 
        					"Exit Confirmation", JOptionPane.YES_NO_OPTION); 
        			if(choice == JOptionPane.YES_OPTION) { 
        				try { 
        					boardServer.bounceUser(WhiteBoard.userID); 
        				} catch (RemoteException e) { 
        					e.printStackTrace(); 
        				} 
        				System.exit(0); 
        			} else { 
        				frame.setVisible(true); 
        			} 
        		} else { 
        			int choice = JOptionPane.showConfirmDialog(frame,  
        					"Are you sure you want to end the current Whiteboard Session?", 
        					"Exit Confirmation", JOptionPane.YES_NO_OPTION); 
        			if(choice == JOptionPane.YES_OPTION) { 
        				if(boardServer != null) { 
        					BoardEvent event = new BoardEvent("Terminate"); 
        					try { 
        						boardServer.addBoardEvent(event); 
        					} catch (RemoteException e) { 
        						e.printStackTrace(); 
        					} 
        				} 
            //System.exit(0); 
        			} else { 
        				frame.setVisible(true); 
        			} 
        		} 
         } 
       }); 
         
        frame.getContentPane().setLayout(new BorderLayout()); 
 
        sharedPanel = new SharedPanel(boardServer, userID, frame); 
        frame.getContentPane().add(sharedPanel, BorderLayout.CENTER); 
         
        JScrollPane scrollPane = new JScrollPane(); 
        userPanel = new UserPanel(boardServer, userID, scrollPane); 
        scrollPane.setPreferredSize(new Dimension(110,700)); 
        scrollPane.getViewport().add(userPanel); 
        frame.getContentPane().add(scrollPane, BorderLayout.EAST); 
 
  //frame.pack(); 
        frame.setSize(1100, 700); 
        frame.setResizable(false); 
        frame.setLocationRelativeTo(null); 
        frame.setVisible(false); 
         
        frame.addWindowFocusListener(new WindowAdapter() { 
            public void windowGainedFocus(WindowEvent e) { 
                sharedPanel.requestFocusInWindow(); 
            } 
        }); 
         
        // start EventDispatcher thread 
        new Thread(new EventDispatcher(sharedPanel, userPanel),"EventDispatcher").start(); 
     
} 
 
public static void main(String[] args) { 
	if (args.length != 2) { 
        System.err.println("usage: WhiteBoard ServerName ServiceName"); 
        System.exit(0); 
    }  
  
     serverName  = args[0]; 
     serviceName = args[1]; 
         
     try { 
      /*if(System.getSecurityManager()==null) { 
    System.setSecurityManager(new RMISecurityManager()); 
   }*/ 
		  boardServer = (IBoardServer) Naming.lookup("rmi://"+serverName+"/"+serviceName); 
		  String candidateID = null; 
		  while ((candidateID == null) || (candidateID.length() < 3) || (candidateID.length() > 8)) { 
			  candidateID = JOptionPane.showInputDialog(null, "Enter 3-8 letters user name","Login",JOptionPane.INFORMATION_MESSAGE); 
		  } 
		  userID = boardServer.joinBoard(candidateID); 
		  adminID = boardServer.getAdmin(); 
		  isAdmin = userID.equalsIgnoreCase(adminID) ? true : false; 
		  System.out.println("Assigned userID " + userID); 
     } catch (MalformedURLException e) { 
    	 e.printStackTrace(); 
    	 System.exit(-1); 
     } catch (RemoteException e) { 
    	 e.printStackTrace(); 
    	 System.exit(-1); 
     } catch (NotBoundException e) { 
    	 e.printStackTrace(); 
    	 System.exit(-1); 
     } 
             
       //Schedule a job for the event-dispatching thread: 
          //creating and showing this application's GUI. 
      javax.swing.SwingUtilities.invokeLater(new Runnable() { 
              public void run()  
              { 
                  initialize(); 
              } 
          }); 
    
      } 
} 