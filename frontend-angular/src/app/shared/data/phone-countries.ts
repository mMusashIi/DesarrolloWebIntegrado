export interface PhoneCountry {
  code: string;
  name: string;
  dialCode: string;
}

export const PHONE_COUNTRIES: PhoneCountry[] = [
  { code: 'PE', name: 'Perú', dialCode: '+51' },
  { code: 'AR', name: 'Argentina', dialCode: '+54' },
  { code: 'BO', name: 'Bolivia', dialCode: '+591' },
  { code: 'BR', name: 'Brasil', dialCode: '+55' },
  { code: 'CL', name: 'Chile', dialCode: '+56' },
  { code: 'CO', name: 'Colombia', dialCode: '+57' },
  { code: 'CR', name: 'Costa Rica', dialCode: '+506' },
  { code: 'CU', name: 'Cuba', dialCode: '+53' },
  { code: 'DO', name: 'República Dominicana', dialCode: '+1' },
  { code: 'EC', name: 'Ecuador', dialCode: '+593' },
  { code: 'SV', name: 'El Salvador', dialCode: '+503' },
  { code: 'GT', name: 'Guatemala', dialCode: '+502' },
  { code: 'HN', name: 'Honduras', dialCode: '+504' },
  { code: 'MX', name: 'México', dialCode: '+52' },
  { code: 'NI', name: 'Nicaragua', dialCode: '+505' },
  { code: 'PA', name: 'Panamá', dialCode: '+507' },
  { code: 'PY', name: 'Paraguay', dialCode: '+595' },
  { code: 'UY', name: 'Uruguay', dialCode: '+598' },
  { code: 'VE', name: 'Venezuela', dialCode: '+58' },
  { code: 'US', name: 'Estados Unidos / Canadá', dialCode: '+1' },
  { code: 'ES', name: 'España', dialCode: '+34' },
  { code: 'FR', name: 'Francia', dialCode: '+33' },
  { code: 'DE', name: 'Alemania', dialCode: '+49' },
  { code: 'IT', name: 'Italia', dialCode: '+39' },
  { code: 'GB', name: 'Reino Unido', dialCode: '+44' },
  { code: 'CN', name: 'China', dialCode: '+86' },
  { code: 'JP', name: 'Japón', dialCode: '+81' },
  { code: 'AU', name: 'Australia', dialCode: '+61' }
];

export const NATIONAL_PHONE_PATTERN = /^\d{6,12}$/;

export function toE164(dialCode: string, nationalNumber: string): string {
  const prefix = String(dialCode || '').replace(/[^\d]/g, '');
  const digits = String(nationalNumber || '').replace(/[^\d]/g, '');
  // Italia conserva el cero inicial en formato internacional; la mayoría de países usa ese cero como prefijo troncal.
  const national = dialCode === '+39' ? digits : digits.replace(/^0+/, '');
  return `+${prefix}${national}`;
}

export function splitE164(phone?: string): { dialCode: string; nationalNumber: string } {
  const normalized = String(phone || '').replace(/[^+\d]/g, '');
  const country = [...PHONE_COUNTRIES]
    .sort((a, b) => b.dialCode.length - a.dialCode.length)
    .find(item => normalized.startsWith(item.dialCode));
  return country
    ? { dialCode: country.dialCode, nationalNumber: normalized.slice(country.dialCode.length) }
    : { dialCode: '+51', nationalNumber: normalized.replace(/^\+/, '') };
}
