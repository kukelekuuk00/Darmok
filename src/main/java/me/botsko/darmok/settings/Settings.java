package me.botsko.darmok.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.botsko.darmok.Darmok;
import me.botsko.darmok.channels.Channel;
import me.botsko.darmok.players.PlayerChannels;

import org.bukkit.entity.Player;

public class Settings {
	
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static PlayerChannels getPlayerChannels( Player player ){
		PlayerChannels channels = new PlayerChannels( player );
		try {

			Connection conn = Darmok.getDb();
            PreparedStatement s;
    		s = conn.prepareStatement ("SELECT * FROM darmok_player_channels WHERE player = ?");
    		s.setString(1, player.getName());
    		ResultSet rs = s.executeQuery();

    		while(rs.next()){

    			// Get channel
    			Channel channel = Darmok.getChannelRegistry().getChannel( rs.getString("channel") );
    			if( channel == null ){
    				// @todo delete channel from user
    				continue;
    			}
    			
    			// Is it the default?
    			if( rs.getInt("isDefault") == 1 ){
    				channel.setDefault( true );
    			}
    			
    			channels.addChannel( channel );

			}
    		
    		rs.close();
    		s.close();
    		conn.close();
            
        } catch (SQLException e) {
//        	plugin.logDbError( e );
        }
		return channels;
	}
	

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static void addChannelToPlayer( Player player, Channel channel ){
		try {
			
			Connection conn = Darmok.getDb();
			
			PreparedStatement s = conn.prepareStatement ("DELETE FROM darmok_player_channels WHERE player = ? AND channel = ?");
			s.setString(1, player.getName());
			s.setString(2, channel.getCommand());
			s.executeUpdate();
			
			s = conn.prepareStatement ("INSERT INTO darmok_player_channels (player,channel,isDefault) VALUES (?,?,0)");
			s.setString(1, player.getName());
			s.setString(2, channel.getCommand());
			s.executeUpdate();
			
			s.close();
			conn.close();
	
		} catch (SQLException e) {
//			plugin.logDbError( e );
		}
	}
	
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static void setDefaultChannelForPlayer( Player player, Channel channel ){
		try {
			Connection conn = Darmok.getDb();
			
			PreparedStatement s = conn.prepareStatement ("UPDATE darmok_player_channels SET isDefault = 0 WHERE player = ?");
			s.setString(1, player.getName());
			s.executeUpdate();
			
			s = conn.prepareStatement ("UPDATE darmok_player_channels SET isDefault = 1 WHERE player = ? AND channel = ?");
			s.setString(1, player.getName());
			s.setString(2, channel.getCommand());
			s.executeUpdate();
			
			s.close();
			conn.close();
		} catch (SQLException e) {
//			plugin.logDbError( e );
		}
	}
}