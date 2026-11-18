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
  } else if (values.senha.length < 8) {
    errors.senha = 'A senha deve ter no mínimo 8 caracteres.';
  } else if (!/^(?=.*[A-Za-z])(?=.*\d).+$/.test(values.senha)) {
    errors.senha = 'A senha deve conter pelo menos uma letra e um número.';
  }

  return errors;
}

// Validação do formulário de Simulação de Colheita (SimuladorRendimento)
export function validateSimulacao(values) {
  const errors = {};

  // Função auxiliar para transformar string em número (ex: "35.000" ou "3,5" -> 35000 ou 3.5)
  const parseNumero = (val) => {
    if (!val) return NaN;
    return Number(String(val).replace(/\./g, '').replace(',', '.'));
  };

  // Produção Bruta
  const producao = parseNumero(values.producaoBruta);
  if (values.producaoBruta === '' || Number.isNaN(producao) || producao <= 0) {
    errors.producaoBruta = 'Informe uma produção válida maior que zero.';
  }

  // Umidade
  const umidade = parseNumero(values.umidade);
  if (values.umidade !== '' && (Number.isNaN(umidade) || umidade < 0 || umidade > 100)) {
    errors.umidade = 'A umidade deve estar entre 0 e 100%.';
  }

  // Desconto de Umidade
  const desconto = parseNumero(values.descontoUmidade);
  if (values.descontoUmidade !== '' && (Number.isNaN(desconto) || desconto < 0 || desconto > 100)) {
    errors.descontoUmidade = 'O desconto deve estar entre 0 e 100%.';
  }

  // Preço por Alqueire
  const preco = parseNumero(values.precoAlqueiro);
  if (values.precoAlqueiro === '' || Number.isNaN(preco) || preco <= 0) {
    errors.precoAlqueiro = 'Informe um preço válido maior que zero.';
  }

  // Taxa da Máquina
  const taxa = parseNumero(values.taxaMaquina);
  if (values.taxaMaquina !== '' && (Number.isNaN(taxa) || taxa < 0 || taxa > 100)) {
    errors.taxaMaquina = 'A taxa deve estar entre 0 e 100%.';
  }

  return errors;
}