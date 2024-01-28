package serialization;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.*;
import serialization.*;
import java.io.*;
import org.junit.*;


public class tests {
    @Test
    void testResultMessageInput() throws NullPointerException, IOException, BadAttributeValueException {
        byte[] result = new byte[] {1, 2, 3, 4, 0, 0, 0, 30, 'f', 'o', 'o', '\n'};
        var r = new Result(new MessageInput(new ByteArrayInputStream(result)));
        assertArrayEquals(new byte[] {1,2,3,4}, r.getFileID());
        assertEquals(30, r.getFileSize());
        assertEquals("foo", r.getFileName());
  }
}