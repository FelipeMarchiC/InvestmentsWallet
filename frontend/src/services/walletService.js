// src/services/walletService.js
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

// Nova função para buscar o histórico de investimentos
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

export const walletService = {
  getUserInvestments,
  getUserHistoryInvestments, // Adiciona a nova função
};