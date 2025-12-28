// src/utils/formatters.ts
/**
 * @param monto
 * @returns 
 */
export const formatCurrency = (monto: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(monto);
};