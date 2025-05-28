import React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString; 
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  } catch (e) {
    console.error('Erro ao formatar data:', e);
    return dateString;
  }
};

const formatCurrency = (value, showSymbol = true) => {
  if (typeof value !== 'number' || isNaN(value)) return 'N/A';
  return value.toLocaleString('pt-BR', {
    style: showSymbol ? 'currency' : 'decimal',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

export default function InvestmentDetailModal({ isOpen, onClose, investment }) {
  if (!isOpen || !investment) {
    return null;
  }

  const handleWithdrawClick = () => {
    // Lógica futura para retirar investimento
    console.log("Botão 'Retirar Investimento' clicado para o investimento ID:", investment.id);
  };

  return (
    <Dialog
      open={isOpen}
      onClose={onClose}
      aria-labelledby="investment-detail-title"
      PaperProps={{
        sx: { 
          width: '90%',
          maxWidth: '550px',
          borderRadius: '8px',
        }
      }}
    >
      <DialogTitle id="investment-detail-title" sx={{ textAlign: 'center', pb: 1 }}>
        Detalhes do Investimento
      </DialogTitle>
      <DialogContent dividers> 
        <Box sx={{ '& p': { mb: 1.5, color: '#333' } }}>
          <Typography variant="subtitle1" gutterBottom>
            <strong>Ativo:</strong> {investment.assetName || 'N/A'}
            {investment.assetSubtitle && (
              <Typography variant="body2" component="span" sx={{ color: '#6c757d', display: 'block' }}>
                {investment.assetSubtitle}
              </Typography>
            )}
          </Typography>
          <Typography variant="body1"><strong>Tipo:</strong> {investment.type || 'N/A'}</Typography>
          <Typography variant="body1"><strong>Valor Aplicado:</strong> {formatCurrency(investment.value)}</Typography>
          <Typography variant="body1">
            <strong>Retorno Atual/Esperado:</strong> {formatCurrency(investment.expectedReturn)}
            {(typeof investment.returnProfit === 'number' && investment.returnPercentage) && (
              <Typography variant="caption" component="span" sx={{ color: '#28a745', display: 'block', fontSize: '0.8rem' }}>
                {`Lucro: ${formatCurrency(investment.returnProfit, false)} (${investment.returnPercentage})`}
              </Typography>
            )}
          </Typography>
          <Typography variant="body1"><strong>Data do Investimento:</strong> {formatDate(investment.investmentDate)}</Typography>
          <Typography variant="body1">
            <strong>{investment.isHistory ? 'Data do Saque:' : 'Vencimento:'}</strong> {formatDate(investment.maturityDate)}
          </Typography>
          {investment.description && investment.description !== investment.assetName && ( 
            <Typography variant="body2" sx={{ mt: 1, color: '#555' }}>
                <strong>Descrição Adicional:</strong> {investment.description}
            </Typography>
          )}
        </Box>
      </DialogContent>
      <DialogActions sx={{ p: '16px 24px' }}>
        <Button onClick={onClose} variant="outlined">
          Voltar
        </Button>
        {!investment.isHistory && (
          <Button onClick={handleWithdrawClick} variant="contained" color="primary">
            Retirar Investimento
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
