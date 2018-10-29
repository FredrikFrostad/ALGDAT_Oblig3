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
        endringer = 0;
    }

    /**
     * Metode som legger inn en ny verdi (node) i treet.
     * @param verdi Verdien som skal legges inn i treet, kan ikke være null
     * @return true dersom noden er lagt inn, false ellers
     */
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
        antall++; //øker antall med 1;
        endringer++;

        return true;
    }


    /**
     * Metode som sjekker om en verdi finnes i treet.
     * @param verdi verdien vi ønsker å finne i treet
     * @return true dersom verdien finnes, false ellers
     */
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

    /**
     * Metode som fjerner første forkomst av en verdi dersom den finnes i treet
     * @param verdi Verdien som skal fjernes
     * @return true dersom verdien er i treet og blir fjernet, false ellers
     */
    @Override
    public boolean fjern(T verdi) {
        if (verdi == null) return false;    // treet har ingen nullverdier


        Node<T> p = rot, q = null;          // q skal være forelder til p

        while (p != null) {                 // leter etter verdi
            int cmp = comp.compare(verdi,p.verdi);      // sammenligner
            if (cmp < 0) {
                q = p;                      // setter forelder
                p = p.venstre; }            // går til venstre
            else if (cmp > 0) {
                q = p;                      // setter forelder
                p = p.høyre; }              // går til høyre
            else break;                     // vi har funnet en node p == parameterverdien
        }

        if (p == null) return false;        // Parameterverdien finnes ikke i treet


        if(p.venstre == null                // Tilfelle 1: Dersom p er en bladnode nuller vi pekeren til p
                && p.høyre == null
                    && q != null) {

            if(q.høyre == p) q.høyre = null;
            else q.venstre = null;

        }
        else if (p.venstre == null || p.høyre == null) {                //Tilfelle 2: p har ett barn

            Node<T> child = p.venstre != null ? p.venstre : p.høyre;    // Finner child noden til p

            if (p == rot) rot = child;      //Oppdaterer pekere og forbereder p for garbagecollection
            else if (p == q.venstre) q.venstre = child;
            else {
                q.høyre = child;
                child.forelder = q;
                p = null;

            }
        }
        // Dersom p har to barn må vi finne neste innorden og erstatte p med denne
        else {
            Node<T> s = p, r = p.høyre;   // finner neste i inorden

            while (r.venstre != null) {
                s = r;    // s er forelder til r
                r = r.venstre;
            }

            p.verdi = r.verdi;   // kopierer verdien i r til p

            if (s != p) s.venstre = r.høyre;
            else s.høyre = r.høyre;
        }

        antall--;
        endringer++;
        return true;

    }

    /**
     * Metode som fjerner alle forekomster av en verdi i treet
     * @param verdi verdien som skal fjernes
     * @return antall forekamster av den fjernede verdien i treet
     */
    public int fjernAlle(T verdi) {

        if (verdi == null || rot == null) return 0;     //Dersoom tomt tre eller nullverdi som parameter

        int antallFjernet = 0;

        while (inneholder(verdi)) {                     // Så lenge parameterverdien finnes i treet kaller vi
            fjern(verdi);                               // fjern-metoden, og oppdaterer antallFjernet
            antallFjernet++;
        }
        antall = 0;                                     // Oppdaterer antall
        endringer++;                                    // Oppdaterer endringer
        return antallFjernet;
    }

    /**
     * Metode som returnerer antall noder i treet
     * @return
     */
    @Override
    public int antall()
    {
        return antall;
    }

    /**
     * Metode som reurnerer antall forekomster av en gitt verdi i treet
     * @param verdi verdien vi vil finne antall forekomster av i treet. Kan ikke være null
     * @return en integer som representerer antall forekomster av verdien
     */
    public int antall(T verdi)
    {
        int antall = 0;
        java.util.Deque<Node<T>> stack = new java.util.ArrayDeque<>(this.antall);
        stack.addFirst(rot);

        // Løper gjennom treet med bruk av hjelpestakk. Sjekker antallet forekomster av
        // verdi i innorden rekkefølge
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


    /**
     * Metode som sjekker om teet er tomt ved hjelp av treets antall-variabel
     * @return true dersom treet er tomt
     */
    @Override
    public boolean tom()
    {
        return antall == 0;
    }

    /**
     * Metode som nulstiller et tre. Metoden nuller alle treets noder i postorden rekkefølge.
     */
    @Override
    public void nullstill()
    {
        if (antall < 1) return;
            nullstillRec(rot);
            antall = 0;
            endringer++;
            rot = null;
    }

    /**
     * Hjelpemetode for nullstilling av et tre. Metoden rekurserer gjennom et binærtre og nuller nodepekere
     * i postorden rekkefølge. Dermed kan garbage collectoren rydde opp i minnet når metoden har kjørt.
     * @param rot
     */
    private void nullstillRec(Node<T> rot) {
        if (rot.venstre != null)
            nullstillRec(rot.venstre);
        if (rot.høyre!= null)
            nullstillRec(rot.høyre);

        rot.venstre = null;
        rot.høyre = null;
        rot.forelder = null;
    }


    /**
     * Metode som tar en node som parameter og returnerer neste node inorden til parameternoden.
     * Dersom vi er kommet til treets siste node returnerer metoden null. Dette må håndteres i metodekallet
     * @param p noden vi ønsker å finne neste innorden node til
     * @param <T> Generisk typedefinisjon
     * @return neste innorden til parameternoden
     */
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


    /**
     * Metode som returnerer en strengrepresentasjon av treet, metoden bruker nesteInorden som hjelpemetode
     * @return en streng med treets innhold
     */
    @Override
    public String toString() {
        if (rot == null) return "[]";
        Node<T> node = rot;

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

        if (rot == null) return "[]";

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

    /**
     * Metode som finner den lengste grenen i et tre.
     * Dersom flere grener har samme lengde, velges grenen lengst til venstre
     * @return en string med verdiene i den lengste grenen
     */
    public String lengstGren() {

        if (rot == null) return "[]";                           //sjekker om treet er tomt

        ArrayList<Node<T>> bladnoder = finnBladnoder();         // finner alle bladnodene
        ArrayList<Deque<Node<T>>> grener = new ArrayList<>();   //Lager et array for å lagre alle grenene som stakker
        Node<T> node;

        for (int i = 0; i < bladnoder.size(); i++) {            // For hver bladnode itererer vi opp til roten
                                                                // og bygger opp en stakk med grenens noder underveis
            Deque<Node<T>> stakk = new ArrayDeque<>();
            node = bladnoder.get(i);

            while (node != null) {
                stakk.add(node);
                node = node.forelder;
            }
            grener.add(stakk);

        }

        int indeks = 0, lengde, antallnoder = -1;

        for (int i = 0; i < grener.size(); i++) {               // Sjekker hvilken gren som er lengst og lagrer indeks
                                                                // til stakken som inneholder den lengste grenen
            lengde = grener.get(i).size();

            if (lengde > antallnoder) {
                antallnoder = lengde;
                indeks = i;
            }
        }
        Deque<Node<T>> stakk = grener.get(indeks);
        StringJoiner sj = new StringJoiner(", ", "[", "]");

        while (!stakk.isEmpty()) {                              // Bygger opp en streng bestående av verdiene til
            sj.add(stakk.removeLast().verdi.toString());        // den lengste grenen.
        }

        return indeks >= 0 ? sj.toString() : "[]";
    }


    /**
     * Metode som finner alle bladnodene i et tre
     * @return en ArrayList inneholdende alle bladnodene
     */
    private ArrayList<Node<T>> finnBladnoder() {

        return bladnoderRec(rot, new ArrayList<Node<T>>());
    }

    /**
     * Rekursiv hjelpemetode for å finne bladnoder
     * @param node rotnoden i treet
     * @param nodeListe Arraylist som skal holde på bladnodene vi finner
     * @param <T> Generist typedefinisjon
     * @return Arraylist med bladnoder
     */
    private static <T> ArrayList<Node<T>> bladnoderRec(Node<T> node, ArrayList<Node<T>> nodeListe) {

        if (node.venstre != null) bladnoderRec(node.venstre, nodeListe);
        if (node.høyre != null) bladnoderRec(node.høyre, nodeListe);

        if (node.venstre == null && node.høyre == null) {
            nodeListe.add(node);
        }
        return nodeListe;
    }

    /**
     * Metode som finner alle grenene i et tre
     * @return et stringarray med verdiene i hver gren. Grenene er ordnet fra venstre til høyre
     */
    public String[] grener() {

        if (rot ==null) return new String[0];           //Sjekker om treet er tomt

        ArrayList<Deque<T>> dqLst = new ArrayList<>();  // Arraylist som skal holde nodene i de forskjellige grenene

        grenRek(dqLst, rot);                            // Kaller på rekursiv hjelpemetode som legger hver gren
                                                        // i en egen stack
        String[] out = new String[dqLst.size()];
        int i = 0;
        for (Deque<T> element: dqLst) {                 // Itererer over listen med grenstakker og
            out[i++] = element.toString();              // kaller tostring for hver gren
        }
        return out;
    }

    /**
     * Rekursiv hjelpemetode for å finne alle grener i et tre
     * @param dqLst En Arraylist av type Deque som skal holde "grenstakkene"
     * @param node rotnoden i treet
     * @param <T> generisk typedefinisjon
     */
    private static <T> void grenRek(ArrayList<Deque<T>> dqLst, Node<T> node) {

        if (node.venstre != null) {             //Rekurserer nedover i treet
            grenRek(dqLst, node.venstre);
        }
        if (node.høyre != null) {               //Rekurserer nedover i treet
            grenRek(dqLst, node.høyre);
        }
        Deque<T> stakk = new ArrayDeque<>();    //Stakk som skal holde alle nodene i grenen

        if (node.venstre == null && node.høyre == null) {   //Traverserer fra grenens bladnode til rotnode og
            while (node != null) {                          // legger alle noder som passeres på stakken
                stakk.addFirst(node.verdi);
                node = node.forelder;
            }
            dqLst.add(stakk);                               // Legger grenstakken til arraylisten som er sendt med
        }                                                   // parameter
    }


    /**
     * Metode som returnerer en string med alle baldnodeverdiene i treet.
     * Bruker en rekursiv hjelpemetode: bladnodeStr()
     * @return En string med alle bladnodeverdiene
     */
    public String bladnodeverdier()
    {
        if (rot == null) return "[]";   // Sjekker om treet er tomt
        return bladnodeStr(rot, new StringJoiner(", ", "[", "]")).toString();
    }

    /**
     * Rekursiv hjelpemetode som for å finne verdien til alle bladnodene i et tre
     * @param node Gjeldende node (rot i forste kall)
     * @param sj stringjoiner-object som bygger opp stringen med nodeverdier
     * @return det ferdige stringjoiner objektet
     */
    private StringJoiner bladnodeStr(Node<T> node, StringJoiner sj) {

        if (node.venstre != null) bladnodeStr(node.venstre, sj);    // Rekurserer til venstre
        if (node.høyre != null) bladnodeStr(node.høyre, sj);        // Rekurserer til høyre

        if (node.venstre == null && node.høyre == null) {           // Dersom bladnode, legg til verdi
            sj.add(node.verdi.toString());
        }

        return sj;
    }


    public String postString() {
        Stack<Node<T>> stakk = new Stack();
        Node<T> node = rot;
        StringJoiner sj = new StringJoiner(", ", "[", "]");

        while (node != null || !stakk.empty()) {
            while (node != null && (node.venstre != null || node.høyre != null) ) {
                stakk.push(node);
                node = node.venstre;
            }

            if (node != null) sj.add(node.verdi.toString());

            while (!stakk.empty() && node == stakk.peek().høyre) {
                node = stakk.pop();
                sj.add(node.verdi.toString());
            }

            if (stakk.empty()) node = null; else node = stakk.peek().høyre;
        }

        return sj.toString();
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
            if (p != null) this.p = firstLeaf(this.p);
        }

        /**
         * Hjelpemetode som finner første bladnode i treet. Dersom treet
         * er tomt returneres parameternoden.
         * @param p rot noden i treet vi ser på
         * @return den første bladnoden i treet
         */
        private Node<T> firstLeaf(Node<T> p) {

            return finnBladnoder().get(0);
        }

        @Override
        public boolean hasNext()
        {
            return p != null; // Denne skal ikke endres!
        }

        @Override
        public T next()
        {
            if (iteratorendringer != endringer) {
                throw new ConcurrentModificationException
                        ("treet er endret!");
            }
            if (!hasNext()) throw new NoSuchElementException
                    ("det er ikke flere elementer i listen!");

            Node<T> nesteKandidat = nesteInorden(p);

            while (nesteKandidat != null &&
                    (nesteKandidat.venstre != null
                            || nesteKandidat.høyre != null)) {
                nesteKandidat = nesteInorden(nesteKandidat);
            }
                removeOK = true;
                T out = p.verdi;
                q = p;
                p = nesteKandidat;
                return out;
        }


        @Override
        public void remove()
        {
            if (iteratorendringer != endringer)
                throw new ConcurrentModificationException
                        ("treet er endret!");
            if (!removeOK)
                throw new IllegalStateException
                        ("kan ikke fjerne node, ulovlig tillstand!");

            Node<T> node = q.forelder;

            if (node != null) {
                if (node.venstre != null && node.venstre.equals(q)) node.venstre = null;
                else node.høyre = null;
            }else {
                rot = null;
                q.verdi = null;
                q
                        = null;
            }
            endringer++;
            iteratorendringer++;
            antall--;
            removeOK = false;
        }
    } // BladnodeIterator


    public Node<T> firstLeaf(Node<T> p, Node<T> q) {

        Node<T> node = p;

        if (node.venstre != null) firstLeaf(node.venstre, node);
        if (node.høyre != null) firstLeaf(node.høyre, node);

        if (node.venstre == null && node.høyre == null && !node.equals(q)) return p = node;

        return p;
    }

    public static void main(String[] args) {
        ObligSBinTre tre = new ObligSBinTre<>(Comparator.naturalOrder());
        int[] a = {4, 7, 2, 9, 4, 10, 8, 7, 4, 6, 1};
        for (int verdi : a) tre.leggInn(verdi);

        Node<Integer> node = tre.firstLeaf(tre.rot, null);
        System.out.println(node.verdi);
    }

    static <T> Node<T> endreNode(Node<T> node) {return node.venstre;}
} // ObligSBinTre

//************************ GJEMMESTED FOR GULL OG TRYLLETRIXX ************************************

/*

    private static <T> Node<T> nesteBladnode(Node<T> p)  {

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

/*
    public String lengstGren() {

        if (rot == null) return "[]";

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

        int index = 0;


        return null;
    }
*/