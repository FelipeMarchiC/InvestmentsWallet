export function setItemWithExpiry(key, value) {
  const now = new Date();
  const item = {
    value,
    expiry: now.getTime() + 8640000, // ~2h24min
  };
  localStorage.setItem(key, JSON.stringify(item));
}

export function getItemWithExpiry(key) {
  const itemStr = localStorage.getItem(key);
  if (!itemStr) return null;

  try {
    const item = JSON.parse(itemStr);
    const now = new Date();

    if (now.getTime() > item.expiry) {
      localStorage.removeItem(key);
      return null;
    }

    return item.value;
  } catch (e) {
    console.error('Erro ao ler item com expiração:', e);
    localStorage.removeItem(key);
    return null;
  }
}

export function removeItem(key) {
  localStorage.removeItem(key);
}
