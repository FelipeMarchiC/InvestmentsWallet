import React, { useState, useEffect } from 'react';
import './WalletPage.css';
import SummaryCard from '../../components/SummaryCard/SummaryCard'; 
import AssetCard from '../../components/AssetCard/AssetCard';
import { walletService } from '../../services/walletService';
import { assetService } from '../../services/assetService';

function WalletPage() {
  // Estados para a lista de investimentos e para os detalhes de todos os ativos
  const [userInvestments, setUserInvestments] = useState([]);
  const [allAssets, setAllAssets] = useState([]);
  const [displayableInvestments, setDisplayableInvestments] = useState([]);
  
  const [loadingInvestments, setLoadingInvestments] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchInvestmentData = async () => {
      setLoadingInvestments(true);
      setError('');

      try {
        const [assetsData, investmentsData] = await Promise.all([
          assetService.getAllAssets(),
          walletService.getUserInvestments()
        ]);
        
        setAllAssets(assetsData);
        setUserInvestments(investmentsData);

      } catch (err) {
        setError(err.message || 'Erro ao carregar dados da carteira.');
        console.error(err);
      } finally {
        setLoadingInvestments(false); // Finaliza o loading dos investimentos
      }
    };

    fetchInvestmentData();
  }, []);

  // processa e combina os dados quando userInvestments ou allAssets mudar
  useEffect(() => {
    if (userInvestments.length > 0 && allAssets.length > 0) {
      const enrichedInvestments = userInvestments.map(inv => {
        const assetDetail = allAssets.find(asset => asset.id === inv.assetId);
        if (assetDetail) {
          return {
            id: inv.id,
            name: assetDetail.name,
            type: assetDetail.assetType,
            profitability: `${(assetDetail.profitability * 100).toFixed(2)}%`,
            maturity: new Date(assetDetail.maturityDate).toLocaleDateString('pt-BR'),
            minInvestment: `R$ ${inv.initialValue.toFixed(2)}`, // Usando initialValue do investimento
            description: assetDetail.name, // Ou outra descrição relevante
          };
        }
        return null;
      }).filter(Boolean);

      setDisplayableInvestments(enrichedInvestments);
    } else {
      setDisplayableInvestments([]);
    }
  }, [userInvestments, allAssets]);


  return (
    <div className="wallet-container">
      <div className="wallet-header">
        <h1>Minha Carteira</h1>
        <button className="new-investment-button">Novo Investimento</button>
      </div>

      <SummaryCard />

      {/* Seção de Investimentos */}
      <div className="investments-section">
        <h2>Investimentos</h2>
        {loadingInvestments ? (
          <p className="loading-message">Carregando seus investimentos...</p>
        ) : error ? (
          <p className="error-message">{error}</p>
        ) : (
          <div className="investments-list-grid">
            {displayableInvestments.length === 0 ? (
              <p className="empty-message">Você ainda não possui investimentos em sua carteira.</p>
            ) : (
              displayableInvestments.map((investment) => (
                <AssetCard key={investment.id} asset={investment} />
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default WalletPage;
