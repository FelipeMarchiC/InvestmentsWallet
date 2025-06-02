import React, { useState } from 'react';
import './WalletPage.css';
import SummaryCard from '../../components/SummaryCard/SummaryCard'; 
import { useNavigate } from 'react-router-dom';
import { useWalletData } from '../../hooks/useWalletData';
import InvestmentListSection from '../../components/InvestmentListSection/InvestmentListSection';
import { useDisplayableInvestments } from '../../hooks/useDisplayableInvestments';
import { useAuthGuard } from '../../hooks/useAuthGuard';
import { walletService } from '../../services/walletService';
import FullScreenModal from '../../components/Modal/FullScreenModal';

function WalletPage() {
  useAuthGuard();

  const {
    userActiveInvestments,
    userHistoryInvestments,
    allAssets,
    totalBalance,
    expectedReturn,
    countInvestments,
    loadingData,
    error,
    refreshWalletData
  } = useWalletData();

  const [walletReport, setWalletReport] = useState()

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

  const [openModal, setOpenModal] = useState(false);
  const navigate = useNavigate()

  const handleNewInvestmentBtn = () => {
    navigate("/assets");
  };

  const handleGenerateReportBtn = async () => {
    try {
      const report = await walletService.generateWalletReport();
      setWalletReport(report)
    } catch (error) {
      const emptyWalletMessage = error?.message;
      setWalletReport(
        emptyWalletMessage === "There are no investments in this wallet"
          ? "Não existem investimentos na carteira"
          : "Erro ao gerar relatório"
      );
      console.log("Erro ao gerar relatório:", error);
    }
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
  };

  return (
    <div className="wallet-container">
      <div className="wallet-header">
        <h1>Minha Carteira</h1>
        <div className='wallet-buttons-container'>
          <button onClick={handleNewInvestmentBtn} className="new-investment-button">Novo Investimento</button>
          <button
            onClick={handleGenerateReportBtn}
            className="new-investment-button"
          >
            Gerar relatório
          </button>

          <FullScreenModal open={openModal} onClose={handleCloseModal} title={"Relatório da Carteira"}>
            <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'monospace', fontSize: '1rem' }}>
              {walletReport}
            </pre>
          </FullScreenModal>
        </div>
      </div>

      <SummaryCard 
        totalBalance={totalBalance}
        expectedReturn={expectedReturn}
        countInvestments={countInvestments}
        loading={loadingData}
        error={error}
      />

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
