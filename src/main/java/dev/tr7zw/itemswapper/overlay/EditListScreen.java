package dev.tr7zw.itemswapper.overlay;

import java.util.List;

import dev.tr7zw.itemswapper.config.CacheManager;
import dev.tr7zw.util.ComponentProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

//spotless:off 
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
//spotless:on
//spotless:off
//#if MC >= 12100
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
//#else
//$$ import net.minecraft.client.gui.screens.OptionsSubScreen;
//#endif
//spotless:on

public class EditListScreen extends OptionsSubScreen {
    private EntrySelectionList selectionList;
    private final boolean whitelist;

    public EditListScreen(final Screen screen, final Options options, boolean whitelist) {
        super(screen, options, whitelist ? ComponentProvider.translatable("text.itemswapper.whitelist")
                : ComponentProvider.translatable("text.itemswapper.blacklist"));
        this.whitelist = whitelist;
    }

    protected void init() {
        this.addWidget((this.selectionList = new EntrySelectionList(this.minecraft)));
        this.addRenderableWidget(Button.builder(ComponentProvider.translatable("text.itemswapper.remove"), button -> {
            EntrySelectionList.ListEntry entry = this.selectionList.getSelected();
            if (entry == null) {
                return;
            }
            if (whitelist) {
                CacheManager.getInstance().getCache().enableOnIp.remove(entry.string);
            } else {
                CacheManager.getInstance().getCache().disableOnIp.remove(entry.string);
            }
            CacheManager.getInstance().writeConfig();
            this.selectionList.remove(entry);
        }).bounds(this.width / 2 - 155, this.height - 38, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 155 + 160, this.height - 38, 150, 20).build());
        super.init();
    }

    // spotless:off
    //#if MC >= 12100
    @Override
    protected void addOptions() {

    }
    //#endif
    //spotless:on

    // spotless:off 
  //#if MC >= 12000
    public void render(final GuiGraphics graphics, final int i, final int j, final float f) {
        RenderContext renderContext = new RenderContext(graphics);
    //#else
    //$$     public void render(final PoseStack graphics, final int i, final int j, final float f) {
    //$$       RenderContext renderContext = new RenderContext(this, graphics);
    //#endif
    // spotless:on
        this.selectionList.render(graphics, i, j, f);
        renderContext.drawCenteredString(this.font, this.title, this.width / 2, 16, 16777215);
        super.render(graphics, i, j, f);
    }

    @Environment(EnvType.CLIENT)
    private class EntrySelectionList
            extends ObjectSelectionList<dev.tr7zw.itemswapper.overlay.EditListScreen.EntrySelectionList.ListEntry> {
        public EntrySelectionList(final Minecraft minecraft) {
            // spotless:off 
          //#if MC >= 12003
            super(minecraft, EditListScreen.this.width, EditListScreen.this.height - 93, 32, 18);
          //#else
          //$$ super(minecraft, EditListScreen.this.width, EditListScreen.this.height, 32,
          //$$  -                    EditListScreen.this.height - 65 + 4, 18);
          //#endif
          //spotless:on
            List<String> ips = whitelist ? CacheManager.getInstance().getCache().enableOnIp
                    : CacheManager.getInstance().getCache().disableOnIp;
            ips.forEach((string) -> {
                addEntry(new ListEntry(string));
            });
        }

        public void remove(ListEntry entry) {
            super.removeEntry(entry);
        }

        //#if MC >= 12104
        public double scrollAmount() {
            return super.scrollAmount() + 20;
        }
        //#else
        //$$protected int getScrollbarPosition() {
        //$$    return super.getScrollbarPosition() + 20;
        //$$}
        //#endif

        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Environment(EnvType.CLIENT)
        public class ListEntry extends ObjectSelectionList.Entry<ListEntry> {
            final String string;
            private final Component text;

            public ListEntry(final String string) {
                this.string = string;
                this.text = ComponentProvider.literal(string);
            }

            // spotless:off 
            //#if MC >= 12000
            public void render(final GuiGraphics graphics, final int i, final int j, final int k, final int l,
                    final int m, final int n, final int o, final boolean bl, final float f) {
                RenderContext renderContext = new RenderContext(graphics);
            //#else
            //$$     public void render(final PoseStack graphics, final int i, final int j, final int k, final int l,
            //$$      final int m, final int n, final int o, final boolean bl, final float f) {
            //$$       RenderContext renderContext = new RenderContext(EditListScreen.this, graphics);
            //#endif
            // spotless:on
                renderContext.drawString(EditListScreen.this.font, this.text,
                        (int) (EntrySelectionList.this.width / 2
                                - EditListScreen.this.font.width((FormattedText) this.text) / 2),
                        (int) (j + 1), 16777215);
            }

            public boolean mouseClicked(final double d, final double e, final int i) {
                if (i == 0) {
                    this.select();
                    return true;
                }
                return false;
            }

            private void select() {
                setSelected(this);
            }

            public Component getNarration() {
                return text;
            }
        }
    }
}