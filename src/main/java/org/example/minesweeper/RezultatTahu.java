package org.example.minesweeper;

class RezultatTahu {
    private boolean uspesny;
    private String sprava;

    public RezultatTahu(boolean uspesny, String sprava) {
        this.uspesny = uspesny;
        this.sprava = sprava;
    }

    public boolean jeUspesny() { return uspesny; }
    public String getSprava() { return sprava; }
}
