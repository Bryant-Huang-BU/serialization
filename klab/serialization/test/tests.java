/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
* DISCLAIMER: ALL JAR FILES ARE JUST FOR SMOOTH TESTING
************************************************/

package klab.serialization.test;
import klab.serialization.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;
//[]
public class tests {
    @Test(expected = BadAttributeValueException.class)
    public void testInValidMessageType() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0 };
        Message r = Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()));
    }

    @Test(expected = BadAttributeValueException.class)
    public void testInValidRoutingService() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 0, 3, 'b', 'o', 'b' };
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals("bob", r.getSearchString()));
    }

    @Test
    public void testMaxLength() throws NullPointerException, IOException, BadAttributeValueException {
        //max unsigned int represented by 2 bytes
        byte[] byteArray = new byte[65535];
        Arrays.fill(byteArray, (byte) 'b');
        //max unsigned int represented by 2 bytes
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, (byte) 0xFF,(byte) 0xFF};
        enc = Arrays.copyOf(enc, enc.length + byteArray.length);
        System.arraycopy(byteArray, 0, enc, enc.length - byteArray.length, byteArray.length);
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals(new String(byteArray), r.getSearchString()));

        }

    @Test(expected = BadAttributeValueException.class)
    public void testInValidSearchString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 0, 5, 'b', 'o', 'b', '/', '}' };
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals("bob", r.getSearchString()));
    }
    

    @Test(expected = BadAttributeValueException.class)
    public void testInValidSearchStringLength() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 0, 5, 'b', 'o', 'b', '.' };
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals("bob", r.getSearchString()));
    }

    @Test
    public void testInvalidMaxLength() throws NullPointerException, IOException, BadAttributeValueException {
        //max unsigned int represented by 2 bytes
        byte[] byteArray = new byte[65536];
        Arrays.fill(byteArray, (byte) 'b');
        byte[] correctByteArray = new byte[65535];
        Arrays.fill(correctByteArray, (byte) 'b');
        //max unsigned int represented by 2 bytes
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, (byte) 0xFF,(byte) 0xFF};
        enc = Arrays.copyOf(enc, enc.length + byteArray.length);
        System.arraycopy(byteArray, 0, enc, enc.length - byteArray.length, byteArray.length);
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertNotEquals(new String(byteArray), r.getSearchString()), () -> assertEquals(new String(correctByteArray), r.getSearchString()));
    }
    @Test
    public void testValidSearch() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 'b', 'o', 'b' };
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals("bob", r.getSearchString()));
    }

    @Test
    public void testValidSearchToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] {1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 4, 1, 0, 4, 'L', 'i', 'p', 'a' }; 
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        System.out.println(r.getTTL());
        System.out.println(r.toString());
        assertEquals("Search: ID=010203040506070809101112131415 TTL=4 Routing=DEPTHFIRST Search=Lipa", r.toString());
    }

    @Test
    public void testPrematureEOS() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 1, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 50, 0, 0, 2, 110, 101};
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, r.getID()),
                () -> assertEquals(50, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals("", r.getSearchString())); 
    }

    @Test
    public void testValidEOS() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] {1, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 50, 0, 0, 0 }; 
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, r.getID()),
                () -> assertEquals(50, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals("", r.getSearchString()));
    }

    @Test
    public void testDoublePrematureEOSa() throws IOException {
    // Create a byte array that simulates a message with a double premature EoS error
    byte[] message = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, -1, 0, 0, 7, 0, 0, 13, 2, 2, 2, 2};

    // Create a ByteArrayInputStream to read the message from
    ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
    // Create a MessageInput object to read the message
    MessageInput messageInput = new MessageInput(inputStream);
    // Try to read the message using the MessageInput object
    assertThrows(IOException.class, () -> {
        Response response = (Response) Message.decode(messageInput);
        System.out.println(response.toString());
    });
}

    @Test
    public void testValidSearchEncode() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] {1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 4, 0, 0, 4, 'L', 'i', 'p', 'a' };
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        //System.out.println(r.getRoutingService());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageOutput m = new MessageOutput(outputStream);
        r.encode(m);
        byte[] actualEnc = outputStream.toByteArray();
        for (byte b : actualEnc) {
            System.out.print(b + " ");
        }
        assertArrayEquals(enc, actualEnc);
    }

    @Test
    public void testValidSearchWithSpecialChars() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 6, 'b', 'o', 'b', '-', '_', '.' };
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals("bob-_.", r.getSearchString()));
    }

    @Test
    public void testValidSearchTwoDigitSize() throws NullPointerException, IOException, BadAttributeValueException { //make the size 259
        byte[] byteArray = new byte[259];
        Arrays.fill(byteArray, (byte) 'b');
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 259; i++) {
            sb.append('b');
        }
        byte[] enc = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 3};
        enc = Arrays.copyOf(enc, enc.length + byteArray.length);
        System.arraycopy(byteArray, 0, enc, enc.length - byteArray.length, byteArray.length);
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals(sb.toString(), r.getSearchString()));
    }

    @Test
    public void testResponseEncode() throws BadAttributeValueException, IOException {
        byte[] enc = new byte[] {2, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 4, 0, 0, 3, 1, 0, 23, (byte) 192, (byte) 168, 1, 5, 1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'};
        Response r = (Response) Message.decode(new MessageInput((new ByteArrayInputStream(enc))));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageOutput m = new MessageOutput(outputStream);
        r.encode(m);
        byte[] actualEnc = outputStream.toByteArray();
        for (byte b : actualEnc) {
            System.out.print(b + " ");
        }
        System.out.print('\n');
        for (byte b : enc) {
            System.out.print(b + " ");
        }
        assertArrayEquals(enc, actualEnc);
    }
    @Test
    public void testResponseEncodeLUL() throws BadAttributeValueException, IOException {
        byte[] enc = new byte[] {2, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 50, 0, 0, 17, 1, 0, 13, 2, 2, 2, 2, 1, 2, 3, 4, 0, 0, 0, 56, 111, 10};
        //2.2.2.2:13
        byte[] t = new byte[] {1,2,3,4};
        Result x = new Result(t, (long) 56, new String ("o"));
        Response r = new Response(new byte[] {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, 50, RoutingService.BREADTHFIRST, new java.net.InetSocketAddress("2.2.2.2", 13));
        r.addResult(x);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageOutput m = new MessageOutput(outputStream);
        r.encode(m);
        byte[] actualEnc = outputStream.toByteArray();
        for (byte b : actualEnc) {
            System.out.print(b + " ");
        }
        System.out.print('\n');
        for (byte b : enc) {
            System.out.print(b + " ");
        }
        assertArrayEquals(enc, actualEnc);
    }
    @Test
    public void testValidNegative() throws NullPointerException, IOException, BadAttributeValueException { //make the size 259
        byte[] byteArray = new byte[259];
        Arrays.fill(byteArray, (byte) 'b');
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 259; i++) {
            sb.append('b');
        }
        byte[] enc = new byte[] { 1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 3, 0, 1, 3};
        enc = Arrays.copyOf(enc, enc.length + byteArray.length);
        System.arraycopy(byteArray, 0, enc, enc.length - byteArray.length, byteArray.length);
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, 0,0, 0, 0, 0}, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals(sb.toString(), r.getSearchString()));
    }

    @Test
    public void testValidNegativew() throws NullPointerException, IOException, BadAttributeValueException { //make the size 259
        byte[] byteArray = new byte[259];
        Arrays.fill(byteArray, (byte) 'b');
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 259; i++) {
            sb.append('b');
        }
        byte[] enc = new byte[] { 1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 3, 0, 1, 3};
        enc = Arrays.copyOf(enc, enc.length + byteArray.length);
        System.arraycopy(byteArray, 0, enc, enc.length - byteArray.length, byteArray.length);
        Search r = (Search) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, 0,0, 0, 0, 0}, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()),
                () -> assertEquals(sb.toString(), r.getSearchString()));
    }

    @Test
    public void testValidResponse() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 1, 0, 23, (byte) 192, (byte) 168, 1, 5, 1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'};
        Response r = (Response) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()), () -> assertEquals( 1, r.getMatches()),
                () -> assertEquals("192.168.1.5", r.getResponseHost().getAddress().getHostAddress()), () -> assertEquals( 23, r.getResponseHost().getPort()), () -> assertEquals(new Result(new MessageInput(new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'}))), r.getResultList().get(0)));
    }

    @Test
    public void testValidDoubleResponse() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[]{2, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 50, 0, 0, 17, 1, 0, 13, 2, 2, 2, 2, 1, 2, 3, 4, 0, 0, 0, 56, 111, 10};
        Response r = (Response) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));

    }



    @Test
    public void testValidResponseToString() throws NullPointerException, IOException, BadAttributeValueException {
        Response r = new Response(new byte[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}, 4, RoutingService.DEPTHFIRST, new java.net.InetSocketAddress("1.2.3.4", 5678));
        r.setMatches(2);
        r.setResultList(new ArrayList<Result>());
        r.addResult(new Result(new byte[] {5, 19, -95, -51}, 500, "readme.txt"));
        r.addResult(new Result(new byte[] {18, 52, 86, 120}, 105, "install.me"));
        String result = r.toString();
        String correct = "Response: ID=010203040506070809101112131415 TTL=4 Routing=DEPTHFIRST Host=1.2.3.4:5678 [Result: FileID=0513A1CD FileSize=500 bytes FileName=readme.txt, Result: FileID=12345678 FileSize=105 bytes FileName=install.me]";
        assertEquals(correct, result);
    }

    @Test
    public void testValidResponseMatches() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 0, 0, 23, (byte) 192, (byte) 168, 1, 5, 1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'};
        Response r = (Response) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        for (byte b : r.getID()) {
            System.out.print(b + " ");
        }
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()), () -> assertEquals( 0, r.getMatches()),
                () -> assertEquals("192.168.1.5", r.getResponseHost().getAddress().getHostAddress()), () -> assertEquals( 23, r.getResponseHost().getPort()), () -> assertEquals(0, r.getResultList().size()));
    }

    @Test(expected=IOException.class)
    public void testInValidResponseMatches() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 2, 0, 23, (byte) 192, (byte) 168, 1, 5, 1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'};
        Response r = (Response) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        for (byte b : r.getID()) {
            System.out.print(b + " ");
        }
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()), () -> assertEquals( 0, r.getMatches()),
                () -> assertEquals("192.168.1.5", r.getResponseHost().getAddress().getHostAddress()), () -> assertEquals( 23, r.getResponseHost().getPort()), () -> assertEquals(0, r.getResultList().size()));
    }

    @Test
    public void testResultMessageInput() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 0, 'f', 'o', 'o', '\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(0, r.getFileSize());
        assertEquals("foo", r.getFileName());
  }
    @Test
    public void testResult() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(30, r.getFileSize());
        assertEquals("foo", r.getFileName());
    }
    @Test(expected = IOException.class)
    public void testNotEnough() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 'o', '\n'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
    }
    @Test(expected = IOException.class)
    public void testnonewline() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', 'w', 'h', 'e'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
    }

    @Test
    public void donahootestdecode() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {-1,-1,-1,-1, 0, 0, 0, 56, 111, 110, 101, 10};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {-1,-1,-1,-1}, r.getFileID());
        assertEquals(56, r.getFileSize());
        assertEquals("one", r.getFileName());
    }

    @Test
    public void testearlynewline() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(30, r.getFileSize());
        assertEquals("foon", r.getFileName());
    }
    @Test
    @Ignore
    public void testtoStringWithEnd() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("", r.toString());
    }
    @Test
    public void testAllZeroToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '1', '\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result: FileID=00000000 FileSize=0 bytes FileName=1", r.toString());
    }
    @Test
    public void testUnsignedCapabilitiesToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {30, 30, 30, 30, (byte) 0b11111111, (byte) 0b11111111 , (byte) 0b11111111, (byte) 0b11111111, 'm', 'a','x','\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result: FileID=1E1E1E1E FileSize=4294967295 bytes FileName=max", r.toString());
    }
    @Test
    public void testUnsignedCapaStraight() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {30, 30, 30, 30, (byte) 0b11111111, (byte) 0b11111111 , (byte) 0b11111111, (byte) 0b11111111, 'm', 'a','x','\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {30,30,30,30}, r.getFileID());
        assertEquals(4294967295L, r.getFileSize());
        assertEquals("max", r.getFileName());
    }
    @Test
    public void testAllZeroStraight() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 'z', '\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {0,0,0,0}, r.getFileID());
        assertEquals(0, r.getFileSize());
        assertEquals("z", r.getFileName());
    }
    @Test
    @Ignore
    public void testDirect() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        Result r = new Result(new byte[] {1,2,3,4}, 123509, "sir");
        assertEquals("Result{fileID=1234, fileSize=123509, fileName=sir}", r.toString());
    }
    @Test(expected = BadAttributeValueException.class)
    public void testNullParam() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        //should throw BadAttributeValueException
        Result r = new Result(null, 123509, "sir");
        assertEquals("Result{fileID=1234, fileSize=123509, fileName=sir}", r.toString());
    }
    @Test(expected = IOException.class)
    public void testNullIn() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        //should throw IOException
        Result r = new Result(null);
        assertEquals("Result{fileID=0, fileSize=0, fileName='\0'}", r.toString());
    }
    @Test
    public void testEncodeWithLong() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageOutput out = new MessageOutput(outputStream);
        r.encode(out);
        byte[] bytes = outputStream.toByteArray();
        assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n'}, bytes);
    }
    @Test
    @Ignore
    public void testReadAllBytes() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        MessageInput m = new MessageInput(new ByteArrayInputStream(result));
        //byte[] bytes = m.readAllBytes();
        //shouldn't have any issue, the sanitization is done in Result
        //assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'}, bytes);
    }
    @Test(expected = NullPointerException.class)
    @Ignore
    public void testResultInNull() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        MessageInput m = new MessageInput(null);
        //byte[] bytes = m.readAllBytes();
        //shouldn't have any issue, the sanitization is done in Result
        //assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'}, bytes);
    }
    @Ignore
    @Test(expected = IOException.class)
    public void testReadAllBytesFailure() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = null;
        MessageInput m = new MessageInput(new ByteArrayInputStream(result));
        //byte[] bytes = m.readAllBytes();
        //is there even a way to pass this in my own control????
        //shouldn't have any issue, the sanitization is done in Result
        //assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'}, bytes);
    }
    @Test
    public void testParameterizedConstructor() throws NullPointerException, IOException, BadAttributeValueException {
        Result r = new Result(new byte[] {1,2,3,4}, 123456, "sir.txt");
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(123456, r.getFileSize());
        assertEquals("sir.txt", r.getFileName());
    }
    @Test(expected = BadAttributeValueException.class)
    public void testParameterizedConstructorAndInvalidInput() throws NullPointerException, IOException, BadAttributeValueException {
        Result r = new Result(new byte[] {1,2,3,4}, 123456, "s;ir");
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(123456, r.getFileSize());
        assertEquals("sir.txt", r.getFileName());
    }
    @Test(expected = IOException.class)
    public void testDoublePrematureEOS() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', -1, -1, '\n', -1, -1, -1, -1};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
    }
}