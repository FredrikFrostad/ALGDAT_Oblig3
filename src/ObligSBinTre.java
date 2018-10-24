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

        Node<T> node = rot, forelder = null;
        int c = 0;

        // Flytter node nedover i treet til den kommer "faller ut".
        // Dersom verdien som skal legges inn er større enn gjeldende node flyttes node til venstre barn,
        // ellers flyttes node til høyre barn. Dette gjøres til node er null. Da vil forelder holde på node
        //sin siste posisjon. Parent vil dermed bli node sin forelder.
        while (node != null) {
            forelder = node;
            c = comp.compare(verdi, node.verdi);
            node = c < 0 ? node.venstre : node.høyre;
        }

        //lager en ny node med parameter som verdi og forelder som forelder
        node = new Node<>(verdi, forelder);

        if (forelder == null) rot = node;             // treet er tomt. Node blir rot.
        else if (c < 0) forelder.venstre = node;      // node blir venstre barn
        else forelder.høyre = node;                   // node blir høyre barn
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


    //@Override
    public boolean fjern(T verdi) {
        if (verdi == null) return false;  // treet har ingen nullverdier

        Node<T> p = rot, q = null;   // q skal være forelder til p

        while (p != null)            // leter etter verdi
        {
            int cmp = comp.compare(verdi,p.verdi);      // sammenligner
            if (cmp < 0) { q = p; p = p.venstre; }      // går til venstre
            else if (cmp > 0) { q = p; p = p.høyre; }   // går til høyre
            else break;    // den søkte verdien ligger i p. verdi == p.verdi.
        }
        if (p == null) return false;   // finner ikke verdi

        //Jeg la til dette
        if(p.venstre == null && p.høyre == null && q!=null){

            //Kan denne koden optimaliseres?
            if(q.høyre==p){
                q.høyre=null;
            }else{
                q.venstre = null;
            }
        }
        //Jeg flyttet denne til en else if. Måtte håndtere om p.venstre == null og p.høyre==null
        else if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
        {
            Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn
            if (p == rot) rot = b;
            else if (p == q.venstre) q.venstre = b;
            else {
                q.høyre = b;

                //Jeg la til denne if setningen og at b.forelder = q;
                if(q!= null)
                    b.forelder = q; //Linje lagt til for å kunne oppdatere forelder hvis man fjærner siste i in-orden
            }
        }
        else  // Tilfelle 3)
        {
            Node<T> s = p, r = p.høyre;   // finner neste i inorden

            while (r.venstre != null)
            {
                s = r;    // s er forelder til r
                r = r.venstre;
            }

            p.verdi = r.verdi;   // kopierer verdien i r til p

            if (s != p) s.venstre = r.høyre;
            else s.høyre = r.høyre;
        }

        antall--;   // det er nå én node mindre i treet
        return true;

    }

    private <T> Node<T> fjernVerdiRekursivt(Node<T> node, T verdi, ObligSBinTre<T> tre) {

        Node<T> p = node;

        int cmp = tre.comp.compare(p.verdi, verdi);
        if (cmp > 0) {
            p.venstre = fjernVerdiRekursivt(p.venstre, verdi, tre);
        }

        else if (cmp < 0) {
            p.høyre = fjernVerdiRekursivt(p.høyre, verdi, tre);
        }

        else {

            if (p.venstre == null && p.høyre == null) {
                p = null;
            }

            else if (p.høyre == null) {
                p = p.venstre;
            }

            else if (p.venstre == null) {
                p = p.høyre;
            }

            else {
                //TODO: bytt ut denne metoden hvis det ikke virker
                Node<T> temp = nesteInorden(p.høyre);
                p.verdi = temp.verdi;
                p.høyre = fjernVerdiRekursivt(p.høyre, temp.verdi, tre);
            }

        }

        return p;
    }

    //TODO: fjern denne hvis den ikke blir brukt
    private void fjernBladnode(Node<T> node) {
        Node<T> forelder = node.forelder;

        node.verdi = null;
        node.forelder = null;
        node = null;

        if (forelder.venstre.equals(node)) forelder.venstre = null;
        else forelder.høyre = null;



    }

    private void fjernNodeEttBarn(Node<T> node) {

        if (node.venstre != null) {
            node = node.venstre;
        }
        else node = node.høyre;
    }

    private void fjernNodeToBarn(Node<T> node) {

    }

    public int fjernAlle(T verdi) {

        if (verdi == null) return 0;

        int antallFjernet = 0;

        while (inneholder(verdi)) {
            fjern(verdi);
        }

        return antallFjernet;
    }

    @Override
    public int antall()
    {
        return antall;
    }

    public int antall(T verdi)
    {
        int antall = 0;
        java.util.Deque<Node<T>> stack = new java.util.ArrayDeque<>(this.antall);
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


        if (p.høyre != null) {
            p = p.høyre;

            while (p.venstre != null) {
                p = p.venstre;
            }
            return p;
        }

        Node<T> q = p.forelder;

        while (q != null && p == q.høyre) {
            p = q;
            q = q.forelder;
        }
        return q;
    }


    @Override
    public String toString() {
        Node<T> node = rot;
        if (node == null) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        while (node.venstre != null) node = node.venstre;
        sb.append(node.verdi);

        while (node != null) {
            node = nesteInorden(node);
            if (node != null) sb.append(", ").append(node.verdi);
        }

        sb.append("]");

        return sb.toString();
    }

    public String omvendtString()
    {
        Node<T> node = rot;
        if (node ==null) return "[]";

        Deque<T> stack = new ArrayDeque<>();

        while (node.venstre != null) node = node.venstre;
        stack.add(node.verdi);

        while (node != null) {
            node = nesteInorden(node);
            if (node != null) stack.add(node.verdi);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(stack.removeLast());

        while (!stack.isEmpty()) { sb.append(", ").append(stack.removeLast());
        }

        sb.append("]");
        return sb.toString();
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
        int[] a = {4, 7, 2, 9, 4, 10, 8, 7, 4, 6, 1};
        for (int verdi : a) tre.leggInn(verdi);

        Node<Integer> node = tre.rot;

        System.out.println(tre);
        tre.fjernAlle(4);
        System.out.println(tre);
        System.out.println(tre.omvendtString());
    }

    static <T> Node<T> endreNode(Node<T> node) {return node.venstre;}
} // ObligSBinTre

//************************ GJEMMESTED FOR GULL OG TRYLLETRIXX ************************************

/*

    private static <T> Node<T> nesteInorden(Node<T> p)  {

        Node<T> gjeldende = p, forelder, treff = null;

        while (gjeldende.forelder != null) gjeldende = gjeldende.forelder;
        while (gjeldende != null) {

            //Navigerer helt til høyre i gjeldende nodes venstre subtre og setter høyrepeker til gjeldende
            if (gjeldende.venstre == null) {
                //Inorder print eller sjekki
                System.out.println(gjeldende.verdi);
                gjeldende = gjeldende.høyre;

            }else
                {
                forelder = gjeldende.venstre;

                while (forelder.høyre != null && forelder.høyre != gjeldende) forelder = forelder.høyre;

                if (forelder.høyre == null) {
                    forelder.høyre = gjeldende;
                    gjeldende = gjeldende.venstre;
                }else {
                    forelder.høyre = null;
                    //Inorder print eller sjekk
                    System.out.println(gjeldende.verdi);
                    gjeldende = gjeldende.høyre;
                }
            }
        }
        return null;
    }
*/