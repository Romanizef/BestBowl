package de.softwareprojekt.bestbowl.beans;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.softwareprojekt.bestbowl.jpa.entities.drink.Drink;
import de.softwareprojekt.bestbowl.jpa.entities.food.Food;
import de.softwareprojekt.bestbowl.jpa.repositories.drink.DrinkRepository;
import de.softwareprojekt.bestbowl.jpa.repositories.food.FoodRepository;
import de.softwareprojekt.bestbowl.utils.records.ReorderEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ReorderService {
    private final DrinkRepository drinkRepository;
    private final FoodRepository foodRepository;

    @Autowired
    public ReorderService(DrinkRepository drinkRepository, FoodRepository foodRepository) {
        this.drinkRepository = drinkRepository;
        this.foodRepository = foodRepository;
    }

    private static String formatMl(int ml) {
        return String.format(Locale.GERMANY, "%.3f", ((double) ml / 1000)) + " l";
    }

    public void checkForReorderThreshold() {
        List<Drink> drinkList = drinkRepository.findAllByReorderThresholdReached();
        List<Food> foodList = foodRepository.findAllByReorderThresholdReached();
        showReorderNotification(drinkList.size(), foodList.size());
    }

    private void showReorderNotification(int drinks, int foods) {
        String text;
        if (drinks == 0 && foods == 0) {
            text = "Keine Artikel unter Meldebestand";
        } else {
            text = "Meldebestand unterschritten für ";
            if (drinks > 0) {
                text += drinks + " Getränk";
                if (drinks > 1) {
                    text += "e";
                }
            }
            if (drinks > 0 && foods > 0) {
                text += " und ";
            }
            if (foods > 0) {
                text += foods + " Speise";
                if (foods > 1) {
                    text += "n";
                }
            }
        }
        text += ".";

        Notification notification = new Notification();
        if (drinks == 0 && foods == 0) {
            notification.setDuration(4000);
        } else {
            notification.setDuration(0);
        }
        notification.setPosition(Notification.Position.BOTTOM_STRETCH);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        Div statusText = new Div(new Text(text));
        Button closeButton = new Button(LumoIcon.CROSS.create());
        closeButton.getElement().getStyle().set("margin-left", "var(--lumo-space-xl)");
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.addClickListener(event -> notification.close());
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        horizontalLayout.add(statusText, closeButton);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.setPadding(false);
        verticalLayout.setMargin(false);
        verticalLayout.add(horizontalLayout);
        notification.add(verticalLayout);
        notification.open();
    }

    public List<ReorderEntry> getReorderEntryList() {
        List<Drink> drinkList = drinkRepository.findAllByReorderThresholdReached();
        List<Food> foodList = foodRepository.findAllByReorderThresholdReached();
        List<ReorderEntry> reorderEntryList = new ArrayList<>(drinkList.size() + foodList.size());
        reorderEntryList.addAll(getReorderEntryListForDrink(drinkList));
        reorderEntryList.addAll(getReorderEntryListForFood(foodList));
        return reorderEntryList;
    }

    private List<ReorderEntry> getReorderEntryListForDrink(List<Drink> drinkList) {
        List<ReorderEntry> reorderEntryList = new ArrayList<>(drinkList.size());
        for (Drink drink : drinkList) {
            reorderEntryList.add(new ReorderEntry("Getränk", drink.getName(),
                    formatMl(drink.getStockInMilliliters()),
                    formatMl(drink.getReorderPoint())));
        }
        return reorderEntryList;
    }

    private List<ReorderEntry> getReorderEntryListForFood(List<Food> foodList) {
        List<ReorderEntry> reorderEntryList = new ArrayList<>(foodList.size());
        for (Food food : foodList) {
            reorderEntryList.add(new ReorderEntry("Speise", food.getName(),
                    food.getStock() + " Stk.", food.getReorderPoint() + " Stk."));
        }
        return reorderEntryList;
    }
}
