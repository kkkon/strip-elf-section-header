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
import java.io.IOException;

import jp.ne.sakura.kkkon.StripElfSectionHeader.AppOption;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class ElfFile
{
    public static final int EI_MAGIC0          = 0;
    public static final int EI_MAGIC1          = 1;
    public static final int EI_MAGIC2          = 2;
    public static final int EI_MAGIC3          = 3;
    public static final int EI_CLASS           = 4;
    public static final int EI_DATA            = 5;
    public static final int EI_VERSION         = 6;
    public static final int EI_OSABI           = 7;
    public static final int EI_ABIVERSION      = 8;
    public static final int EI_PAD             = 9;
    public static final int EI_NINDENT         = 16;

    // ELFMAGIC
    public static final int ELFMAGIC0 = 0x7f;
    public static final int ELFMAGIC1 = 'E';
    public static final int ELFMAGIC2 = 'L';
    public static final int ELFMAGIC3 = 'F';
    public static final int ELFMAGIC_COUNT     = 4;

    // ELFCLASS
    public static final int ELFCLASSNONE = 0;
    public static final int ELFCLASS32 = 1;
    public static final int ELFCLASS64 = 2;
    public static final int ELFCLASS_COUNT = 3;

    // ELFDATA
    static final int ELFDATANONE = 0;
    static final int ELFDATA2LSB = 1;
    static final int ELFDATA2MSB = 2;
    static final int ELFDATA_COUNT = 3;
    
    public static boolean isElfMagic( final byte[] buff )
    {
        if ( null == buff )
        {
            return false;
        }
        if ( buff.length < ELFMAGIC_COUNT )
        {
            return false;
        }
        
        if ( ELFMAGIC0 != buff[EI_MAGIC0] )
        {
            return false;
        }
        if ( ELFMAGIC1 != buff[EI_MAGIC1] )
        {
            return false;
        }
        if ( ELFMAGIC2 != buff[EI_MAGIC2] )
        {
            return false;
        }
        if ( ELFMAGIC3 != buff[EI_MAGIC3] )
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
        if ( buff.length < EI_NINDENT )
        {
            return false;
        }
        
        if ( ELFCLASS32 != buff[EI_CLASS] )
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
        if ( buff.length < EI_NINDENT )
        {
            return false;
        }
        
        if ( ELFCLASS64 != buff[EI_CLASS] )
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
        if ( buff.length < EI_NINDENT )
        {
            return false;
        }
        
        if ( ELFDATA2LSB != buff[EI_DATA] )
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
        if ( buff.length < EI_NINDENT )
        {
            return false;
        }
        
        if ( ELFDATA2MSB != buff[EI_DATA] )
        {
            return false;
        }
        
        return true;
    }
    
    public static boolean isElfFile( final String path )
    {
        boolean isElfFile = false;

        File file = new File(path);
        {
            FileInputStream inStream = null;

            byte[] buff = new byte[ELFMAGIC_COUNT];
            try
            {
                inStream = new FileInputStream( file );
                final int nRet = inStream.read(buff);
                if ( ELFMAGIC_COUNT != nRet )
                {
                    isElfFile = false;
                }
                else
                {
                    isElfFile = isElfMagic( buff );
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

        return isElfFile;
    }

    public static boolean stripElfSectionHeader( final AppOption option, final String path )
    {
        boolean isStripped = false;

        boolean isElfMagic = false;
        int ElfClass = ELFCLASSNONE;

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
                        isElfMagic = true;
                        if ( isElf32( buff ) )
                        {
                            ElfClass = ELFCLASS32;
                        }
                        else
                        if ( isElf64( buff ) )
                        {
                            ElfClass = ELFCLASS64;
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

        if ( isElfMagic )
        {
            switch ( ElfClass )
            {
                case ELFCLASS32:
                    isStripped = Elf32File.stripSectionHeader( option, path );
                    break;
                case ELFCLASS64:
                    System.err.println( "Not implemented ElfClass64" );
                    break;
                default:
                    System.err.println( "Unknown ElfClass" );
                    break;
            }
        }

        return isStripped;
    }
}
