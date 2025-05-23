package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.exception.EntityAlreadyExistsException;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static br.ifsp.demo.domain.AssetType.*;
import static br.ifsp.demo.domain.InvestmentFactory.createInvestmentWithPurchaseDate;
import static br.ifsp.demo.domain.InvestmentFactory.createInvestmentWithPurchaseDateAndMockWithdraw;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    private Wallet wallet;
    private LocalDate date;
    private User user;

    @Mock
    private WalletRepository repository;
    @Mock
    private JpaUserRepository userRepository;
    @InjectMocks
    private WalletService sut;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        date = LocalDate.of(2025, 4, 25);
        user = new User();
        user.setId(UUID.randomUUID());
    }

    @Nested
    class RegisterInvestment {
        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @Tag("TDD")
        @DisplayName("Should successfully register an investment")
        void shouldSuccessfullyRegisterAnInvestment() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment1 = new Investment(100, asset);
            Investment investment2 = new Investment(100, asset);

            sut.addInvestment(user.getId(), investment1);
            sut.addInvestment(user.getId(), investment2);
            assertThat(sut.getInvestments(user.getId())
                    .containsAll(List.of(investment1, investment2)))
                    .isTrue();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should throw NoSuchElementException for non existent Wallet")
        void shouldThrowNoSuchElementExceptionForNonExistentWallet() {
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);

            UUID randomId = UUID.randomUUID();
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> {
                sut.addInvestment(randomId, investment);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should throw NullPointerException when Wallet or Investment is null")
        void shouldThrowNullPointerExceptionWhenWalletOrInvestmentIsNull() {
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);
            SoftAssertions softly = new SoftAssertions();

            softly.assertThatThrownBy(() -> sut.addInvestment(null, investment))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("User id cannot be null");
            softly.assertThatThrownBy(() -> sut.addInvestment(user.getId(), null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Investment cannot be null");
            softly.assertAll();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should throw EntityAlreadyExistsException when Investment id already exists on Wallet")
        void shouldThrowEntityAlreadyExistsExceptionWhenInvestmentIdAlreadyExistsInWallet() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);

            sut.addInvestment(user.getId(), investment);
            assertThatThrownBy(() -> sut.addInvestment(user.getId(), investment))
                    .isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessage("Investment already exists in the wallet: " + investment.getId());
        }
    }

    @Nested
    class WithdrawInvestment {
        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @Tag("TDD")
        @DisplayName("Should withdraw an investment")
        void shouldWithdrawAnInvestment() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(user.getId(), investment);

            sut.withdrawInvestment(user.getId(), investment.getId(), date);
            assertThat(sut.getHistoryInvestments(user.getId()).contains(investment)).isTrue();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @Tag("TDD")
        @DisplayName("Should return NoSuchElementException if the investment does not exist")
        void shouldReturnNoSuchElementExceptionIfTheInvestmentDoesNotExist() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(user.getId(), investment);

            assertThrows(NoSuchElementException.class, () -> {
                sut.withdrawInvestment(user.getId(), UUID.randomUUID(), date);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if the wallet does not exist")
        void shouldReturnNoSuchElementExceptionIfTheWalletDoesNotExist() {
            UUID randomId = UUID.randomUUID();
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment = new Investment(100, asset);

            assertThrows(NoSuchElementException.class, () -> {
                sut.withdrawInvestment(randomId, investment.getId(), date);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException when wallet has no investment")
        void shouldReturnNoSuchElementExceptionWhenWalletHasNoInvestment() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));

            assertThrows(NoSuchElementException.class, () -> {
                sut.withdrawInvestment(user.getId(), investment.getId(), date);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should mark investment as withdrawn and move to history when single investment in wallet")
        void shouldMarkInvestmentAsWithdrawnAndMoveToHistoryWhenSingleInvestmentInWallet() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            sut.addInvestment(user.getId(), investment);

            SoftAssertions softly = new SoftAssertions();
            sut.withdrawInvestment(user.getId(), investment.getId(), date);
            softly.assertThat(investment.isWithdrawn()).isTrue();
            softly.assertThat(sut.getHistoryInvestments(user.getId())).contains(investment);
            softly.assertAll();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should mark investment as withdrawn and move to history when many investments in wallet")
        void shouldMarkInvestmentAsWithdrawnAndMoveToHistoryWhenManyInvestmentsInWallet() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(150, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));
            Investment investment3 = new Investment(150, new Asset("Banco Itau", LCI, 0.01, date.plusYears(1)));
            sut.addInvestment(user.getId(), investment);
            sut.addInvestment(user.getId(), investment2);
            sut.addInvestment(user.getId(), investment3);

            SoftAssertions softly = new SoftAssertions();
            sut.withdrawInvestment(user.getId(), investment2.getId(), date);
            softly.assertThat(investment2.isWithdrawn()).isTrue();
            softly.assertThat(sut.getHistoryInvestments(user.getId())).contains(investment2);
            softly.assertAll();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException when some parameter is null")
        void shouldReturnNullPointerExceptionWhenSomeParameterIsNull() {
            UUID investmentId = UUID.randomUUID();
            SoftAssertions softly = new SoftAssertions();

            softly.assertThatThrownBy(() -> sut.withdrawInvestment(null, investmentId, date))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("User id cannot be null");
            softly.assertThatThrownBy(() -> sut.withdrawInvestment(user.getId(), null, date))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Investment id cannot be null");
            softly.assertAll();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should move investment to history when withdrawing")
        void shouldMoveInvestmentToHistoryWhenWithdrawing() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            SoftAssertions softly = new SoftAssertions();

            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            sut.addInvestment(user.getId(), investment);

            sut.withdrawInvestment(user.getId(), investment.getId(), date);
            List<Investment> history = sut.getHistoryInvestments(user.getId());

            softly.assertThat(history.size()).isEqualTo(1);
            softly.assertThat(history.getFirst()).isEqualTo(investment);
            softly.assertThat(history.getFirst().getWithdrawDate()).isNotNull();
            softly.assertAll();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should throw EntityAlreadyExistsException if addToHistory fails")
        void shouldThrowEntityAlreadyExistsExceptionIfAddToHistoryFails() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            Investment investment = new Investment(100, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));

            sut.addInvestment(user.getId(), investment);
            sut.withdrawInvestment(user.getId(), investment.getId(), date);

            EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> {
                sut.addInvestment(user.getId(), investment);
                sut.withdrawInvestment(user.getId(), investment.getId(), date);
            });

            assertThat(exception).isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessage("Investment already exists in the wallet: " + investment.getId());
        }
    }

    @Nested
    class GetInvestments {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return all investments on wallet")
        void shouldReturnAllInvestmentsOnWallet() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment1 = new Investment(1000, asset);
            Investment investment2 = new Investment(1500, asset);

            sut.addInvestment(user.getId(), investment1);
            sut.addInvestment(user.getId(), investment2);

            List<Investment> result = sut.getInvestments(user.getId());
            assertThat(result.size()).isEqualTo(2);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("shouldReturnAnEmptyListWhenThereIsNoInvestments")
        void shouldReturnAnEmptyListWhenThereIsNoInvestments() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            List<Investment> result = sut.getInvestments(user.getId());
            assertThat(result).isEqualTo(List.of());
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists() {
            UUID randomId = UUID.randomUUID();
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> {
                sut.getInvestments(randomId);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull() {
            assertThrows(NullPointerException.class, () -> {
                sut.getInvestments(null);
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Functional")
        @MethodSource("getDataToGetInvestmentsTests")
        @DisplayName("should correct return the list of investments")
        void shouldCorrectReturnTheListOfInvestments(List<Investment> investments) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> sut.addInvestment(user.getId(), investment));
            assertThat(sut.getInvestments(user.getId())).isEqualTo(investments);
        }

        public static Stream<Arguments> getDataToGetInvestmentsTests() {
            return Stream.of(
                    Arguments.of(List.of()),
                    Arguments.of(List.of(new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))))),
                    Arguments.of(List.of(
                            new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                            new Investment(2000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                            new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)))
                    ))
            );
        }
    }

    @Nested
    class GetHistoryInvestments {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return all history investments on wallet")
        void shouldReturnAllHistoryInvestmentsOnWallet() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset asset = new Asset("Banco Inter", CDB, 0.01, date.plusYears(1));
            Investment investment1 = new Investment(1000, asset);
            Investment investment2 = new Investment(1500, asset);

            sut.addInvestment(user.getId(), investment1);
            sut.addInvestment(user.getId(), investment2);
            sut.withdrawInvestment(user.getId(), investment1.getId(), date);

            List<Investment> result = sut.getHistoryInvestments(user.getId());
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("shouldReturnAnEmptyListWhenThereIsNoHistoryInvestments")
        void shouldReturnAnEmptyListWhenThereIsNoHistoryInvestments() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            List<Investment> result = sut.getHistoryInvestments(user.getId());
            assertThat(result).isEqualTo(List.of());
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists() {
            UUID randomId = UUID.randomUUID();
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> {
                sut.getHistoryInvestments(randomId);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull() {
            assertThrows(NullPointerException.class, () -> {
                sut.getHistoryInvestments(null);
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Functional")
        @MethodSource("getDataToGetHistoryInvestmentsTests")
        @DisplayName("Should correct return the list of investments on history")
        void shouldCorrectReturnTheListOfInvestmentsOnHistory(List<Investment> investments) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> {
                sut.addInvestment(user.getId(), investment);
                sut.withdrawInvestment(user.getId(), investment.getId(), date);
            });
            assertThat(sut.getHistoryInvestments(user.getId())).isEqualTo(investments);
        }

        public static Stream<Arguments> getDataToGetHistoryInvestmentsTests() {
            return Stream.of(
                    Arguments.of(List.of()),
                    Arguments.of(List.of(new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))))),
                    Arguments.of(List.of(
                            new Investment(1000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                            new Investment(2000, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                            new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, LocalDate.now().plusYears(1)))
                    ))
            );
        }
    }

    @Nested
    class HistoryFilter {
        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @Tag("TDD")
        @DisplayName("Should return investments when filtered by asset type")
        void shouldReturnInvestmentsWhenFilteredByAssetType() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));
            Investment investment3 = new Investment(1500, new Asset("Banco Itau", LCI, 0.01, date.plusYears(1)));

            sut.addInvestment(user.getId(), investment1);
            sut.addInvestment(user.getId(), investment2);
            sut.addInvestment(user.getId(), investment3);

            sut.withdrawInvestment(user.getId(), investment1.getId(), date);
            sut.withdrawInvestment(user.getId(), investment2.getId(), date);
            sut.withdrawInvestment(user.getId(), investment3.getId(), date);

            List<Investment> result = sut.filterHistory(user.getId(), CDB);

            assertThat(result.size()).isEqualTo(2);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @Tag("TDD")
        @DisplayName("Should return investments when filtered by date")
        void shouldReturnInvestmentsWhenFilteredByDate() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            LocalDate initialDate = date.minusMonths(1);
            LocalDate finalDate = date.plusMonths(1);

            Investment investment = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            sut.addInvestment(user.getId(), investment);
            sut.withdrawInvestment(user.getId(), investment.getId(), date);

            List<Investment> result = sut.filterHistory(user.getId(), initialDate, finalDate);

            assertThat(result.size()).isEqualTo(1);
        }

        @ParameterizedTest
        @MethodSource("provideScenariosForEmptyTypeFilterHistory")
        @Tag("UnitTest")
        @Tag("Functional")
        @Tag("TDD")
        @DisplayName("Should return an empty list when filter has no match")
        void shouldReturnAnEmptyListWhenTypeFilterHasNoMatch(List<Investment> investments, AssetType assetType) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> sut.addInvestment(user.getId(), investment));
            investments.forEach(investment -> sut.withdrawInvestment(user.getId(), investment.getId(), date));

            List<Investment> result = sut.filterHistory(user.getId(), assetType);

            assertThat(result).isEqualTo(List.of());
        }

        @ParameterizedTest
        @MethodSource("provideScenariosForEmptyDateFilterHistory")
        @Tag("UnitTest")
        @Tag("Functional")
        @Tag("TDD")
        @DisplayName("Should return an empty list when date filter has no match")
        void shouldReturnAnEmptyListWhenDateFilterHasNoMatch(List<Investment> investments, LocalDate initialDate, LocalDate finalDate) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> sut.addInvestment(user.getId(), investment));
            investments.forEach(investment -> sut.withdrawInvestment(user.getId(), investment.getId(), date));

            List<Investment> result = sut.filterHistory(user.getId(), initialDate, finalDate);

            assertThat(result).isEqualTo(List.of());
        }

        private static Stream<Arguments> provideScenariosForEmptyTypeFilterHistory() {
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), CDB),
                    Arguments.of(List.of(investment1, investment2), LCI)
            );
        }

        private static Stream<Arguments> provideScenariosForEmptyDateFilterHistory() {
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), date.plusMonths(1), date.plusMonths(2)),
                    Arguments.of(List.of(investment1, investment2), date.plusMonths(1), date.plusMonths(2))
            );
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists() {
            UUID randomId = UUID.randomUUID();
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterHistory(randomId, CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull() {
            assertThrows(NullPointerException.class, () -> {
                sut.filterHistory(null, CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if asset type is null")
        void shouldReturnNullPointerExceptionIfAssetTypeIsNull() {
            assertThrows(NullPointerException.class, () -> {
                sut.filterHistory(user.getId(), null);
            });
        }

        @ParameterizedTest
        @MethodSource("provideScenariosForEmptyTypeFilterHistory")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should an empty list when has no history data with this filter")
        void shouldAnEmptyListWhenHasNoHistoryDataWithThisFilter(List<Investment> investments, AssetType assetType) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> {
                sut.addInvestment(user.getId(), investment);
                sut.withdrawInvestment(user.getId(), investment.getId(), date);
            });

            assertThat(sut.filterHistory(user.getId(), assetType)).isEqualTo(List.of());
        }

        @ParameterizedTest
        @MethodSource("getDataToListOfInvestmentsAndTypeFilter")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should correct return the list of investments on history with this filter")
        void shouldCorrectReturnTheListOfInvestmentsOnHistoryWithThisFilter(List<Investment> investments, AssetType assetType, List<Investment> expected) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> {
                sut.addInvestment(user.getId(), investment);
                sut.withdrawInvestment(user.getId(), investment.getId(), date);
            });

            List<Investment> actual = sut.filterHistory(user.getId(), assetType);
            assertThat(actual).isEqualTo(expected);
        }

        public static Stream<Arguments> getDataToListOfInvestmentsAndTypeFilter() {
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

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if wallet does not exists when filter by date")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExistsWhenFilterByDate() {
            LocalDate start = date.plusMonths(1);
            LocalDate end = date.plusMonths(2);
            UUID randomId = UUID.randomUUID();
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterHistory(randomId, start, end);
            });
        }

        @ParameterizedTest
        @MethodSource("getInvalidDataToFilterHistory")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException when some parameter is null")
        void shouldReturnNoSuchElementExceptionWhenSomeParameterIsNull(UUID walletId, LocalDate initialDate, LocalDate finalDate) {
            assertThrows(NullPointerException.class, () -> {
                sut.filterHistory(walletId, initialDate, finalDate);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return an empty list when history is empty")
        void shouldReturnAnEmptyListWhenHistoryIsEmpty() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            LocalDate start = LocalDate.now().plusMonths(1);
            LocalDate end = LocalDate.now().plusMonths(2);
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            List<Investment> result = sut.filterHistory(user.getId(), start, end);

            assertThat(result).isEqualTo(List.of());
        }

        public static Stream<Arguments> getInvalidDataToFilterHistory() {
            return Stream.of(
                    Arguments.of(null, LocalDate.of(2025, 4, 25), LocalDate.of(2025, 5, 25)),
                    Arguments.of(new Wallet().getId(), null, LocalDate.of(2025, 5, 25)),
                    Arguments.of(new Wallet().getId(), LocalDate.of(2025, 4, 25), null)
            );
        }

        @ParameterizedTest
        @MethodSource("getDataToReturnEmptyListWhenFilterByDate")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return an empty list when there is no history data in this filter")
        void shouldReturnAnEmptyListWhenThereIsNoHistoryDataInThisFilter(LocalDate initialDate, LocalDate finalDate) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            sut.addInvestment(user.getId(), investmentCDB);
            sut.withdrawInvestment(user.getId(), investmentCDB.getId(), LocalDate.now());

            assertThat(sut.filterHistory(user.getId(), initialDate, finalDate)).isEqualTo(List.of());
        }

        public static Stream<Arguments> getDataToReturnEmptyListWhenFilterByDate() {
            return Stream.of(
                    Arguments.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(20)),
                    Arguments.of(LocalDate.now().minusDays(20), LocalDate.now().minusDays(1))
            );
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return a list with more then one item with this filter")
        void shouldReturnAListWithMoreThenOneItemWithThisFilter() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1500, assetLCI);

            sut.addInvestment(user.getId(), investmentCDB);
            sut.addInvestment(user.getId(), investmentCDB2);
            sut.addInvestment(user.getId(), investmentLCI);

            sut.withdrawInvestment(user.getId(), investmentCDB.getId(), LocalDate.now().plusDays(3));
            sut.withdrawInvestment(user.getId(), investmentCDB2.getId(), LocalDate.now().plusDays(10));
            sut.withdrawInvestment(user.getId(), investmentLCI.getId(), LocalDate.now().plusDays(1));

            List<Investment> result = sut.filterHistory(
                    user.getId(),
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(1));
            List<Investment> expected = List.of(investmentCDB, investmentCDB2, investmentLCI);

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @MethodSource("getDataToReturnListOfOneElementWhenFilterByDate")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return a list with one item when filter by limit dates")
        void shouldReturnAListWithOneItemWhenFilterByLimitDates(LocalDate initialDate, LocalDate finalDate) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);

            sut.addInvestment(user.getId(), investmentCDB);
            sut.withdrawInvestment(user.getId(), investmentCDB.getId(), LocalDate.now().plusDays(10));

            assertThat(sut.filterHistory(user.getId(), initialDate, finalDate)).isEqualTo(List.of(investmentCDB));
        }

        public static Stream<Arguments> getDataToReturnListOfOneElementWhenFilterByDate() {
            return Stream.of(
                    Arguments.of(LocalDate.now(), LocalDate.now().plusDays(15)),
                    Arguments.of(LocalDate.now().minusDays(1), LocalDate.now().plusDays(14)),
                    Arguments.of(LocalDate.now().minusDays(15), LocalDate.now().plusDays(9)),
                    Arguments.of(LocalDate.now().minusDays(13), LocalDate.now().plusDays(10))
            );
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("ShouldReturn2of3InvestmentsOnHistoryWithThisFilter")
        void shouldReturn2Of3InvestmentsOnHistoryWithThisFilter() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = createInvestmentWithPurchaseDate(1000, assetCDB, LocalDate.now().minusYears(1));
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1000, assetLCI);

            sut.addInvestment(user.getId(), investmentCDB);
            sut.addInvestment(user.getId(), investmentCDB2);
            sut.addInvestment(user.getId(), investmentLCI);
            sut.withdrawInvestment(user.getId(), investmentCDB.getId(), LocalDate.now().minusMonths(5));
            sut.withdrawInvestment(user.getId(), investmentCDB2.getId(), LocalDate.now().plusDays(10));
            sut.withdrawInvestment(user.getId(), investmentLCI.getId(), LocalDate.now().plusDays(10));

            List<Investment> result = sut.filterHistory(user.getId(), LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
            assertThat(result.size()).isEqualTo(2);
        }
    }

    @Nested
    class ActiveInvestmentsFilter {
        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @MethodSource("provideScenariosForEmptyTypeFilterActiveInvestments")
        @DisplayName("Should return an empty list if there is no active investments when filter by type")
        void shouldReturnAnEmptyListIfThereIsNoActiveInvestmentsWhenFilterByType(List<Investment> registeredInvestments, AssetType assetType) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            registeredInvestments.forEach(investment -> sut.addInvestment(user.getId(), investment));

            List<Investment> result = sut.filterActiveInvestments(user.getId(), assetType);
            assertThat(result).isEqualTo(List.of());
        }

        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @MethodSource("provideScenariosForEmptyDateFilterActiveInvestments")
        @DisplayName("Should return an empty list if there is no active investments when filter by date")
        void shouldReturnAnEmptyListIfThereIsNoActiveInvestmentsWhenFilterByDate(List<Investment> registeredInvestments, LocalDate initialDate, LocalDate finalDate) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            registeredInvestments.forEach(investment -> sut.addInvestment(user.getId(), investment));

            List<Investment> result = sut.filterActiveInvestments(user.getId(), initialDate, finalDate);
            assertThat(result).isEqualTo(List.of());
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return the active investments found when filter by type")
        void shouldReturnTheActiveInvestmentsFoundWhenFilterByType() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Investment investment = new Investment(1000, assetCDB);
            sut.addInvestment(user.getId(), investment);
            List<Investment> result = sut.filterActiveInvestments(user.getId(), CDB);
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return the active investments found when filter by date")
        void shouldReturnTheActiveInvestmentsFoundWhenFilterByDate() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Investment investment = new Investment(1000, assetCDB);
            sut.addInvestment(user.getId(), investment);
            List<Investment> result = sut.filterActiveInvestments(user.getId(), date.minusMonths(1), date.plusMonths(1));
            assertThat(result.size()).isEqualTo(1);
        }

        private static Stream<Arguments> provideScenariosForEmptyTypeFilterActiveInvestments() {
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), CDB),
                    Arguments.of(List.of(investment1, investment2), LCI)
            );
        }

        private static Stream<Arguments> provideScenariosForEmptyDateFilterActiveInvestments() {
            LocalDate date = LocalDate.of(2025, 4, 25);
            Investment investment1 = new Investment(1000, new Asset("Banco Inter", CDB, 0.01, date.plusYears(1)));
            Investment investment2 = new Investment(1500, new Asset("Banco Bradesco", CDB, 0.01, date.plusYears(1)));

            return Stream.of(
                    Arguments.of(List.of(), date.plusMonths(1), date.plusMonths(2)),
                    Arguments.of(List.of(investment1, investment2), date.plusMonths(1), date.plusMonths(2))
            );
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if wallet does not exists")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExists() {
            UUID randomId = UUID.randomUUID();
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterActiveInvestments(randomId, CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if wallet id is null")
        void shouldReturnNullPointerExceptionIfWalletIdIsNull() {
            assertThrows(NullPointerException.class, () -> {
                sut.filterActiveInvestments(null, CDB);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if asset type is null")
        void shouldReturnNullPointerExceptionIfAssetTypeIsNull() {
            assertThrows(NullPointerException.class, () -> {
                sut.filterActiveInvestments(user.getId(), null);
            });
        }

        @ParameterizedTest
        @MethodSource("provideScenariosForEmptyTypeFilterActiveInvestments")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should an empty list when has no data with this filter")
        void shouldAnEmptyListWhenHasNoDataWithThisFilter(List<Investment> investments, AssetType assetType) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> sut.addInvestment(user.getId(), investment));

            assertThat(sut.filterActiveInvestments(user.getId(), assetType)).isEqualTo(List.of());
        }

        @ParameterizedTest
        @MethodSource("getDataToListOfInvestmentsAndTypeFilter")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should correct return the list of investments with this filter")
        void shouldCorrectReturnTheListOfInvestmentsWithThisFilter(List<Investment> investments, AssetType assetType, List<Investment> expected) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            investments.forEach(investment -> sut.addInvestment(user.getId(), investment));

            List<Investment> actual = sut.filterActiveInvestments(user.getId(), assetType);
            assertThat(actual).isEqualTo(expected);
        }

        public static Stream<Arguments> getDataToListOfInvestmentsAndTypeFilter() {
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

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if wallet does not exists when filter by date")
        void shouldReturnNoSuchElementExceptionIfWalletDoesNotExistsWhenFilterByDate() {
            UUID randomId = UUID.randomUUID();
            LocalDate start = date.plusMonths(1);
            LocalDate end = date.plusMonths(2);
            when(repository.findByUser_Id(randomId)).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> {
                sut.filterActiveInvestments(randomId, start, end);
            });
        }

        @ParameterizedTest
        @MethodSource("getInvalidDataToFilterInvestments")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException when some parameter is null")
        void shouldReturnNoSuchElementExceptionWhenSomeParameterIsNull(UUID walletId, LocalDate initialDate, LocalDate finalDate) {
            assertThrows(NullPointerException.class, () -> {
                sut.filterActiveInvestments(walletId, initialDate, finalDate);
            });
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return an empty list when investments is empty")
        void shouldReturnAnEmptyListWhenInvestmentsIsEmpty() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            LocalDate start = LocalDate.now().plusMonths(1);
            LocalDate end = LocalDate.now().plusMonths(2);
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            List<Investment> result = sut.filterActiveInvestments(user.getId(), start, end);

            assertThat(result).isEqualTo(List.of());
        }

        public static Stream<Arguments> getInvalidDataToFilterInvestments() {
            return Stream.of(
                    Arguments.of(null, LocalDate.of(2025, 4, 25), LocalDate.of(2025, 5, 25)),
                    Arguments.of(new Wallet().getId(), null, LocalDate.of(2025, 5, 25)),
                    Arguments.of(new Wallet().getId(), LocalDate.of(2025, 4, 25), null)
            );
        }

        @ParameterizedTest
        @MethodSource("getDataToReturnEmptyListWhenFilterByDate")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return an empty list when there is no data in this filter")
        void shouldReturnAnEmptyListWhenThereIsNoDataInThisFilter(LocalDate initialDate, LocalDate finalDate) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            sut.addInvestment(user.getId(), investmentCDB);

            assertThat(sut.filterActiveInvestments(user.getId(), initialDate, finalDate)).isEqualTo(List.of());
        }

        public static Stream<Arguments> getDataToReturnEmptyListWhenFilterByDate() {
            return Stream.of(
                    Arguments.of(LocalDate.now().plusDays(1), LocalDate.now().plusDays(20)),
                    Arguments.of(LocalDate.now().minusDays(20), LocalDate.now().minusDays(1))
            );
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return a list with more then one item with this filter")
        void shouldReturnAListWithMoreThenOneItemWithThisFilter() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1500, assetLCI);

            sut.addInvestment(user.getId(), investmentCDB);
            sut.addInvestment(user.getId(), investmentCDB2);
            sut.addInvestment(user.getId(), investmentLCI);

            List<Investment> result = sut.filterActiveInvestments(
                    user.getId(),
                    LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(1));
            List<Investment> expected = List.of(investmentCDB, investmentCDB2, investmentLCI);

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @MethodSource("getDataToReturnListOfOneElementWhenFilterByDate")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return a list with one item when filter by limit dates")
        void shouldReturnAListWithOneItemWhenFilterByLimitDates(LocalDate initialDate, LocalDate finalDate) {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);

            sut.addInvestment(user.getId(), investmentCDB);

            assertThat(sut.filterActiveInvestments(user.getId(), initialDate, finalDate)).isEqualTo(List.of(investmentCDB));
        }

        public static Stream<Arguments> getDataToReturnListOfOneElementWhenFilterByDate() {
            return Stream.of(
                    Arguments.of(LocalDate.now(), LocalDate.now().plusDays(15)),
                    Arguments.of(LocalDate.now().minusDays(1), LocalDate.now().plusDays(14)),
                    Arguments.of(LocalDate.now().minusDays(15), LocalDate.now().plusDays(9)),
                    Arguments.of(LocalDate.now().minusDays(13), LocalDate.now().plusDays(10))
            );
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("ShouldReturn2of3InvestmentsWithThisFilter")
        void shouldReturn2Of3InvestmentsWithThisFilter() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusYears(1));
            Asset assetLCI = new Asset("Banco Itau", LCI, 0.1, LocalDate.now().plusYears(1));
            Investment investmentCDB = new Investment(1000, assetCDB);
            Investment investmentCDB2 = new Investment(1500, assetCDB);
            Investment investmentLCI = new Investment(1000, assetLCI);

            sut.addInvestment(user.getId(), investmentCDB);
            sut.addInvestment(user.getId(), investmentCDB2);
            sut.addInvestment(user.getId(), investmentLCI);
            sut.withdrawInvestment(user.getId(), investmentCDB.getId(), LocalDate.now().plusDays(10));

            List<Investment> result = sut.filterActiveInvestments(user.getId(), LocalDate.now().minusDays(5), LocalDate.now());
            assertThat(result.size()).isEqualTo(2);
        }
    }

    @Nested
    class Report {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should throw NoSuchElementException when there are no investments")
        void shouldThrowNoSuchElementExceptionWhenThereAreNoInvestments() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            assertThatThrownBy(() -> sut.generateReport(user.getId()))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("There are no investments in this wallet");
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should throw NoSuchElementException when Wallet does not exist")
        void shouldThrowNoSuchElementExceptionWhenWalletDoesNotExist() {
            UUID userId = UUID.randomUUID();
            when(repository.findByUser_Id(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.generateReport(userId))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("This user has not a wallet: " + userId);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should throw NullPointerException when Wallet id is null")
        void shouldThrowNullPointerExceptionWhenWalletIdIsNull() {
            assertThatThrownBy(() -> sut.generateReport(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("User id cannot be null");
        }

        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @MethodSource("provideWalletScenarios")
        @DisplayName("Should return report when there is investments")
        void shouldReturnReportWhenThereIsInvestments(Wallet wallet, List<String> expectedParts) {
            user.setWallet(wallet);
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            String report = sut.generateReport(user.getId());

            SoftAssertions softly = new SoftAssertions();
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

            wallet.addInvestment(investmentCDB);
            wallet.addInvestment(investmentTesouro);
            investmentCDB.setWithdrawDate(date);
            investmentTesouro.setWithdrawDate(date);

            return wallet;
        }

        private static Wallet walletWithActiveInvestmentsOnly() {
            LocalDate date = LocalDate.of(2025, 4, 25);
            Wallet wallet = new Wallet();

            Asset assetCDB = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Asset assetTesouro = new Asset("Banco Inter", TESOURO_DIRETO, 0.1, date.plusMonths(2));

            Investment investmentCDB = createInvestmentWithPurchaseDateAndMockWithdraw(2000, assetCDB, date, date);
            Investment investmentTesouro = createInvestmentWithPurchaseDateAndMockWithdraw(3000, assetTesouro, date, date);

            wallet.addInvestment(investmentCDB);
            wallet.addInvestment(investmentTesouro);
            return wallet;
        }

        private static Wallet walletWithEverything() {
            Wallet historyOnly = walletWithHistoryOnly();
            Wallet activeOnly = walletWithActiveInvestmentsOnly();
            activeOnly.getInvestments().forEach(historyOnly::addInvestment);
            return historyOnly;
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

    @Nested
    class GetWallet {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if userId is null")
        void shouldReturnNullPointerExceptionIfUserIdIsNull() {
            assertThrows(NullPointerException.class, () -> sut.getWallet(null));
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return a wallet")
        void shouldReturnAWallet(){
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            Wallet wallet = sut.getWallet(user.getId());
            assertThat(sut.getWallet(user.getId())).isEqualTo(wallet);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if returns an empty wallet")
        void shouldReturnNoSuchElementExceptionIfReturnsAnEmptyWallet() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> sut.getWallet(user.getId()));
        }
    }

    @Nested
    class GetInvestmentById {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if userId is null")
        void shouldReturnNullPointerExceptionIfUserIdIsNull() {
            Investment investment = new Investment(1000, new Asset("Banco Inter", CDB, 0.1, LocalDate.now()));

            assertThrows(NullPointerException.class, () -> sut.getInvestmentById(null, investment.getId()), "User id cannot be null");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if investmentId is null")
        void shouldReturnNullPointerExceptionIfInvestmentIdIsNull() {
            assertThrows(NullPointerException.class, () -> sut.getInvestmentById(user.getId(), null), "Investment id cannot be null");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NoSuchElementException if investment is not in the wallet")
        void shouldReturnNoSuchElementExceptionIfInvestmentIsNotInTheWallet() {
            UUID randomId = UUID.randomUUID();

            assertThrows(NoSuchElementException.class, () -> sut.getInvestmentById(user.getId(), randomId),
                    "Investment not found: " + randomId);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return an investment")
        void shouldReturnAnInvestment(){
            Investment investment = new Investment(1000, new Asset("Banco Inter", CDB, 0.1, LocalDate.now()));
            wallet.addInvestment(investment);
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            Investment result = sut.getInvestmentById(user.getId(), investment.getId());

            assertThat(result).isEqualTo(investment);
        }
    }

    @Nested
    class GetTotalBalance {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return NullPointerException if userId is null")
        void shouldReturnNullPointerExceptionIfUserIdIsNull() {
            assertThrows(NullPointerException.class, () -> sut.getTotalBalance(null), "User id cannot be null");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return total balance")
        void shouldReturnTotalBalance(){
            Investment investment = new Investment(1000, new Asset("Banco Inter", CDB, 0.1, LocalDate.now()));
            wallet.addInvestment(investment);
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));

            assertThat(sut.getTotalBalance(user.getId())).isEqualTo(wallet.getTotalBalance());
        }
    }

    @Nested
    class StructuralTests {
        @Test
        @Tag("UnitTest")
        @Tag("Structural")
        @DisplayName("Should remove investments saved")
        void shouldRemoveInvestmentsSaved(){
            Investment investment = new Investment(1000, new Asset("Banco Inter", CDB, 0.1, LocalDate.now()));
            wallet.addInvestment(investment);
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.of(wallet));
            sut.removeInvestment(user.getId(), investment.getId());
            assertThat(sut.getInvestments(user.getId()).size()).isEqualTo(0);
        }
    }
}
