package blockchain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class GenerateKeys {

    private final KeyPairGenerator keyGen;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public GenerateKeys(int keylength) throws NoSuchAlgorithmException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keylength);
    }

    public void createKeys() {
        KeyPair pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
}

class MessageUtil {

    private static long id = 0;

    public static String getId() {
        return String.valueOf(id++);
    }

    public static Message generateMessage(String data) {
        try {
            GenerateKeys generateKeys = new GenerateKeys(1024);
            generateKeys.createKeys();
            String identifier = getId();
            PublicKey publicKey = generateKeys.getPublicKey();
            PrivateKey privateKey = generateKeys.getPrivateKey();
            String sign = getMessageSign(data, identifier, privateKey);
            return new Message(data, sign, identifier, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMessageSign(String data, String identifier, PrivateKey privateKey) {
        try {
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initSign(privateKey);
            rsa.update((data + identifier).getBytes());
            return new String(rsa.sign());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isMessageValid(Message message) {
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(message.getPublicKey());
            sig.update((message.getData() + message.getIdentifier()).getBytes());
            return sig.verify(message.getSign().getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }
}

class Message {
    private final String data;
    private final String sign;
    private final String identifier;
    private final PublicKey publicKey;

    public Message(String data, String sign, String identifier, PublicKey publicKey) {
        this.data = data;
        this.sign = sign;
        this.identifier = identifier;
        this.publicKey = publicKey;
    }

    public String getData() {
        return data;
    }

    public String getSign() {
        return sign;
    }

    public String getIdentifier() {
        return identifier;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data='" + data + '\'' +
                ", sign='" + sign + '\'' +
                ", identifier='" + identifier + '\'' +
                ", publicKey=" + publicKey +
                '}';
    }
}

class Block {
    private final Long id;
    private final Long timestamp;
    private final String previousBlockHash;
    private int magicNumber;
    private int generatingTimeInSeconds;
    private List<Message> messages;

    public Block(Long id, Long timestamp, String previousBlockHash) {
        this.id = id;
        this.timestamp = timestamp;
        this.previousBlockHash = previousBlockHash;
    }

    public Long getId() {
        return id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public void setGeneratingTimeInSeconds(int generatingTimeInSeconds) {
        this.generatingTimeInSeconds = generatingTimeInSeconds;
    }

    public int getGeneratingTimeInSeconds() {
        return generatingTimeInSeconds;
    }

    public List<Message> getMessages() {
        if (messages == null) {
            return null;
        } else {
            return List.copyOf(messages);
        }
    }

    public void setMessage(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block:\n");
        sb.append("Created by miner # " + ThreadLocalRandom.current().nextInt(1, 10));
        sb.append("\n");
        sb.append("Id: ");
        sb.append(id);
        sb.append("\n");
        sb.append("Timestamp: ");
        sb.append(timestamp);
        sb.append("\n");
        sb.append("Magic number: ");
        sb.append(magicNumber);
        sb.append("\n");
        sb.append("Hash of the previous block: \n");
        sb.append(previousBlockHash);
        sb.append("\n");
        sb.append("Hash of the block: \n");
        sb.append(StringUtil.applySha256(this));
        sb.append("\n");
        sb.append("Block data: \n");
        if (messages == null) {
            sb.append("no messages");
        } else {
            messages.forEach(message -> sb.append(message.getData()));
        }
        sb.append("\n");
        sb.append("Block was generating for ");
        sb.append(generatingTimeInSeconds);
        sb.append(" seconds");

        return sb.toString();
    }
}

class Blockchain {
    private static final int MIN_ZERO_COUNT = 0;
    private static final int MAX_ZERO_COUNT = 30;
    private static final int MIN_GENERATING_TIME_IN_SECONDS = 5;
    private static final int MAX_GENERATING_TIME_IN_SECONDS = 10;
    private final List<Block> blocks = new ArrayList<>();
    private int nextBlockZeroCount = 0;

    public Block getFirstBlock() {
        return blocks.get(0);
    }

    public List<Block> getBlockchain() {
        return new ArrayList<>(blocks);
    }

    public Block generateBlock(int zeroCount) {
        long startTime = System.currentTimeMillis();
        String prevHash = "0";
        if (!blocks.isEmpty()) {
            prevHash = StringUtil.applySha256(blocks.get(blocks.size() - 1));
        }
        String zerosStandard = new String(new char[zeroCount]).replaceAll("\0", "0");

        String zerosPart;
        int magicNumber;
        Block block = new Block((long) blocks.size(), new Date().getTime(), prevHash);
        do {
            magicNumber = ThreadLocalRandom.current().nextInt();
            block.setMagicNumber(magicNumber);
            zerosPart = StringUtil.applySha256(block).substring(0, zeroCount);
        } while (!zerosStandard.equals(zerosPart));
        long endTime = System.currentTimeMillis();
        int duration = (int) ((endTime - startTime) / 1000);
        block.setGeneratingTimeInSeconds(duration);
        return block;
    }

    public int addBlock(Block block) {
        //todo check if block valid
        blocks.add(block);
        if (block.getGeneratingTimeInSeconds() < MIN_GENERATING_TIME_IN_SECONDS && nextBlockZeroCount < MAX_ZERO_COUNT) {
            nextBlockZeroCount++;
        } else if (block.getGeneratingTimeInSeconds() > MAX_GENERATING_TIME_IN_SECONDS && nextBlockZeroCount > MIN_ZERO_COUNT) {
            nextBlockZeroCount--;
        }
        return nextBlockZeroCount;
    }

    public boolean isBlockchainValid() {
        if (blocks.isEmpty()) {
            return true;
        }
        if (!blocks.get(0).getPreviousBlockHash().equals("0")) {
            return false;
        }
        if (blocks.size() == 1) {
            return true;
        }
        for (int i = 0; i < blocks.size() - 1; i++) {
            if (!StringUtil.applySha256(blocks.get(i)).equals(blocks.get(i + 1).getPreviousBlockHash())) {
                return false;
            }
            List<Message> messages = blocks.get(i + 1).getMessages();
            if (messages != null) {
                for(Message message : messages) {
                    if (!MessageUtil.isMessageValid(message)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Blockchain{" +
                "blockchain=" + blocks +
                '}';
    }
}

class StringUtil {
    /* Applies Sha256 to a string and returns a hash. */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte elem : hash) {
                String hex = Integer.toHexString(0xff & elem);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    /* Applies Sha256 to a Block and returns a hash. */
    public static String applySha256(Block block) {
        StringBuilder sb = new StringBuilder();
        sb.append(block.getId());
        sb.append(block.getTimestamp());
        sb.append(block.getPreviousBlockHash());
        sb.append(block.getMagicNumber());
        if (block.getMessages() != null) {
            sb.append(block.getMessages().toString());
        }
        String input = sb.toString();
        return applySha256(input);
    }
}

public class Main {
    public static void main(String[] args) {

        Blockchain blockchain = new Blockchain();
        int zeroCount = 0;
        int previousZeroCount = 0;
        Block block;
        for (int i = 0; i < 5; i++) {
            block = blockchain.generateBlock(zeroCount);
            Message message = null;
            if (i != 0) {
                message = MessageUtil.generateMessage("message" + i);
                block.setMessage(List.of(message));
            }
            previousZeroCount = zeroCount;
            zeroCount = blockchain.addBlock(block);
            System.out.println(block);
            if (previousZeroCount == zeroCount) {
                System.out.println("N stays the same");
            } else if (zeroCount > previousZeroCount) {
                System.out.println("N was increased to " + zeroCount);
            } else {
                System.out.println("N was decreased by " + (previousZeroCount - zeroCount));
            }
            System.out.println();
        }
    }
}
