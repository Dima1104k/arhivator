package strategy;

import model.Archive;
import model.ArchiveFormat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TarStrategy implements IArchiverStrategy {
    @Override
    public Archive create(String archivePath, List<String> files) {
        System.out.println("Створення TAR.GZ архіву: " + archivePath);
        Archive archive = new Archive(archivePath, ArchiveFormat.TAR_GZ);
        long totalSize = 0;
        
        return null;
    }

    @Override
    public Archive open(String archivePath) throws IOException {
        return null;
    }

    @Override
    public void extract(Archive archive, List<String> items, String destinationFolder) {

    }

    @Override
    public void add(Archive archive, List<String> files) {

    }

    @Override
    public void delete(Archive archive, List<String> itemPaths) {

    }

    @Override
    public boolean test(Archive archive) {
        return false;
    }

    @Override
    public Map<String, String> checksum(Archive archive, List<String> itemPaths, String algorithm) {
        return null;
    }

    @Override
    public List<String> split(Archive archive, int partSizeMB) {
        return null;
    }

    @Override
    public String join(String firstPartPath) {
        return null;
    }
}
