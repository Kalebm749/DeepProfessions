package com.ShibuiKaleb.deepProfessions.data;

import com.ShibuiKaleb.deepProfessions.enums.Profession;
import com.ShibuiKaleb.deepProfessions.enums.Specialization;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet

public class PlayerData {

    private final UUID playerUUID;
    private Profession profession;
    private Specialization specialization;
    private final Map<Profession, Double> proficiency;
    private long lastSwitchTimestamp;
    private final Set<String> completedQuests = new HashSet<>();

    public PlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.profession = null;
        this.specialization = null;
        this.proficiency = new HashMap<>();
        this.lastSwitchTimestamp = 0;
    }

    public UUID getPlayerUUID() { return playerUUID; }

    public Set<String> getCompletedQuests() { return completedQuests; }

    public Profession getProfession() { return profession; }
    public void setProfession(Profession profession) { this.profession = profession; }

    public Specialization getSpecialization() { return specialization; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    public double getProficiency(Profession profession) {
        return proficiency.getOrDefault(profession, 0.0);
    }
    public void setProficiency(Profession profession, double value) {
        proficiency.put(profession, value);
    }

    public boolean hasCompletedQuest(String questId) {
        return completedQuests.contains(questId);
    }

    public void completeQuest(String questId) {
        completedQuests.add(questId);
    }

    public Map<Profession, Double> getAllProficiency() { return proficiency; }

    public long getLastSwitchTimestamp() { return lastSwitchTimestamp; }
    public void setLastSwitchTimestamp(long timestamp) { this.lastSwitchTimestamp = timestamp; }
}