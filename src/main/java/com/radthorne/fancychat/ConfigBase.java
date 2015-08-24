package com.radthorne.fancychat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;

public class ConfigBase
{

    /**
     *
     */
    protected Plugin plugin;

    /**
     *
     */
    protected FileConfiguration config;


    /**
     * @param plugin
     */
    public ConfigBase( Plugin plugin )
    {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig()
    {
        config = plugin.getConfig();
        return config;
    }


    /**
     * Loads language configuration
     *
     * @return
     */
    public FileConfiguration getChannelConfig()
    {

        // Read the base config
        FileConfiguration config = loadConfig( "defaults/", "channels" );

        // copy defaults and save config
//		config.options().copyDefaults(true);
        write( getFilename( "channels" ).getAbsolutePath(), "channels", config );

        return config;
    }


    /**
     * Loads language configuration
     *
     * @return
     */
    public FileConfiguration getProfanityConfig()
    {

        // Read the base config
        FileConfiguration config = loadConfig( "defaults/", "profanity" );

        // copy defaults and save config
//		config.options().copyDefaults(true);
        write( getFilename( "profanity" ).getAbsolutePath(), "profanity", config );

        return config;
    }


    /**
     * Returns base directory for config
     *
     * @return
     */
    protected File getDirectory()
    {
        File dir = new File( plugin.getDataFolder() + "" );
        return dir;
    }


    /**
     * Returns chosen filename with directory
     *
     * @return
     */
    public File getFilename( String filename )
    {
        File file = new File( getDirectory(), filename + ".yml" );
        return file;
    }


    /**
     * @param default_folder
     * @param filename
     * @return
     */
    protected FileConfiguration loadConfig( String default_folder, String filename )
    {
        File file = getFilename( filename );
        if ( file.exists() )
        {
            return YamlConfiguration.loadConfiguration( file );
        }
        else
        {
            // Look for defaults in the jar
            InputStream defConfigStream = plugin.getResource( default_folder + filename + ".yml" );
            if ( defConfigStream != null )
            {
                return YamlConfiguration.loadConfiguration( defConfigStream );
            }
            return null;
        }
    }


    /**
     * @param player
     * @return
     */
    public FileConfiguration loadPlayerConfig( Player player )
    {
        String filename = "/players/" + player.getName();
        File file = getFilename( filename );
        if ( file.exists() )
        {
            return YamlConfiguration.loadConfiguration( file );
        }
        return null;
    }


    /**
     * @param config
     */
    protected void saveConfig( String filename, FileConfiguration config )
    {
        File file = getFilename( filename );
        try
        {
            config.save( file );
        }
        catch ( IOException e )
        {
//			plugin.log("Could not save the configuration file to "+file);
            // Throw exception
        }
    }


    /**
     *
     */
    protected void write( String dir, String filename, FileConfiguration config )
    {
        try
        {
            BufferedWriter bw = new BufferedWriter( new FileWriter( dir, true ) );
            saveConfig( filename, config );
            bw.flush();
            bw.close();
        }
        catch ( IOException e )
        {

        }
    }
}
