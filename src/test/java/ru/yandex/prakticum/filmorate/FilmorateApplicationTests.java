package ru.yandex.prakticum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.prakticum.filmorate.model.User;
import ru.yandex.prakticum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
	@Qualifier("userDbStorage")
	private final UserDbStorage userStorage;
	@Qualifier("filmDbStorage")
	private final FilmDbStorage filmDbStorage;
	private final FilmLikesDBStorage filmLikesDBStorage;
	private final FriendsDBStorage friendsDBStorage;
	private final GenreDBStorage genreDBStorage;
	private final MpaDBStorage mpaDBStorage;

	final int USER_ID = 1;
	final String USER_EMAIL = "user@email.com";
	final String USER_LOGIN = "user_login";
	final String USER_NAME = "user name";
	final LocalDate USER_BIRTHDAY = LocalDate.now().minusYears(30);
	final User USER = User.builder()
			.email(USER_EMAIL)
			.login(USER_LOGIN)
			.name(USER_NAME)
			.birthday(USER_BIRTHDAY)
			.build();

	final int FRIEND_ID = 2;
	final String FRIEND_EMAIL = "friend@email.com";
	final String FRIEND_LOGIN = "friend_login";
	final String FRIEND_NAME = "friend name";
	final LocalDate FRIEND_BIRTHDAY = LocalDate.now().minusYears(35);
	final User FRIEND = User.builder()
			.email(FRIEND_EMAIL)
			.login(FRIEND_LOGIN)
			.name(FRIEND_NAME)
			.birthday(FRIEND_BIRTHDAY)
			.build();

	@Test
	public void testFindUserById() {

		userStorage.add(USER);

		Optional<User> userOptional = Optional.of(userStorage.getById(USER_ID));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", USER_ID)
				);

		USER.setName(FRIEND_NAME);
		userStorage.update(USER);

		userOptional = Optional.of(userStorage.getById(USER_ID));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", FRIEND_NAME)
				);

		userStorage.add(FRIEND);

		Optional<User> friendOptional = Optional.of(userStorage.getById(FRIEND_ID));

		assertThat(friendOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", FRIEND_ID)
				);
	}
}
