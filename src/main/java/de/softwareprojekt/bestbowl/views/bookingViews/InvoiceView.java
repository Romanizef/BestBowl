package de.softwareprojekt.bestbowl.views.bookingViews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import de.softwareprojekt.bestbowl.jpa.entities.*;
import de.softwareprojekt.bestbowl.jpa.repositories.*;
import de.softwareprojekt.bestbowl.utils.VaadinUtils;
import de.softwareprojekt.bestbowl.utils.email.MailSenderService;
import de.softwareprojekt.bestbowl.utils.messages.Notifications;
import de.softwareprojekt.bestbowl.views.MainView;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static de.softwareprojekt.bestbowl.utils.Utils.*;
import static de.softwareprojekt.bestbowl.utils.VaadinUtils.createResponsiveSteps;

/**
 * Creates a view for all booked elements to be displayed in an invoice and paid
 *
 * @author Marten Voß
 */
@Route(value = "invoice", layout = MainView.class)
@PageTitle("Rechnung")
@PermitAll
public final class InvoiceView extends VerticalLayout implements HasUrlParameter<Integer> {
    private static final String NAME_LABEL_WIDTH = "250px";
    private static final String SUM_LABEL_WIDTH = "55px";

    private final transient MailSenderService mailSenderService;
    private final transient BowlingAlleyBookingRepository bowlingAlleyBookingRepository;
    private final transient DrinkBookingRepository drinkBookingRepository;
    private final transient FoodBookingRepository foodBookingRepository;
    private final transient BowlingShoeBookingRepository bowlingShoeBookingRepository;
    private final transient BowlingShoeRepository bowlingShoeRepository;
    private final ResponsiveStep[] responsiveSteps;
    private final H2 header;
    private final TabSheet invoiceTabSheet;
    private final Button completeInvoiceButton;
    private final List<Div> tabContentList = new ArrayList<>();
    private final List<Map<Integer, Article>> articlePerTabList = new ArrayList<>();
    private Label lastTabSumLabel;

    private BowlingAlleyBooking booking;
    private List<DrinkBooking> drinkBookingList;
    private List<FoodBooking> foodBookingList;
    private List<BowlingShoeBooking> bowlingShoeBookingList;
    private List<BowlingShoe> bowlingShoeList;

    /**
     * Constructor for the InvoiceView class. Creates all the components
     */
    @Autowired

    public InvoiceView(BowlingAlleyBookingRepository bowlingAlleyBookingRepository,
                       DrinkBookingRepository drinkBookingRepository, FoodBookingRepository foodBookingRepository,
                       BowlingShoeBookingRepository bowlingShoeBookingRepository,
                       BowlingShoeRepository bowlingShoeRepository) {
        this.bowlingAlleyBookingRepository = bowlingAlleyBookingRepository;
        this.drinkBookingRepository = drinkBookingRepository;
        this.foodBookingRepository = foodBookingRepository;
        this.bowlingShoeBookingRepository = bowlingShoeBookingRepository;
        this.bowlingShoeRepository = bowlingShoeRepository;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        mailSenderService = new MailSenderService();
        responsiveSteps = createResponsiveSteps(600, 5);

        header = new H2();
        invoiceTabSheet = createTabSheet();
        completeInvoiceButton = createCompleteInvoiceButton();
        add(header, invoiceTabSheet, completeInvoiceButton);
    }

