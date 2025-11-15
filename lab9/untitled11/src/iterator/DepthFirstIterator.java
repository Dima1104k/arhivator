package iterator;

import model.ArchiveItem;
import model.ArchivedFolder;

import java.util.List;
import java.util.Stack;

public class DepthFirstIterator implements ArchiveIterator {
    private Stack<ArchiveItem> stack;

    public DepthFirstIterator(ArchiveItem root) {
        this.stack = new Stack<>();
        if( root !=null) {
            this.stack.push(root);
        }
    }
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public ArchiveItem next() {
        ArchiveItem current = stack.pop();
        if (current instanceof ArchivedFolder) {
            ArchivedFolder folder = (ArchivedFolder) current;
            List<ArchiveItem> children = folder.getChildren();
            for(int i = children.size()-1; i>=0; i--){
                stack.push(children.get(i));
            }
        }
        return current;
    }
}
