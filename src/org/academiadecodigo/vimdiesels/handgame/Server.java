package org.academiadecodigo.vimdiesels.handgame;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static int ROUNDS = 5;
    int player1Wins = 0;
    int player2Wins = 0;

    private ServerSocket serverSocket;
    private Player player;

    private static int DEFAULT_PORT = 9090;
    private Socket socket1;
    private Socket socket2;
    private final List<String> playersList = Collections.synchronizedList(new ArrayList<>());
    private final List<Player> al = Collections.synchronizedList(new ArrayList<>());
    private Player[] players;
    private int[] values;
    private Map<String, Integer> hands = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.init();
    }


    private void init() throws IOException {


        serverSocket = new ServerSocket(DEFAULT_PORT);


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


    public synchronized void handStore(String player, int value){
        hands.put(player,value);
        System.out.println(hands.keySet().toString());

        if (playersList.contains(player)) {
            int index = playersList.indexOf(player);
            playersList.add(index, player);
        }

        if (hands.size()==2){
            try {
                multiCompareHands();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void multiCompareHands() throws IOException {

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
            sendMessageToPlayer(al.get(0), "\n" + player1 + " beats "+player2+"\n");
        }

        if (winner == 2) {
            player2Wins++;
            sendMessageToPlayer(al.get(1), "\n" +player2+" beats " + player1+ "\n");
        }

        if (winner == 3) {
            broadCast("\nTie!\n");
        }
    }


    public void singleCompareHands(int value) throws IOException {

        if (value == 0) {

            if (player1Wins > player2Wins) {
                broadCast("\nOverall winner is: " + player.getName());
                return;
            }

            if (player1Wins < player2Wins) {
                broadCast("\nOverall winner is: " + player.getName());
                return;
            }
        }

        Game.GameHand handPlayer1;
        Game.GameHand handPlayer2;

        handPlayer1 = Game.GameHand.values()[value - 1];
        int random = (int) (Math.random() * Game.GameHand.values().length);
        handPlayer2 = Game.GameHand.values()[random];
        int winner = Game.compareHands(handPlayer1, handPlayer2);

        if (winner == 1) {
            player1Wins++;
            sendMessageToPlayer(player, "\n" + player.getName() + " beats computer\n");
        }

        if (winner == 2) {
            player2Wins++;
            sendMessageToPlayer(player, "\nComputer beats " + player.getName() + "\n");
        }

        if (winner == 3) {
            sendMessageToPlayer(player, "\nTie!\n");
        }

    }
}



