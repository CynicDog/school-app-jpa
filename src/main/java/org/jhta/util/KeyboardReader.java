package org.jhta.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KeyboardReader {

    private BufferedReader in;
    public KeyboardReader() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    public String readString() {
        try {
            return in.readLine();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int readInt() {
        try {
            return Integer.parseInt(in.readLine().trim());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public long readLong() {
        try {
            return Long.parseLong(in.readLine().trim());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public double readDouble() {
        try {
            return Double.parseDouble(in.readLine().trim());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public float readFloat() {
        try {
            return Float.parseFloat(in.readLine().trim());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    public boolean readBoolean() {
        try {
            return Boolean.parseBoolean(in.readLine().trim());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

