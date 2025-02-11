package burgers.user;

import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import java.util.Locale;

public class UserRandom {
    Faker faker = new Faker(new Locale("ru"));

    public  UserRegisterRequest generateUser() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(8,8);
        String name = StringUtils.left(faker.name().name(),8);
        return new UserRegisterRequest (email, password, name);
    }

    public  String email() {
        return faker.internet().emailAddress();
    }

    public  String password() {
        return faker.internet().password(8,8);
    }

    public  String name() {
        return StringUtils.left(faker.name().name(),8);
    }

}
