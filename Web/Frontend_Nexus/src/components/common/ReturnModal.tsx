import { useState } from 'react';
import { Boton } from './Boton';
import { X } from 'lucide-react';

interface ReturnModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: (reason: string) => void;
    isLoading?: boolean;
}

const REASON_CODES = [
    { code: 'AC01', label: 'Cuenta inexistente / Incorrecta' },
    { code: 'AC04', label: 'Cuenta Cerrada' },
    { code: 'AG01', label: 'Transacción Prohibida / Bloqueada' },
    { code: 'AM04', label: 'Fondos Insuficientes (Reverso)' },
    { code: 'MS03', label: 'Error Técnico / Operativo' },
    { code: 'FRAD', label: 'Fraude detectado' }, // Custom or ISO generic?
];

export const ReturnModal = ({ isOpen, onClose, onConfirm, isLoading }: ReturnModalProps) => {
    const [selectedReason, setSelectedReason] = useState('AC04');

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
            <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in duration-200">
                <div className="p-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
                    <h3 className="font-bold text-gray-800">Devolver Transacción</h3>
                    <button onClick={onClose} className="p-1 hover:bg-gray-200 rounded-full transition-colors">
                        <X size={20} className="text-gray-500" />
                    </button>
                </div>

                <div className="p-6 space-y-4">
                    <div className="bg-yellow-50 text-yellow-800 p-3 rounded-lg text-sm border border-yellow-100">
                        Esta acción iniciará un proceso de devolución (pacs.004) al Banco Origen.
                        Validar normativa antes de proceder.
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Motivo de la Devolución
                        </label>
                        <select
                            value={selectedReason}
                            onChange={(e) => setSelectedReason(e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-ecusol-primario outline-none bg-white"
                        >
                            {REASON_CODES.map(r => (
                                <option key={r.code} value={r.code}>
                                    {r.code} - {r.label}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>

                <div className="p-4 border-t border-gray-100 bg-gray-50 flex justify-end gap-3">
                    <Boton variante="secundario" onClick={onClose} disabled={isLoading}>
                        Cancelar
                    </Boton>
                    <Boton
                        variante="primario"
                        onClick={() => onConfirm(selectedReason)}
                        disabled={isLoading}
                    >
                        {isLoading ? 'Procesando...' : 'Confirmar Devolución'}
                    </Boton>
                </div>
            </div>
        </div>
    );
};
