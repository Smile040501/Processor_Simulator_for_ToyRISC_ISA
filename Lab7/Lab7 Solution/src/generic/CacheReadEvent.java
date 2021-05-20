package generic;

// CacheReadEvent to generate a event of reading data from Cache
public class CacheReadEvent extends Event {

    int addressToReadFrom; // Address which we want to read

    // Constructor
    public CacheReadEvent(long eventTime, Element requestingElement, Element processingElement,
            int address) {
        super(eventTime, EventType.CacheRead, requestingElement, processingElement);
        this.addressToReadFrom = address;
    }

    public int getAddressToReadFrom() {
        return addressToReadFrom;
    }

    public void setAddressToReadFrom(int addressToReadFrom) {
        this.addressToReadFrom = addressToReadFrom;
    }
}
