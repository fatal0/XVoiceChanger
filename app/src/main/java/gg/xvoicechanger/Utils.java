package gg.xvoicechanger;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Utils {

    public static byte[] shortToByte(short[] buf){

        byte[] bytes = new byte[buf.length * 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(buf);
        return bytes;

    }

    public static short[] byteToShort(byte[] buf, int len){

        short[] shorts = new short[len/2];
        ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        return shorts;


    }
}

