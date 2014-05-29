package me.botsko.darmok.bridge;

import java.util.List;


import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class DarmokTownyBridge {
	
	
	/**
	 * 
	 * @param player
	 * @param channel
	 * @return
	 */
	public boolean playerHasTown( Player player ){
		try {
			Resident resident = TownyUniverse.getDataSource().getResident( player.getName() );
			return resident.hasTown();
		} catch (NotRegisteredException e) {
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	public boolean playerHasNation( Player player ){
		try {
			Resident resident = TownyUniverse.getDataSource().getResident( player.getName() );
			return resident.hasNation();
		} catch (NotRegisteredException e) {
		}
		return false;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<Player> getPlayersInPlayerTown( Player player ){
		try {
			Resident resident = TownyUniverse.getDataSource().getResident( player.getName() );
			if( resident.hasTown() ){
				return TownyUniverse.getOnlinePlayers( resident.getTown() );
			}
		} catch (NotRegisteredException e) {
		}
		return null;
	}

	
	/**
	 * 
	 * @return
	 */
	public List<Player> getPlayersInPlayerNation( Player player ){
		try {
			Resident resident = TownyUniverse.getDataSource().getResident( player.getName() );
			if( resident.hasTown() ){
				return TownyUniverse.getOnlinePlayers( resident.getTown().getNation() );
			}
		} catch (NotRegisteredException e) {
		}
		return null;
	}
}
