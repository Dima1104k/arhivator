package iterator;

import model.ArchiveItem;
import model.ArchivedFolder;
import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstIterator implements ArchiveIterator {
    private Queue<ArchiveItem> queue;

    public BreadthFirstIterator(ArchiveItem root) {
        this.queue = new LinkedList<>();
        if(root != null) this.queue.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public ArchiveItem next() {
        ArchiveItem current = queue.poll();
        if(current instanceof ArchivedFolder){
            ArchivedFolder folder = (ArchivedFolder) current;
            for(ArchiveItem child : folder.getChildren()){
                queue.add(child);
            }
        }
        return current;
    }
}
