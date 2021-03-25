package app;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.PartidaXadres;
import chess.PecaXadres;
import chess.XadresException;
import chess.XadresPosicao;

public class App {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaXadres partidaXadres = new PartidaXadres(); 
		List<PecaXadres> capturada = new ArrayList<PecaXadres>();

		while (!partidaXadres.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printPartida(partidaXadres, capturada);
				System.out.println();
				System.out.print("Origem: ");
				XadresPosicao origem = UI.lerXadresPosica(sc);
				
				boolean[][] possiveisJogadas = partidaXadres.possibilidadesDeMover(origem);
				UI.clearScreen();
				UI.printTabuleiro(partidaXadres.getPeca(), possiveisJogadas);

				System.out.println();

				System.out.print("Destino: ");
				XadresPosicao fim = UI.lerXadresPosica(sc);

				PecaXadres capturarPeca = partidaXadres.performaMoverPeca(origem, fim);
				
				if(capturarPeca != null) {
					capturada.add(capturarPeca);
				}
				
				if(partidaXadres.getPromocao() != null) {
					System.out.print("Escolha uma peca para promocao (T/C/B/Q): ");
					String tipo = sc.nextLine().toUpperCase();
					while (!tipo.equals("T") && !tipo.equals("C") && !tipo.equals("B") && !tipo.equals("Q")) {
						System.out.print("Peca invalida! Escolha uma peca para promocao (T/C/B/Q): ");
						tipo = sc.nextLine().toUpperCase();
					}
					partidaXadres.substituirPecaPromovida(tipo);
				}
			}
			catch (XadresException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.clearScreen();
		UI.printPartida(partidaXadres, capturada);
	}

}
