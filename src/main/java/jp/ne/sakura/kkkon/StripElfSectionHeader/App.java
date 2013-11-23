package jp.ne.sakura.kkkon.StripElfSectionHeader;

import java.io.File;
import jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void stripRecursive( final String path )
    {
        File file = new File( path );
        if ( file.isDirectory() )
        {
            final File[] files = file.listFiles();
            if ( null != files )
            {
                final int count = files.length;
                for ( int index = 0; index < count; ++index )
                {
                    if ( null == files[index] )
                    {
                        continue;
                    }
                    //System.err.println( files[index].getPath() );

                    if ( 0 == ".".compareTo(files[index].getPath()))
                    {
                        continue;
                    }
                    if ( 0 == "..".compareTo(files[index].getPath()))
                    {
                        continue;
                    }
                    stripRecursive( files[index].getAbsolutePath() );
                }
            }
        }
        else
        {
            final boolean isElfFile = ElfFile.stripElfSectionHeader( path );
            System.err.println( path + ": " + isElfFile );
        }
        
    }

    public static void main( String[] args )
    {
        if ( null == args )
        {
            return;
        }
        if ( args.length < 1 )
        {
            return;
        }

        stripRecursive( args[0] );
    }
}
