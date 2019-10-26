package org.academiadecodigo.vimdiesels.handgame;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;

public class Player implements Runnable {


    private int rounds;
    private String name;
    private Socket socket;
    private Prompt prompt;


    public Player(int rounds, Socket socket) throws IOException {

        this.rounds = rounds;
        this.socket = socket;
        this.prompt = new Prompt(socket.getInputStream(), new PrintStream(socket.getOutputStream()));
    }

    public String getName() {

        return this.name;
    }

    private void setName(String name){

        this.name = name;
    }

    private void chooseName(){

        StringInputScanner user = new StringInputScanner();
        user.setMessage("Tell me your name: ");
        String userName = prompt.getUserInput(user);
        setName(userName);
    }

    private int menu(){

        String[] gameOptions = {"Single-player", "Multi-player", "See instructions"};
        MenuInputScanner menu = new MenuInputScanner(gameOptions);
        menu.setMessage("Want to practice, or are you ready to defeat someone else?");
        int playMode = prompt.getUserInput(menu);
        return playMode;
    }

    private void getHand() throws IOException {

        String[] gameHands = {"Paper", "Rock", "Scisors", "Lizard", "Spock"};
        MenuInputScanner hands = new MenuInputScanner(gameHands);
        hands.setMessage("Pick a hand!");
        int value = prompt.getUserInput(hands);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(value);
        writer.flush();
    }

    private void instructions(){

        String instructions = "";
        String[] menuInstructions = {"Go back."};
        MenuInputScanner menu = new MenuInputScanner(menuInstructions);
        menu.setMessage(instructions);
        int goBack = prompt.getUserInput(menu);

        if(goBack == 1){
            menu();
        }
    }

    @Override
    public void run() {

        chooseName();
        int menuAnswer = menu();

        if(menuAnswer == 1){}

        if(menuAnswer == 2){

            try {
                getHand();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(menuAnswer == 3){
            instructions();
        }
    }
}
