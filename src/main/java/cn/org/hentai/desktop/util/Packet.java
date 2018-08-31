package cn.org.hentai.desktop.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by matrixy on 2018/4/14.
 */
public class Packet
{
    int size = 0;
    int offset = 0;
    int maxSize = 0;
    public byte[] data;

    private Packet()
    {
        // do nothing here..
    }

    /**
     * 创建协议数据包
     * @param command 指令，参见cn.org.hentai.tentacle.protocol.Command类
     * @param length 数据包的长度
     * @return
     */
    public static Packet create(byte command, int length)
    {
        Packet p = new Packet();
        p.data = new byte[length + 6 + 1 + 4];
        p.data[0] = 'H';
        p.data[1] = 'E';
        p.data[2] = 'N';
        p.data[3] = 'T';
        p.data[4] = 'A';
        p.data[5] = 'I';
        p.data[6] = command;
        System.arraycopy(ByteUtils.toBytes(length), 0, p.data, 7, 4);
        p.size = 11;
        p.maxSize = length;
        return p;
    }

    /**
     * 从流中读取并建立一个数据包
     * @param inputStream
     * @return
     */
    public static Packet read(InputStream inputStream) throws Exception
    {
        if (inputStream.available() < 11) return null;
        byte[] head = new byte[11];
        int len = inputStream.read(head);
        int dataLength = ByteUtils.getInt(head, 7,4) & 0x7fffff;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(dataLength + 10);
        byte[] buff = new byte[512];
        for (int i = 0; i < dataLength; i += len)
        {
            len = inputStream.read(buff, 0, Math.min(512, dataLength - i));
            if (len == -1) break;
            baos.write(buff, 0, len);
        }
        Packet p = new Packet();
        p.data = new byte[dataLength + 6 + 1 + 4];
        p.size = 0;
        p.maxSize = p.size;
        p.addBytes(head);
        p.addBytes(baos.toByteArray());
        return p;
    }

    public Packet addByte(byte b)
    {
        this.data[size++] = b;
        return this;
    }

    public Packet addShort(short s)
    {
        this.data[size++] = (byte)((s >> 8) & 0xff);
        this.data[size++] = (byte)(s & 0xff);
        return this;
    }

    public Packet addInt(int i)
    {
        this.data[size++] = (byte)((i >> 24) & 0xff);
        this.data[size++] = (byte)((i >> 16) & 0xff);
        this.data[size++] = (byte)((i >> 8) & 0xff);
        this.data[size++] = (byte)(i & 0xff);
        return this;
    }

    public Packet addLong(long l)
    {
        this.data[size++] = (byte)((l >> 56) & 0xff);
        this.data[size++] = (byte)((l >> 48) & 0xff);
        this.data[size++] = (byte)((l >> 40) & 0xff);
        this.data[size++] = (byte)((l >> 32) & 0xff);
        this.data[size++] = (byte)((l >> 24) & 0xff);
        this.data[size++] = (byte)((l >> 16) & 0xff);
        this.data[size++] = (byte)((l >> 8) & 0xff);
        this.data[size++] = (byte)(l & 0xff);
        return this;
    }

    public Packet addBytes(byte[] b)
    {
        System.arraycopy(b, 0, this.data, size, b.length);
        size += b.length;
        return this;
    }

    public Packet rewind()
    {
        this.offset = 0;
        return this;
    }

    public byte nextByte()
    {
        return this.data[offset++];
    }

    public short nextShort()
    {
        return (short)(((this.data[offset++] & 0xff) << 8) | (this.data[offset++] & 0xff));
    }

    public int nextInt()
    {
        return (this.data[offset++] & 0xff) << 24 | (this.data[offset++] & 0xff) << 16 | (this.data[offset++] & 0xff) << 8 | (this.data[offset++] & 0xff);
    }

    public long nextLong()
    {
        return ((long)this.data[offset++] & 0xff) << 56
                | ((long)this.data[offset++] & 0xff) << 48
                | ((long)this.data[offset++] & 0xff) << 40
                | ((long)this.data[offset++] & 0xff) << 32
                | ((long)this.data[offset++] & 0xff) << 24
                | ((long)this.data[offset++] & 0xff) << 16
                | ((long)this.data[offset++] & 0xff) << 8
                | ((long)this.data[offset++] & 0xff);
    }

    public byte[] nextBytes(int length)
    {
        byte[] buf = new byte[length];
        System.arraycopy(this.data, offset, buf, 0, length);
        offset += length;
        return buf;
    }

    public Packet skip(int offset)
    {
        this.offset += offset;
        return this;
    }

    public Packet seek(int index)
    {
        this.offset = index;
        return this;
    }

    public byte[] getBytes()
    {
        if (size == maxSize) return this.data;
        else
        {
            byte[] buff = new byte[size];
            System.arraycopy(this.data, 0, buff, 0, size);
            return buff;
        }
    }
}
