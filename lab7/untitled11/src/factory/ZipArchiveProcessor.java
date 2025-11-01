package factory;

import strategy.IArchiverStrategy;
import strategy.ZipStrategy;

public class ZipArchiveProcessor extends ArchiveProcessor {
    @Override
    protected IArchiverStrategy createStrategy() {
        System.out.println("Factory Method: створює ZipStrategy");
        return new ZipStrategy();
    }

}
