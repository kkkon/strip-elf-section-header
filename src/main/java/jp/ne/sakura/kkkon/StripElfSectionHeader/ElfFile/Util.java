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
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 *
 * @author Kiyofumi Kondoh
 */
public class Util
{
    public static short read2byte(
            final byte[] buff
            , final RandomAccessFile inStream
            , final boolean isLittleEndian
            )
            throws IOException
    {
        if ( null == buff )
        {
            throw new InvalidParameterException("buff is null");
        }
        if ( null == inStream )
        {
            throw new InvalidParameterException("inStream is null");
        }
        
        short result = 0;

        {
            final int length = 2;
            final int count = inStream.read(buff, 0, length);
            if ( length != count )
            {
                throw new IOException("short read");
            }
            else
            {
                if ( isLittleEndian )
                {
                    result = (short)(
                              ((buff[1] << 8) & 0xFF00)
                            + ((buff[0] << 0) & 0x00FF)
                            );
                }
                else
                {
                    result = (short)(
                              ((buff[0] << 8) & 0xFF00)
                            + ((buff[1] << 0) & 0x00FF)
                            );
                }
            }
        }
        
        return result;
    }

    public static int read4byte(
            final byte[] buff
            , final RandomAccessFile inStream
            , final boolean isLittleEndian
            )
            throws IOException
    {
        if ( null == buff )
        {
            throw new InvalidParameterException("buff is null");
        }
        if ( null == inStream )
        {
            throw new InvalidParameterException("inStream is null");
        }
        
        int result = 0;

        {
            final int length = 4;
            final int count = inStream.read(buff, 0, length);
            if ( length != count )
            {
                throw new IOException("short read");
            }
            else
            {
                if ( isLittleEndian )
                {
                    result = (int)(
                              ((buff[3] << 24) & 0xFF000000)
                            + ((buff[2] << 16) & 0x00FF0000)
                            + ((buff[1] <<  8) & 0x0000FF00)
                            + ((buff[0] <<  0) & 0x000000FF)
                            );
                }
                else
                {
                    result = (int)(
                              ((buff[0] << 24) & 0xFF000000)
                            + ((buff[1] << 16) & 0x00FF0000)
                            + ((buff[2] <<  8) & 0x0000FF00)
                            + ((buff[3] <<  0) & 0x000000FF)
                            );
                }
            }
        }
        
        return result;
    }

    public static void write2byte(
            final byte[] buff
            , final short value
            , final OutputStream outStream
            , final boolean isLittleEndian
            )
            throws IOException
    {
        if ( null == buff )
        {
            throw new InvalidParameterException("buff is null");
        }
        if ( null == outStream )
        {
            throw new InvalidParameterException("outStream is null");
        }

        {
            final int length = 2;

            if ( isLittleEndian )
            {
                buff[1] = (byte)((value >> 8) & 0xFF);
                buff[0] = (byte)((value >> 0) & 0xFF);
            }
            else
            {
                buff[0] = (byte)((value >> 8) & 0xFF);
                buff[1] = (byte)((value >> 0) & 0xFF);
            }
            outStream.write( buff, 0, length );
        }

        return;
    }

    public static void write4byte(
            final byte[] buff
            , final int value
            , final OutputStream outStream
            , final boolean isLittleEndian
            )
            throws IOException
    {
        if ( null == buff )
        {
            throw new InvalidParameterException("buff is null");
        }
        if ( null == outStream )
        {
            throw new InvalidParameterException("outStream is null");
        }

        {
            final int length = 4;

            if ( isLittleEndian )
            {
                buff[3] = (byte)((value >> 24) & 0xFF);
                buff[2] = (byte)((value >> 16) & 0xFF);
                buff[1] = (byte)((value >>  8) & 0xFF);
                buff[0] = (byte)((value >>  0) & 0xFF);
            }
            else
            {
                buff[0] = (byte)((value >> 24) & 0xFF);
                buff[1] = (byte)((value >> 16) & 0xFF);
                buff[2] = (byte)((value >>  8) & 0xFF);
                buff[3] = (byte)((value >>  0) & 0xFF);
            }
            outStream.write( buff, 0, length );
        }

        return;
    }
    
}
