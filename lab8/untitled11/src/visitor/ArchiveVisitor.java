package visitor;

import model.ArchivedFile;
import model.ArchivedFolder;

public interface ArchiveVisitor {
    void visit(ArchivedFile file);
    void visit(ArchivedFolder folder);
}
