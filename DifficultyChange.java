package com.jousafio.difficulty;

public class DifficultyChange {
    private final String id;
    private final String name;
    private final String description;
    private final Runnable action;

    public DifficultyChange(String id, String name, String description, Runnable action) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.action = action;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void apply() { if (action != null) action.run(); }
}
