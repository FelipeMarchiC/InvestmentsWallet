import React from 'react';
import './WalletPage.css';
import SummaryCard from '../../components/SummaryCard/SummaryCard'; 
import { useNavigate } from 'react-router-dom';
import { useWalletData } from '../../hooks/useWalletData';
import InvestmentListSection from '../../components/InvestmentListSection/InvestmentListSection';
import { useDisplayableInvestments } from '../../hooks/useDisplayableInvestments';

function WalletPage() {
  const {
    userActiveInvestments,
    userHistoryInvestments,
    allAssets,
    loadingData,
    error,
    refreshWalletData
  } = useWalletData();

  const displayableActiveInvestments = useDisplayableInvestments(
    userActiveInvestments,
    allAssets,
    loadingData,
    false
  );

  const displayableHistoryInvestments = useDisplayableInvestments(
    userHistoryInvestments,
    allAssets,
    loadingData,
    true
  );

  const navigate = useNavigate()

  const handleNewInvestmentBtn = () => {
    navigate("/assets");
  };

  return (
    <div className="wallet-container">
      <div className="wallet-header">
        <h1>Minha Carteira</h1>
        <button onClick={handleNewInvestmentBtn} className="new-investment-button">Novo Investimento</button>
      </div>

      <SummaryCard />

      <div className="investments-section">
        <h2>Investimentos Ativos</h2>
        <InvestmentListSection
          title="Investimentos Ativos"
          investmentsList={displayableActiveInvestments}
          isLoading={loadingData}
          specificError={error}
          refreshWalletData={refreshWalletData}
        />
      </div>

      <div className="investments-section">
        <h2>Histórico de Investimentos</h2>
        <InvestmentListSection
          title="Histórico"
          investmentsList={displayableHistoryInvestments}
          isLoading={loadingData}
          specificError={error}
          refreshWalletData={refreshWalletData}
        />
      </div>
    </div>
  );
}

export default WalletPage;
