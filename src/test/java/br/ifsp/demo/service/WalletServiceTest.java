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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
        @DisplayName("Should successfully register an investment")
        void shouldSuccessfullyRegisterAnInvestment(){
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment1 = new Investment(100, asset);
            Investment investment2 = new Investment(100, asset);

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);
            assertThat(sut.getInvestments(wallet.getId())
                    .containsAll(List.of(investment1, investment2)))
                    .isTrue();
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should throw NoSuchElementException for non existent Wallet")
        void shouldThrowNoSuchElementExceptionForNonExistentWallet(){
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);

            assertThrows(NoSuchElementException.class, () -> { sut.addInvestment(UUID.randomUUID(), investment); });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should throw NullPointerException when Wallet or Investment is null")
        void shouldThrowNullPointerExceptionWhenWalletOrInvestmentIsNull(){
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);
            SoftAssertions softly = new SoftAssertions();

            softly.assertThatThrownBy(() -> sut.addInvestment(null, investment))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Wallet id cannot be null");
            softly.assertThatThrownBy(() -> sut.addInvestment(wallet.getId(), null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Investment cannot be null");
            softly.assertAll();
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should throw IllegalArgumentException when Investment id already exists on Wallet")
        void shouldThrowIllegalArgumentExceptionWhenInvestmentIdAlreadyExistsInWallet(){
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);

            sut.addInvestment(wallet.getId(), investment);
            assertThatThrownBy(() -> sut.addInvestment(wallet.getId(), investment))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Investment already exists in the wallet: " + investment.getId());
        }
    }

    @Nested
    class WithdrawInvestment {

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should withdraw an investment")
        void shouldWithdrawAnInvestment(){
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
        
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NullPointerException when some parameter is null")
        void shouldReturnNullPointerExceptionWhenSomeParameterIsNull(){
            UUID investmentId = UUID.randomUUID();
            SoftAssertions softly = new SoftAssertions();

            softly.assertThatThrownBy(() -> sut.withdrawInvestment(null, investmentId, date))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Wallet id cannot be null");
            softly.assertThatThrownBy(() -> sut.withdrawInvestment(wallet.getId(), null, date))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Investment id cannot be null");
            softly.assertAll();
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

        // Unit Tests by type
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

        // Unit Tests by Date
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException if wallet does not exists when filter by date")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExistsWhenFilterByDate(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterHistory(UUID.randomUUID(), date.plusMonths(1), date.plusMonths(2));
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getInvalidDataToFilterHistory")
        @DisplayName("Should return NoSuchElementException when some parameter is null")
        void shouldReturnNoSuchElementExceptionWhenSomeParameterIsNull(UUID walletId, LocalDate initialDate, LocalDate finalDate){
            assertThrows(NullPointerException.class, () -> {
                sut.filterHistory(walletId, initialDate, finalDate);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return an empty list when history is empty")
        void shouldReturnAnEmptyListWhenHistoryIsEmpty(){
            List<Investment> result = sut.filterHistory(wallet.getId(), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2));

            assertThat(result).isEqualTo(List.of());
        }

        public static Stream<Arguments> getInvalidDataToFilterHistory(){
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);

            return Stream.of(
                    Arguments.of(null, LocalDate.of(2025, 4, 25), LocalDate.of(2025, 5, 25)),
                    Arguments.of(wallet.getId(), null, LocalDate.of(2025, 5, 25)),
                    Arguments.of(wallet.getId(), LocalDate.of(2025, 4, 25), null)
            );
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToReturnEmptyListWhenFilterByDate")
        @DisplayName("Should return an empty list when there is no history data in this filter")
        void shouldReturnAnEmptyListWhenThereIsNoHistoryDataInThisFilter(LocalDate initialDate, LocalDate finalDate){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            sut.addInvestment(wallet.getId(), investmentCDB);
            sut.withdrawInvestment(wallet.getId(), investmentCDB.getId(), LocalDate.now());

            assertThat(sut.filterHistory(wallet.getId(), initialDate, finalDate)).isEqualTo(List.of());
        }

        public static Stream<Arguments> getDataToReturnEmptyListWhenFilterByDate(){
            return Stream.of(
                    // um item com o withdraw um dia antes da data inicial
                    Arguments.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(20)),
                    // um item com o withdraw um dia depois da data final
                    Arguments.of(LocalDate.now().minusDays(20), LocalDate.now().minusDays(1))

            );
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return a list with more then one item with this filter")
        void shouldReturnAListWithMoreThenOneItemWithThisFilter(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1500, assetLCI);

            sut.addInvestment(wallet.getId(), investmentCDB);
            sut.addInvestment(wallet.getId(), investmentCDB2);
            sut.addInvestment(wallet.getId(), investmentLCI);
            sut.withdrawInvestment(wallet.getId(), investmentCDB.getId(), LocalDate.now().plusDays(3));
            sut.withdrawInvestment(wallet.getId(), investmentCDB2.getId(), LocalDate.now().plusDays(10));
            sut.withdrawInvestment(wallet.getId(), investmentLCI.getId(), LocalDate.now().plusDays(1));

            Comparator<Investment> byId = Comparator.comparing(Investment::getId);

            List<Investment> resultImmutable = sut.filterHistory(
                    wallet.getId(),
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(1));
            List<Investment> expectedResultImmutable = List.of(investmentCDB, investmentCDB2, investmentLCI);

            List<Investment> expectedResult = new ArrayList<>(expectedResultImmutable);
            List<Investment> result = new ArrayList<>(resultImmutable);
            expectedResult.sort(byId);
            result.sort(byId);

            assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToReturnListOfOneElementWhenFilterByDate")
        @DisplayName("Should return a list with one item when filter by limit dates")
        void shouldReturnAListWithOneItemWhenFilterByLimitDates(LocalDate initialDate, LocalDate finalDate){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);

            sut.addInvestment(wallet.getId(), investmentCDB);
            sut.withdrawInvestment(wallet.getId(), investmentCDB.getId(), LocalDate.now().plusDays(10));

            assertThat(sut.filterHistory(wallet.getId(), initialDate, finalDate)).isEqualTo(List.of(investmentCDB));
        }

        public static Stream<Arguments> getDataToReturnListOfOneElementWhenFilterByDate(){
            return Stream.of(
                    //Exatamente a mesma data inicial
                    Arguments.of(LocalDate.now() , LocalDate.now().plusDays(15)),
                    // Um dia depois da data inicial
                    Arguments.of(LocalDate.now().minusDays(1), LocalDate.now().plusDays(14)),
                    // Um dia antes da data final
                    Arguments.of(LocalDate.now().minusDays(15), LocalDate.now().plusDays(9)),
                    // Mesmo dia da data final
                    Arguments.of(LocalDate.now().minusDays(13), LocalDate.now().plusDays(10))
            );
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("ShouldReturn2of3InvestmentsOnHistoryWithThisFilter")
        void shouldReturn2Of3InvestmentsOnHistoryWithThisFilter(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = InvestmentFactory.createInvestmentWithPurchaseDate(1000, assetCDB, LocalDate.now().minusYears(1));
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1000, assetLCI);

            sut.addInvestment(wallet.getId(), investmentCDB);
            sut.addInvestment(wallet.getId(), investmentCDB2);
            sut.addInvestment(wallet.getId(), investmentLCI);
            sut.withdrawInvestment(wallet.getId(), investmentCDB.getId(), LocalDate.now().minusMonths(5));
            sut.withdrawInvestment(wallet.getId(), investmentCDB2.getId(), LocalDate.now().plusDays(10));
            sut.withdrawInvestment(wallet.getId(), investmentLCI.getId(), LocalDate.now().plusDays(10));

            List<Investment> result = sut.filterHistory(wallet.getId(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
            assertThat(result.size()).isEqualTo(2);
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

        // Unit Tests by type
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

        // Unit Tests by Date
        @Test
        @Tag("UnitTest")
        @DisplayName("Should return NoSuchElementException if wallet does not exists when filter by date")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExistsWhenFilterByDate(){
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterActiveInvestments(UUID.randomUUID(), date.plusMonths(1), date.plusMonths(2));
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getInvalidDataToFilterInvestments")
        @DisplayName("Should return NoSuchElementException when some parameter is null")
        void shouldReturnNoSuchElementExceptionWhenSomeParameterIsNull(UUID walletId, LocalDate initialDate, LocalDate finalDate){
            assertThrows(NullPointerException.class, () -> {
                sut.filterActiveInvestments(walletId, initialDate, finalDate);
            });
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return an empty list when investments is empty")
        void shouldReturnAnEmptyListWhenInvestmentsIsEmpty(){
            List<Investment> result = sut.filterActiveInvestments(wallet.getId(), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2));

            assertThat(result).isEqualTo(List.of());
        }

        public static Stream<Arguments> getInvalidDataToFilterInvestments(){
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);

            return Stream.of(
                    Arguments.of(null, LocalDate.of(2025, 4, 25), LocalDate.of(2025, 5, 25)),
                    Arguments.of(wallet.getId(), null, LocalDate.of(2025, 5, 25)),
                    Arguments.of(wallet.getId(), LocalDate.of(2025, 4, 25), null)
            );
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToReturnEmptyListWhenFilterByDate")
        @DisplayName("Should return an empty list when there is no data in this filter")
        void shouldReturnAnEmptyListWhenThereIsNoDataInThisFilter(LocalDate initialDate, LocalDate finalDate){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            sut.addInvestment(wallet.getId(), investmentCDB);

            assertThat(sut.filterActiveInvestments(wallet.getId(), initialDate, finalDate)).isEqualTo(List.of());
        }

        public static Stream<Arguments> getDataToReturnEmptyListWhenFilterByDate(){
            return Stream.of(
                    // um item com o withdraw um dia antes da data inicial
                    Arguments.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(20)),
                    // um item com o withdraw um dia depois da data final
                    Arguments.of(LocalDate.now().minusDays(20), LocalDate.now().minusDays(1))

            );
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should return a list with more then one item with this filter")
        void shouldReturnAListWithMoreThenOneItemWithThisFilter(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1500, assetLCI);

            sut.addInvestment(wallet.getId(), investmentCDB);
            sut.addInvestment(wallet.getId(), investmentCDB2);
            sut.addInvestment(wallet.getId(), investmentLCI);

            Comparator<Investment> byId = Comparator.comparing(Investment::getId);

            List<Investment> resultImmutable = sut.filterActiveInvestments(
                    wallet.getId(),
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(1));
            List<Investment> expectedResultImmutable = List.of(investmentCDB, investmentCDB2, investmentLCI);

            List<Investment> expectedResult = new ArrayList<>(expectedResultImmutable);
            List<Investment> result = new ArrayList<>(resultImmutable);
            expectedResult.sort(byId);
            result.sort(byId);

            assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @MethodSource("getDataToReturnListOfOneElementWhenFilterByDate")
        @DisplayName("Should return a list with one item when filter by limit dates")
        void shouldReturnAListWithOneItemWhenFilterByLimitDates(LocalDate initialDate, LocalDate finalDate){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);

            sut.addInvestment(wallet.getId(), investmentCDB);

            assertThat(sut.filterActiveInvestments(wallet.getId(), initialDate, finalDate)).isEqualTo(List.of(investmentCDB));
        }

        public static Stream<Arguments> getDataToReturnListOfOneElementWhenFilterByDate(){
            return Stream.of(
                    //Exatamente a mesma data inicial
                    Arguments.of(LocalDate.now() , LocalDate.now().plusDays(15)),
                    // Um dia depois da data inicial
                    Arguments.of(LocalDate.now().minusDays(1), LocalDate.now().plusDays(14)),
                    // Um dia antes da data final
                    Arguments.of(LocalDate.now().minusDays(15), LocalDate.now().plusDays(9)),
                    // Mesmo dia da data final
                    Arguments.of(LocalDate.now().minusDays(13), LocalDate.now().plusDays(10))
            );
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("ShouldReturn2of3InvestmentsWithThisFilter")
        void shouldReturn2Of3InvestmentsWithThisFilter(){
            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB =  new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1000, assetLCI);

            sut.addInvestment(wallet.getId(), investmentCDB);
            sut.addInvestment(wallet.getId(), investmentCDB2);
            sut.addInvestment(wallet.getId(), investmentLCI);
            sut.withdrawInvestment(wallet.getId(), investmentCDB.getId(), LocalDate.now().plusDays(10));

            List<Investment> result = sut.filterActiveInvestments(wallet.getId(), LocalDate.now().minusDays(5), LocalDate.now());
            assertThat(result.size()).isEqualTo(2);
        }
    }

    @Nested
    class Report {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should throw NoSuchElementException when there are no investments")
        void shouldThrowNoSuchElementExceptionWhenThereAreNoInvestments(){
            assertThatThrownBy(() -> { sut.generateReport(wallet.getId(), date);})
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("There are no investments in this wallet");
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should throw NoSuchElementException when Wallet does not exist")
        void shouldThrowNoSuchElementExceptionWhenWalletDoesNotExist(){
            UUID walletId = UUID.randomUUID();
            assertThatThrownBy(() -> { sut.generateReport(walletId, date);})
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Wallet not found: " + walletId);
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Should throw NullPointerException when Wallet id is null")
        void shouldThrowNullPointerExceptionWhenWalletIdIsNull(){
            assertThatThrownBy(() -> { sut.generateReport(null, date);})
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Wallet id cannot be null");
        }

        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @MethodSource("provideWalletScenarios")
        @DisplayName("Should return report when there is investments")
        void shouldReturnReportWhenThereIsInvestments(Wallet wallet, List<String> expectedParts){
            InMemoryWalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);
            WalletService sut = new WalletService(inMemoryRepository);
            SoftAssertions softly = new SoftAssertions();

            String report = sut.generateReport(wallet.getId(), date);

            expectedParts.forEach(expectedPart -> {
                softly.assertThat(report).contains(expectedPart);
            });
            softly.assertAll();
        }

        private static Stream<Arguments> provideWalletScenarios() {
            return Stream.of(
                    Arguments.of(walletWithHistoryOnly(), expectedReportWithHistoryOnly()),
                    Arguments.of(walletWithActiveInvestmentsOnly(), expectedReportWithInvestmentsOnly()),
                    Arguments.of(walletWithEverything(), expectedCompleteReport())
            );
        }

        private static Wallet walletWithHistoryOnly() {
            LocalDate date = LocalDate.of(2025, 4, 25);
            Wallet wallet = new Wallet();

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Asset assetTesouro = new Asset("Banco Inter", TESOURO_DIRETO, 0.1, date.plusMonths(2));

            Investment investmentCDB = createInvestmentWithPurchaseDate(1000, assetCDB, date);
            Investment investmentTesouro = createInvestmentWithPurchaseDate(1500, assetTesouro, date);
            investmentCDB.setWithdrawDate(date);
            investmentTesouro.setWithdrawDate(date);

            wallet.addInvestmentOnHistory(investmentCDB);
            wallet.addInvestmentOnHistory(investmentTesouro);
            return wallet;
        }

        private static Wallet walletWithActiveInvestmentsOnly() {
            LocalDate date = LocalDate.of(2025, 4, 25);
            Wallet wallet = new Wallet();

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Asset assetTesouro = new Asset("Banco Inter", TESOURO_DIRETO, 0.1, date.plusMonths(2));

            Investment investmentCDB = createInvestmentWithPurchaseDate(2000, assetCDB, date);
            Investment investmentTesouro = createInvestmentWithPurchaseDate(3000, assetTesouro, date);

            wallet.addInvestment(investmentCDB);
            wallet.addInvestment(investmentTesouro);
            return wallet;
        }

        private static Wallet walletWithEverything() {
            Wallet wallet = walletWithHistoryOnly();
            Wallet investmentOnlyWallet = walletWithActiveInvestmentsOnly();

            investmentOnlyWallet.getInvestments().forEach(wallet::addInvestment);
            return wallet;
        }

        private static List<String> expectedReportWithHistoryOnly() {
            return List.of(
                    "Initial value = R$ 1000,00 | Asset name = Banco Inter | Type: CDB",
                    "Initial value = R$ 1500,00 | Asset name = Banco Inter | Type: TESOURO_DIRETO",
                    "Current Total Balance: R$ 2500,00",
                    "Future Investments Balance: R$ 0,00",
                    "Total Balance (Current + Future): R$ 2500,00",
                    "| TESOURO_DIRETO: 50,00% | CDB: 50,00% | LCI: 0,00% | LCA: 0,00% | CRI: 0,00% | CRA: 0,00%"
            );
        }

        private static List<String> expectedReportWithInvestmentsOnly() {
            return List.of(
                    "Initial value = R$ 2000,00 | Asset name = Banco Inter | Type: CDB",
                    "Initial value = R$ 3000,00 | Asset name = Banco Inter | Type: TESOURO_DIRETO",
                    "Current Total Balance: R$ 5000,00",
                    "Future Investments Balance: R$ 6069,25",
                    "Total Balance (Current + Future): R$ 11069,25",
                    "| TESOURO_DIRETO: 50,00% | CDB: 50,00% | LCI: 0,00% | LCA: 0,00% | CRI: 0,00% | CRA: 0,00%"
            );
        }

        private static List<String> expectedCompleteReport() {
            return List.of(
                    "Initial value = R$ 2000,00 | Asset name = Banco Inter | Type: CDB",
                    "Initial value = R$ 3000,00 | Asset name = Banco Inter | Type: TESOURO_DIRETO",
                    "Initial value = R$ 1000,00 | Asset name = Banco Inter | Type: CDB",
                    "Initial value = R$ 1500,00 | Asset name = Banco Inter | Type: TESOURO_DIRETO",
                    "Current Total Balance: R$ 7500,00",
                    "Future Investments Balance: R$ 6069,25",
                    "Total Balance (Current + Future): R$ 13569,25",
                    "| TESOURO_DIRETO: 50,00% | CDB: 50,00% | LCI: 0,00% | LCA: 0,00% | CRI: 0,00% | CRA: 0,00%",
                    "| TESOURO_DIRETO: 50,00% | CDB: 50,00% | LCI: 0,00% | LCA: 0,00% | CRI: 0,00% | CRA: 0,00%"
            );
        }
    }
}
