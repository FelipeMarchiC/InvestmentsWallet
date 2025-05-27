import React, { useState } from "react";
import "./WalletPage.css";
import SummaryCard from "../../components/SummaryCard/SummaryCard";

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

  return (
    <div className="wallet-container">
      <div className="wallet-header">
        <h1>My wallet</h1>
        <button className="new-investment-button">New investment</button>
      </div>

      <div>
        <SummaryCard />
      </div>

      {/* Seção de Investimentos */}
      <div className="investments-section">
        <h2>Investimentos ativos</h2>
        <div className="investments-list">
          {walletData.investments.length === 0 ? (
            <p className="empty-message">
              Você não possui investimentos ativos.
            </p>
          ) : (
            {
              /* Futuramente, a lista de investimentos será renderizada aqui */
            }
          )}
        </div>
      </div>
      <div className="investments-section">
        <h2>Histórico</h2>
        <div className="investments-list">
          {walletData.investments.length === 0 ? (
            <p className="empty-message">
              Você não possui investimentos no histórico.
            </p>
          ) : (
            {
              /* Futuramente, a lista de investimentos será renderizada aqui */
            }
          )}
        </div>
      </div>
    </div>
  );
}

export default WalletPage;
