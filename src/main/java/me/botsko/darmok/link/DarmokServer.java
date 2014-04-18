package me.botsko.darmok.link;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import me.botsko.darmok.Darmok;

public class DarmokServer implements Runnable {
    
    public static volatile ArrayList<DarmokServer> pool = new ArrayList<DarmokServer>();
    
    private String identifier;
    private boolean identified = false;
    private BufferedReader in;
    private BufferedWriter out;
    private String password;

    
    /**
     * 
     * @param socket
     * @param password
     */
    public DarmokServer(Socket socket, String password) {
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
     */
    public void run() {
        String line;
        try {
            while((line = in.readLine()) != null){
                this.handle(line);
            }
        } catch (IOException e) {
            synchronized(pool){
                pool.remove(this);
            }
            System.out.println("Removing client " + this.identifier
                    + " - error occurred while reading chat.");
            DarmokServer.update();
        }
    }
    
    
    /**
     * 
     * @param raw
     */
    public void handle( String raw ){
        Darmok.debug("Raw server message: " + raw);
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
                    synchronized(pool){
                        pool.add(this);
                    }
                    Darmok.log("Authentication successful for " + args[1]);
                    DarmokServer.update();
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
            for(DarmokServer i : pool){
                if(i.identifier.equals(id) && i.identifier != this.identifier){
                    i.send(raw);
                    break;
                }
            }
        }
        else if(args[0].equals("EMSG") && this.identified){
            String id = args[1].split("@")[1];
            for(DarmokServer i : pool){
                if(i.identifier.equals(id) && i.identifier != this.identifier){
                    i.send(raw);
                    break;
                }
            }
        }
        else if (this.identified){
            for(DarmokServer i : pool){
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
            synchronized(pool){
                pool.remove(this);
            }
            Darmok.log("Removing client " + this.identifier
                    + " from connection pool - error occurred while relaying chat.");
            DarmokServer.update();
        }
    }

    
    /**
     * 
     */
    public static void update(){
        String s = "UPD";
        for(DarmokServer i : pool){
            s += " " + i.identifier;
        }
        for(DarmokServer i : pool){
            i.send(s);
        }
    }
}