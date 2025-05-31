import axios from 'axios';
import { getItemWithExpiry } from '../utils/storageWithExpiry';

const API_BASE_URL = '/api/v1';

const getUserInvestments = async () => {
  try {
    const token = getItemWithExpiry("authToken");
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
    const token = getItemWithExpiry("authToken");
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
    const token = getItemWithExpiry("authToken");
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

const getWalletTotalBalance = async () => {
  try {
    const token = getItemWithExpiry("authToken");
    if (!token) {
      throw new Error('No auth token found');
    }
    const response = await axios.get(`${API_BASE_URL}/wallet/totalBalance`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data; // Espera-se um Double com o valor do balance
  } catch (error) {
    console.error('Get Wallet Total Balance API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch total balance');
  }
};

const getWalletFutureBalance = async () => {
  try {
    const token = getItemWithExpiry("authToken");
    if (!token) {
      throw new Error('No auth token found');
    }
    const response = await axios.get(`${API_BASE_URL}/wallet/futureBalance`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data; // Espera-se um Double com o valor do balance
  } catch (error) {
    console.error('Get Wallet Future Balance API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch future balance');
  }
};

const generateWalletReport = async () => {
  try {
    const token = getItemWithExpiry("authToken");
    if (!token) {
      throw new Error('No auth token found');
    }
    const response = await axios.get(`${API_BASE_URL}/wallet/report`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Generate report API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to generate report');
  }
};

const removeInvestment = async (investmentId) => {
  try {
    const token = getItemWithExpiry("authToken");
    if (!token) {
      throw new Error('No auth token found');
    }
    const response = await axios.delete(`${API_BASE_URL}/wallet/investment/${investmentId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Remove investment error: ', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to remove investment');
  }
}

export const walletService = {
  getUserInvestments,
  getUserHistoryInvestments,
  withdrawInvestment,
  getWalletTotalBalance,
  getWalletFutureBalance,
  generateWalletReport,
  removeInvestment
};
