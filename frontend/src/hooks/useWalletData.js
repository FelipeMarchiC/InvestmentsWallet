// src/hooks/useWalletData.js
import { useState, useEffect, useCallback } from 'react';
import { walletService } from '../services/walletService';
import { assetService } from '../services/assetService';

export function useWalletData() {
  const [userActiveInvestments, setUserActiveInvestments] = useState([]); 
  const [userHistoryInvestments, setUserHistoryInvestments] = useState([]);
  const [allAssets, setAllAssets] = useState([]);

  const [loadingData, setLoadingData] = useState(true); 
  const [error, setError] = useState('');

  const fetchAllWalletData = useCallback(async () => {
    setLoadingData(true);
    setError('');
    try {
      const [assetsData, activeInvestmentsData, historyData] = await Promise.all([
        assetService.getAllAssets(),
        walletService.getUserInvestments(),
        walletService.getUserHistoryInvestments()
      ]);
      
      setAllAssets(assetsData);
      setUserActiveInvestments(activeInvestmentsData); 
      setUserHistoryInvestments(historyData);
    } catch (err) {
      const errorMessage = 'Erro ao carregar dados da carteira.';
      setError(errorMessage);
      console.error("Erro ao buscar dados da carteira:", err);
    } finally {
      setLoadingData(false);
    }
  }, []);

  useEffect(() => {
    fetchAllWalletData();
  }, [fetchAllWalletData]);

  return {
    userActiveInvestments,
    userHistoryInvestments,
    allAssets,
    loadingData,
    error,
    refreshWalletData: fetchAllWalletData,
  };
}
