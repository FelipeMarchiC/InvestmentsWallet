package br.ifsp.demo.controller;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.dto.asset.AssetResponseDTO;
import br.ifsp.demo.dto.investment.InvestmentResponseDTO;
import br.ifsp.demo.dto.wallet.WalletResponseDTO;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.auth.AuthenticationInfoService;
import br.ifsp.demo.service.WalletService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletRepository repository;
    private final AuthenticationInfoService authService;

    public WalletController(WalletService walletService, WalletRepository repository, AuthenticationInfoService authService) {
        this.walletService = walletService;
        this.repository = repository;
        this.authService = authService;
    }

    @PostMapping()
    public ResponseEntity<WalletResponseDTO> createWallet() {
        UUID userId = authService.getAuthenticatedUserId();
        Wallet wallet = walletService.createWallet(userId);

        List<Investment> investments = walletService.getInvestments(wallet.getId());
        List<Investment> history = walletService.getHistoryInvestments(wallet.getId());

        List<InvestmentResponseDTO> investmentResponseDTOS = investments
                .stream().map(investment -> {
                    return new InvestmentResponseDTO(
                            investment.getId(),
                            investment.getInitialValue(),
                            investment.getAsset().getId(),
                            investment.getPurchaseDate(),
                            investment.getWithdrawDate(),
                            wallet.getId());
                }).toList();

        List<InvestmentResponseDTO> historyResponseDTOS = history
                .stream().map(investment -> {
                    return new InvestmentResponseDTO(
                            investment.getId(),
                            investment.getInitialValue(),
                            investment.getAsset().getId(),
                            investment.getPurchaseDate(),
                            investment.getWithdrawDate(),
                            wallet.getId());})
                .toList();

        WalletResponseDTO walletResponseDTO = new WalletResponseDTO(wallet.getId(), investmentResponseDTOS, historyResponseDTOS);
        return ResponseEntity.status(HttpStatus.CREATED).body(walletResponseDTO);
    }

    @GetMapping("/{id}")
    public void getWalletById(@PathVariable UUID id) {}

    @GetMapping("/investment/{id}")
    public void getInvestmentById(@PathVariable UUID id) {}

    @PostMapping("/investment")
    public void addInvestment(Investment investment) {}

    @DeleteMapping("/investment/{id}")
    public void removeInvestment(@PathVariable UUID id) {}

    @PostMapping("/investment/withdraw")
    public void withdrawInvestment(UUID investmentId, LocalDate withdrawDate) {}

    @GetMapping("/report/{relativeDate}")
    public void generateReport(@PathVariable LocalDate relativeDate) {}

    @GetMapping("/history/filterByType/{type}")
    public void filterHistoryByType(@PathVariable AssetType type) {}

    @GetMapping("/history/filterByDate")
    public void filterHistoryByDate(@RequestParam(required = true) LocalDate initialDate, @RequestParam(required = true) LocalDate finalDate) {}

    @GetMapping("/investment/filterByType/{type}")
    public void filterActiveInvestmentsByType(@PathVariable AssetType type) {}

    @GetMapping("/investment/filterByDate")
    public void filterActiveInvestmentsByDate(@RequestParam(required = true) LocalDate initialDate, @RequestParam(required = true) LocalDate finalDate) {}

    @GetMapping("/investment")
    public void getInvestments() {}

    @GetMapping("/history")
    public void getHistoryInvestments(){}

    @GetMapping("/totalBalance/{withdrawDate}")
    public void getTotalBalance(@PathVariable LocalDate withdrawDate){}

    @GetMapping("/futureBalance/{withdrawDate}")
    public void getFutureBalance(@PathVariable LocalDate withdrawDate){}
}
