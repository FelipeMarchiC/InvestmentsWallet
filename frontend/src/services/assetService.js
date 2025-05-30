import axios from 'axios';
import { getItemWithExpiry } from '../utils/storageWithExpiry';

const API_BASE_URL = '/api/v1';

const getAllAssets = async () => {
  try {
    const token = getItemWithExpiry("authToken");
    if (!token) {
      throw new Error('No auth token found');
    }

    const response = await axios.get(`${API_BASE_URL}/asset`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data; // Espera-se uma lista de AssetResponseDTO
  } catch (error) {
    console.error('Get All Assets API Error:', error.response || error.message);
    throw new Error(error.response?.data?.message || error.message || 'Failed to fetch assets');
  }
};

export const assetService = {
  getAllAssets,
};