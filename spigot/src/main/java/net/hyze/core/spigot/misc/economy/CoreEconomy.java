package net.hyze.core.spigot.misc.economy;

public interface CoreEconomy {

    public Double get(int userId);

    public void add(int userId, double value);

    public void remove(int userId, double value);

    public String format(double value);

}
