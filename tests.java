package serialization;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import serialization.*;
import java.io.*;
import org.junit.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;


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
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(30, r.getFileSize());
        assertEquals("foon", r.getFileName());
    }
    @Test
    public void testtoStringWithEnd() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result{fileID=1234, fileSize=30, fileName=foon}", r.toString());
    }
    @Test
    public void testAllZeroToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result{fileID=0000, fileSize=0, fileName=\0}", r.toString());
    }
    @Test
    public void testUnsignedCapabilitiesToString() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {30, 30, 30, 30, (byte) 0b11111111, (byte) 0b11111111 , (byte) 0b11111111, (byte) 0b11111111, 'm', 'a','x','\n'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertEquals("Result{fileID=30303030, fileSize=4294967295, fileName=max}", r.toString());
    }
    @Test
    public void testUnsignedCapaStraight() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {30, 30, 30, 30, (byte) 0b11111111, (byte) 0b11111111 , (byte) 0b11111111, (byte) 0b11111111, 'm', 'a','x','\n'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {30,30,30,30}, r.getFileID());
        assertEquals(4294967295L, r.getFileSize());
        assertEquals("max", r.getFileName());
    }
    @Test
    public void testAllZeroStraight() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 'z', '\n'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {0,0,0,0}, r.getFileID());
        assertEquals(0, r.getFileSize());
        assertEquals("z", r.getFileName());
    }
    @Test
    public void testDirect() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        //should throw IOException
        Result r = new Result(new byte[] {1,2,3,4}, 123509, "sir");
        assertEquals("Result{fileID=1234, fileSize=123509, fileName=sir}", r.toString());
    }
    @Test(expected = BadAttributeValueException.class)
    public void testNullParam() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        //should throw IOException
        Result r = new Result(null, 123509, "sir");
        assertEquals("Result{fileID=1234, fileSize=123509, fileName=sir}", r.toString());
    }
    @Test(expected = NullPointerException.class)
    public void testNullIn() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        //should throw IOException
        Result r = new Result(null);
        assertEquals("Result{fileID=0, fileSize=0, fileName='\0'}", r.toString());
    }
    /*@Test
    public void testEncode() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, '\0', '\n'};
        //should throw IOException
        Result r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        Result out = new MessageOutput(new ByteArrayOutputStream());
        byte[] bytes = r.encode(out);
        assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', 'n', '\n', 'w', 'h', 'e'}, );
    }*/
    @Test
    public void testEncode() throws NullPointerException, IOException, BadAttributeValueException {
        Result r = new Result(new byte[] {1,2,3,4}, 123509, "sir");
        byte[] bytes;
        MessageOutput out = new MessageOutput(new ByteArrayOutputStream());
        r.encode(out);
        out.toByteArray();
        assertArrayEquals(new byte[] {1, 2, 3, 4, 0, 0, 1, -91, 0, 0, 0, 4, 115, 105, 114}, bytes);
    }
}