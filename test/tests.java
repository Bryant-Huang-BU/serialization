/************************************************
*
* Author: Bryant Huang
* Assignment: Program 0
* Class: CSI4321
* DISCLAIMER: ALL JAR FILES ARE JUST FOR SMOOTH TESTING
************************************************/

package serialization.test;
import serialization.*;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class tests {
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
    public void testearlynewline() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(30, r.getFileSize());
        assertEquals("foon", r.getFileName());
    }
    @Test
    public void testtoStringWithEnd() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result{fileID=1234, fileSize=30, fileName=foon}", r.toString());
    }
    @Test
    public void testAllZeroToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '1', '\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result{fileID=0000, fileSize=0, fileName=1}", r.toString());
    }
    @Test
    public void testUnsignedCapabilitiesToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {30, 30, 30, 30, (byte) 0b11111111, (byte) 0b11111111 , (byte) 0b11111111, (byte) 0b11111111, 'm', 'a','x','\n'};
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result{fileID=30303030, fileSize=4294967295, fileName=max}", r.toString());
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
    public void testReadAllBytes() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        MessageInput m = new MessageInput(new ByteArrayInputStream(result));
        byte[] bytes = m.readAllBytes();
        //shouldn't have any issue, the sanitization is done in Result
        assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'}, bytes);
    }
    @Test(expected = NullPointerException.class)
    public void testResultInNull() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        MessageInput m = new MessageInput(null);
        byte[] bytes = m.readAllBytes();
        //shouldn't have any issue, the sanitization is done in Result
        assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'}, bytes);
    }
    @Ignore
    @Test(expected = IOException.class)
    public void testReadAllBytesFailure() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = null;
        MessageInput m = new MessageInput(new ByteArrayInputStream(result));
        byte[] bytes = m.readAllBytes();
        //is there even a way to pass this in my own control????
        //shouldn't have any issue, the sanitization is done in Result
        assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'}, bytes);
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
}