package core;
public interface AeroportLogger {
    void log(String message);
    void setPisteEtat(int index, boolean occupe);
    void setPorteEtat(int index, boolean occupe);
    void setAvionEtat(int index, boolean waiting);
}
