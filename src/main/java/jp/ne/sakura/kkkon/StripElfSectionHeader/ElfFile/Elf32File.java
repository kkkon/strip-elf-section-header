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

    static final class ProgramHeader
    {
        int     p_type;
        int     p_offset;
        int     p_vaddr;
        int     p_paddr;
        int     p_filesz;
        int     p_memsz;
        int     p_flags;
        int     p_align;

        @Override
        public String toString() {
            return "ProgramHeader{"
                    + "p_type=" + Integer.toHexString(p_type)
                    + ", p_offset=" + Integer.toHexString(p_offset)
                    + ", p_vaddr=" + Integer.toHexString(p_vaddr)
                    + ", p_paddr=" + Integer.toHexString(p_paddr)
                    + ", p_filesz=" + Integer.toHexString(p_filesz)
                    + ", p_memsz=" + Integer.toHexString(p_memsz)
                    + ", p_flags=" + Integer.toHexString(p_flags)
                    + ", p_align=" + Integer.toHexString(p_align)
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

                            final boolean isBigEndian = isElfBigEndian( buff );
                            final boolean isLittleEndian = isElfLittleEndian( buff );
                            {
                                if ( isBigEndian || isLittleEndian )
                                {
                                }
                                else
                                {
                                    throw new RuntimeException("unknown endian");
                                }
                            }
                            {
                                if ( isBigEndian || isLittleEndian )
                                {
                                    byte[] byte4 = new byte[4];

                                    header.e_type       = Util.read2byte( byte4, inStream, isLittleEndian );
                                    header.e_machine    = Util.read2byte( byte4, inStream, isLittleEndian );
                                    header.e_version    = Util.read4byte( byte4, inStream, isLittleEndian );
                                    header.e_entry      = Util.read4byte( byte4, inStream, isLittleEndian );
                                    header.e_phoff      = Util.read4byte( byte4, inStream, isLittleEndian );
                                    header.e_shoff      = Util.read4byte( byte4, inStream, isLittleEndian );
                                    header.e_flags      = Util.read4byte( byte4, inStream, isLittleEndian );
                                    header.e_ehsize     = Util.read2byte( byte4, inStream, isLittleEndian );
                                    header.e_phentsize  = Util.read2byte( byte4, inStream, isLittleEndian );
                                    header.e_phnum      = Util.read2byte( byte4, inStream, isLittleEndian );
                                    header.e_shentsize  = Util.read2byte( byte4, inStream, isLittleEndian );
                                    header.e_shnum      = Util.read2byte( byte4, inStream, isLittleEndian );
                                    header.e_shstrndx   = Util.read2byte( byte4, inStream, isLittleEndian );
                                }
                                //System.out.println( header.toString() );
                            }
                            inStream.close();

                            SectionHeader[] sectionHeaders = new SectionHeader[header.e_shnum];
                            
                            inStream = new FileInputStream( file );
                            inStream.skip( header.e_shoff );
                            int offsetSectionHeader_StringTable = -1;
                            if ( 0 < header.e_shentsize )
                            {
                                for ( int index = 0; index < header.e_shnum; ++index )
                                {
                                    SectionHeader   sectionHeader = new SectionHeader();
                                    {
                                        if ( isBigEndian || isLittleEndian )
                                        {
                                            byte[] byte4 = new byte[4];

                                            sectionHeader.sh_name = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_type = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_flags = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_addr = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_offset = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_size = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_link = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_info = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_addralign = Util.read4byte( byte4, inStream, isLittleEndian );
                                            sectionHeader.sh_entsize = Util.read4byte( byte4, inStream, isLittleEndian );
                                        }
                                        //System.out.println( sectionHeader.toString() );
                                        sectionHeaders[index] = sectionHeader;
                                    }
                                } // for
                                
                                {
                                    final int count = sectionHeaders.length;
                                    final int index = header.e_shstrndx;
                                    if ( 0 <= index && index < count )
                                    {
                                        final SectionHeader sectionHeader = sectionHeaders[index];
                                        if ( ElfFile.SHT_STRTAB == sectionHeader.sh_type )
                                        {
                                            // TODO check ".shstrtab"
                                            if ( offsetSectionHeader_StringTable < sectionHeader.sh_offset )
                                            {
                                                offsetSectionHeader_StringTable = sectionHeader.sh_offset;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        throw new IndexOutOfBoundsException("e_shstrndx out of bounds");
                                    }
                                }
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
                            {
                                if ( isBigEndian || isLittleEndian )
                                {
                                    byte[] byte4 = new byte[4];
                                    
                                    Util.write2byte( byte4, header.e_type, outStream, isLittleEndian );
                                    Util.write2byte( byte4, header.e_machine, outStream, isLittleEndian );
                                    Util.write4byte( byte4, header.e_version, outStream, isLittleEndian );
                                    Util.write4byte( byte4, header.e_entry, outStream, isLittleEndian );
                                    Util.write4byte( byte4, header.e_phoff, outStream, isLittleEndian );
                                    Util.write4byte( byte4, header.e_shoff, outStream, isLittleEndian );
                                    Util.write4byte( byte4, header.e_flags, outStream, isLittleEndian );
                                    Util.write2byte( byte4, header.e_ehsize, outStream, isLittleEndian );
                                    Util.write2byte( byte4, header.e_phentsize, outStream, isLittleEndian );
                                    Util.write2byte( byte4, header.e_phnum, outStream, isLittleEndian );
                                    Util.write2byte( byte4, header.e_shentsize, outStream, isLittleEndian );
                                    Util.write2byte( byte4, header.e_shnum, outStream, isLittleEndian );
                                    Util.write2byte( byte4, header.e_shstrndx, outStream, isLittleEndian );
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
