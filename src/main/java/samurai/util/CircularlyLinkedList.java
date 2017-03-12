package samurai.util;

/**
 * @author TonTL
 * @version 4.x - 3/11/2017
 */
public class CircularlyLinkedList<T> {

    Node<T> current;

    public CircularlyLinkedList() {
        current = null;
    }

    public boolean isEmpty() {
        return current == null;
    }

    public T getNext() {
        if (current == null) return null;
        current = current.next;
        return current.object;
    }

    public T current() {
        return current.object;
    }

    public void advance() {
        if (current == null) return;
        current = current.next;
    }

    public void insert(T object) {
        if (current == null) {
            current = new Node<>(object, null);
            current.next = current;
            return;
        }
        Node<T> node = new Node<>(object, current.next);
        current.next = node;
        current = node;
    }

    private class Node<t> {
        t object;
        Node<t> next;

        Node(t object, Node<t> next) {
            this.object = object;
            this.next = next;
        }
    }
}
