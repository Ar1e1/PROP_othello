package edu.upc.epsevg.prop.othello.players.minmaxothello;
import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;

public class PlayerMinMax implements IPlayer, IAuto 
{
    String name;
    public static final int MAXFILES = 8;
    public static final int MAXCOLUMNES = 8;
    private int[][] taulaPrioritats = new int[8][8];
    private boolean jugadorNegre;
   
    public PlayerMinMax(String name) {
        this.name = name;
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     * @return -
     */
    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Getter del nom.
     * @return el nom de PlayerMinMax.
     */
    @Override
    public String getName() {
        return name;
    }    
    
    private static class MovePreferent {
        private final int preferencia;
        private final Point p;
        
        /**
         *
         * Constructor de objecte MovePreferent a partir de
         * @param pref Preferencia calculada segons la taulaPrioritats de la millor jugada possible.
         * @param p punt a on es moura la peca.
         * @return el nom de PlayerMinMax.
         */
        public MovePreferent(int pref, Point p) {
            this.preferencia = pref;
            this.p = p;
        }
        
        /**
         * Getter del Point
         * @return this.p.
         */
        public Point getPoint() {
            return this.p;
        }
        
        /**
         * Getter de la preferencia
         * @return this.preferencia
         */        
        public int getPreferencia() {
            return preferencia;
        }
    }

     /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) 
    {
        
        // -------PROFUNDITAT escollida-------
        int profunditat = 7;

        
        // Omplim la tuala de prioritats amb els valor que hem escollit. 
        this.omplirTaulaAmbValors(0); // modif = 0
        //Llista de Moviments amb preferencia calulada.
        ArrayList<MovePreferent> listOfMoves = new ArrayList<>();

        jugadorNegre = s.getCurrentPlayer() == CellType.PLAYER1;

        for (int fila = 0; fila < MAXFILES; ++fila) {
            for (int col = 0; col < MAXCOLUMNES; ++col) {
                Point desti = new Point(fila, col);
                if (s.canMove(desti, s.getCurrentPlayer())) {
                    GameStatus copia = new GameStatus(s);
                    copia.movePiece(desti);

                    // Si el Punt(fila, col) és un cantó, té prioritat. 
                    if ((fila == 0 && col == 0) || (fila == 0 && col == 7) || 
                        (fila == 7 && col == 0) || (fila == 7 && col == 7)) 
                    {
                        Point corner = new Point(fila, col);
                        return new Move(corner, 0L, profunditat, SearchType.MINIMAX);
                    }
                    
                    MovePreferent nou = new MovePreferent(alphabeta(copia,
                            profunditat, Integer.MIN_VALUE, Integer.MAX_VALUE), desti);
                    listOfMoves.add(nou);
                }
            }
        }
     
        MovePreferent max = listOfMoves.get(0);
        for (int i = 1; i < listOfMoves.size(); ++i) {
            if (listOfMoves.get(i).getPreferencia() > max.getPreferencia()) {
                max = listOfMoves.get(i);
            }
        }        
        
        //System.out.println("\n > Moviment amb preferencia maxima, Point: " + max.getPoint());
        //System.out.println(" > Moviment amb preferencia maxima, Preferencia: " + max.getPreferencia());

        return new Move(max.getPoint(), 0L, profunditat, SearchType.MINIMAX);
    }
    
