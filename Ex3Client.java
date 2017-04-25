//Michael Ly, cs380

import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.net.*;

public class Ex3Client {

    protected static byte[] barray;
    protected static int barraysize;
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("codebank.xyz", 38103);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            if(socket.isConnected())
            {
                System.out.println("Connected to server");
            }
            else{
                System.exit(1);
            }

            //gets the first value which is the size of the byte array
            barraysize = is.read();
            barray = new byte[barraysize];
            System.out.print("Reading " + barraysize + " bytes \nData recieved: ");

            //gets the bytes and fills it into byte array
            for(int i = 0; i < barraysize; i++)
            {
                if(i%10 == 0)
                {
                    System.out.println();
                }
                barray[i] = (byte) is.read();
                System.out.printf("%02X", barray[i]);
            }

            //calls checksum algorithm
            short cs = checksum(barray);
            System.out.printf("\nChecksum calculated: 0x%02X\n", cs );

            //seperates checksum into 2 bytes with bytebuffer
            ByteBuffer bcs = ByteBuffer.allocate(2);
            bcs.putShort(cs);

            //sends to server to verify
            for(int i = 0; i < bcs.array().length; i++)
            {
                os.write(bcs.array()[i]);
            }

            int sysres = is.read();

            if(sysres == 1)
            {
                System.out.println("Response is good");
                is.close();
                os.close();
                socket.close();
                if(socket.isClosed())
                {
                    //System.out.println("Disconnected from server");
                }
            }
            else
                System.out.println("Response is bad");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //checksum alg.
    public static short checksum(byte[] b) {
        long sum = 0;
        long temp1, temp2;

        for (int i = 0; i < b.length / 2; i++) {
            temp1 = (b[(i*2)] << 8) & 0xFF00;
            temp2 = (b[(i*2) + 1]) & 0xFF;
            sum += (long) (temp1 + temp2);
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
            }
        }

        //handler for odd length byte array
        if (b.length % 2 == 1)
        {
            sum += ((b[b.length-1] << 8) & 0xFF00);
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
            }
        }
        return (short) ~(sum & 0xFFFF);
    }
}
