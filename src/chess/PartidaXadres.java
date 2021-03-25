package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Peca;
import boardgame.Posicao;
import boardgame.Tabuleiro;
import chess.pecas.Bispo;
import chess.pecas.Cavalo;
import chess.pecas.Peao;
import chess.pecas.Rainha;
import chess.pecas.Rei;
import chess.pecas.Torre;

public class PartidaXadres {

	private int turno;
	private Cor jogadorCorrespondente;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaXadres naJogadaVulneravel;
	private PecaXadres promocao;

	private List<Peca> pecaNoTabuleiro = new ArrayList<Peca>();
	private List<Peca> pecaCapturadas = new ArrayList<Peca>();

	public PartidaXadres() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorCorrespondente = Cor.BRANCO;
		configuracaoInicial();
	}

	public PecaXadres[][] getPeca(){
		PecaXadres[][] mat = new PecaXadres[tabuleiro.getLinha()][tabuleiro.getColuna()];
		for (int i=0; i<tabuleiro.getLinha(); i++) {
			for (int j=0; j<tabuleiro.getColuna(); j++) {
				mat[i][j] = (PecaXadres) tabuleiro.pecas(i, j);
			}
		}
		return mat;
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorCorrespondente() {
		return jogadorCorrespondente;
	}

	public boolean getCheck() {
		return check;
	}
	public boolean getCheckMate() {
		return checkMate;
	}

	public PecaXadres getNaJogadaVulneravel() {
		return naJogadaVulneravel;
	}

	public PecaXadres getPromocao() {
		return promocao;
	}

	public boolean[][] possibilidadesDeMover(XadresPosicao origemPosicao){
		Posicao posicao = origemPosicao.toPosicao();
		validarPosicaoOrigem(posicao);
		return tabuleiro.pecas(posicao).possivelMover();
	}

	public PecaXadres performaMoverPeca(XadresPosicao posicaoOrigem, XadresPosicao posicaoFinal) {
		Posicao origem = posicaoOrigem.toPosicao();
		Posicao fim = posicaoFinal.toPosicao();
		validarPosicaoOrigem(origem);
		validarPosicaoFinal(origem, fim);
		Peca capturarPeca = moverPeca(origem, fim);

		if(testeCheck(jogadorCorrespondente)) {
			desfazerMovimento(origem, fim, capturarPeca);
			throw new XadresException("Voce nao pode fazer essa jogada porque voce vai se por em check.");
		}

		PecaXadres movePeca = (PecaXadres)tabuleiro.pecas(fim);

		// jogada promoção
		promocao = null;
		if(movePeca instanceof Peao) {
			if ((movePeca.getCor() == Cor.BRANCO && fim.getLinha() == 0) || (movePeca.getCor() == Cor.PRETO && fim.getLinha() == 7)) {
				promocao = (PecaXadres)tabuleiro.pecas(fim);
				promocao = substituirPecaPromovida("§");
			}
		}

		check = (testeCheck(adversario(jogadorCorrespondente))) ? true : false;

		if(testeCheckMate(adversario(jogadorCorrespondente))) {
			checkMate = true;
		} else {
			trocaDeTurno();
		}

		// jogada enPassantt
		if(movePeca instanceof Peao && (origem.getLinha() == fim.getLinha() - 2 || origem.getLinha() == fim.getLinha() + 2)) {
			naJogadaVulneravel = movePeca;
		} else {
			naJogadaVulneravel = null;
		}

		return (PecaXadres)capturarPeca;
	}

	public PecaXadres substituirPecaPromovida (String tipo) {
		if (promocao == null) {
			throw new IllegalStateException("Nao tem peca para a promocao.");
		}
		if (!tipo.equals("T") && !tipo.equals("C") && !tipo.equals("B") && !tipo.equals("Q")) {
			return promocao;
		}

		Posicao pos = promocao.getXadresPosicao().toPosicao();
		Peca p = tabuleiro.removePeca(pos);
		pecaNoTabuleiro.remove(p);

		PecaXadres novaPeca = novaPeca(tipo, promocao.getCor());
		tabuleiro.lugarPeca(novaPeca, pos);
		pecaNoTabuleiro.add(novaPeca);

		return novaPeca;
	}

	private PecaXadres novaPeca(String tipo, Cor cor) {
		if(tipo.equals("T")) return new Torre(tabuleiro, cor);
		if(tipo.equals("C")) return new Cavalo(tabuleiro, cor);
		if(tipo.equals("B")) return new Bispo(tabuleiro, cor);
		return new Rainha(tabuleiro, cor);
	}

	private Peca moverPeca(Posicao origem, Posicao fim) {
		PecaXadres p = (PecaXadres)tabuleiro.removePeca(origem);
		p.incrementarContJogada();
		Peca capturarPeca = tabuleiro.removePeca(fim);
		tabuleiro.lugarPeca(p, fim);

		if (capturarPeca != null) {
			pecaNoTabuleiro.remove(capturarPeca);
			pecaCapturadas.add(capturarPeca);
		}

		// jogada roque pequeno
//		if(p instanceof Rei && fim.getColuna() == origem.getColuna() + 2) {
//			Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
//			Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
//			PecaXadres rei = (PecaXadres)tabuleiro.removePeca(oringemT);
//			tabuleiro.lugarPeca(rei, fimT);
//			rei.incrementarContJogada();
//		}
		if (p instanceof Rei && p.getCor() == Cor.BRANCO) {
			if(p instanceof Rei && fim.getColuna() == origem.getColuna() + 2) {
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(oringemT);
				tabuleiro.lugarPeca(rei, fimT);
				rei.incrementarContJogada();
			} else if(p instanceof Rei && fim.getColuna() == origem.getColuna() - 2) {
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(oringemT);
				tabuleiro.lugarPeca(rei, fimT);
				rei.incrementarContJogada();
			}
		}

		// jogada roque grande
//		if(p instanceof Rei && fim.getColuna() == origem.getColuna() - 2) {
//			Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
//			Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
//			PecaXadres rei = (PecaXadres)tabuleiro.removePeca(oringemT);
//			tabuleiro.lugarPeca(rei, fimT);
//			rei.incrementarContJogada();
//		}
		if (p instanceof Rei && p.getCor() == Cor.BRANCO) {
			if(p instanceof Rei && fim.getColuna() == origem.getColuna() - 2) {
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(oringemT);
				tabuleiro.lugarPeca(rei, fimT);
				rei.incrementarContJogada();
			} else if(p instanceof Rei && fim.getColuna() == origem.getColuna() + 2) {
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(oringemT);
				tabuleiro.lugarPeca(rei, fimT);
				rei.incrementarContJogada();
			}
		}

		// jogada en Passant
		if(p instanceof Peao) {
			if(origem.getColuna() != fim.getColuna() && capturarPeca == null) {
				Posicao peaoPosicao;
				if(p.getCor() == Cor.BRANCO) {
					peaoPosicao = new Posicao(fim.getLinha() + 1, fim.getColuna());
				} else {
					peaoPosicao = new Posicao(fim.getLinha() - 1, fim.getColuna());
				}
				capturarPeca = tabuleiro.removePeca(peaoPosicao);
				pecaCapturadas.add(capturarPeca);
				pecaNoTabuleiro.remove(capturarPeca);
			}
		}

		return capturarPeca;
	}

	private void desfazerMovimento(Posicao origem, Posicao fim, Peca capturarPeca) {
		PecaXadres p = (PecaXadres)tabuleiro.removePeca(fim);
		p.decrementarContJogada();
		tabuleiro.lugarPeca(p, origem);

		if(capturarPeca != null) {
			tabuleiro.lugarPeca(capturarPeca, fim);
			pecaCapturadas.remove(capturarPeca);
			pecaNoTabuleiro.add(capturarPeca);
		}

		// jogada roque pequeno
//		if(p instanceof Rei && fim.getColuna() == origem.getColuna() + 2) {
//			Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
//			Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
//			PecaXadres rei = (PecaXadres)tabuleiro.removePeca(fimT);
//			tabuleiro.lugarPeca(rei, oringemT);
//			rei.incrementarContJogada();
//		}
		if (p instanceof Rei && p.getCor() == Cor.BRANCO) {
			if(p instanceof Rei && fim.getColuna() == origem.getColuna() + 2) {
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(fimT);
				tabuleiro.lugarPeca(rei, oringemT);
				rei.incrementarContJogada();
			} else if(p instanceof Rei && fim.getColuna() == origem.getColuna() - 2){
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(fimT);
				tabuleiro.lugarPeca(rei, oringemT);
				rei.incrementarContJogada();
			}
		}

		// jogada roque grande
//		if(p instanceof Rei && fim.getColuna() == origem.getColuna() - 2) {
//			Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
//			Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
//			PecaXadres rei = (PecaXadres)tabuleiro.removePeca(fimT);
//			tabuleiro.lugarPeca(rei, oringemT);
//			rei.incrementarContJogada();
//		}
		if (p instanceof Rei && p.getCor() == Cor.BRANCO) {
			if(p instanceof Rei && fim.getColuna() == origem.getColuna() - 2) {
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(fimT);
				tabuleiro.lugarPeca(rei, oringemT);
				rei.incrementarContJogada();
			} else if(p instanceof Rei && fim.getColuna() == origem.getColuna() + 2) {
				Posicao oringemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
				Posicao fimT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
				PecaXadres rei = (PecaXadres)tabuleiro.removePeca(fimT);
				tabuleiro.lugarPeca(rei, oringemT);
				rei.incrementarContJogada();
			}
		}

		// jogada en Passant
		if(p instanceof Peao) {
			if(origem.getColuna() != fim.getColuna() && capturarPeca == naJogadaVulneravel) {
				PecaXadres peao = (PecaXadres)tabuleiro.removePeca(fim);
				Posicao peaoPosicao;
				if(p.getCor() == Cor.BRANCO) {
					peaoPosicao = new Posicao(3, origem.getColuna());
				} else {
					peaoPosicao = new Posicao(4, origem.getColuna());
				}
				tabuleiro.lugarPeca(peao, peaoPosicao);
			}
		}
	}

	private void validarPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadresException("Nao ha peca na posicao.");
		}
		if (jogadorCorrespondente != ((PecaXadres)tabuleiro.pecas(posicao)).getCor()) {
			throw new XadresException("Essa peca nao e sua.");
		}
		if (!tabuleiro.pecas(posicao).possivelPeloMenosUmaJogada()) {
			throw new XadresException("Nao ha jogada possivel.");
		}
	}

	private void validarPosicaoFinal(Posicao origem, Posicao fim) {
		if(!tabuleiro.pecas(origem).possivelMover(fim)) {
			throw new XadresException("A peca escolhida nao pode se mover para a posicao destino.");
		}
	}

	private void trocaDeTurno() {
		turno ++;
		jogadorCorrespondente = (jogadorCorrespondente == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private Cor adversario(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private PecaXadres rei(Cor cor) {
		List<Peca> lista = pecaNoTabuleiro.stream().filter(x -> ((PecaXadres)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : lista) {
			if (p instanceof Rei) {
				return (PecaXadres)p;
			}
		}
		throw new IllegalStateException("Nao existe o rei " + cor + " no tabuleiro." );
	}

	private boolean testeCheck(Cor cor) {
		Posicao posicaoRei = rei(cor).getXadresPosicao().toPosicao();
		List<Peca> pecaAdversario = pecaNoTabuleiro.stream().filter(x -> ((PecaXadres)x).getCor() == adversario(cor)).collect(Collectors.toList()); 
		for (Peca p : pecaAdversario) {
			boolean[][] mat = p.possivelMover();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testeCheckMate(Cor cor) {
		if(!testeCheck(cor)) {
			return false;
		}
		List<Peca> lista = pecaNoTabuleiro.stream().filter(x -> ((PecaXadres)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : lista) {
			boolean[][]mat = p.possivelMover();
			for(int i=0; i<tabuleiro.getLinha(); i++) {
				for(int j=0; j<tabuleiro.getColuna(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaXadres)p).getXadresPosicao().toPosicao();
						Posicao fim = new Posicao(i, j);
						Peca capturarPeca = moverPeca(origem, fim);
						boolean testeCheck = testeCheck(cor);
						desfazerMovimento(origem, fim, capturarPeca);
						if (!testeCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void coloqueNovaPeca(char coluna, int linha, PecaXadres peca) {
		tabuleiro.lugarPeca(peca, new XadresPosicao(coluna, linha).toPosicao());
		pecaNoTabuleiro.add(peca);
	}

	private void configuracaoInicial() {
		coloqueNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		coloqueNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		coloqueNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));	
		coloqueNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));
		coloqueNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		coloqueNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		coloqueNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		coloqueNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		coloqueNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));

		coloqueNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		coloqueNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		coloqueNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		coloqueNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		coloqueNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		coloqueNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		coloqueNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		coloqueNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		coloqueNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));	
	}
}
