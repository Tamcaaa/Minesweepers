package org.example.minesweeper;

abstract class HracaMapa {
    protected Pole[][] mapa;
    protected int riadky;
    protected int stlpce;
    protected int pocetMin;

    public HracaMapa(int riadky, int stlpce, int pocetMin) {
        this.riadky = riadky;
        this.stlpce = stlpce;
        this.pocetMin = pocetMin;
        this.mapa = new Pole[riadky][stlpce];
        inicializujMapu();
    }

    protected abstract void inicializujMapu();

    public Pole getPole(int riadok, int stlpec) {
        if (jeValidnaPozicia(riadok, stlpec)) {
            return mapa[riadok][stlpec];
        }
        return null;
    }

    public boolean jeValidnaPozicia(int riadok, int stlpec) {
        return riadok >= 0 && riadok < riadky && stlpec >= 0 && stlpec < stlpce;
    }

    public int getRiadky() { return riadky; }
    public int getStlpce() { return stlpce; }
    public int getPocetMin() { return pocetMin; }
}
