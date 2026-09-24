# Comandos Java

## Para executar os testes no terminal, na raiz do projeto:

```powershell
.\mvnw.cmd test
```

## Executar apenas o teste do contexto:

```powershell
.\mvnw.cmd -Dtest=SipaApplicationTests test
```

## Executar o teste específico de segurança, caso o arquivo exista:


```powershell
.\mvnw.cmd -Dtest=DashboardControllerSecurityTest test
```

## Compilar sem executar testes
```powershell
.\mvnw.cmd clean package -DskipTests
```