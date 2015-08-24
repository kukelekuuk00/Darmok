package com.radthorne.fancychat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config extends ConfigBase
{


    /**
     * @param plugin
     */
    public Config( Plugin plugin )
    {
        super( plugin );
    }


    public FileConfiguration getConfig()
    {

        return plugin.getConfig();
    }
}