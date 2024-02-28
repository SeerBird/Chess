package game.model;

public class Attack {
    public boolean black;
    public boolean white;

    public Attack(boolean black, boolean white) {
        this.black = black;
        this.white = white;
    }

    public boolean getAttack(boolean attacker) {
        return (black && attacker) || (white && !attacker);
    }

    public void addAttack(boolean attacker) {
        if (attacker) {
            black = true;
        } else {
            white = true;
        }
    }
}
