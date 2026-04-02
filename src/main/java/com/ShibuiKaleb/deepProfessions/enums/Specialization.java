package com.ShibuiKaleb.deepProfessions.enums;

public enum Specialization {

    // Miner
    GEMCUTTER(Profession.MINER),
    EXCAVATOR(Profession.MINER),

    // Lumberjack
    CARPENTER(Profession.LUMBERJACK),
    ARBORIST(Profession.LUMBERJACK),

    // Farmer
    AGRICULTURIST(Profession.FARMER),
    HERBALIST(Profession.FARMER),

    // Hunter
    TRACKER(Profession.HUNTER),
    TRAPPER(Profession.HUNTER),

    // Fisher
    ANGLER(Profession.FISHER),
    AQUACULTURIST(Profession.FISHER),

    // Blacksmith
    QUARTERMASTER(Profession.BLACKSMITH),
    FORGER(Profession.BLACKSMITH),

    // Alchemist
    ENHANCER(Profession.CHEMIST),
    ARTISAN(Profession.CHEMIST),

    // Chef
    BAKER(Profession.CHEF),
    GOURMET(Profession.CHEF),

    // Tailor
    LEATHERWORKER(Profession.TAILOR),
    WEAVER(Profession.TAILOR),

    // Arcanist
    CONDUIT(Profession.ARCANIST),
    FOCUS(Profession.ARCANIST);

    private final Profession parentProfession;

    Specialization(Profession parentProfession) {
        this.parentProfession = parentProfession;
    }

    public Profession getParentProfession() {
        return parentProfession;
    }
}