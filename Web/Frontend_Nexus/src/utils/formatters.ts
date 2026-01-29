
export const formatCurrency = (monto: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(monto);
};

/**
 * Formatea una fecha UTC a la zona horaria de Ecuador (America/Guayaquil, UTC-5)
 * @param dateString - Fecha en formato ISO string (UTC)
 * @returns Fecha formateada en zona horaria de Ecuador
 */
export const formatDateEcuador = (dateString: string | Date | null | undefined): string => {
  if (!dateString) return '';

  // Si es string y no tiene indicador de zona horaria (Z o +/-), asumimos UTC
  let date: Date;
  if (typeof dateString === 'string') {
    const hasTimezone = dateString.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(dateString);
    const utcString = hasTimezone ? dateString : dateString + 'Z';
    date = new Date(utcString);
  } else {
    date = dateString;
  }

  if (!date || isNaN(date.getTime())) return '';

  return date.toLocaleDateString('es-EC', {
    timeZone: 'America/Guayaquil',
    day: 'numeric',
    month: 'numeric',
    year: 'numeric',
  });
};

/**
 * Formatea una fecha y hora UTC a la zona horaria de Ecuador
 * @param dateString - Fecha en formato ISO string (UTC)
 * @returns Fecha y hora formateada en zona horaria de Ecuador
 */
export const formatDateTimeEcuador = (dateString: string | Date | null | undefined): string => {
  if (!dateString) return '';

  // Si es string y no tiene indicador de zona horaria (Z o +/-), asumimos UTC
  let date: Date;
  if (typeof dateString === 'string') {
    const hasTimezone = dateString.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(dateString);
    const utcString = hasTimezone ? dateString : dateString + 'Z';
    date = new Date(utcString);
  } else {
    date = dateString;
  }

  if (!date || isNaN(date.getTime())) return '';

  return date.toLocaleString('es-EC', {
    timeZone: 'America/Guayaquil',
    day: 'numeric',
    month: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * Formatea una fecha UTC a formato corto para Ecuador (día/mes)
 * @param dateString - Fecha en formato ISO string (UTC)
 * @returns Fecha formateada (ej: "29/1")
 */
export const formatDateShortEcuador = (dateString: string | Date | null | undefined): string => {
  if (!dateString) return '';

  // Si es string y no tiene indicador de zona horaria (Z o +/-), asumimos UTC
  let date: Date;
  if (typeof dateString === 'string') {
    const hasTimezone = dateString.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(dateString);
    const utcString = hasTimezone ? dateString : dateString + 'Z';
    date = new Date(utcString);
  } else {
    date = dateString;
  }

  if (!date || isNaN(date.getTime())) return '';

  return date.toLocaleDateString('es-EC', {
    timeZone: 'America/Guayaquil',
    day: 'numeric',
    month: 'numeric',
  });
};

/**
 * Formatea solo la hora en zona horaria de Ecuador
 * @param dateString - Fecha en formato ISO string (UTC)
 * @returns Hora formateada (ej: "23:49")
 */
export const formatTimeEcuador = (dateString: string | Date | null | undefined): string => {
  if (!dateString) return '';

  // Si es string y no tiene indicador de zona horaria (Z o +/-), asumimos UTC
  let date: Date;
  if (typeof dateString === 'string') {
    // Si el string no termina en 'Z' ni tiene offset (+/-), agregamos 'Z' para forzar UTC
    const hasTimezone = dateString.endsWith('Z') || /[+-]\d{2}:\d{2}$/.test(dateString);
    const utcString = hasTimezone ? dateString : dateString + 'Z';
    date = new Date(utcString);
  } else {
    date = dateString;
  }

  if (!date || isNaN(date.getTime())) return '';

  return date.toLocaleTimeString('es-EC', {
    timeZone: 'America/Guayaquil',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });
};
