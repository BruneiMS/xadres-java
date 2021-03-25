package boardgame;

public class Tabuleiro {

	private int linha;
	private int coluna;
	private Peca [][] pecas;
	
	public Tabuleiro(int linha, int coluna) {
		if(linha < 1 || coluna < 1) {
			throw new BoardException("Erro na criacao do tabuleiro: tabuleiro tem que ter mais de 1 linha e 1 coluna!");
		}
		this.linha = linha;
		this.coluna = coluna;
		pecas = new Peca[linha][coluna];
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}
	
	public Peca pecas (int linha, int coluna) {
		if (!posicaoExistente(linha, coluna)) {
			throw new BoardException("Posicao nao existe no tabuleiro!");
		}
		return pecas[linha][coluna];
	}
	
	public Peca pecas (Posicao posicao) {
		if (!posicaoExistente(posicao)) {
			throw new BoardException("Posicao nao existe no tabuleiro!");
		}
		return pecas[posicao.getLinha()][posicao.getColuna()];
	}
	
	public void lugarPeca(Peca peca, Posicao posicao) {
		if(temUmaPeca(posicao)) {
			throw new BoardException("Ja existe uma peca nessa posicao! " + posicao);
		}
		pecas[posicao.getLinha()][posicao.getColuna()] = peca;
		peca.posicao = posicao;
	}
	
	public Peca removePeca(Posicao posicao) {
		if (!posicaoExistente(posicao)) {
			throw new BoardException("Posicao nao existe no tabuleiro!");
		} 
		if (pecas(posicao) == null) {
			return null;
		}
		Peca aux = pecas(posicao);
		aux.posicao = null;
		pecas[posicao.getLinha()][posicao.getColuna()] = null;
		return aux;
	}
	
	public boolean posicaoExistente(int lin, int col) {
		return lin >= 0 && lin < linha && col >= 0 && col < coluna;
	}
	
	public boolean posicaoExistente(Posicao posicao) {
		return posicaoExistente(posicao.getLinha(), posicao.getColuna());
	}
	
	public boolean temUmaPeca(Posicao posicao) {
		if (!posicaoExistente(posicao)) {
			throw new BoardException("Posicao nao existe no tabuleiro!");
		} 
		return pecas(posicao) != null;
	}
}
