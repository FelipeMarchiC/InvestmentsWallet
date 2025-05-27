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
        <h3>{asset.name}</h3>
        <div className="asset-tags">
          {asset.bankRisk && <span className="tag tag-risk">{asset.bankRisk}</span>}
          {asset.type && <span className="tag tag-type">{asset.type}</span>}
        </div>
      </div>
      <div className="asset-details">
        <p>Rentabilidade: <strong>{asset.profitability}</strong></p>
        <p>Vencimento: <strong>{asset.maturity}</strong></p>
        <p>Investimento mínimo: <strong>{asset.minInvestment}</strong></p>
      </div>
      <p className="asset-description">{asset.description}</p>
      <button className="invest-button">
        <FaChartLine /> Investir
      </button>
    </div>
  );
}

export default AssetCard;