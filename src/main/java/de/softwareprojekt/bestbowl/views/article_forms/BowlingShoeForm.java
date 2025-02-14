package de.softwareprojekt.bestbowl.views.article_forms;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import de.softwareprojekt.bestbowl.beans.Repos;
import de.softwareprojekt.bestbowl.jpa.entities.BowlingCenter;
import de.softwareprojekt.bestbowl.jpa.entities.bowling_shoe.BowlingShoe;
import de.softwareprojekt.bestbowl.utils.validators.article.BowlingShoeValidator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Objects;

/**
 * Creates the Form for the Shoe Entity.
 *
 * @author Max Ziller
 */
public class BowlingShoeForm extends FormLayout {

    /**
     * Constructor for the ShoeForm. Creates a boughtAt and size Field and a
     * checkbox for the active status.
     * The {@code Binder} binds the fields to the entity.
     *
     * @param shoeBinder
     */
    public BowlingShoeForm(Binder<BowlingShoe> shoeBinder) {
        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setMargin(false);
        labelLayout.setPadding(false);
        Label dateLabel = new Label("Kaufdatum");
        Label timeLabel = new Label("Kaufzeit");
        labelLayout.add(dateLabel, timeLabel);
        labelLayout.expand(dateLabel, timeLabel);

        DateTimePicker boughtAtField = new DateTimePicker();
        boughtAtField.setWidthFull();
        boughtAtField.setLocale(Locale.GERMANY);
        boughtAtField.setMin(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));

        IntegerField sizeField = new IntegerField("Größe");
        sizeField.setWidthFull();
        sizeField.setSuffixComponent(new Span("Größe"));
        sizeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        sizeField.setRequired(true);

        HorizontalLayout checkboxLayout = new HorizontalLayout();
        checkboxLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        checkboxLayout.setWidthFull();
        checkboxLayout.setHeight("50px");
        Checkbox activeCheckbox = new Checkbox("Artikel aktivieren");
        checkboxLayout.add(activeCheckbox);

        add(labelLayout, boughtAtField, sizeField, checkboxLayout);

        BowlingCenter bowlingCenter = Repos.getBowlingCenterRepository().getBowlingCenter();
        shoeBinder.withValidator(new BowlingShoeValidator(bowlingCenter.getMinShoeSize(), bowlingCenter.getMaxShoeSize()));
        shoeBinder.bind(boughtAtField, bowlingShoe -> LocalDateTime.ofInstant(Instant.ofEpochMilli(bowlingShoe.getBoughtAt()),
                ZoneId.systemDefault()), (bowlingShoe, localDateTime) -> {
            bowlingShoe.setBoughtAt(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
        });
        shoeBinder.bind(sizeField, BowlingShoe::getSize,
                (bowlingShoe, size) -> bowlingShoe.setSize(Objects.requireNonNullElse(size, 0)));
        shoeBinder.bind(activeCheckbox, BowlingShoe::isActive,
                (bowlingShoe, active) -> bowlingShoe.setActive(Objects.requireNonNullElse(active, false)));
    }
}