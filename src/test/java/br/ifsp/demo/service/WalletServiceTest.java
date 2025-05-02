package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.InMemoryWalletRepository;
import br.ifsp.demo.repository.WalletRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static br.ifsp.demo.domain.AssetType.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {
    private Wallet wallet;
    private WalletService sut;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
        WalletRepository inMemoryRepository = new InMemoryWalletRepository();
        inMemoryRepository.save(wallet);
        sut = new WalletService(inMemoryRepository);
    }

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

            Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
            Investment investment = new Investment(100, asset);

            boolean result = sut.addInvestment(wallet.getId(), investment);
            assertThat(result).isTrue();
        }
    }

    @Nested
    class WithdrawInvestment {

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should withdraw an investment")
        void shouldWithdrawAnInvestment(){
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);
            WalletService sut = new WalletService(inMemoryRepository);

            Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(wallet.getId(), investment);

            boolean result = sut.withdrawInvestment(wallet.getId(), investment.getId());
            assertThat(result).isTrue();
        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return NoSuchElementException if the investment does not exist")
        void shouldReturnNoSuchElementExceptionIfTheInvestmentDoesNotExist(){
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);
            WalletService sut = new WalletService(inMemoryRepository);

            Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(wallet.getId(), investment);

            assertThrows(NoSuchElementException.class, () -> {
               sut.withdrawInvestment(wallet.getId(), UUID.randomUUID());
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return NoSuchElementException if the wallet does not exist")
        void shouldReturnNoSuchElementExceptionIfTheWalletDoesNotExist(){
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);
            WalletService sut = new WalletService(inMemoryRepository);

            Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(wallet.getId(), investment);

            assertThrows(NoSuchElementException.class, () -> {
                sut.withdrawInvestment(UUID.randomUUID(), investment.getId());
            });
        }
        
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException when wallet has no investment")
        void shouldReturnNoSuchElementExceptionWhenWalletHasNoInvestment(){
            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));

            assertThrows(NoSuchElementException.class, () -> {
                sut.withdrawInvestment(wallet.getId(), investment.getId());
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return true when withdrawing from a wallet with only one investment")
        void shouldReturnTrueWhenWithdrawingFromAWalletWithOnlyOneInvestment(){
            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));
            sut.addInvestment(wallet.getId(), investment);

            boolean result = sut.withdrawInvestment(wallet.getId(), investment.getId());

            assertThat(result).isTrue();
        }
    }

    @Nested
    class GetInvestments {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return all investments on wallet")
        void shouldReturnAllInvestmentsOnWallet() {
            Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
            Investment investment1 = new Investment(1000, asset);
            Investment investment2 = new Investment(1500, asset);

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);

            List<Investment> result = sut.getInvestments(wallet.getId());
            assertThat(result.size()).isEqualTo(2);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("shouldReturnAnEmptyListWhenThereIsNoInvestments")
        void shouldReturnAnEmptyListWhenThereIsNoInvestments(){
            List<Investment> result = sut.getInvestments(wallet.getId());
            assertThat(result).isEqualTo(List.of());
        }
    }

    @Nested
    class GetHistoryInvestments {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return all history investments on wallet")
        void shouldReturnAllHistoryInvestmentsOnWallet() {
            Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
            Investment investment1 = new Investment(1000, asset);
            Investment investment2 = new Investment(1500, asset);

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);
            sut.withdrawInvestment(wallet.getId(), investment1.getId());

            List<Investment> result = sut.getHistoryInvestments(wallet.getId());
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("shouldReturnAnEmptyListWhenThereIsNoHistoryInvestments")
        void shouldReturnAnEmptyListWhenThereIsNoHistoryInvestments(){
            List<Investment> result = sut.getHistoryInvestments(wallet.getId());
            assertThat(result).isEqualTo(List.of());
        }
    }

    @Nested
    class HistoryFilter{
        
        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return investments when filtered by asset type")
        void shouldReturnInvestmentsWhenFilteredByAssetType(){
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)));
            Investment investment3 = new Investment(1500, new Asset("Banco Itau", LCI, 0.01, LocalDate.now().plusYears(1)));

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);
            sut.addInvestment(wallet.getId(), investment3);

            sut.withdrawInvestment(wallet.getId(), investment1.getId());
            sut.withdrawInvestment(wallet.getId(), investment2.getId());
            sut.withdrawInvestment(wallet.getId(), investment3.getId());

            List<Investment> result = sut.filterHistory(wallet.getId(), CDB);

            assertThat(result.size()).isEqualTo(2);
        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return investments when filtered by date")
        void shouldReturnInvestmentsWhenFilteredByDate(){
            LocalDate initialDate = LocalDate.now().minusMonths(1);
            LocalDate finalDate = LocalDate.now().plusMonths(1);

            Investment investment = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));
            sut.addInvestment(wallet.getId(), investment);
            sut.withdrawInvestment(wallet.getId(), investment.getId());

            List<Investment> result = sut.filterHistory(wallet.getId(), initialDate, finalDate);

            assertThat(result.size()).isEqualTo(1);
        }
        
        @ParameterizedTest
        @MethodSource("provideScenariosForEmptyTypeFilterHistory")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return an empty list when filter has no match")
        void shouldReturnAnEmptyListWhenTypeFilterHasNoMatch(List<Investment> investments, AssetType assetType){
            investments.forEach(investment -> sut.addInvestment(wallet.getId(), investment));
            investments.forEach(investment -> sut.withdrawInvestment(wallet.getId(), investment.getId()));

            List<Investment> result = sut.filterHistory(wallet.getId(), assetType);

            assertThat(result).isEqualTo(List.of());
        }

        @ParameterizedTest
        @MethodSource("provideScenariosForEmptyDateFilterHistory")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return an empty list when date filter has no match")
        void shouldReturnAnEmptyListWhenDateFilterHasNoMatch(List<Investment> investments, LocalDate initialDate, LocalDate finalDate){
            investments.forEach(investment -> sut.addInvestment(wallet.getId(), investment));
            investments.forEach(investment -> sut.withdrawInvestment(wallet.getId(), investment.getId()));

            List<Investment> result = sut.filterHistory(wallet.getId(), initialDate, finalDate);

            assertThat(result).isEqualTo(List.of());
        }

        private static Stream<Arguments> provideScenariosForEmptyTypeFilterHistory(){
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), CDB),
                    Arguments.of(List.of(investment1, investment2), LCI)
            );
        }

        private static Stream<Arguments> provideScenariosForEmptyDateFilterHistory(){
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2)),
                    Arguments.of(List.of(investment1, investment2), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2))
            );
        }
    }

    @Nested
    class ActiveInvestmentsFilter {
        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @MethodSource("provideScenariosForEmptyTypeFilterActiveInvestments")
        @DisplayName("Should return an empty list if there is no active investments when filter by type")
        void shouldReturnAnEmptyListIfThereIsNoActiveInvestmentsWhenFilterByType(List<Investment> registeredInvestments, AssetType assetType){
            registeredInvestments.forEach(investment -> sut.addInvestment(wallet.getId(), investment));

            List<Investment> result = sut.filterActiveInvestments(wallet.getId(), assetType);
            assertThat(result).isEqualTo(List.of());
        }

        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @MethodSource("provideScenariosForEmptyDateFilterActiveInvestments")
        @DisplayName("Should return an empty list if there is no active investments when filter by date")
        void shouldReturnAnEmptyListIfThereIsNoActiveInvestmentsWhenFilterByDate(List<Investment> registeredInvestments, LocalDate initialDate, LocalDate finalDate){
            registeredInvestments.forEach(investment -> sut.addInvestment(wallet.getId(), investment));

            List<Investment> result = sut.filterActiveInvestments(wallet.getId(), initialDate, finalDate);
            assertThat(result).isEqualTo(List.of());
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return the active investments found when filter by type")
        void shouldReturnTheActiveInvestmentsFoundWhenFilterByType(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusMonths(2));
            Investment investment = new Investment(1000, assetCDB);
            sut.addInvestment(wallet.getId(), investment);
            List<Investment> result = sut.filterActiveInvestments(wallet.getId(), CDB);
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return the active investments found when filter by date")
        void shouldReturnTheActiveInvestmentsFoundWhenFilterByDate(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusMonths(2));
            Investment investment = new Investment(1000, assetCDB);
            sut.addInvestment(wallet.getId(), investment);
            List<Investment> result = sut.filterActiveInvestments(wallet.getId(), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1));
            assertThat(result.size()).isEqualTo(1);
        }

        private static Stream<Arguments> provideScenariosForEmptyTypeFilterActiveInvestments(){
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), CDB),
                    Arguments.of(List.of(investment1, investment2), LCI)
            );
        }

        private static Stream<Arguments> provideScenariosForEmptyDateFilterActiveInvestments(){
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2)),
                    Arguments.of(List.of(investment1, investment2), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2))
            );
        }
    }

    @Nested
    class Report {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should throw NoSuchElementException when there is no investments")
        void shouldThrowNoSuchElementExceptionWhenThereIsNoInvestments(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.generateReport(wallet.getId());
            });
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should throw NoSuchElementException when there is no wallet registered")
        void shouldThrowNoSuchElementExceptionWhenThereIsNoWalletRegistered(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.generateReport(UUID.randomUUID());
            });
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return report when there is investments")
        void shouldReturnReportWhenThereIsInvestments(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusMonths(2));
            Asset assetTesouroDireto = new Asset("Banco BMG", TESOURO_DIRETO, 0.1, LocalDate.now().plusMonths(2));
            Investment investment1 = new Investment(1000, assetCDB);
            Investment investment2 = new Investment(1500, assetTesouroDireto);
            Investment investment3 = new Investment(1500, assetTesouroDireto);
            Investment investment4 = new Investment(1500, assetTesouroDireto);
            Investment investment5 = new Investment(1500, assetCDB);

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);
            sut.addInvestment(wallet.getId(), investment3);
            sut.addInvestment(wallet.getId(), investment4);
            sut.addInvestment(wallet.getId(), investment5);
            sut.withdrawInvestment(wallet.getId(), investment2.getId());
            sut.withdrawInvestment(wallet.getId(), investment5.getId());

            SoftAssertions softly = new SoftAssertions();
            String report = sut.generateReport(wallet.getId());

            softly.assertThat(report).contains(investment1.toString());
            softly.assertThat(report).contains(investment2.toString());
            softly.assertThat(report).contains("> Current Total Balance: R$ 7000,00");
            softly.assertThat(report).contains("> Future Investments Balance: R$ 4855,41");
            softly.assertThat(report).contains("> Total Balance (Current + Future): R$ 11855,41");

            softly.assertThat(report).contains("CRA: 0,00%");
            softly.assertThat(report).contains("CRI: 0,00%");
            softly.assertThat(report).contains("TESOURO_DIRETO: 66,67%");
            softly.assertThat(report).contains("TESOURO_DIRETO: 50,00%");
            softly.assertThat(report).contains("LCI: 0,00%");
            softly.assertThat(report).contains("CDB: 33,33%");
            softly.assertThat(report).contains("CDB: 50,00%");
            softly.assertThat(report).contains("LCA: 0,00%");

            softly.assertAll();
        }
    }
}
