package com.example.employees.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a department within the company.
 * Now contains a unique identifier `id` (UUID) and a `name`.
 */
public class Department {
    private final UUID id;
    private final String name;

    public Department(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() { return name; }
}