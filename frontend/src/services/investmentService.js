import axios from 'axios';

const API_BASE_URL = '/api/v1';

const registerInvestment = async (initialValue, assetId) => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No auth token found');
    }

    const requestData = {
      initialValue: initialValue,
      assetId: assetId
    };

    const requestConfig = {
      headers: {
        Authorization: `Bearer ${token}`,
      }
    };

    const response = await axios.post(`${API_BASE_URL}/wallet/investment`, requestData, requestConfig);
    return response;
  } catch (error) {
    console.error('Post investment API error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch assets');
  }
};

export const investmentService = {
  registerInvestment,
};