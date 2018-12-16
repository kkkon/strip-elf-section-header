/**
 * The MIT License
 * 
 * Copyright (C) 2018 Kiyofumi Kondoh
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
*
* @author Kiyofumi Kondoh
*/
public class UtilTest {

    static final String mFileNameEndianLittle = "target/test-classes/test-util-little.bin";
    static final String mFileNameEndianBig = "target/test-classes/test-util-big.bin";

    static final String mFileNameEndianLittleOut = "target/test-classes/test-util-little.out";
    static final String mFileNameEndianBigOut = "target/test-classes/test-util-big.out";

    static final byte[] mEndianLittle = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
    };
    static final byte[] mEndianBig = {
            0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01,
    };

    @BeforeClass
    public static void setUpClass()
    {
        try
        {
            final RandomAccessFile fileLittle = new RandomAccessFile( mFileNameEndianLittle, "rw");
            final RandomAccessFile fileBig = new RandomAccessFile( mFileNameEndianBig, "rw");

            fileLittle.write( mEndianLittle );
            fileLittle.close();
            fileBig.write( mEndianBig );
            fileBig.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
            fail();
        }
    }

    @AfterClass
    public static void tearDownClass()
    {
        final File fileLittle = new File( mFileNameEndianLittle );
        fileLittle.delete();
        final File fileBig = new File( mFileNameEndianBig );
        fileBig.delete();
    }

	@Test
	public void testRead2byte() {
        byte[] buff = new byte[2];

        try
        {
            {
            	final RandomAccessFile fileLittle = new RandomAccessFile( mFileNameEndianLittle, "r");
                final boolean isLittleEndian = false;
                final long value = Util.read2byte( buff, fileLittle, isLittleEndian );
                fileLittle.close();
                assertEquals( 0x0102L, value );
            }

            {
            	final RandomAccessFile fileBig = new RandomAccessFile( mFileNameEndianBig, "r");
                final boolean isLittleEndian = true;
                final long value = Util.read2byte( buff, fileBig, isLittleEndian );
                fileBig.close();
                assertEquals( 0x0708L, value );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
            fail();
        }
	}

	@Test
	public void testRead4byte() {
        byte[] buff = new byte[4];

        try
        {
            {
            	final RandomAccessFile fileLittle = new RandomAccessFile( mFileNameEndianLittle, "r");
                final boolean isLittleEndian = false;
                final long value = Util.read4byte( buff, fileLittle, isLittleEndian );
                fileLittle.close();
                assertEquals( 0x01020304L, value );
            }

            {
            	final RandomAccessFile fileBig = new RandomAccessFile( mFileNameEndianBig, "r");
                final boolean isLittleEndian = true;
                final long value = Util.read4byte( buff, fileBig, isLittleEndian );
                fileBig.close();
                assertEquals( 0x05060708L, value );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
            fail();
        }
	}

	@Test
	public void testRead8byte() {
        byte[] buff = new byte[8];

        try
        {
            {
            	final RandomAccessFile fileLittle = new RandomAccessFile( mFileNameEndianLittle, "r");
                final boolean isLittleEndian = false;
                final long value = Util.read8byte( buff, fileLittle, isLittleEndian );
                fileLittle.close();
                assertEquals( 0x0102030405060708L, value );
            }

            {
            	final RandomAccessFile fileBig = new RandomAccessFile( mFileNameEndianBig, "r");
                final boolean isLittleEndian = true;
                final long value = Util.read8byte( buff, fileBig, isLittleEndian );
                fileBig.close();
                assertEquals( 0x0102030405060708L, value );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
            fail();
        }
	}

	@Test
	public void testWrite2byte() {
        byte[] buff = new byte[2];

        try
        {
            {
                final RandomAccessFile fileLittle = new RandomAccessFile( mFileNameEndianLittleOut, "rw");
                final boolean isLittleEndian = false;
                final long value = 0x0102;
                Util.write2byte( buff, (short)value, fileLittle, isLittleEndian );
                fileLittle.close();
                assertEquals( 0x01, buff[0] );
                assertEquals( 0x02, buff[1] );
                final File file = new File( mFileNameEndianLittleOut );
                file.delete();
            }

            {
                final RandomAccessFile fileBig = new RandomAccessFile( mFileNameEndianBigOut, "rw");
                final boolean isLittleEndian = true;
                final long value = 0x0102;
                Util.write2byte( buff, (short)value, fileBig, isLittleEndian );
                fileBig.close();
                assertEquals( 0x02, buff[0] );
                assertEquals( 0x01, buff[1] );
                final File file = new File( mFileNameEndianBigOut );
                file.delete();
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
            fail();
        }
	}

	@Test
	public void testWrite4byte() {
        byte[] buff = new byte[4];

        try
        {
            {
                final RandomAccessFile fileLittle = new RandomAccessFile( mFileNameEndianLittleOut, "rw");
                final boolean isLittleEndian = false;
                final long value = 0x01020304L;
                Util.write4byte( buff, (int)value, fileLittle, isLittleEndian );
                fileLittle.close();
                assertEquals( 0x01, buff[0] );
                assertEquals( 0x02, buff[1] );
                assertEquals( 0x03, buff[2] );
                assertEquals( 0x04, buff[3] );
                final File file = new File( mFileNameEndianLittleOut );
                file.delete();
            }

            {
                final RandomAccessFile fileBig = new RandomAccessFile( mFileNameEndianBigOut, "rw");
                final boolean isLittleEndian = true;
                final long value = 0x01020304L;
                Util.write4byte( buff, (int)value, fileBig, isLittleEndian );
                fileBig.close();
                assertEquals( 0x04, buff[0] );
                assertEquals( 0x03, buff[1] );
                assertEquals( 0x02, buff[2] );
                assertEquals( 0x01, buff[3] );
                final File file = new File( mFileNameEndianBigOut );
                file.delete();
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
            fail();
        }
	}

	@Test
	public void testWrite8byte() {
        byte[] buff = new byte[8];

        try
        {
            {
                final RandomAccessFile fileLittle = new RandomAccessFile( mFileNameEndianLittleOut, "rw");
                final boolean isLittleEndian = false;
                final long value = 0x0102030405060708L;
                Util.write8byte( buff, (long)value, fileLittle, isLittleEndian );
                fileLittle.close();
                assertEquals( 0x01, buff[0] );
                assertEquals( 0x02, buff[1] );
                assertEquals( 0x03, buff[2] );
                assertEquals( 0x04, buff[3] );
                assertEquals( 0x05, buff[4] );
                assertEquals( 0x06, buff[5] );
                assertEquals( 0x07, buff[6] );
                assertEquals( 0x08, buff[7] );
                final File file = new File( mFileNameEndianLittleOut );
                file.delete();
            }

            {
                final RandomAccessFile fileBig = new RandomAccessFile( mFileNameEndianBigOut, "rw");
                final boolean isLittleEndian = true;
                final long value = 0x0102030405060708L;
                Util.write8byte( buff, (long)value, fileBig, isLittleEndian );
                fileBig.close();
                assertEquals( 0x08, buff[0] );
                assertEquals( 0x07, buff[1] );
                assertEquals( 0x06, buff[2] );
                assertEquals( 0x05, buff[3] );
                assertEquals( 0x04, buff[4] );
                assertEquals( 0x03, buff[5] );
                assertEquals( 0x02, buff[6] );
                assertEquals( 0x01, buff[7] );
                final File file = new File( mFileNameEndianBigOut );
                file.delete();
            }

        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
            fail();
        }
	}

}
