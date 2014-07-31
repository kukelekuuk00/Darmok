package me.botsko.darmok.link;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.botsko.darmok.Darmok;

public class DarmokServerListener implements Runnable {
    
    private String identifier;
    private String version;
    private boolean identified = false;
    private BufferedReader in;
    private BufferedWriter out;
    private String password;
    private boolean isDead = false;

    
    /**
     * 
     * @param socket
     * @param password
     */
    public DarmokServerListener(Socket socket, String password) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.password = password;
    }
    
    /**
     * 
     * @return
     */
    public String getIdentifier(){
    	return identifier;
    }
    
    /**
     * 
     * @return
     */
    public String getVersion(){
    	return version;
    }
    
    /**
     * 
     * @return
     */
    public boolean isDead(){
        return isDead;
    }
    
    /**
     * 
     */
    public void run() {
        String line;
        try {
            while((line = in.readLine()) != null){
                this.handle(line);
            }
        } catch (IOException e) {
            isDead = true;
            DarmokServerListener.update();
        }
    }
    
    
    /**
     * 
     * @param raw
     */
    public void handle( String raw ){
        Darmok.debug("Raw server message (from client): " + raw);
        Darmok.debug("Indent: " + this.identifier);
        
        
        String[] args = raw.split(" ");

        // Command handling
        if(args[0].equals("VERSION")){
            this.version = args[1];
        }
        
        // AUTH
        else if(args[0].equals("AUTH")){
            if(args[2].equals(this.password)){
                this.identified = true;
                this.identifier = args[1];
                synchronized(DarmokServer.pool){
                    DarmokServer.pool.add(this);
                }
                Darmok.log("Authentication successful for " + args[1]);
                DarmokServerListener.update();
            } else {
                try {
                    this.in.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        }
        
        // CBAN
        else if(args[0].equals("CBAN") && this.identified){
        	LinkCommandHandler.cban(args);
        }
        
        // CUNBAN
        else if(args[0].equals("CUNBAN") && this.identified){
        	LinkCommandHandler.cunban(args);
        }
        
        // CMSG
        else if(args[0].equals("CMSG") && this.identified){
        	LinkCommandHandler.cmsg(args);
        }
        
        // DMSG
        else if(args[0].equals("DMSG") && this.identified){
        	LinkCommandHandler.dmsg(args);
        }
        else {
            this.send("DBG You are not authenticated. use AUTH.");
        }
    }
    
    
    /**
     * 
     * @param raw
     */
    public void send(String raw){
        try {
            this.out.write(raw + "\r\n");
            Darmok.debug("Writing " + raw + " TO " + this.identifier);
            this.out.flush();
        } catch (IOException e) {
            isDead = true;
            Darmok.log("Removing client " + this.identifier
                    + " from connection pool - error occurred while relaying chat.");
            DarmokServerListener.update();
        }
    }

    
    /**
     * 
     */
    public static void update(){
        String s = "UPD";
        for(DarmokServerListener i : DarmokServer.pool){
            s += " " + i.identifier;
        }
        for(DarmokServerListener i : DarmokServer.pool){
            i.send(s);
        }
    }
}