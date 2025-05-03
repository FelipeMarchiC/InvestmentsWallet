package br.ifsp.demo.service;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.repository.InMemoryWalletRepository;
import br.ifsp.demo.repository.WalletRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static br.ifsp.demo.domain.AssetType.*;
import static br.ifsp.demo.domain.InvestmentFactory.createInvestmentWithPurchaseDate;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {
    private Wallet wallet;
    private WalletService sut;
    private LocalDate date;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
        WalletRepository inMemoryRepository = new InMemoryWalletRepository();
        inMemoryRepository.save(wallet);
        sut = new WalletService(inMemoryRepository);
        date = LocalDate.of(2025, 4, 25);
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

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
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

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(wallet.getId(), investment);

            boolean result = sut.withdrawInvestment(wallet.getId(), investment.getId(), date);
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

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(wallet.getId(), investment);

            assertThrows(NoSuchElementException.class, () -> {
               sut.withdrawInvestment(wallet.getId(), UUID.randomUUID(), date);
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

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(wallet.getId(), investment);

            assertThrows(NoSuchElementException.class, () -> {
                sut.withdrawInvestment(UUID.randomUUID(), investment.getId(), date);
            });
        }
        
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException when wallet has no investment")
        void shouldReturnNoSuchElementExceptionWhenWalletHasNoInvestment(){
            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));

            assertThrows(NoSuchElementException.class, () -> {
                sut.withdrawInvestment(wallet.getId(), investment.getId(), date);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return true when withdrawing from a wallet with only one investment")
        void shouldReturnTrueWhenWithdrawingFromAWalletWithOnlyOneInvestment(){
            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            sut.addInvestment(wallet.getId(), investment);

            boolean result = sut.withdrawInvestment(wallet.getId(), investment.getId(), date);

            assertThat(result).isTrue();
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return true when withdrawing from a wallet with many investments")
        void shouldReturnTrueWhenWithdrawingFromAWalletWithManyInvestments(){
            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(150, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));
            Investment investment3 = new Investment(150, new Asset("Banco Itau", LCI, 0.01, date.plusYears(1)));
            sut.addInvestment(wallet.getId(), investment);
            sut.addInvestment(wallet.getId(), investment2);
            sut.addInvestment(wallet.getId(), investment3);

            boolean result = sut.withdrawInvestment(wallet.getId(), investment2.getId(), date);

            assertThat(result).isTrue();
        }
        
        @ParameterizedTest
        @MethodSource("provideScenariosToNullPointerException")
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException when some parameter is null")
        void shouldReturnNullPointerExceptionWhenSomeParameterIsNull(UUID walletId, UUID investmentId){
            assertThrows(NullPointerException.class, () -> {
                sut.withdrawInvestment(walletId, investmentId, date);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should move investment to history when withdrawing")
        void shouldMoveInvestmentToHistoryWhenWithdrawing(){
            SoftAssertions softly = new SoftAssertions();

            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            sut.addInvestment(wallet.getId(), investment);

            sut.withdrawInvestment(wallet.getId(), investment.getId(), date);
            List<Investment> history = sut.getHistoryInvestments(wallet.getId());

            softly.assertThat(history.size()).isEqualTo(1);
            softly.assertThat(history.getFirst()).isEqualTo(investment);
            softly.assertThat(history.getFirst().getWithdrawDate()).isNotNull();
            softly.assertAll();
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should throw IllegalArgumentException if addToHistory fails")
        void shouldThrowIllegalArgumentExceptionIfAddToHistoryFails(){
            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            sut.addInvestment(wallet.getId(), investment);

            wallet.addInvestmentOnHistory(investment);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sut.withdrawInvestment(wallet.getId(), investment.getId(), date);
            });

            assertThat(exception).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Could not move investment to history");
        }

        static Stream<Arguments> provideScenariosToNullPointerException(){
            return Stream.of(
                    Arguments.of(null, UUID.randomUUID()),
                    Arguments.of(UUID.randomUUID(), null)
            );
        }
    }

    @Nested
    class GetInvestments {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return all investments on wallet")
        void shouldReturnAllInvestmentsOnWallet() {
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
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

        // Unit Tests
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.getInvestments(UUID.randomUUID());
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull(){
            assertThrows(NullPointerException.class, () -> {
                sut.getInvestments(null);
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToGetInvestmentsTests")
        @DisplayName("should correct return the list of investments")
        void shouldCorrectReturnTheListOfInvestments(List<Investment> investments){
            investments.forEach(investment -> sut.addInvestment(wallet.getId(), investment));
            assertThat(sut.getInvestments(wallet.getId())).isEqualTo(investments);
        }

        public static Stream<Arguments> getDataToGetInvestmentsTests(){
            return Stream.of(
                    // Nenhum
                    Arguments.of(List.of()),
                    // Um investimento
                    Arguments.of(List.of(new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))))),
                    // 3 investimentos
                    Arguments.of(List.of(
                                    new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)))),
                            new Investment(2000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                            new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)))
                    )

            );
        }
    }

    @Nested
    class GetHistoryInvestments {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return all history investments on wallet")
        void shouldReturnAllHistoryInvestmentsOnWallet() {
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment1 = new Investment(1000, asset);
            Investment investment2 = new Investment(1500, asset);

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);
            sut.withdrawInvestment(wallet.getId(), investment1.getId(), date);

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

        // Unit Tests
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.getHistoryInvestments(UUID.randomUUID());
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull(){
            assertThrows(NullPointerException.class, () -> {
                sut.getHistoryInvestments(null);
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToGetHistoryInvestmentsTests")
        @DisplayName("Should correct return the list of investments on history")
        void shouldCorrectReturnTheListOfInvestmentsOnHistory(List<Investment> investments){
            investments.forEach(investment -> {
                sut.addInvestment(wallet.getId(), investment);
                sut.withdrawInvestment(wallet.getId(), investment.getId(), date);
            });
            assertThat(sut.getHistoryInvestments(wallet.getId())).isEqualTo(investments);
        }

        public static Stream<Arguments> getDataToGetHistoryInvestmentsTests() {
            return Stream.of(
                    // Nenhum
                    Arguments.of(List.of()),
                    // Um investimento
                    Arguments.of(List.of(new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))))),
                    // 3 investimentos
                    Arguments.of(List.of(
                                    new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1)))),
                            new Investment(2000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                            new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)))
                    )

            );
        }
    }

    @Nested
    class HistoryFilter{
        
        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return investments when filtered by asset type")
        void shouldReturnInvestmentsWhenFilteredByAssetType(){
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));
            Investment investment3 = new Investment(1500, new Asset("Banco Itau", LCI, 0.01, date.plusYears(1)));

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);
            sut.addInvestment(wallet.getId(), investment3);

            sut.withdrawInvestment(wallet.getId(), investment1.getId(), date);
            sut.withdrawInvestment(wallet.getId(), investment2.getId(), date);
            sut.withdrawInvestment(wallet.getId(), investment3.getId(), date);

            List<Investment> result = sut.filterHistory(wallet.getId(), CDB);

            assertThat(result.size()).isEqualTo(2);
        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should return investments when filtered by date")
        void shouldReturnInvestmentsWhenFilteredByDate(){
            LocalDate initialDate = date.minusMonths(1);
            LocalDate finalDate = date.plusMonths(1);

            Investment investment = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            sut.addInvestment(wallet.getId(), investment);
            sut.withdrawInvestment(wallet.getId(), investment.getId(), date);

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
            investments.forEach(investment -> sut.withdrawInvestment(wallet.getId(), investment.getId(), date));

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
            investments.forEach(investment -> sut.withdrawInvestment(wallet.getId(), investment.getId(), date));

            List<Investment> result = sut.filterHistory(wallet.getId(), initialDate, finalDate);

            assertThat(result).isEqualTo(List.of());
        }

        private static Stream<Arguments> provideScenariosForEmptyTypeFilterHistory(){
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), CDB),
                    Arguments.of(List.of(investment1, investment2), LCI)
            );
        }

        private static Stream<Arguments> provideScenariosForEmptyDateFilterHistory(){
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), date.plusMonths(1), date.plusMonths(2)),
                    Arguments.of(List.of(investment1, investment2), date.plusMonths(1), date.plusMonths(2))
            );
        }

        // Unit Tests
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterHistory(UUID.randomUUID(), CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull(){
            assertThrows(NullPointerException.class, () -> {
                sut.filterHistory(null, CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException if asset type is null")
        void shouldReturnNullPointerExceptionIfAssetTypeIsNull(){
            assertThrows(NullPointerException.class, () -> {
                sut.filterHistory(wallet.getId(), null);
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("provideScenariosForEmptyTypeFilterHistory")
        @DisplayName("Should an empty list when has no history data with this filter")
        void shouldAnEmptyListWhenHasNoHistoryDataWithThisFilter(List<Investment> investments, AssetType assetType){
            investments.forEach(investment -> {
                sut.addInvestment(wallet.getId(), investment);
                sut.withdrawInvestment(wallet.getId(), investment.getId(), date);
            });

            assertThat(sut.filterHistory(wallet.getId(), assetType)).isEqualTo(List.of());
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToListOfInvestmentsAndTypeFilter")
        @DisplayName("Should correct return the list of investments on history with this filter")
        void shouldCorrectReturnTheListOfInvestmentsOnHistoryWithThisFilter(List<Investment> investments, AssetType assetType, List<Investment> expectedResult){
            investments.forEach(investment -> {
                sut.addInvestment(wallet.getId(), investment);
                sut.withdrawInvestment(wallet.getId(), investment.getId(), date);
            });

            List<Investment> actualImmutable = sut.filterHistory(wallet.getId(), assetType);
            Comparator<Investment> byId = Comparator.comparing(Investment::getId);

            List<Investment> actual = new ArrayList<>(actualImmutable);
            List<Investment> expected = new ArrayList<>(expectedResult);
            actual.sort(byId);
            expected.sort(byId);

            assertThat(actual).isEqualTo(expected);
        }

        public static Stream<Arguments> getDataToListOfInvestmentsAndTypeFilter(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.of(2026, 1, 1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.of(2026, 1, 1));
            Investment investmentLCI = new Investment(1000, assetLCI);
            return Stream.of(
                    Arguments.of(List.of(investmentCDB, investmentLCI), CDB, List.of(investmentCDB)),
                    Arguments.of(List.of(investmentCDB, investmentCDB2, investmentLCI), CDB, List.of(investmentCDB, investmentCDB2))
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
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
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
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Investment investment = new Investment(1000, assetCDB);
            sut.addInvestment(wallet.getId(), investment);
            List<Investment> result = sut.filterActiveInvestments(wallet.getId(), date.minusMonths(1), date.plusMonths(1));
            assertThat(result.size()).isEqualTo(1);
        }

        private static Stream<Arguments> provideScenariosForEmptyTypeFilterActiveInvestments(){
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), CDB),
                    Arguments.of(List.of(investment1, investment2), LCI)
            );
        }

        private static Stream<Arguments> provideScenariosForEmptyDateFilterActiveInvestments(){
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), date.plusMonths(1), date.plusMonths(2)),
                    Arguments.of(List.of(investment1, investment2), date.plusMonths(1), date.plusMonths(2))
            );
        }

        // Unit Tests
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterActiveInvestments(UUID.randomUUID(), CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull(){
            assertThrows(NullPointerException.class, () -> {
                sut.filterActiveInvestments(null, CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException if asset type is null")
        void shouldReturnNullPointerExceptionIfAssetTypeIsNull(){
            assertThrows(NullPointerException.class, () -> {
                sut.filterActiveInvestments(wallet.getId(), null);
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("provideScenariosForEmptyTypeFilterActiveInvestments")
        @DisplayName("Should an empty list when has no data with this filter")
        void shouldAnEmptyListWhenHasNoDataWithThisFilter(List<Investment> investments, AssetType assetType){
            investments.forEach(investment -> {
                sut.addInvestment(wallet.getId(), investment);
            });

            assertThat(sut.filterActiveInvestments(wallet.getId(), assetType)).isEqualTo(List.of());
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToListOfInvestmentsAndTypeFilter")
        @DisplayName("Should correct return the list of investments with this filter")
        void shouldCorrectReturnTheListOfInvestmentsWithThisFilter(List<Investment> investments, AssetType assetType, List<Investment> expectedResult){
            investments.forEach(investment -> {
                sut.addInvestment(wallet.getId(), investment);
            });

            Comparator<Investment> byId = Comparator.comparing(Investment::getId);
            List<Investment> actualImmutable = sut.filterActiveInvestments(wallet.getId(), assetType);

            List<Investment> actual = new ArrayList<>(actualImmutable);
            List<Investment> expected = new ArrayList<>(expectedResult);
            actual.sort(byId);
            expected.sort(byId);

            assertThat(actual).isEqualTo(expected);
        }

        public static Stream<Arguments> getDataToListOfInvestmentsAndTypeFilter(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.of(2026, 1, 1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.of(2026, 1, 1));
            Investment investmentLCI = new Investment(1000, assetLCI);
            return Stream.of(
                    Arguments.of(List.of(investmentCDB, investmentLCI), CDB, List.of(investmentCDB)),
                    Arguments.of(List.of(investmentCDB, investmentCDB2, investmentLCI), CDB, List.of(investmentCDB, investmentCDB2))
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
                sut.generateReport(wallet.getId(), date);
            });
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should throw NoSuchElementException when there is no wallet registered")
        void shouldThrowNoSuchElementExceptionWhenThereIsNoWalletRegistered(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.generateReport(UUID.randomUUID(), date);
            });
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return report when there is investments")
        void shouldReturnReportWhenThereIsInvestments(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Asset assetTesouroDireto = new Asset("Banco BMG", TESOURO_DIRETO, 0.1, date.plusMonths(2));
            Investment investmentCDB1 = createInvestmentWithPurchaseDate(1000, assetCDB, date);
            Investment investmentCDB2 = createInvestmentWithPurchaseDate(1500, assetCDB, date);
            Investment investmentTS1 = createInvestmentWithPurchaseDate(1500, assetTesouroDireto, date);
            Investment investmentTS2 = createInvestmentWithPurchaseDate(1500, assetTesouroDireto, date);
            Investment investmentTS3 = createInvestmentWithPurchaseDate(1500, assetTesouroDireto, date);

            sut.addInvestment(wallet.getId(), investmentCDB1);
            sut.addInvestment(wallet.getId(), investmentCDB2);
            sut.addInvestment(wallet.getId(), investmentTS1);
            sut.addInvestment(wallet.getId(), investmentTS2);
            sut.addInvestment(wallet.getId(), investmentTS3);
            sut.withdrawInvestment(wallet.getId(), investmentTS1.getId(), date);
            sut.withdrawInvestment(wallet.getId(), investmentCDB2.getId(), date);

            SoftAssertions softly = new SoftAssertions();
            String report = sut.generateReport(wallet.getId(), date);

            softly.assertThat(report).contains(investmentCDB1.toString());
            softly.assertThat(report).contains(investmentTS1.toString());
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
