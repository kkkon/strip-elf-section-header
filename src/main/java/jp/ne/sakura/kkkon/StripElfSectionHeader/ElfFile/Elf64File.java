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

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class Elf32File
    implements IElfFile
{

    static final class ElfHeader
    {
        byte[]  e_ident = new byte[IElfFile.EI_NINDENT];
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

    public boolean isElfMagic()
    {
        return ElfFile.isElfMagic(  header.e_ident );
    }

    public boolean isElf32()
    {
        return ElfFile.isElf32(  header.e_ident );
    }

    public boolean isElf64()
    {
        return ElfFile.isElf64(  header.e_ident );
    }

    public boolean isElfLittleEndian()
    {
        return ElfFile.isElfLittleEndian(  header.e_ident );
    }

    public boolean isElfBigEndian()
    {
        return ElfFile.isElfBigEndian(  header.e_ident );
    }
    
    
    
    protected ElfHeader  header = new ElfHeader();
    public boolean readElfHeader( final RandomAccessFile input )
            throws IOException
    {
        {
            final long current = input.getFilePointer();
            if ( 0 != current )
            {
                input.seek( 0 );
            }

            final int result = input.read( header.e_ident );
            if ( header.e_ident.length != result )
            {
                return false;
            }
        }
        
        if ( ! isElfMagic() )
        {
            return false;
        }
        
        if ( ! isElf32() )
        {
            return false;
        }

        final boolean isBigEndian = isElfBigEndian();
        final boolean isLittleEndian = isElfLittleEndian();
        {
            if ( isBigEndian || isLittleEndian )
            {
            }
            else
            {
                throw new RuntimeException("unknown endian");
            }
        }
        
        final long current = input.getFilePointer();
        if ( IElfFile.EI_NINDENT != current )
        {
            input.seek( IElfFile.EI_NINDENT );
        }
        
        {
            if ( isBigEndian || isLittleEndian )
            {
                byte[] buff = new byte[4];

                header.e_type       = Util.read2byte( buff, input, isLittleEndian );
                header.e_machine    = Util.read2byte( buff, input, isLittleEndian );
                header.e_version    = Util.read4byte( buff, input, isLittleEndian );
                header.e_entry      = Util.read4byte( buff, input, isLittleEndian );
                header.e_phoff      = Util.read4byte( buff, input, isLittleEndian );
                header.e_shoff      = Util.read4byte( buff, input, isLittleEndian );
                header.e_flags      = Util.read4byte( buff, input, isLittleEndian );
                header.e_ehsize     = Util.read2byte( buff, input, isLittleEndian );
                header.e_phentsize  = Util.read2byte( buff, input, isLittleEndian );
                header.e_phnum      = Util.read2byte( buff, input, isLittleEndian );
                header.e_shentsize  = Util.read2byte( buff, input, isLittleEndian );
                header.e_shnum      = Util.read2byte( buff, input, isLittleEndian );
                header.e_shstrndx   = Util.read2byte( buff, input, isLittleEndian );
            }
            //System.out.println( header.toString() );
        }
        return true;
    }

    protected   ProgramHeader[] programHeaders = null;
    public boolean readProgramHeader( final RandomAccessFile input )
            throws IOException
    {
        if ( ! isElfMagic() )
        {
            return false;
        }
        
        if ( ! isElf32() )
        {
            return false;
        }

        final boolean isBigEndian = isElfBigEndian();
        final boolean isLittleEndian = isElfLittleEndian();
        {
            if ( isBigEndian || isLittleEndian )
            {
            }
            else
            {
                throw new RuntimeException("unknown endian");
            }
        }

        if ( 0 < header.e_phnum )
        {
            programHeaders = new ProgramHeader[header.e_phnum];
        }
        else
        {
            programHeaders = new ProgramHeader[0];
        }

        input.seek( header.e_phoff );
        if ( 0 < header.e_phentsize )
        {
            for ( int index = 0; index < header.e_phnum; ++index )
            {
                ProgramHeader   programHeader = new ProgramHeader();
                {
                    byte[] buff = new byte[4];

                    programHeader.p_type = Util.read4byte( buff, input, isLittleEndian );
                    programHeader.p_offset = Util.read4byte( buff, input, isLittleEndian );
                    programHeader.p_vaddr = Util.read4byte( buff, input, isLittleEndian );
                    programHeader.p_paddr = Util.read4byte( buff, input, isLittleEndian );
                    programHeader.p_filesz = Util.read4byte( buff, input, isLittleEndian );
                    programHeader.p_memsz = Util.read4byte( buff, input, isLittleEndian );
                    programHeader.p_flags = Util.read4byte( buff, input, isLittleEndian );
                    programHeader.p_align = Util.read4byte( buff, input, isLittleEndian );
                }
                //System.out.println( programHeader.toString() );
                programHeaders[index] = programHeader;
            } // for

        }

        return true;
    }

    protected   SectionHeader[] sectionHeaders = null;
    protected   String[] sectionHeaderStrings = null;
    protected   int offsetSectionHeader_StringTable = -1;
    public boolean readSectionHeader( final RandomAccessFile input )
            throws IOException
    {
        if ( ! isElfMagic() )
        {
            return false;
        }
        
        if ( ! isElf32() )
        {
            return false;
        }

        final boolean isBigEndian = isElfBigEndian();
        final boolean isLittleEndian = isElfLittleEndian();
        {
            if ( isBigEndian || isLittleEndian )
            {
            }
            else
            {
                throw new RuntimeException("unknown endian");
            }
        }

        if ( 0 < header.e_shnum )
        {
            sectionHeaders = new SectionHeader[header.e_shnum];
        }
        else
        {
            sectionHeaders = new SectionHeader[0];
        }

        input.seek( header.e_shoff );
        if ( 0 < header.e_shentsize )
        {
            for ( int index = 0; index < header.e_shnum; ++index )
            {
                SectionHeader   sectionHeader = new SectionHeader();
                {
                    byte[] buff = new byte[4];

                    sectionHeader.sh_name = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_type = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_flags = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_addr = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_offset = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_size = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_link = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_info = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_addralign = Util.read4byte( buff, input, isLittleEndian );
                    sectionHeader.sh_entsize = Util.read4byte( buff, input, isLittleEndian );
                }
                //System.out.println( sectionHeader.toString() );
                sectionHeaders[index] = sectionHeader;
            } // for

        }

        if ( 0 < header.e_shnum )
        {
            sectionHeaderStrings = new String[header.e_shnum];
        }
        else
        {
            sectionHeaderStrings = new String[0];
        }

        input.seek( 0 );
        {
            byte[] stringTable = null;
            {
                final int index = header.e_shstrndx;
                if ( 0 <= index && index < sectionHeaders.length )
                {
                    stringTable = new byte[sectionHeaders[index].sh_size];
                    input.seek( sectionHeaders[index].sh_offset );
                    input.read(stringTable);
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
        
        return true;
    }


    public boolean writeElfHeader( final RandomAccessFile output )
            throws IOException
    {
        if ( ! isElfMagic() )
        {
            return false;
        }
        
        if ( ! isElf32() )
        {
            return false;
        }

        final boolean isBigEndian = isElfBigEndian();
        final boolean isLittleEndian = isElfLittleEndian();
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
            byte[] buff = new byte[4];

            Util.write2byte( buff, header.e_type, output, isLittleEndian );
            Util.write2byte( buff, header.e_machine, output, isLittleEndian );
            Util.write4byte( buff, header.e_version, output, isLittleEndian );
            Util.write4byte( buff, header.e_entry, output, isLittleEndian );
            Util.write4byte( buff, header.e_phoff, output, isLittleEndian );
            Util.write4byte( buff, header.e_shoff, output, isLittleEndian );
            Util.write4byte( buff, header.e_flags, output, isLittleEndian );
            Util.write2byte( buff, header.e_ehsize, output, isLittleEndian );
            Util.write2byte( buff, header.e_phentsize, output, isLittleEndian );
            Util.write2byte( buff, header.e_phnum, output, isLittleEndian );
            Util.write2byte( buff, header.e_shentsize, output, isLittleEndian );
            Util.write2byte( buff, header.e_shnum, output, isLittleEndian );
            Util.write2byte( buff, header.e_shstrndx, output, isLittleEndian );
        }
        
        return true;
    }

    public boolean writeProgramHeader( final RandomAccessFile output )
            throws IOException
    {
        if ( ! isElfMagic() )
        {
            return false;
        }
        
        if ( ! isElf32() )
        {
            return false;
        }

        final boolean isBigEndian = isElfBigEndian();
        final boolean isLittleEndian = isElfLittleEndian();
        {
            if ( isBigEndian || isLittleEndian )
            {
            }
            else
            {
                throw new RuntimeException("unknown endian");
            }
        }

        for ( int index = 0; index < this.programHeaders.length; ++index )
        {
            final ProgramHeader progHeader = this.programHeaders[index];

            byte[] buff = new byte[4];

            Util.write4byte( buff, progHeader.p_type, output, isLittleEndian);
            Util.write4byte( buff, progHeader.p_offset, output, isLittleEndian);
            Util.write4byte( buff, progHeader.p_vaddr, output, isLittleEndian);
            Util.write4byte( buff, progHeader.p_paddr, output, isLittleEndian);
            Util.write4byte( buff, progHeader.p_filesz, output, isLittleEndian);
            Util.write4byte( buff, progHeader.p_memsz, output, isLittleEndian);
            Util.write4byte( buff, progHeader.p_flags, output, isLittleEndian);
            Util.write4byte( buff, progHeader.p_align, output, isLittleEndian);
        }

        return true;
    }

    public boolean writeSectionHeader( final RandomAccessFile output )
            throws IOException
    {
        if ( ! isElfMagic() )
        {
            return false;
        }
        
        if ( ! isElf32() )
        {
            return false;
        }

        final boolean isBigEndian = isElfBigEndian();
        final boolean isLittleEndian = isElfLittleEndian();
        {
            if ( isBigEndian || isLittleEndian )
            {
            }
            else
            {
                throw new RuntimeException("unknown endian");
            }
        }

        for ( int index = 0; index < this.sectionHeaderStrings.length; ++index )
        {
            final String s = this.sectionHeaderStrings[index];
            if ( null == s )
            {
                output.writeByte(0);
            }
            else
            {
                output.writeBytes(s);
                output.writeByte(0);
            }
        }

        // padding to SectionHeader table
        {
            final long curPos = output.getFilePointer();
            final long paddingCount = (long)this.header.e_shoff - curPos;

            for ( int count = 0; count < paddingCount; ++count )
            {
                output.write( 0x00 );
            }
        }

        for ( int index = 0; index < this.sectionHeaders.length; ++index )
        {
            final SectionHeader secHeader = this.sectionHeaders[index];

            byte[] buff = new byte[4];

            Util.write4byte( buff, secHeader.sh_name, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_type, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_flags, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_addr, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_offset, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_size, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_link, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_info, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_addralign, output, isLittleEndian);
            Util.write4byte( buff, secHeader.sh_entsize, output, isLittleEndian);
        }

        return true;
    }

    @Override
    public long getElfHeaderHeaderSize() {
        return header.e_ehsize;
    }

    @Override
    public long getElfHeaderProgramHeaderOffset() {
        return header.e_phoff;
    }

    @Override
    public long getElfHeaderProgramHeaderSize() {
        return header.e_phentsize*header.e_phnum;
    }

    @Override
    public long getElfHeaderSectionHeaderOffset() {
        return header.e_shoff;
    }

    @Override
    public long getElfHeaderSectionHeaderStringTableOffset() {
        return this.offsetSectionHeader_StringTable;
    }

    @Override
    public void setElfHeaderSectionHeaderOffset(long offset) {
        header.e_shoff = (int)offset;
    }

    @Override
    public void setElfHeaderSectionHeaderNumber(int number) {
        header.e_shnum = (short)number;
    }

    @Override
    public void setElfHeaderSectionHeaderSize(int size) {
        header.e_shentsize = (short)size;
    }

    @Override
    public void setElfHeaderSectionHeaderStringTableIndex(int index) {
        header.e_shstrndx = (short)index;
    }

    public boolean hasSectionDebug()
    {
        boolean haveDebug = false;
        {
            final int sectionHeaderStringsLength = sectionHeaderStrings.length;
            for ( int index = 0; index < sectionHeaderStringsLength; ++index )
            {
                final String str = sectionHeaderStrings[index];
                if ( null != str )
                {
                    if ( str.startsWith( ElfFile.ELF_DEBUG ) )
                    {
                        haveDebug = true;
                        break;
                    }
                }
            }
        }

        return haveDebug;
    }

    public boolean stripSectionAndroid()
    {
        List<String> listHeaderName = new ArrayList<String>(32);
        List<SectionHeader> listHeader = new ArrayList<SectionHeader>(32);

        if ( null == this.sectionHeaders )
        {
            return true;
        }
        if ( null == this.sectionHeaderStrings )
        {
            return true;
        }
        if ( this.sectionHeaders.length != this.sectionHeaderStrings.length )
        {
            throw new RuntimeException("count not match sectionHeader and sectionHeaderStrings");
        }

        {
            final String[] needSectionNameArray = {
                ElfFile.ELF_DYNAMIC
                //, ElfFile.ELF_DYNSYM
                , ElfFile.ELF_DYNSTR
                , ElfFile.ELF_SHSTRTAB
            };

            for ( int index = 0; index < this.sectionHeaders.length; ++index )
            {
                for ( int i = 0; i < needSectionNameArray.length; ++ i )
                {
                    if ( 0 == needSectionNameArray[i].compareTo( this.sectionHeaderStrings[index] ) )
                    {
                        listHeaderName.add( this.sectionHeaderStrings[index] );
                        listHeader.add( this.sectionHeaders[index] );
                    }
                }
            }
        }

        String[] newHeaderName = new String[listHeaderName.size()+1];
        SectionHeader[] newHeader = new SectionHeader[listHeader.size()+1];
        {
            newHeaderName[0] = null;
            newHeader[0] = new SectionHeader();
            newHeader[0].sh_type = ElfFile.SHT_NULL;
        }
        {
            int indexDynStr = -1;
            for ( int index = 0; index < listHeaderName.size(); ++index )
            {
                newHeaderName[index+1] = listHeaderName.get(index);
                if ( 0 == ElfFile.ELF_DYNSTR.compareTo(listHeaderName.get(index)) )
                {
                    indexDynStr = index + 1;
                }
            }

            for ( int index = 0; index < listHeader.size(); ++index )
            {
                newHeader[index+1] = listHeader.get(index);
                if ( 0 == ElfFile.ELF_DYNAMIC.compareTo(newHeaderName[index+1]) )
                {
                    newHeader[index+1].sh_link = indexDynStr;
                }
            }
        }

        this.sectionHeaderStrings = newHeaderName;
        this.sectionHeaders = newHeader;

        this.header.e_shnum = (short)this.sectionHeaders.length;
        this.header.e_shstrndx = (short)(this.sectionHeaderStrings.length-1);
        
        long stringTableSize = 0;
        {
            for ( int index = 0; index < this.sectionHeaderStrings.length; ++index )
            {
                if ( null == this.sectionHeaderStrings[index] )
                {
                    this.sectionHeaders[index].sh_name = (short)stringTableSize;
                    stringTableSize += 1;
                    continue;
                }
                this.sectionHeaders[index].sh_name = (short)stringTableSize;
                stringTableSize += this.sectionHeaderStrings[index].length() + 1;
            }
        }
        {
            this.sectionHeaders[this.sectionHeaders.length-1].sh_size = (int)stringTableSize;
            this.header.e_shoff = this.offsetSectionHeader_StringTable + (int)stringTableSize;

            // 4 byte alignment
            this.header.e_shoff = (this.header.e_shoff + (4-1)) & (~(4-1));
        }

        return true;
    }


    @Override
    public boolean fixProgramFlagAndroidO()
    {
        if ( null == this.programHeaders )
        {
            return false;
        }

        final int countProgramHeader = this.programHeaders.length;
        for ( int index = 0; index < countProgramHeader; ++index )
        {
            if ( IElfFile.PT_LOAD != this.programHeaders[index].p_type )
            {
                continue;
            }

            // Executable?
            if ( 0 != (this.programHeaders[index].p_flags & IElfFile.PF_X) )
            {
                // Writable?
                if ( 0 != (this.programHeaders[index].p_flags & IElfFile.PF_W) )
                {
                    // drop Writable flag
                    this.programHeaders[index].p_flags &= ~(IElfFile.PF_W);
                }
            }
        }

        return true;
    }


}
