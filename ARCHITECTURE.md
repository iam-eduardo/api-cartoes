# Documentação Técnica de Arquitetura - API Cartões

## Visão Geral da Arquitetura

A API Cartões foi projetada seguindo os princípios da **Clean Architecture** (Arquitetura Limpa), com o objetivo de
criar um sistema que seja:

- **Independente de frameworks** - A lógica de negócio não depende de bibliotecas ou frameworks específicos
- **Testável** - Possibilidade de testar regras de negócio sem dependências externas
- **Independente da UI** - A lógica de negócio não está acoplada à interface
- **Independente do banco de dados** - A lógica central não depende de tecnologias de persistência
- **Independente de agentes externos** - O core do sistema não depende de serviços externos

## Estrutura do Projeto

A estrutura do projeto segue uma divisão em camadas concêntricas, onde as dependências fluem de fora para dentro:

```
com.cartoes.api_cartoes/
├── domain/             # Camada de domínio (mais interna)
│   ├── entity/         # Entidades de negócio
│   ├── enums/          # Enumerações do domínio
│   ├── exception/      # Exceções de domínio
│   ├── repository/     # Interfaces de repositório
│   ├── service/        # Interfaces e implementações de serviços de domínio
│   └── strategy/       # Interface e implementações do padrão Strategy
├── application/        # Camada de aplicação
│   ├── dto/            # Objetos de transferência de dados
│   ├── exception/      # Handlers de exceção
│   ├── factory/        # Fábricas para conversão entre domínio e DTOs
│   ├── service/        # Orquestradores dos casos de uso
│   └── util/           # Utilitários da aplicação
├── infrastructure/     # Camada de infraestrutura
│   ├── client/         # Clientes HTTP e outros
│   ├── config/         # Configurações do Spring e outros frameworks
│   ├── repository/     # Implementações concretas dos repositórios
│   └── resilience/     # Configurações de resiliência
└── presentation/       # Camada de apresentação
    └── controller/     # Controladores REST
```

## Decisões Arquiteturais

### 1. Clean Architecture

A escolha da Clean Architecture foi motivada pelos seguintes fatores:

- **Manutenibilidade**: Facilita a manutenção ao isolar a lógica de negócio das mudanças em bibliotecas e frameworks
- **Testabilidade**: Permite testar o domínio de forma isolada, sem dependências externas
- **Extensibilidade**: Facilita a adição de novas funcionalidades sem impactar o código existente
- **Adaptabilidade**: Permite trocar implementações tecnológicas com impacto mínimo no sistema

Esta abordagem visa proteger as regras de negócio de mudanças em tecnologias e interfaces, garantindo que o sistema
possa evoluir com o mínimo de atrito.

### 2. Padrão Strategy

O padrão Strategy foi implementado para gerenciar as diferentes regras de concessão de cartões:

```java
public interface StrategyAvaliacaoCartao {
    boolean seAplica(Cliente cliente);

    List<Cartao> avaliarCartoes(Cliente cliente);
}
```

#### Motivação:

- As regras de negócio para concessão de cartões variam conforme o perfil do cliente
- Novas regras podem ser adicionadas no futuro
- É necessário selecionar a regra correta em tempo de execução

#### Implementações:

1. `StrategyClienteJovem` - Para clientes entre 18 e 25 anos
2. `StrategyClienteSaoPaulo` - Para clientes residentes em SP
3. `StrategyClienteSPJovemAdulto` - Para clientes de SP entre 25 e 30 anos
4. `StrategyPadrao` - Regra padrão baseada apenas na renda

#### Configuração com Ordem de Prioridade:

```java

@Bean
public List<StrategyAvaliacaoCartao> estrategiasAvaliacaoCartao(...) {
    return List.of(
            estrategiaClienteJovem,
            estrategiaClienteSPJovemAdulto,
            estrategiaClienteSaoPaulo,
            estrategiaPadrao
    );
}
```

Esta ordem garante que a primeira estratégia aplicável seja selecionada, mantendo a hierarquia de regras.

### 3. Padrão Factory

Foi implementado o padrão Factory para criação de objetos `CartaoResponse` a partir de entidades `Cartao`:

```java
public interface CartaoFactory {
    CartaoResponse toCartaoResponse(Cartao cartao);

    List<CartaoResponse> toCartaoResponseList(List<Cartao> cartoes);
}
```

#### Motivação:

- Encapsular a lógica de criação de DTOs
- Permitir mudanças na transformação entre domínio e DTOs sem afetar outros componentes
- Simplificar testes ao permitir mocks da fábrica

### 4. Serviços em Duas Camadas

A lógica de serviço foi dividida em duas camadas distintas:

1. **Serviços de Domínio**:
    - `AvaliacaoCartaoService` - Contém regras de negócio puras
    - Trabalha apenas com entidades de domínio
    - Não conhece detalhes de apresentação ou infraestrutura

2. **Serviços de Aplicação**:
    - `CartaoApplicationService` - Orquestra o fluxo da aplicação
    - Coordena a conversão entre DTOs e entidades
    - Gerencia transações e integração com componentes externos

#### Vantagens:

- Separação clara entre regras de negócio e lógica de aplicação
- Possibilidade de reutilização dos serviços de domínio em diferentes contextos
- Melhor testabilidade de cada responsabilidade

### 5. Tratamento Centralizado de Exceções

Foi implementado um handler global de exceções com `@RestControllerAdvice`:

```java

@RestControllerAdvice
public class ApiExceptionHandler {
    // Handlers para diferentes tipos de exceções
}
```

#### Motivação:

- Padronização das respostas de erro
- Centralização da lógica de tratamento de exceções
- Separação entre lançamento e tratamento de exceções
- Conformidade com RFC 9457 (ProblemDetails)

