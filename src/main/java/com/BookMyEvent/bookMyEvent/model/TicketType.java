package com.BookMyEvent.bookMyEvent.model;

public enum TicketType {
    VIP("VIP", 1),
    PREMIUM("Premium", 2),
    GENERAL("General", 3);

    private final String displayName;
    private final int priority;

    TicketType(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPriority() {
        return priority;
    }
}
