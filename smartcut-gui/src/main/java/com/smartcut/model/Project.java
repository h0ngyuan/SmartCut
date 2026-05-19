package com.smartcut.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Project {

    private final UUID id;
    private final String name;
    private final List<VideoTask> tasks;

    public Project(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public void addTask(VideoTask task) {
        this.tasks.add(task);
    }

    public List<VideoTask> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
