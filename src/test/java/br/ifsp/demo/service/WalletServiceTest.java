package br.ifsp.demo.service;

import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.InMemoryWalletRepository;
import br.ifsp.demo.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {

    @Nested
    class RegisterInvestment {

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should register an investment")
        void shouldRegisterAnInvestment(){
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            WalletService sut = new WalletService(inMemoryRepository);
            Asset asset = new Asset();
            Investment investment = new Investment(asset);

            boolean result = sut.addInvestment(investment);
            assertThat(result).isTrue();
        }
    }
}