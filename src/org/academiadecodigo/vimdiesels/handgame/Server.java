package org.academiadecodigo.vimdiesels.handgame;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static int ROUNDS = 5;
    private ServerSocket serverSocket;
    private Player player1;
    private Player player2;
    private static int DEFAULT_PORT = 9090;

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.init();
    }




    private void init() throws IOException {

    String welcomeMsg = "--------------- Welcome to Rock, Paper, " +
            "Scissors, Lizard, Spock -----------------";

        serverSocket = new ServerSocket(DEFAULT_PORT);

        int cycle = 0;
        int player1Wins = 0;
        int player2Wins = 0;
        int draw = 0;

        Socket socket1 = serverSocket.accept();
        Socket socket2 = serverSocket.accept();

        player1 = new Player(rounds, socket1);
        player2 = new Player(rounds, socket2);

        new Thread(player1).start();
        new Thread(player2).start();

        broadCast(welcomeMsg);

        while (cycle < 5) {
            Game.GameHand handPlayer1;
            Game.GameHand handPlayer2;
            System.out.println("\n Ok and running on port " + serverSocket.getLocalPort() + "...");

            handPlayer1 = Game.GameHand.values()[player1.getHand()];
            System.out.println("Value 1 generated");

            handPlayer2 = Game.GameHand.values()[player2.getHand()];
            System.out.println("Value 2 generated");

            cycle++;
            int winner = Game.compareHands(handPlayer1, handPlayer2);

            if (winner == 0){
                draw++;
                broadCast("Draw!");
                continue;
            }

            if (winner == 1){
                player1Wins++;
                broadCast("Player 1 beats player 2");
                continue;
            }

            if (winner == 2){
                player2Wins++;
                broadCast("Player 2 beats player 1");
            }

        }

        if (draw>player1Wins && draw>player2Wins){
            broadCast("Is a TIE!");
        }

        if (player1Wins > player2Wins){
            broadCast(player1.getName()+" wins!!");
        }

        broadCast(player2.getName()+" wins!!");


    }

    private void sendMessage(String message, Socket playerSocket) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(playerSocket.getOutputStream()));
        writer.write(message);
        writer.flush();
    }

    private void broadCast(String message){
        sendMessage(message, player1.getSocket);
        sendMessage(message, player2.getSocket);
    }


}
