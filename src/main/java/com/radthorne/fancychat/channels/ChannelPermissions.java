package com.radthorne.fancychat.channels;

import com.radthorne.fancychat.exceptions.ChannelPermissionException;
import org.bukkit.entity.Player;

public class ChannelPermissions
{


    public static boolean playerCanAutoJoin( Player player, Channel channel ) throws ChannelPermissionException
    {
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( player.hasPermission( permPrefix + "autojoin" ) && player.hasPermission( permPrefix + "read" ) )
        {
            return true;
        }
        throw new ChannelPermissionException( "Insufficient permission to auto-join this channel." );
    }


    public static boolean playerCanBan( Player player, Channel channel ) throws ChannelPermissionException
    {
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( player.hasPermission( permPrefix + "ban" ) || isMod( player ) )
        {
            return true;
        }
        throw new ChannelPermissionException( "Insufficient permission to ban a player from this channel." );
    }

    private static boolean isMod( Player player )
    {
        return player.hasPermission( "fancychat.mod" );
    }


    public static boolean playerCanDefaultTo( Player player, Channel channel ) throws ChannelPermissionException
    {

        // Perms?
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) )
        {
            throw new ChannelPermissionException( "Insufficient permission to read or speak in this channel." );
        }

        // Banned?
        if ( channel.isBanned( player ) )
        {
            throw new ChannelPermissionException( "Player has been banned from this channel." );
        }

        return true;
    }


    public static boolean playerCanForce( Player player, Channel channel ) throws ChannelPermissionException
    {
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( player.hasPermission( permPrefix + "ban" ) || isMod( player ) )
        {
            return true;
        }
        throw new ChannelPermissionException( "Insufficient permission to force a player into this channel." );
    }


    public static boolean playerCanJoin( Player player, Channel channel ) throws ChannelPermissionException
    {

        // Perms?
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) )
        {
            throw new ChannelPermissionException( "Insufficient permission to read or speak in this channel." );
        }

        // Banned?
        if ( channel.isBanned( player ) )
        {
            throw new ChannelPermissionException( "Player has been banned from this channel." );
        }

        return true;
    }


    public static boolean playerCanKick( Player player, Channel channel ) throws ChannelPermissionException
    {
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( player.hasPermission( permPrefix + "kick" ) || player.hasPermission( permPrefix + "ban" ) || isMod( player ) )
        {
            return true;
        }
        throw new ChannelPermissionException( "Insufficient permission to kick a player from this channel." );
    }


    public static boolean playerCanLeave( Player player, Channel channel ) throws ChannelPermissionException
    {
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( player.hasPermission( permPrefix + "leave" ) )
        {
            return true;
        }
        throw new ChannelPermissionException( "Insufficient permission to leave this channel." );
    }


    public static boolean playerCanRead( Player player, Channel channel ) throws ChannelPermissionException
    {
        // Perms?
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( !player.hasPermission( permPrefix + "read" ) && !player.hasPermission( permPrefix + "speak" ) )
        {
            throw new ChannelPermissionException( "Insufficient permission to read this channel." );
        }

        // Banned?
        if ( channel.isBanned( player ) )
        {
            throw new ChannelPermissionException( "Player has been banned from this channel." );
        }
        return true;
    }

    public static boolean playerCanSpeak( Player player, Channel channel ) throws ChannelPermissionException
    {
        // Perms?
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( !player.hasPermission( permPrefix + "speak" ) )
        {
            throw new ChannelPermissionException( "Insufficient permission to speak this channel." );
        }

        // Banned?
        if ( channel.isBanned( player ) )
        {
            throw new ChannelPermissionException( "Player has been banned from this channel." );
        }

        return true;
    }

    public static boolean playerCanMute( Player player, Channel channel ) throws ChannelPermissionException
    {
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( player.hasPermission( permPrefix + "mute" ) || isMod( player ) )
        {
            return true;
        }
        throw new ChannelPermissionException( "Insufficient permission to mute a player in this channel." );
    }

    public static boolean playerCanUnban( Player player, Channel channel ) throws ChannelPermissionException
    {
        String permPrefix = "fancychat.channel." + channel.getName().toLowerCase() + ".";
        if ( player.hasPermission( permPrefix + "unban" ) || isMod( player ) )
        {
            return true;
        }
        throw new ChannelPermissionException( "Insufficient permission to unban a player from this channel." );
    }
}
