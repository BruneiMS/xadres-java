package chess.pecas;

import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.Cor;
import chess.PartidaXadres;
import chess.PecaXadres;

public class Peao extends PecaXadres {

	private PartidaXadres partidaXadres;


	public Peao(Tabuleiro tabuleiro, Cor cor, PartidaXadres partidaXadres) {
		super(tabuleiro, cor);
		this.partidaXadres = partidaXadres;
	}

	@Override
	public String toString() {
		return "P";
	}

	@Override
	public boolean[][] possivelMover() {
		boolean[][] mat = new boolean [getTabuleiro().getLinha()][getTabuleiro().getColuna()];

		Posicao p = new Posicao(0, 0);

		if(getCor() == Cor.BRANCO) {

			//Peça Branca mover 1 casa para cima
			p.setValor(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) && !getTabuleiro().temUmaPeca(p)){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			//Peça Branca se for a 1 jogada pode mover 2 casa para cima
			p.setValor(posicao.getLinha() - 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) && !getTabuleiro().temUmaPeca(p) && getTabuleiro().posicaoExistente(p2) 
					&& !getTabuleiro().temUmaPeca(p2) && getContJogada() == 0){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			//Peça Branca se tiver adversario pode comer a peça na diagonal cima esquerda
			p.setValor(posicao.getLinha() - 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExistente(p) && pecaDoOponente(p)){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			//Peça Branca se tiver adversario pode comer a peça na diagonal cima direita
			p.setValor(posicao.getLinha() - 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExistente(p) && pecaDoOponente(p)){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			// jogada en Passant peça branca
			if(posicao.getLinha() == 3) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if(getTabuleiro().posicaoExistente(esquerda) && pecaDoOponente(esquerda) && getTabuleiro().pecas(esquerda) == partidaXadres.getNaJogadaVulneravel()) {
					mat[esquerda.getLinha() - 1][esquerda.getColuna()] = true;
				}
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if(getTabuleiro().posicaoExistente(direita) && pecaDoOponente(direita) && getTabuleiro().pecas(direita) == partidaXadres.getNaJogadaVulneravel()) {
					mat[direita.getLinha() - 1][direita.getColuna()] = true;
				}
			}
		} else {
			//Peça Preta mover 1 casa para cima
			p.setValor(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) && !getTabuleiro().temUmaPeca(p)){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			//Peça Preta se for a 1 jogada pode mover 2 casa para cima
			p.setValor(posicao.getLinha() + 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExistente(p) && !getTabuleiro().temUmaPeca(p) && getTabuleiro().posicaoExistente(p2) 
					&& !getTabuleiro().temUmaPeca(p2) && getContJogada() == 0){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			//Peça Preta se tiver adversario pode comer a peça na diagonal cima esquerda
			p.setValor(posicao.getLinha() + 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExistente(p) && pecaDoOponente(p)){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			//Peça Preta se tiver adversario pode comer a peça na diagonal cima direita
			p.setValor(posicao.getLinha() + 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExistente(p) && pecaDoOponente(p)){
				mat[p.getLinha()][p.getColuna()] = true;
			}

			// jogada en Passant peça preto
			if(posicao.getLinha() == 4) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if(getTabuleiro().posicaoExistente(esquerda) && pecaDoOponente(esquerda) && getTabuleiro().pecas(esquerda) == partidaXadres.getNaJogadaVulneravel()) {
					mat[esquerda.getLinha() + 1][esquerda.getColuna()] = true;
				}
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if(getTabuleiro().posicaoExistente(direita) && pecaDoOponente(direita) && getTabuleiro().pecas(direita) == partidaXadres.getNaJogadaVulneravel()) {
					mat[direita.getLinha() + 1][direita.getColuna()] = true;
				}
			}
		}
		return mat;
	}
}
