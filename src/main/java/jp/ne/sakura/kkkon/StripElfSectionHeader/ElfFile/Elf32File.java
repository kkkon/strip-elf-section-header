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
            else
            {
                final int indexNonWindows = path.lastIndexOf( '/' );
                if ( 0 < indexNonWindows )
                {
                    fileName = path.substring( indexNonWindows );
                }
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
                else
                {
                    final int indexNonWindows = path.lastIndexOf( '/' );
                    if ( 0 < indexNonWindows )
                    {
                        destPath = path.substring( 0, indexNonWindows );
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

                            SectionHeader[] sectionHeaders = null;
                            if ( 0 < header.e_shnum )
                            {
                                sectionHeaders = new SectionHeader[header.e_shnum];
                            }
                            else
                            {
                                sectionHeaders = new SectionHeader[0];
                            }
                            
                            inStream = new FileInputStream( file );
                            inStream.skip( header.e_shoff );
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
                                
                            }

                            inStream.close();

                            String[] sectionHeaderStrings = null;
                            if ( 0 < header.e_shnum )
                            {
                                sectionHeaderStrings = new String[header.e_shnum];
                            }
                            else
                            {
                                sectionHeaderStrings = new String[0];
                            }
                            
                            inStream = new FileInputStream( file );
                            {
                                byte[] stringTable = null;
                                {
                                    final int index = header.e_shstrndx;
                                    if ( 0 <= index && index < sectionHeaders.length )
                                    {
                                        stringTable = new byte[sectionHeaders[index].sh_size];
                                        inStream.skip( sectionHeaders[index].sh_offset );
                                        inStream.read(stringTable);
                                    }
                                }

                                if ( null != stringTable )
                                {
                                    final int sectionHeadersLength = sectionHeaders.length;
                                    for ( int index = 0; index < sectionHeadersLength; ++index )
                                    {
                                        final int indexStart = sectionHeaders[index].sh_name;
                                        StringBuilder sb = new StringBuilder(128);
                                        final int stringTableLength = stringTable.length;
                                        for ( int i = 0; indexStart+i < stringTableLength; i++ )
                                        {
                                           final byte c = stringTable[indexStart+i];
                                           if ( '\0' == c )
                                           {
                                               break;
                                           }
                                           sb.append((char)c);
                                        }
                                        sectionHeaderStrings[index] = sb.toString();
                                        //System.out.println(sectionHeaderStrings[index]);
                                    }
                                }

                            }

                            int offsetSectionHeader_StringTable = -1;
                            {
                                {
                                    final int count = sectionHeaders.length;
                                    final int index = header.e_shstrndx;
                                    if ( 0 <= index && index < count )
                                    {
                                        final SectionHeader sectionHeader = sectionHeaders[index];
                                        if ( ElfFile.SHT_STRTAB == sectionHeader.sh_type )
                                        {
                                            if ( 0 == ElfFile.ELF_SHSTRTAB.compareTo( sectionHeaderStrings[index] ) )
                                            {
                                                if ( offsetSectionHeader_StringTable < sectionHeader.sh_offset )
                                                {
                                                    offsetSectionHeader_StringTable = sectionHeader.sh_offset;
                                                }
                                            }
                                            
                                        }
                                    }
                                    else
                                    {
                                        throw new IndexOutOfBoundsException("e_shstrndx out of bounds");
                                    }
                                }
                            }

                            boolean expectedElf = true;
                            {
                                final int sectionHeaderStringsLength = sectionHeaderStrings.length;
                                for ( int index = 0; index < sectionHeaderStringsLength; ++index )
                                {
                                    final String str = sectionHeaderStrings[index];
                                    if ( null != str )
                                    {
                                        if ( str.startsWith( ElfFile.ELF_DEBUG ) )
                                        {
                                            expectedElf = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            if ( false == expectedElf )
                            {
                                System.err.println( "sorry. contain debug info. " + path );
                                return false;
                            }
                            
                            if ( offsetSectionHeader_StringTable < 0 )
                            {
                                System.err.println( "sorry. fail detect sh_offset. " + path );
                                return false;
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
            finally
            {
                if ( null != inStream )
                {
                    try { inStream.close(); } catch ( Exception e ) { }
                }
                if ( null != outStream )
                {
                    try { outStream.close(); } catch ( Exception e ) { }
                }
            }
        }
        
        return isStripped;
    }
}
