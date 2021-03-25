package chess;

import boardgame.Peca;
import boardgame.Posicao;
import boardgame.Tabuleiro;

public abstract class PecaXadres extends Peca{

	private Cor cor;
	private int contJogada;

	public PecaXadres(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}
	
	public int getContJogada() {
		return contJogada;
	}
	
	public void incrementarContJogada() {
		contJogada ++;
	}
	
	public void decrementarContJogada() {
		contJogada --;
	}
	
	public XadresPosicao getXadresPosicao() {
		return XadresPosicao.fromPosicao(posicao);
	}

	protected boolean pecaDoOponente(Posicao posicao) {
		PecaXadres p = (PecaXadres)getTabuleiro().pecas(posicao);
		return p != null && p.getCor() != cor;
	}
}
