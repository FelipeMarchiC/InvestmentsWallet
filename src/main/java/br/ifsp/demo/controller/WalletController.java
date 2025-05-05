package br.ifsp.demo.controller;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/wallet")
public class WalletController {

    WalletService walletService;
    WalletRepository repository;

    public WalletController(WalletService walletService, WalletRepository repository) {
        this.walletService = walletService;
        this.repository = repository;
    }

    @PostMapping("/create")
    public void createWallet() {}

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
