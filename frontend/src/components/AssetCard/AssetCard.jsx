import React from 'react';
import './AssetCard.css';
import { FaChartLine } from 'react-icons/fa';

function AssetCard({ asset }) {
  if (!asset) {
    return null;
  }

  return (
    <div className="asset-card">
      <div className="asset-card-header">
        <h3>{asset.name || 'Nome Indisponível'}</h3>
        <div className="asset-tags">
          {asset.type && <span className="tag tag-type">{asset.type}</span>}
        </div>
      </div>
      <div className="asset-details">
        <p>Rentabilidade: <strong>{asset.profitability || 'N/A'}</strong></p>
        <p>Vencimento: <strong>{asset.maturity || 'N/A'}</strong></p>
      </div>
      <button className="invest-button">
        <FaChartLine /> Investir
      </button>
    </div>
  );
}

export default AssetCard;