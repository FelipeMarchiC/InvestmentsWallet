// src/hooks/useWalletData.js
import { useState, useEffect, useCallback } from 'react';
import { walletService } from '../services/walletService';
import { assetService } from '../services/assetService';

export function useWalletData() {
  const [userActiveInvestments, setUserActiveInvestments] = useState([]); 
  const [userHistoryInvestments, setUserHistoryInvestments] = useState([]);
  const [allAssets, setAllAssets] = useState([]);

  const [totalBalance, setTotalBalance] = useState(0);
  const [expectedReturn, setExpectedReturn] = useState(0);
  const [countInvestments, setCountInvestments] = useState(0);

  const [loadingData, setLoadingData] = useState(true); 
  const [error, setError] = useState('');

  const fetchAllWalletData = useCallback(async () => {
    setLoadingData(true);
    setError('');
    try {
      const [
        assetsData,
        activeInvestmentsData,
        historyData,
        totalBalanceData,
        futureBalanceData
      ] = await Promise.all([
        assetService.getAllAssets(),
        walletService.getUserInvestments(),
        walletService.getUserHistoryInvestments(),
        walletService.getWalletTotalBalance(),
        walletService.getWalletFutureBalance()
      ]);
      

      setAllAssets(assetsData);
      setUserActiveInvestments(activeInvestmentsData); 
      setUserHistoryInvestments(historyData);

      setTotalBalance(totalBalanceData);
      setExpectedReturn(futureBalanceData);
      setCountInvestments(activeInvestmentsData.length + historyData.length);
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
    totalBalance,
    expectedReturn,
    countInvestments,
    loadingData,
    error,
    refreshWalletData: fetchAllWalletData,
  };
}
