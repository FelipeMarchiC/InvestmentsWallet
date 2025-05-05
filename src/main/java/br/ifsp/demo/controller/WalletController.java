package br.ifsp.demo.controller;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.dto.investment.InvestmentRequestDTO;
import br.ifsp.demo.dto.investment.InvestmentResponseDTO;
import br.ifsp.demo.dto.investment.InvestmentWithdrawRequestDTO;
import br.ifsp.demo.dto.wallet.WalletResponseDTO;
import br.ifsp.demo.mapper.InvestmentMapper;
import br.ifsp.demo.mapper.WalletMapper;
import br.ifsp.demo.security.auth.AuthenticationInfoService;
import br.ifsp.demo.service.WalletService;
import jakarta.validation.Valid;
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
    private final AuthenticationInfoService authenticationInfoService;

    public WalletController(WalletService walletService, AuthenticationInfoService authenticationInfoService) {
        this.walletService = walletService;
        this.authenticationInfoService = authenticationInfoService;
    }

    @PostMapping()
    public ResponseEntity<WalletResponseDTO> createWallet() {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.createWallet(userId);

        List<Investment> investments = walletService.getInvestments(wallet.getId());
        List<Investment> history = walletService.getHistoryInvestments(wallet.getId());

        WalletResponseDTO walletResponseDTO = WalletMapper.toResponseDTO(wallet, investments, history);
        return ResponseEntity.status(HttpStatus.CREATED).body(walletResponseDTO);
    }

    @GetMapping()
    public ResponseEntity<WalletResponseDTO> getWallet() {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        List<Investment> investments = walletService.getInvestments(wallet.getId());
        List<Investment> history = walletService.getHistoryInvestments(wallet.getId());

        WalletResponseDTO walletResponseDTO = WalletMapper.toResponseDTO(wallet, investments, history);
        return ResponseEntity.ok().body(walletResponseDTO);
    }

    @GetMapping("/investment/{investmentId}")
    public ResponseEntity<InvestmentResponseDTO> getInvestmentById(@PathVariable UUID investmentId) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Investment investment = walletService.getInvestmentById(userId, investmentId);

        InvestmentResponseDTO investmentsResponseDTO = InvestmentMapper.toResponseDTO(investment);
        return ResponseEntity.ok().body(investmentsResponseDTO);
    }

    @PostMapping("/investment")
    public ResponseEntity<HttpStatus> addInvestment(@RequestBody @Valid InvestmentRequestDTO investmentRequestDTO) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        Investment investment = new Investment();
        BeanUtils.copyProperties(investmentRequestDTO, investment);
        walletService.addInvestment(wallet.getId(), investment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/investment/{investmentId}")
    public ResponseEntity<HttpStatus> removeInvestment(@PathVariable UUID investmentId) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        walletService.removeInvestment(userId, investmentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/investment/withdraw")
    public ResponseEntity<HttpStatus> withdrawInvestment(@RequestBody @Valid InvestmentWithdrawRequestDTO dto) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        UUID investmentId = dto.investmentId();
        LocalDate withdrawDate = dto.withdrawDate();

        walletService.withdrawInvestment(userId, investmentId, withdrawDate);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report")
    public ResponseEntity<String> generateReport(@RequestParam LocalDate relativeDate) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        String report = walletService.generateReport(userId, relativeDate);

        return ResponseEntity.ok().body(report);
    }

    @GetMapping("/history/filterByType/{type}")
    public ResponseEntity<List<InvestmentResponseDTO>> filterHistoryByType(@PathVariable AssetType type) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        List<Investment> investments = walletService.filterHistory(userId, type);
        List<InvestmentResponseDTO> investmentsResponseDTO = InvestmentMapper.listToResponseDTO(investments, wallet);
        return ResponseEntity.ok().body(investmentsResponseDTO);
    }

    @GetMapping("/history/filterByDate")
    public ResponseEntity<List<InvestmentResponseDTO>> filterHistoryByDate(@RequestParam LocalDate initialDate, @RequestParam LocalDate finalDate) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        List<Investment> investments = walletService.filterHistory(userId, initialDate, finalDate);
        List<InvestmentResponseDTO> investmentsResponseDTO = InvestmentMapper.listToResponseDTO(investments, wallet);
        return ResponseEntity.ok().body(investmentsResponseDTO);
    }

    @GetMapping("/investment/filterByType/{type}")
    public ResponseEntity<List<InvestmentResponseDTO>> filterActiveInvestmentsByType(@PathVariable AssetType type) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        List<Investment> investments = walletService.filterActiveInvestments(userId, type);
        List<InvestmentResponseDTO> investmentsResponseDTO = InvestmentMapper.listToResponseDTO(investments, wallet);

        return ResponseEntity.ok().body(investmentsResponseDTO);
    }

    @GetMapping("/investment/filterByDate")
    public ResponseEntity<List<InvestmentResponseDTO>> filterActiveInvestmentsByDate(@RequestParam LocalDate initialDate, @RequestParam LocalDate finalDate) {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        List<Investment> investments = walletService.filterActiveInvestments(userId, initialDate, finalDate);
        List<InvestmentResponseDTO> investmentsResponseDTO = InvestmentMapper.listToResponseDTO(investments, wallet);
        return ResponseEntity.ok().body(investmentsResponseDTO);
    }

    @GetMapping("/investment")
    public ResponseEntity<List<InvestmentResponseDTO>> getInvestments() {
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        List<Investment> investments = walletService.getInvestments(userId);
        List<InvestmentResponseDTO> investmentsResponseDTO = InvestmentMapper.listToResponseDTO(investments, wallet);
        return ResponseEntity.ok().body(investmentsResponseDTO);
    }

    @GetMapping("/history")
    public ResponseEntity<List<InvestmentResponseDTO>> getHistoryInvestments(){
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);

        List<Investment> investments = walletService.getHistoryInvestments(userId);
        List<InvestmentResponseDTO> investmentsResponseDTO = InvestmentMapper.listToResponseDTO(investments, wallet);
        return ResponseEntity.ok().body(investmentsResponseDTO);
    }

    @GetMapping("/totalBalance")
    public ResponseEntity<Double> getTotalBalance(@RequestParam LocalDate withdrawDate){
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        double balance = walletService.getTotalBalance(userId, withdrawDate);

        return ResponseEntity.ok().body(balance);
    }

    @GetMapping("/futureBalance")
    public ResponseEntity<Double> getFutureBalance(){
        UUID userId = authenticationInfoService.getAuthenticatedUserId();
        Wallet wallet = walletService.getWallet(userId);
        double balance = wallet.getFutureBalance();

        return ResponseEntity.ok().body(balance);
    }
}
