package model;

public enum ArchiveFormat {
    ZIP, TAR_GZ, RAR, SevenZ;
    public static ArchiveFormat fromPath(String path) {
        String p = path.toLowerCase();
        if (p.endsWith(".zip")) return ZIP;
        if (p.endsWith(".tar.gz") || p.endsWith(".tgz")) return TAR_GZ;
        if (p.endsWith(".rar")) return RAR;
        if (p.endsWith(".7z")) return SevenZ;
        throw new IllegalArgumentException("Невідомий формат для файлу: " + path);
    }
}
