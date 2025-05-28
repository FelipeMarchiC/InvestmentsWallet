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
    return response.data;
  } catch (error) {
    console.error('Get User Investments API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch user investments');
  }
};

const getUserHistoryInvestments = async () => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No auth token found');
    }
    const response = await axios.get(`${API_BASE_URL}/wallet/history`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data; // Espera-se uma lista de InvestmentResponseDTO
  } catch (error) {
    console.error('Get User History Investments API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch user history investments');
  }
};

const withdrawInvestment = async (investmentId) => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No auth token found');
    }
    const response = await axios.post(`${API_BASE_URL}/wallet/investment/withdraw/${investmentId}`, {}, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response;
  } catch (error) {
    console.error('Withdraw Investment API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to withdraw investment');
  }
};

export const walletService = {
  getUserInvestments,
  getUserHistoryInvestments,
  withdrawInvestment,
};
