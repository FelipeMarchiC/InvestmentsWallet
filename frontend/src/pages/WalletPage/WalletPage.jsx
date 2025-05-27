// src/pages/WalletPage/WalletPage.jsx

import React, { useState, useEffect } from 'react';
import './WalletPage.css';
import SummaryCard from '../../components/SummaryCard/SummaryCard'; 
import AssetCard from '../../components/AssetCard/AssetCard';
import { walletService } from '../../services/walletService';
import { assetService } from '../../services/assetService';

function WalletPage() {
  // Estados para investimentos ativos
  const [userInvestments, setUserInvestments] = useState([]);
  const [displayableInvestments, setDisplayableInvestments] = useState([]);
  
  // Estados para histórico de investimentos
  const [userHistory, setUserHistory] = useState([]);
  const [displayableHistory, setDisplayableHistory] = useState([]);

  const [allAssets, setAllAssets] = useState([]);
  
  const [loadingData, setLoadingData] = useState(true); 
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAllWalletData = async () => {
      setLoadingData(true);
      setError('');
      try {
        // Busca todos os dados em paralelo
        const [assetsData, activeInvestmentsData, historyData] = await Promise.all([
          assetService.getAllAssets(),
          walletService.getUserInvestments(),
          walletService.getUserHistoryInvestments() // Busca o histórico
        ]);
        
        setAllAssets(assetsData);
        setUserInvestments(activeInvestmentsData);
        setUserHistory(historyData); // Define os dados do histórico

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

  useEffect(() => {
    if (allAssets.length > 0) {
      const enrichedInvestments = userInvestments.map(inv => {
        const assetDetail = allAssets.find(asset => asset.id === inv.assetId);
        if (assetDetail) {
          return {
            id: inv.id,
            name: assetDetail.name,
            type: assetDetail.assetType,
            profitability: `${(assetDetail.profitability * 100).toFixed(2)}%`,
            maturity: inv.withdrawDate ? `Sacado em: ${new Date(inv.withdrawDate).toLocaleDateString('pt-BR')}` : new Date(assetDetail.maturityDate).toLocaleDateString('pt-BR'),
            minInvestment: `R$ ${inv.initialValue.toFixed(2)}`,
            description: assetDetail.name, 
            isHistory: !!inv.withdrawDate, 
          };
        }
        return null;
      }).filter(Boolean);
      setDisplayableInvestments(enrichedInvestments);
    } else if (!loadingData) { 
        setDisplayableInvestments([]);
    }
  }, [userInvestments, allAssets, loadingData]);

  useEffect(() => {
    if (allAssets.length > 0) { // Processa mesmo se userHistory for vazio
      const enrichedHistory = userHistory.map(inv => {
        const assetDetail = allAssets.find(asset => asset.id === inv.assetId);
        if (assetDetail) {
          return {
            id: inv.id,
            name: assetDetail.name,
            type: assetDetail.assetType,
            profitability: `${(assetDetail.profitability * 100).toFixed(2)}%`, // Ou rentabilidade no momento do saque se tiver
            maturity: inv.withdrawDate ? `Sacado em: ${new Date(inv.withdrawDate).toLocaleDateString('pt-BR')}` : 'Data de saque não disponível',
            minInvestment: `R$ ${inv.initialValue.toFixed(2)}`,
            description: `${assetDetail.name} (Histórico)`,
            isHistory: true, // Indica que é um item de histórico
          };
        }
        return null;
      }).filter(Boolean);
      setDisplayableHistory(enrichedHistory);
    } else if (!loadingData) {
        setDisplayableHistory([]);
    }
  }, [userHistory, allAssets, loadingData]);

  // renderiza uma seção de cards
  const renderInvestmentSection = (title, investments, isLoading, specificError) => {
    if (isLoading) {
      return <p className="loading-message">Carregando {title.toLowerCase()}...</p>;
    }
    if (specificError) { 
      return <p className="error-message">{specificError}</p>;
    }
    if (investments.length === 0) {
      return <p className="empty-message">Você não possui investimentos {title === 'Investimentos Ativos' ? 'ativos' : 'no histórico'}.</p>;
    }
    return (
      <div className="investments-list-grid">
        {investments.map((investment) => (
          <AssetCard key={investment.id} asset={investment} />
        ))}
      </div>
    );
  };

  return (
    <div className="wallet-container">
      <div className="wallet-header">
        <h1>Minha Carteira</h1>
        <button className="new-investment-button">Novo Investimento</button>
      </div>

      <SummaryCard />

      {/* Seção de Investimentos Ativos */}
      <div className="investments-section">
        <h2>Investimentos Ativos</h2>
        {renderInvestmentSection("Investimentos Ativos", displayableInvestments, loadingData, error)}
      </div>

      <div className="investments-section">
        <h2>Histórico de Investimentos</h2>
        {renderInvestmentSection("Histórico", displayableHistory, loadingData, error)}
      </div>
    </div>
  );
}

export default WalletPage;