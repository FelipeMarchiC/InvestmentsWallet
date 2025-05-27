import { useState } from "react";
import "./SummaryCard.css"; 

function SummaryCard() {

    const [summaryData] = useState({
        totalInvested: 0,
        expectedReturn: 0,
        expectedReturnPercentage: 0,
        totalAssets: 0,
        totalProfitability: 0,
        totalProfitabilityPercentage: 0,
    });

    const formatCurrency = (value) => {
        return value.toLocaleString("pt-BR", {
            style: "currency",
            currency: "BRL",
        });
    };

    return (

        <div className="new-summary-card">
            <h2>Resumo da Carteira</h2>
            <div className="new-summary-grid">
                <div className="new-info-box">
                    <p className="new-info-label">Total Investido</p>
                    <p className="new-info-value">
                        {formatCurrency(summaryData.totalInvested)}
                    </p>
                </div>
                <div className="new-info-box new-highlight-green">
                    <p className="new-info-label">Retorno Esperado</p>
                    <p className="new-info-value">
                        {formatCurrency(summaryData.expectedReturn)} (
                        {summaryData.expectedReturnPercentage.toFixed(2)}%)
                    </p>
                </div>
                <div className="new-info-box new-highlight-purple">
                    <p className="new-info-label">Total de Ativos</p>
                    <p className="new-info-value">{summaryData.totalAssets}</p>
                </div>
            </div>
            <div className="new-total-profitability">
                <p className="new-info-label">Rentabilidade Total</p>
                <p className="new-info-value-profit">
                    {formatCurrency(summaryData.totalProfitability)} (
                    {summaryData.totalProfitabilityPercentage.toFixed(2)}%)
                </p>
            </div>
        </div>
    )
}

export default SummaryCard;
