/*
 * The MIT License
 *
 * Copyright 2013 kkkon.
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

/**
 *
 * @author Kiyofumi Kondoh
 */
public interface IElfFile
{
    //abstract public boolean readElfHeaderIdent( final RandomAccessFile input ) throws IOException;
    abstract public boolean readElfHeader( final RandomAccessFile input ) throws IOException;
    abstract public boolean readProgramHeader( final RandomAccessFile input ) throws IOException;
    abstract public boolean readSectionHeader( final RandomAccessFile input ) throws IOException;

    //abstract public boolean writeElfHeaderIdent( final RandomAccessFile output ) throws IOException;
    abstract public boolean writeElfHeader( final RandomAccessFile output ) throws IOException;
    abstract public boolean writeProgramHeader( final RandomAccessFile output ) throws IOException;
    abstract public boolean writeSectionHeader( final RandomAccessFile output ) throws IOException;

    abstract public long getElfHeaderHeaderSize();
    abstract public long getElfHeaderSectionHeaderOffset();
    
    abstract public long getElfHeaderSectionHeaderStringTableOffset();
    abstract public void setElfHeaderSectionHeaderOffset( long offset );
    abstract public void setElfHeaderSectionHeaderNumber( int number );
    abstract public void setElfHeaderSectionHeaderSize( int size );
    abstract public void setElfHeaderSectionHeaderStringTableIndex( int index );

    abstract public boolean hasSectionDebug();

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
    public static final int ELFDATANONE = 0;
    public static final int ELFDATA2LSB = 1;
    public static final int ELFDATA2MSB = 2;
    public static final int ELFDATA_COUNT = 3;

}
