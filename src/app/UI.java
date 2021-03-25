package app;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import chess.Cor;
import chess.PartidaXadres;
import chess.PecaXadres;
import chess.XadresPosicao;

public class UI {

	// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	public static void clearScreen() {
		System.out.println("\033[H\033[2J");
		System.out.flush();
	}

	public static XadresPosicao lerXadresPosica(Scanner sc) {
		try {
			String s = sc.nextLine();
			char coluna = s.charAt(0);
			int liinha = Integer.parseInt(s.substring(1));
			return new XadresPosicao(coluna, liinha);
		}
		catch (RuntimeException e) {
			throw new InputMismatchException("Erro ao ler posicao do Xadres: posicoes validas sao do a1 ao h8!");
		}
	}

	public static void printPartida(PartidaXadres partidaXadres, List<PecaXadres> captudrada) {
		printTabuleiro(partidaXadres.getPeca());
		System.out.println();
		printPecaCapturada(captudrada);
		System.out.println();
		System.out.println("Turno :" + partidaXadres.getTurno());
		if(!partidaXadres.getCheckMate()) {
			System.out.println("A vez do jogador: " + partidaXadres.getJogadorCorrespondente());
			if (partidaXadres.getCheck()) {
				System.out.println("CHECK!");
			}
		} else {
			System.out.println("CHECKMATE!!");
			System.out.println("Vencedor: " + partidaXadres.getJogadorCorrespondente());
		}
	}

	public static void printTabuleiro(PecaXadres[][] pecas) {
		for (int i=0; i<pecas.length; i++) {
			System.out.print(ANSI_CYAN_BACKGROUND  + ANSI_RED + (8 - i) + " " + ANSI_RESET);
			for (int j=0; j<pecas.length; j++) {
				printPeca(pecas[i][j], false);
			}
			System.out.println();
		}
		System.out.println(ANSI_CYAN_BACKGROUND  + ANSI_RED + "  a b c d e f g h" + ANSI_RESET);
	}

	public static void printTabuleiro(PecaXadres[][] pecas, boolean[][] possiveisJogadas) {
		for (int i=0; i<pecas.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j=0; j<pecas.length; j++) {
				printPeca(pecas[i][j], possiveisJogadas[i][j]);
			}
			System.out.println();
		}
		System.out.println(ANSI_CYAN_BACKGROUND  + ANSI_RED + "  a b c d e f g h" + ANSI_RESET);
	}

	private static void printPeca(PecaXadres peca, boolean background) {
		if (background) {
			System.out.print(ANSI_BLUE_BACKGROUND);
		}
		if (peca == null) {
			System.out.print("-" + ANSI_RESET);
		}
		else {
			if (peca.getCor() == Cor.BRANCO) {
				System.out.print(ANSI_WHITE + peca + ANSI_RESET);
			}
			else {
				System.out.print(ANSI_YELLOW + peca + ANSI_RESET);
			}
		}
		System.out.print(" ");
	}

	private static void printPecaCapturada(List<PecaXadres> capturada) {
		List<PecaXadres> branca = capturada.stream().filter(x -> x.getCor() == Cor.BRANCO).collect(Collectors.toList());
		List<PecaXadres> preta = capturada.stream().filter(x -> x.getCor() == Cor.PRETO).collect(Collectors.toList());
		System.out.println("Pecas capturadas: ");
		System.out.print("Branca: ");
		System.out.print(ANSI_WHITE);
		System.out.print(Arrays.toString(branca.toArray()));
		System.out.println(ANSI_RESET);
		System.out.print("Pretas: ");
		System.out.print(ANSI_YELLOW);
		System.out.print(Arrays.toString(preta.toArray()));
		System.out.println(ANSI_RESET);
	}
}
