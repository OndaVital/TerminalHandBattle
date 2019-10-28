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
    private int cycle;

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

    private void chooseName() throws IOException {
        String welcomeMsg = TerminalStrings.introBanner();
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


    public void giveMultiHand() throws IOException {

        synchronized (server) {
            String[] gameMultiHands = {"Paper", "Rock", "Scissors", "Lizard", "Spock"};
            MenuInputScanner hands = new MenuInputScanner(gameMultiHands);
            hands.setMessage("Pick a hand!");
            int value = prompt.getUserInput(hands);
            System.out.println(value);

            server.handStore(this.name, value);
            notifyAll();
        }
    }

    private void instructions() {
        String instructions = TerminalColors.ANSI_YELLOW.getAnsi() + "Greetings!\n" +
                "Welcome to the TerminalHandBattle game! In order to play, follow the instructions below.\n\n" +
                TerminalColors.ANSI_WHITE.getAnsi() + "1. Choose one of either game modes, single-player or " +
                "multi-player.\n" +
                TerminalColors.ANSI_WHITE.getAnsi() + "2. Once in the game, press the number that corresponds " +
                "to the hand that you want to play.\n" +
                TerminalColors.ANSI_WHITE.getAnsi() + "3. Game rules are:\n" +
                TerminalColors.ANSI_GREEN.getAnsi() + "\t -> Scissors cuts Paper \n\t -> Paper covers Rock \n\t " +
                "-> Rock crushes Lizard \n\t " +
                "-> Lizard poisons Spock \n\t -> Spock smashes Scissors \n\t -> Scissors decapitates Lizard \n\t " +
                "-> Lizard eats Paper \n\t " +
                "-> Paper disproves Spock \n\t -> Spock vaporizes Rock\n\t -> Rock crushes Scissors \n" +
                TerminalColors.ANSI_WHITE.getAnsi() + "4. At the end, you’ll find out who is the winner!" +
                TerminalColors.ANSI_RESET.getAnsi();

        String[] menuInstructions = {"Go back."};
        MenuInputScanner menu = new MenuInputScanner(menuInstructions);
        menu.setMessage(instructions);
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
            if (cycle == 0) {
                chooseName();
            }
            int menuAnswer = menu();
            if (menuAnswer == 1) {
                int cycles = 0;
                while (cycles < rounds) {
                    giveSingle();
                    cycles++;
                }
                server.singleCompareHands(0);
                cycle++;
                run();
            }

            if (menuAnswer == 2) {
                while (server.getPlayer1Wins() < 4
                        || server.getPlayer2Wins() < 4) {
                    giveMultiHand();
                }
            }

            if (menuAnswer == 3) {
                instructions();
                cycle++;
                run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

