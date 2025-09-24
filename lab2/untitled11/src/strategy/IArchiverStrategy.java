package strategy;

import model.Archive;
import java.util.List;
import java.util.Map;

public interface IArchiverStrategy {
    Archive create(String archivePath, List<String> files);
    Archive open(String archivePath);
    void extract(Archive archive, List<String> items, String destinationFolder);
    void add(Archive archive, List<String> files);
    void delete(Archive archive, List<String> itemPaths);
    boolean test(Archive archive);
    Map<String, String> checksum(Archive archive, List<String> itemPaths, String algorithm);
    List<String> split(Archive archive, int partSizeMB);
    String join(String firstPartPath);
}