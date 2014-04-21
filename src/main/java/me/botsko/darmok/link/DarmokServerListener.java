package me.botsko.darmok.link;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;

public class DarmokServerListener implements Runnable {
    
    private String identifier;
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
        String[] args = raw.split(" ");
        if(this.identifier != null){
            Darmok.debug(this.identifier + ": " + raw);
        }
        if(args[0].equals("VERSION")){
//            this.version = args[1];
        }
        else if(args[0].equals("AUTH")){
            if(!this.identified){
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
        }
        else if((args[0].equals("CBAN") || args[0].equals("UBAN")) && this.identified){
            String id = args[2].split("@")[1];
            for(DarmokServerListener i : DarmokServer.pool){
                if(i.identifier.equals(id) && i.identifier != this.identifier){
                    i.send(raw);
                    break;
                }
            }
        }
        else if(args[0].equals("CMSG") && this.identified){
            
            DarmokUser remoteUser = new RemoteUser(args[1]);
            
            String chatMessage = "";
            for(int i = 3; i < args.length; i++){
                chatMessage += " " + args[i];
            }
            
            Darmok.debug( "Finding channel for: " + args[2] );
            Channel channel = Darmok.getChannelRegistry().getChannel( args[2] );
            
            Darmok.getChatter().send( remoteUser, channel, chatMessage);
            
//            String id = args[1].split("@")[1];
//            for(DarmokServerListener i : DarmokServer.pool){
//                if(i.identifier.equals(id) && i.identifier != this.identifier){
//                    i.send(raw);
//                    break;
//                }
//            }
        }
        else if (this.identified){
            for(DarmokServerListener i : DarmokServer.pool){
                if(i.identifier != this.identifier){
                    i.send(raw);
                }
            }
        } else {
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
            Darmok.debug("DEBUG: WRITING " + raw + " TO " + this.identifier);
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