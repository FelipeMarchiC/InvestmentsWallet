import { useEffect, useState } from "react";

export const useDisplayableInvestments = (userInvestments, allAssets, loadingData, isHistory = false) => {
  const [displayableInvestments, setDisplayableInvestments] = useState([]);

  useEffect(() => {
    if (allAssets.length > 0) {
      const enriched = userInvestments
        .map((inv) => {
          const assetDetail = allAssets.find(asset => asset.id === inv.assetId);
          if (!assetDetail) return null;
          const initialVal = inv.initialValue || 0;

          return {
            id: inv.id,
            assetName: assetDetail.name,
            assetSubtitle: `Banco ${assetDetail.name.split(' ')[1] || 'Genérico'}`,
            type: assetDetail.assetType,
            value: initialVal,
            investmentDate: inv.purchaseDate,
            maturityDate: isHistory ? (inv.withdrawDate || assetDetail.maturityDate) : assetDetail.maturityDate,
            isHistory,
          };
        })
        .filter(Boolean);

      setDisplayableInvestments(enriched);
    } else if (!loadingData) {
      setDisplayableInvestments([]);
    }
  }, [userInvestments, allAssets, loadingData, isHistory]);

  return displayableInvestments;
};
