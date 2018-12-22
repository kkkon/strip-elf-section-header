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
import jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFileUtil;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class App 
{
    public static void stripRecursive( final AppOption option, final String rootPath, final String path )
    {
        final File file = new File( path );
        String rootDir = null;
        {
            if ( null != rootPath )
            {
                rootDir = rootPath;
            }
            else
            {
                rootDir = file.getAbsolutePath();
                if ( file.isDirectory() )
                {
                    // nothing
                }
                else
                {
                    final int index = rootDir.lastIndexOf( File.separatorChar );
                    if ( 0 < index )
                    {
                        rootDir = rootDir.substring( 0, index );
                    }
                    else
                    {
                        final int indexNonWindows = rootDir.lastIndexOf( '/' );
                        if ( 0 < indexNonWindows )
                        {
                            rootDir = rootDir.substring( 0, indexNonWindows );
                        }
                    }
                }
            }
        }

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

                    {
                        boolean needCall = false;
                        final File f = new File( files[index].getPath() );
                        if ( f.isDirectory() )
                        {
                            if ( option.isRecursive() )
                            {
                                needCall = true;
                            }
                        }
                        else
                        {
                            needCall = true;
                        }
                        
                        if ( needCall )
                        {
                            stripRecursive( option, rootDir, files[index].getPath() );
                        }
                    }
                }
            }
        }
        else
        {
            String relativePath = null;
            {
                final int indexStart = rootDir.length() + 1;
                final File filePath = new File(path);
                final String pathAbsolute = filePath.getAbsolutePath();
                final int indexLast = pathAbsolute.lastIndexOf( File.separatorChar );
                if ( 0 < indexStart && 0 < indexLast )
                {
                    if ( indexStart < indexLast )
                    {
                        relativePath = pathAbsolute.substring( indexStart, indexLast );
                    }
                    else
                    {
                        relativePath = "";
                    }
                }
                //System.err.println( relativePath );
                if ( null == relativePath )
                {
                    throw new RuntimeException(
                            "relativePath is null. indexStart=" + indexStart
                            + ", indexLast=" + indexLast
                            + ", rootDir=" + rootDir
                            + ", pathAbsolute=" + pathAbsolute
                            );
                }
            }

            final boolean isElfFile = ElfFileUtil.stripElfSectionHeader( option, relativePath, path );
            System.err.println( path + ": " + isElfFile );
        }
        
    }

    public static void main( String[] args )
    {
        AppOption appOpt = new AppOption();
        appOpt.createOptions();
        {
            final boolean result = appOpt.parseOption( args );
            if ( false == result )
            {
                appOpt.showUsage();
                return;
            }
        }
        appOpt.applyOption();

        if ( null == args )
        {
            appOpt.showUsage();
            return;
        }
        if ( args.length < 1 )
        {
            appOpt.showUsage();
            return;
        }

        {
            final String[] remain_args = appOpt.getArgs();
            if ( null != remain_args )
            {
                final int count = remain_args.length;
                for ( int index = 0; index < count; ++index )
                {
                    final String arg = remain_args[index];
                    stripRecursive( appOpt, null, arg );
                }
            }
        }

        if ( appOpt.isAndroid() )
        {
            final String lineSeparator = System.getProperty("line.separator");
            final StringBuilder sb = new StringBuilder();
            sb.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sb.append(lineSeparator);
            sb.append("!!  If you build apk by android gradle's version 2.3 higher,   !!");
            sb.append(lineSeparator);
            sb.append("!!   you must set 'packagingOptions.doNotStrip'                !!");
            sb.append(lineSeparator);
            sb.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sb.append(lineSeparator);
            //sb.append();
            System.out.println( sb.toString() );
        }
    }
}