    /**
     * Omplim la taula de prioritats amb els valors que hem escollit.
     * 
     * @param modif auxiliar per testejar valors, modificant la taula, i comparar num 
     *              victories/derrotes contra DesdemonaPlayer.
    */
    public void omplirTaulaAmbValors(int modif) 
    {
        /*
        taulaPrioritats = new int[][] {
                            { 4, -3,  2,  2,  2,  2, -3,  4}, 
                            {-3, -4, -1, -1, -1, -1, -4, -3}, 
                            { 2, -1,  1,  0,  0,  1, -1,  2}, 
                            { 2, -1,  0,  1,  1,  0, -1,  2}, 
                            { 2, -1,  0,  1,  1,  0, -1,  2}, 
                            { 2, -1,  1,  0,  0,  1, -1,  2},
                            {-3, -4, -1, -1, -1, -1, -4, -3}, 
                            { 4, -3,  2,  2,  2,  2, -3,  4}};
        */
        
        taulaPrioritats = new int[][] {
                            {1000, -300, 100,  80,  80, 100, -300, 1000}, 
                            {-300, -500, -45, -50, -50, -45, -500, -300}, 
                            { 100,  -45,   3,   1,   1,   3,  -45,  100}, 
                            {  80,  -50,   1,   5,   5,   1,  -50,   80}, 
                            {  80,  -50,   1,   5,   5,   1,  -50,   80}, 
                            { 100,  -45,   3,   1,   1,   3,  -45,  100},
                            {-300, -500, -45, -50, -50, -45, -500, -300}, 
                            {1000, -300, 100,  80,  80, 100, -300, 1000}};
        
        // Modifiquem la taula de prioritats per optimitzar els seus valors.                    
        if (modif != 0) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    // Els cantons tenen mes pes
                    if ((i == 0 && j == 0) || (i == 0 && j == 0) || 
                        (i == 7 && j == 0) || (i == 7 && j == 7)) 
                    {
                        taulaPrioritats[i][j] += (modif * 4);
                    }
                    if (j == 1 && i == 6)
                        taulaPrioritats[i][j] -= (modif * 2);
                    if (i == 1 && j == 6)
                        taulaPrioritats[i][j] -= (modif * 2);
                    
                    taulaPrioritats[i][j] += modif;
                }
            }
        }
        
    }
    
    /**
     * 
     * @param sActual estado de juego actual
     * @param profunditat profundidad del arbol
     * @param alpha mejor movimeinto nuestro
     * @param beta mejor movimiento del rival
     * @return devuelve alpha o beta si la profundidad es >= 0 o una evaluacion simple. 
     */
    public int alphabeta(GameStatus sActual, int profunditat, int alpha, int beta) 
    {
        if (profunditat == 0 || sActual.isGameOver()) 
        {
            int sumaPrioritats = calcPreferencia(sActual);
            // System.out.println(a);
            return sumaPrioritats;
        }
        else 
        {
            if (jugadorNegre && sActual.getCurrentPlayer() == CellType.PLAYER1 || 
               (!jugadorNegre && !(sActual.getCurrentPlayer() == CellType.PLAYER1))) { // MAX
                for (int row = 0; row < MAXFILES; row++) {
                    for (int col = 0; col < MAXCOLUMNES; col++) {
                        Point desti = new Point(row, col);
                        if (sActual.canMove(desti, sActual.getCurrentPlayer())) {
                            
                            // Fem una copia del tauler actual.
                            GameStatus copia = new GameStatus(sActual);
                            
                            // Apliquem el moviment al tauler copia.
                            copia.movePiece(desti);

                            int MAX = alphabeta(copia, profunditat - 1, alpha, beta);
                            if (MAX > alpha) {
                                alpha = MAX;
                            }
                            
                            // En cas que alpha >= beta ja no necessitem 
                            // la resta de possible recorreguts. Apliquem la poda. 
                            if (alpha >= beta) {
                                return alpha;
                            }
                        }
                    }
                }
                return alpha;
            } 
            else { // MIN
                for (int row = 0; row < MAXFILES; row++) {
                    for (int col = 0; col < MAXCOLUMNES; col++) {
                        Point desti = new Point(row, col);
                        if (sActual.canMove(desti, sActual.getCurrentPlayer())) {
                            
                            // Fem una copia del tauler actual.
                            GameStatus copia = new GameStatus(sActual);

                            // Apliquem el moviment al tauler copia.
                            copia.movePiece​(desti);

                            int MIN = alphabeta(copia, profunditat - 1, alpha, beta);
                            if (MIN < beta) {
                                beta = MIN;
                            }
                            
                            // En cas que alpha >= beta ja no necessitem 
                            // la resta de possible recorreguts. Apliquem la poda.
                            if (alpha >= beta) {
                                return beta;
                            }
                        }
                    }
                }
                return beta;
            }
        }
    }

    /**
     * Calculem la preferencia d'una casella segons la taula de prioritats i 
     * les fitxes que es troben al tauler.
     * 
     * @param sActual
     * @return preferencia segons siguem jugador negre (CellType.PLAYER1)
     *         o jugador blanc (CellType.PLAYER2)
     */
    public int calcPreferencia(GameStatus sActual) 
    {
        int preferencia = 0;
        int puntsNegre = 0;
        int puntsBlanc = 0;

        for (int fila = 0; fila < MAXFILES; fila++) 
        {
            for (int col = 0; col < MAXCOLUMNES; col++) {
                Point desti = new Point(fila, col);
                if (sActual.getPos(fila, col) == CellType.PLAYER1) {
                    puntsNegre += taulaPrioritats[fila][col];
                } 
                else if (sActual.getPos(fila, col) == CellType.PLAYER2) {
                    puntsBlanc += taulaPrioritats[fila][col];
                }

                if ((jugadorNegre && sActual.getCurrentPlayer() == CellType.PLAYER1) || 
                    (!jugadorNegre && !(sActual.getCurrentPlayer() == CellType.PLAYER1))) {
                    if (sActual.canMove(desti, sActual.getCurrentPlayer())) {
                        preferencia += 100;
                    }
                }
                else if ((jugadorNegre && sActual.getCurrentPlayer() == CellType.PLAYER1) || 
                         (!jugadorNegre && !(sActual.getCurrentPlayer() == CellType.PLAYER1))) { 
                    if (sActual.canMove(desti, sActual.getCurrentPlayer())) {
                        preferencia -= 100;
                    }
                }
            }
        }

        if (jugadorNegre) {
            preferencia += (puntsNegre - puntsBlanc);
        } 
        else {
            preferencia += (puntsBlanc - puntsNegre);
        }

        return preferencia;
    }
}
