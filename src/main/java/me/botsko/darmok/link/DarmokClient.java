package me.botsko.darmok.link;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.commands.ChannelCommands;
import me.botsko.darmok.listeners.DarmokPlayerListener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DarmokClient implements Runnable {
    
    private String[] servers;
    public static PrintWriter out;
    private static BufferedReader in;
    
    private String hostname;
    private int port;
    private String password;
    public static String ident;
    private Darmok plugin;
    private String plugin_version;
    
    private ChannelCommands commandHandler;
    
    public DarmokClient(Darmok plugin, String version, FileConfiguration config){
        hostname = config.getString("darmok.link.hostname");
        port = config.getInt("darmok.link.port");
        password = config.getString("darmok.link.password");
        ident = config.getString("darmok.link.ident");
        this.plugin = plugin;
        this.plugin_version = version;
        this.commandHandler = new ChannelCommands(plugin);
//        this.playerListener = new DarmokPlayerListener(plugin);
    }

    public void run(){
        while(true){
            try {
                Socket relay = new Socket(this.hostname, this.port);
                DarmokClient.out = new PrintWriter(relay.getOutputStream(), true);
                DarmokClient.in = new BufferedReader(new InputStreamReader(relay.getInputStream()));
                out.println("VERSION " + plugin_version);
                out.println("AUTH " + ident + " " +  password);
                String line;
                while((line = in.readLine()) != null){
                    cmd_exec(line);
                }
            } catch (UnknownHostException e) {
                //e.printStackTrace();
                System.out.println("We were unable to connect to the Darmok Chat link. Stack trace: " + e);
                return;
            } catch (IOException e) {
                // e.printStackTrace();
                System.out.println("Darmok Chat link was broken. Reconnecting in 30 seconds.");
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
        }
    }
    
    public void cmd_exec(String line){
//        System.out.println("RECEIVED: " + line);
        String[] args = line.split(" ");
        if(args[0].equals("UPD")){
            servers = Arrays.copyOfRange(args, 1, args.length);
            return;
        }
        Command c = null;
        try{
            c = Command.valueOf(args[0]);
        } catch(Exception e){
            return;
        }
        String cmd;
        String[] new_args;
        DarmokUser player;
        switch(c){
            case CMSG:
                player = new RemoteUser(args[1]);
                cmd = args[2];
                for(int i = 3; i < args.length; i++){
                    cmd += " " + args[i];
                }
//                this.playerListener.onPlayerCommandPreprocess(player, cmd);
                return;
            case CBAN:
                cmd = "ch";
                player = new RemoteUser(args[1]);
                new_args = new String[]{"ban", args[2].split("@")[0], args[3]};
                break;
            case CUNBAN:
                cmd = "ch";
                player = new RemoteUser(args[1]);
                new_args = new String[]{"unban", args[2].split("@")[0], args[3]};
                break;
            case EMSG:
                Player ply = plugin.getServer().getPlayer(args[1].split("@")[0]);
                if(ply != null){
                    String response = "";
                    for(int i=2;i<args.length;i++){
                        response += args[i] + " ";
                    }
                    ply.sendMessage(Darmok.messenger.playerError(response.trim()));
                }
                return;
            default:
                return;
        }
//        System.out.println(player + ", " + cmd + ", " + new_args);
//        for(String x : new_args){
//            System.out.println(x);
//        }
        commandHandler.onCommand(player, plugin.getCommand(cmd), cmd, new_args);
    }

    
    enum Command{
        UPD,
        CBAN,
        CUNBAN,
        CMSG,
        EMSG;
    }
}



interface Handler{
    public void handle(String[] args);
}