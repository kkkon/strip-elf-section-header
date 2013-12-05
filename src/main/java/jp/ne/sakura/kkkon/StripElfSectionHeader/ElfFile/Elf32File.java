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

        @Override
        public String toString() {
            return "SectionHeader{"
                    + "sh_name=" + Integer.toHexString(sh_name)
                    + ", sh_type=" + Integer.toHexString(sh_type)
                    + ", sh_flags=" + Integer.toHexString(sh_flags)
                    + ", sh_addr=" + Integer.toHexString(sh_addr)
                    + ", sh_offset=" + Integer.toHexString(sh_offset)
                    + ", sh_size=" + Integer.toHexString(sh_size)
                    + ", sh_link=" + Integer.toHexString(sh_link)
                    + ", sh_info=" + Integer.toHexString(sh_info)
                    + ", sh_addralign=" + Integer.toHexString(sh_addralign)
                    + ", sh_entsize=" + Integer.toHexString(sh_entsize)
                    + '}';
        }
        
    }
    
    public static boolean stripSectionHeader( final String path )
    {
        boolean isStripped = false;

        File file = new File(path);
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

                            inStream.close();
                            //System.out.println( header.toString() );

                            inStream = new FileInputStream( file );
                            inStream.skip( header.e_shoff );
                            int offsetSectionHeader_StringTable = -1;
                            if ( 0 < header.e_shentsize )
                            {
                                SectionHeader   sectionHeader = new SectionHeader();
                                for ( int i = 0; i < header.e_shnum; ++i )
                                {
                                    if ( isElfBigEndian( buff ) )
                                    {
                                        byte[] byte4 = new byte[4];

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
                                                sectionHeader.sh_name = data;
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
                                                sectionHeader.sh_type = data;
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
                                                sectionHeader.sh_flags = data;
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
                                                sectionHeader.sh_addr = data;
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
                                                sectionHeader.sh_offset = data;
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
                                                sectionHeader.sh_size = data;
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
                                                sectionHeader.sh_link = data;
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
                                                sectionHeader.sh_info = data;
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
                                                sectionHeader.sh_addralign = data;
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
                                                sectionHeader.sh_entsize = data;
                                            }
                                        }

                                    }
                                    else
                                    if ( isElfLittleEndian( buff ) )
                                    {
                                        byte[] byte4 = new byte[4];

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
                                                sectionHeader.sh_name = data;
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
                                                sectionHeader.sh_type = data;
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
                                                sectionHeader.sh_flags = data;
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
                                                sectionHeader.sh_addr = data;
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
                                                sectionHeader.sh_offset = data;
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
                                                sectionHeader.sh_size = data;
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
                                                sectionHeader.sh_link = data;
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
                                                sectionHeader.sh_info = data;
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
                                                sectionHeader.sh_addralign = data;
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
                                                sectionHeader.sh_entsize = data;
                                            }
                                        }

                                    }
                                    //System.out.println( sectionHeader.toString() );
                                    if ( ElfFile.SHT_STRTAB == sectionHeader.sh_type )
                                    {
                                        if ( 0 == sectionHeader.sh_flags )
                                        {
                                            // TODO check ".shstrtab"
                                            if ( offsetSectionHeader_StringTable < sectionHeader.sh_offset )
                                            {
                                                offsetSectionHeader_StringTable = sectionHeader.sh_offset;
                                            }
                                        }
                                    }
                                } // for
                            }

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
                            final long fileSize = file.length();
                            if ( size < 0 || fileSize < size )
                            {
                                size = fileSize;
                            }
                            else
                            {
                            }

                            if ( offsetSectionHeader_StringTable < size )
                            {
                                size = offsetSectionHeader_StringTable;
                            }
                            size -= header.e_ehsize;

                            byte[] temp = new byte[(int)size];
                            inStream.read( temp );
                            outStream.write( temp );

                            inStream.close();
                            outStream.flush();
                            outStream.close();

                            /*
                            File fileBackup = new File(file.getAbsoluteFile() + ".bak" );
                            if ( file.renameTo( fileBackup ) )
                            {
                                if ( tempFile.renameTo( file.getAbsoluteFile() ) )
                                {
                                }
                                else
                                {
                                    System.err.println( "Failed. '" + tempFile.getAbsoluteFile() + "' renameTo '" + file.getAbsolutePath() + "'" );
                                }
                            }
                            else
                            {
                                System.err.println( "Failed. '" + file.getAbsoluteFile() + "' renameTo '" + fileBackup.getAbsolutePath() + "'" );
                            }
                            */
                            
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
