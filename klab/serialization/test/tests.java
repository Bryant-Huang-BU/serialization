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
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

public class tests {
    @Test
    public void testValidMessage() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0 };
        Message r = Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()));
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
    public void testValidResponse() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] enc = new byte[] { 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 1, 0, 23, (byte) 192, (byte) 168, 1, 5, 1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'};
        Response r = (Response) Message.decode(new MessageInput(new ByteArrayInputStream(enc)));
        assertAll(() -> assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, r.getID()),
                () -> assertEquals(3, r.getTTL()), () -> assertEquals(RoutingService.BREADTHFIRST, r.getRoutingService()), () -> assertEquals( 1, r.getMatches()),
                () -> assertEquals("192.168.1.5", r.getResponseHost().getAddress().getHostAddress()), () -> assertEquals( 23, r.getResponseHost().getPort()), () -> assertEquals(new Result(new MessageInput(new ByteArrayInputStream(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'}))), r.getResultList().get(0)));
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
    public void donahootestencode() throws NullPointerException, IOException, BadAttributeValueException {
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
        assertEquals("Result: FileID=00000000FileSize=0bytes FileName=1", r.toString());
    }
    @Test
    public void testUnsignedCapabilitiesToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {30, 30, 30, 30, (byte) 0b11111111, (byte) 0b11111111 , (byte) 0b11111111, (byte) 0b11111111, 'm', 'a','x','\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result: FileID=1E1E1E1EFileSize=4294967295bytes FileName=max", r.toString());
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