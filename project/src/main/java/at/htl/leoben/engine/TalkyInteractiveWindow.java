package at.htl.leoben.engine;

public abstract class TalkyInteractiveWindow<T> extends InteractiveWindow {
    private T exchangeableData;

    protected void setExchangeableData(T data) {
        this.exchangeableData = data;
    }

    public T getExchangeableData() {
        return exchangeableData;
    }
}
