/*
 * The MIT License
 * 
 * Copyright (C) 2013 Kiyofumi Kondoh
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jp.ne.sakura.kkkon.StripElfSectionHeader;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class App 
{
    private static class MyOption
    {
        public boolean keepBackup = false;
        public String output = null;

        protected Options options = null;
        protected CommandLine commandLine = null;

        public void createOptions()
        {
            Options opts = new Options();
            Option keep = new Option("k", "keep", false, "keep backup");
            opts.addOption( keep );
            opts.addOption("o", "output", true, "output file or directory" );

            this.options = opts;
        }

        public void showUsage()
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "strip-elf-section-header", this.options );
        }
        public boolean parseOption( final String args[] )
        {
            CommandLineParser parser = new PosixParser();

            CommandLine cmdLine = null;
            try {
                cmdLine = parser.parse( this.options, args );
            } catch (ParseException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            if ( null == cmdLine )
            {
                return false;
            }

            this.commandLine = cmdLine;
            return true;
        }
        public boolean applyOption()
        {
            if ( null == this.commandLine )
            {
                return false;
            }
            
            {
                this.keepBackup = false;
                if ( this.commandLine.hasOption("k") )
                {
                    this.keepBackup = true;
                }
            }
            {
                this.output = this.commandLine.getOptionValue("o");
            }

            return true;
        }
    }

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
        MyOption myOpt = new MyOption();
        myOpt.createOptions();
        myOpt.parseOption( args );
        myOpt.applyOption();

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
