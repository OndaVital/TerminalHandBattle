package org.academiadecodigo.vimdiesels.handgame;


class Game {

    private static int compareHands(GameHand handPlayer1, GameHand handPlayer2) {

        int winner = 1;

        switch (handPlayer1) {
            case PAPER:
                if (handPlayer2.equals(GameHand.SCISSORS) || handPlayer2.equals(GameHand.LIZARD)) {
                    winner = 2;
                }
                break;
            case ROCK:
                if (handPlayer2.equals(GameHand.PAPER) || handPlayer2.equals(GameHand.SPOCK)) {
                    winner = 2;
                }
                break;
            case SCISSORS:
                if (handPlayer2.equals(GameHand.ROCK) || handPlayer2.equals(GameHand.SPOCK)) {
                    winner = 2;
                }
                break;
            case LIZARD:
                if (handPlayer2.equals(GameHand.ROCK) || handPlayer2.equals(GameHand.SCISSORS)) {
                    winner = 2;
                }
                break;
            case SPOCK:
                if (handPlayer2.equals(GameHand.PAPER) || handPlayer2.equals(GameHand.LIZARD)) {
                    winner = 2;
                }
                break;
        }

        return winner;

    }


    public enum GameHand {
        PAPER,
        ROCK,
        SCISSORS,
        LIZARD,
        SPOCK
    }


}
