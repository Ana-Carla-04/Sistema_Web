import Icon from '../../components/Icons/Icon.jsx';
import useForm from '../../hooks/useForm.js';
import { KG_POR_ALQUEIRE } from '../../utils/colheita.js';
import { validateSimulacao } from '../../utils/validators.js';
import styles from './SimuladorRendimento.module.css';

const INITIAL_VALUES = {
    producaoBruta: '',
    umidade: '',
    descontoUmidade: '',
    precoAlqueiro: '',
    taxaMaquina: '',
};

// A ordem aqui é a ordem na tela (2 colunas, depois do select de lote).
const FIELDS = [
    { name: 'producaoBruta', label: 'Produção Bruta (kg) - Peso Total com Umidade', placeholder: '35.000', suffix: 'kg' },
    { name: 'umidade', label: 'Umidade (%)', placeholder: '16,5', suffix: '%' },
    { name: 'descontoUmidade', label: 'Desconto por Umidade (%)', placeholder: '3,5', suffix: '%' },
    { name: 'precoAlqueiro', label: `Preço por Alqueire (${KG_POR_ALQUEIRE} kg)`, placeholder: '220,00', prefix: 'R$' },
    { name: 'taxaMaquina', label: 'Taxa da Máquina de Colheita (%)', placeholder: '15', suffix: '%' },
];

export default function SimuladorRendimento({
                                                lotes,
                                                loteId,
                                                onLoteChange,
                                                loadingLotes,
                                                lotesError,
                                                calculando,
                                                onCalcular,
                                            }) {
    const { values, errors, handleChange, handleBlur, handleSubmit } = useForm({
        initialValues: INITIAL_VALUES,
        validate: validateSimulacao,
    });

    const semLotes = !loadingLotes && lotes.length === 0;

    return (
        <form className={styles.card} onSubmit={handleSubmit(onCalcular)} noValidate>
            <h2 className={styles.title}>Simulador de Rendimento do Lote</h2>

            <div className={styles.grid}>
                <div className={styles.field}>
                    <label htmlFor="lote">Lote Selecionado</label>
                    <div className={styles.selectWrap}>
                        <select
                            id="lote"
                            value={loteId}
                            onChange={(event) => onLoteChange(event.target.value)}
                            disabled={loadingLotes || semLotes}
                        >
                            {loadingLotes && <option value="">Carregando...</option>}
                            {semLotes && <option value="">Nenhum plantio cadastrado</option>}
                            {lotes.map((lote) => (
                                <option key={lote.id} value={String(lote.id)}>
                                    {lote.nome}
                                </option>
                            ))}
                        </select>
                        <Icon name="chevron" size={14} />
                    </div>
                </div>

                {FIELDS.map((field) => (
                    <div key={field.name} className={styles.field}>
                        <label htmlFor={field.name}>{field.label}</label>
                        <div className={errors[field.name] ? `${styles.inputWrap} ${styles.invalid}` : styles.inputWrap}>
                            {field.prefix && <span className={styles.adornment}>{field.prefix}</span>}
                            <input
                                id={field.name}
                                name={field.name}
                                inputMode="decimal"
                                value={values[field.name]}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                placeholder={field.placeholder}
                                aria-invalid={Boolean(errors[field.name])}
                            />
                            {field.suffix && <span className={styles.adornment}>{field.suffix}</span>}
                        </div>
                        {errors[field.name] && <span className={styles.error}>{errors[field.name]}</span>}
                    </div>
                ))}
            </div>

            {lotesError && (
                <p className={styles.alert} role="alert">
                    {lotesError}
                </p>
            )}
            {semLotes && !lotesError && (
                <p className={styles.hint}>Cadastre um plantio na tela Plantios para simular a colheita.</p>
            )}

            <button type="submit" className={styles.submit} disabled={calculando || !loteId}>
                {calculando ? 'Calculando...' : 'Calcular Receita Estimada'}
            </button>
        </form>
    );
}