/************************************************
* Author: Bryant Huang
* Assignment: Program 4
* Class: CSI4321
************************************************/
package metanode.serialization;
/**
 * Represents the type of a message in the system.
 */
public enum MessageType {
    RequestNodes(0,"RN"),
    RequestMetaNodes(1,"RM"),
    AnswerRequest(2,"AR"),
    NodeAdditions(3,"NA"),
    MetaNodeAdditions(4,"MA"),
    NodeDeletions(5,"ND"),
    MetaNodeDeletions(6,"MD");
    private final int code;
    private final String cmd;

    MessageType(int code, String cmd) {
        this.code = code;
        this.cmd = cmd;
    }


    /**
     * Retrieves the InnerMessageType enum value
     * based on the given command string.
     *
     * @param s The command string
     * representing the message type.
     * @return The InnerMessageType enum
     * value corresponding to the command string.
     * @throws IllegalArgumentException If
     * the command string is invalid.
     */
    public static MessageType getByCmd(String cmd) throws IllegalArgumentException {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getCmd().equals(cmd)) {
                return messageType;
            }
        }
        return null;
        //throw new IllegalArgumentException("Invalid command");
    }
    /**
     * Returns the MessageType corresponding to the given code.
     *
     * @param code the code representing the MessageType
     * @return the MessageType corresponding to the code
     * @throws IllegalArgumentException if the code is invalid
     */
    public static MessageType getByCode(int code) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getCode() == code) {
                return messageType;
            }
        }
        return null;
        //throw new IllegalArgumentException("Invalid message type");
    }

    /**
     * Returns the code associated with the message type.
     *
     * @return the code associated with the message type
     * @throws IllegalArgumentException if the message type is invalid
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Returns the command associated with the message type.
     *
     * @return the command as a string
     * @throws IllegalArgumentException if the message type is invalid
     */
    public String getCmd() throws IllegalArgumentException {
        if (this.cmd == null) {
            throw new IllegalArgumentException("Invalid message type");
        }
        return this.cmd;
    }
}
