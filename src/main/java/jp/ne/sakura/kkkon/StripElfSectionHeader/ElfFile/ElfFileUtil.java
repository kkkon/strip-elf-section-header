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
package jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import jp.ne.sakura.kkkon.StripElfSectionHeader.AppOption;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class ElfFileUtil
{
    public static boolean stripElfSectionHeader( final AppOption option, final String relativePath, final String path )
    {
        boolean isStripped = false;

        final File file;
        {
            file = new File(path);
            if ( file.getPath().endsWith(".bak") )
            {
                if ( option.isVerbose() )
                {
                    System.out.println( "skip " + file.getPath() );
                }
                return false;
            }
        }

        final String fileName;
        {
            final int index = path.lastIndexOf( File.separatorChar );
            if ( 0 < index )
            {
                fileName = path.substring( index );
            }
            else
            {
                final int indexNonWindows = path.lastIndexOf( '/' );
                if ( 0 < indexNonWindows )
                {
                    fileName = path.substring( indexNonWindows );
                }
                else
                {
                    fileName = path;
                }
            }
        }

        final String destPath;
        {
            final String outputDir = option.getOutput();
            if ( null == outputDir )
            {
                final int index = path.lastIndexOf( File.separatorChar );
                if ( 0 < index )
                {
                    destPath = path.substring( 0, index );
                }
                else
                {
                    final int indexNonWindows = path.lastIndexOf( '/' );
                    if ( 0 < indexNonWindows )
                    {
                        destPath = path.substring( 0, indexNonWindows );
                    }
                    else
                    {
                        destPath = "";
                    }
                }
            }
            else
            {
                destPath = outputDir + File.separator + relativePath;
                final File destDir = new File( destPath );
                if ( ! destDir.exists() )
                {
                    if ( ! option.isDryRun() )
                    {
                        destDir.mkdirs();
                    }
                }
            }
        }

        ElfFile elfFile = new ElfFile();
        {
            final boolean result = elfFile.readFile( path );
            if ( false == result )
            {
                return false;
            }
        }

        {
            {
                boolean expectedElf = true;
                expectedElf = ! elfFile.hasSectionDebug();
                if ( false == expectedElf )
                {
                    System.err.println( "sorry. contain debug info. " + path );
                    return false;
                }

                final long offsetSectionHeader_StringTable = elfFile.getElfHeaderSectionHeaderStringTableOffset();
                if ( offsetSectionHeader_StringTable < 0 )
                {
                    System.err.println( "sorry. fail detect sh_offset. " + path );
                    return false;
                }
            }
        }

        RandomAccessFile input = null;
        RandomAccessFile output = null;
        try
        {
            input = new RandomAccessFile( path, "r" );

            File tempFile = File.createTempFile( "kkkon_strip", ".tmp" );
            output = new RandomAccessFile( tempFile, "rw" );

            elfFile.setElfHeaderSectionHeaderSize( 0 );
            elfFile.setElfHeaderSectionHeaderNumber( 0 );
            elfFile.setElfHeaderSectionHeaderStringTableIndex( 0 );

            final long elfHeaderHeaderSize = elfFile.getElfHeaderHeaderSize();
            input.seek( elfHeaderHeaderSize );

            final boolean resultWriteIdent = elfFile.writeElfHeaderIdent( output );
            if ( false == resultWriteIdent )
            {
                isStripped = false;
            }
            else
            {
                final boolean resultWriteElfHeader = elfFile.writeElfHeader( output );
                if ( false == resultWriteElfHeader )
                {
                    isStripped = false;
                }
            }

            long size = elfFile.getElfHeaderSectionHeaderOffset();
            final long fileSize = file.length();
            if ( size < 0 || fileSize < size )
            {
                size = fileSize;
            }
            else
            {
            }

            final long offsetSectionHeader_StringTable = elfFile.getElfHeaderSectionHeaderStringTableOffset();
            if ( offsetSectionHeader_StringTable < size )
            {
                size = offsetSectionHeader_StringTable;
            }
            size -= elfHeaderHeaderSize;

            byte[] temp = new byte[(int)size];
            input.read( temp );
            output.write( temp );

            input.close();
            output.close();

            if ( option.isDryRun() )
            {
                tempFile.delete();
            }
            else
            {
                if ( null != option.getOutput() )
                {
                    File fileDest = new File( destPath + File.separator + fileName );
                    if ( fileDest.exists() )
                    {
                        fileDest.delete();
                    }

                    if ( tempFile.renameTo( fileDest.getAbsoluteFile() ) )
                    {
                        isStripped = true;
                    }
                    else
                    {
                        System.err.println( "Failed. rename tempFile to original '" + tempFile.getAbsoluteFile() + "' renameTo '" + file.getAbsolutePath() + "'" );
                    }
                }
                else
                {
                    boolean readyRenameBackup = false;
                    File fileBackup = new File(file.getPath() + ".bak" );
                    if ( fileBackup.exists() )
                    {
                        boolean readyDelete = false;

                        if ( option.isBatchRun() )
                        {
                            if ( option.isKeepBackup() )
                            {

                            }
                            else
                            {
                                readyDelete = true;
                            }
                        }
                        else
                        {
                            while ( true )
                            {
                                System.out.println("do you want to delete ? (Y/N) " + fileBackup.getPath() );
                                final int c = System.in.read();
                                if ( 'Y' == c || 'y' == c )
                                {
                                    readyDelete = true;
                                }
                                else
                                if ( 'N' == c || 'n' == c )
                                {
                                    break;
                                }
                            }
                            while ( true )
                            {
                                if ( 0 < System.in.available() )
                                {
                                    System.in.read();
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }

                        if ( readyDelete )
                        {
                            if ( fileBackup.canWrite() )
                            {
                                readyRenameBackup = fileBackup.delete();
                            }
                        }
                    }
                    else
                    {
                        readyRenameBackup = true;
                    }

                    boolean successReanmeToBackup = false;
                    if ( readyRenameBackup )
                    {
                        successReanmeToBackup = file.renameTo( fileBackup );
                        if ( successReanmeToBackup )
                        {
                            successReanmeToBackup = true;
                        }
                        else
                        {
                            successReanmeToBackup = false;
                            System.err.println( "Failed. rename original to backup. '" + file.getAbsoluteFile() + "' renameTo '" + fileBackup.getAbsolutePath() + "'" );
                        }
                    }
                    if ( false == successReanmeToBackup && !option.isKeepBackup() )
                    {
                        file.delete();
                    }

                    if ( successReanmeToBackup || (false == successReanmeToBackup && !option.isKeepBackup() ) )
                    {
                        File fileDest = new File( destPath + File.separator + fileName );
                        if ( tempFile.renameTo( fileDest.getAbsoluteFile() ) )
                        {
                            isStripped = true;

                            if ( option.isKeepBackup() )
                            {
                            }
                            else
                            {
                                if ( fileBackup.canWrite() )
                                {
                                    fileBackup.delete();
                                }
                            }
                        }
                        else
                        {
                            System.err.println( "Failed. rename tempFile to original '" + tempFile.getAbsoluteFile() + "' renameTo '" + file.getAbsolutePath() + "'" );
                        }
                    }
                    else
                    {
                        System.err.println( "Failed. rename to backup. '" + file.getAbsoluteFile() + "' renameTo '" + fileBackup.getAbsolutePath() + "'" );
                    }
                }

                if ( tempFile.exists() )
                {
                    tempFile.delete();
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( null != input )
            {
                try { input.close(); } catch ( Exception e ) { }
            }
            if ( null != output )
            {
                try { output.close(); } catch ( Exception e ) { }
            }
        }

        return isStripped;
    }

}
