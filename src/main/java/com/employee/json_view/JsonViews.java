package com.employee.json_view;

public class JsonViews {
    public static class Basic {}                    // Minimal data
    public static class Detailed extends Basic {}   // + nested simple objects
    public static class Full extends Detailed {}    // + full nested collections
}
