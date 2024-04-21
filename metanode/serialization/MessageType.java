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
    public static MessageType getByCmd(String cmd) {
        switch (cmd) {
            case "RN":
                return RequestNodes;
            case "RM":
                return RequestMetaNodes;
            case "AR":
                return AnswerRequest;
            case "NA":
                return NodeAdditions;
            case "MA":
                return MetaNodeAdditions;
            case "ND":
                return NodeDeletions;
            case "MD":
                return MetaNodeDeletions;
            default:
                return null;
        }
    }
    /**
     * Returns the MessageType corresponding to the given code.
     *
     * @param code the code representing the MessageType
     * @return the MessageType corresponding to the code
     * @throws IllegalArgumentException if the code is invalid
     */
    public static MessageType getByCode(int code) {
        switch (code) {
            case 0:
                return RequestNodes;
            case 1:
                return RequestMetaNodes;
            case 2:
                return AnswerRequest;
            case 3:
                return NodeAdditions;
            case 4:
                return MetaNodeAdditions;
            case 5:
                return NodeDeletions;
            case 6:
                return MetaNodeDeletions;
            default:
                return null;
        }
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
    public String getCmd() {
        return this.cmd;
    }
}
