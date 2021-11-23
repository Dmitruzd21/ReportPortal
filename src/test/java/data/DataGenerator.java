package data;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    // метод генерации даты встречи в формате String (который принимает "+дней" и формат даты)
    public static String getDateOfMeetingInString(int days, String formatePattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(formatePattern));
    }

    public static String generateCity(String locale) {
        String[] cities = {"Москва", "Казань", "Самара", "Хабаровск", "Махачкала", "Новосибирск", "Санкт-Петербург"};
        Random index = new Random();
        int indexInt = index.nextInt(cities.length);
        String randomCity = cities[indexInt];
        return randomCity;
    }

    public static String generateFullName(String locale) {
        Faker faker = new Faker(new Locale(locale));
        String fullName = faker.name().fullName().replace("ё", "е");
        return fullName;
    }

    public static String generatePhone(String locale) {
        Faker faker = new Faker(new Locale(locale));
        String phone = faker.phoneNumber().phoneNumber();
        return phone;
    }
}