    private TabSheet createTabSheet() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_CENTERED, TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabSheet.getStyle()
                .set("border", "1px solid #dadfe4")
                .set("height", "calc(100vh - 200px)");
        return tabSheet;
    }

    private Button createCompleteInvoiceButton() {
        Button button = new Button("Rechnung abschließen");
        button.setIcon(VaadinIcon.CART.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.setWidth("55%");
        button.setEnabled(false);
        button.addClickListener(e -> {
            VaadinUtils.showConfirmationDialog("Rechnung abschließen?", "Ja", "Abbrechen", () -> {
                //free up shoes for reuse
                bowlingShoeList.forEach(bs -> bs.setClient(null));
                bowlingShoeRepository.saveAll(bowlingShoeList);
                //complete booking
                booking.setCompleted(true);
                bowlingAlleyBookingRepository.save(booking);
                mailSenderService.sendInvoiceMail(booking);
                Notifications.showInfo("Rechnung abgeschlossen");
                UI.getCurrent().navigate(ExtrasView.class);
            });
        });
        return button;
    }

    private Tab createTotalTab() {
        Tab tab = new Tab(VaadinIcon.MONEY.create(), new Span("Gesamtrechnung"));
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.getElement().addEventListener("click", e -> replaceTabContent(0));
        return tab;
    }

    private Component createTotalTabContent() {
        Div tabContent = new Div();
        tabContentList.add(tabContent);

        Map<Integer, Article> articleMap = createArticleMap();
        articlePerTabList.add(articleMap);

        replaceTabContent(0);
        return tabContent;
    }

    private Map<Integer, Article> createArticleMap() {
        Map<Integer, Article> articleMap = new TreeMap<>();
        int sortIndex = 0;
        articleMap.put(sortIndex++, new Article("Bowling Bahn (Anteil in %)", 100, booking.getPrice() / 100));
        for (DrinkBooking drinkBooking : drinkBookingList) {
            articleMap.put(sortIndex++, new Article(drinkBooking.getName(), drinkBooking.getAmount(), drinkBooking.getPrice()));
        }
        for (FoodBooking foodBooking : foodBookingList) {
            articleMap.put(sortIndex++, new Article(foodBooking.getName(), foodBooking.getAmount(), foodBooking.getPrice()));
        }
        for (BowlingShoeBooking bowlingShoeBooking : bowlingShoeBookingList) {
            articleMap.put(sortIndex++,
                    new Article("Bowling Schuhe, Größe: " + bowlingShoeBooking.getBowlingShoe().getSize(),
                            1, bowlingShoeBooking.getPrice()));
        }
        return articleMap;
    }

    private void replaceTabContent(int tabIndex) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(Alignment.CENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(responsiveSteps);
        articlePerTabList.get(tabIndex).forEach((i, article) -> formLayout.add(createDisplayPanel(article)));

        Label sumLabel = createNewSumLabel();
        sumLabel.setText(calculatePartialSumString(tabIndex));

        verticalLayout.add(formLayout, sumLabel);

        //replace old content
        Div tabContent = tabContentList.get(tabIndex);
        tabContent.removeAll();
        tabContent.add(verticalLayout);
    }

    private Tab createPartialTab() {
        Tab tab = new Tab(VaadinIcon.MONEY.create(), new Span("Teilrechnung " + articlePerTabList.size()));
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        return tab;
    }

    private Component createPartialTabContent() {
        Div tabContent = new Div();
        tabContentList.add(tabContent);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(Alignment.CENTER);

        Map<Integer, Article> articleMap = new TreeMap<>();
        articlePerTabList.get(0).forEach((i, article) -> {
            if (article.getAmount() > 0) {
                articleMap.put(i, new Article(article));
            }
        });
        articlePerTabList.add(articleMap);

        if (!articleMap.isEmpty()) {
            FormLayout formLayout = new FormLayout();
            formLayout.setResponsiveSteps(responsiveSteps);
            articleMap.forEach((name, article) -> {
                formLayout.add(createEditPanel(name, article, articlePerTabList.size() - 1));
                article.setAmount(0);
            });

            Button completePartialInvoiceButton = new Button("Teilrechnung abschließen");
            completePartialInvoiceButton.setDisableOnClick(true);
            completePartialInvoiceButton.addClickListener(e -> {
                removeAmount0ArticlesFromTab(articlePerTabList.size() - 1);
                replaceTabContent(tabContentList.size() - 1);
                invoiceTabSheet.add(createPartialTab(), createPartialTabContent());
            });

            Label sumLabel = createNewSumLabel();
            lastTabSumLabel = sumLabel;
            verticalLayout.add(formLayout, sumLabel, completePartialInvoiceButton);
        } else {
            verticalLayout.add(new Label("Es gibt keine offenen Positionen mehr"));
        }

        tabContent.add(verticalLayout);
        return tabContent;
    }

    private void removeAmount0ArticlesFromTab(int tabIndex) {
        Map<Integer, Article> articleMap = articlePerTabList.get(tabIndex);
        articleMap.entrySet().removeIf(entry -> entry.getValue().getAmount() == 0);
    }

    private Label createNewSumLabel() {
        Label label = new Label("Summe: 0,00€");
        label.getStyle().set("border", "3px solid #338CFF")
                .set("background-color", "#338CFF")
                .set("padding", "7px")
                .set("border-radius", "10px")
                .set("font-weight", "bold");
        return label;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Integer parameter) {
        if (parameter == null) {
            header.setText("Keine Rechnung ausgewählt");
            return;
        }
        Optional<BowlingAlleyBooking> bowlingAlleyBookingOptional = bowlingAlleyBookingRepository.findById(parameter);
        if (bowlingAlleyBookingOptional.isPresent()) {
            BowlingAlleyBooking bowlingAlleyBooking = bowlingAlleyBookingOptional.get();
            if (bowlingAlleyBooking.isActive() && !bowlingAlleyBooking.isCompleted()) {
                this.booking = bowlingAlleyBooking;
                getBookingDataFromDB();
                initializeComponents();
            } else {
                header.setText("Diese Rechnung ist bereits abgeschlossen");
            }
        } else {
            header.setText("Es existiert keine Rechnung mit dieser ID");
        }
    }

    private void getBookingDataFromDB() {
        drinkBookingList = drinkBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
                        booking.getClient(), booking.getBowlingAlley(), booking.getStartTime());
        drinkBookingList.sort(Comparator.comparing(DrinkBooking::getName));
        foodBookingList = foodBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
                        booking.getClient(), booking.getBowlingAlley(), booking.getStartTime());
        foodBookingList.sort(Comparator.comparing(FoodBooking::getName));
        bowlingShoeBookingList = bowlingShoeBookingRepository
                .findAllByClientEqualsAndBowlingAlleyEqualsAndTimeStampEquals(
                        booking.getClient(), booking.getBowlingAlley(), booking.getStartTime());
        bowlingShoeBookingList.sort(Comparator.comparingInt(shoe -> shoe.getBowlingShoe().getSize()));
        bowlingShoeList = bowlingShoeBookingList.stream().map(BowlingShoeBooking::getBowlingShoe).toList();
    }

    private void initializeComponents() {
        String text = "Rechnung Nr: " + booking.getId() + " , " +
                booking.getClient().getFullName() + " , " +
                "Bahn: " + booking.getBowlingAlley().getId() + " , " +
                toDateString(booking.getStartTime()) + " - " +
                toHourOnlyString(booking.getEndTime() + 1);
        header.setText(text);
        completeInvoiceButton.setEnabled(true);

        invoiceTabSheet.setSelectedTab(invoiceTabSheet.add(createTotalTab(), createTotalTabContent()));
        invoiceTabSheet.add(createPartialTab(), createPartialTabContent());
    }

    private HorizontalLayout createDisplayPanel(Article article) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);

        Label nameLabel = new Label(article.getName());
        nameLabel.setMinWidth(NAME_LABEL_WIDTH);
        nameLabel.setMaxWidth(NAME_LABEL_WIDTH);

        IntegerField amountField = new IntegerField();
        amountField.setValue(article.getAmount());
        amountField.setStepButtonsVisible(true);
        amountField.setReadOnly(true);

        Label articleSumLabel = new Label(formatDouble(article.getAmount() * article.getPrice()) + "€");
        articleSumLabel.setMinWidth(SUM_LABEL_WIDTH);
        articleSumLabel.setMaxWidth(SUM_LABEL_WIDTH);

        layout.add(nameLabel, amountField, articleSumLabel);
        layout.expand(amountField);
        addCSS(layout);
        return layout;
    }

    private HorizontalLayout createEditPanel(int articleIndex, Article article, int tabIndex) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);

        Label nameLabel = new Label(article.getName());
        nameLabel.setMinWidth(NAME_LABEL_WIDTH);
        nameLabel.setMaxWidth(NAME_LABEL_WIDTH);

        Label articleSumLabel = new Label("0,00€");
        articleSumLabel.setMinWidth(SUM_LABEL_WIDTH);
        articleSumLabel.setMaxWidth(SUM_LABEL_WIDTH);

        IntegerField amountField = new IntegerField();
        amountField.setValue(0);
        amountField.setStepButtonsVisible(true);
        amountField.setMin(0);
        amountField.setMax(article.getAmount());
        amountField.addValueChangeListener(e -> {
            Integer value = e.getValue();
            if (value == null) {
                value = 0;
            }
            if (value < amountField.getMin() || value > amountField.getMax()) {
                amountField.setValue(e.getOldValue());
                return;
            }
            articlePerTabList.get(0).get(articleIndex).setAmount(amountField.getMax() - value);
            articlePerTabList.get(tabIndex).get(articleIndex).setAmount(value);
            articleSumLabel.setText(formatDouble(value * article.getPrice()) + "€");
            lastTabSumLabel.setText(calculatePartialSumString(tabIndex));
        });

        layout.add(nameLabel, amountField, articleSumLabel);
        layout.expand(amountField);
        addCSS(layout);
        return layout;
    }

    private void addCSS(Component component) {
        component.getStyle()
                .set("border", "2px solid #338CFF")
                .set("background-color", "#338CFF10")
                .set("padding", "10px")
                .set("border-radius", "30px")
                .set("margin-bottom", "10px");
    }

    private String calculatePartialSumString(int tabIndex) {
        double sum = 0;
        for (Article article : articlePerTabList.get(tabIndex).values()) {
            sum += article.getAmount() * article.getPrice();
        }
        return "Summe: " + formatDouble(sum) + "€";
    }

    private static final class Article {
        private final String name;
        private final double price;
        private int amount;

        private Article(String name, int amount, double price) {
            this.name = name;
            this.amount = amount;
            this.price = price;
        }

        public Article(Article other) {
            this.name = other.name;
            this.price = other.price;
            this.amount = other.amount;
        }

        public String getName() {
            return name;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public double getPrice() {
            return price;
        }
    }
}
