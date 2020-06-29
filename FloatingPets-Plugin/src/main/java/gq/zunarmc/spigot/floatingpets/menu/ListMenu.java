package gq.zunarmc.spigot.floatingpets.menu;

import gq.zunarmc.spigot.floatingpets.util.ItemBuilder;
import gq.zunarmc.spigot.floatingpets.menu.model.Menu;
import gq.zunarmc.spigot.floatingpets.menu.model.MenuItem;
import gq.zunarmc.spigot.floatingpets.menu.model.MenuItemRepository;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ListMenu<T> extends Menu implements Cloneable {

    public ListMenu(String title, int rows) {
        super(title, rows);
    }

    @Override  @SuppressWarnings("unchecked")
    public MenuItemRepository getItems() {
        MenuItemRepository repository = new MenuItemRepository();
        List<T> list = (List<T>) getData("list");
        int menuIndex    = (int) getData("menuIndex");

        Menu nextMenu = null;
        try {
            nextMenu = (Menu) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if(nextMenu == null)
            return null;

        nextMenu.setData("list", list);
        repository.add(transformMenuForPagination(nextMenu, menuIndex, list.size()));

        int i;
        for(i = 0; i<9*(getRows() - 1); i++) {
            int index = ((getRows() - 1) * 9 * menuIndex) + i;
            if (list.size() - 1 >= index) {
                T obj = list.get(index);
                List<MenuItem> current = repository.getAll();

                int curr = i;
                if (current.stream()
                        .anyMatch(menuItem -> menuItem.getSlot() == curr)) {
                    i++;
                }

                if (obj != null) {
                    final ItemStack itemStack = buildItem(obj);
                    repository.add(new MenuItem(itemStack, i) {
                        @Override
                        public void onClick(Player player) {
                            ListMenu.this.onClick(player, obj, index);
                        }
                    });
                }

            }
        }

        return repository;
    }

    public MenuItemRepository transformMenuForPagination(Menu menu, int menuIndex, int size){

        MenuItemRepository repository = new MenuItemRepository();

        repository.filledRow(Material.GRAY_STAINED_GLASS_PANE, getRows());

        ItemStack itemPage = getData("itemPage") != null
                ? (ItemStack) getData("itemPage") : (new ItemBuilder(Material.DIAMOND).name("§bPage: " + (menuIndex + 1)).build());


        final int lastRow = (getRows() - 1) * 9;
        repository.add(new MenuItem(itemPage, lastRow + 4) {
            @Override
            public void onClick(Player player) { }
        });

        if(menuIndex != 0){
            ItemStack itemPrevious = getData("itemPrevious") != null
                    ? (ItemStack) getData("itemPrevious") : (new ItemBuilder(Material.PAPER).name("§bPrevious")).build();
            repository.add(new MenuItem(itemPrevious, lastRow + 3) {
                @Override
                public void onClick(Player player) {
                    menu.setData("menuIndex", menuIndex - 1);
                    getMenuManager().openMenu(player, menu, getPlugin());
                }
            });
        }

        if((size - 1 >= lastRow * (menuIndex + 1))) {
            repository.add(new MenuItem(new ItemBuilder(Material.PAPER).name("§bNext").build(), lastRow + 5) {
                @Override
                public void onClick(Player player) {
                    menu.setData("menuIndex", menuIndex + 1);
                    getMenuManager().openMenu(player, menu, getPlugin());
                }
            });
        }

        return repository;

    }

    public abstract ItemStack buildItem(T object);

    public abstract void onClick(Player player, T object, int index);

}
