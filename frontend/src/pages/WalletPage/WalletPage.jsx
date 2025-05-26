import React, { useState } from 'react';
import './WalletPage.css';

function WalletPage() {
  const [walletData, setWalletData] = useState({
    totalInvested: 0,
    expectedReturn: 0,
    expectedReturnPercentage: 0,
    totalAssets: 0,
    totalProfitability: 0,
    totalProfitabilityPercentage: 0,
    investments: [],
  });

  // Função para formatar valores monetários
  const formatCurrency = (value) => {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };

  return (
    <div className="wallet-container">
      {/* Cabeçalho da página */}
      <div className="wallet-header">
        <h1>My wallet</h1>
        <button className="new-investment-button">New investment</button>
      </div>

      {/* Card de Resumo da Carteira */}
      <div className="summary-card">
        <h2>Resumo da Carteira</h2>
        <div className="summary-grid">
          <div className="info-box">
            <p className="info-label">Total Investido</p>
            <p className="info-value">{formatCurrency(walletData.totalInvested)}</p>
          </div>
          <div className="info-box highlight-green">
            <p className="info-label">Retorno Esperado</p>
            <p className="info-value">
              {formatCurrency(walletData.expectedReturn)} ({walletData.expectedReturnPercentage.toFixed(2)}%)
            </p>
          </div>
          <div className="info-box highlight-purple">
            <p className="info-label">Total de Ativos</p>
            <p className="info-value">{walletData.totalAssets}</p>
          </div>
        </div>
        <div className="total-profitability">
          <p className="info-label">Rentabilidade Total</p>
          <p className="info-value-profit">
            {formatCurrency(walletData.totalProfitability)} ({walletData.totalProfitabilityPercentage.toFixed(2)}%)
          </p>
        </div>
      </div>

      {/* Seção de Investimentos */}
      <div className="investments-section">
        <h2>Investimentos</h2>
        <div className="investments-list">
          {walletData.investments.length === 0 ? (
            <p className="empty-message">Você ainda não possui investimentos em sua carteira.</p>
          ) : (
            {/* Futuramente, a lista de investimentos será renderizada aqui */}
          )}
        </div>
      </div>
    </div>
  );
}

export default WalletPage;