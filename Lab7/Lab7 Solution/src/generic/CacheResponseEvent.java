package generic;

public class CacheResponseEvent extends Event {

    int value;

    public CacheResponseEvent(long eventTime, Element requestingElement, Element processingElement,
            int value) {
        super(eventTime, EventType.CacheResponse, requestingElement, processingElement);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
