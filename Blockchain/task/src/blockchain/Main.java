package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
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

    public int getGeneratingTimeInSeconds() {
        return generatingTimeInSeconds;
    }

    public void setGeneratingTimeInSeconds(int generatingTimeInSeconds) {
        this.generatingTimeInSeconds = generatingTimeInSeconds;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block:\n");
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
        sb.append("lock was generating for ");
        sb.append(generatingTimeInSeconds);
        sb.append(" seconds");
        sb.append("\n");

        return sb.toString();
    }
}

class Blockchain {
    private List<Block> blockchain = new ArrayList<>();
    private int zerosCountInStart;

    public Blockchain(int zerosCountInStart) {
        this.zerosCountInStart = zerosCountInStart;
    }

    public Block getFirstBlock() {
        return blockchain.get(0);
    }

    public List<Block> getBlockchain() {
        return new ArrayList<>(blockchain);
    }

    public Block addBlock() {
        long startTime = System.currentTimeMillis();
        String prevHash = "0";
        if (!blockchain.isEmpty()) {
            prevHash = StringUtil.applySha256(blockchain.get(blockchain.size() - 1));
        }
        String zerosStandard = new String(new char[zerosCountInStart]).replaceAll("\0", "0");

        String zerosPart;
        int magicNumber;
        Block block = new Block((long) blockchain.size(), new Date().getTime(), prevHash);
        do {
            magicNumber = ThreadLocalRandom.current().nextInt();
            block.setMagicNumber(magicNumber);
            zerosPart = StringUtil.applySha256(block).substring(0, zerosCountInStart);
        } while (!zerosStandard.equals(zerosPart));
        long endTime = System.currentTimeMillis();
        int duration = (int) ((endTime - startTime) / 1000);
        block.setGeneratingTimeInSeconds(duration);
        blockchain.add(block);
        return block;
    }

    public boolean isBlockchainValid() {
        if (blockchain.isEmpty()) {
            return true;
        }
        if (!blockchain.get(0).getPreviousBlockHash().equals("0")) {
            return false;
        }
        if (blockchain.size() == 1) {
            return true;
        }
        for (int i = 0; i < blockchain.size() - 1; i++) {
            if (!StringUtil.applySha256(blockchain.get(i)).equals(blockchain.get(i + 1).getPreviousBlockHash())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Blockchain{" +
                "blockchain=" + blockchain +
                '}';
    }
}

class StringUtil {
    /* Applies Sha256 to a string and returns a hash. */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
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
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(block.getId());
            sb.append(block.getTimestamp());
            sb.append(block.getPreviousBlockHash());
            sb.append(block.getMagicNumber());
            String input = sb.toString();
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
}

public class Main {
    public static void main(String[] args) {

        System.out.println("Enter how many zeros the hash must start with: ");
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        Blockchain blockchain = new Blockchain(n);
        for (int i = 0; i < 5; i++) {
            System.out.println(blockchain.addBlock());
        }
    }
}

