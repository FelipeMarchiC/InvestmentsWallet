import React from "react"; 
import "./SummaryCard.css";

function SummaryCard({ totalBalance, expectedReturn, countInvestments, loading, error }) {

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
        <p>{error}</p>
      </div>
    );
  }

  return (
    <div className="new-summary-card">
      <h2>Resumo da Carteira</h2>
      <div className="new-summary-grid">
        <div className="new-info-box new-highlight-blue">
          <p className="new-info-label">Saldo Total</p>
          <p className="new-info-value">
            {formatCurrency(totalBalance)}
          </p>
          <p className="new-info-label">
            (Histórico + Ativos)
          </p>
        </div>
        <div className="new-info-box new-highlight-green">
          <p className="new-info-label">Retorno Esperado</p>
          <p className="new-info-value">
            {formatCurrency(expectedReturn)}
          </p>
          <p className="new-info-label">
            (Ativos)
          </p>
        </div>
        <div className="new-info-box new-highlight-purple">
          <p className="new-info-label">Total de Investimentos</p>
          <p className="new-info-value">{formatTotalAssets(countInvestments)}</p>
          <p className="new-info-label">
            (Histórico + Ativos)
          </p>
        </div>
      </div>
    </div>
  );
}

export default SummaryCard;