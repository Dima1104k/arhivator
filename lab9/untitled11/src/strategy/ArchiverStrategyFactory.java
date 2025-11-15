package strategy;

import model.ArchiveFormat;

public class ArchiverStrategyFactory {

    private ArchiverStrategyFactory() {


    }

    public static IArchiverStrategy createStrategy(ArchiveFormat format) {
        switch (format) {
            case ZIP:
                return new ZipStrategy();
            case TAR_GZ:
                return new TarStrategy();

            default:
                throw new UnsupportedOperationException("Формат ще не підтримується: " + format);
        }
    }

    public static IArchiverStrategy createForPath(String archivePath) {
        ArchiveFormat format = ArchiveFormat.fromPath(archivePath);
        return createStrategy(format);
    }
}