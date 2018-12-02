import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * TODO Kommentare schreiben
 * 
 * @author Steffen Dworsky, Ramón Schultz
 *
 */
class ClientWorker implements Runnable {
	private Socket socket;
	private JTextArea textArea;
	private PrintWriter out = null;
    private String clientname;

	ClientWorker(Socket socket, JTextArea textArea) {
		this.socket = socket;
		this.textArea = textArea;   
	}

	public void run(){
		String line;
		BufferedReader in = null;
		Useradmin admin = new Useradmin();
		String username = "";
		char[] password = null;
        try{
            InputStream inStream = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
            out = new	 PrintWriter(socket.getOutputStream(), true);

            boolean accepted = false;
            do {         	
            	out.println("Benutzername:");
            	username = in.readLine();
            	out.println("Password:");
            	password = in.readLine().toCharArray();
            	Date timestamp = SocketThrdServer.timestamp.get(socket.getInetAddress());
            	Date now = new Date();
            	if (timestamp == null || (now.getTime() - timestamp.getTime()) >= 1000)  
            	{
            		SocketThrdServer.timestamp.put(socket.getInetAddress(),new Date());
            		accepted = admin.checkUser(username, password);
            		if (accepted)
            		{
            			synchronized(SocketThrdServer.clients)
            			{
            				if (SocketThrdServer.clients.containsKey(username))
            				{
            					out.println("Benutzer "+ username + " bereits angemeldet");
            					accepted = false;
            					break;
            				} else {
            					setClientName(username);
            					SocketThrdServer.clients.put(username,this);
            				}
            			}
            			out.println("Hallo " + username);
            			textArea.append(username + " hat sich eingeloggt. \n");
            		} else
            		{
            			out.println("Benutzereingabe falsch!");
            		}                    		
            	} else {
            			out.println("Bruteforce detection!");
                }
         } while(!accepted);

            while(accepted){
                line = in.readLine();
                if (line != null)
                {
                    broadcast(clientname + ": " + line);
                }
                else {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Read failed");
            System.exit(-1);
        } finally {

            synchronized(SocketThrdServer.clients)
            {
                if (SocketThrdServer.clients.remove(clientname) != null)
                {
                    broadcast(">> " +clientname + " hat sich abgemeldet. ");
                }
            }
            try{
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close socket");
                System.exit(-1);
            }

        }

	}

	private synchronized void broadcast(String msg){
        textArea.append(msg + "\n");
		for (ClientWorker client:SocketThrdServer.clients.values()){
			client.send(msg);
		}
	}



	public void send(String msg){
		out.println(msg);
	}

    private void setClientName(String username){
        clientname = username;
    }

    public String getClientname()
    {
        return clientname;
    }
}
