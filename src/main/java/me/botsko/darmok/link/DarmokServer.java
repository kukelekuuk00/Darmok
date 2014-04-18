package me.botsko.darmok.link;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import me.botsko.darmok.Darmok;

public class DarmokServer implements Runnable {
    
    public static volatile List<DarmokServerListener> pool = new ArrayList<DarmokServerListener>();
    
    private final int port;
    private final String pass;
    
    
    /**
     * 
     * @param port
     * @param pass
     */
    public DarmokServer( final int port, final String pass ){
        this.port = port;
        this.pass = pass;
    }
    
    
    /**
     * 
     * @param message
     */
    public static void write( String message ){
        if( pool.isEmpty() ) return;
        for( DarmokServerListener l : pool ){
            l.send( message );
        }
    }
    
    
    /**
     * 
     */
    public void run(){

        try {
            
            ServerSocket ssocket = new ServerSocket(port);
            
            while(true){
                
                // remove dead connections
                for( DarmokServerListener l : pool ){
                    if( l.isDead() ){
                        pool.remove( l );
                    }
                }

                DarmokServerListener connHandler = new DarmokServerListener(ssocket.accept(), pass);
                new Thread(connHandler).start();
                Darmok.log("Chat link server running on port " + port);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}