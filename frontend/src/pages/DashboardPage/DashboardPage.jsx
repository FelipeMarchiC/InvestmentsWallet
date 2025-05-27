import React, { useState } from "react";
import "./DashboardPage.css";
import { FaChartBar, FaStar, FaDollarSign } from "react-icons/fa";
import SummaryCard from "../../components/SummaryCard/SummaryCard";

function DashboardPage() {
  const [userName] = useState("Tiago");
  const [availableBalance, setAvailableBalance] = useState(0);
  const [recentInvestments] = useState([]);

  const [opportunities, setOpportunities] = useState([
    {
      id: 1,
      name: "Debênture Empresa Alpha",
      type: "Debênture Incentivada",
      description:
        "Debênture incentivada com isenção de IR para pessoa física...",
      profitability: "13.50%",
      minInvestment: 10000,
    },
    {
      id: 2,
      name: "CDB Banco GHI",
      type: "CDB",
      description:
        "CDB com rentabilidade de 115% do CDI, com carência de 1 ano...",
      profitability: "12.20%",
      minInvestment: 2000,
    },
  ]);

  const formatCurrency = (value) => {
    return value.toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    });
  };

  return (
    <div className="new-dashboard-container">
      <div className="new-dashboard-header">
        <h1>Dashboard</h1>
        <button className="deposit-button">
          <FaDollarSign /> Depositar (Demo)
        </button>
      </div>

      <div className="new-dashboard-main-content">
        <div className="new-dashboard-left-column">
          <div>
            <SummaryCard/>
          </div>

          <div className="new-recent-investments-card">
            <h2>Investimentos Recentes</h2>
            {recentInvestments.length === 0 ? (
              <p className="new-empty-message">
                Nenhum investimento recente para exibir.
              </p>
            ) : (
              <ul>{/* Mapear os investimentos recentes aqui */}</ul>
            )}
          </div>
        </div>
        <div className="new-dashboard-right-column">
          <div className="new-welcome-card">
            <h3>Bem-vindo, {userName}</h3>
            <p>Seu saldo disponível</p>
            <p className="new-balance-value">
              {formatCurrency(availableBalance)}
            </p>
            <button className="invest-now-button">
              <FaChartBar /> Investir Agora
            </button>
          </div>

          <div className="new-opportunities-card">
            <h3>Melhores Oportunidades</h3>
            <div className="new-opportunities-list">
              {opportunities.map((opp) => (
                <div key={opp.id} className="new-opportunity-item">
                  <div className="new-opportunity-header">
                    <h4 className="new-opportunity-item-title">{opp.name}</h4>
                    <span className="new-opportunity-profitability">
                      {opp.profitability}
                    </span>
                  </div>
                  <p className="new-opportunity-type">{opp.type}</p>
                  <p className="new-opportunity-description">
                    {opp.description}
                  </p>
                  <div className="new-opportunity-footer">
                    <span className="new-opportunity-min">
                      Min: {formatCurrency(opp.minInvestment)}
                    </span>
                    <button className="view-details-button">
                      Ver Detalhes
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;
