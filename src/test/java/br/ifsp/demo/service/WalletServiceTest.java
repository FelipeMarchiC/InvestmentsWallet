package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
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
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);

            WalletService sut = new WalletService(inMemoryRepository);
            Asset asset = new Asset("PETR4");
            Investment investment = new Investment(100, 50, asset);

            boolean result = sut.addInvestment(wallet.getId(), investment);
            assertThat(result).isTrue();
        }
    }
}