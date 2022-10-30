package ru.yandex.prakticum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.prakticum.filmorate.model.User;
import ru.yandex.prakticum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
	@Qualifier("userDbStorage")
	private final UserDbStorage userStorage;

	@Test
	public void testFindUserById() {

		userStorage.add(User.builder()
				.email("test@test.com")
				.login("login")
				.name("name")
				.birthday(LocalDate.now())
				.build());

		Optional<User> userOptional = Optional.of(userStorage.getById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}
}
