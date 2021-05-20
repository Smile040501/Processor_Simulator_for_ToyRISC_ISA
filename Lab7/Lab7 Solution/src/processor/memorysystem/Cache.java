package processor.memorysystem;

import configuration.Configuration;
import generic.CacheReadEvent;
import generic.CacheResponseEvent;
import generic.CacheWriteEvent;
import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import processor.Clock;
import processor.Processor;

// Cache class that implements Element class
public class Cache implements Element {

    Processor containingProcessor;
    int cacheType; // 0 - L1i_cache | 1 - L1d_cache
    int NUM_CACHE_LINES;
    int CACHE_LATENCY;
    int CACHE_LINE_SIZE;
    int CACHE_LINE_ARRAY_SIZE;
    int CACHE_SIZE;
    CacheLine[] buffer;
    int currIndex;
    boolean isCacheBusy;

    // Constructor
    public Cache(Processor containingProcessor, int cacheType) {
        this.containingProcessor = containingProcessor;
        this.cacheType = cacheType;
        if (this.cacheType == 0) {
            this.NUM_CACHE_LINES = Configuration.L1i_numberOfLines;
            this.CACHE_LATENCY = Configuration.L1i_latency;
        } else {
            this.NUM_CACHE_LINES = Configuration.L1d_numberOfLines;
            this.CACHE_LATENCY = Configuration.L1d_latency;
        }
        this.CACHE_LINE_SIZE = Configuration.CACHE_LINE_SIZE;
        this.CACHE_LINE_ARRAY_SIZE = Configuration.CACHE_LINE_SIZE / Configuration.INSTRUCTION_SIZE;
        this.CACHE_SIZE = this.CACHE_LINE_SIZE * this.NUM_CACHE_LINES;

        this.buffer = new CacheLine[this.NUM_CACHE_LINES];
        for (int i = 0; i < this.NUM_CACHE_LINES; ++i) {
            this.buffer[i] = null;
        }

        this.currIndex = 0;
        this.isCacheBusy = false;
    }

    public boolean isCacheBusy() {
        return isCacheBusy;
    }

    public void setCacheBusy(boolean isCacheBusy) {
        this.isCacheBusy = isCacheBusy;
    }

    // Cache Read Function
    public boolean cacheRead(int address, Element processingElement) {
        // System.out.println("Reading Cache"); // TEST
        for (int i = 0; i < NUM_CACHE_LINES && buffer[i] != null; ++i) {
            int index = buffer[i].findIndexOf(address);
            if (index != -1) {
                // System.out.println("Cache Hit! | Scheduling Event"); // TEST
                Simulator.getEventQueue()
                        .addEvent(new CacheResponseEvent(Clock.getCurrentTime() + CACHE_LATENCY,
                                this, processingElement, buffer[i].getDataAt(index)));
                isCacheBusy = true;
                return true;
            }
        }
        handleCacheMiss(address);
        return false;
    }

    // Cache Write Function
    public void cacheWrite(int address, int value, Element processingElement) {
        // System.out.println("Writing Cache\n"); // TEST

        Simulator.getEventQueue().addEvent(new MemoryWriteEvent(Clock.getCurrentTime(), this,
                containingProcessor.getMainMemory(), address, value));

        Simulator.getEventQueue()
                .addEvent(new CacheResponseEvent(
                        Clock.getCurrentTime() + CACHE_LATENCY + Configuration.mainMemoryLatency,
                        this, processingElement, value));
    }

    // handleCacheMiss Function
    public void handleCacheMiss(int address) {
        // System.out.println("Cache Miss\n"); // TEST
        if (containingProcessor.getMainMemory().isMainBusy()) {
            // System.out.println("Postponing Event | Main Memory Busy\n"); // TEST
            return;
        }
        // System.out.println("Cache Reading Main Memory\n"); // TEST
        Simulator.getEventQueue().addEvent(new MemoryReadEvent(Clock.getCurrentTime(), this,
                containingProcessor.getMainMemory(), address));
        isCacheBusy = true;
    }

    // handleResponse Function
    public void handleResponse(int address) {
        // System.out.println("Handling Response From Main Memory"); // TEST
        boolean isPresent = false;
        for (int i = 0; i < NUM_CACHE_LINES && buffer[i] != null; ++i) {
            int index = buffer[i].findIndexOf(address);
            if (index != -1) {
                // System.out.println("Updating CacheLine"); // TEST
                buffer[i].setDataAt(index, containingProcessor.getMainMemory().getWord(address));
                isPresent = true;
            }
        }
        if (!isPresent) {
            // System.out.println("Creating CacheLine"); // TEST
            buffer[currIndex] = new CacheLine(CACHE_LINE_SIZE, CACHE_LINE_ARRAY_SIZE);
            for (int i = 0; i < CACHE_LINE_ARRAY_SIZE && ((address + i) < containingProcessor
                    .getMainMemory().MAIN_MEMORY_SIZE); ++i) {
                buffer[currIndex].setValuesAt(i, address + i,
                        containingProcessor.getMainMemory().getWord(address + i));
            }
            currIndex = (currIndex + 1) % NUM_CACHE_LINES;
        }
    }

    // Implementing handleEvent Function as it implements Element class
    @Override
    public void handleEvent(Event e) {
        // System.out.println("Cache " + cacheType + " Event Detected: " + e); // TEST
        if (e.getEventType() == Event.EventType.CacheRead) {
            CacheReadEvent event = (CacheReadEvent) e;
            // System.out.println("Event Triggered in Cache: \n" + event); // TEST
            boolean cacheHit = cacheRead(event.getAddressToReadFrom(), e.getRequestingElement());
            if (!cacheHit) {
                // System.out.println("Rescheduling Event\n"); // TEST
                e.setEventTime(Clock.getCurrentTime() + 1);
                Simulator.getEventQueue().addEvent(e);
            }

        } else if (e.getEventType() == Event.EventType.CacheWrite) {
            if (containingProcessor.getMainMemory().isMainBusy()) {
                // System.out.println("CacheWrite Event Postponed | Main Memory Busy\n"); // TEST
                e.setEventTime(Clock.getCurrentTime() + 1);
                Simulator.getEventQueue().addEvent(e);
            } else {
                CacheWriteEvent event = (CacheWriteEvent) e;
                // System.out.println("Event Triggered in Cache: \n" + event); // TEST
                cacheWrite(event.getAddressToWriteTo(), event.getValue(),
                        event.getRequestingElement());
                isCacheBusy = true;
            }

        } else if (e.getEventType() == Event.EventType.MemoryResponse) {
            MemoryResponseEvent event = (MemoryResponseEvent) e;
            // System.out.println("Event Triggered in Cache: \n" + event); // TEST
            handleResponse(event.getAddress());
            containingProcessor.getMainMemory().setMainBusy(false);
        }
    }

}
