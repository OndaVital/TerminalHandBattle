package org.academiadecodigo.vimdiesels.handgame;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;

import java.io.*;
import java.net.Socket;

public class Player implements Runnable {


    private int rounds;
    private String name;
    private Server server;
    private Socket socket;
    private Prompt prompt;
    private PrintWriter writer;
    private BufferedReader reader;


    public Player(int rounds, Server server, Socket socket) throws IOException {
        this.rounds = rounds;
        this.server = server;
        this.socket = socket;
        this.prompt = new Prompt(socket.getInputStream(), new PrintStream(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream());
    }

    public String getName() {

        return this.name;
    }

    private void setName(String name) {

        this.name = name;
    }

    private void chooseName() throws IOException {

        String welcomeMsg = "\n--------------- Welcome to Rock, Paper, " +
                "Scissors, Lizard, Spock -----------------\n";
        String tellName = "Tell me your name: ";

        writer.println(welcomeMsg);
        writer.flush();
        writer.println(tellName);
        writer.flush();
        this.name = reader.readLine();
    }

    private int menu() {

        String[] gameOptions = {"Single-player", "Multi-player", "See instructions"};
        MenuInputScanner menu = new MenuInputScanner(gameOptions);
        menu.setMessage("Want to practice, or are you ready to defeat someone else?");
        int playMode = prompt.getUserInput(menu);
        return playMode;
    }

    public void giveSingle() throws IOException {

        String[] gameHands = {"Paper", "Rock", "Scissors", "Lizard", "Spock"};
        MenuInputScanner hands = new MenuInputScanner(gameHands);
        hands.setMessage("Pick a hand!");

        int value = prompt.getUserInput(hands);
        server.singleCompareHands(value);
    }


    private synchronized void giveMultiHand() {

        String[] gameMultiHands = {"Paper", "Rock", "Scissors", "Lizard", "Spock"};
        MenuInputScanner hands = new MenuInputScanner(gameMultiHands);
        hands.setMessage("Pick a hand!");
        int value = prompt.getUserInput(hands);
        server.handStore(this.name, value);
        notifyAll();

    }

    private void instructions() {

        String instructions = "";
        String[] menuInstructions = {"Go back."};
        MenuInputScanner menu = new MenuInputScanner(menuInstructions);
        menu.setMessage(instructions);
        int goBack = prompt.getUserInput(menu);

        if (goBack == 1) {
            menu();
        }
    }


    public void sendMessage(String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.flush();
    }

    @Override
    public synchronized void run() {


        try {
            chooseName();

        } catch (IOException e) {
            e.printStackTrace();
        }


        int menuAnswer = menu();

        if (menuAnswer == 1) {


            int cycles = 0;

            while (cycles < rounds) {

                try {
                    giveSingle();
                    cycles++;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            try {
                server.singleCompareHands(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (menuAnswer == 2) {
            int cycles = 0;
            while (cycles < rounds) {
                giveMultiHand();
                cycles++;
                System.out.println(cycles);
                //notifyAll();
                System.out.println(cycles + "asdasd");
            }
        }
        if (menuAnswer == 3) {
            instructions();
        }
    }


}

