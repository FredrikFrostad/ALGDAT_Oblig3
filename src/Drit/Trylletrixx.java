package Drit;

public class Trylletrixx {

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
}
