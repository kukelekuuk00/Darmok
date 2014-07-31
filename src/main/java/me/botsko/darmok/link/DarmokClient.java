package me.botsko.darmok.link;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import me.botsko.darmok.Darmok;

import org.bukkit.configuration.file.FileConfiguration;

public class DarmokClient implements Runnable {
    
    private static PrintWriter out;
    public static String ident;
    
    private static BufferedReader in;
    private String hostname;
    private int port;
    private String password;
    
    /**
     * 
     * @param version
     * @param config
     */
    public DarmokClient( FileConfiguration config ){
        hostname = config.getString("darmok.link.client.hostname");
        port = config.getInt("darmok.link.client.port");
        password = config.getString("darmok.link.client.password");
        ident = config.getString("darmok.link.client.name");
    }
    
    /**
     * 
     * @param message
     */
    public static void write( String message ){
        if( out == null ) return;
        out.println(message);
        out.flush();
    }

    /**
     * 
     */
    public void run(){
        while(true){
            try {
                Socket relay = new Socket(this.hostname, this.port);
                out = new PrintWriter(relay.getOutputStream(), true);
                DarmokClient.in = new BufferedReader(new InputStreamReader(relay.getInputStream()));
                out.println("VERSION " + Darmok.plugin_version);
                out.println("AUTH " + ident + " " +  password);
                String line;
                while((line = in.readLine()) != null){
                    cmd_exec(line);
                }
            } catch (UnknownHostException e) {
                Darmok.log("We were unable to connect to the Darmok Chat link. Stack trace: " + e);
                return;
            } catch (IOException e) {
                Darmok.log("Darmok Chat link was broken. Reconnecting in 30 seconds.");
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
        }
    }
    
    /**
     * 
     * @param line
     */
    public void cmd_exec(String line){
        Darmok.debug("Raw message (from server): " + line);
        String[] args = line.split(" ");
        if(args[0].equals("UPD")){
//            servers = Arrays.copyOfRange(args, 1, args.length);
            return;
        }
        Command c = null;
        try{
            c = Command.valueOf(args[0]);
        } catch(Exception e){
            return;
        }

        switch(c){
            case CMSG:
            	LinkCommandHandler.cmsg(args);
                break;
            case DMSG:
            	LinkCommandHandler.dmsg(args);
                break;
            case CBAN:
            	LinkCommandHandler.cban(args);
            	break;
            case CUNBAN:
            	LinkCommandHandler.cunban(args);
            	break;
            default:
            	break;
        }
        return;
    }

    /**
     * 
     * @author botskonet
     *
     */
    enum Command{
        UPD,
        CBAN,
        CUNBAN,
        CMSG,
        DMSG;
    }
}