package boardgame;

public abstract class Peca {

	protected Posicao posicao;
	private Tabuleiro tabuleiro;

	public Peca(Tabuleiro tabuleiro) {
		this.tabuleiro = tabuleiro;
		posicao = null;
	}

	protected Tabuleiro getTabuleiro() {
		return tabuleiro;
	}

	public abstract boolean[][] possivelMover();

	public boolean possivelMover(Posicao posicao) {
		return possivelMover()[posicao.getLinha()][posicao.getColuna()];
	}

	public boolean possivelPeloMenosUmaJogada() {
		boolean[][] mat = possivelMover();
		for (int i=0; i<mat.length; i++ ) {
			for (int j=0; j<mat.length;j++) {
				if(mat[i][j]) {
					return true;
				}
			}
		}
		return false;
	}
}
