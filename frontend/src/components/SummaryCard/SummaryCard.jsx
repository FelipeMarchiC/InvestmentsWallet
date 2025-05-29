import React, { useState, useEffect } from "react"; 
import "./SummaryCard.css";
import { walletService } from "../../services/walletService"; 

function SummaryCard() {
  const [summaryData, setSummaryData] = useState({
    totalInvested: 0,
    expectedReturn: 0,
    expectedReturnPercentage: 0,
    totalAssets: 0,
    totalProfitability: 0,
    totalProfitabilityPercentage: 0,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchSummaryData = async () => {
      setLoading(true);
      setError('');
      try {
        // Buscar todos os dados necessários em paralelo
        const [
          totalBalanceData, 
          activeInvestments, 
          historyInvestments
        ] = await Promise.all([
          walletService.getWalletTotalBalance(),
          walletService.getUserInvestments(),      
          walletService.getUserHistoryInvestments()
        ]);

        const totalNumberOfAssets = (activeInvestments?.length || 0) + (historyInvestments?.length || 0);

        setSummaryData((prevData) => ({
          ...prevData,
          totalInvested: totalBalanceData,
          totalAssets: totalNumberOfAssets,
        }));

      } catch (err) {
        console.error("Erro ao buscar dados do resumo:", err);
        // Para mensagens de erro mais específicas, você pode verificar o tipo de erro
        let specificMessage = "Falha ao carregar resumo da carteira.";
        if (err.message.includes("total balance")) {
            specificMessage = "Falha ao carregar total investido.";
        } else if (err.message.includes("user investments")) {
            specificMessage = "Falha ao carregar investimentos ativos para contagem.";
        } else if (err.message.includes("history investments")) {
            specificMessage = "Falha ao carregar histórico para contagem.";
        }
        setError(specificMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchSummaryData();
  }, []); 

  const formatCurrency = (value) => {
    const numericValue = typeof value === 'number' ? value : 0;
    return numericValue.toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    });
  };

  const formatTotalAssets = (value) => {
    return typeof value === 'number' ? value : 0;
  };

  if (loading) {
    return (
      <div className="new-summary-card loading-state">
        <p>Carregando resumo da carteira...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="new-summary-card error-state">
        <p>Erro ao carregar resumo: {error}</p>
      </div>
    );
  }

  return (
    <div className="new-summary-card">
      <h2>Resumo da Carteira</h2>
      <div className="new-summary-grid">
        <div className="new-info-box new-highlight-blue">
          <p className="new-info-label">Total Investido</p>
          <p className="new-info-value">
            {formatCurrency(summaryData.totalInvested)}
          </p>
        </div>
        <div className="new-info-box new-highlight-green">
          <p className="new-info-label">Retorno Esperado</p>
          <p className="new-info-value">
            {formatCurrency(summaryData.expectedReturn)} (
            {summaryData.expectedReturnPercentage.toFixed(2)}%)
          </p>
        </div>
        <div className="new-info-box new-highlight-purple">
          <p className="new-info-label">Total de Ativos</p>
          <p className="new-info-value">{formatTotalAssets(summaryData.totalAssets)}</p>
        </div>
      </div>
      <div className="new-total-profitability">
        <p className="new-info-label">Rentabilidade Total</p>
        <p className="new-info-value-profit">
          {formatCurrency(summaryData.totalProfitability)} (
          {summaryData.totalProfitabilityPercentage.toFixed(2)}%)
        </p>
      </div>
    </div>
  );
}

export default SummaryCard;
