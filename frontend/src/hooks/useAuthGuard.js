import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getItemWithExpiry } from '../utils/storageWithExpiry';

export function useAuthGuard(key = 'authToken') {
  const navigate = useNavigate();

  useEffect(() => {
    const checkToken = () => {
      const token = getItemWithExpiry(key);
      if (!token) {
        localStorage.clear();
        navigate('/login');
      }
    };

    checkToken();

    const interval = setInterval(checkToken, 30 * 1000); // A cada 30s

    return () => clearInterval(interval);
  }, [key, navigate]);
}
