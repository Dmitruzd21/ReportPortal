package ru.netology;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;


public class ChangeOfTimeTest {
    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        //Открытие формы
        open("http://localhost:9999");
    }

    // Успешное заполнение формы
    @Test
    public void shouldOrderCardDelivery() {
        int daysToAddForFirstMeeting = 4;
        int daysToAddForSecondMeeting = 5;
        // Город (полный ввод)
        $("[data-test-id=city] .input__control").setValue(DataGenerator.generateCity("ru"));
        // Дата встречи(текущая + 4 дня)
        String formattedDateOfMeeting = DataGenerator.getDateOfMeetingInString(daysToAddForFirstMeeting, "dd.MM.yyyy");
        // Очистить строку с датой
        $("[data-test-id=date] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id=date] .input__control").sendKeys(Keys.BACK_SPACE);
        // Ввести дату (через клавиатуру)
        $("[data-test-id=date] .input__control").setValue(formattedDateOfMeeting);
        // Фамилия и имя
        $("[data-test-id=name] .input__control").setValue(DataGenerator.generateFullName("ru"));
        // Телефон
        $("[data-test-id=phone] .input__control").setValue(DataGenerator.generatePhone("ru"));
        // Чек-бокс
        $("div form fieldset label").click();
        // Запланировать
        $(Selectors.byText("Запланировать")).click();
        // Всплывающее окно успешно
        $(Selectors.withText("Успешно")).shouldBe(visible, Duration.ofSeconds(15));
        // Окно "Успешно" имеет текст "Встреча успешно забронирована на + дата"
        $("div.notification__content").shouldHave(exactText("Встреча успешно запланирована на " + formattedDateOfMeeting));
        // Новая дата встречи(текущая + 5 дня)
        String NewFormattedDateOfMeeting = DataGenerator.getDateOfMeetingInString(daysToAddForSecondMeeting, "dd.MM.yyyy");
        // Очистить строку с датой
        $("[data-test-id=date] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id=date] .input__control").sendKeys(Keys.BACK_SPACE);
        // Ввести новую дату (через клавиатуру)
        $("[data-test-id=date] .input__control").setValue(NewFormattedDateOfMeeting);
        // Запланировать
        $(Selectors.byText("Запланировать")).click();
        // Всплывающее окно "Необходимо подтверждение"
        $(Selectors.withText("Необходимо подтверждение")).shouldBe(visible);
        $(Selectors.withText("У вас уже запланирована встреча на другую дату. Перепланировать?")).shouldBe(visible);
        // Кликнуть на кнопку "Перепланировать"
        $(Selectors.byText("Перепланировать")).click();
        // Повторное всплывающее окно успешно
        $(Selectors.withText("Успешно")).shouldBe(visible, Duration.ofSeconds(15));
        // Окно "Успешно" имеет текст "Встреча успешно забронирована на + дата"
        $("div.notification__content").shouldHave(exactText("Встреча успешно запланирована на " + NewFormattedDateOfMeeting));
    }

}
