import axios from 'axios';

const API_BASE_URL = '/api/v1';

const login = async (username, password) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/authenticate`, {
      username,
      password,
    });
    return response.data;
  } catch (error) {
    console.error('Full Axios error object:', error);
    if (error.response) {
      console.error('Authentication API Error - Status:', error.response.status);
      console.error('Authentication API Error - Data:', error.response.data);
      throw new Error(error.response.data?.message || `Authentication failed. Status: ${error.response.status}`);
    } else if (error.request) {
      console.error('Authentication Network Error - Request made but no response:', error.request);
      throw new Error('Network error. Please check your connection (proxied request).');
    } else {
      console.error('Authentication Setup Error - Message:', error.message);
      throw new Error('An unexpected error occurred during login setup.');
    }
  }
};

const register = async (userData) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/register`, userData);
    return response.data;
  } catch (error) {
    console.error('Full Axios error object (register):', error);
    if (error.response) {
      console.error('Registration API Error - Status:', error.response.status);
      console.error('Registration API Error - Data:', error.response.data);
      throw new Error(error.response.data?.message || `Registration failed. Status: ${error.response.status}`);
    } else if (error.request) {
      console.error('Registration Network Error - Request made but no response:', error.request);
      throw new Error('Network error. Please check your connection (proxied request).');
    } else {
      console.error('Registration Setup Error - Message:', error.message);
      throw new Error('An unexpected error occurred during registration setup.');
    }
  }
};

export const authService = {
  login,
  register,
};