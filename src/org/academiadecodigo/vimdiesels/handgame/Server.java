package org.academiadecodigo.vimdiesels.handgame;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int ROUNDS = 5;
    private static final int DEFAULT_PORT = 9090;
    private int player1Wins = 0;
    private int player2Wins = 0;
    private Player player;
    private final List<String> playersList = Collections.synchronizedList(new ArrayList<>());
    private final List<Player> al = Collections.synchronizedList(new ArrayList<>());
    private Map<String, Integer> hands = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.init();
    }


    private void init() throws IOException {

        ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);

        while (true) {

            Socket playerSocket = serverSocket.accept();
            System.out.println("\n Ok and running on port " + serverSocket.getLocalPort() + "...");

            player = new Player(ROUNDS, this, playerSocket);
            al.add(player);

            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(player);

            System.out.println("connection established in:" + serverSocket.getLocalPort());
        }
    }

    private void broadCast(String message) throws IOException {
        synchronized (al) {
            for (Player player : al) {
                player.sendMessage(message);
            }
        }
    }

    private void sendMessageToPlayer(Player player, String message) throws IOException {
        player.sendMessage(message);
    }

    private boolean enoughPlayers(){
        return hands.size() == 2;
    }

    synchronized void handStore(String player, int value){
        hands.put(player,value);

        /*if (playersList.contains(player)) {
            int index = playersList.indexOf(player);
            playersList.add(index, player);
            if (enoughPlayers()){
                try {
                    multiCompareHands();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

        playersList.add(player);

        if (enoughPlayers()){
            try {
                notifyAll();
                multiCompareHands();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void multiCompareHands() throws IOException {
        System.out.println("estou aqui!");
        /* if (value == 0) {

            if (player1Wins > player2Wins) {
                broadCast("\nOverall winner is: " + player.getName());
                return;
            }

            if (player1Wins < player2Wins) {
                broadCast("\nOverall winner is: " + player.getName());
                return;
            }
        }*/

        Game.GameHand handPlayer1;
        Game.GameHand handPlayer2;

        String player1 = playersList.get(0);
        String player2 = playersList.get(1);

        handPlayer1 = Game.GameHand.values()[hands.get(player1)];
        handPlayer2 = Game.GameHand.values()[hands.get(player2)];

        int winner = Game.compareHands(handPlayer1, handPlayer2);

        if (winner == 1) {
            player1Wins++;
            broadCast("\n" + player1 + " beats "+player2+"\n");
            clearLists();
            notifyAll();
        }

        if (winner == 2) {
            player2Wins++;
            broadCast("\n" +player2+" beats " + player1+ "\n");
            clearLists();
            notifyAll();
        }

        if (winner == 3) {
            broadCast("\nTie!\n");
            clearLists();
            notifyAll();
        }

    }

    void singleCompareHands(int value) throws IOException {

        if (value == 0) {

            if (player1Wins > player2Wins) {
                sendMessageToPlayer(player,"\nOverall winner is " + player.getName()+"!");
                return;
            }

            if (player1Wins < player2Wins) {
                sendMessageToPlayer(player,"\nOverall winner is the computer!");
                return;
            }

            sendMessageToPlayer(player,"\n The game was a tie!");
        }

        Game.GameHand handPlayer1;
        Game.GameHand handPlayer2;

        handPlayer1 = Game.GameHand.values()[value - 1];
        int random = (int) (Math.random() * Game.GameHand.values().length);
        handPlayer2 = Game.GameHand.values()[random];
        int winner = Game.compareHands(handPlayer1, handPlayer2);

        if (winner == 1) {

            player1Wins++;
            sendMessageToPlayer(player, "Computer chose: " + handPlayer2);
            sendMessageToPlayer(player, "\n" + player.getName() + " beats computer!\n");
        }

        if (winner == 2) {

            player2Wins++;
            sendMessageToPlayer(player, "Computer chose: " + handPlayer2);
            sendMessageToPlayer(player, "\nComputer beats " + player.getName() + "!\n");
        }

        if (winner == 3) {

            sendMessageToPlayer(player,"Computer hand was: " + handPlayer2);
            sendMessageToPlayer(player, "\nTie!\n");
        }

    }

    private void clearLists(){
        hands.clear();
        playersList.clear();
    }
}



