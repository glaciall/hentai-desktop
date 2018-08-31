package cn.org.hentai.desktop.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by matrixy on 2018/4/14.
 */
public class Packet
{
    int size = 0;
    int offset = 0;
    int maxSize = 0;
    public byte[] data;

    protected Packet()
    {
        // do nothing here..
    }

    public int size()
    {
        return size;
    }

    public void resizeTo(int size)
    {
        if (this.maxSize >= size) return;
        byte[] old = Arrays.copyOf(this.data, this.offset);
        this.data = new byte[size];
        System.arraycopy(old, 0, this.data, 0, old.length);
    }

    /**
     * 以指定的数据直接创建数据包
     * @param command
     * @param bizId
     * @param data
     * @return
     */
    public static Packet create(short command, long bizId, byte[] data)
    {
        Packet p = new Packet();
        p.size = 2 + 2 + 8 + 4 + data.length;
        p.maxSize = p.size;
        p.data = new byte[p.size];
        p.data[0] = (byte)0xfa;
        p.data[1] = (byte)0xfa;
        p.data[2] = (byte)(command >> 8);
        p.data[3] = (byte)(command & 0xff);
        System.arraycopy(ByteUtils.toBytes(bizId), 0, p.data, 4, 8);
        System.arraycopy(ByteUtils.toBytes(data.length), 0, p.data, 12, 4);
        System.arraycopy(data, 0, p.data, 16, data.length);
        p.offset = p.size;
        return p;
    }

    /**
     * 按指定的大小初始化一个数据包，一般大小为2 + 2 + 8 + 4 + DATA_LENGTH
     * @param packetLength 数据包最大字节数
     * @return
     */
    public static Packet create(int packetLength)
    {
        Packet p = new Packet();
        p.data = new byte[packetLength];
        p.maxSize = packetLength;
        return p;
    }

    public static Packet create(byte[] data)
    {
        Packet p = new Packet();
        p.data = data;
        p.maxSize = data.length;
        p.size = data.length;
        p.offset = 0;
        return p;
    }

    public void reset()
    {
        this.size = 0;
        this.offset = 0;
    }

    public Packet addByte(byte b)
    {
        this.data[size++] = b;
        return this;
    }

    public Packet putByte(byte b)
    {
        this.data[offset++] = b;
        return this;
    }

    public Packet addShort(short s)
    {
        this.data[size++] = (byte)((s >> 8) & 0xff);
        this.data[size++] = (byte)(s & 0xff);
        return this;
    }

    public Packet putShort(short s)
    {
        this.data[offset++] = (byte)((s >> 8) & 0xff);
        this.data[offset++] = (byte)(s & 0xff);
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

    public Packet putInt(int i)
    {
        this.data[offset++] = (byte)((i >> 24) & 0xff);
        this.data[offset++] = (byte)((i >> 16) & 0xff);
        this.data[offset++] = (byte)((i >> 8) & 0xff);
        this.data[offset++] = (byte)(i & 0xff);
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

    public Packet putLong(long l)
    {
        this.data[offset++] = (byte)((l >> 56) & 0xff);
        this.data[offset++] = (byte)((l >> 48) & 0xff);
        this.data[offset++] = (byte)((l >> 40) & 0xff);
        this.data[offset++] = (byte)((l >> 32) & 0xff);
        this.data[offset++] = (byte)((l >> 24) & 0xff);
        this.data[offset++] = (byte)((l >> 16) & 0xff);
        this.data[offset++] = (byte)((l >> 8) & 0xff);
        this.data[offset++] = (byte)(l & 0xff);
        return this;
    }

    public Packet addBytes(byte[] b)
    {
        System.arraycopy(b, 0, this.data, size, b.length);
        size += b.length;
        return this;
    }

    public Packet putBytes(byte[] b)
    {
        System.arraycopy(b, 0, this.data, offset, b.length);
        offset += b.length;
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

    public String nextBCD()
    {
        byte val = this.data[offset++];
        int ch1 = (val >> 4) & 0x0f;
        int ch2 = (val & 0x0f);
        return ch1 + "" + ch2;
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

    public static void main(String[] args) throws Exception
    {
        // ...
    }
}
