package game;

public class ScoreByCase {
	
	private final static SC[][]tab=new SC[][]{	{SC.TW, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.TW, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.TW},
												{SC.N, SC.DW, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.DW, SC.N},
												{SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N},
												{SC.DL, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.DL},
												{SC.N, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.N},
												{SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N},
												{SC.N, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.N},
												{SC.TW, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.TW},
												{SC.N, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.N},
												{SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N},
												{SC.N, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.N},
												{SC.DL, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N, SC.DL},
												{SC.N, SC.N, SC.DW, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.DW, SC.N, SC.N},
												{SC.N, SC.DW, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.TL, SC.N, SC.N, SC.N, SC.DW, SC.N},
												{SC.TW, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.N, SC.TW, SC.N, SC.N, SC.N, SC.DL, SC.N, SC.N, SC.TW}}; // c'était fun à faire
	
	public static SC findScoreCase(int x, int y) {
		return tab[x][y];
	}
	
	public enum SC {
		N,	// Nothing
		DL,	// Double_Letter
		TL,	// Triple_Letter
		DW,	// Double_Word
		TW;	// Triple_Word
	}
}