### 6. Resiliência

Foram implementados mecanismos de resiliência para comunicação com APIs externas:

1. **Circuit Breaker**:
   ```java
   @CircuitBreaker(name = "clienteService", fallbackMethod = "registrarClienteFallback")
   ```
    - Previne chamadas repetidas a serviços com falha
    - Falha rápido quando o serviço está indisponível
    - Permite recuperação gradual

2. **Retry**:
   ```java
   @Retry(name = "clienteService")
   ```
    - Tenta novamente operações que falharam
    - Utiliza backoff exponencial para evitar sobrecarga

3. **Fallback**:
   ```java
   private UUID registrarClienteFallback(Cliente cliente, Exception e) {
       // Lógica de contingência
   }
   ```
    - Fornece comportamento alternativo quando o serviço principal falha

### 7. Validação em Camadas

A validação foi implementada em diferentes níveis:

1. **Validação de API** (Bean Validation):
    - Validação dos campos obrigatórios
    - Validação de formato e restrições simples

2. **Validação Específica de Aplicação**:
    - Implementada em `ClienteValidator`
    - Validações que vão além das anotações padrão

3. **Validação de Domínio**:
    - Implementada em `AvaliacaoCartaoService.validarCliente()`
    - Validações específicas de regras de negócio

Esta abordagem garante que as validações sejam aplicadas no nível apropriado.

## Padrões de Código

### Injeção de Dependência

Utilizamos injeção de dependência via construtor com `@RequiredArgsConstructor` do Lombok:

```java

@Service
@RequiredArgsConstructor
public class ServiceImpl implements Service {
    private final Dependency dependency;
    // ...
}
```

#### Vantagens:

- Imutabilidade das dependências
- Facilidade para testes unitários
- Explicitação das dependências obrigatórias

### Imutabilidade

Utilizamos objetos imutáveis sempre que possível:

- DTOs com Lombok `@Builder` para criação
- Entidades protegidas contra modificação após criação
- Coleções não modificáveis retornadas pelos serviços

### Logging Estruturado

Implementamos logging estruturado com SLF4J e Logback:

```java
log.info("Processando solicitação para cliente: {}",cliente.getCpf());
```

## Configurações Externalizadas

Todas as configurações do sistema estão externalizadas no `application.yml`:

```yaml
aplicacao:
  cliente:
    idade-minima: 18
    idade-jovem-maxima: 25
  # ...
```

### Benefícios:

- Configuração sem recompilação
- Valores diferentes por ambiente
- Possibilidade de override via variáveis de ambiente

## Decisões de Infraestrutura

### Docker Multi-stage Build

Implementamos um Dockerfile com build multi-estágio:

```dockerfile
# Estágio de compilação
FROM eclipse-temurin:22-jdk-alpine AS build
# ...

# Estágio de execução
FROM eclipse-temurin:22-jre-alpine
# ...
```

#### Vantagens:

- Imagens menores (apenas o runtime JRE)
- Menor superfície de ataque
- Melhor performance de deploy

### Healthchecks

Implementamos endpoints específicos para observabilidade:

- `/health` - Status geral da aplicação
- `/health/liveness` - Verificação se a aplicação está viva
- `/health/readiness` - Verificação se a aplicação está pronta para receber tráfego

### Documentação da API

Integramos Springdoc OpenAPI (Swagger) para documentação automática:

```java
@Operation(summary = "Solicitar cartão de crédito",
        description = "Recebe dados do cliente e retorna cartões disponíveis")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "..."),
        // ...
})
```

## Considerações de Performance

### Estratégias para Redução de Latência

1. **Processamento Eficiente**:
    - Uso do padrão Strategy para evitar condicionais complexos
    - Conversão eficiente entre entidades e DTOs
    - Uso de Stream API de forma otimizada

2. **Paralelização**:
    - Possibilidade de paralelizar avaliações independentes

3. **Timeouts Configuráveis**:
    - Configuração de timeouts para evitar esperas muito longas
    - Circuit breaker para falha rápida

## Evolução da Arquitetura

### Extensões Futuras

A arquitetura foi projetada para permitir as seguintes evoluções:

1. **Persistência**:
    - Adição de banco de dados sem impacto nas regras de negócio
    - Implementação de cache para consultas frequentes

2. **Mensageria**:
    - Integração com sistemas de mensageria para processamento assíncrono
    - Eventos de domínio para desacoplamento de subsistemas

3. **Novas Regras**:
    - Adição de novas estratégias para perfis de cliente
    - Novas validações de domínio

4. **Observabilidade**:
    - Métricas detalhadas via Micrometer
    - Tracing distribuído com OpenTelemetry

## Compromissos Arquiteturais

Todo design arquitetural envolve compromissos (trade-offs). Destacamos os principais:

1. **Complexidade vs. Flexibilidade**:
    - A separação em múltiplas camadas aumenta a complexidade inicial
    - Benefício: Maior flexibilidade para mudanças e evolução

2. **Overhead vs. Manutenibilidade**:
    - A criação de abstrações e interfaces adiciona certo overhead
    - Benefício: Código mais manutenível e testável

3. **Performance vs. Desacoplamento**:
    - O desacoplamento via interfaces pode adicionar indireções
    - Benefício: Melhor testabilidade e possibilidade de evolução independente

## Conclusão

A arquitetura da API Cartões foi projetada com foco em qualidade, manutenibilidade e extensibilidade. As decisões
arquiteturais foram tomadas para equilibrar requisitos atuais e necessidades futuras, permitindo que o sistema evolua
com o mínimo de atrito.

Os princípios da Clean Architecture, combinados com padrões de design eficazes, proporcionam uma base sólida que
facilita a adaptação às mudanças de requisitos e tecnologias ao longo do tempo.