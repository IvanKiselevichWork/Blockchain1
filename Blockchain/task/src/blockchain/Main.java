package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class Block {
    private final Long id;
    private final Long timestamp;
    private final String previousBlockHash;
    private int magicNumber;
    private int generatingTimeInSeconds;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block:\n");
        sb.append("Created by miner # " + ThreadLocalRandom.current().nextInt(1,10));
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
        sb.append("block was generating for ");
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
