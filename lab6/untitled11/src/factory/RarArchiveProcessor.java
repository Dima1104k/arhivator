package factory;

import strategy.IArchiverStrategy;
import strategy.RarStrategy;

public class RarArchiveProcessor  extends ArchiveProcessor {
    @Override
    protected IArchiverStrategy createStrategy() {
        System.out.println("Factory Method: створює RarStrategy");
        return new RarStrategy();
    }
}
