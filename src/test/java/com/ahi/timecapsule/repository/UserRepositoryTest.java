package com.ahi.timecapsule.repository;

import com.ahi.timecapsule.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void existsByEmail_Success() {
		String email = "test@test.com";
		User user = User.builder()
						.userId("testUser")
						.email(email)
						.build();
		userRepository.save(user);

		boolean result = userRepository.existsByEmail(email);

		assertTrue(result, "중복된 이메일이 존재합니다.");
	}

	@Test
	void existsByEmail_False() {
		String email = "nonexistent@example.com";

		boolean result = userRepository.existsByEmail(email);

		assertFalse(result, "중복된 이메일이 존재하지 않습니다.");
	}

	@Test
	void existsByNickname_True() {
		String nickname = "testNickname";
		User user = User.builder()
						.userId("testUser")
						.nickname(nickname)
						.build();
		userRepository.save(user);

		boolean result = userRepository.existsByNickname(nickname);

		assertTrue(result, "중복된 닉네임이 존재합니다.");
	}

	@Test
	void existsByNickname_False() {
		String nickname = "nonExistentNick";

		boolean result = userRepository.existsByNickname(nickname);

		assertFalse(result, "중복된 닉네임이 존재하지 않습니다.");
	}

	@Test
	void findByUserIdAndEmail_Success() {
		String userId = "testUser";
		String email = "test@test.com";
		User user = User.builder()
						.userId(userId)
						.email(email)
						.build();
		userRepository.save(user);

		Optional<User> result = userRepository.findByUserIdAndEmail(userId, email);

		assertTrue(result.isPresent(), "일치하는 계정이 존재합니다.");
	}

	@Test
	void findByUserIdAndEmail_UserEmpty() {
		String userId = "nonexistentUser";
		String email = "nonexistent@test.com";

		Optional<User> result = userRepository.findByUserIdAndEmail(userId, email);

		assertTrue(result.isEmpty(), "일치하는 계정이 존재하지 않습니다.");
	}
}
