import React, { useState, useEffect } from 'react';
import './WalletPage.css';
import SummaryCard from '../../components/SummaryCard/SummaryCard'; 
import InvestmentCard from '../../components/InvestmentCard/InvestmentCard';
import { walletService } from '../../services/walletService';
import { assetService } from '../../services/assetService';
import InvestmentDetailModal from '../../components/InvestmentDetailModal/InvestmentDetailModal';


function WalletPage() {
  // Estados para investimentos ativos
  const [userActiveInvestments, setUserActiveInvestments] = useState([]); 
  const [displayableActiveInvestments, setDisplayableActiveInvestments] = useState([]);
  
  // Estados para histórico de investimentos
  const [userHistoryInvestments, setUserHistoryInvestments] = useState([]); 
  const [displayableHistoryInvestments, setDisplayableHistoryInvestments] = useState([]);

  // Estados para o modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedInvestmentForModal, setSelectedInvestmentForModal] = useState(null);

  const [allAssets, setAllAssets] = useState([]);
  
  const [loadingData, setLoadingData] = useState(true); 
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAllWalletData = async () => {
      setLoadingData(true);
      setError('');
      try {
        const [assetsData, activeInvestmentsData, historyData] = await Promise.all([
          assetService.getAllAssets(),
          walletService.getUserInvestments(),
          walletService.getUserHistoryInvestments()
        ]);
        
        setAllAssets(assetsData);
        setUserActiveInvestments(activeInvestmentsData); 
        setUserHistoryInvestments(historyData);

      } catch (err) {
        const errorMessage = err.message || 'Erro ao carregar dados da carteira.';
        setError(errorMessage);
        console.error("Erro ao buscar dados da carteira:", err);
      } finally {
        setLoadingData(false);
      }
    };

    fetchAllWalletData();
  }, []);

  // Função auxiliar para transformar dados para o InvestmentCard
  const transformToInvestmentCardData = (inv, assetDetail, isHistory = false) => {
    if (!assetDetail) return null;

    // SIMULAÇÃO DE RETORNO
    const profitRate = assetDetail.profitability || 0; 
    const initialVal = inv.initialValue || 0;
    
    const simulatedProfit = initialVal * profitRate * 0.1; 
    const simulatedExpectedReturn = initialVal + simulatedProfit;
    const simulatedReturnPercentage = initialVal > 0 ? ((simulatedProfit / initialVal) * 100).toFixed(2) + '%' : '0.00%';

    return {
      id: inv.id,
      assetName: assetDetail.name,
      assetSubtitle: `Banco ${assetDetail.name.split(' ')[1] || 'Genérico'}`,
      type: assetDetail.assetType,
      value: initialVal,
      expectedReturn: simulatedExpectedReturn, 
      returnProfit: simulatedProfit, 
      returnPercentage: simulatedReturnPercentage,
      investmentDate: inv.purchaseDate,
      // Para histórico, prioriza withdrawDate. Se não houver, usa maturityDate do ativo.
      maturityDate: isHistory ? (inv.withdrawDate || assetDetail.maturityDate) : assetDetail.maturityDate, 
      isHistory: isHistory,
    };
  };

  // Efeito para processar investimentos ATIVOS
  useEffect(() => {
    if (allAssets.length > 0) {
      const enriched = userActiveInvestments 
        .map(inv => transformToInvestmentCardData(inv, allAssets.find(asset => asset.id === inv.assetId), false))
        .filter(Boolean);
      setDisplayableActiveInvestments(enriched);
    } else if (!loadingData) { 
      setDisplayableActiveInvestments([]);
    }
  }, [userActiveInvestments, allAssets, loadingData]);

  // Efeito para processar HISTÓRICO
  useEffect(() => {
    if (allAssets.length > 0) { 
      const enriched = userHistoryInvestments
        .map(inv => transformToInvestmentCardData(inv, allAssets.find(asset => asset.id === inv.assetId), true))
        .filter(Boolean);
      setDisplayableHistoryInvestments(enriched);
    } else if (!loadingData) {
      setDisplayableHistoryInvestments([]);
    }
  }, [userHistoryInvestments, allAssets, loadingData]);

  const handleOpenInvestmentModal = (investmentData) => {
    setSelectedInvestmentForModal(investmentData);
    setIsModalOpen(true);
  };

  const handleCloseInvestmentModal = () => {
    setIsModalOpen(false);
    setSelectedInvestmentForModal(null); // Limpa o investimento selecionado
  };

  // JSX para renderizar uma seção de InvestmentCards
  const renderInvestmentRows = (title, investmentsList, isLoading, specificError) => { 
    if (isLoading) {
      return <p className="loading-message">Carregando {title.toLowerCase()}...</p>;
    }
    if (specificError) { 
      return <p className="error-message">{specificError}</p>;
    }
    
    return (
      <>
        <div className="investment-row-header">
          <div className="header-cell asset-name-cell">ATIVO</div>
          <div className="header-cell asset-type-cell">TIPO</div>
          <div className="header-cell asset-value-cell">VALOR</div>
          <div className="header-cell asset-return-cell">RETORNO ESPERADO</div>
          <div className="header-cell asset-date-cell">DATA INVESTIMENTO</div>
          <div className="header-cell asset-maturity-cell">VENCIMENTO</div>
        </div>
        {investmentsList.length === 0 ? (
           <p className="empty-message full-width-message">
             Você não possui {title === 'Investimentos Ativos' ? 'investimentos ativos' : 'movimentações no histórico'}.
           </p>
        ) : (
          investmentsList.map((investmentData) => (
            <InvestmentCard 
              key={investmentData.id} 
              investment={investmentData} 
              onClick={() => handleOpenInvestmentModal(investmentData)} 
            />
          ))
        )}
      </>
    );
  };

  return (
    <div className="wallet-container">
      <div className="wallet-header">
        <h1>Minha Carteira</h1>
        <button className="new-investment-button">Novo Investimento</button>
      </div>

      <SummaryCard />

      <div className="investments-section">
        <h2>Investimentos Ativos</h2>
        {renderInvestmentRows("Investimentos Ativos", displayableActiveInvestments, loadingData, error)}
      </div>

      <div className="investments-section">
        <h2>Histórico de Investimentos</h2>
        {renderInvestmentRows("Histórico", displayableHistoryInvestments, loadingData, error)}
      </div>

      <InvestmentDetailModal
        isOpen={isModalOpen}
        onClose={handleCloseInvestmentModal}
        investment={selectedInvestmentForModal}
      />
    </div>
  );
}

export default WalletPage;
