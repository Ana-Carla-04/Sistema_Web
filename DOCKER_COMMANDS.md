# Comandos Docker

Execute estes comandos na raiz do projeto, onde está o `docker-compose.yml`.

## Primeira configuracao

Copie o modelo de variaveis e altere os valores:

```powershell
Copy-Item .env.example .env
code .env
```

O arquivo `.env` deve conter uma senha do PostgreSQL e um segredo JWT forte.

## Criar e iniciar

Iniciar os containers em primeiro plano:

```powershell
docker compose up --build
```

Iniciar em segundo plano:

```powershell
docker compose up --build -d
```

Iniciar sem reconstruir a imagem:

```powershell
docker compose up -d
```

## Verificar o estado

Listar containers do projeto:

```powershell
docker compose ps
```

Ver os logs da aplicacao:

```powershell
docker compose logs -f app
```

Ver os logs do banco:

```powershell
docker compose logs -f db
```

Ver os logs de todos os servicos:

```powershell
docker compose logs -f
```

## Parar e iniciar novamente

Parar os containers sem remove-los:

```powershell
docker compose stop
```

Iniciar novamente os containers parados:

```powershell
docker compose start
```

Reiniciar a aplicacao:

```powershell
docker compose restart app
```

Reiniciar todos os servicos:

```powershell
docker compose restart
```

## Excluir containers e recriar

Parar e remover os containers e a rede, preservando o volume do banco:

```powershell
docker compose down
```

Remover e criar novamente os containers com a imagem atualizada:

```powershell
docker compose down
docker compose up --build -d
```

Reconstruir a imagem ignorando o cache:

```powershell
docker compose build --no-cache
docker compose up -d
```

## Apagar tudo, incluindo o banco

> Atencao: o comando abaixo apaga o volume `postgres_data` e todos os dados do PostgreSQL.

```powershell
docker compose down -v
docker compose up --build -d
```

Remover tambem imagens orfas do projeto:

```powershell
docker compose down --rmi local --remove-orphans
```

## Acessar o banco

Abrir um terminal PostgreSQL dentro do container:

```powershell
docker compose exec db psql -U sipa -d sipa
```

Sair do PostgreSQL:

```sql
\q
```

## Acessar a aplicacao

A API fica disponivel em:

```text
http://localhost:8080
```

Para encerrar os containers depois do uso:

```powershell
docker compose down
```
