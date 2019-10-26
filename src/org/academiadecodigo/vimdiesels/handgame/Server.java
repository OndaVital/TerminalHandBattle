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
    private final List<Player> playersList = Collections.synchronizedList(new ArrayList<>());
    private Player[] players;
    private int[] values;

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
            playersList.add(player);

            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(player);

            System.out.println("connection established in:" + serverSocket.getLocalPort());

        }

    }

    private void broadCast(String message) throws IOException {
        synchronized (playersList) {
            for (Player player : playersList) {
                player.sendMessage(message);
            }
        }

    }

    private void sendMessageToPlayer(Player player, String message) throws IOException {
        player.sendMessage(message);
    }

    int i = 0;

    public void handStorer(Player player, int value) throws IOException {
        players[i] = player;
        values[i] = value;
        i++;

        if(players.length == 2 && values.length == 2){
            multiCompareHands();
        }
    }

    public void multiCompareHands() throws IOException {


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



        handPlayer1 = Game.GameHand.values()[values[0]];

        handPlayer2 = Game.GameHand.values()[values[1]];
        int winner = Game.compareHands(handPlayer1, handPlayer2);

        if (winner == 1) {
            player1Wins++;
            sendMessageToPlayer(players[0], "\n" + players[0].getName() + " beats computer\n");
        }

        if (winner == 2) {
            player2Wins++;
            sendMessageToPlayer(players[1], "\n beats " + players[1].getName() + "\n");
        }

        if (winner == 3) {
            sendMessageToPlayer(player, "\nTie!\n");
        }

        i = 0;

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



