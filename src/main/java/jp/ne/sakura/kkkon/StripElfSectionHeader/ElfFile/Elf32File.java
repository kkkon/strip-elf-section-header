/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.EI_NINDENT;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElf32;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElfMagic;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElfBigEndian;
import static jp.ne.sakura.kkkon.StripElfSectionHeader.ElfFile.ElfFile.isElfLittleEndian;

/**
 *
 * @author kkkon
 */
public class Elf32File {

    static final class Header
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
    
    public static boolean stripSectionHeader( final String path )
    {
        boolean isStripped = false;

        File file = new File(path);
        {
            FileInputStream inStream = null;

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
                            Header  header = new Header();
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
                            
                            System.out.println( header.toString() );
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
        }
        
        return isStripped;
    }
}
