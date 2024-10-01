package net.hyze.core.shared.contracts;

public interface Provider<T> {
    
    public void prepare();
    
    public T provide();
    
    public void shut();
}
