package server;


import java.rmi.Naming; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList; 
 
 
public class BoardServerImpl extends UnicastRemoteObject implements IBoardServer { 
 
	 protected BoardServerImpl() throws RemoteException {   
		 super(); 
	 } 
	 
	 ArrayList<String> users = new ArrayList<String>();  
	 ArrayList<BoardEvent> masterEvents;  
	 int usersSequence;  
	 int eventSequence;  
	 String adminID;    
	 
	 @Override  
	 public String joinBoard(String candidateID) throws RemoteException {   
		 String userID;   
		 ArrayList<String> list = null;   
		 synchronized(users) {    
			 list = new ArrayList<String>(users);   
		 }      
		 
		 // check if no current board users exist   
		 boolean usersExist = false;  
		 if(list != null) {    
			 for(String auser : list) {     
				 if(auser.charAt(0) != '#') {      
					 usersExist = true;      
					 break;     
					 }    
				 }   
		 }     
		 
		 if ((list == null) || (!usersExist)) {    
			 	 // start a fresh new board    
				 users = new ArrayList<String>(); 
				 masterEvents = new ArrayList<BoardEvent>();    
				 usersSequence = 0;    
				 eventSequence = 0;    
				 adminID = candidateID;    
				 userID = candidateID;    
				 approveUser(userID);   
			 } else {    
				 for (String auser : list) {     
					 if(auser.charAt(0) == '#') {      
						 	auser = auser.substring(1);     
					 }     
					 if(auser.equals(candidateID)) {
						 candidateID = candidateID + usersSequence;
						 break;     
					 }    
				 }
				 userID = candidateID;
				 BoardEvent event = new BoardEvent("joinRequest");
				 event.userID = userID;
				 addBoardEvent(event);
			 }
		 	 usersSequence++;
		 	 return userID;
	}    
	
	 @Override  
	public String getAdmin()  {   
		 return adminID;
	}    
	 
	public void addUserListEvent () {
		BoardEvent event = new BoardEvent("userList");
		synchronized(users) {    
			event.userList = new ArrayList<String>(users);
		}   
		
		addBoardEvent(event);  
	}      
	
	@Override  
	public void approveUser(String userID) {
		synchronized(users) {    
			users.add(userID);
		}   
		addUserListEvent();
	}
	
	@Override  
	public void bounceUser(String userID) {   
		boolean found = false;
		ArrayList<String> list = null;
		synchronized(users) {    
			list = new ArrayList<String>(users);
		}   
		for (String auser:list) {    
			if(auser.equals("#" + userID)) {
				found = true;
				break;
			} else if(auser.equals(userID)) { 
				found = true;
				int idx = list.indexOf(auser);
				synchronized(users) {
					users.set(idx, "#" + userID);
				}
				break;
			}
		}
		if(! found) {
			synchronized(users) {
				users.add("#" + userID);
			}
		}
		addUserListEvent();

	}
	
	@Override
	public synchronized void addBoardEvent(BoardEvent event) {
		event.eventID = eventSequence++;
		masterEvents.add(event);
	}
	
	@Override
	public synchronized ArrayList<BoardEvent> getBoardEvents(int startFrom) {
		return new ArrayList<BoardEvent>(
				masterEvents.subList(startFrom, masterEvents.size()));
	}
	
	public static void main(String args[]) {
		try {
			String serverName = "localhost";
			String serviceName = "BoardServer";
	
		 //System.setSecurityManager(new RMISecurityManager());
			IBoardServer obj = new BoardServerImpl();
		 // Bind this object instance to the name "BoardServer"
			Naming.rebind("rmi://"+serverName+"/"+serviceName, obj);
	
			System.out.println("Successful rebind service call");
	
		 }
		 catch (Exception e)
		 {
			 System.out.println("BoardServerImpl err: " + e.getMessage());
			 e.printStackTrace();
		 }
	 }
	private static final long serialVersionUID = 1L;
		
}
