package redstonedev.permitted;

public class PermittedLang {
    public static void init() {
        Permitted.REGISTRATE.addRawLang("itemGroup.permitted", "Permitted");

        Permitted.REGISTRATE.addRawLang("permit.title", "%s Permit");
        Permitted.REGISTRATE.addRawLang("permit.title.none", "Permit");
        Permitted.REGISTRATE.addRawLang("permit.items.list", "§aPermit items:§r");
        Permitted.REGISTRATE.addRawLang("permit.items.item", "- %s");
        Permitted.REGISTRATE.addRawLang("permit.owner", "§eOwner: §b%s§r");
        Permitted.REGISTRATE.addRawLang("permit.owner.none", "§cNone§r");

        Permitted.REGISTRATE.addRawLang("permit.rarity.none", "§o§l§cNo Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.dirt", "§o§l§6Dirt Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.iron", "§o§l§fIron Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.gold", "§o§l§eGold Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.diamond", "§o§l§bDiamond Tier§r");
        Permitted.REGISTRATE.addRawLang("permit.rarity.emerald", "§o§l§aEmerald Tier§r");

        Permitted.REGISTRATE.addRawLang("permit.instructions.right_click", "§7Shift-right-click to claim this permit.§r");
        Permitted.REGISTRATE.addRawLang("permit.instructions.left_click", "§7Shift-left-click to unclaim this permit.§r");

        Permitted.REGISTRATE.addRawLang("death.attack.permit_office.waited_too_long", "The Permit Office did its job, and %1$s suffered the consequences.");
        Permitted.REGISTRATE.addRawLang("death.attack.permit_office.waited_too_long.player", "The Permit Office did its job, and %1$s suffered the consequences from %2$s.");
    }
}
