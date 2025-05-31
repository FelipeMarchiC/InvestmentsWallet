import React, { useState } from "react";
import InvestmentCard from "../InvestmentCard/InvestmentCard";
import './InvestmentListSection.css';
import InvestmentDetailModal from "../InvestmentDetailModal/InvestmentDetailModal";

const InvestmentListSection = ({
  title,
  investmentsList,
  isLoading,
  specificError,
  refreshWalletData
}) => {
  if (isLoading) {
    return <p className="loading-message">Carregando {title.toLowerCase()}...</p>;
  }

  if (specificError) {
    return <p className="error-message">{specificError}</p>;
  }


  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedInvestmentForModal, setSelectedInvestmentForModal] = useState(null);

  const handleOpenInvestmentModal = (investmentData) => {
    setSelectedInvestmentForModal(investmentData);
    setIsModalOpen(true);
  };

  const handleCloseInvestmentModal = () => {
    setIsModalOpen(false);
    setSelectedInvestmentForModal(null);
  };

  const handleOperationSuccess = () => {
    refreshWalletData();
  };

  return (
    <>
      <div className="investment-row-header">
        <div className="header-cell asset-name-cell">ATIVO</div>
        <div className="header-cell asset-type-cell">TIPO</div>
        <div className="header-cell asset-value-cell">VALOR</div>
        <div className="header-cell asset-date-cell">DATA INVESTIMENTO</div>
        <div className="header-cell asset-maturity-cell">VENCIMENTO</div>
      </div>
      {investmentsList.length === 0 ? (
        <p className="empty-message full-width-message">
          Você não possui {title === "Investimentos Ativos" ? "investimentos ativos" : "movimentações no histórico"}.
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
        <InvestmentDetailModal
          isOpen={isModalOpen}
          onClose={handleCloseInvestmentModal}
          investment={selectedInvestmentForModal}
          onOperationSuccess={handleOperationSuccess}
        />
    </>
  );
};

export default InvestmentListSection;
