import java.util.*;
public class ObligSBinTre<T> implements Beholder<T> {

    /**
     * En indre nodeklasse
     * @param <T>
     */
    private static final class Node<T>
    {
        private T verdi; // nodens verdi
        private Node<T> venstre, høyre; // venstre og høyre barn
        private Node<T> forelder; // forelder

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
        {
            this.verdi = verdi;
            venstre = v; høyre = h;
            this.forelder = forelder;
        }
        private Node(T verdi, Node<T> forelder) // konstruktør
        {
            this(verdi, null, null, forelder);
        }
        @Override
        public String toString(){ return "" + verdi;}
    } // class Node

    /**
     * Instansvariabler for klasse beholder
     */
    private Node<T> rot; // peker til rotnoden
    private int antall; // antall noder
    private int endringer; // antall endringer
    private final Comparator<? super T> comp; // komparator

    // konstruktør
    public ObligSBinTre(Comparator<? super T> c) {

        rot = null;
        antall = 0;
        comp = c;
    }

    @Override
    public boolean leggInn(T verdi)
    {
        Objects.requireNonNull("Nullverdier er ikke tillat!");

        Node<T> node = rot, parent = null;
        int c = 0;

        // Flytter node nedover i treet til den kommer "faller ut".
        // Dersom verdien som skal legges inn er større enn gjeldende node flyttes node til venstre barn,
        // ellers flyttes node til høyre barn. Dette gjøres til node er null. Da vil parent holde på node
        //sin siste posisjon. Parent vil dermed bli node sin forelder.
        while (node != null) {
            parent = node;
            c = comp.compare(verdi, node.verdi);
            node = c < 0 ? node.venstre : node.høyre;
        }

        //lager en ny node med parameter som verdi og parent som forelder
        node = new Node<>(verdi, parent);

        if (parent == null) rot = node;             // treet er tomt. Node blir rot.
        else if (c < 0) parent.venstre = node;      // node blir venstre barn
        else parent.høyre = node;                   // node blir høyre barn
        antall++;                                   //øker antall med 1;

        return true;
    }

    @Override
    public boolean inneholder(T verdi)
    {
        if (verdi == null) return false;

        Node<T> p = rot;

        while (p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);

            if (cmp < 0) p = p.venstre;
            else if (cmp > 0) p = p.høyre;
            else return true;
        }
        return false;
    }

    @Override
    public boolean fjern(T verdi)
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public int fjernAlle(T verdi)
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    @Override
    public int antall()
    {
        return antall;
    }

    public int antall(T verdi)
    {
        int antall = 0;
        java.util.Deque<Node<T>> stack = new ArrayDeque<>(this.antall);
        stack.addFirst(rot);

        while (!stack.isEmpty()) {

            Node<T> node = stack.removeFirst();
            if (node.verdi.equals(verdi)) antall++;

            if (node.venstre != null) {
                stack.addFirst(node.venstre);
            }
            if (node.høyre != null) {
                stack.addFirst(node.høyre);
            }
        }

        return antall;
    }

    @Override
    public boolean tom()
    {
        return antall == 0;
    }

    @Override
    public void nullstill()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    private static <T> Node<T> nesteInorden(Node<T> p)  {

        Node<T> node = null;

        return null;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<T> node = nesteInorden(rot);
        sb.append(node.verdi);

        while (node != null) {
            node = nesteInorden(node);
            if (node != null) sb.append(",").append(node.verdi);
        }

        sb.append("]");

        return sb.toString();
    }

    public String omvendtString()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String høyreGren()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String lengstGren()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String[] grener()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String bladnodeverdier()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String postString()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }


    @Override
    public Iterator<T> iterator()
    {
        return new BladnodeIterator();
    }

    private class BladnodeIterator implements Iterator<T>
    {
        private Node<T> p = rot, q = null;
        private boolean removeOK = false;
        private int iteratorendringer = endringer;

        private BladnodeIterator() // konstruktør
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public boolean hasNext()
        {
            return p != null; // Denne skal ikke endres!
        }

        @Override
        public T next()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }
    } // BladnodeIterator

    public static void main(String[] args) {
        ObligSBinTre tre = new ObligSBinTre<>(Comparator.naturalOrder());
        int[] a = {4,7,2,9,4,10,8,7,4,6,1};
        for (int verdi : a) tre.leggInn(verdi);

        //nesteInorden(tre.rot);
        System.out.println(tre);
    }
} // ObligSBinTre