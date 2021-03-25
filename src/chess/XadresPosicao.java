package chess;

import boardgame.Posicao;

public class XadresPosicao {

	private char coluna;
	private int linha;
	
	public XadresPosicao(char coluna, int linha) {
		if (coluna < 'a' || coluna > 'h' || linha < 1 || linha > 8) {
			throw new XadresException("Erro na inicializacao do Xadres Posicao. Validos somente as posicoes do a1 ate h8.");
		}
		this.coluna = coluna;
		this.linha = linha;
	}

	public char getColuna() {
		return coluna;
	}

	public int getLinha() {
		return linha;
	}
	
	protected Posicao toPosicao() {
		return new Posicao(8 - linha, coluna - 'a');
	}
	
	protected static XadresPosicao fromPosicao(Posicao posicao) {
		return new XadresPosicao((char)('a' + posicao.getColuna()), 8 - posicao.getLinha());
	}

	@Override
	public String toString() {
		return "" + coluna + linha;
	}
	
	
}
