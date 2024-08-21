package redstonedev.permitted;

public class PermittedLang {
    public static void init() {
        Permitted.REGISTRATE.addRawLang("itemGroup.permitted", "Permitted");

        Permitted.REGISTRATE.addRawLang("permit.title", "%s Permit");
        Permitted.REGISTRATE.addRawLang("permit.items.joiner", ", ");
        Permitted.REGISTRATE.addRawLang("permit.items.joiner_and", " and ");
        Permitted.REGISTRATE.addRawLang("permit.items.joiner_last", ", and ");

        Permitted.REGISTRATE.addRawLang("permit.rarity.none", "§o§l§cNo Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.dirt", "§o§l§6Dirt Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.iron", "§o§l§fIron Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.gold", "§o§l§eGold Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.diamond", "§o§l§bDiamond Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.emerald", "§o§l§aEmerald Tier§r");
    }
}
