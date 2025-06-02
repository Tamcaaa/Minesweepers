package org.example.minesweeper;

class MinyMapa extends HracaMapa {

    public MinyMapa(int riadky, int stlpce, int pocetMin) {
        super(riadky, stlpce, pocetMin);
        rozmiestniminy();
        vypocitajSusedneMiny();
    }

    @Override
    protected void inicializujMapu() {
        for (int i = 0; i < riadky; i++) {
            for (int j = 0; j < stlpce; j++) {
                mapa[i][j] = new Pole(i, j);
            }
        }
    }

    private void rozmiestniminy() {
        int umiestneneMin = 0;
        java.util.Random random = new java.util.Random();

        while (umiestneneMin < pocetMin) {
            int riadok = random.nextInt(riadky);
            int stlpec = random.nextInt(stlpce);

            if (!mapa[riadok][stlpec].jeMina()) {
                mapa[riadok][stlpec].setMina(true);
                umiestneneMin++;
            }
        }
    }

    private void vypocitajSusedneMiny() {
        for (int i = 0; i < riadky; i++) {
            for (int j = 0; j < stlpce; j++) {
                if (!mapa[i][j].jeMina()) {
                    int pocet = 0;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            int ni = i + di;
                            int nj = j + dj;
                            if (jeValidnaPozicia(ni, nj) && mapa[ni][nj].jeMina()) {
                                pocet++;
                            }
                        }
                    }
                    mapa[i][j].setPocetSusednychMin(pocet);
                }
            }
        }
    }
}


