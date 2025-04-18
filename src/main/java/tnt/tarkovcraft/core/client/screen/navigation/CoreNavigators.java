package tnt.tarkovcraft.core.client.screen.navigation;

import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.SkillScreen;
import tnt.tarkovcraft.core.util.helper.TextHelper;

public final class CoreNavigators {

    public static final NavigationEntry SKILL_ENTRY = new SimpleNavigationEntry(
            TextHelper.createScreenTitle(TarkovCraftCore.MOD_ID, "skills"),
            SkillScreen::new,
            300
    );

    public static final DynamicNavigationProvider CHARACTER_NAVIGATION_PROVIDER = new DynamicNavigationProvider();

    static {
        CHARACTER_NAVIGATION_PROVIDER.register(SKILL_ENTRY);
    }
}
