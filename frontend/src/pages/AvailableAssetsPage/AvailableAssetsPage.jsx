import React, { useState, useEffect } from 'react';
import './AvailableAssetsPage.css';
import AssetCard from '../../components/AssetCard/AssetCard';
import { assetService } from '../../services/assetService';
import { useAuthGuard } from '../../hooks/useAuthGuard';

const formatAssetType = (apiAssetType) => {
  switch (apiAssetType) {
    case 'TESOURO_DIRETO':
      return 'Tesouro Direto';
    // O resto fica igual
    default:
      return apiAssetType;
  }
};

// Função auxiliar para formatar a data (YYYY-MM-DD para DD/MM/YYYY)
const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  const [year, month, day] = dateString.split('-');
  return `${day}/${month}/${year}`;
};

function AvailableAssetsPage() {
  useAuthGuard();

  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchAssets = async () => {
      try {
        setLoading(true);
        setError(null);
        const dataFromApi = await assetService.getAllAssets();

        // Mapeia os dados da API para o formato esperado pelo AssetCard
        const formattedAssets = dataFromApi.map(apiAsset => ({
          id: apiAsset.id,
          name: apiAsset.name,
          type: formatAssetType(apiAsset.assetType),
          profitability: `${(apiAsset.profitability*100).toFixed(2)}%`,
          maturity: formatDate(apiAsset.maturityDate), // Formata a data
        }));

        setAssets(formattedAssets);
      } catch (err) {
        console.error("Failed to fetch assets:", err);
        setError('Falha ao buscar os ativos.');
      } finally {
        setLoading(false);
      }
    };

    fetchAssets();
  }, []);

  if (loading) {
    return <div className="available-assets-container"><p>Carregando ativos...</p></div>;
  }

  if (error) {
    return <div className="available-assets-container"><p>{error}</p></div>;
  }

  return (
    <div className="available-assets-container">
      <h1>Ativos Disponíveis</h1>
      {assets.length === 0 && !loading && (
        <p>Nenhum ativo encontrado.</p>
      )}

      <div className="assets-grid">
        {assets.map((asset) => (
          <AssetCard key={asset.id} asset={asset} />
        ))}
      </div>
    </div>
  );
}

export default AvailableAssetsPage;