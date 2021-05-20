package processor.memorysystem;

// CacheLine Class representing one Cache Line
public class CacheLine {

    int size;
    int arrSize;
    int[] tag, data;

    public CacheLine(int size, int arrSize) {
        this.size = size;
        this.arrSize = arrSize;
        tag = new int[this.arrSize];
        data = new int[this.arrSize];

        for (int i = 0; i < this.arrSize; ++i) {
            tag[i] = data[i] = -1;
        }
    }

    public int findIndexOf(int address) {
        for (int i = 0; i < arrSize; ++i) {
            if (tag[i] == address) {
                return i;
            }
        }
        return -1;
    }

    public void setValuesAt(int index, int address, int value) {
        tag[index] = address;
        data[index] = value;
    }

    public int getAddressAt(int index) {
        return tag[index];
    }

    public void setAddressAt(int index, int address) {
        tag[index] = address;
    }

    public int getDataAt(int index) {
        return data[index];
    }

    public void setDataAt(int index, int value) {
        data[index] = value;
    }

}
