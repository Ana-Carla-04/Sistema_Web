// Serviço de Meteorologia em tempo real para Mossoró, RN (UFERSA)
// Utiliza a API pública Open-Meteo (livre, sem necessidade de chave e com suporte a CORS)

const MOSSORO_COORDS = {
  latitude: -5.1878,
  longitude: -37.3442,
  timezone: 'America/Fortaleza',
};

const WMO_WEATHER_MAP = {
  0: { label: 'Ensolarado', short: 'Ensolarado' },
  1: { label: 'Ensolarado', short: 'Ensolarado' },
  2: { label: 'Parcialmente Nublado', short: 'Nublado' },
  3: { label: 'Nublado', short: 'Nublado' },
  45: { label: 'Nevoeiro', short: 'Nevoeiro' },
  48: { label: 'Nevoeiro', short: 'Nevoeiro' },
  51: { label: 'Chuva Leve', short: 'Chuva' },
  53: { label: 'Chuva Moderada', short: 'Chuva' },
  55: { label: 'Chuva Forte', short: 'Chuva' },
  61: { label: 'Chuva Leve', short: 'Chuva' },
  63: { label: 'Chuva Moderada', short: 'Chuva' },
  65: { label: 'Chuva Forte', short: 'Chuva' },
  80: { label: 'Pancadas de Chuva', short: 'Chuva' },
  81: { label: 'Pancadas Fortes', short: 'Chuva' },
  82: { label: 'Chuva Torrencial', short: 'Chuva' },
  95: { label: 'Tempestade', short: 'Tempestade' },
  96: { label: 'Tempestade com Granizo', short: 'Tempestade' },
  99: { label: 'Tempestade Severa', short: 'Tempestade' },
};

function getWeekDayShort(isoDateString) {
  // ISO date "YYYY-MM-DD"
  const [year, month, day] = isoDateString.split('-').map(Number);
  const date = new Date(year, month - 1, day);
  const days = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
  return days[date.getDay()];
}

function getWeekDayFull(isoDateString) {
  const [year, month, day] = isoDateString.split('-').map(Number);
  const date = new Date(year, month - 1, day);
  const days = ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'];
  return days[date.getDay()];
}

function getIrrigationAdvice(temp, humidity, rain, wind) {
  if (rain >= 3) {
    return 'Previsão de chuva — reduza a lâmina de irrigação artificial.';
  }
  if (wind >= 22) {
    return 'Ventos moderados/fortes — evite irrigação por aspersão nas horas de pico.';
  }
  if (temp >= 33 && humidity <= 45) {
    return 'Tempo quente e seco — monitore a lâmina de água nos lotes.';
  }
  return 'Tempo ideal para irrigação controlada.';
}

export const weatherService = {
  async obterClimaAtual(signal) {
    const url = new URL('https://api.open-meteo.com/v1/forecast');
    url.searchParams.set('latitude', String(MOSSORO_COORDS.latitude));
    url.searchParams.set('longitude', String(MOSSORO_COORDS.longitude));
    url.searchParams.set(
      'current',
      'temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m',
    );
    url.searchParams.set(
      'daily',
      'weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,uv_index_max',
    );
    url.searchParams.set('timezone', MOSSORO_COORDS.timezone);
    url.searchParams.set('forecast_days', '5');

    const res = await fetch(url.toString(), { signal });
    if (!res.ok) {
      throw new Error(`Falha ao obter dados meteorológicos (${res.status})`);
    }

    const data = await res.json();
    const current = data.current || {};
    const daily = data.daily || {};

    const currentWeatherCode = current.weather_code ?? 1;
    const weatherInfo = WMO_WEATHER_MAP[currentWeatherCode] || {
      label: 'Parcialmente Nublado',
      short: 'Nublado',
    };

    const tempAtual = Math.round(current.temperature_2m ?? 28);
    const minHoje = Math.round(daily.temperature_2m_min?.[0] ?? 22);
    const maxHoje = Math.round(daily.temperature_2m_max?.[0] ?? 33);
    const umidade = Math.round(current.relative_humidity_2m ?? 70);
    const vento = Math.round(current.wind_speed_10m ?? 14);
    const chuvaHoje = Number((daily.precipitation_sum?.[0] ?? 0).toFixed(1));
    const uvHoje = Math.round(daily.uv_index_max?.[0] ?? 7);

    // Mapeia os próximos 4 dias (a partir de amanhã ou dias seguintes)
    const dates = daily.time || [];
    const proximosDias = dates.slice(1, 5).map((dateStr, index) => {
      const idx = index + 1;
      const code = daily.weather_code?.[idx] ?? 1;
      const dayW = WMO_WEATHER_MAP[code] || { label: 'Ensolarado', short: 'Ideal' };
      const tempMax = Math.round(daily.temperature_2m_max?.[idx] ?? 30);
      const rain = daily.precipitation_sum?.[idx] ?? 0;
      const dayShort = getWeekDayShort(dateStr);
      const dayFull = getWeekDayFull(dateStr);

      return {
        dia: dayShort,
        diaCompleto: dayFull,
        temp: tempMax,
        condicao: dayW.short,
        condicaoCompleta: dayW.label,
        chuva: rain,
        resumoDia: getIrrigationAdvice(tempMax, umidade, rain, vento),
      };
    });

    // Encontra o melhor dia para irrigação (menor chuva e condições estáveis)
    let melhorDia = proximosDias[0]?.diaCompleto || 'Sábado';
    const diaIdeal = proximosDias.find((d) => d.chuva < 1 && d.temp <= 32);
    if (diaIdeal) {
      melhorDia = diaIdeal.diaCompleto;
    }

    return {
      cidade: 'MOSSORÓ, RN',
      min: minHoje,
      max: maxHoje,
      atual: tempAtual,
      condicao: weatherInfo.label,
      resumo: getIrrigationAdvice(tempAtual, umidade, chuvaHoje, vento),
      metricas: [
        { key: 'umidade', valor: `${umidade}%`, label: 'Umidade' },
        { key: 'vento', valor: `${vento} km/h`, label: 'Vento' },
        { key: 'chuva', valor: `${chuvaHoje} mm`, label: 'Chuva/dia' },
        { key: 'uv', valor: `UV ${uvHoje}`, label: 'Índice UV' },
      ],
      proximosDias,
      alerta: {
        prefixo: 'Condições favoráveis para irrigação ',
        destaque: melhorDia,
        sufixo: ' – umidade e vento ideais.',
      },
    };
  },
};
