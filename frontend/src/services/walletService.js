import axios from 'axios';

const API_BASE_URL = '/api/v1';

const getUserInvestments = async () => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No auth token found');
    }

    const response = await axios.get(`${API_BASE_URL}/wallet/investment`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data; // Espera-se uma lista de InvestmentResponseDTO
  } catch (error) {
    console.error('Get User Investments API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch user investments');
  }
};

// Poderíamos adicionar aqui também as funções para buscar o resumo da carteira (total investido, etc.)
// Por exemplo:
// const getWalletSummary = async () => { ... }

export const walletService = {
  getUserInvestments,
  // getWalletSummary, // Descomente e implemente quando precisar
};