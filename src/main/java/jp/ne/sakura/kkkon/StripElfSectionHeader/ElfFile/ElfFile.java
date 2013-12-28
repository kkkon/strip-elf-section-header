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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidParameterException;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class ElfFile
{
    protected byte[] _ident = new byte[IElfFile.EI_NINDENT];
    protected IElfFile _elfFile;

    public boolean readElfHeaderIdent( final RandomAccessFile input )
            throws IOException
    {
        if ( null == input )
        {
            throw new InvalidParameterException("input null");
        }

        final long current = input.getFilePointer();
        if ( 0 != current )
        {
            input.seek( 0 );
        }

        final int result = input.read( this._ident );
        if ( _ident.length != result )
        {
            return false;
        }

        return true;
    }
    
    public boolean writeElfHeaderIdent( final RandomAccessFile output )
            throws IOException
    {
        if ( null == output )
        {
            throw new InvalidParameterException("input null");
        }

        final long current = output.getFilePointer();
        if ( 0 != current )
        {
            output.seek( 0 );
        }

        output.write( this._ident );
        final long pos = output.getFilePointer();
        if ( this._ident.length != pos )
        {
            return false;
        }

        return true;
    }

    public boolean isElfMagic()
    {
        return isElfMagic( _ident );
    }

    public boolean isElf32()
    {
        return isElf32( _ident );
    }

    public boolean isElf64()
    {
        return isElf64( _ident );
    }

    public boolean isElfLittleEndian()
    {
        return isElfLittleEndian( _ident );
    }

    public boolean isElfBigEndian()
    {
        return isElfBigEndian( _ident );
    }

    public static boolean isElfMagic( final byte[] buff )
    {
        if ( null == buff )
        {
            return false;
        }
        if ( buff.length < IElfFile.ELFMAGIC_COUNT )
        {
            return false;
        }
        
        if ( IElfFile.ELFMAGIC0 != buff[IElfFile.EI_MAGIC0] )
        {
            return false;
        }
        if ( IElfFile.ELFMAGIC1 != buff[IElfFile.EI_MAGIC1] )
        {
            return false;
        }
        if ( IElfFile.ELFMAGIC2 != buff[IElfFile.EI_MAGIC2] )
        {
            return false;
        }
        if ( IElfFile.ELFMAGIC3 != buff[IElfFile.EI_MAGIC3] )
        {
            return false;
        }
        
        return true;
    }

    public static boolean isElf32( final byte[] buff )
    {
        if ( null == buff )
        {
            return false;
        }
        if ( buff.length < IElfFile.EI_NINDENT )
        {
            return false;
        }
        
        if ( IElfFile.ELFCLASS32 != buff[IElfFile.EI_CLASS] )
        {
            return false;
        }
        
        return true;
    }

    public static boolean isElf64( final byte[] buff )
    {
        if ( null == buff )
        {
            return false;
        }
        if ( buff.length < IElfFile.EI_NINDENT )
        {
            return false;
        }
        
        if ( IElfFile.ELFCLASS64 != buff[IElfFile.EI_CLASS] )
        {
            return false;
        }
        
        return true;
    }

    public static boolean isElfLittleEndian( final byte[] buff )
    {
        if ( null == buff )
        {
            return false;
        }
        if ( buff.length < IElfFile.EI_NINDENT )
        {
            return false;
        }
        
        if ( IElfFile.ELFDATA2LSB != buff[IElfFile.EI_DATA] )
        {
            return false;
        }
        
        return true;
    }

    public static boolean isElfBigEndian( final byte[] buff )
    {
        if ( null == buff )
        {
            return false;
        }
        if ( buff.length < IElfFile.EI_NINDENT )
        {
            return false;
        }
        
        if ( IElfFile.ELFDATA2MSB != buff[IElfFile.EI_DATA] )
        {
            return false;
        }
        
        return true;
    }
    
    
    
    public boolean readFile( final String path )
    {
        boolean isElfMagic = false;
        int ElfClass = IElfFile.ELFCLASSNONE;

        {
            RandomAccessFile input = null;
            try
            {
                input = new RandomAccessFile( path, "r" );
                final boolean resultReadIdent = this.readElfHeaderIdent( input );
                if ( false == resultReadIdent )
                {
                    return false;
                }

                if ( this.isElfMagic() )
                {
                    isElfMagic = true;
                    if ( this.isElf32() )
                    {
                        ElfClass = IElfFile.ELFCLASS32;
                    }
                    else
                    if ( this.isElf64() )
                    {
                        ElfClass = IElfFile.ELFCLASS64;
                    }
                }

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
            }
        }

        if ( isElfMagic )
        {
            switch ( ElfClass )
            {
                case IElfFile.ELFCLASS32:
                    _elfFile = new Elf32File();
                    break;
                case IElfFile.ELFCLASS64:
                    System.err.println( "Not implemented ElfClass64" );
                    break;
                default:
                    System.err.println( "Unknown ElfClass" );
                    break;
            }
        }

        if ( null != _elfFile )
        {
            RandomAccessFile input = null;
            try
            {
                input = new RandomAccessFile( path, "r" );
                
                {
                    final boolean result = _elfFile.readElfHeader( input );
                    if ( false == result )
                    {
                        return false;
                    }
                }
                
                {
                    final boolean result = _elfFile.readProgramHeader( input );
                    if ( false == result )
                    {
                        return false;
                    }
                }
                
                {
                    final boolean result = _elfFile.readSectionHeader( input );
                    if ( false == result )
                    {
                        return false;
                    }
                }
                
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
            }
        }

        return true;
    }




    public boolean readElfHeader(RandomAccessFile input) throws IOException
    {
        if ( null == this._elfFile )
        {
            return false;
        }
        
        return this._elfFile.readElfHeader( input );
    }

    public boolean readProgramHeader(RandomAccessFile input) throws IOException
    {
        if ( null == this._elfFile )
        {
            return false;
        }
        
        return this._elfFile.readProgramHeader( input );
    }

    public boolean readSectionHeader(RandomAccessFile input) throws IOException
    {
        if ( null == this._elfFile )
        {
            return false;
        }
        
        return this._elfFile.readSectionHeader( input );
    }

    public boolean writeElfHeader(RandomAccessFile output) throws IOException
    {
        if ( null == this._elfFile )
        {
            return false;
        }
        
        return this._elfFile.writeElfHeader( output );
    }

    public boolean writeProgramHeader(RandomAccessFile output) throws IOException
    {
        if ( null == this._elfFile )
        {
            return false;
        }
        
        return this._elfFile.writeProgramHeader( output );
    }

    public boolean writeSectionHeader(RandomAccessFile output) throws IOException
    {
        if ( null == this._elfFile )
        {
            return false;
        }
        
        return this._elfFile.writeSectionHeader( output );
    }


    public long getElfHeaderHeaderSize()
    {
        if ( null == this._elfFile )
        {
            throw new RuntimeException("_elfFile null");
        }

        return this._elfFile.getElfHeaderHeaderSize();
    }

    public long getElfHeaderSectionHeaderOffset()
    {
        if ( null == this._elfFile )
        {
            throw new RuntimeException("_elfFile null");
        }

        return this._elfFile.getElfHeaderSectionHeaderOffset();
    }


    public long getElfHeaderSectionHeaderStringTableOffset()
    {
        if ( null == this._elfFile )
        {
            throw new RuntimeException("_elfFile null");
        }

        return this._elfFile.getElfHeaderSectionHeaderStringTableOffset();
    }

    public void setElfHeaderSectionHeaderOffset(long offset)
    {
        if ( null == this._elfFile )
        {
            throw new RuntimeException("_elfFile null");
        }
        
        this._elfFile.setElfHeaderSectionHeaderOffset( offset );
    }

    public void setElfHeaderSectionHeaderNumber(int number)
    {
        if ( null == this._elfFile )
        {
            throw new RuntimeException("_elfFile null");
        }
        
        this._elfFile.setElfHeaderSectionHeaderNumber( number );
    }

    public void setElfHeaderSectionHeaderSize(int size)
    {
        if ( null == this._elfFile )
        {
            throw new RuntimeException("_elfFile null");
        }

        this._elfFile.setElfHeaderSectionHeaderSize( size );
    }

    public void setElfHeaderSectionHeaderStringTableIndex(int index)
    {
        if ( null == this._elfFile )
        {
            throw new RuntimeException("_elfFile null");
        }

        this._elfFile.setElfHeaderSectionHeaderStringTableIndex( index );
    }

    public boolean hasSectionDebug()
    {
        if ( null == this._elfFile )
        {
            return false;
        }
        
        return this._elfFile.hasSectionDebug();
    }

    static final int SHT_NULL = 0;
    static final int SHT_PROGBITS = 1;
    static final int SHT_SYMTAB = 2;
    static final int SHT_STRTAB = 3;
    static final int SHT_RELA = 4;
    static final int SHT_HASH = 5;
    static final int SHT_DYNAMIC = 6;
    static final int SHT_NOTE = 7;
    static final int SHT_NOBITS = 8;
    static final int SHT_REL = 9;
    static final int SHT_SHLIB = 10;
    static final int SHT_DYNSYM = 11;

    static final String ELF_BSS             = ".bss";
    static final String ELF_DATA            = ".data";
    static final String ELF_DEBUG           = ".debug";
    static final String ELF_DYNAMIC         = ".dynamic";
    static final String ELF_DYNSTR          = ".dynstr";
    static final String ELF_DYNSYM          = ".dynsym";
    static final String ELF_FINI            = ".fini";
    static final String ELF_GOT             = ".got";
    static final String ELF_HASH            = ".hash";
    static final String ELF_INIT            = ".init";
    static final String ELF_REL_DATA        = ".rel.data";
    static final String ELF_REL_FINI        = ".rel.fini";
    static final String ELF_REL_INIT        = ".rel.init";
    static final String ELF_REL_DYN         = ".rel.dyn";
    static final String ELF_REL_RODATA      = ".rel.rodata";
    static final String ELF_REL_TEXT        = ".rel.text";
    static final String ELF_RODATA          = ".rodata";
    static final String ELF_SHSTRTAB        = ".shstrtab";
    static final String ELF_STRTAB          = ".strtab";
    static final String ELF_SYMTAB          = ".symtab";
    static final String ELF_TEXT            = ".text";
    
}
