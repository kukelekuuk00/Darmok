package com.radthorne.fancychat.channels;

import com.radthorne.fancychat.FancyChat;
import com.radthorne.fancychat.JChat;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Channel
{

    private final String name;
    private final String shortName;
    private final String color;
    private final String format;
    private final int range;
    private final String context;

    private boolean isDefault = false;
    Set<UUID> muted = new HashSet<>();
    Set<UUID> banned = new HashSet<>();

    /**
     * @param command
     */
    public Channel( String name, String command, String color, String format, int range, String context )
    {

        this.name = name;
        this.shortName = command;
        this.color = color;
        this.format = format;
        this.range = range;
        this.context = context;
    }


    public void mute( Player player )
    {
        muted.add( player.getUniqueId() );
    }


    public void ban( Player player )
    {
        banned.add( player.getUniqueId() );
    }

    public void unban( Player player )
    {
        banned.remove( player.getUniqueId() );
    }


    public ArrayList<Player> getPlayers()
    {
        return FancyChat.getPlayerRegistry().getPlayersInChannel( this );
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }


    /**
     * @return
     */
    public String getShortName()
    {
        return shortName;
    }


    /**
     * @return
     */
    public String getColor()
    {
        return color;
    }


    /**
     * @return
     */
    public String getFormat()
    {
        return format;
    }


    /**
     * @return
     */
    public int getRange()
    {
        return range;
    }


    /**
     * @return
     */
    public String getContext()
    {
        return context;
    }


    /**
     * @return
     */
    public boolean isDefault()
    {
        return isDefault;
    }


    /**
     * @return
     */
    public void setDefault( boolean isDefault )
    {
        this.isDefault = isDefault;
    }


    /**
     * @param player
     * @return
     */
    public boolean isMuted( Player player )
    {
        return muted.contains( player.getUniqueId() );
    }

    /**
     * @param player
     * @return
     */
    public boolean isBanned( Player player )
    {
        return banned.contains( player.getUniqueId() );
    }


    /**
     * @param msg
     * @return
     */
    public JChat formatMessage( Player player, String msg )
    {
        Hashtable<String, String> headVal = new Hashtable<>();
        headVal.put( "color", color );
        headVal.put( "shortname", shortName );
        String formatted = getString( format, headVal );
        formatted = PlaceholderAPI.setBracketPlaceholders( player, formatted );
        return jsonReplacer( formatted, colorize( color + msg + color ) );
    }


    private JChat jsonReplacer( String formatted, String msg )
    {
        formatted = formatted.replaceAll( "\\n", "||||" );
        JChat jchat = new JChat( "" );
        String regex = "(?i)\\[(hover-text|click-url|click-command|hover-command|hover-url)=(.+)\\](.+)\\[/\\1\\]";
        Pattern pattern = Pattern.compile( regex );
        Matcher matcher = pattern.matcher( formatted );
        List<String> bbcodes = new ArrayList<>();
        while ( matcher.find() )
        {
            bbcodes.add( matcher.group() );
        }
        String remainder = formatted;
        for ( String bbcode : bbcodes )
        {
            String[] split = remainder.split( Pattern.quote( bbcode ), 2 );
            jchat.then( split[0] );
            if ( split.length == 2 )
            {
                remainder = split[1];
            }
            String param = bbcode.replaceFirst( regex, "$2" ).replaceAll( "\\|\\|\\|\\|", "\n" );
            String text = bbcode.replaceFirst( regex, "$3" ).replaceAll( "\\|\\|\\|\\|", "\n" );
            jchat.then( text );
            if ( bbcode.toLowerCase().contains( "[click-command=" ) )
            {
                jchat.command( param );
            }
            else if ( bbcode.toLowerCase().contains( "[click-url=" ) )
            {
                jchat.link( param );
            }
            else if ( bbcode.toLowerCase().contains( "[hover-text=" ) )
            {
                jchat.tooltip( param );
            }
            else if ( bbcode.toLowerCase().contains( "[hover-command=" ) )
            {
                String[] params = param.split( "====" );
                if ( params.length == 2 )
                {
                    jchat.tooltip( params[0] ).command( params[1] );
                }
            }
            else if ( bbcode.toLowerCase().contains( "[hover-url=" ) )
            {
                String[] params = param.split( "====" );
                if ( params.length == 2 )
                {
                    jchat.tooltip( params[0] ).link( params[1] );
                }
            }
        }
        Pattern colorPattern = Pattern.compile( "(?i)" + String.valueOf( '§' ) + "+([0-9A-FK-OR])" );
        List<String> colors = new ArrayList<>();
        Matcher colorMatcher = colorPattern.matcher( msg );
        while ( colorMatcher.find() )
        {
            colors.add( colorMatcher.group() );
        }
        String urlRegex = "(?i)\\b((?:https?:(?:/{1,3}|[a-z0-9%])|[a-z0-9.\\-]+[.](?:com|net|org|edu|gov|mil|aero|asia|biz|cat|coop|info|int|jobs|mobi|museum|name|post|pro|tel|travel|xxx|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cs|cu|cv|cx|cy|cz|dd|de|dj|dk|dm|do|dz|ec|ee|eg|eh|er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|Ja|sk|sl|sm|sn|so|sr|ss|st|su|sv|sx|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw)/)(?:[^\\s()<>{}\\[\\]]+|\\([^\\s()]*?\\([^\\s()]+\\)[^\\s()]*?\\)|\\([^\\s]+?\\))+(?:\\([^\\s()]*?\\([^\\s()]+\\)[^\\s()]*?\\)|\\([^\\s]+?\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’])|(?:(?<!@)[a-z0-9]+(?:[.\\-][a-z0-9]+)*[.](?:com|net|org|edu|gov|mil|aero|asia|biz|cat|coop|info|int|jobs|mobi|museum|name|post|pro|tel|travel|xxx|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cs|cu|cv|cx|cy|cz|dd|de|dj|dk|dm|do|dz|ec|ee|eg|eh|er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|Ja|sk|sl|sm|sn|so|sr|ss|st|su|sv|sx|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw)\\b/?(?!@)))";
        Pattern urlPattern = Pattern.compile( urlRegex );
        String[] strings = remainder.split( "\\{msg\\}" );
        ChatColor lastColor = getBukkitColor();
        jchat.then( strings[0] );
        String remainder2 = msg;
        for ( String color : colors )
        {
            String[] split = remainder2.split( Pattern.quote( color ), 2 );
            msg = split[0];
            Matcher urlMatcher = urlPattern.matcher( msg );
            List<String> urls = new ArrayList<>();
            while ( urlMatcher.find() )
            {
                urls.add( urlMatcher.group() );
            }
            String remainder3 = msg;
            for ( String url : urls )
            {
                String[] urlSplit = remainder3.split( Pattern.quote( url ), 2 );
                jchat.then( urlSplit[0] ).color( lastColor );
                jchat.then( url ).color( lastColor ).link( url ).color( lastColor );

                if ( urlSplit.length == 2 )
                {
                    remainder3 = urlSplit[1];
                }
            }
            jchat.then( remainder3 ).color( lastColor );

            if ( split.length == 2 )
            {
                remainder2 = split[1];
            }
            lastColor = ChatColor.getByChar( color.charAt( 1 ) );
        }
        if ( !remainder2.isEmpty() )
        {
            Matcher urlMatcher = urlPattern.matcher( remainder2 );
            List<String> urls = new ArrayList<>();
            while ( urlMatcher.find() )
            {
                urls.add( urlMatcher.group() );
            }
            String remainder3 = remainder2;
            for ( String url : urls )
            {
                String[] urlSplit = remainder2.split( Pattern.quote( url ), 2 );
                jchat.then( urlSplit[0] ).color( lastColor );
                jchat.then( url ).color( lastColor ).link( url ).color( lastColor );

                if ( urlSplit.length == 2 )
                {
                    remainder3 = urlSplit[1];
                }
            }
            jchat.then( remainder3 ).color( lastColor );
        }
        if ( strings.length > 1 )
        {
            for ( int i = 1; i < strings.length; i++ )
            {
                jchat.then( strings[i] ).color( lastColor );
            }
        }
        return jchat;
    }

    /**
     * @param format
     * @param replacer
     * @return
     */
    protected String getString( String format, Hashtable<String, String> replacer )
    {
        if ( format != null && !replacer.isEmpty() )
        {
            for ( Entry<String, String> entry : replacer.entrySet() )
            {
                format = format.replace( "{" + entry.getKey() + "}", entry.getValue() );
            }
        }
        return colorize( format );
    }


    /**
     * Converts colors place-holders.
     *
     * @param text
     * @return
     */
    public String colorize( String text )
    {
        return ChatColor.translateAlternateColorCodes( '&', text );
    }


    /**
     * @param text
     * @return
     */
    public String stripColor( String text )
    {
        return ChatColor.stripColor( text.replaceAll( "(&([a-z0-9A-Z]))", "" ) );
    }

    public ChatColor getBukkitColor()
    {
        if ( color.length() == 2 )
        {
            return ChatColor.getByChar( color.charAt( 1 ) );
        }
        return ChatColor.RESET;
    }

    /**
     *
     */
    public Channel clone() throws CloneNotSupportedException
    {
        return (Channel) super.clone();
    }
}