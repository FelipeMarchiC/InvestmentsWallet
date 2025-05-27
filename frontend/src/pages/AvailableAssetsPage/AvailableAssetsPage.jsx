import React, { useState } from 'react';
import './AvailableAssetsPage.css';
import AssetCard from '../../components/AssetCard/AssetCard';
import { FaFilter, FaBroom } from 'react-icons/fa';

function AvailableAssetsPage() {
  const [filters, setFilters] = useState({
    assetType: '',
    minProfitability: 0,
    minValue: 0,
    maxValue: 100000,
  });

  const [assets, setAssets] = useState([
    {
      id: '1',
      name: 'CDB Banco ABC',
      bankRisk: 'Banco Risco',
      type: 'CDB',
      profitability: '11.50%',
      maturity: '30/12/2025',
      minInvestment: 'R$ 1.000,00',
      description: 'CDB com rentabilidade de 110% do CDI, com liquidez diária após 30 dias.',
    },
    {
      id: '2',
      name: 'LCI Banco XYZ',
      bankRisk: 'Banco Risco',
      type: 'LCI',
      profitability: '10.80%',
      maturity: '29/06/2026',
      minInvestment: 'R$ 5.000,00',
      description: 'LCI com rentabilidade de 103% do CDI, isento de IR para pessoa física.',
    },
    {
      id: '3',
      name: 'Tesouro Prefixado 2026',
      bankRisk: 'Banco Risco',
      type: 'Tesouro Direto',
      profitability: '10.20%',
      maturity: '31/12/2025',
      minInvestment: 'R$ 100,00',
      description: 'Título público com taxa prefixada, garantido pelo Tesouro Nacional.',
    },
  ]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: name === 'assetType' ? value : parseFloat(value) || 0,
    }));
  };

  const applyFilters = () => {
    console.log('Filtros aplicados:', filters);
    // Lógica de filtro aqui
  };

  const clearFilters = () => {
    setFilters({
      assetType: '',
      minProfitability: 0,
      minValue: 0,
      maxValue: 100000,
    });
  };

  return (
    <div className="available-assets-container">
      <h1>Ativos Disponíveis</h1>

      <div className="filter-card">
        <h2>Filtrar Ativos</h2>
        <div className="filter-form-row">
          <div className="filter-form-group">
            <label htmlFor="assetType">Tipo do Ativo</label>
            <select id="assetType" name="assetType" value={filters.assetType} onChange={handleFilterChange}>
              <option value="">Todos os tipos</option>
              <option value="CDB">CDB</option>
              <option value="LCI">LCI</option>
              <option value="LCA">LCA</option>
              <option value="TESOURO_DIRETO">Tesouro Direto</option>
            </select>
          </div>
          <div className="filter-form-group">
            <label htmlFor="minProfitability">Rentabilidade Mínima</label>
            <div className="input-with-symbol">
              <input type="number" id="minProfitability" name="minProfitability" value={filters.minProfitability} onChange={handleFilterChange} placeholder="0"/>
              <span>%</span>
            </div>
          </div>
          <div className="filter-form-group">
            <label htmlFor="minValue">Valor Mínimo</label>
            <div className="input-with-symbol">
              <span>R$</span>
              <input type="number" id="minValue" name="minValue" value={filters.minValue} onChange={handleFilterChange} placeholder="0"/>
            </div>
          </div>
          <div className="filter-form-group">
            <label htmlFor="maxValue">Valor Máximo</label>
            <div className="input-with-symbol">
              <span>R$</span>
              <input type="number" id="maxValue" name="maxValue" value={filters.maxValue} onChange={handleFilterChange} placeholder="100000"/>
            </div>
          </div>
        </div>
        <div className="filter-actions">
          <button onClick={applyFilters} className="apply-filters-button">
            <FaFilter /> Aplicar Filtros
          </button>
          <button onClick={clearFilters} className="clear-filters-button">
            <FaBroom /> Limpar Filtros
          </button>
        </div>
      </div>

      <div className="assets-grid">
        {assets.map((asset) => (
          <AssetCard key={asset.id} asset={asset} />
        ))}
      </div>
    </div>
  );
}

export default AvailableAssetsPage;