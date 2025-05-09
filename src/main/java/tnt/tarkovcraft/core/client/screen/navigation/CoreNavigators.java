package tnt.tarkovcraft.core.client.screen.navigation;

import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.SkillScreen;
import tnt.tarkovcraft.core.client.screen.StatisticsScreen;
import tnt.tarkovcraft.core.util.helper.TextHelper;

public final class CoreNavigators {

    public static final NavigationEntry STATISTICS_ENTRY = new SimpleNavigationEntry(
            TextHelper.createScreenTitle(TarkovCraftCore.MOD_ID, "statistics"),
            StatisticsScreen::new,
            10
    );
    public static final NavigationEntry SKILL_ENTRY = new OptionalNavigationEntry(
            TextHelper.createScreenTitle(TarkovCraftCore.MOD_ID, "skills"),
            ctx -> TarkovCraftCore.getConfig().skillSystemConfig.skillSystemEnabled,
            SkillScreen::new,
            100
    );

    public static final DynamicNavigationProvider CHARACTER_NAVIGATION_PROVIDER = new DynamicNavigationProvider();

    static {
        CHARACTER_NAVIGATION_PROVIDER.register(STATISTICS_ENTRY);
        CHARACTER_NAVIGATION_PROVIDER.register(SKILL_ENTRY);
    }
}
