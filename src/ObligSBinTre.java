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


    @Override
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
            else if (p == q.venstre)

                q.venstre = b;
            //Jeg la til denne if setningen og at b.forelder = q;
            if(q!= null)
                b.forelder = q; //Linje lagt til for å kunne oppdatere forelder hvis man fjærner siste i in-orden
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

    //TODO: Prøv å få denne rekursive metoden for fjerning av node til å virke!!
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
                Node<T> temp = finnMinsteFraHøyre(node.høyre);
                p.verdi = temp.verdi;
                p.høyre = fjernVerdiRekursivt(p.høyre, temp.verdi, tre);
            }
        }
        return p;
    }

    private <T> Node<T> finnMinsteFraHøyre(Node<T> node) {
        while(node.venstre != null){
            node = node.venstre;
        }
        return node;
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

        if (verdi == null || rot == null) return 0;

        int antallFjernet = 0;

        while (inneholder(verdi)) {
            fjern(verdi);
            antallFjernet++;
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
        if (antall < 1) return;
            nullstillRec(rot);
            antall = 0;
            rot = null;
    }

    private void nullstillRec(Node<T> rot) {
        if (rot.venstre != null)
            nullstillRec(rot.venstre);
        if (rot.høyre!= null)
            nullstillRec(rot.høyre);

        rot.venstre = null;
        rot.høyre = null;
        rot.forelder = null;
    }



    private static <T> Node<T> nesteInorden(Node<T> p)  {

        //Dersom p har et høyre subtre, ligger neste inorden til venstre i dette subtreet
        if (p.høyre != null) {
            p = p.høyre;

            //Nederste node i venstre subtre er neste innorden
            while (p.venstre != null) {
                p = p.venstre;
            }
            return p;
        }

        //Vi er nederst i et subtre og må "klatre" opppever i treet
        Node<T> q = p.forelder;

        while (q != null && p == q.høyre) {            p = q;
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

    /**
     * Motode som skriver ut treets innhold i innorden rekkefølge reversert
     * Metoden bruker en hjelpestakk for å reversere rekkefølgen til verdiene.
     * @return en string bestående av treets verdier i omvendt innorden rekkefølge
     */
    public String omvendtString() {

        // Sjekker om treet er tomt
        Node<T> node = rot;
        if (node ==null) return "[]";

        Deque<T> stack = new ArrayDeque<>();

        //Legger verdier på stakken i innorden rekkefølge
        while (node.venstre != null) node = node.venstre;
        stack.add(node.verdi);

        while (node != null) {
            node = nesteInorden(node);
            if (node != null) stack.add(node.verdi);
        }

        //Snur rekkefølgen på verdiene ved å poppe fra toppen av stakken
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(stack.removeLast());

        while (!stack.isEmpty()) { sb.append(", ").append(stack.removeLast());
        }

        sb.append("]");
        return sb.toString();
    }

    public String høyreGren() {

        Node<T> node = rot;
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(rot.verdi);

        //Vi iterere så lenge vi ikke er kommet til en bladnode
        while (node.venstre != null || node.høyre != null) {

            //Går til høyre og legger til verdi dersom det er mulig
            if (node.høyre != null) {
                node = node.høyre;
                sb.append(", ").append(node.verdi);
            }
            //Dersom det ikke finnes noen høyrenode og vi ikke er i en bladnode,
            //går vi til venstre og legger til verdi
            else {
                node = node.venstre;
                sb.append(", ").append(node.verdi);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public String lengstGren() {

        Node<T> node = rot, forrige = null;
        Deque<Node<T>> stakk = new ArrayDeque<>(antall);
        ArrayList<Node<T>> nodeliste = new ArrayList<>();
        ArrayList<Integer> lengde = new ArrayList<>();

        stakk.add(node);

        while (node != null) {

            if (node.venstre != null) {
                stakk.add(node.venstre);
                node = node.venstre;
            }
            if (node.høyre != null) {
                stakk.add(node.høyre);
                node = node.høyre;
            }

            if (node.venstre == null && node.høyre == null) {
                stakk.toArray();

                while (node.høyre == null && forrige != node.høyre) {
                    node = node.forelder;
                }
                node = node.høyre;
            }
        }
        return null;
    }

    public String[] grener()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String bladnodeverdier()
    {

        return bladnodeStr(rot, new StringJoiner(", ", "[", "]")).toString();
    }

    private StringJoiner bladnodeStr(Node<T> node, StringJoiner sj) {

        if (node.venstre != null) bladnodeStr(node.venstre, sj);
        if (node.høyre != null) bladnodeStr(node.høyre, sj);

        if (node.venstre == null && node.høyre == null) {
            sj.add(node.verdi.toString());
        }

        return sj;
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
//
        //char[] verdier = "IATBHJCRSOFELKGDMPQN".toCharArray();
        //for (char c : verdier) tre.leggInn(c);


        //Node<Integer> node = tre.rot;
        //tre.nullstill();
        //System.out.println(tre);
        System.out.println(tre.bladnodeverdier());
        //System.out.println(tre);
        //System.out.println(tre.høyreGren());
        //System.out.println(tre.omvendtString());
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