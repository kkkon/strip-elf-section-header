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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jp.ne.sakura.kkkon.StripElfSectionHeader.AppOption;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.EI_NINDENT;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElf32;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElfMagic;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElfBigEndian;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElfLittleEndian;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class Elf32File {

    static final class ElfHeader
    {
        byte[]  e_ident = new byte[ElfFile.EI_NINDENT];
        short   e_type;
        short   e_machine;
        int     e_version;
        int     e_entry;
        int     e_phoff;
        int     e_shoff;
        int     e_flags;
        short   e_ehsize;
        short   e_phentsize;
        short   e_phnum;
        short   e_shentsize;
        short   e_shnum;
        short   e_shstrndx;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for ( int index = 0; index < e_ident.length; ++index )
            {
                sb.append( String.format( "0x%02X ", e_ident[index] & 0xFF ) );
            }
            return "Header{"
                    + "e_ident=" + sb.toString() + System.getProperty("line.separator")
                    + ", e_type=0x" + Integer.toHexString(e_type & 0xFFFF)
                    + ", e_machine=0x" + Integer.toHexString(e_machine & 0xFFFF)
                    + ", e_version=0x" + Integer.toHexString(e_version)
                    + ", e_entry=0x" + Integer.toHexString(e_entry)
                    + ", e_phoff=0x" + Integer.toHexString(e_phoff)
                    + ", e_shoff=0x" + Integer.toHexString(e_shoff)
                    + ", e_flags=0x" + Integer.toHexString(e_flags)
                    + ", e_ehsize=0x" + Integer.toHexString(e_ehsize & 0xFFFF)
                    + ", e_phentsize=0x" + Integer.toHexString(e_phentsize & 0xFFFF)
                    + ", e_phnum=0x" + Integer.toHexString(e_phnum & 0xFFFF)
                    + ", e_shentsize=0x" + Integer.toHexString(e_shentsize & 0xFFFF)
                    + ", e_shnum=0x" + Integer.toHexString(e_shnum & 0xFFFF)
                    + ", e_shstrndx=0x" + Integer.toHexString(e_shstrndx & 0xFFFF)
                    + '}';
        }
        
    }
    
    static final class SectionHeader
    {
        int     sh_name;
        int     sh_type;
        int     sh_flags;
        int     sh_addr;
        int     sh_offset;
        int     sh_size;
        int     sh_link;
        int     sh_info;
        int     sh_addralign;
        int     sh_entsize;
    }
    
    public static boolean stripSectionHeader( final AppOption option, final String relativePath, final String path )
    {
        boolean isStripped = false;

        File file = new File(path);
        if ( file.getPath().endsWith(".bak") )
        {
            if ( option.isVerbose() )
            {
                System.out.println( "skip " + file.getPath() );
            }
            return false;
        }

        String fileName = null;
        {
            final int index = path.lastIndexOf( File.separatorChar );
            if ( 0 < index )
            {
                fileName = path.substring( index );
            }
        }

        String destPath = null;
        {
            final String outputDir = option.getOutput();
            if ( null == outputDir )
            {
                final int index = path.lastIndexOf( File.separatorChar );
                if ( 0 < index )
                {
                    destPath = path.substring( 0, index );
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

        {
            InputStream inStream = null;
            OutputStream outStream = null;

            byte[] buff = new byte[EI_NINDENT];
            try
            {
                inStream = new FileInputStream( file );
                final int nRet = inStream.read(buff);
                if ( EI_NINDENT != nRet )
                {
                    isStripped = false;
                }
                else
                {
                    if ( isElfMagic( buff ) )
                    {
                        if ( isElf32( buff ) )
                        {
                            ElfHeader  header = new ElfHeader();
                            System.arraycopy( buff, 0, header.e_ident, 0, buff.length );
                            if ( isElfBigEndian( buff ) )
                            {
                                byte[] byte2 = new byte[2];
                                byte[] byte4 = new byte[4];
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_type = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_machine = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[0] << 24 ) & 0xFF000000)
                                                + ((byte4[1] << 16 ) & 0x00FF0000)
                                                + ((byte4[2] <<  8 ) & 0x0000FF00)
                                                + ((byte4[3] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_version = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[0] << 24 ) & 0xFF000000)
                                                + ((byte4[1] << 16 ) & 0x00FF0000)
                                                + ((byte4[2] <<  8 ) & 0x0000FF00)
                                                + ((byte4[3] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_entry = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[0] << 24 ) & 0xFF000000)
                                                + ((byte4[1] << 16 ) & 0x00FF0000)
                                                + ((byte4[2] <<  8 ) & 0x0000FF00)
                                                + ((byte4[3] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_phoff = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[0] << 24 ) & 0xFF000000)
                                                + ((byte4[1] << 16 ) & 0x00FF0000)
                                                + ((byte4[2] <<  8 ) & 0x0000FF00)
                                                + ((byte4[3] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_shoff = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[0] << 24 ) & 0xFF000000)
                                                + ((byte4[1] << 16 ) & 0x00FF0000)
                                                + ((byte4[2] <<  8 ) & 0x0000FF00)
                                                + ((byte4[3] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_flags = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_ehsize = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_phentsize = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_phnum = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_shentsize = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_shnum = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[0] << 8) & 0xFF00) + ((byte2[1] << 0 ) & 0x00FF));
                                        header.e_shstrndx = data;
                                    }
                                }

                            }
                            else
                            if ( isElfLittleEndian( buff ) )
                            {
                                byte[] byte2 = new byte[2];
                                byte[] byte4 = new byte[4];
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_type = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_machine = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[3] << 24 ) & 0xFF000000)
                                                + ((byte4[2] << 16 ) & 0x00FF0000)
                                                + ((byte4[1] <<  8 ) & 0x0000FF00)
                                                + ((byte4[0] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_version = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[3] << 24 ) & 0xFF000000)
                                                + ((byte4[2] << 16 ) & 0x00FF0000)
                                                + ((byte4[1] <<  8 ) & 0x0000FF00)
                                                + ((byte4[0] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_entry = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[3] << 24 ) & 0xFF000000)
                                                + ((byte4[2] << 16 ) & 0x00FF0000)
                                                + ((byte4[1] <<  8 ) & 0x0000FF00)
                                                + ((byte4[0] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_phoff = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[3] << 24 ) & 0xFF000000)
                                                + ((byte4[2] << 16 ) & 0x00FF0000)
                                                + ((byte4[1] <<  8 ) & 0x0000FF00)
                                                + ((byte4[0] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_shoff = data;
                                    }
                                }
                                {
                                    final int read4 = inStream.read(byte4);
                                    if ( 4 != read4 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        int data = (int)(
                                                (  (byte4[3] << 24 ) & 0xFF000000)
                                                + ((byte4[2] << 16 ) & 0x00FF0000)
                                                + ((byte4[1] <<  8 ) & 0x0000FF00)
                                                + ((byte4[0] <<  0 ) & 0x000000FF)
                                            );
                                        header.e_flags = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_ehsize = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_phentsize = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_phnum = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_shentsize = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_shnum = data;
                                    }
                                }
                                {
                                    final int read2 = inStream.read(byte2);
                                    if ( 2 != read2 )
                                    {
                                        
                                    }
                                    else
                                    {
                                        short data = (short)(((byte2[1] << 8) & 0xFF00) + ((byte2[0] << 0 ) & 0x00FF));
                                        header.e_shstrndx = data;
                                    }
                                }
                                
                            }

                            //System.out.println( header.toString() );
                            File tempFile = File.createTempFile( "kkkon_strip", ".tmp" );
                            outStream = new FileOutputStream( tempFile );

                            header.e_shentsize = 0;
                            header.e_shnum = 0;
                            header.e_shstrndx = 0;

                            inStream.close();
                            inStream = new FileInputStream( file );
                            inStream.skip( header.e_ehsize );
                            
                            outStream.write(header.e_ident);
                            if ( isElfBigEndian( buff ) )
                            {
                                byte[] byte2 = new byte[2];
                                byte[] byte4 = new byte[4];
                                {
                                    byte2[0] = (byte)((header.e_type >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_type >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[0] = (byte)((header.e_machine >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_machine >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte4[0] = (byte)((header.e_version >> 24) & 0xFF);
                                    byte4[1] = (byte)((header.e_version >> 16) & 0xFF);
                                    byte4[2] = (byte)((header.e_version >>  8) & 0xFF);
                                    byte4[3] = (byte)((header.e_version >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[0] = (byte)((header.e_entry >> 24) & 0xFF);
                                    byte4[1] = (byte)((header.e_entry >> 16) & 0xFF);
                                    byte4[2] = (byte)((header.e_entry >>  8) & 0xFF);
                                    byte4[3] = (byte)((header.e_entry >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[0] = (byte)((header.e_phoff >> 24) & 0xFF);
                                    byte4[1] = (byte)((header.e_phoff >> 16) & 0xFF);
                                    byte4[2] = (byte)((header.e_phoff >>  8) & 0xFF);
                                    byte4[3] = (byte)((header.e_phoff >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[0] = (byte)((header.e_shoff >> 24) & 0xFF);
                                    byte4[1] = (byte)((header.e_shoff >> 16) & 0xFF);
                                    byte4[2] = (byte)((header.e_shoff >>  8) & 0xFF);
                                    byte4[3] = (byte)((header.e_shoff >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[0] = (byte)((header.e_flags >> 24) & 0xFF);
                                    byte4[1] = (byte)((header.e_flags >> 16) & 0xFF);
                                    byte4[2] = (byte)((header.e_flags >>  8) & 0xFF);
                                    byte4[3] = (byte)((header.e_flags >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte2[0] = (byte)((header.e_ehsize >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_ehsize >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[0] = (byte)((header.e_phentsize >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_phentsize >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[0] = (byte)((header.e_phnum >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_phnum >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[0] = (byte)((header.e_shentsize >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_shentsize >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[0] = (byte)((header.e_shnum >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_shnum >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[0] = (byte)((header.e_shstrndx >> 8) & 0xFF);
                                    byte2[1] = (byte)((header.e_shstrndx >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                            }
                            else
                            if ( isElfLittleEndian( buff ) )
                            {
                                byte[] byte2 = new byte[2];
                                byte[] byte4 = new byte[4];
                                {
                                    byte2[1] = (byte)((header.e_type >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_type >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[1] = (byte)((header.e_machine >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_machine >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte4[3] = (byte)((header.e_version >> 24) & 0xFF);
                                    byte4[2] = (byte)((header.e_version >> 16) & 0xFF);
                                    byte4[1] = (byte)((header.e_version >>  8) & 0xFF);
                                    byte4[0] = (byte)((header.e_version >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[3] = (byte)((header.e_entry >> 24) & 0xFF);
                                    byte4[2] = (byte)((header.e_entry >> 16) & 0xFF);
                                    byte4[1] = (byte)((header.e_entry >>  8) & 0xFF);
                                    byte4[0] = (byte)((header.e_entry >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[3] = (byte)((header.e_phoff >> 24) & 0xFF);
                                    byte4[2] = (byte)((header.e_phoff >> 16) & 0xFF);
                                    byte4[1] = (byte)((header.e_phoff >>  8) & 0xFF);
                                    byte4[0] = (byte)((header.e_phoff >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[3] = (byte)((header.e_shoff >> 24) & 0xFF);
                                    byte4[2] = (byte)((header.e_shoff >> 16) & 0xFF);
                                    byte4[1] = (byte)((header.e_shoff >>  8) & 0xFF);
                                    byte4[0] = (byte)((header.e_shoff >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte4[3] = (byte)((header.e_flags >> 24) & 0xFF);
                                    byte4[2] = (byte)((header.e_flags >> 16) & 0xFF);
                                    byte4[1] = (byte)((header.e_flags >>  8) & 0xFF);
                                    byte4[0] = (byte)((header.e_flags >>  0) & 0xFF);
                                    outStream.write( byte4 );
                                }
                                {
                                    byte2[1] = (byte)((header.e_ehsize >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_ehsize >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[1] = (byte)((header.e_phentsize >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_phentsize >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[1] = (byte)((header.e_phnum >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_phnum >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[1] = (byte)((header.e_shentsize >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_shentsize >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[1] = (byte)((header.e_shnum >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_shnum >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                                {
                                    byte2[1] = (byte)((header.e_shstrndx >> 8) & 0xFF);
                                    byte2[0] = (byte)((header.e_shstrndx >> 0) & 0xFF);
                                    outStream.write( byte2 );
                                }
                            }

                            long size = header.e_shoff;
                            long fileSize = file.length();
                            if ( size < 0 || fileSize < size )
                            {
                                size = fileSize;
                            }
                            else
                            {
                            }
                            size -= header.e_ehsize;

                            byte[] temp = new byte[(int)size];
                            inStream.read( temp );
                            outStream.write( temp );

                            inStream.close();
                            outStream.flush();
                            outStream.close();

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
            
            if ( null != inStream )
            {
                try { inStream.close(); } catch ( Exception e ) { }
            }
            if ( null != outStream )
            {
                try { outStream.close(); } catch ( Exception e ) { }
            }
        }
        
        return isStripped;
    }
}
