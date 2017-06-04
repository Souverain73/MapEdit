package helpers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.Locale;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Created by Souverain73 on 30.05.2017.
 */
public class XML {
    private static class NodeIterator implements Iterator<Node>{
        NodeList list;
        int item;

        public NodeIterator(NodeList list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return item < list.getLength();
        }

        @Override
        public Node next() {
            return list.item(item++);
        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer<? super Node> action) {

        }
    }
    public static class IterableNodeList implements Iterable<Node>{
        NodeList list;

        public IterableNodeList(NodeList list) {
            this.list = list;
        }

        @Override
        public Iterator<Node> iterator() {
            return new NodeIterator(list);
        }

        @Override
        public void forEach(Consumer<? super Node> action) {
            for (int i = 0; i < list.getLength(); i++) {
                 action.accept(list.item(i));
            }
        }

        @Override
        public Spliterator<Node> spliterator() {
            return null;
        }
    }

    public static String tag(String name, String ... strings){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++)
            sb.append(strings[i]);

        return String.format("<%s>%s</%s>\n", name, sb.toString(), name);
    }

    public static String xmlHeader(){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    }

    public static String coords(double x, double y){
        return String.format(Locale.ROOT, "%f, %f, %d", x, y, 0);
    }

    public static String coordsTag(double x, double y){
        return tag("coordinates", coords(x, y));
    }
}
