package klab.serialization;

import java.io.IOException;

public class Message {
        private int type;
        private byte[] msgID;
        private int ttl;
        private RoutingService routingService;

        public Message (byte[] msgID, int ttl, RoutingService routingService) throws BadAttributeValueException {
            setMsgID(msgID);
            setTtl(ttl);
            setRoutingService(routingService);
        }

        public void encode(MessageOutput out)
            throws IOException {
            out.writeBytes(this.msgID);
            out.writeBytes(intToBytes(this.ttl));
            out.writeBytes(intToBytes(this.routingService.getCode()));
        }

        public byte[] intToBytes(int x) {
            byte[] bytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                bytes[i] = (byte)(x >>> (i * 8));
            }
            return bytes;
        }

        public byte[] getMsgID() {
            return msgID;
        }

        public Message setMsgID(byte[] msgID) throws BadAttributeValueException {
            if (msgID.length != 15) {
                throw new BadAttributeValueException("msgID is not 15 bytes", "msgID");
            }
            this.msgID = msgID;
            return this;
        }

        public int getTtl() {
            return ttl;
        }

        public Message setTtl(int ttl) throws BadAttributeValueException {
            if (ttl < 0) {
                throw new BadAttributeValueException("ttl is less than 0", "ttl");
            }
            this.ttl = ttl;
            return this;
        }

        public RoutingService getRoutingService() {
            return routingService;
        }

        public Message setRoutingService(RoutingService routingService) {
            this.routingService = routingService;
            return this;
        }

        public String toString() {
            return "Message: ID=" + msgID + " TTL=" + ttl + " Routing=" + routingService.getCode();
        }

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }
}