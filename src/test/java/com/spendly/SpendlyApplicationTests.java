package com.spendly;

import com.spendly.domain.repository.CustomerRepository;
import com.spendly.domain.repository.TransactionRepository;
import com.spendly.domain.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@MockitoBean(types = {
		CustomerRepository.class,
		TransactionRepository.class,
		WalletRepository.class
})
class SpendlyApplicationTests {

	@Test
	void contextLoads() {
	}

}
