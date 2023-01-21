package user;

import org.apache.commons.lang3.RandomStringUtils;

public class DataGenForUser {
    public String generateEmail() {
        return RandomStringUtils.randomAlphabetic(12).toLowerCase() + "@yandex.ru";
    }

    public String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(8).toLowerCase();
    }

    public String generateName() {
        return RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphabetic(5).toLowerCase();
    }
}
