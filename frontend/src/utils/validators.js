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

// Validação do formulário de Login (espelha LoginRequestDTO)
export function validateLogin(values) {
  const errors = {};

  if (!values.email || !values.email.trim()) {
    errors.email = 'O e-mail é obrigatório.';
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email.trim())) {
    errors.email = 'Informe um e-mail válido.';
  }

  if (!values.senha) {
    errors.senha = 'A senha é obrigatória.';
  }

  return errors;
}

// Validação do formulário de Cadastro (espelha CadastroRequestDTO)
export function validateCadastro(values) {
  const errors = {};

  if (!values.nome || !values.nome.trim()) {
    errors.nome = 'O nome é obrigatório.';
  }

  if (!values.email || !values.email.trim()) {
    errors.email = 'O e-mail é obrigatório.';
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email.trim())) {
    errors.email = 'Informe um e-mail válido.';
  }

  if (!values.senha) {
    errors.senha = 'A senha é obrigatória.';
  } else if (values.senha.length < 6) {
    errors.senha = 'A senha deve ter no mínimo 6 caracteres.';
  }

  return errors;
}

