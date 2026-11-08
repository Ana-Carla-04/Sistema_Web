// Espelha as validações do PlantioRequestDTO do back-end.
export function validatePlantio(values) {
  const errors = {};

  if (!values.nome.trim()) {
    errors.nome = 'Informe o nome do lote.';
  }

  if (values.area !== '') {
    const area = Number(String(values.area).replace(',', '.'));
    if (Number.isNaN(area) || area <= 0) {
      errors.area = 'A área deve ser maior que zero.';
    }
  }

  if (!values.dataPlantio) {
    errors.dataPlantio = 'Informe a data de plantio.';
  }

  if (!values.status) {
    errors.status = 'Selecione o status.';
  }

  return errors;
}
