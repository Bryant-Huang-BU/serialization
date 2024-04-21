package metanode.serialization.test;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.List;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;
//[]
import metanode.serialization.*;
public class testmetanode {
        @Test
        public void testValidMessage() {
            byte[] enc = new byte[] {64,0,0,0};
            try {
                Message m = new Message(enc);
                assertAll(() -> assertEquals(m.getVersion(), 4),
                () -> assertEquals(m.getType(),MessageType.getByCode(0)), ()-> assertEquals(m.getError(),
                ErrorType.getByCode(0)), () -> assertEquals(m.getSessionID(), 0));
            } catch (IOException e) {
                System.out.println(e.getMessage());
                fail();
            }
        }

    @Test
    public void testencode() {
        byte[] enc = new byte[] {64,0,0,0};
        try {
            Message m = new Message(enc);
            assertArrayEquals(enc, m.encode());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void testValidMessageWithData() {
        byte[] enc = new byte[] {70,0,0,1,127,0,0,1,0,100};
        try {
            Message m = new Message(enc);
            assertArrayEquals(enc, m.encode());
            InetSocketAddress test = new InetSocketAddress("127.0.0.1", 100);
            List testlist = new ArrayList<InetSocketAddress>();
            testlist.add(test);
            assertEquals(testlist, m.getAddresses());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test(expected = IOException.class)
    public void testInValidMessageWithBadIPData() throws IOException{
        byte[] enc = new byte[] {70,0,0,3,127,0,0,1,(byte) 259, (byte) 259, (byte) 259, (byte) 259,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
        InetSocketAddress test = new InetSocketAddress("127.0.0.1", 100);
        List testlist = new ArrayList<InetSocketAddress>();
        testlist.add(test);
        assertEquals(testlist, m.getAddresses());
    }

    @Test
    public void testValidTwo() throws IOException{
        byte[] enc = new byte[] {70,0,0,2,127,0,0,2, 1,-1,(byte) 259, (byte) 259, (byte) 259, (byte) 259,0,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
        InetSocketAddress test = new InetSocketAddress("127.0.0.2", 511);
        InetSocketAddress test1 = new InetSocketAddress("3.3.3.3", 0);
        List testlist = new ArrayList<InetSocketAddress>();
        testlist.add(test);
        testlist.add(test1);
        assertEquals(testlist, m.getAddresses());
    }

    @Test
    public void test() throws IllegalArgumentException, IOException {
        Message msg = new Message(new byte[] { 0x41, 0, 59, 0 });
        assertEquals(MessageType.RequestMetaNodes, msg.getType());
        assertEquals(ErrorType.None, msg.getError());
        assertEquals(59, msg.getSessionID());
        assertEquals(0, msg.getAddrList().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidError() throws IOException, IllegalArgumentException{
        byte[] enc = new byte[] {66,11,0,2,127,0,0,2, 1, -1,(byte) 259, (byte) 259, (byte) 259, (byte) 259,0,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
        InetSocketAddress test = new InetSocketAddress("127.0.0.2", 255);
        InetSocketAddress test1 = new InetSocketAddress("3.3.3.3", 0);
        List testlist = new ArrayList<InetSocketAddress>();
        testlist.add(test);
        testlist.add(test1);
        assertEquals(testlist, m.getAddresses());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidErrorNotAR() throws IllegalArgumentException, IOException {
        byte[] enc = new byte[] {70,10,0,2,127,0,0,2, -1,(byte) 259, (byte) 259, (byte) 259, (byte) 259,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
        InetSocketAddress test = new InetSocketAddress("127.0.0.2", 255);
        InetSocketAddress test1 = new InetSocketAddress("3.3.3.3", 0);
        List testlist = new ArrayList<InetSocketAddress>();
        testlist.add(test);
        testlist.add(test1);
        assertEquals(testlist, m.getAddresses());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidError20() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {70,20,0,2,127,0,0,2, -1,(byte) 259, (byte) 259, (byte) 259, (byte) 259,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
        InetSocketAddress test = new InetSocketAddress("127.0.0.2", 255);
        InetSocketAddress test1 = new InetSocketAddress("3.3.3.3", 0);
        List testlist = new ArrayList<InetSocketAddress>();
        testlist.add(test);
        testlist.add(test1);
        assertEquals(testlist, m.getAddresses());
    }
    @Test
    public void testValidParam() throws IOException{
        Message m = new Message(MessageType.getByCode(2), ErrorType.getByCode(10), 10);
        m.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        m.addAddress(new InetSocketAddress("0.0.0.0", 0));
        Message m1 = new Message(MessageType.getByCode(2), ErrorType.getByCode(10), 10);
        m1.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        m1.addAddress(new InetSocketAddress("0.0.0.0", 0));
        assertAll(() -> assertEquals(m.getType(), MessageType.AnswerRequest),
        () -> assertEquals(m.getError(), ErrorType.System), () -> assertEquals(m.getSessionID(), 10),
        () -> assertEquals(m.getAddresses().get(0), new InetSocketAddress("255.255.255.255", 65535)),
        () -> assertEquals(m.getAddresses().get(1), new InetSocketAddress("0.0.0.0", 0)));
        assertEquals(m, m1);
        assertEquals(m1.hashCode(), m.hashCode());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testValidParamNotEqualsNotOrder() throws IOException{
        Message m = new Message(MessageType.getByCode(2), ErrorType.getByCode(10), 10);
        m.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        m.addAddress(new InetSocketAddress("0.0.0.0", 0));
        Message m1 = new Message(MessageType.getByCode(1), ErrorType.getByCode(10), 10);
        m1.addAddress(new InetSocketAddress("0.0.0.0", 0));
        m1.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        assertAll(() -> assertEquals(m.getType(), MessageType.RequestMetaNodes),
                () -> assertEquals(m.getError(), ErrorType.System), () -> assertEquals(m.getSessionID(), 10),
                () -> assertEquals(m.getAddresses().get(0), new InetSocketAddress("255.255.255.255", 65535)),
                () -> assertEquals(m.getAddresses().get(1), new InetSocketAddress("0.0.0.0", 0)));
        assertNotEquals(m, m1);
        assertNotEquals(m1.hashCode(), m.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidParamNotEqualsMesnull() throws IOException, IllegalArgumentException{
        Message m = new Message(null, ErrorType.getByCode(10), 10);
        m.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        m.addAddress(new InetSocketAddress("0.0.0.0", 0));
        Message m1 = new Message(MessageType.getByCode(1), ErrorType.getByCode(10), 10);
        m1.addAddress(new InetSocketAddress("0.0.0.0", 0));
        m1.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        assertAll(() -> assertEquals(m.getType(), MessageType.RequestMetaNodes),
                () -> assertEquals(m.getError(), ErrorType.System), () -> assertEquals(m.getSessionID(), 10),
                () -> assertEquals(m.getAddresses().get(0), new InetSocketAddress("255.255.255.255", 65535)),
                () -> assertEquals(m.getAddresses().get(1), new InetSocketAddress("0.0.0.0", 0)));
        assertNotEquals(m, m1);
        assertNotEquals(m1.hashCode(), m.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidParamNotEqualsErrnull() throws IOException{
        Message m = new Message(MessageType.getByCode(1), null, 10);
        m.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        m.addAddress(new InetSocketAddress("0.0.0.0", 0));
        Message m1 = new Message(MessageType.getByCode(1), ErrorType.getByCode(10), 10);
        m1.addAddress(new InetSocketAddress("0.0.0.0", 0));
        m1.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        assertAll(() -> assertEquals(m.getType(), MessageType.RequestMetaNodes),
                () -> assertEquals(m.getError(), ErrorType.System), () -> assertEquals(m.getSessionID(), 10),
                () -> assertEquals(m.getAddresses().get(0), new InetSocketAddress("255.255.255.255", 65535)),
                () -> assertEquals(m.getAddresses().get(1), new InetSocketAddress("0.0.0.0", 0)));
        assertNotEquals(m, m1);
        assertNotEquals(m1.hashCode(), m.hashCode());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testValidParambaderror() throws IOException, IllegalArgumentException{
        Message m = new Message(MessageType.getByCode(1), ErrorType.getByCode(10), 10);
        m.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        m.addAddress(new InetSocketAddress("0.0.0.0", 0));
        Message m1 = new Message(MessageType.getByCode(1), ErrorType.getByCode(10), 10);
        m1.addAddress(new InetSocketAddress("0.0.0.0", 0));
        m1.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        assertAll(() -> assertEquals(m.getType(), MessageType.RequestMetaNodes),
                () -> assertEquals(m.getError(), ErrorType.System), () -> assertEquals(m.getSessionID(), 10),
                () -> assertEquals(m.getAddresses().get(0), new InetSocketAddress("255.255.255.255", 65535)),
                () -> assertEquals(m.getAddresses().get(1), new InetSocketAddress("0.0.0.0", 0)));
        assertNotEquals(m, m1);
        assertNotEquals(m1.hashCode(), m.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidParambasesh() throws IOException{
        Message m = new Message(MessageType.getByCode(2), ErrorType.getByCode(10), 100000);
        m.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        m.addAddress(new InetSocketAddress("0.0.0.0", 0));
        Message m1 = new Message(MessageType.getByCode(1), ErrorType.getByCode(10), 10);
        m1.addAddress(new InetSocketAddress("0.0.0.0", 0));
        m1.addAddress(new InetSocketAddress("255.255.255.255", 65535));
        assertAll(() -> assertEquals(m.getType(), MessageType.RequestMetaNodes),
                () -> assertEquals(m.getError(), ErrorType.System), () -> assertEquals(m.getSessionID(), 10),
                () -> assertEquals(m.getAddresses().get(0), new InetSocketAddress("255.255.255.255", 65535)),
                () -> assertEquals(m.getAddresses().get(1), new InetSocketAddress("0.0.0.0", 0)));
        assertNotEquals(m, m1);
        assertNotEquals(m1.hashCode(), m.hashCode());
    }
    @Test
    public void testInValidEnum(){
        assertNull(ErrorType.getByCode(100));
        assertNull(ErrorType.getByCode(-1));
        assertNull(MessageType.getByCode(100));
        assertNull(MessageType.getByCode(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testversionbad() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {0,0,0,0};
            Message m = new Message(enc);
            assertArrayEquals(enc, m.encode());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testmessagebad() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {79,0,0,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
    }
    @Test(expected = IOException.class)
    public void testmessagebadlenshort() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {69,0,0,1,0,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
    }
    @Test(expected = IOException.class)
    public void testmessagebadlenlong() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {69,0,0,1,0,0,0,0,0,0,0,0,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testmessagebadlcnttype() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {64,0,0,1,0,0,0,0,0,0};
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
    }
    @Test(expected = IOException.class)
    public void testmessagenullbad() throws IllegalArgumentException, IOException{
        byte[] enc = null;
        Message m = new Message(enc);
        assertArrayEquals(enc, m.encode());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testMessageChangeID() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {64,0,0,0};
        Message m = new Message(enc);
        m.setSessionID(-1);
        assertArrayEquals(enc, m.encode());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testMessageChangeIDMidfliht() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {64,0,0,0};
        Message m = new Message(enc);
        m.setSessionID(2000);
        assertArrayEquals(enc, m.encode());
    }
    @Test
    public void toStringValidTest() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {68,0,5,2,1,1,1,1,0,50,2,2,2,2,0,70};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=MetaNodeAdditions Error=None Session ID=5 Addrs=1.1.1.1:50 2.2.2.2:70", m.toString());
    }
    @Test
    public void toStringInValidTest() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {68,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=MetaNodeAdditions Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestReqNodes() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {64,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=RequestNodes Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestMetaNode() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {65,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=RequestMetaNodes Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestAR() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {66,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=AnswerRequest Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestNA() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {67,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=NodeAdditions Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestMA() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {68,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=MetaNodeAdditions Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestND() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {69,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=NodeDeletions Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestMD() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {70,0,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=MetaNodeDeletions Error=None Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestSystemFail() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {66,10,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=AnswerRequest Error=System Session ID=5 Addrs=", m.toString());
    }

    @Test
    public void toStringTestPacket() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {66,20,5,0};
        Message m = new Message(enc);
        //  m.setSessionID(2000);
        assertEquals("Type=AnswerRequest Error=IncorrectPacket Session ID=5 Addrs=", m.toString());
    }
    @Test
    public void toStringTestPacketLocalhost() throws IllegalArgumentException, IOException{
        Message m = new Message(MessageType.getByCode(2), ErrorType.IncorrectPacket, 5);
        m.addAddress(new InetSocketAddress(Inet4Address.getLoopbackAddress(), 1010));
        //  m.setSessionID(2000);
        assertEquals("Type=AnswerRequest Error=IncorrectPacket Session ID=5 Addrs=127.0.0.1:1010", m.toString());
    }
    @Test
    public void decodeTest() throws IllegalArgumentException, IOException{
        byte[] enc = new byte[] {66,20,36,2, 127,0,0,1,1,1,127,0,0,1,1,1};
        Message m = new Message(enc);
        byte[] test = new byte [] {66,20,36,1,127,0,0,1,1,1};
        assertArrayEquals(m.encode(), test);
    }
}
